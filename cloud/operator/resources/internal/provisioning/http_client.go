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
	"io"
	"maps"
	"net/http"
	"strings"
	"time"

	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const (
	offlineActivationPayloadExpiration = 90 * 24 * time.Hour
	provisioningRequestExpiration      = 60 * time.Second
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
		provisioningRequestExpiration,
	)

	if error != nil {
		return error
	}

	response, error := httpClient.post(
		context, token, fmt.Sprintf(
			"%s/cloud/v1/environments/%s/activation",
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

func (httpClient *HTTPClient) DownloadAddOn(
	context context.Context,
	downloadRequest DownloadRequest,
	privateKey *rsa.PrivateKey,
) (io.ReadCloser, error) {
	token, error := signJWT(
		map[string]any{
			"environmentID":  downloadRequest.EnvironmentID,
			"virtualEntryId": downloadRequest.VirtualEntryID,
		},
		downloadRequest.EnvironmentID,
		privateKey,
		provisioningRequestExpiration,
	)

	if error != nil {
		return nil, error
	}

	response, error := httpClient.post(
		context, token, downloadRequest.DownloadURL,
	)

	if error != nil {
		return nil, error
	}

	if response.StatusCode != http.StatusOK {
		response.Body.Close()

		return nil, fmt.Errorf(
			"add-on download: unexpected status %d", response.StatusCode,
		)
	}

	return response.Body, nil
}

func EntitlementsFromResponse(entitlementsResponse EntitlementsResponse) (*Entitlements, error) {
	licenseXML, error := base64.StdEncoding.DecodeString(entitlementsResponse.LicenseXML)

	if error != nil {
		return nil, fmt.Errorf("entitlements: decode licenseXML: %w", error)
	}

	return &Entitlements{
		AddOns:          entitlementsResponse.AddOns,
		LicenseXML:      licenseXML,
		MaxClusterNodes: entitlementsResponse.MaxClusterNodes,
	}, nil
}

func (httpClient *HTTPClient) Manifest(
	context context.Context,
	manifestRequest ManifestRequest,
	privateKey *rsa.PrivateKey,
) (*Entitlements, error) {
	token, error := signJWT(
		map[string]any{
			"dxpVersion":    manifestRequest.DxpVersion,
			"environmentID": manifestRequest.EnvironmentID,
		},
		manifestRequest.EnvironmentID,
		privateKey,
		provisioningRequestExpiration,
	)

	if error != nil {
		return nil, error
	}

	response, error := httpClient.post(
		context, token, fmt.Sprintf(
			"%s/cloud/v1/environments/%s/manifest",
			httpClient.BaseURL, manifestRequest.EnvironmentID,
		),
	)

	if error != nil {
		return nil, error
	}

	defer response.Body.Close()

	if response.StatusCode != http.StatusOK {
		return nil, fmt.Errorf(
			"entitlements: unexpected status %d", response.StatusCode,
		)
	}

	var entitlementsResponse EntitlementsResponse

	if error := json.NewDecoder(response.Body).Decode(&entitlementsResponse); error != nil {
		return nil, fmt.Errorf("entitlements: decode response: %w", error)
	}

	return EntitlementsFromResponse(entitlementsResponse)
}

func NewHTTPClient(baseURL string) *HTTPClient {
	return &HTTPClient{
		BaseURL: strings.TrimRight(baseURL, "/"),
		Client:  &http.Client{Timeout: 30 * time.Second},
	}
}

func OfflineActivationPayload(
	activationRequest ActivationRequest,
	privateKey *rsa.PrivateKey,
) (string, error) {
	return signJWT(
		map[string]any{
			"environmentID":   activationRequest.EnvironmentID,
			"environmentName": activationRequest.EnvironmentName,
			"publicKey":       activationRequest.PublicKey,
		},
		activationRequest.EnvironmentID,
		privateKey,
		offlineActivationPayloadExpiration,
	)
}

func PayloadExpired(payload string) bool {
	segments := strings.Split(payload, ".")

	if len(segments) != 3 {
		return true
	}

	claimsJSON, error := base64.RawURLEncoding.DecodeString(segments[1])

	if error != nil {
		return true
	}

	var claims struct {
		Exp int64 `json:"exp"`
	}

	if error := json.Unmarshal(claimsJSON, &claims); error != nil {
		return true
	}

	if claims.Exp == 0 {
		return false
	}

	return time.Now().Unix() >= claims.Exp
}

func decodeJWTPayload(token string) string {
	segments := strings.Split(token, ".")

	if len(segments) != 3 {
		return token
	}

	payload, error := base64.RawURLEncoding.DecodeString(segments[1])

	if error != nil {
		return token
	}

	return string(payload)
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
	logger := logf.FromContext(context)

	logger.V(1).Info(
		"Provisioning POST",
		"payload", redactSensitive(decodeJWTPayload(token)),
		"url", url,
	)

	request, error := http.NewRequestWithContext(
		context, http.MethodPost, url, bytes.NewReader([]byte(token)),
	)

	if error != nil {
		return nil, error
	}

	request.Header.Set("Content-Type", "text/plain")

	response, error := httpClient.Client.Do(request)

	if error != nil {
		return nil, error
	}

	if logger.V(1).Enabled() {
		body, error := io.ReadAll(response.Body)

		response.Body.Close()

		if error != nil {
			return nil, error
		}

		logger.V(1).Info(
			"Provisioning response",
			"body", redactSensitive(string(body)),
			"status", response.StatusCode, "url", url,
		)

		response.Body = io.NopCloser(bytes.NewReader(body))
	}

	return response, nil
}

func redactSensitive(payload string) string {
	var fields map[string]any

	if error := json.Unmarshal([]byte(payload), &fields); error != nil {
		return payload
	}

	redacted := false

	for _, key := range []string{"activationCode", "licenseXML"} {
		if _, ok := fields[key]; ok {
			fields[key] = "[REDACTED]"
			redacted = true
		}
	}

	if !redacted {
		return payload
	}

	marshaled, error := json.Marshal(fields)

	if error != nil {
		return payload
	}

	return string(marshaled)
}

func signJWT(
	claims map[string]any,
	issuer string,
	privateKey *rsa.PrivateKey,
	expiration time.Duration,
) (string, error) {
	now := time.Now()

	payload := maps.Clone(claims)

	randomID, error := encodeRandomID()

	if error != nil {
		return "", error
	}

	payload["exp"] = now.Add(expiration).Unix()
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
