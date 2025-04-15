/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet;

import com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.Portlet;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Marcellus Tavares
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.autopropagated-parameters=showAncestorScopes",
		"com.liferay.portlet.autopropagated-parameters=showManageTemplates",
		"com.liferay.portlet.css-class-wrapper=portlet-dynamic-data-mapping",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/portlet_display_template.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Widget Templates",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.refererWebDAVToken=application_display_template",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view_template.jsp",
		"jakarta.portlet.name=" + PortletKeys.PORTLET_DISPLAY_TEMPLATE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class PortletDisplayTemplatePortlet extends DDMPortlet {

	@Activate
	@Modified
	@Override
	protected void activate(Map<String, Object> properties) {
		ddmWebConfiguration = ConfigurableUtil.createConfigurable(
			DDMWebConfiguration.class, properties);
	}

}