/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PricingClassDiscount;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICING_CLASSES_DISCOUNTS,
	service = FDSDataProvider.class
)
public class CommercePricingClassDiscountFDSDataProvider
	implements FDSDataProvider<PricingClassDiscount> {

	@Override
	public List<PricingClassDiscount> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commercePricingClassId = ParamUtil.getLong(
			httpServletRequest, "commercePricingClassId");

		return TransformUtil.transform(
			_commerceDiscountService.searchByCommercePricingClassId(
				commercePricingClassId, fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commerceDiscount -> {
				String statusDisplayStyle = StringPool.BLANK;

				if (commerceDiscount.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					statusDisplayStyle = "success";
				}
				else if (commerceDiscount.getStatus() ==
							WorkflowConstants.STATUS_DRAFT) {

					statusDisplayStyle = "secondary";
				}
				else if (commerceDiscount.getStatus() ==
							WorkflowConstants.STATUS_EXPIRED) {

					statusDisplayStyle = "warning";
				}

				return new PricingClassDiscount(
					commerceDiscount.getCommerceDiscountId(),
					commerceDiscount.getTitle(), "Product Group",
					_getDiscountType(commerceDiscount.isUsePercentage()),
					new LabelField(
						statusDisplayStyle,
						_language.get(
							httpServletRequest,
							WorkflowConstants.getStatusLabel(
								commerceDiscount.getStatus()))));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commercePricingClassId = ParamUtil.getLong(
			httpServletRequest, "commercePricingClassId");

		return _commerceDiscountService.
			getCommerceDiscountsCountByPricingClassId(
				commercePricingClassId, fdsKeywords.getKeywords());
	}

	private String _getDiscountType(boolean usePercentage) {
		if (usePercentage) {
			return "Percentage";
		}

		return "Absolute";
	}

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private Language _language;

}