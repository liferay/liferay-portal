package controller

import (
	"context"

	corev1 "k8s.io/api/core/v1"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
)

func (r *Reconciler) Reconcile(
	ctx context.Context,
	req ctrl.Request,
) (ctrl.Result, error) {
	cm := &corev1.ConfigMap{}

	if err := r.Get(ctx, req.NamespacedName, cm); err != nil {
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}

	log := logf.FromContext(ctx)
	log.Info("Hello, world! ConfigMap reconciled.", "name", cm.Name)

	return ctrl.Result{}, nil
}

func (r *Reconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(
		mgr,
	).For(
		&corev1.ConfigMap{},
	).Named(
		"Controller",
	).Complete(
		r,
	)
}

type Reconciler struct {
	client.Client
}
