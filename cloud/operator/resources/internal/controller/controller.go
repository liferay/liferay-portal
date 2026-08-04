package controller

import (
	"fmt"

	controllerruntime "sigs.k8s.io/controller-runtime"
)

func SetupWithManager(
	manager controllerruntime.Manager,
	reconcilers ...reconciler,
) error {
	for _, reconciler := range reconcilers {
		if error := reconciler.SetupWithManager(manager); error != nil {
			return fmt.Errorf("unable to create %T controller: %w", reconciler, error)
		}

		SetupLog.Info("Created controller", "reconciler", fmt.Sprintf("%T", reconciler))
	}

	return nil
}

type reconciler interface {
	SetupWithManager(manager controllerruntime.Manager) error
}

var SetupLog = controllerruntime.Log.WithName("setup")
