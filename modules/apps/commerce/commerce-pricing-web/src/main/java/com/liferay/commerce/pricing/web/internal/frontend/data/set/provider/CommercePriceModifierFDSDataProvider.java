/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.PriceModifier;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.PRICE_MODIFIERS,
	service = FDSDataProvider.class
)
public class CommercePriceModifierFDSDataProvider
	implements FDSDataProvider<PriceModifier> {

	@Override
	public List<PriceModifier> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		long commercePriceListId = ParamUtil.getLong(
			httpServletRequest, "commercePriceListId");

		return TransformUtil.transform(
			_commercePriceModifierService.getCommercePriceModifiers(
				commercePriceListId, fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), null),
			commercePriceModifier -> new PriceModifier(
				_getEndDate(commercePriceModifier, dateTimeFormat),
				_language.get(
					resourceBundle, commercePriceModifier.getModifierType()),
				commercePriceModifier.getTitle(),
				commercePriceModifier.getCommercePriceModifierId(),
				dateTimeFormat.format(commercePriceModifier.getDisplayDate()),
				_language.get(
					resourceBundle, commercePriceModifier.getTarget())));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commercePriceListId = ParamUtil.getLong(
			httpServletRequest, "commercePriceListId");

		return _commercePriceModifierService.getCommercePriceModifiersCount(
			commercePriceListId);
	}

	private String _getEndDate(
		CommercePriceModifier commercePriceModifier, Format dateTimeFormat) {

		if (commercePriceModifier.getExpirationDate() == null) {
			return StringPool.BLANK;
		}

		return dateTimeFormat.format(commercePriceModifier.getExpirationDate());
	}

	@Reference
	private CommercePriceModifierService _commercePriceModifierService;

	@Reference
	private Language _language;

}