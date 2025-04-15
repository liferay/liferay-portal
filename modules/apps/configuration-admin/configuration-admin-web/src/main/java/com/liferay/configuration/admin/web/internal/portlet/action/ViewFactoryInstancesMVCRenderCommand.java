/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationModelConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelIterator;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
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
		"mvc.command.name=/configuration_admin/view_factory_instances",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = MVCRenderCommand.class
)
public class ViewFactoryInstancesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String factoryPid = ParamUtil.getString(renderRequest, "factoryPid");

		MVCRenderCommand customRenderCommand = _serviceTrackerMap.getService(
			factoryPid);

		if (customRenderCommand != null) {
			return customRenderCommand.render(renderRequest, renderResponse);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(renderRequest);

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		try {
			ConfigurationModel factoryConfigurationModel =
				configurationModels.get(factoryPid);

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY,
				_configurationEntryRetriever.
					getConfigurationCategoryMenuDisplay(
						factoryConfigurationModel.getCategory(),
						themeDisplay.getLanguageId(),
						configurationScopeDisplayContext.getScope(),
						configurationScopeDisplayContext.getScopePK()));

			List<ConfigurationModel> factoryInstances =
				_configurationModelRetriever.getFactoryInstances(
					factoryConfigurationModel,
					configurationScopeDisplayContext.getScope(),
					configurationScopeDisplayContext.getScopePK());

			ConfigurationEntry configurationEntry =
				new ConfigurationModelConfigurationEntry(
					factoryConfigurationModel,
					_portal.getLocale(renderRequest));

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY,
				configurationEntry);

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_MODEL_ITERATOR,
				new ConfigurationModelIterator(factoryInstances));
			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.FACTORY_CONFIGURATION_MODEL,
				factoryConfigurationModel);

			return "/view_factory_instances.jsp";
		}
		catch (IOException ioException) {
			throw new PortletException(ioException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MVCRenderCommand.class,
			StringBundler.concat(
				"(&(jakarta.portlet.name=",
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				")(mvc.command.name=/configuration_admin",
				"/view_factory_instances)(configurationPid=*))"),
			(serviceReference, emitter) -> emitter.emit(
				(String)serviceReference.getProperty("configurationPid")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, MVCRenderCommand> _serviceTrackerMap;

}