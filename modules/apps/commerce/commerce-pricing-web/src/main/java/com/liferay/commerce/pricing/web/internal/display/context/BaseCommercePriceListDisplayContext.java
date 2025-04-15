/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseCommercePriceListDisplayContext
	extends BasePricingDisplayContext {

	public BaseCommercePriceListDisplayContext(
		CommerceCatalogService commerceCatalogService,
		ModelResourcePermission<CommercePriceList>
			commercePriceListModelResourcePermission,
		CommercePriceListService commercePriceListService,
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);

		this.commerceCatalogService = commerceCatalogService;
		this.commercePriceListModelResourcePermission =
			commercePriceListModelResourcePermission;
		this.commercePriceListService = commercePriceListService;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public long getCommerceCatalogId() throws PortalException {
		CommercePriceList commercePriceList = getCommercePriceList();

		CommerceCatalog commerceCatalog =
			commerceCatalogService.fetchCommerceCatalogByGroupId(
				commercePriceList.getGroupId());

		if (commerceCatalog == null) {
			return 0;
		}

		return commerceCatalog.getCommerceCatalogId();
	}

	public CommercePriceList getCommercePriceList() throws PortalException {
		if (commercePriceList != null) {
			return commercePriceList;
		}

		long commercePriceListId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(), "commercePriceListId");

		if (commercePriceListId > 0) {
			commercePriceList = commercePriceListService.getCommercePriceList(
				commercePriceListId);
		}

		return commercePriceList;
	}

	public long getCommercePriceListId() throws PortalException {
		CommercePriceList commercePriceList = getCommercePriceList();

		if (commercePriceList == null) {
			return 0;
		}

		return commercePriceList.getCommercePriceListId();
	}

	public String getCommercePriceListType(String portletName) {
		if (Validator.isNull(portletName)) {
			return StringPool.BLANK;
		}

		if (portletName.equals(
				CommercePricingPortletKeys.COMMERCE_PRICE_LIST)) {

			return CommercePriceListConstants.TYPE_PRICE_LIST;
		}
		else if (portletName.equals(
					CommercePricingPortletKeys.COMMERCE_PROMOTION)) {

			return CommercePriceListConstants.TYPE_PROMOTION;
		}

		return StringPool.BLANK;
	}

	public boolean hasPermission(long commercePriceListId, String actionId)
		throws PortalException {

		return commercePriceListModelResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(),
			commercePriceListId, actionId);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		PortletResourcePermission portletResourcePermission =
			commercePriceListModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(), null,
			actionId);
	}

	protected List<FDSActionDropdownItem> getFDSActionDropdownItems(
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
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));

		return fdsActionDropdownItems;
	}

	protected CommerceCatalogService commerceCatalogService;
	protected CommercePriceList commercePriceList;
	protected final ModelResourcePermission<CommercePriceList>
		commercePriceListModelResourcePermission;
	protected CommercePriceListService commercePriceListService;
	protected final CPRequestHelper cpRequestHelper;

}