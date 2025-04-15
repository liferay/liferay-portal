/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.deploy.hot.BaseHotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.servlet.FileTimestampUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResourceLoaderUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Ivica Cardic
 */
public class ThemeHotDeployListener extends BaseHotDeployListener {

	@Override
	public void invokeDeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeDeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error registering themes for ", throwable);
		}
	}

	@Override
	public void invokeUndeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeUndeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error unregistering themes for ", throwable);
		}
	}

	protected void doInvokeDeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking deploy for " + servletContextName);
		}

		String[] xmls = {
			StreamUtil.toString(
				servletContext.getResourceAsStream(
					"/WEB-INF/liferay-look-and-feel.xml"))
		};

		if (xmls[0] == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Registering themes for " + servletContextName);
		}

		FileTimestampUtil.reset(servletContext);

		List<Theme> themes = ThemeLocalServiceUtil.init(
			servletContextName, servletContext, null, true, xmls,
			hotDeployEvent.getPluginPackage());

		_themes.put(servletContextName, themes);

		servletContext.setAttribute(WebKeys.PLUGIN_THEMES, themes);

		if (_log.isInfoEnabled()) {
			if (themes.size() == 1) {
				_log.info(
					"1 theme for " + servletContextName +
						" is available for use");
			}
			else {
				_log.info(
					StringBundler.concat(
						themes.size(), " themes for ", servletContextName,
						" are available for use"));
			}
		}
	}

	protected void doInvokeUndeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking undeploy for " + servletContextName);
		}

		List<Theme> themes = _themes.remove(servletContextName);

		if (themes != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unregistering themes for " + servletContextName);
			}

			try {
				ThemeLocalServiceUtil.uninstallThemes(themes);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
		else {
			return;
		}

		// LEP-2057

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			TemplateResourceLoaderUtil.clearCache(
				TemplateConstants.LANG_TYPE_FTL);
			TemplateResourceLoaderUtil.clearCache(
				TemplateConstants.LANG_TYPE_VM);
		}

		if (_log.isInfoEnabled()) {
			if (themes.size() == 1) {
				_log.info(
					"1 theme for " + servletContextName + " was unregistered");
			}
			else {
				_log.info(
					StringBundler.concat(
						themes.size(), " themes for ", servletContextName,
						" were unregistered"));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ThemeHotDeployListener.class);

	private static final Map<String, List<Theme>> _themes = new HashMap<>();

}