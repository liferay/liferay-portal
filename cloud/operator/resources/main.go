package main

import (
	"os"
	"time"

	"github.com/caarlos0/env/v11"
	"k8s.io/apimachinery/pkg/runtime"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/healthz"
	"sigs.k8s.io/controller-runtime/pkg/log/zap"
	metricsserver "sigs.k8s.io/controller-runtime/pkg/metrics/server"

	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	licensingcontroller "github.com/liferay/liferay-portal/cloud/operator/internal/controller/licensing"
)

func init() {
	utilruntime.Must(clientgoscheme.AddToScheme(scheme))
	utilruntime.Must(licensingv1alpha1.AddToScheme(scheme))
}

func main() {
	config, _ := env.ParseAs[config]()

	ctrl.SetLogger(zap.New())

	manager, error := ctrl.NewManager(
		ctrl.GetConfigOrDie(),
		ctrl.Options{
			HealthProbeBindAddress: config.ProbeAddress,
			Metrics: metricsserver.Options{
				BindAddress: config.MetricsAddress,
			},
			Scheme: scheme,
		},
	)

	if error != nil {
		setupLog.Error(error, "Unable to start manager.")

		os.Exit(1)
	}

	if error := manager.AddHealthzCheck("healthz", healthz.Ping); error != nil {
		setupLog.Error(error, "Unable to set up health check.")

		os.Exit(1)
	}

	if error := manager.AddReadyzCheck("readyz", healthz.Ping); error != nil {
		setupLog.Error(error, "Unable to set up ready check.")

		os.Exit(1)
	}

	liferayEnvironmentReconciler := &licensingcontroller.LiferayEnvironmentReconciler{
		Client:            manager.GetClient(),
		HeartbeatInterval: config.HeartbeatInterval,
	}

	if error := liferayEnvironmentReconciler.SetupWithManager(manager); error != nil {
		setupLog.Error(error, "Unable to create liferayenvironment controller.")

		os.Exit(1)
	}

	if error := manager.Start(ctrl.SetupSignalHandler()); error != nil {
		setupLog.Error(error, "Unexpected error while running manager.")

		os.Exit(1)
	}
}

type config struct {
	HeartbeatInterval time.Duration `env:"HEARTBEAT_INTERVAL" envDefault:"10m"`
	MetricsAddress    string        `env:"METRICS_ADDRESS" envDefault:":8080"`
	ProbeAddress      string        `env:"PROBE_ADDRESS" envDefault:":8081"`
}

var (
	scheme   = runtime.NewScheme()
	setupLog = ctrl.Log.WithName("setup")
)
