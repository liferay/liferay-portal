/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelService;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelService;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommercePriceListQualifiersDisplayContext
	extends BaseCommercePriceListDisplayContext {

	public CommercePriceListQualifiersDisplayContext(
		CommerceCatalogService commerceCatalogService,
		CommercePriceListAccountRelService commercePriceListAccountRelService,
		CommercePriceListChannelRelService commercePriceListChannelRelService,
		CommercePriceListCommerceAccountGroupRelService
			commercePriceListCommerceAccountGroupRelService,
		CommercePriceListOrderTypeRelService
			commercePriceListOrderTypeRelService,
		ModelResourcePermission<CommercePriceList>
			commercePriceListModelResourcePermission,
		CommercePriceListService commercePriceListService, Portal portal,
		HttpServletRequest httpServletRequest) {

		super(
			commerceCatalogService, commercePriceListModelResourcePermission,
			commercePriceListService, httpServletRequest);

		_commercePriceListAccountRelService =
			commercePriceListAccountRelService;
		_commercePriceListChannelRelService =
			commercePriceListChannelRelService;
		_commercePriceListCommerceAccountGroupRelService =
			commercePriceListCommerceAccountGroupRelService;
		_commercePriceListOrderTypeRelService =
			commercePriceListOrderTypeRelService;
		_portal = portal;
	}

	public String getActiveAccountEligibility() throws PortalException {
		long commercePriceListId = getCommercePriceListId();

		long commercePriceListAccountRelsCount =
			_commercePriceListAccountRelService.
				getCommercePriceListAccountRelsCount(commercePriceListId);

		if (commercePriceListAccountRelsCount > 0) {
			return "accounts";
		}

		long commercePriceListAccountGroupRelsCount =
			_commercePriceListCommerceAccountGroupRelService.
				getCommercePriceListCommerceAccountGroupRelsCount(
					commercePriceListId);

		if (commercePriceListAccountGroupRelsCount > 0) {
			return "accountGroups";
		}

		return "all";
	}

	public String getActiveChannelEligibility() throws PortalException {
		int commercePriceListChannelRelsCount =
			_commercePriceListChannelRelService.
				getCommercePriceListChannelRelsCount(getCommercePriceListId());

		if (commercePriceListChannelRelsCount > 0) {
			return "channels";
		}

		return "all";
	}

	public String getActiveOrderTypeEligibility() throws PortalException {
		int commercePriceListChannelRelsCount =
			_commercePriceListOrderTypeRelService.
				getCommercePriceListOrderTypeRelsCount(
					getCommercePriceListId(), null);

		if (commercePriceListChannelRelsCount > 0) {
			return "orderTypes";
		}

		return "all";
	}

	public List<FDSActionDropdownItem>
			getPriceListAccountFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest,
					AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/account_admin/edit_account_entry"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"accountEntryId", "{account.id}"
			).buildString(),
			false);
	}

	public List<FDSActionDropdownItem>
			getPriceListAccountGroupFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));
	}

	public String getPriceListAccountGroupsApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/price-lists/" +
			getCommercePriceListId() +
				"/price-list-account-groups?nestedFields=accountGroup";
	}

	public String getPriceListAccountsApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/price-lists/" +
			getCommercePriceListId() +
				"/price-list-accounts?nestedFields=account";
	}

	public List<FDSActionDropdownItem>
			getPriceListChannelFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceChannel.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_channels/edit_commerce_channel"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"commerceChannelId", "{channel.id}"
			).buildString(),
			false);
	}

	public String getPriceListChannelsApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/price-lists/" +
			getCommercePriceListId() +
				"/price-list-channels?nestedFields=channel";
	}

	public List<FDSActionDropdownItem>
			getPriceListOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceOrderType.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_order_type/edit_commerce_order_type"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"commerceOrderTypeId", "{orderType.id}"
			).buildString(),
			false);
	}

	public String getPriceListOrderTypesAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/price-lists/" +
			getCommercePriceListId() +
				"/price-list-order-types?nestedFields=orderType";
	}

	private final CommercePriceListAccountRelService
		_commercePriceListAccountRelService;
	private final CommercePriceListChannelRelService
		_commercePriceListChannelRelService;
	private final CommercePriceListCommerceAccountGroupRelService
		_commercePriceListCommerceAccountGroupRelService;
	private final CommercePriceListOrderTypeRelService
		_commercePriceListOrderTypeRelService;
	private final Portal _portal;

}