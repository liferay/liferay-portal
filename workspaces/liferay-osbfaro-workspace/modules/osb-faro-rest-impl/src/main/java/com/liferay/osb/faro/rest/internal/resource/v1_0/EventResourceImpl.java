/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse;
import com.liferay.osb.faro.rest.resource.v1_0.EventResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/event.properties",
	scope = ServiceScope.PROTOTYPE, service = EventResource.class
)
public class EventResourceImpl extends BaseEventResourceImpl {

	@Override
	public Page<Event> getWorkspaceGroupChannelEventsPage(
			Long groupId, String channelId, String accountId,
			Boolean includeAnonymousUsers, String individualId, String rangeEnd,
			String rangeKey, String rangeStart, String search, String segmentId,
			Pagination pagination)
		throws Exception {

		GetWorkspaceGroupChannelEventsPageResponse
			getWorkspaceGroupChannelEventsPageResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelEventsPageResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelEventsPage",
					HashMapBuilder.<String, Object>put(
						"accountId", accountId
					).put(
						"channelId", channelId
					).put(
						"includeAnonymousUsers", includeAnonymousUsers
					).put(
						"individualId", individualId
					).put(
						"keywords", search
					).put(
						"page",
						Math.max(0, FaroPaginationUtil.getCur(pagination) - 1)
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
					).build());

		GetWorkspaceGroupChannelEventsPageResponse.EventBag eventBag =
			getWorkspaceGroupChannelEventsPageResponse.getEventBag();

		if (eventBag == null) {
			return Page.of(Collections.emptyList(), pagination, 0);
		}

		Integer total = eventBag.getTotal();

		if (total == null) {
			total = 0;
		}

		return Page.of(
			transform(
				eventBag.getEvents(),
				event -> _eventDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(), null,
						contextAcceptLanguage.getPreferredLocale()),
					event)),
			pagination, total);
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.EventDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelEventsPageResponse.Event, Event>
			_eventDTOConverter;

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}