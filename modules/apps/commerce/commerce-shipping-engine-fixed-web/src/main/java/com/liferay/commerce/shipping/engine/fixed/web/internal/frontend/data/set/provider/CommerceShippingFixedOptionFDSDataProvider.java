/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.frontend.data.set.provider;

import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.commerce.shipping.engine.fixed.util.comparator.CommerceShippingFixedOptionPriorityComparator;
import com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionFDSNames;
import com.liferay.commerce.shipping.engine.fixed.web.internal.model.ShippingFixedOption;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShippingFixedOptionFDSNames.SHIPPING_FIXED_OPTIONS,
	service = FDSDataProvider.class
)
public class CommerceShippingFixedOptionFDSDataProvider
	implements FDSDataProvider<ShippingFixedOption> {

	@Override
	public List<ShippingFixedOption> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceShippingMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceShippingMethodId");

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.getCommerceShippingMethod(
				commerceShippingMethodId);

		return TransformUtil.transform(
			ListUtil.sort(
				_commerceShippingFixedOptionService.
					getCommerceShippingFixedOptions(
						themeDisplay.getCompanyId(),
						commerceShippingMethod.getGroupId(),
						commerceShippingMethodId, fdsKeywords.getKeywords(),
						fdsPagination.getStartPosition(),
						fdsPagination.getEndPosition()),
				CommerceShippingFixedOptionPriorityComparator.getInstance(
					sort.isReverse())),
			commerceShippingFixedOption -> new ShippingFixedOption(
				HtmlUtil.escape(
					commerceShippingFixedOption.getDescription(
						themeDisplay.getLocale())),
				HtmlUtil.escape(
					commerceShippingFixedOption.getName(
						themeDisplay.getLocale())),
				commerceShippingFixedOption.getPriority(),
				commerceShippingFixedOption.
					getCommerceShippingFixedOptionId()));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceShippingMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceShippingMethodId");

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.getCommerceShippingMethod(
				commerceShippingMethodId);

		List<CommerceShippingFixedOption> commerceShippingFixedOptions =
			_commerceShippingFixedOptionService.getCommerceShippingFixedOptions(
				commerceShippingMethod.getCompanyId(),
				commerceShippingMethod.getGroupId(), commerceShippingMethodId,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return commerceShippingFixedOptions.size();
	}

	@Reference
	private CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

}