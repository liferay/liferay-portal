/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.configuration.DDMGroupServiceConfiguration;
import com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration;
import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.util.DDMTemplateHelper;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.web.internal.display.context.InformationTemplatesEditDDMTemplateDisplayContext;
import com.liferay.template.web.internal.display.context.WidgetTemplatesEditDDMTemplateDisplayContext;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration",
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/edit_ddm_template"
	},
	service = MVCRenderCommand.class
)
public class EditDDMTemplateMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		renderRequest.setAttribute(
			DDMGroupServiceConfiguration.class.getName(),
			_getDDMGroupServiceConfiguration(themeDisplay.getScopeGroupId()));

		renderRequest.setAttribute(
			DDMTemplateHelper.class.getName(), _ddmTemplateHelper);

		String tabs1 = ParamUtil.getString(
			renderRequest, "tabs1", "information-templates");

		if (Objects.equals(tabs1, "information-templates")) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new InformationTemplatesEditDDMTemplateDisplayContext(
					_infoItemServiceRegistry,
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse)));
		}
		else if (Objects.equals(tabs1, "widget-templates")) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new WidgetTemplatesEditDDMTemplateDisplayContext(
					_ddmWebConfiguration,
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse)));
		}

		return "/edit_ddm_template.jsp";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddmWebConfiguration = ConfigurableUtil.createConfigurable(
			DDMWebConfiguration.class, properties);
	}

	private DDMGroupServiceConfiguration _getDDMGroupServiceConfiguration(
		long groupId) {

		try {
			return _configurationProvider.getConfiguration(
				DDMGroupServiceConfiguration.class,
				new GroupServiceSettingsLocator(
					groupId, DDMConstants.SERVICE_NAME));
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DDMTemplateHelper _ddmTemplateHelper;

	private volatile DDMWebConfiguration _ddmWebConfiguration;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Portal _portal;

}