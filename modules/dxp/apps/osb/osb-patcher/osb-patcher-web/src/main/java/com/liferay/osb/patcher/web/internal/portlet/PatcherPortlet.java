/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.exception.NoSuchPatcherAccountException;
import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.exception.NoSuchPatcherBuildRelException;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixComponentException;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixException;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException;
import com.liferay.osb.patcher.exception.NoSuchPatcherProductVersionException;
import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.exception.NoSuchPatcherTicketHintException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=osb-patcher-portlet",
		"com.liferay.portlet.display-category=category.osb",
		"com.liferay.portlet.header-portlet-javascript=/js/main.js",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=OSB Patcher",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/osb_patcher/views/accounts/index.jsp",
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class PatcherPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, NoSuchPatcherAccountException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchPatcherBuildException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				NoSuchPatcherBuildRelException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				NoSuchPatcherFixComponentException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchPatcherFixException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchPatcherFixPackException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchPatcherFixRelException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				NoSuchPatcherProductVersionException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				NoSuchPatcherProjectVersionException.class.getName()) ||
			SessionErrors.contains(
				renderRequest,
				NoSuchPatcherTicketHintException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PortalException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include(
				"/osb_patcher/views/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof PortalException) {
			return true;
		}

		return false;
	}

}