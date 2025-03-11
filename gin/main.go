package main

import (
	"log"

	"github.com/006/stuck-gin/routes"
	"github.com/joho/godotenv"
)

func main() {
	// Load environment variables from .env file
	err := godotenv.Load(".env")
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	// Start DB
	// db.ConnectMySQL(os.Getenv("DATABASE_TYPE"), os.Getenv("DATABASE_DSN"))

	// Start Gin
	routes.SetupGinRouter()
	// routes.SetupViaHttp()
}
