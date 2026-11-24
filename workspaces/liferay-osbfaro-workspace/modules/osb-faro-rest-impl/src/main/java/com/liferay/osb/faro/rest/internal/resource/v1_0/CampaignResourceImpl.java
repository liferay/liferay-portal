/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.Campaign;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.CampaignResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/campaign.properties",
	scope = ServiceScope.PROTOTYPE, service = CampaignResource.class
)
public class CampaignResourceImpl extends BaseCampaignResourceImpl {

	@Override
	public Campaign getWorkspaceGroupChannelCampaign(
			Long groupId, String channelId, String campaignId)
		throws Exception {

		return _toCampaign(
			_contactsEngineClient.getCampaign(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId), null,
				GetterUtil.getLong(channelId), campaignId));
	}

	@Override
	public Page<Campaign> getWorkspaceGroupChannelCampaignsPage(
			Long groupId, String channelId, String rangeEnd, String rangeKey,
			String rangeStart, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Results<com.liferay.osb.faro.engine.client.model.Campaign> results =
			_contactsEngineClient.getCampaigns(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				GetterUtil.getLong(channelId), null, search, rangeEnd,
				TimeRange.getRangeKey(rangeKey), rangeStart,
				_toSortString(sorts), FaroPaginationUtil.getCur(pagination),
				FaroPaginationUtil.getDelta(pagination));

		return Page.of(
			transform(results.getItems(), this::_toCampaign), pagination,
			results.getTotal());
	}

	private Campaign _toCampaign(
			com.liferay.osb.faro.engine.client.model.Campaign campaign)
		throws Exception {

		return _campaignDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), campaign.getId(),
				contextAcceptLanguage.getPreferredLocale()),
			campaign);
	}

	private String _toSortString(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return null;
		}

		Sort sort = sorts[0];

		String fieldName = sort.getFieldName();

		if (fieldName == null) {
			return null;
		}

		if (fieldName.equals("name")) {
			fieldName = "campaignName";
		}

		String order = "asc";

		if (sort.isReverse()) {
			order = "desc";
		}

		return StringBundler.concat(fieldName, StringPool.COLON, order);
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.CampaignDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.Campaign, Campaign>
			_campaignDTOConverter;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}