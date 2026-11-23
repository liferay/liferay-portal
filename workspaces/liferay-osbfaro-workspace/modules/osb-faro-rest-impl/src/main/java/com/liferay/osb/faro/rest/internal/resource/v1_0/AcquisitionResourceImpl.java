/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.Acquisition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAcquisitionsPageResponse;
import com.liferay.osb.faro.rest.resource.v1_0.AcquisitionResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Caio Pinheiro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/acquisition.properties",
	scope = ServiceScope.PROTOTYPE, service = AcquisitionResource.class
)
public class AcquisitionResourceImpl extends BaseAcquisitionResourceImpl {

	@Override
	public Page<Acquisition> getWorkspaceGroupChannelAcquisitionsPage(
			Long groupId, String channelId, String accountId, String rangeEnd,
			String rangeKey, String rangeStart, String segmentId, String type,
			Pagination pagination)
		throws Exception {

		String acquisitionType = type;

		if (Validator.isNull(acquisitionType)) {
			acquisitionType = "CHANNEL";
		}

		GetWorkspaceGroupChannelAcquisitionsPageResponse
			getWorkspaceGroupChannelAcquisitionsPageResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelAcquisitionsPageResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelAcquisitionsPage",
					HashMapBuilder.<String, Object>put(
						"accountId", accountId
					).put(
						"acquisitionType", acquisitionType
					).put(
						"channelId", channelId
					).put(
						"rangeEnd", rangeEnd
					).put(
						"rangeKey", TimeRange.getRangeKey(rangeKey)
					).put(
						"rangeStart", rangeStart
					).put(
						"segmentId", segmentId
					).put(
						"size", FaroPaginationUtil.getDelta(pagination)
					).put(
						"start", FaroPaginationUtil.getGraphQLPage(pagination)
					).build());

		GetWorkspaceGroupChannelAcquisitionsPageResponse.CompositionBag
			compositionBag =
				getWorkspaceGroupChannelAcquisitionsPageResponse.
					getCompositionBag();

		if (compositionBag == null) {
			return Page.of(Collections.emptyList(), pagination, 0);
		}

		return Page.of(
			transform(
				compositionBag.getCompositions(),
				composition -> _acquisitionDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(), null,
						contextAcceptLanguage.getPreferredLocale()),
					composition)),
			pagination, GetterUtil.getLong(compositionBag.getTotalCount()));
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.AcquisitionDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition,
		 Acquisition> _acquisitionDTOConverter;

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}