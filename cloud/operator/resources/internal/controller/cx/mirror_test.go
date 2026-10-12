package cx

import (
	"context"
	"errors"
	"maps"
	"reflect"
	"strings"
	"testing"

	cxv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/cx/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	types "k8s.io/apimachinery/pkg/types"
	controllerruntime "sigs.k8s.io/controller-runtime"
	client "sigs.k8s.io/controller-runtime/pkg/client"
	interceptor "sigs.k8s.io/controller-runtime/pkg/client/interceptor"
	reconcile "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const userAgentApplicationPID = "com.liferay.oauth2.provider.configuration.OAuth2ProviderApplicationUserAgentConfiguration~able-oauth-application-user-agent"

func TestReconcileDeletesMirrorsWhenRefused(t *testing.T) {
	testCases := map[string]struct {
		refuse     func(clientExtensionReconciler *ClientExtensionReconciler)
		wantReason string
	}{
		"the DXP namespace no longer exists": {
			refuse: func(clientExtensionReconciler *ClientExtensionReconciler) {
				if error := clientExtensionReconciler.Delete(context.Background(), newDxpNamespace("able")); error != nil {
					t.Fatal(error)
				}
			},
			wantReason: ReasonDxpNamespaceNotFound,
		},
		"the DXP namespace no longer permits it": {
			refuse: func(clientExtensionReconciler *ClientExtensionReconciler) {
				dxpNamespace := newDxpNamespace("")

				if error := clientExtensionReconciler.Update(context.Background(), dxpNamespace); error != nil {
					t.Fatal(error)
				}
			},
			wantReason: ReasonNamespaceNotPermitted,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			clientExtension := newClientExtension("liferay-dev", "able", "able")

			addOAuth2Application(clientExtension, userAgentApplicationPID)

			clientExtensionReconciler := newClientExtensionReconciler(
				nil, t, clientExtension, newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
				newDxpNamespace("able"), newExtInitConfigMap("able-oauth-application-user-agent"),
			)

			reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

			testCase.refuse(clientExtensionReconciler)

			if _, reason := reconcileClientExtension(
				clientExtension, clientExtensionReconciler, t,
			); reason != testCase.wantReason {
				t.Errorf("Delivered reason = %q, want %q", reason, testCase.wantReason)
			}

			if mirrored := getCondition(
				clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionMirrored, t,
			); mirrored != nil {
				t.Errorf("Mirrored = %v, want it removed once the client extension is refused", mirrored)
			}

			for _, name := range []string{"able-liferay.com-lxc-ext-init-metadata", "liferay.com-lxc-dxp-metadata"} {
				if getConfigMap(clientExtensionReconciler, name, "able", t) != nil {
					t.Errorf(
						"Expected the mirror %s to be deleted once the client extension is refused",
						name,
					)
				}
			}
		})
	}
}

func TestReconcileIgnoresMirrorAsDxpMetadata(t *testing.T) {
	able := newClientExtension("liferay-dev", "able", "able")
	baker := newClientExtension("", "baker", "able")

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, able, baker, newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	reconcileClientExtension(able, clientExtensionReconciler, t)

	if getConfigMap(clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "able", t) == nil {
		t.Fatal("Expected the dxp metadata mirror")
	}

	if _, reason := reconcileClientExtension(
		baker, clientExtensionReconciler, t,
	); reason != ReasonUnknownVirtualInstance {
		t.Errorf("Delivered reason = %q, want %q: a mirror does not make its namespace a DXP", reason, ReasonUnknownVirtualInstance)
	}

	if getConfigMap(clientExtensionReconciler, "baker-liferay.com-lxc-ext-provision-metadata", "able", t) != nil {
		t.Error("Expected no ext-provision ConfigMap beside a mirror")
	}
}

func TestReconcileKeepsMirrorsWhenVirtualInstanceDisappears(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newDxpNamespace("able"), newExtInitConfigMap("able-oauth-application-user-agent"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	if error := clientExtensionReconciler.Delete(
		context.Background(), newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
	); error != nil {
		t.Fatal(error)
	}

	if _, reason := reconcileClientExtension(
		clientExtension, clientExtensionReconciler, t,
	); reason != ReasonUnknownVirtualInstance {
		t.Errorf("Delivered reason = %q, want %q", reason, ReasonUnknownVirtualInstance)
	}

	for _, name := range []string{"able-liferay.com-lxc-ext-init-metadata", "liferay.com-lxc-dxp-metadata"} {
		if getConfigMap(clientExtensionReconciler, name, "able", t) == nil {
			t.Errorf(
				"Expected the mirror %s to remain while the virtual instance is missing, which may be brief",
				name,
			)
		}
	}
}

func TestReconcileMirrorsDxpMetadataIntoClientExtensionNamespace(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	dxpMetadata := newDxpMetadataConfigMap("liferay-dev", "liferay.com")

	dxpMetadata.Data = map[string]string{"com.liferay.lxc.dxp.mainDomain": "liferay.example.com"}

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, dxpMetadata, newDxpNamespace("able"),
	)

	if phase, _ := reconcileClientExtension(
		clientExtension, clientExtensionReconciler, t,
	); phase != cxv1alpha1.PhaseReady {
		t.Errorf("phase = %q, want %q", phase, cxv1alpha1.PhaseReady)
	}

	configMap := getConfigMap(
		clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "able", t,
	)

	if configMap == nil {
		t.Fatal("Expected ConfigMap able/liferay.com-lxc-dxp-metadata")
	}

	if !maps.Equal(configMap.Data, dxpMetadata.Data) {
		t.Errorf("data = %v, want %v", configMap.Data, dxpMetadata.Data)
	}

	wantLabels := map[string]string{
		LabelMetadataType:    MetadataTypeDxp,
		LabelMirror:          "true",
		LabelVirtualInstance: "liferay.com",
	}

	if !maps.Equal(configMap.Labels, wantLabels) {
		t.Errorf("labels = %v, want %v", configMap.Labels, wantLabels)
	}

	if source := configMap.Annotations[AnnotationSource]; source != "liferay-dev/liferay.com-lxc-dxp-metadata" {
		t.Errorf("source = %q, want %q", source, "liferay-dev/liferay.com-lxc-dxp-metadata")
	}

	if (len(configMap.OwnerReferences) != 1) || (clientExtension.UID != configMap.OwnerReferences[0].UID) ||
		((configMap.OwnerReferences[0].Controller != nil) && *configMap.OwnerReferences[0].Controller) {

		t.Errorf(
			"owner references = %v, want one non-controller reference to the client extension",
			configMap.OwnerReferences,
		)
	}
}

func TestReconcileMirrorsExtInitIntoClientExtensionNamespace(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	extInit := newExtInitConfigMap("able-oauth-application-user-agent")

	extInit.Data["able-oauth-application-user-agent.oauth2.user.agent.client.id"] = "able-id"

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, extInit, newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newDxpNamespace("able"),
	)

	if phase, _ := reconcileClientExtension(
		clientExtension, clientExtensionReconciler, t,
	); phase != cxv1alpha1.PhaseReady {
		t.Errorf("phase = %q, want %q", phase, cxv1alpha1.PhaseReady)
	}

	configMap := getConfigMap(
		clientExtensionReconciler, "able-liferay.com-lxc-ext-init-metadata", "able", t,
	)

	if configMap == nil {
		t.Fatal("Expected ConfigMap able/able-liferay.com-lxc-ext-init-metadata")
	}

	if !maps.Equal(configMap.Data, extInit.Data) {
		t.Errorf("data = %v, want %v", configMap.Data, extInit.Data)
	}

	wantLabels := map[string]string{
		LabelMetadataType:    MetadataTypeExtInit,
		LabelMirror:          "true",
		LabelServiceID:       "able",
		LabelVirtualInstance: "liferay.com",
	}

	if !maps.Equal(configMap.Labels, wantLabels) {
		t.Errorf("labels = %v, want %v", configMap.Labels, wantLabels)
	}

	if source := configMap.Annotations[AnnotationSource]; source != "liferay-dev/able-liferay.com-lxc-ext-init-metadata" {
		t.Errorf("source = %q, want %q", source, "liferay-dev/able-liferay.com-lxc-ext-init-metadata")
	}

	if controllerReference := metav1.GetControllerOf(configMap); (controllerReference == nil) || (clientExtension.UID != controllerReference.UID) {
		t.Errorf("controller = %v, want the client extension", controllerReference)
	}

	mirrored := getCondition(clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionMirrored, t)

	if (mirrored == nil) || (mirrored.Status != metav1.ConditionTrue) || (mirrored.Reason != ReasonMirrored) {
		t.Fatalf("Mirrored = %v, want True / %s", mirrored, ReasonMirrored)
	}

	if want := `Mirrored DXP's ConfigMaps "liferay.com-lxc-dxp-metadata", "able-liferay.com-lxc-ext-init-metadata" into namespace "able".`; mirrored.Message != want {
		t.Errorf("Mirrored message = %q, want %q", mirrored.Message, want)
	}
}

func TestReconcileMirrorsNothingIntoDxpNamespace(t *testing.T) {
	clientExtension := newClientExtension("", "able", "liferay-dev")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, newDxpMetadataConfigMap("liferay-dev", "liferay.com"),
		newExtInitConfigMap("able-oauth-application-user-agent"),
	)

	if phase, _ := reconcileClientExtension(clientExtension, clientExtensionReconciler, t); phase != cxv1alpha1.PhaseReady {
		t.Errorf("phase = %q, want %q", phase, cxv1alpha1.PhaseReady)
	}

	var configMapList corev1.ConfigMapList

	if error := clientExtensionReconciler.List(
		context.Background(), &configMapList, client.MatchingLabels{LabelMirror: "true"},
	); error != nil {
		t.Fatal(error)
	}

	if len(configMapList.Items) != 0 {
		t.Errorf("Expected no mirror where the workload can mount DXP's own ConfigMaps, got %d", len(configMapList.Items))
	}

	if mirrored := getCondition(
		clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionMirrored, t,
	); (mirrored == nil) || (mirrored.Status != metav1.ConditionTrue) || (mirrored.Reason != ReasonNoMirrorRequired) {
		t.Errorf("Mirrored = %v, want True / %s", mirrored, ReasonNoMirrorRequired)
	}

	for _, name := range []string{"able-liferay.com-lxc-ext-init-metadata", "liferay.com-lxc-dxp-metadata"} {
		if configMap := getConfigMap(clientExtensionReconciler, name, "liferay-dev", t); (configMap == nil) || (len(configMap.OwnerReferences) != 0) {
			t.Errorf("Expected DXP's ConfigMap %s to be untouched, got %v", name, configMap)
		}
	}
}

func TestReconcileReleasesStaleDxpMetadataMirror(t *testing.T) {
	able := newClientExtension("liferay-dev", "able", "able")
	baker := newClientExtension("liferay-dev", "baker", "able")

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, able, baker,
		newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpMetadataConfigMap("liferay-dev", "other.test"), newDxpNamespace("able"),
	)

	reconcileClientExtension(able, clientExtensionReconciler, t)
	reconcileClientExtension(baker, clientExtensionReconciler, t)

	moveToOtherVirtualInstance := func(clientExtension *cxv1alpha1.ClientExtension) {
		updatedClientExtension := getClientExtension(clientExtension, clientExtensionReconciler, t)

		updatedClientExtension.Spec.VirtualInstanceID = "other.test"

		if error := clientExtensionReconciler.Update(context.Background(), updatedClientExtension); error != nil {
			t.Fatal(error)
		}

		reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

		if getConfigMap(clientExtensionReconciler, "other.test-lxc-dxp-metadata", "able", t) == nil {
			t.Errorf("Expected %s to mirror the new virtual instance", clientExtension.Name)
		}
	}

	moveToOtherVirtualInstance(able)

	configMap := getConfigMap(clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "able", t)

	if (configMap == nil) || (len(configMap.OwnerReferences) != 1) || (baker.UID != configMap.OwnerReferences[0].UID) {
		t.Fatalf("Expected the old mirror to remain, owned by baker alone, got %v", configMap)
	}

	moveToOtherVirtualInstance(baker)

	if getConfigMap(clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "able", t) != nil {
		t.Error("Expected the old mirror to be deleted once no client extension uses it")
	}
}

func TestReconcileReplacesMirrorDataWhenSourceChanges(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	extInit := newExtInitConfigMap()

	extInit.Data = map[string]string{"able.oauth2.user.agent.client.id": "able-id", "able.oauth2.user.agent.scopes": "everything"}

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, extInit, newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	extInit.Data = map[string]string{"able.oauth2.user.agent.client.id": "able-new-id"}

	if error := clientExtensionReconciler.Update(context.Background(), extInit); error != nil {
		t.Fatal(error)
	}

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	configMap := getConfigMap(clientExtensionReconciler, "able-liferay.com-lxc-ext-init-metadata", "able", t)

	if !maps.Equal(configMap.Data, extInit.Data) {
		t.Errorf("data = %v, want %v: a key DXP removed must not linger in the mirror", configMap.Data, extInit.Data)
	}
}

func TestReconcileReportsMirrorConflict(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	foreignConfigMap := &corev1.ConfigMap{
		Data: map[string]string{"owner": "untouched"},
		ObjectMeta: metav1.ObjectMeta{
			Labels:    map[string]string{LabelMetadataType: MetadataTypeDxp, LabelVirtualInstance: "liferay.com"},
			Name:      "liferay.com-lxc-dxp-metadata",
			Namespace: "able",
		},
	}

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, clientExtension, foreignConfigMap,
		newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if result.RequeueAfter <= 0 {
		t.Errorf("Reconcile() RequeueAfter = %v, want a retry", result.RequeueAfter)
	}

	if phase := getClientExtension(clientExtension, clientExtensionReconciler, t).Status.Phase; phase != cxv1alpha1.PhaseDegraded {
		t.Errorf("phase = %q, want %q", phase, cxv1alpha1.PhaseDegraded)
	}

	mirrored := getCondition(clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionMirrored, t)

	if (mirrored == nil) || (mirrored.Status != metav1.ConditionFalse) || (mirrored.Reason != ReasonMirrorFailed) {
		t.Fatalf("Mirrored = %v, want False / %s", mirrored, ReasonMirrorFailed)
	}

	want := `configmap "liferay.com-lxc-dxp-metadata" exists and is not the mirror of "liferay-dev/liferay.com-lxc-dxp-metadata"`

	if !strings.Contains(mirrored.Message, want) {
		t.Errorf("Mirrored message = %q, want it to contain %q", mirrored.Message, want)
	}

	if provisioned := getCondition(
		clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionProvisioned, t,
	); (provisioned == nil) || (provisioned.Status != metav1.ConditionTrue) || (provisioned.Reason != ReasonNoExtInitRequired) {
		t.Errorf("Provisioned = %v, want True / %s whatever the mirror does", provisioned, ReasonNoExtInitRequired)
	}

	configMap := getConfigMap(clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "able", t)

	if !maps.Equal(configMap.Data, foreignConfigMap.Data) || !maps.Equal(configMap.Labels, foreignConfigMap.Labels) {
		t.Errorf("Expected the foreign ConfigMap to be untouched, got %v / %v", configMap.Labels, configMap.Data)
	}
}

func TestReconcileReportsMirrorNotPermitted(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	clientExtensionReconciler := newClientExtensionReconciler(
		&interceptor.Funcs{
			Create: func(
				context context.Context, client client.WithWatch, object client.Object,
				options ...client.CreateOption,
			) error {
				if _, ok := object.(*corev1.ConfigMap); ok && (object.GetNamespace() == "able") {
					return apierrors.NewForbidden(corev1.Resource("configmaps"), object.GetName(), errors.New("no RoleBinding"))
				}

				return client.Create(context, object, options...)
			},
		},
		t, clientExtension, newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	clientExtensionReconciler.ServiceAccount = "liferay-system/dxp-operator"

	reconcileClientExtension(clientExtension, clientExtensionReconciler, t)

	mirrored := getCondition(clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionMirrored, t)

	if (mirrored == nil) || (mirrored.Reason != ReasonMirrorFailed) {
		t.Fatalf("Mirrored = %v, want reason %s", mirrored, ReasonMirrorFailed)
	}

	if want := `The DXP operator is not permitted to write ConfigMaps in namespace "able", where it mirrors DXP's metadata. Grant that by binding ClusterRole "client-extension-delivery-cluster-role" to ServiceAccount "liferay-system/dxp-operator" there.`; mirrored.Message != want {
		t.Errorf("Mirrored message = %q, want %q", mirrored.Message, want)
	}
}

func TestReconcileReportsMirrorNotPermittedAlongsideMissingExtInit(t *testing.T) {
	clientExtension := newClientExtension("liferay-dev", "able", "able")

	addOAuth2Application(clientExtension, userAgentApplicationPID)

	clientExtensionReconciler := newClientExtensionReconciler(
		&interceptor.Funcs{
			Create: func(
				context context.Context, client client.WithWatch, object client.Object,
				options ...client.CreateOption,
			) error {
				if _, ok := object.(*corev1.ConfigMap); ok && (object.GetNamespace() == "able") {
					return apierrors.NewForbidden(corev1.Resource("configmaps"), object.GetName(), errors.New("no RoleBinding"))
				}

				return client.Create(context, object, options...)
			},
		},
		t, clientExtension, newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	result, error := clientExtensionReconciler.Reconcile(
		context.Background(),
		controllerruntime.Request{NamespacedName: client.ObjectKeyFromObject(clientExtension)},
	)

	if error != nil {
		t.Fatalf("Reconcile() error = %v, want nil", error)
	}

	if (result.RequeueAfter <= 0) || (result.RequeueAfter > extInitGracePeriod) {
		t.Errorf("Reconcile() RequeueAfter = %v, want a retry within %v", result.RequeueAfter, extInitGracePeriod)
	}

	if phase := getClientExtension(clientExtension, clientExtensionReconciler, t).Status.Phase; phase != cxv1alpha1.PhaseDegraded {
		t.Errorf("phase = %q, want %q: the grace period covers DXP writing ext-init, not a mirror the operator cannot write", phase, cxv1alpha1.PhaseDegraded)
	}

	if mirrored := getCondition(
		clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionMirrored, t,
	); (mirrored == nil) || (mirrored.Reason != ReasonMirrorFailed) {
		t.Errorf("Mirrored = %v, want reason %s", mirrored, ReasonMirrorFailed)
	}

	if provisioned := getCondition(
		clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionProvisioned, t,
	); (provisioned == nil) || (provisioned.Reason != ReasonExtInitMissing) {
		t.Errorf("Provisioned = %v, want reason %s", provisioned, ReasonExtInitMissing)
	}

	if ready := getCondition(
		clientExtension, clientExtensionReconciler, cxv1alpha1.ConditionReady, t,
	); (ready == nil) || (ready.Reason != ReasonMirrorFailed) {
		t.Errorf("Ready = %v, want reason %s", ready, ReasonMirrorFailed)
	}
}

func TestReconcileSharesDxpMetadataMirrorBetweenClientExtensions(t *testing.T) {
	able := newClientExtension("liferay-dev", "able", "able")
	baker := newClientExtension("liferay-dev", "baker", "able")

	clientExtensionReconciler := newClientExtensionReconciler(
		nil, t, able, baker, newDxpMetadataConfigMap("liferay-dev", "liferay.com"), newDxpNamespace("able"),
	)

	reconcileClientExtension(able, clientExtensionReconciler, t)
	reconcileClientExtension(baker, clientExtensionReconciler, t)

	configMap := getConfigMap(clientExtensionReconciler, "liferay.com-lxc-dxp-metadata", "able", t)

	var ownerUIDs []types.UID

	for _, ownerReference := range configMap.OwnerReferences {
		ownerUIDs = append(ownerUIDs, ownerReference.UID)
	}

	if wantOwnerUIDs := []types.UID{able.UID, baker.UID}; !reflect.DeepEqual(ownerUIDs, wantOwnerUIDs) {
		t.Errorf("owners = %v, want %v", ownerUIDs, wantOwnerUIDs)
	}
}

func TestRequestsForConfigMapMapsMirrorToItsOwners(t *testing.T) {
	clientExtensionReconciler := newClientExtensionReconciler(nil, t, newClientExtension("able", "charlie", "liferay-dev"))

	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Labels: map[string]string{
				LabelMetadataType:    MetadataTypeDxp,
				LabelMirror:          "true",
				LabelVirtualInstance: "liferay.com",
			},
			Name:      "liferay.com-lxc-dxp-metadata",
			Namespace: "able",
			OwnerReferences: []metav1.OwnerReference{
				{APIVersion: "cx.liferay.com/v1alpha1", Kind: "ClientExtension", Name: "able", UID: "able-able"},
				{APIVersion: "apps/v1", Kind: "Deployment", Name: "delta", UID: "able-delta"},
				{APIVersion: "cx.liferay.com/v1alpha1", Kind: "ClientExtension", Name: "baker", UID: "able-baker"},
				{APIVersion: "cx.liferay.com/v1beta1", Kind: "ClientExtension", Name: "charlie", UID: "able-charlie"},
				{APIVersion: "other.liferay.com/v1alpha1", Kind: "ClientExtension", Name: "dog", UID: "able-dog"},
			},
		},
	}

	want := []reconcile.Request{
		{NamespacedName: types.NamespacedName{Name: "able", Namespace: "able"}},
		{NamespacedName: types.NamespacedName{Name: "baker", Namespace: "able"}},
		{NamespacedName: types.NamespacedName{Name: "charlie", Namespace: "able"}},
	}

	if got := clientExtensionReconciler.requestsForConfigMap(context.Background(), configMap); !reflect.DeepEqual(got, want) {
		t.Errorf("requestsForConfigMap() = %v, want %v: a mirror maps to its owners, not to the client extensions delivering to its namespace", got, want)
	}
}
