package db

import (
	"database/sql"
	"log"
	"os"

	_ "github.com/go-sql-driver/mysql"
)

func ConnectDB() *sql.DB {
	// Replace "username", "password", "dbname" with your database credentials
	connectionString := os.Getenv("DATABASE_USER") + ":" + os.Getenv("DATABASE_PWD") + "@tcp(" + os.Getenv("DATABASE_HOST") + ":3306)/test"
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		log.Fatal(err)
	}

	db.SetConnMaxLifetime(0)
	db.SetMaxIdleConns(50)
	db.SetMaxOpenConns(50)

	return db
}
