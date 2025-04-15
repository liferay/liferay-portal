/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.model.ChannelCountry;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.CHANNEL_COUNTRIES,
	service = FDSDataProvider.class
)
public class CommerceChannelCountryFDSDataProvider
	implements FDSDataProvider<ChannelCountry> {

	@Override
	public List<ChannelCountry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		return TransformUtil.transform(
			_commerceChannelRelService.getCountryCommerceChannelRels(
				commerceChannelId, fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commerceChannelRel -> {
				Country country = _countryLocalService.fetchCountry(
					commerceChannelRel.getClassPK());

				if (country == null) {
					return null;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return new ChannelCountry(
					commerceChannelId,
					commerceChannelRel.getCommerceChannelRelId(),
					country.getA2(), country.getA3(), country.getCountryId(),
					country.getName(themeDisplay.getLocale()));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		return _commerceChannelRelService.getCountryCommerceChannelRelsCount(
			commerceChannelId, fdsKeywords.getKeywords());
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CountryLocalService _countryLocalService;

}