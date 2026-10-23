package licensing

import (
	"bytes"
	"context"
	"fmt"
	"maps"
	"regexp"
	"testing"
	"time"

	addon "github.com/liferay/liferay-portal/cloud/operator/internal/addon"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	record "k8s.io/client-go/tools/record"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
)

func TestDeploySyncScriptVariables(t *testing.T) {
	variables := map[string]bool{}

	variablePattern := regexp.MustCompile(`\$\{(LIFERAY_SYNC_[A-Z_]+):([?-])`)

	for _, match := range variablePattern.FindAllStringSubmatch(string(deploySyncScript), -1) {
		variables[match[1]] = match[2] == "?"
	}

	want := map[string]bool{
		"LIFERAY_SYNC_DEST":     true,
		"LIFERAY_SYNC_INTERVAL": false,
		"LIFERAY_SYNC_MARKER":   true,
		"LIFERAY_SYNC_PATTERN":  false,
		"LIFERAY_SYNC_SOURCE":   true,
	}

	if !maps.Equal(variables, want) {
		t.Errorf("deploy-sync.sh variables (name: required) = %v, want %v", variables, want)
	}
}

func TestReconcilePublishesDeploySyncSecret(t *testing.T) {
	objects := []client.Object{
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&corev1.Secret{
			Data: map[string][]byte{
				"activationCode": []byte("one-time-code"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-activation",
				Namespace: "liferay-dev",
			},
		},
		pendingEnvironment(),
	}

	provisioningClient := &stubProvisioning{
		activateError: fmt.Errorf("provisioning: activation code rejected"),
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(provisioningClient, t, objects...)

	secret := getSecret("dev-deploy-sync", liferayEnvironmentReconciler, t)

	if !bytes.Equal(secret.Data["deploy-sync.sh"], deploySyncScript) {
		t.Errorf("Data[%q] = %q, want the embedded deploy sync script", "deploy-sync.sh", secret.Data["deploy-sync.sh"])
	}

	assertOwnedSecret(liferayEnvironmentReconciler, "dev-deploy-sync", t)
}

func TestReconcilePublishesDeploySyncSecretWhenIdentityUnreadable(t *testing.T) {
	provisioningClient := &stubProvisioning{}

	liferayEnvironmentReconciler := &LiferayEnvironmentReconciler{
		Client: newFakeClient(
			t,
			&corev1.Namespace{
				ObjectMeta: metav1.ObjectMeta{
					Name: "liferay-dev",
					UID:  "dev-namespace-uid",
				},
			},
			&corev1.Secret{
				Data: map[string][]byte{
					"private.pem": []byte("not a private key"),
				},
				ObjectMeta: metav1.ObjectMeta{
					Name:      "dev-identity",
					Namespace: "liferay-dev",
				},
			},
			pendingEnvironment(),
		),
		HeartbeatInterval:    10 * time.Minute,
		MarketplaceMountPath: t.TempDir(),
		Provisioning:         provisioningClient,
		Recorder:             record.NewFakeRecorder(10),
		Syncer: addon.NewSyncer(
			provisioningClient, 15*time.Second, 30*time.Second, 30*time.Minute,
			inlineRunner{},
		),
	}

	if _, error := liferayEnvironmentReconciler.Reconcile(
		context.Background(), controllerruntime.Request{
			NamespacedName: types.NamespacedName{
				Name:      "dev",
				Namespace: "liferay-dev",
			},
		},
	); error == nil {
		t.Fatal("Reconcile() error = <nil>, want the unreadable identity rejected")
	}

	secret := getSecret("dev-deploy-sync", liferayEnvironmentReconciler, t)

	if !bytes.Equal(secret.Data["deploy-sync.sh"], deploySyncScript) {
		t.Errorf("Data[%q] = %q, want the embedded deploy sync script", "deploy-sync.sh", secret.Data["deploy-sync.sh"])
	}
}

func TestReconcileRestoresDeploySyncSecret(t *testing.T) {
	objects := []client.Object{
		&corev1.Namespace{
			ObjectMeta: metav1.ObjectMeta{
				Name: "liferay-dev",
				UID:  "dev-namespace-uid",
			},
		},
		&corev1.Secret{
			Data: map[string][]byte{
				"activationCode": []byte("one-time-code"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-activation",
				Namespace: "liferay-dev",
			},
		},
		&corev1.Secret{
			Data: map[string][]byte{
				"deploy-sync.sh": []byte("echo edited"),
			},
			ObjectMeta: metav1.ObjectMeta{
				Name:      "dev-deploy-sync",
				Namespace: "liferay-dev",
			},
		},
		pendingEnvironment(),
	}

	provisioningClient := &stubProvisioning{
		activateError: fmt.Errorf("provisioning: activation code rejected"),
	}

	liferayEnvironmentReconciler, _ := reconcileEnvironment(provisioningClient, t, objects...)

	secret := getSecret("dev-deploy-sync", liferayEnvironmentReconciler, t)

	if !bytes.Equal(secret.Data["deploy-sync.sh"], deploySyncScript) {
		t.Errorf("Data[%q] = %q, want the embedded deploy sync script", "deploy-sync.sh", secret.Data["deploy-sync.sh"])
	}

	assertOwnedSecret(liferayEnvironmentReconciler, "dev-deploy-sync", t)
}
