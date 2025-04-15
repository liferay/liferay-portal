/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet;

import com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.template.constants.TemplatePortletKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.autopropagated-parameters=refererWebDAVToken",
		"com.liferay.portlet.css-class-wrapper=portlet-template",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Templates",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.refererWebDAVToken=template",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class TemplatePortlet extends MVCPortlet {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddmWebConfiguration = ConfigurableUtil.createConfigurable(
			DDMWebConfiguration.class, properties);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			DDMWebConfiguration.class.getName(), _ddmWebConfiguration);
		renderRequest.setAttribute(
			InfoItemServiceRegistry.class.getName(), _infoItemServiceRegistry);
		renderRequest.setAttribute(
			PortletDisplayTemplate.class.getName(), _portletDisplayTemplate);

		super.doDispatch(renderRequest, renderResponse);
	}

	private volatile DDMWebConfiguration _ddmWebConfiguration;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private PortletDisplayTemplate _portletDisplayTemplate;

}