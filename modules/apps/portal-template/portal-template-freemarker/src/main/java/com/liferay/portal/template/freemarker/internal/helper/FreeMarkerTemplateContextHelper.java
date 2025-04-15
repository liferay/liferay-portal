/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal.helper;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.TemplatePortletPreferences;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;
import com.liferay.portal.template.freemarker.internal.LiferayObjectConstructor;

import freemarker.ext.beans.BeansWrapper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Mika Koivisto
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	service = TemplateContextHelper.class
)
public class FreeMarkerTemplateContextHelper extends TemplateContextHelper {

	@Override
	public Set<String> getRestrictedVariables() {
		return _restrictedVariables;
	}

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		super.prepare(contextObjects, httpServletRequest);

		// Theme display

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			Theme theme = themeDisplay.getTheme();

			// Full css and templates path

			String servletContextName = GetterUtil.getString(
				theme.getServletContextName());

			contextObjects.put(
				"fullCssPath",
				StringBundler.concat(
					StringPool.SLASH, servletContextName,
					theme.getFreeMarkerTemplateLoader(), theme.getCssPath()));

			String fullTemplatesPath = StringBundler.concat(
				StringPool.SLASH, servletContextName,
				theme.getFreeMarkerTemplateLoader(), theme.getTemplatesPath());

			contextObjects.put("fullTemplatesPath", fullTemplatesPath);

			// Init

			contextObjects.put("init", fullTemplatesPath + "/init.ftl");

			// Navigation items

			if (_freeMarkerEngineConfiguration.includeNavItemsInTheContext() &&
				(themeDisplay.getLayout() != null)) {

				try {
					contextObjects.put("navItems", themeDisplay.getNavItems());
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}

		// Insert custom ftl variables

		Map<String, Object> ftlVariables =
			(Map<String, Object>)httpServletRequest.getAttribute(
				WebKeys.FTL_VARIABLES);

		if (ftlVariables != null) {
			for (Map.Entry<String, Object> entry : ftlVariables.entrySet()) {
				String key = entry.getKey();

				if (Validator.isNotNull(key)) {
					contextObjects.put(key, entry.getValue());
				}
			}
		}

		// Custom template context contributors

		for (TemplateContextContributor templateContextContributor :
				getTemplateContextContributors()) {

			templateContextContributor.prepare(
				contextObjects, httpServletRequest);
		}
	}

	public void setDefaultBeansWrapper(BeansWrapper defaultBeansWrapper) {
		_defaultBeansWrapper = defaultBeansWrapper;
	}

	public void setRestrictedBeansWrapper(BeansWrapper restrictedBeansWrapper) {
		_restrictedBeansWrapper = restrictedBeansWrapper;
	}

	@Activate
	@Modified
	protected void activate(
		Map<String, Object> properties, BundleContext bundleContext) {

		init(bundleContext);

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, properties);

		_restrictedVariables = SetUtil.fromArray(
			_freeMarkerEngineConfiguration.restrictedVariables());
	}

	@Deactivate
	protected void deactivate() {
		destory();
	}

	@Override
	protected void populateExtraHelperUtilities(
		Map<String, Object> helperUtilities, boolean restricted) {

		BeansWrapper beansWrapper = _defaultBeansWrapper;

		if (restricted) {
			beansWrapper = _restrictedBeansWrapper;
		}

		// Enum util

		helperUtilities.put("enumUtil", beansWrapper.getEnumModels());

		// Object util

		helperUtilities.put(
			"objectUtil", new LiferayObjectConstructor(beansWrapper));

		// Portlet preferences

		helperUtilities.put(
			"freeMarkerPortletPreferences", new TemplatePortletPreferences());

		// Static class util

		helperUtilities.put("staticUtil", beansWrapper.getStaticModels());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FreeMarkerTemplateContextHelper.class);

	private BeansWrapper _defaultBeansWrapper;
	private volatile FreeMarkerEngineConfiguration
		_freeMarkerEngineConfiguration;
	private BeansWrapper _restrictedBeansWrapper;
	private volatile Set<String> _restrictedVariables;

}