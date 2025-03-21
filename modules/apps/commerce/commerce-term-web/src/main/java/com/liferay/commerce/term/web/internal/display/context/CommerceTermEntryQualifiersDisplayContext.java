/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.web.internal.display.context;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.term.entry.type.CommerceTermEntryTypeRegistry;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryRelService;
import com.liferay.commerce.term.service.CommerceTermEntryService;
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
 * @author Crescenzo Rega
 * @author Alessio Antonio Rendina
 */
public class CommerceTermEntryQualifiersDisplayContext
	extends CommerceTermEntryDisplayContext {

	public CommerceTermEntryQualifiersDisplayContext(
		ModelResourcePermission<CommerceTermEntry>
			commerceTermEntryModelResourcePermission,
		CommerceTermEntryRelService commerceTermEntryRelService,
		CommerceTermEntryService commerceTermEntryService,
		CommerceTermEntryTypeRegistry commerceTermEntryTypeRegistry,
		HttpServletRequest httpServletRequest, Portal portal) {

		super(
			commerceTermEntryModelResourcePermission,
			commerceTermEntryTypeRegistry, commerceTermEntryService,
			httpServletRequest, portal);

		_commerceTermEntryRelService = commerceTermEntryRelService;
	}

	public String getActiveOrderTypeEligibility() throws PortalException {
		long commerceOrderTypeCommerceTermEntryRelsCount =
			_commerceTermEntryRelService.
				getCommerceOrderTypeCommerceTermEntryRelsCount(
					getCommerceTermEntryId(), null);

		if (commerceOrderTypeCommerceTermEntryRelsCount > 0) {
			return "orderTypes";
		}

		return "all";
	}

	public String getCommerceOrderTypeCommerceTermEntriesAPIURL()
		throws PortalException {

		return "/o/headless-commerce-admin-order/v1.0/terms/" +
			getCommerceTermEntryId() +
				"/term-order-types?nestedFields=orderType";
	}

	public List<FDSActionDropdownItem>
			getCommerceOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return _getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceOrderType.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_order_type/edit_commerce_order_type"
			).setRedirect(
				commerceTermEntryRequestHelper.getCurrentURL()
			).setParameter(
				"commerceOrderTypeId", "{orderType.id}"
			).buildString(),
			false);
	}

	private List<FDSActionDropdownItem> _getFDSActionTemplates(
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

	private final CommerceTermEntryRelService _commerceTermEntryRelService;

}