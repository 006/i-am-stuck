package utils

import (
	"context"
	"fmt"
	"strings"
	"time"

	"github.com/lestrrat-go/httprc/v3"
	"github.com/lestrrat-go/jwx/v3/jwk"
	"github.com/lestrrat-go/jwx/v3/jwt"
)

type ValidateResult struct {
	Result bool
	Err    string
}

func ValidateJSONWebToken(token string, tagetScopes ...string) ValidateResult {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	const auth0 = "https://dev-msx36x4pag8s22w3.ca.auth0.com/.well-known/jwks.json"

	// The first steps are the same as examples/jwk_cache_example_test.go
	c, err := jwk.NewCache(ctx, httprc.NewClient())
	if err != nil {
		fmt.Printf("failed to create cache: %s\n", err)
		return ValidateResult{Result: false, Err: ""}
	}

	if err := c.Register(
		ctx,
		auth0,
		jwk.WithMaxInterval(24*time.Hour*7),
		jwk.WithMinInterval(15*time.Minute),
	); err != nil {
		fmt.Printf("failed to register google JWKS: %s\n", err)
		return ValidateResult{Result: false, Err: ""}
	}

	cached, err := c.CachedSet(auth0)
	if err != nil {
		fmt.Printf("failed to get cached keyset: %s\n", err)
		return ValidateResult{Result: false, Err: ""}
	}

	// cached fulfills the jwk.Set interface.
	// var _ jwk.Set = cached
	// _ = jws.WithKeySet(cached)

	// verified, err := jws.Verify([]byte(token), jws.WithKeySet(cached, jws.WithRequireKid(true)))
	// if err != nil {
	// 	fmt.Printf("failed to verify payload: %s\n", err)
	// 	return false
	// }
	// fmt.Printf("%s\n", verified)

	tok, err := jwt.Parse([]byte(token), jwt.WithKeySet(cached))
	if err != nil {
		fmt.Printf("failed to parse serialized: %s\n", err)
		return ValidateResult{Result: false, Err: err.Error()}
	}

	_ = tok

	err = jwt.Validate(tok, jwt.WithIssuer(`https://dev-msx36x4pag8s22w3.ca.auth0.com/`))
	if err != nil {
		fmt.Printf("failed to verify token: %s\n", err)
		return ValidateResult{Result: false, Err: err.Error()}
	}
	var v interface{}
	tok.Get("scope", &v)

	if len(tagetScopes) > 0 {
		fmt.Printf("Scope: %s, %s\n", v, tagetScopes[0])

		if !strings.Contains(v.(string), tagetScopes[0]) {
			return ValidateResult{Result: false, Err: "scope not granted"}
		}
	}
	return ValidateResult{Result: true, Err: ""}
	// log.Print()
}
