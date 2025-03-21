package db

import (
	"database/sql"
	"log"
	"os"

	_ "github.com/go-sql-driver/mysql"
)

func ConnectDB() *sql.DB {
	// Replace "username", "password", "dbname" with your database credentials
	connectionString := os.Getenv("db_stuck_user") + ":" + os.Getenv("db_stuck_passwd") + "@tcp(" + os.Getenv("db_stuck_host") + ":3306)/test"
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		log.Fatal(err)
	}

	db.SetConnMaxLifetime(0)
	db.SetMaxIdleConns(50)
	db.SetMaxOpenConns(50)

	return db
}
