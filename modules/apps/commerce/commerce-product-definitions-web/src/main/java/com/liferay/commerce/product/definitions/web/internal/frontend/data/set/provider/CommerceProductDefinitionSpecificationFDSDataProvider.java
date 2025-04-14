/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.constants.CPOptionCategoryConstants;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductSpecification;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueService;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_DEFINITION_SPECIFICATIONS,
	service = FDSDataProvider.class
)
public class CommerceProductDefinitionSpecificationFDSDataProvider
	implements FDSDataProvider<ProductSpecification> {

	@Override
	public List<ProductSpecification> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(
			_portal.getLocale(httpServletRequest));

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return TransformUtil.transform(
			_cpDefinitionSpecificationOptionValueService.
				getCPDefinitionSpecificationOptionValues(
					cpDefinitionId, null, fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), null),
			cpDefinitionSpecificationOptionValue -> {
				CPSpecificationOption cpSpecificationOption =
					cpDefinitionSpecificationOptionValue.
						getCPSpecificationOption();

				return new ProductSpecification(
					cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId(),
					_getCPSpecificationOptionTitle(
						cpSpecificationOption, languageId),
					_getCPDefinitionSpecificationOptionValue(
						cpDefinitionSpecificationOptionValue, languageId),
					_getCPOptionCategoryTitle(
						cpDefinitionSpecificationOptionValue, languageId),
					cpDefinitionSpecificationOptionValue.getPriority());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return _cpDefinitionSpecificationOptionValueService.
			getCPDefinitionSpecificationOptionValuesCount(cpDefinitionId, null);
	}

	private String _getCPDefinitionSpecificationOptionValue(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue,
		String languageId) {

		String[] availableLanguageIds =
			cpDefinitionSpecificationOptionValue.getAvailableLanguageIds();

		if (availableLanguageIds.length == 1) {
			return cpDefinitionSpecificationOptionValue.getValue(
				availableLanguageIds[0]);
		}

		if (Validator.isBlank(
				cpDefinitionSpecificationOptionValue.getValue(languageId))) {

			return cpDefinitionSpecificationOptionValue.getValue(
				cpDefinitionSpecificationOptionValue.getDefaultLanguageId());
		}

		return cpDefinitionSpecificationOptionValue.getValue(languageId);
	}

	private String _getCPOptionCategoryTitle(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue,
			String languageId)
		throws PortalException {

		long cpOptionCategoryId =
			cpDefinitionSpecificationOptionValue.getCPOptionCategoryId();

		if (cpOptionCategoryId ==
				CPOptionCategoryConstants.DEFAULT_CP_OPTION_CATEGORY_ID) {

			return StringPool.BLANK;
		}

		try {
			CPOptionCategory cpOptionCategory =
				_cpOptionCategoryService.getCPOptionCategory(
					cpOptionCategoryId);

			return cpOptionCategory.getTitle(languageId);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getCPSpecificationOptionTitle(
		CPSpecificationOption cpSpecificationOption, String languageId) {

		String[] availableLanguageIds =
			cpSpecificationOption.getAvailableLanguageIds();

		if (availableLanguageIds.length == 1) {
			return cpSpecificationOption.getTitle(availableLanguageIds[0]);
		}

		if (Validator.isBlank(cpSpecificationOption.getTitle(languageId))) {
			return cpSpecificationOption.getTitle(
				cpSpecificationOption.getDefaultLanguageId());
		}

		return cpSpecificationOption.getTitle(languageId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductDefinitionSpecificationFDSDataProvider.class);

	@Reference
	private CPDefinitionSpecificationOptionValueService
		_cpDefinitionSpecificationOptionValueService;

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

	@Reference
	private Portal _portal;

}