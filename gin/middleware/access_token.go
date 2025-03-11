package middleware

import (
	"net/http"
	"strings"

	"github.com/006/stuck-gin/utils"
	"github.com/gin-gonic/gin"
)

func AccessTokenMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		token := strings.Replace(c.Request.Header.Get("Authorization"), "Bearer ", "", 1)
		// fmt.Printf("%s\n", token)

		result := utils.ValidateJSONWebToken(token)
		if !result.Result {
			c.AbortWithStatusJSON(http.StatusForbidden, gin.H{"error": result.Err})
		}
		//
		c.Next()
	}
}
