/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;

import freemarker.core.Environment;
import freemarker.core.TemplateClassResolver;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.Execute;
import freemarker.template.utility.ObjectConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	service = TemplateClassResolver.class
)
public class LiferayTemplateClassResolver implements TemplateClassResolver {

	@Override
	public Class<?> resolve(
			String className, Environment environment, Template template)
		throws TemplateException {

		if (className.equals(Execute.class.getName()) ||
			className.equals(ObjectConstructor.class.getName())) {

			throw new TemplateException(
				StringBundler.concat(
					"Instantiating ", className, " is not allowed in the ",
					"template for security reasons"),
				environment);
		}

		String[] restrictedClassNames = GetterUtil.getStringValues(
			_freeMarkerEngineConfiguration.restrictedClasses());

		for (String restrictedClassName : restrictedClassNames) {
			if (match(restrictedClassName, className)) {
				throw new TemplateException(
					StringBundler.concat(
						"Instantiating ", className, " is not allowed in the ",
						"template for security reasons"),
					environment);
			}
		}

		boolean allowed = false;

		String[] allowedClasseNames = GetterUtil.getStringValues(
			_freeMarkerEngineConfiguration.allowedClasses());

		for (String allowedClassName : allowedClasseNames) {
			if (match(allowedClassName, className)) {
				allowed = true;

				break;
			}
		}

		if (allowed) {
			try {
				ClassLoader[] wwhitelistedClassLoaders =
					_whitelistedClassLoaders.toArray(new ClassLoader[0]);

				Thread currentThread = Thread.currentThread();

				ClassLoader[] classLoaders = ArrayUtil.append(
					wwhitelistedClassLoaders,
					currentThread.getContextClassLoader());

				ClassLoader wwhitelistedAggregateClassLoader =
					AggregateClassLoader.getAggregateClassLoader(classLoaders);

				return Class.forName(
					className, true, wwhitelistedAggregateClassLoader);
			}
			catch (Exception exception) {
				throw new TemplateException(exception, environment);
			}
		}

		throw new TemplateException(
			StringBundler.concat(
				"Instantiating ", className, " is not allowed in the template ",
				"for security reasons"),
			environment);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, properties);

		_classLoaderBundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE,
			new ClassLoaderBundleTrackerCustomizer());

		_classLoaderBundleTracker.open();

		_whitelistedClassLoaders.add(
			LiferayTemplateClassResolver.class.getClassLoader());
	}

	@Deactivate
	protected void deactivate() {
		_classLoaderBundleTracker.close();
	}

	protected boolean match(String className, String matchedClassName) {
		if (className.equals(StringPool.STAR)) {
			return true;
		}
		else if (className.endsWith(StringPool.STAR)) {
			if (matchedClassName.regionMatches(
					0, className, 0, className.length() - 1)) {

				return true;
			}
		}
		else if (className.equals(matchedClassName)) {
			return true;
		}
		else {
			int index = className.lastIndexOf('.');

			if ((className.length() == index) &&
				className.regionMatches(0, matchedClassName, 0, index)) {

				return true;
			}
		}

		return false;
	}

	@Modified
	protected void modified(
		BundleContext bundleContext, Map<String, Object> properties) {

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, properties);

		for (Bundle bundle : _bundles) {
			ClassLoader classLoader = _findClassLoader(
				_freeMarkerEngineConfiguration.allowedClasses(),
				bundle.getBundleContext());

			if (classLoader == null) {
				continue;
			}

			_whitelistedClassLoaders.add(classLoader);
		}
	}

	private ClassLoader _findClassLoader(
		String clazz, BundleContext bundleContext) {

		Bundle bundle = bundleContext.getBundle();

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<BundleCapability> bundleCapabilities =
			bundleWiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);

		for (BundleCapability bundleCapability : bundleCapabilities) {
			Map<String, Object> attributes = bundleCapability.getAttributes();

			String packageName = (String)attributes.get(
				BundleRevision.PACKAGE_NAMESPACE);

			if (clazz.endsWith(StringPool.STAR)) {
				if (packageName.regionMatches(
						0, clazz, 0, clazz.length() - 1)) {

					BundleRevision bundleRevision =
						bundleCapability.getRevision();

					Bundle bundleRevisionBundle = bundleRevision.getBundle();

					BundleWiring bundleRevisionBundleWiring =
						bundleRevisionBundle.adapt(BundleWiring.class);

					return bundleRevisionBundleWiring.getClassLoader();
				}
			}
			else if (clazz.equals(packageName)) {
				BundleRevision bundleRevision = bundleCapability.getRevision();

				Bundle bundleRevisionBundle = bundleRevision.getBundle();

				BundleWiring bundleRevisionBundleWiring =
					bundleRevisionBundle.adapt(BundleWiring.class);

				return bundleRevisionBundleWiring.getClassLoader();
			}
			else {
				int index = clazz.lastIndexOf('.');

				if ((packageName.length() == index) &&
					packageName.regionMatches(0, clazz, 0, index)) {

					BundleRevision bundleRevision =
						bundleCapability.getRevision();

					Bundle bundleRevisionBundle = bundleRevision.getBundle();

					BundleWiring bundleRevisionBundleWiring =
						bundleRevisionBundle.adapt(BundleWiring.class);

					return bundleRevisionBundleWiring.getClassLoader();
				}
			}
		}

		return null;
	}

	private ClassLoader _findClassLoader(
		String[] allowedClassNames, BundleContext bundleContext) {

		if (allowedClassNames == null) {
			allowedClassNames = new String[0];
		}

		for (String allowedClassName : allowedClassNames) {
			if (Validator.isBlank(allowedClassName) ||
				allowedClassName.equals(StringPool.STAR)) {

				continue;
			}

			ClassLoader classLoader = _findClassLoader(
				allowedClassName, bundleContext);

			if (classLoader != null) {
				return classLoader;
			}

			if (_log.isWarnEnabled()) {
				Bundle bundle = bundleContext.getBundle();

				_log.warn(
					StringBundler.concat(
						"Bundle ", bundle.getSymbolicName(),
						" does not export ", allowedClassName));
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayTemplateClassResolver.class);

	private final Set<Bundle> _bundles = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	private BundleTracker<ClassLoader> _classLoaderBundleTracker;
	private volatile FreeMarkerEngineConfiguration
		_freeMarkerEngineConfiguration;
	private final Set<ClassLoader> _whitelistedClassLoaders =
		Collections.newSetFromMap(new ConcurrentHashMap<>());

	private class ClassLoaderBundleTrackerCustomizer
		implements BundleTrackerCustomizer<ClassLoader> {

		@Override
		public ClassLoader addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			ClassLoader classLoader = _findClassLoader(
				_freeMarkerEngineConfiguration.allowedClasses(),
				bundle.getBundleContext());

			if (classLoader != null) {
				_whitelistedClassLoaders.add(classLoader);
			}

			_bundles.add(bundle);

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			return bundleWiring.getClassLoader();
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent, ClassLoader classLoader) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent, ClassLoader classLoader) {

			_whitelistedClassLoaders.remove(classLoader);

			_bundles.remove(bundle);
		}

	}

}