package utils

import (
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"testing"
)

func TestParseJWKS(t *testing.T) {

	type JWK struct {
		Kty string   `json:"kty"`
		Use string   `json:"use"`
		N   string   `json:"stringValue"`
		E   string   `json:"n"`
		Kid string   `json:"kid"`
		X5t string   `json:"x5t"`
		X5c []string `json:"x5c"`
		Alg string   `json:"alg"`
	}

	type JWKS struct {
		Keys []JWK `json:"keys"`
	}

	resp, err := http.Get("https://dev-msx36x4pag8s22w3.ca.auth0.com/.well-known/jwks.json")
	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Fatal(err)
	}
	log.Print(string(body))

	var jwks JWKS

	err = json.Unmarshal(body, &jwks)
	if err != nil {
		fmt.Println("Error unmarshalling JSON:", err)
	}

	fmt.Printf("%s: %s\n", jwks.Keys[0].Kty, jwks.Keys[0].Kid)

	sDec, _ := base64.StdEncoding.DecodeString(jwks.Keys[0].X5c[0])
	// pubData, err := x509.MarshalPKIXPublicKey(sDec)
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println("%h\n", sDec)
}
