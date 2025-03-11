package db

import (
	"testing"

	"github.com/joho/godotenv"
)

func TestConnectMySQL(t *testing.T) {
	err := godotenv.Load("../../.env") // load .env file from parent directory.
	if err != nil {
		t.Fatalf("Error loading .env file")
	}

	// ConnectDB()

	err = ConnectDB().Ping()
	if err != nil {
		t.Error(err)
	}
}
