/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.model.HealthCheck;
import com.liferay.commerce.product.channel.CommerceChannelHealthStatus;
import com.liferay.commerce.product.channel.CommerceChannelHealthStatusRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.CHANNEL_HEALTH_CHECK,
	service = FDSDataProvider.class
)
public class CommerceChannelHealthCheckFDSDataProvider
	implements FDSDataProvider<HealthCheck> {

	@Override
	public List<HealthCheck> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		return TransformUtil.transform(
			_commerceChannelHealthStatusRegistry.
				getCommerceChannelHealthStatuses(),
			commerceChannelHealthStatus -> {
				if (commerceChannelHealthStatus.isFixed(
						commerceChannel.getCompanyId(),
						commerceChannel.getCommerceChannelId())) {

					return null;
				}

				return new HealthCheck(
					commerceChannelHealthStatus.getKey(),
					commerceChannelHealthStatus.getName(
						_portal.getLocale(httpServletRequest)),
					commerceChannelHealthStatus.getDescription(
						_portal.getLocale(httpServletRequest)));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		List<CommerceChannelHealthStatus> commerceChannelHealthStatuses =
			_commerceChannelHealthStatusRegistry.
				getCommerceChannelHealthStatuses();

		int healthStatusToFixCount = 0;

		for (CommerceChannelHealthStatus commerceChannelHealthStatus :
				commerceChannelHealthStatuses) {

			if (!commerceChannelHealthStatus.isFixed(
					commerceChannel.getCompanyId(),
					commerceChannel.getCommerceChannelId())) {

				healthStatusToFixCount++;
			}
		}

		return healthStatusToFixCount;
	}

	@Reference
	private CommerceChannelHealthStatusRegistry
		_commerceChannelHealthStatusRegistry;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private Portal _portal;

}