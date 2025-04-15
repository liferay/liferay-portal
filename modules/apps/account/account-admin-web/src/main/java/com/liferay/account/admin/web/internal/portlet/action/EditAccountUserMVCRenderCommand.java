/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.display.context.EditAccountEntryAccountUserDisplayContext;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountUserPermission;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_user"
	},
	service = MVCRenderCommand.class
)
public class EditAccountUserMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			User accountUser = _userLocalService.fetchUser(
				ParamUtil.getLong(renderRequest, "accountUserId"));
			AccountEntry accountEntry =
				_accountEntryLocalService.fetchAccountEntry(
					ParamUtil.getLong(renderRequest, "accountEntryId"));

			AccountUserPermission.checkEditUserPermission(
				_permissionCheckerFactory.create(
					_portal.getUser(renderRequest)),
				AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT, accountEntry,
				accountUser);

			EditAccountEntryAccountUserDisplayContext portletDisplayContext =
				_createDisplayContext(
					accountEntry, accountUser, renderRequest, renderResponse);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, portletDisplayContext);

			_populatePortletDisplay(
				portletDisplayContext, renderRequest, renderResponse);
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(
				renderRequest, PrincipalException.class, principalException);

			return "/account_entries_admin/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return "/account_entries_admin/account_users/edit_account_user.jsp";
	}

	private EditAccountEntryAccountUserDisplayContext _createDisplayContext(
			AccountEntry accountEntry, User accountUser,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		EditAccountEntryAccountUserDisplayContext
			editAccountEntryAccountUserDisplayContext =
				new EditAccountEntryAccountUserDisplayContext();

		Contact contact = accountUser.getContact();

		editAccountEntryAccountUserDisplayContext.setAccountEntryId(
			accountEntry.getAccountEntryId());
		editAccountEntryAccountUserDisplayContext.setAccountUserId(
			accountUser.getUserId());
		editAccountEntryAccountUserDisplayContext.setBackURL(
			ParamUtil.getString(renderRequest, "backURL"));
		editAccountEntryAccountUserDisplayContext.setEditAccountUserActionURL(
			PortletURLBuilder.createActionURL(
				renderResponse
			).setActionName(
				"/account_admin/edit_account_user"
			).buildString());
		editAccountEntryAccountUserDisplayContext.setSelectedAccountUser(
			accountUser);
		editAccountEntryAccountUserDisplayContext.setSelectedAccountUserContact(
			contact);
		editAccountEntryAccountUserDisplayContext.setTitle(
			_language.format(
				_portal.getHttpServletRequest(renderRequest), "edit-user-x",
				contact.getFullName(), false));

		return editAccountEntryAccountUserDisplayContext;
	}

	private void _populatePortletDisplay(
		EditAccountEntryAccountUserDisplayContext
			editAccountEntryAccountUserDisplayContext,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			editAccountEntryAccountUserDisplayContext.getBackURL());

		renderResponse.setTitle(
			editAccountEntryAccountUserDisplayContext.getTitle());
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}