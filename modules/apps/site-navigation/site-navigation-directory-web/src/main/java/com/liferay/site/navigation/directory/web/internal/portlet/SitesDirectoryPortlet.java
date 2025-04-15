/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.directory.web.internal.portlet;

import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.site.navigation.directory.web.internal.constants.SitesDirectoryPortletKeys;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true;",
		"com.liferay.portlet.css-class-wrapper=portlet-sites-directory",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.icon=/icons/navigation.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.single-page-application=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Sites Directory",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SitesDirectoryPortletKeys.SITES_DIRECTORY,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SitesDirectoryPortlet extends MVCPortlet {

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.site.navigation.directory.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}