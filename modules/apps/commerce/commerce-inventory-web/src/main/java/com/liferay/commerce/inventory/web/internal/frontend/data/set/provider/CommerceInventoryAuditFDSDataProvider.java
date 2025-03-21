/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.data.set.provider;

import com.liferay.commerce.frontend.model.TimelineModel;
import com.liferay.commerce.inventory.service.CommerceInventoryAuditService;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditType;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditTypeRegistry;
import com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
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
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceInventoryFDSNames.INVENTORY_AUDIT,
	service = FDSDataProvider.class
)
public class CommerceInventoryAuditFDSDataProvider
	implements FDSDataProvider<TimelineModel> {

	@Override
	public List<TimelineModel> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return TransformUtil.transform(
			_commerceInventoryAuditService.getCommerceInventoryAudits(
				_portal.getCompanyId(httpServletRequest), sku, unitOfMeasureKey,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commerceInventoryAudit -> {
				StringBundler titleSB = new StringBundler(1);

				try {
					CommerceInventoryAuditType commerceInventoryAuditType =
						_commerceInventoryAuditTypeRegistry.
							getCommerceInventoryAuditType(
								commerceInventoryAudit.getLogType());

					Locale locale = _portal.getLocale(httpServletRequest);

					titleSB.append(
						commerceInventoryAuditType.formatLog(
							commerceInventoryAudit.getUserId(),
							commerceInventoryAudit.getLogTypeSettings(),
							locale));

					BigDecimal commerceInventoryWarehouseItemQuantity =
						commerceInventoryAudit.getQuantity();

					return new TimelineModel(
						commerceInventoryAudit.getCommerceInventoryAuditId(),
						dateTimeFormat.format(
							commerceInventoryAudit.getCreateDate()),
						commerceInventoryAuditType.formatQuantity(
							commerceInventoryWarehouseItemQuantity, locale),
						titleSB.toString());
				}
				catch (Exception exception) {
					throw new PortalException(
						exception.getMessage(), exception);
				}
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return _commerceInventoryAuditService.getCommerceInventoryAuditsCount(
			_portal.getCompanyId(httpServletRequest), sku, unitOfMeasureKey);
	}

	@Reference
	private CommerceInventoryAuditService _commerceInventoryAuditService;

	@Reference
	private CommerceInventoryAuditTypeRegistry
		_commerceInventoryAuditTypeRegistry;

	@Reference
	private Portal _portal;

}