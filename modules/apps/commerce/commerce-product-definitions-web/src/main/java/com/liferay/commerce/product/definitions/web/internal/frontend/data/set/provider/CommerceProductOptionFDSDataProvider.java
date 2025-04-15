/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductOption;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_OPTIONS,
	service = FDSDataProvider.class
)
public class CommerceProductOptionFDSDataProvider
	implements FDSDataProvider<ProductOption> {

	@Override
	public List<ProductOption> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		Locale locale = _portal.getLocale(httpServletRequest);

		return TransformUtil.transform(
			_getCPDefinitionOptionRels(
				cpDefinitionId, fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), sort),
			cpDefinitionOptionRel -> {
				CommerceOptionType commerceOptionType =
					_commerceOptionTypeRegistry.getCommerceOptionType(
						cpDefinitionOptionRel.getCommerceOptionTypeKey());

				return new ProductOption(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					commerceOptionType.getLabel(locale),
					cpDefinitionOptionRel.getName(
						_language.getLanguageId(locale)),
					cpDefinitionOptionRel.getPriority(),
					_language.get(
						locale,
						cpDefinitionOptionRel.isRequired() ? "yes" : "no"),
					_language.get(
						locale,
						cpDefinitionOptionRel.isSkuContributor() ? "yes" :
							"no"),
					cpDefinitionOptionRel.
						getCPDefinitionOptionValueRelsCount());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpDefinitionId);

		return _cpDefinitionOptionRelService.searchCPDefinitionOptionRelsCount(
			cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
			cpDefinition.getCPDefinitionId(), fdsKeywords.getKeywords());
	}

	private BaseModelSearchResult<CPDefinitionOptionRel>
			_getBaseModelSearchResult(
				long cpDefinitionId, String keywords, int start, int end,
				Sort sort)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpDefinitionId);

		return _cpDefinitionOptionRelService.searchCPDefinitionOptionRels(
			cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
			cpDefinitionId, keywords, start, end, new Sort[] {sort});
	}

	private List<CPDefinitionOptionRel> _getCPDefinitionOptionRels(
			long cpDefinitionId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		BaseModelSearchResult<CPDefinitionOptionRel> baseModelSearchResult =
			_getBaseModelSearchResult(
				cpDefinitionId, keywords, start, end, sort);

		return baseModelSearchResult.getBaseModels();
	}

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}