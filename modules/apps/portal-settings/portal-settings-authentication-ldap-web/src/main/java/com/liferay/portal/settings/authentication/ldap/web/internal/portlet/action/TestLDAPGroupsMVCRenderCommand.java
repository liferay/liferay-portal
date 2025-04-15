/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Philip Jones
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/portal_settings_authentication_ldap/test_ldap_groups"
	},
	service = MVCRenderCommand.class
)
public class TestLDAPGroupsMVCRenderCommand
	extends BasePortalSettingsMVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			AuthTokenUtil.checkCSRFToken(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(renderRequest)),
				getClass().getName());

			return super.render(renderRequest, renderResponse);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to test LDAP connection: " +
						principalException.getMessage(),
					principalException);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to test LDAP connection: " +
						principalException.getMessage());
			}
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	@Override
	protected String getJspPath() {
		return _JSP_PATH;
	}

	private static final String _JSP_PATH =
		"/com.liferay.portal.settings.web/test_ldap_groups.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		TestLDAPGroupsMVCRenderCommand.class);

	@Reference
	private Portal _portal;

}