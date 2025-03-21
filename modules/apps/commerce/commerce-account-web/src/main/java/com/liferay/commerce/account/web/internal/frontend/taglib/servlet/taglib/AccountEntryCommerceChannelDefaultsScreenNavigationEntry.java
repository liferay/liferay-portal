/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.account.web.internal.display.context.CommerceAccountDisplayContext;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.constants.CommerceAccountWebKeys;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class AccountEntryCommerceChannelDefaultsScreenNavigationEntry
	extends AccountEntryCommerceChannelDefaultsScreenNavigationCategory
	implements ScreenNavigationEntry<AccountEntry> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, AccountEntry accountEntry) {
		if (accountEntry.isNew() ||
			!Objects.equals(
				accountEntry.getType(),
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS)) {

			return false;
		}

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try {
			if (_accountEntryModelResourcePermission.contains(
					permissionChecker, accountEntry.getAccountEntryId(),
					CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
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
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/account_entry/commerce_channel_defaults.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryCommerceChannelDefaultsScreenNavigationEntry.class);

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
	private JSPRenderer _jspRenderer;

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