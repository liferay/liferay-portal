package licensing

import (
	"context"
	_ "embed"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
)

const deploySyncSecretSuffix = "-deploy-sync"

func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) ensureDeploySyncSecret(
	context context.Context,
	liferayEnvironment *licensingv1alpha1.LiferayEnvironment,
) error {
	return liferayEnvironmentReconciler.ensureOwnedSecret(
		context, map[string][]byte{"deploy-sync.sh": deploySyncScript}, liferayEnvironment,
		liferayEnvironment.Name+deploySyncSecretSuffix,
	)
}

//go:embed deploy-sync.sh
var deploySyncScript []byte
