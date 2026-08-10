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
