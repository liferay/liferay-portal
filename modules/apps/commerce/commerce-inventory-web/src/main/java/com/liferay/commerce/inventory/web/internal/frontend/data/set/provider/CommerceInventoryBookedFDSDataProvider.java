/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.data.set.provider;

import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityService;
import com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames;
import com.liferay.commerce.inventory.web.internal.model.BookedQuantity;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
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

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceInventoryFDSNames.INVENTORY_BOOKED,
	service = FDSDataProvider.class
)
public class CommerceInventoryBookedFDSDataProvider
	implements FDSDataProvider<BookedQuantity> {

	@Override
	public List<BookedQuantity> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return TransformUtil.transform(
			_commerceInventoryBookedQuantityService.
				getCommerceInventoryBookedQuantities(
					_portal.getCompanyId(httpServletRequest),
					fdsKeywords.getKeywords(), sku, unitOfMeasureKey,
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition()),
			commerceInventoryBookedQuantity -> {
				CommerceOrderItem commerceOrderItem =
					_commerceOrderItemLocalService.
						fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
							commerceInventoryBookedQuantity.
								getCommerceInventoryBookedQuantityId());

				BigDecimal bookedQuantity = BigDecimal.ZERO;

				BigDecimal commerceInventoryWarehouseItemQuantity =
					commerceInventoryBookedQuantity.getQuantity();

				if (commerceInventoryWarehouseItemQuantity != null) {
					bookedQuantity = commerceInventoryWarehouseItemQuantity;
				}

				return new BookedQuantity(
					_getAccountName(commerceOrderItem),
					_getCommerceOrderId(commerceOrderItem),
					_getExpirationDate(
						commerceInventoryBookedQuantity.getExpirationDate(),
						httpServletRequest),
					_commerceQuantityFormatter.format(
						commerceInventoryBookedQuantity.getCompanyId(),
						bookedQuantity,
						commerceInventoryBookedQuantity.getSku(),
						commerceInventoryBookedQuantity.getUnitOfMeasureKey()),
					commerceInventoryBookedQuantity.getUnitOfMeasureKey());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return _commerceInventoryBookedQuantityService.
			getCommerceInventoryBookedQuantitiesCount(
				_portal.getCompanyId(httpServletRequest),
				fdsKeywords.getKeywords(), sku, unitOfMeasureKey);
	}

	private String _getAccountName(CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (commerceOrderItem == null) {
			return StringPool.BLANK;
		}

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		return commerceOrder.getCommerceAccountName();
	}

	private long _getCommerceOrderId(CommerceOrderItem commerceOrderItem) {
		if (commerceOrderItem == null) {
			return 0;
		}

		return commerceOrderItem.getCommerceOrderId();
	}

	private String _getExpirationDate(
		Date expirationDate, HttpServletRequest httpServletRequest) {

		if (expirationDate == null) {
			return _language.get(httpServletRequest, "never-expire");
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return dateTimeFormat.format(expirationDate);
	}

	@Reference
	private CommerceInventoryBookedQuantityService
		_commerceInventoryBookedQuantityService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}