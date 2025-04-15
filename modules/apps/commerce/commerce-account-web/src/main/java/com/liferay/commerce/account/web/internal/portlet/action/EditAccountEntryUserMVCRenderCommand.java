/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.account.web.internal.display.context.CommerceAccountDisplayContext;
import com.liferay.commerce.constants.CommerceAccountWebKeys;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_entries_admin/edit_account_entry_user"
	},
	service = MVCRenderCommand.class
)
public class EditAccountEntryUserMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/account_entry/users/select_default_user.jsp");

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);

			CommerceAccountDisplayContext commerceAccountDisplayContext =
				new CommerceAccountDisplayContext(
					_accountEntryModelResourcePermission, _accountEntryService,
					_commerceChannelAccountEntryRelService,
					_commerceChannelService,
					_commerceShippingFixedOptionService,
					_commerceShippingMethodService,
					_commerceShippingOptionAccountEntryRelService,
					httpServletRequest, _language, _portal, _userService);

			httpServletRequest.setAttribute(
				CommerceAccountWebKeys.COMMERCE_ACCOUNT_DISPLAY_CONTEXT,
				commerceAccountDisplayContext);

			requestDispatcher.include(
				httpServletRequest,
				_portal.getHttpServletResponse(renderResponse));
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private CommerceShippingOptionAccountEntryRelService
		_commerceShippingOptionAccountEntryRelService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.account.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserService _userService;

}