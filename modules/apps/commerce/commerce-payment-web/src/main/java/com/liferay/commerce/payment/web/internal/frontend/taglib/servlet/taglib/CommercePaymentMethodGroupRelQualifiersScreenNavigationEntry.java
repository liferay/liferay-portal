/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.web.internal.display.context.CommercePaymentMethodGroupRelQualifiersDisplayContext;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommercePaymentMethodGroupRelQualifiersScreenNavigationEntry
	extends CommercePaymentMethodGroupRelQualifiersScreenNavigationCategory
	implements ScreenNavigationEntry<CommercePaymentMethodGroupRel> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(
		User user,
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		if (commercePaymentMethodGroupRel == null) {
			return false;
		}

		boolean hasPermission = false;

		try {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByGroupId(
					commercePaymentMethodGroupRel.getGroupId());

			hasPermission = _commerceChannelModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), commerceChannel,
				ActionKeys.UPDATE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return hasPermission;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CommercePaymentMethodGroupRelQualifiersDisplayContext
			commercePaymentMethodGroupRelQualifiersDisplayContext =
				new CommercePaymentMethodGroupRelQualifiersDisplayContext(
					_commerceChannelModelResourcePermission,
					_commerceChannelLocalService,
					_commercePaymentMethodGroupRelService,
					_commercePaymentMethodGroupRelQualifierService,
					_commercePaymentMethodRegistry,
					_commercePaymentIntegrationRegistry, _countryService,
					httpServletRequest);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commercePaymentMethodGroupRelQualifiersDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/commerce_payment_method/qualifiers.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentMethodGroupRelQualifiersScreenNavigationEntry.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelQualifierService
		_commercePaymentMethodGroupRelQualifierService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CountryService _countryService;

	@Reference
	private JSPRenderer _jspRenderer;

}