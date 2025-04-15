/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.generator.internal;

import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.osgi.web.wab.generator.internal.artifact.ArtifactURLUtil;
import com.liferay.portal.osgi.web.wab.generator.internal.artifact.WarArtifactUrlTransformer;
import com.liferay.portal.osgi.web.wab.generator.internal.handler.WabURLStreamHandlerService;
import com.liferay.portal.osgi.web.wab.generator.internal.processor.WabProcessor;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
@Component(
	service = com.liferay.portal.osgi.web.wab.generator.WabGenerator.class
)
public class WabGenerator
	implements com.liferay.portal.osgi.web.wab.generator.WabGenerator {

	@Override
	public File generate(
			ClassLoader classLoader, File file,
			Map<String, String[]> parameters)
		throws IOException {

		WabProcessor wabProcessor = new WabProcessor(
			classLoader, file, parameters);

		return wabProcessor.getProcessedFile();
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_registerURLStreamHandlerService(bundleContext);

		_registerArtifactUrlTransformer(bundleContext);

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				Set<String> requiredForStartupContextPaths =
					_getRequiredForStartupContextPaths(
						Paths.get(PropsValues.LIFERAY_HOME, "osgi/portal-war"));

				requiredForStartupContextPaths.addAll(
					_getRequiredForStartupContextPaths(
						Paths.get(PropsValues.LIFERAY_HOME, "osgi/war")));

				if (requiredForStartupContextPaths.isEmpty()) {
					return null;
				}

				CountDownLatch countDownLatch = new CountDownLatch(1);

				BundleTracker<Void> bundleTracker = new BundleTracker<Void>(
					bundleContext, Bundle.ACTIVE, null) {

					@Override
					public Void addingBundle(
						Bundle bundle, BundleEvent bundleEvent) {

						String location = bundle.getLocation();

						if (_log.isDebugEnabled()) {
							_log.debug("Activated bundle " + location);
						}

						if (requiredForStartupContextPaths.remove(
								HttpComponentsUtil.getParameter(
									location, "Web-ContextPath", false))) {

							if (_log.isDebugEnabled()) {
								_log.debug(
									"Bundle " + location +
										" is required for startup");
							}

							if (requiredForStartupContextPaths.isEmpty()) {
								countDownLatch.countDown();
							}
						}

						return null;
					}

				};

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Bundles required for startup: " +
							requiredForStartupContextPaths);
				}

				bundleTracker.open();

				while (true) {
					if (countDownLatch.await(1, TimeUnit.MINUTES)) {
						break;
					}

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Waiting on startup required bundles to " +
								"activate: " + requiredForStartupContextPaths);
					}
				}

				bundleTracker.close();

				if (_log.isDebugEnabled()) {
					_log.debug("All startup required bundles are active");
				}

				return null;
			});
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) throws Exception {
		_serviceRegistration.unregister();

		_serviceRegistration = null;
	}

	private Set<String> _getRequiredForStartupContextPaths(Path path)
		throws Exception {

		Set<String> contextPaths = new HashSet<>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				path.toRealPath(), "*.war")) {

			for (Path warPath : directoryStream) {
				URI uri = warPath.toUri();

				try (ZipFile zipFile = new ZipFile(new File(uri));
					InputStream inputStream = zipFile.getInputStream(
						new ZipEntry(
							"WEB-INF/liferay-plugin-package.properties"))) {

					if (inputStream == null) {
						continue;
					}

					Properties properties = new Properties();

					properties.load(inputStream);

					if (!Boolean.valueOf(
							properties.getProperty("required-for-startup"))) {

						continue;
					}

					String contextName = properties.getProperty(
						"servlet-context-name");

					if (contextName == null) {
						contextName = ArtifactURLUtil.getSymbolicName(
							uri.getPath());
					}

					contextPaths.add("/".concat(contextName));
				}
			}
		}

		return contextPaths;
	}

	private void _registerArtifactUrlTransformer(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			FileInstaller.class, new WarArtifactUrlTransformer(), null);
	}

	private void _registerURLStreamHandlerService(BundleContext bundleContext) {
		Bundle bundle = bundleContext.getBundle(0);

		Class<?> clazz = bundle.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		bundleContext.registerService(
			URLStreamHandlerService.class.getName(),
			new WabURLStreamHandlerService(classLoader, this),
			HashMapDictionaryBuilder.<String, Object>put(
				URLConstants.URL_HANDLER_PROTOCOL, new String[] {"webbundle"}
			).build());
	}

	private static final Log _log = LogFactoryUtil.getLog(WabGenerator.class);

	private ServiceRegistration<FileInstaller> _serviceRegistration;

	@Reference(
		target = "(&(original.bean=true)(bean.id=jakarta.servlet.ServletContext))"
	)
	private ServletContext _servletContext;

}