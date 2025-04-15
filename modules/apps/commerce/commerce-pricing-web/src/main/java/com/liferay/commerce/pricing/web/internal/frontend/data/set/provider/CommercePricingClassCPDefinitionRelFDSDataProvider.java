/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.frontend.model.ImageField;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PricingClassCPDefinitionRel;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICING_CLASSES_PRODUCT_DEFINITIONS,
	service = FDSDataProvider.class
)
public class CommercePricingClassCPDefinitionRelFDSDataProvider
	implements FDSDataProvider<PricingClassCPDefinitionRel> {

	@Override
	public List<PricingClassCPDefinitionRel> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<PricingClassCPDefinitionRel> pricingClasseCPDefinitionRels =
			new ArrayList<>();

		try {
			Locale locale = _portal.getLocale(httpServletRequest);

			long commercePricingClassId = ParamUtil.getLong(
				httpServletRequest, "commercePricingClassId");

			List<CommercePricingClassCPDefinitionRel>
				commercePricingClassCPDefinitionRels =
					_commercePricingClassCPDefinitionRelService.
						searchByCommercePricingClassId(
							commercePricingClassId, fdsKeywords.getKeywords(),
							_language.getLanguageId(locale),
							fdsPagination.getStartPosition(),
							fdsPagination.getEndPosition());

			for (CommercePricingClassCPDefinitionRel
					commercePricingClassCPDefinitionRel :
						commercePricingClassCPDefinitionRels) {

				CPDefinition cpDefinition =
					_cpDefinitionService.getCPDefinition(
						commercePricingClassCPDefinitionRel.
							getCPDefinitionId());

				pricingClasseCPDefinitionRels.add(
					new PricingClassCPDefinitionRel(
						commercePricingClassCPDefinitionRel.
							getCommercePricingClassCPDefinitionRelId(),
						cpDefinition.getCPDefinitionId(),
						cpDefinition.getName(),
						_getSku(
							cpDefinition,
							_portal.getLocale(httpServletRequest)),
						new ImageField(
							cpDefinition.getName(
								_language.getLanguageId(locale)),
							"rounded", "lg",
							cpDefinition.getDefaultImageThumbnailSrc(
								AccountConstants.ACCOUNT_ENTRY_ID_ADMIN))));
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return pricingClasseCPDefinitionRels;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commercePricingClassId = ParamUtil.getLong(
			httpServletRequest, "commercePricingClassId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRelsCount(
				commercePricingClassId, fdsKeywords.getKeywords(),
				themeDisplay.getLanguageId());
	}

	private String _getSku(CPDefinition cpDefinition, Locale locale) {
		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		if (cpInstances.isEmpty()) {
			return StringPool.BLANK;
		}

		if (cpInstances.size() > 1) {
			return _language.get(locale, "multiple-skus");
		}

		CPInstance cpInstance = cpInstances.get(0);

		return cpInstance.getSku();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePricingClassCPDefinitionRelFDSDataProvider.class);

	@Reference
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}