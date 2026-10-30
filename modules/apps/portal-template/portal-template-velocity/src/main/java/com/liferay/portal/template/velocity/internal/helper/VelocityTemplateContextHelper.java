/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.velocity.internal.helper;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil_IW;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.TemplatePortletPreferences;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.IteratorTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.SortTool;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Peter Fellwock
 */
@Component(
	configurationPid = "com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration",
	service = TemplateContextHelper.class
)
public class VelocityTemplateContextHelper extends TemplateContextHelper {

	@Override
	public Set<String> getRestrictedVariables() {
		return SetUtil.fromArray(
			_velocityEngineConfiguration.restrictedVariables());
	}

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		super.prepare(contextObjects, httpServletRequest);

		Map<String, Function<HttpServletRequest, Object>> lazyContextFunctions =
			new AbstractMap<>() {

				@Override
				public Set<Entry<String, Function<HttpServletRequest, Object>>>
					entrySet() {

					return Collections.emptySet();
				}

				@Override
				public Function<HttpServletRequest, Object> put(
					String key, Function<HttpServletRequest, Object> function) {

					Object value = function.apply(httpServletRequest);

					if (value != null) {
						contextObjects.put(key, value);
					}

					return null;
				}

			};

		// Theme display

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		// Full css and templates path

		lazyContextFunctions.put(
			"fullCssPath",
			curHttpServletRequest -> _getThemePath(
				curHttpServletRequest, Theme::getCssPath));
		lazyContextFunctions.put(
			"fullTemplatesPath",
			curHttpServletRequest -> _getThemePath(
				curHttpServletRequest, Theme::getTemplatesPath));

		// Init

		lazyContextFunctions.put("init", this::_getInit);

		// Navigation items

		lazyContextFunctions.put("navItems", this::_getNavItems);

		// Insert custom vm variables

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		Map<String, Object> vmVariables =
			(Map<String, Object>)httpServletRequest.getAttribute(
				WebKeys.VM_VARIABLES + portletDisplay.getId());

		if (vmVariables != null) {
			for (Map.Entry<String, Object> entry : vmVariables.entrySet()) {
				String key = entry.getKey();

				if (Validator.isNull(key)) {
					continue;
				}

				contextObjects.put(key, entry.getValue());
			}
		}

		// Custom template context contributors

		for (TemplateContextContributor templateContextContributor :
				getTemplateContextContributors()) {

			templateContextContributor.prepare(
				contextObjects, httpServletRequest);
		}
	}

	@Activate
	protected void activate(
		Map<String, Object> properties, BundleContext bundleContext) {

		init(bundleContext);

		modified(properties);
	}

	@Deactivate
	protected void deactivate() {
		destory();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_velocityEngineConfiguration = ConfigurableUtil.createConfigurable(
			VelocityEngineConfiguration.class, properties);
	}

	@Override
	protected void populateExtraHelperUtilities(
		Map<String, Object> velocityContext, boolean restrict) {

		// Date tool

		velocityContext.put("dateTool", new DateTool());

		// Escape tool

		velocityContext.put("escapeTool", new EscapeTool());

		// Iterator tool

		velocityContext.put("iteratorTool", new IteratorTool());

		// List tool

		velocityContext.put("listTool", new ListTool());

		// Math tool

		velocityContext.put("mathTool", new MathTool());

		// Number tool

		velocityContext.put("numberTool", new NumberTool());

		// Portlet preferences

		velocityContext.put(
			"velocityPortletPreferences", new TemplatePortletPreferences());

		// Sort tool

		velocityContext.put("sortTool", new SortTool());

		// Permissions

		try {
			velocityContext.put(
				"rolePermission", RolePermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}
	}

	private Object _getInit(HttpServletRequest httpServletRequest) {
		String fullTemplatesPath = _getThemePath(
			httpServletRequest, Theme::getTemplatesPath);

		if (fullTemplatesPath == null) {
			return null;
		}

		return fullTemplatesPath + "/init.vm";
	}

	private Object _getNavItems(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) ||
			!_velocityEngineConfiguration.includeNavItemsInTheContext() ||
			(themeDisplay.getLayout() == null)) {

			return null;
		}

		try {
			return themeDisplay.getNavItems();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private String _getThemePath(
		HttpServletRequest httpServletRequest,
		Function<Theme, String> function) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return null;
		}

		Theme theme = themeDisplay.getTheme();

		return GetterUtil.getString(theme.getServletContextName()) +
			theme.getVelocityResourceListener() + function.apply(theme);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VelocityTemplateContextHelper.class);

	private static volatile VelocityEngineConfiguration
		_velocityEngineConfiguration;

}