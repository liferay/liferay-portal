package main

import (
	"os"

	"github.com/caarlos0/env/v11"
	"k8s.io/apimachinery/pkg/labels"
	"k8s.io/apimachinery/pkg/runtime"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/cache"
	"sigs.k8s.io/controller-runtime/pkg/healthz"
	"sigs.k8s.io/controller-runtime/pkg/log/zap"
	metricsserver "sigs.k8s.io/controller-runtime/pkg/metrics/server"

	"github.com/liferay/liferay-portal/cloud/operator/internal/controller"
)

func init() {
	utilruntime.Must(clientgoscheme.AddToScheme(scheme))
}

func main() {
	cfg, _ := env.ParseAs[config]()

	ctrl.SetLogger(zap.New())

	mgr, err := ctrl.NewManager(
		ctrl.GetConfigOrDie(),
		ctrl.Options{
			Cache: cache.Options{
				DefaultLabelSelector: labels.SelectorFromSet(
					map[string]string{
						"controller-watched": "yes",
					},
				),
			},
			HealthProbeBindAddress: cfg.ProbeAddress,
			Metrics: metricsserver.Options{
				BindAddress: cfg.MetricsAddress,
			},
			Scheme: scheme,
		},
	)

	if err != nil {
		setupLog.Error(err, "Unable to start manager.")

		os.Exit(1)
	}

	if err := mgr.AddHealthzCheck("healthz", healthz.Ping); err != nil {
		setupLog.Error(err, "Unable to set up health check.")

		os.Exit(1)
	}

	if err := mgr.AddReadyzCheck("readyz", healthz.Ping); err != nil {
		setupLog.Error(err, "Unable to set up ready check.")

		os.Exit(1)
	}

	reconciler := &controller.Reconciler{Client: mgr.GetClient()}

	if err := reconciler.SetupWithManager(mgr); err != nil {
		setupLog.Error(err, "Unable to create controller.")

		os.Exit(1)
	}

	if err := mgr.Start(ctrl.SetupSignalHandler()); err != nil {
		setupLog.Error(err, "Unexpected error while running manager.")

		os.Exit(1)
	}
}

type config struct {
	MetricsAddress string `env:"METRICS_ADDRESS" envDefault:":8080"`
	ProbeAddress   string `env:"PROBE_ADDRESS" envDefault:":8081"`
}

var (
	scheme   = runtime.NewScheme()
	setupLog = ctrl.Log.WithName("setup")
)
