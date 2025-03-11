package utils

import (
	"strings"
	"testing"

	"github.com/google/uuid"
)

func TestGenerate(t *testing.T) {

	zzz := uuid.NewString()
	zzz = strings.ToUpper(strings.ReplaceAll(zzz, "-", ""))
	t.Fatal(zzz)
}
