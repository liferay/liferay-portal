/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.osgi.web.servlet.context.helper.ServletContextHelperFactory;
import com.liferay.portal.osgi.web.wab.extender.internal.configuration.WabExtenderConfiguration;

import java.util.Dictionary;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.osgi.web.wab.extender.internal.configuration.WabExtenderConfiguration",
	service = {}
)
public class WabFactory
	implements BundleTrackerCustomizer<Supplier<WabFactory.WABExtension>> {

	@Override
	public Supplier<WABExtension> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		String contextPath = WabUtil.getWebContextPath(bundle);

		if (contextPath == null) {
			return null;
		}

		FutureTask<WABExtension> futureTask = new FutureTask<>(
			() -> {
				WABExtension wabExtension = new WABExtension(bundle);

				wabExtension.start();

				return wabExtension;
			});

		if (_parallel) {
			ExecutorService executorService =
				SystemExecutorServiceUtil.getExecutorService();

			executorService.submit(futureTask);
		}
		else {
			futureTask.run();
		}

		return () -> {
			try {
				return futureTask.get();
			}
			catch (Exception exception) {
				return ReflectionUtil.throwException(exception);
			}
		};
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		Supplier<WABExtension> wabExtensionSupplier) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		Supplier<WABExtension> wabExtensionSupplier) {

		WABExtension wabExtension = wabExtensionSupplier.get();

		wabExtension.destroy();
	}

	public class WABExtension {

		public WABExtension(Bundle bundle) {
			_bundle = bundle;
		}

		public void destroy() {
			try {
				_started.await(
					_wabExtenderConfiguration.stopTimeout(),
					TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException interruptedException) {
				_log.error(
					String.format(
						"The wait for bundle {0}/{1} to start before " +
							"destroying was interrupted",
						_bundle.getSymbolicName(), _bundle.getBundleId()),
					interruptedException);
			}

			if (_serviceRegistration != null) {
				_serviceRegistration.unregister();
			}

			_webBundleDeployer.doStop(_bundle);
		}

		public void start() {
			try {
				_serviceRegistration = _webBundleDeployer.doStart(_bundle);
			}
			finally {
				_started.countDown();
			}
		}

		private final Bundle _bundle;
		private ServiceRegistration<?> _serviceRegistration;
		private final CountDownLatch _started = new CountDownLatch(1);

	}

	@Activate
	protected void activate(ComponentContext componentContext) {
		_parallel = StartupHelperUtil.isDBWarmed();

		BundleContext bundleContext = componentContext.getBundleContext();

		Dictionary<String, Object> properties =
			componentContext.getProperties();

		_wabExtenderConfiguration = ConfigurableUtil.createConfigurable(
			WabExtenderConfiguration.class, properties);

		_webBundleDeployer = new WebBundleDeployer(
			bundleContext, _jspServletFactory, properties);

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		DependencyManagerSyncUtil.registerSyncFutureTask(
			new FutureTask<>(
				() -> {
					_bundleTracker.open();

					return null;
				}),
			WabFactory.class.getName() + "-BundleTrackerOpener");
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_webBundleDeployer.close();

		_webBundleDeployer = null;
	}

	private static final Log _log = LogFactoryUtil.getLog(WabFactory.class);

	private BundleTracker<?> _bundleTracker;

	@Reference
	private JSPServletFactory _jspServletFactory;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	private boolean _parallel;

	@Reference
	private ServletContextHelperFactory _servletContextHelperFactory;

	private WabExtenderConfiguration _wabExtenderConfiguration;
	private WebBundleDeployer _webBundleDeployer;

}