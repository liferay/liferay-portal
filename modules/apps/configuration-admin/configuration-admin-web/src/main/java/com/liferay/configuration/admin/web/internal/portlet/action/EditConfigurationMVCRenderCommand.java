/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.menu.ConfigurationMenuItem;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationModelConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.helper.DDMFormRendererHelper;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationFormRendererRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.resource.manager.ClassLoaderResourceManager;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/configuration_admin/edit_configuration",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = MVCRenderCommand.class
)
public class EditConfigurationMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String factoryPid = ParamUtil.getString(renderRequest, "factoryPid");

		String pid = ParamUtil.getString(renderRequest, "pid", factoryPid);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(renderRequest);

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		ConfigurationModel configurationModel = configurationModels.get(pid);

		if ((configurationModel == null) && Validator.isNotNull(factoryPid)) {
			configurationModel = configurationModels.get(factoryPid);
		}

		if (configurationModel != null) {
			Configuration configuration =
				_configurationModelRetriever.getConfiguration(
					pid, configurationScopeDisplayContext.getScope(),
					configurationScopeDisplayContext.getScopePK());

			if (configurationModel.isFactory() && pid.equals(factoryPid)) {
				configuration = null;
			}

			configurationModel = new ConfigurationModel(
				configuration, configurationModel);

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY,
				_configurationEntryRetriever.
					getConfigurationCategoryMenuDisplay(
						configurationModel.getCategory(),
						themeDisplay.getLanguageId(),
						configurationScopeDisplayContext.getScope(),
						configurationScopeDisplayContext.getScopePK()));

			ConfigurationEntry configurationEntry =
				new ConfigurationModelConfigurationEntry(
					configurationModel, _portal.getLocale(renderRequest));

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY,
				configurationEntry);

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_FORM_RENDERER,
				_configurationFormRendererRetriever.
					getConfigurationFormRenderer(
						configurationModel.getBaseID()));

			List<ConfigurationMenuItem> configurationMenuItems =
				_serviceTrackerMap.getService(configurationModel.getBaseID());

			if (configurationMenuItems != null) {
				renderRequest.setAttribute(
					ConfigurationAdminWebKeys.CONFIGURATION_MENU_ITEMS,
					configurationMenuItems);
			}

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_MODEL,
				configurationModel);

			LocationVariableResolver locationVariableResolver =
				new LocationVariableResolver(
					new ClassLoaderResourceManager(
						configurationModel.getClassLoader()),
					_settingsLocatorHelper);

			DDMFormRendererHelper ddmFormRendererHelper =
				new DDMFormRendererHelper(
					renderRequest, renderResponse, configurationModel,
					_ddmFormRenderer, locationVariableResolver);

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_MODEL_FORM_HTML,
				ddmFormRendererHelper.getDDMFormHTML());

			return "/edit_configuration.jsp";
		}

		SessionErrors.add(renderRequest, "entryInvalid");

		return "/error.jsp";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, ConfigurationMenuItem.class, "configuration.pid");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference
	private ConfigurationFormRendererRetriever
		_configurationFormRendererRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, List<ConfigurationMenuItem>>
		_serviceTrackerMap;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}