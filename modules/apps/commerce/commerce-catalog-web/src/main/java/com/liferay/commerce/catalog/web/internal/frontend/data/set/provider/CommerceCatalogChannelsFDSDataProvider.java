/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.frontend.data.set.provider;

import com.liferay.commerce.catalog.web.internal.constants.CommerceCatalogFDSNames;
import com.liferay.commerce.catalog.web.internal.model.Channel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "fds.data.provider.key=" + CommerceCatalogFDSNames.CATALOG_CHANNELS,
	service = FDSDataProvider.class
)
public class CommerceCatalogChannelsFDSDataProvider
	implements FDSDataProvider<Channel> {

	@Override
	public List<Channel> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceCatalogId = ParamUtil.getLong(
			httpServletRequest, "commerceCatalogId");

		List<CommerceChannelRel> commerceChannels =
			_commerceChannelRelService.getCommerceChannelRels(
				CommerceCatalog.class.getName(), commerceCatalogId, null,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition());

		return TransformUtil.transform(
			commerceChannels,
			commerceChannelRel -> {
				CommerceChannel commerceChannel =
					commerceChannelRel.getCommerceChannel();

				return new Channel(
					commerceChannel.getCommerceChannelId(),
					commerceChannel.getName(), commerceChannel.getType());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceCatalogId = ParamUtil.getLong(
			httpServletRequest, "commerceCatalogId");

		return _commerceChannelRelService.getCommerceChannelRelsCount(
			CommerceCatalog.class.getName(), commerceCatalogId);
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

}