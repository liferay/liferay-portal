package main

import (
	"os"
	"time"

	env "github.com/caarlos0/env/v11"
	licensingv1alpha1 "github.com/liferay/liferay-portal/cloud/operator/api/licensing/v1alpha1"
	controller "github.com/liferay/liferay-portal/cloud/operator/internal/controller"
	licensing "github.com/liferay/liferay-portal/cloud/operator/internal/controller/licensing"
	liferay "github.com/liferay/liferay-portal/cloud/operator/internal/controller/liferay"
	provisioning "github.com/liferay/liferay-portal/cloud/operator/internal/provisioning"
	runtime "k8s.io/apimachinery/pkg/runtime"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	controllerruntime "sigs.k8s.io/controller-runtime"
	healthz "sigs.k8s.io/controller-runtime/pkg/healthz"
	zap "sigs.k8s.io/controller-runtime/pkg/log/zap"
	metricsserver "sigs.k8s.io/controller-runtime/pkg/metrics/server"
)

func init() {
	utilruntime.Must(clientgoscheme.AddToScheme(scheme))
	utilruntime.Must(licensingv1alpha1.AddToScheme(scheme))
}

func main() {
	config, configError := env.ParseAs[config]()

	controllerruntime.SetLogger(zap.New(zap.UseDevMode(config.Debug)))

	if configError != nil {
		controller.SetupLog.Error(configError, "Unable to read configuration, falling back to defaults")
	}

	manager, error := controllerruntime.NewManager(
		controllerruntime.GetConfigOrDie(),
		controllerruntime.Options{
			HealthProbeBindAddress: config.ProbeAddress,
			Metrics: metricsserver.Options{
				BindAddress: config.MetricsAddress,
			},
			Scheme: scheme,
		},
	)

	if error != nil {
		controller.SetupLog.Error(error, "Unable to start manager")

		os.Exit(1)
	}

	if error := manager.AddHealthzCheck("healthz", healthz.Ping); error != nil {
		controller.SetupLog.Error(error, "Unable to set up health check")

		os.Exit(1)
	}

	if error := manager.AddReadyzCheck("readyz", healthz.Ping); error != nil {
		controller.SetupLog.Error(error, "Unable to set up ready check")

		os.Exit(1)
	}

	if error := controller.SetupWithManager(
		manager,
		&licensing.LiferayEnvironmentReconciler{
			Client:            manager.GetClient(),
			HeartbeatInterval: config.HeartbeatInterval,
			Provisioning:      provisioning.NewHTTPClient(config.ProvisioningBaseURL),
		},
		&liferay.LiferayStatefulSetReconciler{
			Client: manager.GetClient(),
		},
	); error != nil {
		controller.SetupLog.Error(error, "Unable to set up controllers")

		os.Exit(1)
	}

	controller.SetupLog.Info(
		"Starting manager",
		"heartbeatInterval", config.HeartbeatInterval,
		"metricsAddress", config.MetricsAddress,
		"probeAddress", config.ProbeAddress,
		"provisioningBaseURL", config.ProvisioningBaseURL,
	)

	if error := manager.Start(controllerruntime.SetupSignalHandler()); error != nil {
		controller.SetupLog.Error(error, "Unexpected error while running manager")

		os.Exit(1)
	}
}

type config struct {
	Debug               bool          `env:"DEBUG" envDefault:"false"`
	HeartbeatInterval   time.Duration `env:"HEARTBEAT_INTERVAL" envDefault:"10m"`
	MetricsAddress      string        `env:"METRICS_ADDRESS" envDefault:":8080"`
	ProbeAddress        string        `env:"PROBE_ADDRESS" envDefault:":8081"`
	ProvisioningBaseURL string        `env:"PROVISIONING_BASE_URL" envDefault:"https://webserver-lrprovisioning.lfr.cloud"`
}

var scheme = runtime.NewScheme()
