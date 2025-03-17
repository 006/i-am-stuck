package routes

import (
	"testing"

	"github.com/joho/godotenv"
)

func TestGetSpot(t *testing.T) {
	err := godotenv.Load("../.env") // load .env file from parent directory.

	if err != nil {
		t.Fatalf("Error loading .env file")
	}

	spot, err := GetSpot("E352724AFBC24B72B581D172E09B2CCD")
	if err != nil {
		t.Fatalf("Error query spot:%s\n", err)
	}
	t.Logf("%d, %s, %s, %t\n", spot.AIID, spot.UNID, spot.Cellphone, spot.OpenIdSaver.Valid)
}
