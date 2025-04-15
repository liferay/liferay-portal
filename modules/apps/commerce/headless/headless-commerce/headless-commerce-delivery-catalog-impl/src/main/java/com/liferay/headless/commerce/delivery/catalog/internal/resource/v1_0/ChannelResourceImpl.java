/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Channel;
import com.liferay.headless.commerce.delivery.catalog.internal.odata.entity.v1_0.ChannelEntityModel;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ChannelResource;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Crescenzo Rega
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/channel.properties",
	scope = ServiceScope.PROTOTYPE, service = ChannelResource.class
)
public class ChannelResourceImpl extends BaseChannelResourceImpl {

	@Override
	public Page<Channel> getChannelsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceChannel.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toChannel(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	private Channel _toChannel(Long commerceChannelId) throws Exception {
		return _channelDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, commerceChannelId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private static final EntityModel _entityModel = new ChannelEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ChannelDTOConverter)"
	)
	private DTOConverter<CommerceChannel, Channel> _channelDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

}