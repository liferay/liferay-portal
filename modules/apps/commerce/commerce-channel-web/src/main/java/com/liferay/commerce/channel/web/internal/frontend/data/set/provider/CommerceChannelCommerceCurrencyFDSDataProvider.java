/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.model.ChannelCurrency;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
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
 * @author Fabio Monaco
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.COMMERCE_CURRENCIES,
	service = FDSDataProvider.class
)
public class CommerceChannelCommerceCurrencyFDSDataProvider
	implements FDSDataProvider<ChannelCurrency> {

	@Override
	public List<ChannelCurrency> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		return TransformUtil.transform(
			_commerceChannelRelService.getCommerceCurrencyCommerceChannelRels(
				commerceChannelId, fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commerceChannelRel -> {
				CommerceCurrency commerceCurrency =
					_commerceCurrencyService.getCommerceCurrency(
						commerceChannelRel.getClassPK());

				if (commerceCurrency == null) {
					return null;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return new ChannelCurrency(
					commerceChannelId,
					commerceChannelRel.getCommerceChannelRelId(),
					commerceCurrency.getCode(),
					commerceCurrency.getCommerceCurrencyId(),
					commerceCurrency.getName(themeDisplay.getLocale()),
					commerceCurrency.getSymbol());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		return _commerceChannelRelService.
			getCommerceCurrencyCommerceChannelRelsCount(
				commerceChannelId, fdsKeywords.getKeywords());
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

}