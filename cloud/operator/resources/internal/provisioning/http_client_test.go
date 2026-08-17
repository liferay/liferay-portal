package provisioning

import (
	"bytes"
	"context"
	"crypto/rand"
	"crypto/rsa"
	"encoding/base64"
	"encoding/json"
	"io"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"
)

func TestDownloadAddOn(t *testing.T) {
	privateKey, error := rsa.GenerateKey(rand.Reader, 2048)

	if error != nil {
		t.Fatalf("Unable to generate key: %v", error)
	}

	testCases := map[string]struct {
		responseBody []byte
		statusCode   int
		wantError    bool
	}{
		"error on non-200 status": {
			responseBody: []byte("not found"),
			statusCode:   http.StatusNotFound,
			wantError:    true,
		},
		"streams the binary on success": {
			responseBody: []byte("PK\x03\x04 fake lpkg bytes"),
			statusCode:   http.StatusOK,
			wantError:    false,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			var capturedClaims map[string]any

			server := httptest.NewServer(
				http.HandlerFunc(
					func(responseWriter http.ResponseWriter, request *http.Request) {
						body, _ := io.ReadAll(request.Body)

						capturedClaims = decodeClaims(t, string(body))

						responseWriter.WriteHeader(testCase.statusCode)
						responseWriter.Write(testCase.responseBody)
					},
				),
			)

			defer server.Close()

			httpClient := NewHTTPClient(server.URL)

			reader, error := httpClient.DownloadAddOn(
				context.Background(),
				DownloadRequest{
					DownloadURL:    server.URL,
					EnvironmentID:  "env-123",
					VirtualEntryID: 456,
				},
				privateKey,
			)

			if capturedClaims["environmentID"] != "env-123" {
				t.Errorf(
					"Claim environmentID = %v, want env-123",
					capturedClaims["environmentID"],
				)
			}

			if capturedClaims["virtualEntryId"] != float64(456) {
				t.Errorf(
					"Claim virtualEntryId = %v, want 456",
					capturedClaims["virtualEntryId"],
				)
			}

			if testCase.wantError {
				if error == nil {
					t.Fatal("Expected an error, got nil")
				}

				return
			}

			if error != nil {
				t.Fatalf("Unexpected error: %v", error)
			}

			defer reader.Close()

			downloaded, error := io.ReadAll(reader)

			if error != nil {
				t.Fatalf("Unable to read body: %v", error)
			}

			if !bytes.Equal(downloaded, testCase.responseBody) {
				t.Errorf(
					"Body = %q, want %q", downloaded, testCase.responseBody,
				)
			}
		})
	}
}

func TestOfflineActivationPayload(t *testing.T) {
	privateKey, error := rsa.GenerateKey(rand.Reader, 2048)

	if error != nil {
		t.Fatalf("Unable to generate key: %v", error)
	}

	payload, error := OfflineActivationPayload(
		ActivationRequest{
			ActivationCode:  "must-be-omitted",
			EnvironmentID:   "env-123",
			EnvironmentName: "prod",
			PublicKey:       "public-key-base64",
		},
		privateKey,
	)

	if error != nil {
		t.Fatalf("Unexpected error: %v", error)
	}

	claims := decodeClaims(t, payload)

	if claims["environmentID"] != "env-123" {
		t.Errorf("environmentID = %v, want env-123", claims["environmentID"])
	}

	if claims["environmentName"] != "prod" {
		t.Errorf("environmentName = %v, want prod", claims["environmentName"])
	}

	if claims["publicKey"] != "public-key-base64" {
		t.Errorf("publicKey = %v, want public-key-base64", claims["publicKey"])
	}

	if _, present := claims["activationCode"]; present {
		t.Error("activationCode should be omitted from the offline payload")
	}

	if delta := int64(claims["exp"].(float64)) - int64(claims["iat"].(float64)); delta != 90*24*60*60 {
		t.Errorf("exp - iat = %d seconds, want 90 days (7776000)", delta)
	}
}

func TestPayloadExpired(t *testing.T) {
	privateKey, error := rsa.GenerateKey(rand.Reader, 2048)

	if error != nil {
		t.Fatalf("Unable to generate key: %v", error)
	}

	fresh, error := signJWT(map[string]any{}, "issuer", privateKey, time.Hour)

	if error != nil {
		t.Fatalf("Unable to sign a fresh token: %v", error)
	}

	expired, error := signJWT(map[string]any{}, "issuer", privateKey, -time.Hour)

	if error != nil {
		t.Fatalf("Unable to sign an expired token: %v", error)
	}

	withoutExp := "header." +
		base64.RawURLEncoding.EncodeToString([]byte(`{
			"iss": "issuer"
		}`)) + ".signature"

	testCases := map[string]struct {
		token string
		want  bool
	}{
		"expired token is expired": {
			token: expired,
			want:  true,
		},
		"fresh token is not expired": {
			token: fresh,
			want:  false,
		},
		"malformed token is expired": {
			token: "not-a-jwt",
			want:  true,
		},
		"token without exp is not expired": {
			token: withoutExp,
			want:  false,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if got := PayloadExpired(testCase.token); got != testCase.want {
				t.Errorf("PayloadExpired = %v, want %v", got, testCase.want)
			}
		})
	}
}

func TestRedactSensitive(t *testing.T) {
	testCases := map[string]struct {
		assertContains    string
		assertNotContains string
		payload           string
	}{
		"activationCode is redacted": {
			assertContains:    `"activationCode":"[REDACTED]"`,
			assertNotContains: "one-time-secret",
			payload: `{
				"activationCode": "one-time-secret",
				"environmentID": "env-1"
			}`,
		},
		"manifest request is not redacted": {
			assertContains:    `"dxpVersion": "2026.q3.0"`,
			assertNotContains: "[REDACTED]",
			payload: `{
				"dxpVersion": "2026.q3.0",
				"environmentID": "env-1"
			}`,
		},
		"manifest response redacts licenseXML": {
			assertContains:    `"licenseXML":"[REDACTED]"`,
			assertNotContains: "c2lnbmVkLWxpY2Vuc2U=",
			payload: `{
				"licenseXML": "c2lnbmVkLWxpY2Vuc2U=",
				"maxClusterNodes": 3
			}`,
		},
		"non-JSON payload is returned as is": {
			assertContains:    "not-a-json-token",
			assertNotContains: "[REDACTED]",
			payload:           "not-a-json-token",
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			redacted := redactSensitive(testCase.payload)

			if !strings.Contains(redacted, testCase.assertContains) {
				t.Errorf(
					"redactSensitive = %q, want it to contain %q",
					redacted, testCase.assertContains,
				)
			}

			if strings.Contains(redacted, testCase.assertNotContains) {
				t.Errorf(
					"redactSensitive = %q, want it to not contain %q",
					redacted, testCase.assertNotContains,
				)
			}
		})
	}
}

func decodeClaims(t *testing.T, token string) map[string]any {
	t.Helper()

	segments := strings.Split(token, ".")

	if len(segments) != 3 {
		t.Fatalf("Token: expected 3 segments, got %d", len(segments))
	}

	payload, error := base64.RawURLEncoding.DecodeString(segments[1])

	if error != nil {
		t.Fatalf("Unable to decode payload: %v", error)
	}

	claims := map[string]any{}

	if error := json.Unmarshal(payload, &claims); error != nil {
		t.Fatalf("Unable to unmarshal claims: %v", error)
	}

	return claims
}
