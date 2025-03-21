/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.data.set.provider;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames;
import com.liferay.commerce.inventory.web.internal.model.Replenishment;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceInventoryFDSNames.INVENTORY_REPLENISHMENT,
	service = FDSDataProvider.class
)
public class CommerceInventoryReplenishmentFDSDataProvider
	implements FDSDataProvider<Replenishment> {

	@Override
	public List<Replenishment> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDate(
			DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return TransformUtil.transform(
			_commerceInventoryReplenishmentItemService.
				getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
					_portal.getCompanyId(httpServletRequest), sku,
					unitOfMeasureKey, fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition()),
			commerceInventoryReplenishmentItem -> {
				CommerceInventoryWarehouse commerceInventoryWarehouse =
					commerceInventoryReplenishmentItem.
						getCommerceInventoryWarehouse();

				BigDecimal quantity = BigDecimal.ZERO;

				BigDecimal commerceInventoryWarehouseItemQuantity =
					commerceInventoryReplenishmentItem.getQuantity();

				if (commerceInventoryWarehouseItemQuantity != null) {
					quantity = commerceInventoryWarehouseItemQuantity;
				}

				return new Replenishment(
					commerceInventoryReplenishmentItem.
						getCommerceInventoryWarehouseId(),
					commerceInventoryReplenishmentItem.
						getCommerceInventoryReplenishmentItemId(),
					commerceInventoryWarehouse.getName(
						_portal.getLocale(httpServletRequest)),
					dateTimeFormat.format(
						commerceInventoryReplenishmentItem.
							getAvailabilityDate()),
					_commerceQuantityFormatter.format(
						commerceInventoryReplenishmentItem.getCompanyId(),
						quantity, commerceInventoryReplenishmentItem.getSku(),
						commerceInventoryReplenishmentItem.
							getUnitOfMeasureKey()),
					commerceInventoryReplenishmentItem.getUnitOfMeasureKey());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return _commerceInventoryReplenishmentItemService.
			getCommerceInventoryReplenishmentItemsCountByCompanyIdSkuAndUnitOfMeasureKey(
				_portal.getCompanyId(httpServletRequest), sku,
				unitOfMeasureKey);
	}

	@Reference
	private CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private Portal _portal;

}