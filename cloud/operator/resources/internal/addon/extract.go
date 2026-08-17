package addon

import (
	"archive/zip"
	"context"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

const addOnsPrefix = "add-ons/"

func Extract(
	addOns []provisioning.AddOn,
	cache Cache,
	context context.Context,
	zipReader *zip.Reader,
) []licensingv1alpha1.AppStatus {
	logger := logf.FromContext(context)

	apps := make([]licensingv1alpha1.AppStatus, 0, len(addOns))

	for _, addOn := range addOns {
		if error := extract(addOn, cache, zipReader); error != nil {
			logger.Error(
				error, "Unable to extract add-on",
				"productName", addOn.ProductName,
				"virtualEntryId", addOn.VirtualEntryID,
			)

			apps = append(apps, newAppStatus(addOn, stateFailed))

			continue
		}

		apps = append(apps, newAppStatus(addOn, stateDownloaded))
	}

	return apps
}

func extract(addOn provisioning.AddOn, cache Cache, zipReader *zip.Reader) error {
	has, error := cache.Has(addOn.SHA256Checksum, addOn.VirtualEntryID)

	if error == nil && has {
		return nil
	}

	entry, error := zipReader.Open(addOnsPrefix + addOn.ProductID + ".lpkg")

	if error != nil {
		return error
	}

	defer entry.Close()

	return cache.Save(addOn.SHA256Checksum, entry, addOn.VirtualEntryID)
}
