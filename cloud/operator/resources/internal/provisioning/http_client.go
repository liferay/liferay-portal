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
	"net/http"
	"strings"
	"time"
)

func (httpClient *HTTPClient) Activate(
	activationRequest ActivationRequest,
	context context.Context,
	privateKey *rsa.PrivateKey,
) error {
	token, signError := signJWT(
		map[string]any{
			"activationCode":  activationRequest.ActivationCode,
			"environmentID":   activationRequest.EnvironmentID,
			"environmentName": activationRequest.EnvironmentName,
			"publicKey":       activationRequest.PublicKey,
		},
		activationRequest.EnvironmentID,
		privateKey,
	)

	if signError != nil {
		return signError
	}

	url := fmt.Sprintf(
		"%s/o/provisioning-rest/v1.0/cloud/environment/%s/activation",
		httpClient.BaseURL, activationRequest.EnvironmentID,
	)

	response, postError := httpClient.post(context, url, token)

	if postError != nil {
		return postError
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

func encodeSegment(bytes []byte) string {
	return base64.RawURLEncoding.EncodeToString(bytes)
}

func (httpClient *HTTPClient) post(
	context context.Context,
	url string,
	token string,
) (*http.Response, error) {

	request, error := http.NewRequestWithContext(
		context, http.MethodPost, url, bytes.NewReader([]byte(token)),
	)

	if error != nil {
		return nil, error
	}

	request.Header.Set("Content-Type", "application/jwt")

	return httpClient.Client.Do(request)
}

func randomID() (string, error) {
	buffer := make([]byte, 16)

	if _, error := rand.Read(buffer); error != nil {
		return "", error
	}

	return hex.EncodeToString(buffer), nil
}

func signJWT(
	claims map[string]any,
	issuer string,
	privateKey *rsa.PrivateKey,
) (string, error) {
	now := time.Now()

	payload := map[string]any{}

	for key, value := range claims {
		payload[key] = value
	}

	randomID, error := randomID()

	if error != nil {
		return "", error
	}

	payload["exp"] = now.Add(60 * time.Second).Unix()
	payload["iat"] = now.Unix()
	payload["iss"] = issuer
	payload["jti"] = randomID

	header, error := json.Marshal(map[string]string{"alg": "RS256", "typ": "JWT"})

	if error != nil {
		return "", error
	}

	body, error := json.Marshal(payload)

	if error != nil {
		return "", error
	}

	signingInput := encodeSegment(header) + "." + encodeSegment(body)

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
