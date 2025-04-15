/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.audit.AuditEvent;
import com.liferay.portal.security.audit.web.internal.AuditEventManagerUtil;
import com.liferay.portal.security.audit.web.internal.constants.AuditPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Greenwald
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/audit.png",
		"com.liferay.portlet.instanceable=false",
		"jakarta.portlet.display-name=Audit",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AuditPortletKeys.AUDIT,
		"jakarta.portlet.portlet-info.short-title=Audit",
		"jakarta.portlet.portlet-info.title=Audit",
		"jakarta.portlet.portlet-mode=text/html;view",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AuditPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		_checkCompanyAdmin(actionRequest);

		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_checkCompanyAdmin(renderRequest);

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		_checkCompanyAdmin(resourceRequest);

		super.serveResource(resourceRequest, resourceResponse);
	}

	private void _checkCompanyAdmin(PortletRequest portletRequest)
		throws PortletException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		long auditEventId = ParamUtil.getLong(portletRequest, "auditEventId");

		if (auditEventId > 0) {
			AuditEvent auditEvent = AuditEventManagerUtil.fetchAuditEvent(
				auditEventId);

			if ((auditEvent != null) &&
				(permissionChecker.getCompanyId() !=
					auditEvent.getCompanyId())) {

				PrincipalException principalException =
					new PrincipalException.MustBeCompanyAdmin(
						permissionChecker.getUserId());

				throw new PortletException(principalException);
			}
		}

		if (!permissionChecker.isCompanyAdmin()) {
			PrincipalException principalException =
				new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}
	}

}