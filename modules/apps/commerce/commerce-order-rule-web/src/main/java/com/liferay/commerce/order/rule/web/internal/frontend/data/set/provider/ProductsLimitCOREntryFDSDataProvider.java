/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.frontend.data.set.provider;

import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.commerce.order.rule.web.internal.constants.COREntryFDSNames;
import com.liferay.commerce.order.rule.web.internal.frontend.model.Product;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + COREntryFDSNames.COR_ENTRY_PRODUCTS_LIMITS,
	service = FDSDataProvider.class
)
public class ProductsLimitCOREntryFDSDataProvider
	implements FDSDataProvider<Product> {

	@Override
	public List<Product> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long corEntryId = ParamUtil.getLong(httpServletRequest, "corEntryId");

		COREntry corEntry = _corEntryService.getCOREntry(corEntryId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		return TransformUtil.transform(
			ListUtil.filter(
				TransformUtil.transform(
					StringUtil.split(
						typeSettingsUnicodeProperties.getProperty(
							COREntryConstants.
								TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES)),
					cProductExternalReferenceCode -> _fetchCPDefinition(
						cProductExternalReferenceCode,
						corEntry.getCompanyId())),
				cpDefinition -> cpDefinition != null),
			filteredCPDefinition -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				CProduct cProduct = filteredCPDefinition.getCProduct();

				return new Product(
					cProduct.getExternalReferenceCode(),
					filteredCPDefinition.getName(themeDisplay.getLanguageId()));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long corEntryId = ParamUtil.getLong(httpServletRequest, "corEntryId");

		COREntry corEntry = _corEntryService.getCOREntry(corEntryId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		List<String> cProductExternalReferenceCodes = ListUtil.filter(
			StringUtil.split(
				typeSettingsUnicodeProperties.getProperty(
					COREntryConstants.
						TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES)),
			cProductExternalReferenceCode -> {
				CPDefinition cpDefinition = _fetchCPDefinition(
					cProductExternalReferenceCode, corEntry.getCompanyId());

				return cpDefinition != null;
			});

		return cProductExternalReferenceCodes.size();
	}

	private CPDefinition _fetchCPDefinition(
		String cProductExternalReferenceCode, long companyId) {

		try {
			CProduct cProduct =
				_cProductLocalService.fetchCProductByExternalReferenceCode(
					cProductExternalReferenceCode, companyId);

			if (cProduct == null) {
				return null;
			}

			return _cpDefinitionService.fetchCPDefinitionByCProductId(
				cProduct.getCProductId(), false);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductsLimitCOREntryFDSDataProvider.class);

	@Reference
	private COREntryService _corEntryService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CProductLocalService _cProductLocalService;

}