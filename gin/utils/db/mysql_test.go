package db

import (
	"testing"

	"github.com/joho/godotenv"
)

func TestConnectMySQL(t *testing.T) {
	err := godotenv.Load("../../.env") // load .env file from parent directory.
	if err != nil {
		t.Logf("Error loading .env file")
		t.Skip("No .env found. Skip test")
	}

	// ConnectDB()
	err = ConnectDB().Ping()
	if err != nil {
		t.Error(err)
	}
}
