package routes

import (
	"database/sql"
	"fmt"
	"log"
	"net/http"
	"strconv"
	"strings"

	"github.com/006/stuck-gin/middleware"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
)

func extract(in sql.NullString) string {
	if in.Valid {
		return in.String
	}
	return ""
}

func stringToFloat64(str string) float64 {
	floatVal, err := strconv.ParseFloat(str, 64) // 64 for float64
	if err != nil {
		fmt.Println("Error converting string to float64:", err)
		return 0
	}
	return floatVal
}

func SetupGinRouter() {
	gin.SetMode(gin.ReleaseMode) //before Defualt
	r := gin.Default()

	// r.Use(gin.Logger())

	r.Use(middleware.CORSMiddleware())
	r.Use(middleware.AccessTokenMiddleware())

	r.GET("/spot/:unid", func(c *gin.Context) {
		unid := c.Param("unid")
		spot, err := GetSpot(unid)

		log.Printf("query spot: %s", unid)

		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"message": err,
			})
			return
		}

		c.JSON(200, gin.H{
			"maker":           "gin", //just for indicate whihc API the data comes from
			"unid":            spot.UNID,
			"openid_stucker":  spot.OpenIdStucker,
			"openid_saver":    extract(spot.OpenIdSaver),
			"lon":             spot.Lon,
			"lat":             spot.Lat,
			"geohash":         spot.Geohash,
			"phone":           spot.Cellphone,
			"conversation_id": extract(spot.ConversationId),
			"description":     extract(spot.Description),
			"vehicle_color":   spot.VehicleColor,
		})
	})

	r.POST("/spot", func(c *gin.Context) {
		spot := Spot{
			OpenIdStucker: c.PostForm("openid"),
			Lon:           stringToFloat64(c.PostForm("lon")),
			Lat:           stringToFloat64(c.PostForm("lat")),
			VehicleColor:  c.PostForm("vehicle_color"),
			Cellphone:     c.PostForm("phone"),
			Description:   sql.NullString{String: c.PostForm("description"), Valid: true},
			UNID:          strings.ToUpper(strings.ReplaceAll(uuid.NewString(), "-", "")),
		}

		err := PersistSpot(spot)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"message": err,
			})
			return
		}

		c.JSON(http.StatusOK, gin.H{
			"message": "pong",
			"unid":    spot.UNID,
		})
	})

	r.POST("/spot/:unid/report", func(c *gin.Context) {
		unid := c.Param("unid")

		c.JSON(200, gin.H{
			"unid":    unid,
			"message": "pong",
		})
	})

	r.NoRoute(noRoute)
	r.Run(":8000")
}

func noRoute(c *gin.Context) {
	c.JSON(404, gin.H{"message": "Page not found"})
}
