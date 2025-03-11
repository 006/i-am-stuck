package routes

import (
	"context"
	"database/sql"

	"github.com/006/stuck-gin/utils/db"
)

type Spot struct {
	AIID           int
	UNID           string
	Cellphone      string
	VehicleColor   string
	Geohash        string
	Lon            float64
	Lat            float64
	OpenIdStucker  string
	OpenIdSaver    sql.NullString
	ConversationId sql.NullString
	Description    sql.NullString
}

var pool *sql.DB

func GetSpot(unid string) (Spot, error) {
	if pool == nil {
		pool = db.ConnectDB()
	}

	ctx, stop := context.WithCancel(context.Background())
	defer stop()

	spot := Spot{}
	query := `select AIID,UNID,OPEN_ID_STUCKER,OPEN_ID_SAVER,CELLPHONE,GEO_HASH, LON,LAT,COLOR_VEHICLE,ID_CVSN,CONTENT from SK_SPOT where FLAG_DEL=0 and unid=?;`
	err := pool.QueryRowContext(ctx, query, unid).Scan(&spot.AIID, &spot.UNID, &spot.OpenIdStucker, &spot.OpenIdSaver, &spot.Cellphone, &spot.Geohash, &spot.Lon, &spot.Lat, &spot.VehicleColor, &spot.ConversationId, &spot.Description)
	if err != nil {
		return Spot{}, err
	}
	return spot, nil
}

func PersistSpot(spot Spot) error {
	if pool == nil {
		pool = db.ConnectDB()
	}

	ctx, stop := context.WithCancel(context.Background())
	defer stop()

	query := `insert into SK_SPOT(UNID,MAKER,OPEN_ID_STUCKER,CELLPHONE,LON,LAT,COLOR_VEHICLE,CONTENT) values(?,?,?,?,?,?,?,?);`
	_, err := pool.ExecContext(ctx, query, spot.UNID, "gin", spot.OpenIdStucker, spot.Cellphone, spot.Lon, spot.Lat, spot.VehicleColor, spot.Description)
	if err != nil {
		return err
	}
	return nil
}
