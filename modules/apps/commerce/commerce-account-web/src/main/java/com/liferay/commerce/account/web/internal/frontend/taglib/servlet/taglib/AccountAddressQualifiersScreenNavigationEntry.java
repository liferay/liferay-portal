/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.account.web.internal.display.context.AccountAddressQualifiersDisplayContext;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class AccountAddressQualifiersScreenNavigationEntry
	extends AccountAddressQualifiersScreenNavigationCategory
	implements ScreenNavigationEntry<Address> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, Address address) {
		if ((address != null) && !address.isNew()) {
			return true;
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		AccountAddressQualifiersDisplayContext
			accountAddressQualifiersDisplayContext =
				new AccountAddressQualifiersDisplayContext(
					_addressLocalService, _commerceChannelRelService,
					httpServletRequest);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			accountAddressQualifiersDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/account_entry/addresses/qualifiers.jsp");
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.account.web)"
	)
	private ServletContext _servletContext;

}