/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.frontend.model.ImageField;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.DiscountRuleCPDefinition;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.DISCOUNT_RULE_PRODUCT_DEFINITIONS,
	service = FDSDataProvider.class
)
public class CommerceDiscountRuleCPDefinitionFDSDataProvider
	implements FDSDataProvider<DiscountRuleCPDefinition> {

	@Override
	public List<DiscountRuleCPDefinition> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		try {
			return _getDiscountRuleCPDefinitions(
				fdsKeywords, httpServletRequest);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.emptyList();
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		try {
			List<DiscountRuleCPDefinition> discountRuleCPDefinitions =
				_getDiscountRuleCPDefinitions(fdsKeywords, httpServletRequest);

			return discountRuleCPDefinitions.size();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return 0;
	}

	private List<DiscountRuleCPDefinition> _getDiscountRuleCPDefinitions(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws Exception {

		long commerceDiscountRuleId = ParamUtil.getLong(
			httpServletRequest, "commerceDiscountRuleId");

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleService.getCommerceDiscountRule(
				commerceDiscountRuleId);

		long[] cpDefinitionIds = StringUtil.split(
			commerceDiscountRule.getSettingsProperty(
				commerceDiscountRule.getType()),
			0L);

		if (cpDefinitionIds == null) {
			return Collections.emptyList();
		}

		List<DiscountRuleCPDefinition> discountRuleCPDefinitions =
			new ArrayList<>();

		Locale locale = _portal.getLocale(httpServletRequest);

		String languageId = _language.getLanguageId(locale);

		String keywordsLowerCase = StringUtil.toLowerCase(
			fdsKeywords.getKeywords());

		for (long cpDefinitionId : cpDefinitionIds) {
			CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
				cpDefinitionId);

			String cpDefinitionName = cpDefinition.getName(languageId);

			String cpDefinitionNameLowerCase = StringUtil.toLowerCase(
				cpDefinitionName);

			if (!cpDefinitionNameLowerCase.contains(keywordsLowerCase)) {
				continue;
			}

			discountRuleCPDefinitions.add(
				new DiscountRuleCPDefinition(
					commerceDiscountRule.getCommerceDiscountRuleId(),
					cpDefinition.getCPDefinitionId(), cpDefinitionName,
					_getSku(cpDefinition, locale),
					new ImageField(
						cpDefinitionName, "rounded", "lg",
						cpDefinition.getDefaultImageThumbnailSrc(
							AccountConstants.ACCOUNT_ENTRY_ID_ADMIN))));
		}

		return discountRuleCPDefinitions;
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
		CommerceDiscountRuleCPDefinitionFDSDataProvider.class);

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}