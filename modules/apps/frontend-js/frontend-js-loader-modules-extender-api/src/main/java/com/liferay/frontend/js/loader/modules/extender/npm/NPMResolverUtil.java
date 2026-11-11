/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.npm;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * This class provides the same functionality as NPMResolver but without the
 * need to reference any OSGi service or component.
 *
 * @author Iván Zaera Avellón
 * @see    NPMResolver
 * @review
 */
public class NPMResolverUtil {

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public static JSPackage getDependencyJSPackage(
		Bundle bundle, String packageName) {

		NPMResolver npmResolver = _getNPMResolver(bundle);

		return npmResolver.getDependencyJSPackage(packageName);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public static JSPackage getDependencyJSPackage(
		Class<?> clazz, String packageName) {

		return getDependencyJSPackage(
			FrameworkUtil.getBundle(clazz), packageName);
	}

	public static NPMResolver getNPMResolver(Bundle bundle) {
		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<NPMResolver> serviceReference =
			bundleContext.getServiceReference(NPMResolver.class);

		if (serviceReference == null) {
			throw new IllegalArgumentException(
				"Bundle " + bundle.getSymbolicName() +
					" does not have an associated NPMResolver");
		}

		NPMResolver npmResolver = bundleContext.getService(serviceReference);

		if (npmResolver == null) {
			throw new IllegalArgumentException(
				"Bundle " + bundle.getSymbolicName() +
					" does not have an associated NPMResolver");
		}

		return npmResolver;
	}

	public static NPMResolver getNPMResolver(Class<?> clazz) {
		return getNPMResolver(FrameworkUtil.getBundle(clazz));
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public static String resolveModuleName(Bundle bundle, String moduleName) {
		NPMResolver npmResolver = _getNPMResolver(bundle);

		return npmResolver.resolveModuleName(moduleName);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public static String resolveModuleName(Class<?> clazz, String moduleName) {
		return resolveModuleName(FrameworkUtil.getBundle(clazz), moduleName);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public JSPackage getJSPackage(Bundle bundle) {
		NPMResolver npmResolver = _getNPMResolver(bundle);

		return npmResolver.getJSPackage();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public JSPackage getJSPackage(Class<?> clazz) {
		return getJSPackage(FrameworkUtil.getBundle(clazz));
	}

	private static NPMResolver _getNPMResolver(Bundle bundle) {
		throw new IllegalArgumentException(
			"Bundle " + bundle.getSymbolicName() +
				" does not have an associated NPMResolver");
	}

}