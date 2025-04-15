/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderTypeRelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderTypeQualifiersDisplayContext
	extends CommerceOrderTypeDisplayContext {

	public CommerceOrderTypeQualifiersDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<CommerceOrderType>
			commerceOrderTypeModelResourcePermission,
		CommerceOrderTypeService commerceOrderTypeService,
		CommerceOrderTypeRelService commerceOrderTypeRelService,
		Portal portal) {

		super(
			httpServletRequest, commerceOrderTypeModelResourcePermission,
			commerceOrderTypeService, portal);

		_commerceOrderTypeRelService = commerceOrderTypeRelService;
	}

	public String getActiveChannelEligibility() throws PortalException {
		long commerceChannelRelsCount =
			_commerceOrderTypeRelService.
				getCommerceOrderTypeCommerceChannelRelsCount(
					getCommerceOrderTypeId(), null);

		if (commerceChannelRelsCount > 0) {
			return "channels";
		}

		return "all";
	}

	public List<FDSActionDropdownItem>
			getCommerceOrderTypeChannelFDSActionDropdownItems()
		throws PortalException {

		return _getHeadlessFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceChannel.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_channels/edit_commerce_channel"
			).setRedirect(
				commerceOrderRequestHelper.getCurrentURL()
			).setParameter(
				"commerceChannelId", "{channel.id}"
			).buildString(),
			false);
	}

	public String getCommerceOrderTypeChannelsApiURL() throws PortalException {
		return "/o/headless-commerce-admin-order/v1.0/order-types/" +
			getCommerceOrderTypeId() +
				"/order-type-channels?nestedFields=channel";
	}

	private List<FDSActionDropdownItem> _getHeadlessFDSActionTemplates(
		String portletURL, boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			portletURL, "pencil", "edit",
			LanguageUtil.get(httpServletRequest, "edit"), "get", null, null);

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "trash", "remove",
				LanguageUtil.get(httpServletRequest, "remove"), "delete",
				"delete", "headless"));

		return fdsActionDropdownItems;
	}

	private final CommerceOrderTypeRelService _commerceOrderTypeRelService;

}