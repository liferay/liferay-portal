/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.display.context;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CountryService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Riccardo Alberti
 */
public class CommercePaymentMethodGroupRelQualifiersDisplayContext
	extends CommercePaymentMethodGroupRelsDisplayContext {

	public CommercePaymentMethodGroupRelQualifiersDisplayContext(
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceChannelLocalService commerceChannelLocalService,
		CommercePaymentMethodGroupRelService
			commercePaymentMethodGroupRelService,
		CommercePaymentMethodGroupRelQualifierService
			commercePaymentMethodGroupRelQualifierService,
		CommercePaymentMethodRegistry commercePaymentMethodRegistry,
		CommercePaymentIntegrationRegistry commercePaymentIntegrationRegistry,
		CountryService countryService, HttpServletRequest httpServletRequest) {

		super(
			commerceChannelLocalService, commercePaymentMethodGroupRelService,
			commercePaymentMethodRegistry, commercePaymentIntegrationRegistry,
			httpServletRequest);

		_commerceChannelModelResourcePermission =
			commerceChannelModelResourcePermission;
		_commercePaymentMethodGroupRelQualifierService =
			commercePaymentMethodGroupRelQualifierService;
	}

	public String getActiveOrderTypeEligibility() throws PortalException {
		long commerceOrderTypeCommercePaymentMethodGroupRelsCount =
			_commercePaymentMethodGroupRelQualifierService.
				getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
					getCommercePaymentMethodGroupRelId(), null);

		if (commerceOrderTypeCommercePaymentMethodGroupRelsCount > 0) {
			return "orderTypes";
		}

		return "all";
	}

	public String getActiveTermEntryEligibility() throws PortalException {
		long commerceTermEntryCommercePaymentMethodGroupRelsCount =
			_commercePaymentMethodGroupRelQualifierService.
				getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
					getCommercePaymentMethodGroupRelId(), null);

		if (commerceTermEntryCommercePaymentMethodGroupRelsCount > 0) {
			return "termEntries";
		}

		return "none";
	}

	public String getCommerceOrderTypeCommercePaymentMethodGroupRelsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-channel/v1.0/payment-method-group-rels",
			"/", getCommercePaymentMethodGroupRelId(),
			"/payment-method-group-rel-order-types?nestedFields=orderType");
	}

	public List<FDSActionDropdownItem>
			getCommerceOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return _getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					commercePaymentRequestHelper.getRequest(),
					CommerceOrderType.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_order_type/edit_commerce_order_type"
			).setRedirect(
				commercePaymentRequestHelper.getCurrentURL()
			).setParameter(
				"commerceOrderTypeId", "{orderType.id}"
			).buildString(),
			false);
	}

	public String getCommerceTermEntriesCommercePaymentMethodGroupRelsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-channel/v1.0/payment-method-group-rels",
			"/", getCommercePaymentMethodGroupRelId(),
			"/payment-method-group-rel-terms?nestedFields=term");
	}

	public List<FDSActionDropdownItem>
			getCommerceTermEntryFDSActionDropdownItems()
		throws PortalException {

		return _getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					commercePaymentRequestHelper.getRequest(),
					CommerceTermEntry.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_term_entry/edit_commerce_term_entry"
			).setRedirect(
				commercePaymentRequestHelper.getCurrentURL()
			).setParameter(
				"commerceTermEntryId", "{term.id}"
			).buildString(),
			false);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _commerceChannelModelResourcePermission.contains(
			commercePaymentRequestHelper.getPermissionChecker(),
			getCommerceChannelId(), actionId);
	}

	private List<FDSActionDropdownItem> _getFDSActionTemplates(
		String portletURL, boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			portletURL, "pencil", "edit",
			LanguageUtil.get(commercePaymentRequestHelper.getRequest(), "edit"),
			"get", null, null);

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "trash", "remove",
				LanguageUtil.get(
					commercePaymentRequestHelper.getRequest(), "remove"),
				"delete", "delete", "headless"));

		return fdsActionDropdownItems;
	}

	private final ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;
	private final CommercePaymentMethodGroupRelQualifierService
		_commercePaymentMethodGroupRelQualifierService;

}