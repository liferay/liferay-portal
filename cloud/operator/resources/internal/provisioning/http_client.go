package provisioning

import (
	"bytes"
	"context"
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"maps"
	"net/http"
	"strings"
	"time"
)

func (httpClient *HTTPClient) Activate(
	activationRequest ActivationRequest,
	context context.Context,
	privateKey *rsa.PrivateKey,
) error {
	token, error := signJWT(
		map[string]any{
			"activationCode":  activationRequest.ActivationCode,
			"environmentID":   activationRequest.EnvironmentID,
			"environmentName": activationRequest.EnvironmentName,
			"publicKey":       activationRequest.PublicKey,
		},
		activationRequest.EnvironmentID,
		privateKey,
	)

	if error != nil {
		return error
	}

	response, error := httpClient.post(
		context, token, fmt.Sprintf(
			"%s/o/provisioning-rest/v1.0/cloud/environment/%s/activation",
			httpClient.BaseURL, activationRequest.EnvironmentID,
		),
	)

	if error != nil {
		return error
	}

	defer response.Body.Close()

	if response.StatusCode == http.StatusNotFound {
		return ErrorActivationRejected
	}

	if response.StatusCode != http.StatusOK {
		return fmt.Errorf("activation: unexpected status %d", response.StatusCode)
	}

	return nil
}

func NewHTTPClient(baseURL string) *HTTPClient {
	return &HTTPClient{
		BaseURL: strings.TrimRight(baseURL, "/"),
		Client:  &http.Client{Timeout: 30 * time.Second},
	}
}

func encodeRandomID() (string, error) {
	buffer := make([]byte, 16)

	if _, error := rand.Read(buffer); error != nil {
		return "", error
	}

	return hex.EncodeToString(buffer), nil
}

func encodeSegment(bytes []byte) string {
	return base64.RawURLEncoding.EncodeToString(bytes)
}

func (httpClient *HTTPClient) post(
	context context.Context,
	token string,
	url string,
) (*http.Response, error) {
	request, error := http.NewRequestWithContext(
		context, http.MethodPost, url, bytes.NewReader([]byte(token)),
	)

	if error != nil {
		return nil, error
	}

	request.Header.Set("Content-Type", "text/plain")

	return httpClient.Client.Do(request)
}

func signJWT(
	claims map[string]any,
	issuer string,
	privateKey *rsa.PrivateKey,
) (string, error) {
	now := time.Now()

	payload := maps.Clone(claims)

	randomID, error := encodeRandomID()

	if error != nil {
		return "", error
	}

	payload["exp"] = now.Add(60 * time.Second).Unix()
	payload["iat"] = now.Unix()
	payload["iss"] = issuer
	payload["jti"] = randomID

	headerJSON, error := json.Marshal(
		map[string]string{
			"alg": "RS256",
			"typ": "JWT",
		},
	)

	if error != nil {
		return "", error
	}

	payloadJSON, error := json.Marshal(payload)

	if error != nil {
		return "", error
	}

	signingInput := encodeSegment(headerJSON) + "." + encodeSegment(payloadJSON)

	digest := sha256.Sum256([]byte(signingInput))

	signature, error := rsa.SignPKCS1v15(
		rand.Reader, privateKey, crypto.SHA256, digest[:],
	)

	if error != nil {
		return "", error
	}

	return signingInput + "." + encodeSegment(signature), nil
}

type HTTPClient struct {
	BaseURL string
	Client  *http.Client
}
