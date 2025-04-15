/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.model.Channel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.CHANNEL,
	service = FDSDataProvider.class
)
public class CommerceChannelFDSDataProvider
	implements FDSDataProvider<Channel> {

	@Override
	public List<Channel> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			_portal.getCompanyId(httpServletRequest), fdsKeywords.getKeywords(),
			fdsPagination.getStartPosition(), fdsPagination.getEndPosition(),
			sort);

		return TransformUtil.transform(
			commerceChannels,
			commerceChannel -> new Channel(
				commerceChannel.getCommerceChannelId(),
				commerceChannel.getName(),
				_language.get(httpServletRequest, commerceChannel.getType())));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		return _commerceChannelService.searchCommerceChannelsCount(
			_portal.getCompanyId(httpServletRequest),
			fdsKeywords.getKeywords());
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}