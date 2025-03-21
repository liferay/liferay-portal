/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PricingClass;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICING_CLASSES,
	service = FDSDataProvider.class
)
public class CommercePricingClassFDSDataProvider
	implements FDSDataProvider<PricingClass> {

	@Override
	public List<PricingClass> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return TransformUtil.transform(
			_getCommercePricingClasses(
				themeDisplay.getCompanyId(), fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), sort),
			commercePricingClass -> new PricingClass(
				commercePricingClass.getCommercePricingClassId(),
				commercePricingClass.getTitle(themeDisplay.getLocale()),
				_commercePricingClassCPDefinitionRelService.
					getCommercePricingClassCPDefinitionRelsCount(
						commercePricingClass.getCommercePricingClassId()),
				dateTimeFormat.format(
					commercePricingClass.getLastPublishDate())));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String keywords = fdsKeywords.getKeywords();

		if (Validator.isNotNull(keywords)) {
			BaseModelSearchResult<CommercePricingClass> baseModelSearchResult =
				_getBaseModelSearchResult(
					themeDisplay.getCompanyId(), keywords, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			return baseModelSearchResult.getLength();
		}

		return _commercePricingClassService.getCommercePricingClassesCount(
			themeDisplay.getCompanyId());
	}

	private BaseModelSearchResult<CommercePricingClass>
			_getBaseModelSearchResult(
				long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		return _commercePricingClassService.searchCommercePricingClasses(
			companyId, keywords, start, end, sort);
	}

	private List<CommercePricingClass> _getCommercePricingClasses(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		if (Validator.isNotNull(keywords)) {
			BaseModelSearchResult<CommercePricingClass> baseModelSearchResult =
				_getBaseModelSearchResult(
					companyId, keywords, start, end, sort);

			return baseModelSearchResult.getBaseModels();
		}

		return _commercePricingClassService.getCommercePricingClasses(
			companyId, start, end, null);
	}

	@Reference
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

}