/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet;

import com.liferay.account.admin.web.internal.constants.AccountWebKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.settings.AccountEntryGroupSettings;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.accounts",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Account Management",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/account_entries_admin/view.jsp",
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AccountEntriesManagementPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			renderRequest.setAttribute(
				AccountWebKeys.ACCOUNT_ENTRY_ALLOWED_TYPES,
				ArrayUtil.append(
					_accountEntryGroupSettings.getAllowedTypes(
						_portal.getScopeGroupId(renderRequest)),
					AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER));

			super.doDispatch(renderRequest, renderResponse);
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference
	private AccountEntryGroupSettings _accountEntryGroupSettings;

	@Reference
	private Portal _portal;

}