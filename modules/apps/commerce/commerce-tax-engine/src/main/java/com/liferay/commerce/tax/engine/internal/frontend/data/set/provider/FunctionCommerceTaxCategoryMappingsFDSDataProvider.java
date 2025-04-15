/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.frontend.data.set.provider;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.tax.engine.internal.constants.FunctionCommerceTaxEngineFDSNames;
import com.liferay.commerce.tax.engine.internal.model.TaxCategoryMapping;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	property = "fds.data.provider.key=" + FunctionCommerceTaxEngineFDSNames.FUNCTION_COMMERCE_TAX_ENGINE_TAX_CATEGORY_MAPPINGS,
	service = FDSDataProvider.class
)
public class FunctionCommerceTaxCategoryMappingsFDSDataProvider
	implements FDSDataProvider<TaxCategoryMapping> {

	@Override
	public List<TaxCategoryMapping> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");
		long commerceTaxMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceTaxMethodId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		return TransformUtil.transform(
			_commerceTaxCategoryMappingService.getCommerceTaxCategoryMappings(
				commerceChannel.getGroupId(), commerceTaxMethodId,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), null),
			commerceTaxCategoryMapping -> {
				CPTaxCategory cpTaxCategory =
					_cpTaxCategoryLocalService.getCPTaxCategory(
						commerceTaxCategoryMapping.getCPTaxCategoryId());

				return new TaxCategoryMapping(
					commerceTaxCategoryMapping.getExternalReferenceCode(),
					cpTaxCategory.getName(themeDisplay.getLanguageId()),
					commerceTaxCategoryMapping.
						getCommerceTaxCategoryMappingId());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");
		long commerceTaxMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceTaxMethodId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		return _commerceTaxCategoryMappingService.
			getCommerceTaxCategoryMappingCount(
				commerceChannel.getGroupId(), commerceTaxMethodId);
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceTaxCategoryMappingService
		_commerceTaxCategoryMappingService;

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

}