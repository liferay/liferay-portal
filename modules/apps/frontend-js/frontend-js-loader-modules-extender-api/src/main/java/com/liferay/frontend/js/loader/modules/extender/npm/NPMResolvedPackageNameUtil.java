/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.npm;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * @author Iván Zaera Avellón
 */
public class NPMResolvedPackageNameUtil {

	/**
	 * Get the NPM resolved package name associated to the current portlet.
	 *
	 * The current portlet is inferred from the portletResource parameter or
	 * the {@link ServletContext} associated to the given request.
	 *
	 * @param  httpServletRequest
	 * @throws UnsupportedOperationException if the associated bundle does not contain AMD modules
	 * @review
	 */
	public static String get(HttpServletRequest httpServletRequest) {
		ServletContext servletContext = httpServletRequest.getServletContext();

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNotNull(portletResource)) {
			PortletBag portletBag = PortletBagPool.get(
				PortletIdCodec.decodePortletName(portletResource));

			if (portletBag != null) {
				servletContext = portletBag.getServletContext();
			}
		}

		return get(servletContext);
	}

	/**
	 * Get the NPM resolved package name associated to the bundle containing the
	 * given servlet context.
	 *
	 * @param  servletContext
	 * @throws UnsupportedOperationException if the associated bundle does not contain AMD modules
	 * @review
	 */
	public static String get(ServletContext servletContext) {
		Object object = servletContext.getAttribute(
			NPMResolvedPackageNameUtil.class.getName());

		if (object instanceof String) {
			return (String)object;
		}

		if (object == _NULL_HOLDER) {
			return null;
		}

		String npmResolvedPackageName = _getNPMResolvedPackageName(
			ServletContextClassLoaderPool.getClassLoader(
				servletContext.getServletContextName()));

		if (npmResolvedPackageName == null) {
			servletContext.setAttribute(
				NPMResolvedPackageNameUtil.class.getName(), _NULL_HOLDER);
		}
		else {
			servletContext.setAttribute(
				NPMResolvedPackageNameUtil.class.getName(),
				npmResolvedPackageName);
		}

		return npmResolvedPackageName;
	}

	public static void set(
		ServletContext servletContext, String npmResolvedPackageName) {

		if (npmResolvedPackageName != null) {
			servletContext.setAttribute(
				NPMResolvedPackageNameUtil.class.getName(),
				npmResolvedPackageName);
		}
		else {
			servletContext.removeAttribute(
				NPMResolvedPackageNameUtil.class.getName());
		}
	}

	private static String _getNPMResolvedPackageName(
		Bundle bundle, NPMResolver npmResolver) {

		URL url = bundle.getResource("META-INF/resources/package.json");

		if (url == null) {
			return null;
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				URLUtil.toString(url));

			String name = jsonObject.getString("name");

			return npmResolver.resolveModuleName(name);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to resolve NPM package " + bundle.getSymbolicName(),
				exception);
		}

		return null;
	}

	private static String _getNPMResolvedPackageName(ClassLoader classLoader) {
		if (!(classLoader instanceof BundleReference)) {
			return null;
		}

		BundleReference bundleReference = (BundleReference)classLoader;

		Bundle bundle = bundleReference.getBundle();

		return _getNPMResolvedPackageName(
			bundle, NPMResolverUtil.getNPMResolver(bundle));
	}

	private static final Object _NULL_HOLDER = new Object();

	private static final Log _log = LogFactoryUtil.getLog(
		NPMResolvedPackageNameUtil.class);

}