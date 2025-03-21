/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductOptionValue;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.util.CPCollectionProviderHelper;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_OPTION_VALUES,
	service = FDSDataProvider.class
)
public class CommerceProductOptionValueFDSDataProvider
	implements FDSDataProvider<ProductOptionValue> {

	@Override
	public List<ProductOptionValue> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			new ArrayList<>();

		long cpDefinitionOptionRelId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionOptionRelId");

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			cpDefinitionOptionRelId);

		Locale locale = _portal.getLocale(httpServletRequest);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(
				cpDefinitionOptionRelId);

		if (cpDefinitionOptionRel.isDefinedExternally()) {
			cpDefinitionOptionValueRels =
				_cpCollectionProviderHelper.getCPDefinitionOptionValueRels(
					cpDefinitionOptionRel, fdsKeywords.getKeywords(),
					Pagination.of(
						fdsPagination.getEndPosition(),
						fdsPagination.getStartPosition()));
		}
		else {
			BaseModelSearchResult<CPDefinitionOptionValueRel>
				cpDefinitionOptionValueRelBaseModelSearchResult =
					_cpDefinitionOptionValueRelService.
						searchCPDefinitionOptionValueRels(
							cpDefinitionOptionRel.getCompanyId(),
							cpDefinitionOptionRel.getGroupId(),
							cpDefinitionOptionRelId, fdsKeywords.getKeywords(),
							fdsPagination.getStartPosition(),
							fdsPagination.getEndPosition(), new Sort[] {sort});

			cpDefinitionOptionValueRels =
				cpDefinitionOptionValueRelBaseModelSearchResult.getBaseModels();
		}

		return TransformUtil.transform(
			cpDefinitionOptionValueRels,
			cpDefinitionOptionValueRel -> new ProductOptionValue(
				cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
				_commercePriceFormatter.format(
					commerceCurrency,
					_getPrice(
						cpDefinitionOptionValueRel, cpDefinitionOptionRelId),
					locale),
				cpDefinitionOptionValueRel.getKey(),
				cpDefinitionOptionValueRel.getName(
					_language.getLanguageId(locale)),
				cpDefinitionOptionValueRel.getPriority(),
				_language.get(
					locale,
					cpDefinitionOptionValueRel.isPreselected() ? "yes" : "no"),
				_getSku(cpDefinitionOptionValueRel)));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionOptionRelId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionOptionRelId");

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(
				cpDefinitionOptionRelId);

		if (cpDefinitionOptionRel.isDefinedExternally()) {
			return _cpCollectionProviderHelper.
				getCPDefinitionOptionValueRelsCount(
					cpDefinitionOptionRel, fdsKeywords.getKeywords());
		}

		return _cpDefinitionOptionValueRelService.
			searchCPDefinitionOptionValueRelsCount(
				cpDefinitionOptionRel.getCompanyId(),
				cpDefinitionOptionRel.getGroupId(),
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				fdsKeywords.getKeywords());
	}

	private CommerceCurrency _getCommerceCurrency(long cpDefinitionOptionRelId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(
				cpDefinitionOptionRelId);

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByGroupId(
				cpDefinitionOptionRel.getGroupId());

		return _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCatalog.getCompanyId(),
			commerceCatalog.getCommerceCurrencyCode());
	}

	private BigDecimal _getPrice(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			long cpDefinitionOptionRelId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(
				cpDefinitionOptionRelId);

		if (!cpDefinitionOptionRel.isPriceTypeStatic() ||
			(cpDefinitionOptionValueRel.getPrice() == null)) {

			return BigDecimal.ZERO;
		}

		if (BigDecimalUtil.eq(
				cpDefinitionOptionValueRel.getQuantity(), BigDecimal.ZERO)) {

			return cpDefinitionOptionValueRel.getPrice();
		}

		BigDecimal quantity = cpDefinitionOptionValueRel.getQuantity();

		return quantity.multiply(cpDefinitionOptionValueRel.getPrice());
	}

	private String _getSku(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		if (Validator.isNull(cpDefinitionOptionValueRel.getCPInstanceUuid())) {
			return StringPool.BLANK;
		}

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		if (cpInstance == null) {
			return StringPool.BLANK;
		}

		String unitOfMeasureKey =
			cpDefinitionOptionValueRel.getUnitOfMeasureKey();

		if (Validator.isNull(unitOfMeasureKey)) {
			return cpInstance.getSku();
		}

		return StringBundler.concat(
			cpInstance.getSku(), StringPool.SPACE, StringPool.DASH,
			StringPool.SPACE, unitOfMeasureKey);
	}

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CPCollectionProviderHelper _cpCollectionProviderHelper;

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}