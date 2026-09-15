/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelUserSessionsPageResponse;
import com.liferay.osb.faro.rest.resource.v1_0.UserSessionResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-session.properties",
	scope = ServiceScope.PROTOTYPE, service = UserSessionResource.class
)
public class UserSessionResourceImpl extends BaseUserSessionResourceImpl {

	@Override
	public Page<UserSession> getWorkspaceGroupChannelAccountUserSessionsPage(
			Long groupId, String channelId, String accountId, String rangeEnd,
			String rangeKey, String rangeStart, String search,
			Pagination pagination)
		throws Exception {

		return _getUserSessionsPage(
			groupId, accountId, channelId, "", rangeEnd, rangeKey, rangeStart,
			search, pagination);
	}

	@Override
	public Page<UserSession> getWorkspaceGroupChannelIndividualUserSessionsPage(
			Long groupId, String channelId, String individualId,
			String rangeEnd, String rangeKey, String rangeStart, String search,
			Pagination pagination)
		throws Exception {

		return _getUserSessionsPage(
			groupId, null, channelId, individualId, rangeEnd, rangeKey,
			rangeStart, search, pagination);
	}

	private Page<UserSession> _getUserSessionsPage(
			Long groupId, String accountId, String channelId, String entityId,
			String rangeEnd, String rangeKey, String rangeStart, String search,
			Pagination pagination)
		throws Exception {

		int cur = FaroPaginationUtil.getCur(pagination);
		int delta = FaroPaginationUtil.getDelta(pagination);

		GetWorkspaceGroupChannelUserSessionsPageResponse
			getWorkspaceGroupChannelUserSessionsPageResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelUserSessionsPageResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelUserSessionsPage",
					HashMapBuilder.<String, Object>put(
						"accountId", accountId
					).put(
						"channelId", channelId
					).put(
						"entityId", entityId
					).put(
						"entityType", _ENTITY_TYPE_INDIVIDUAL
					).put(
						"keywords", search
					).put(
						"page", Math.max(0, cur - 1)
					).put(
						"rangeEnd", rangeEnd
					).put(
						"rangeKey", TimeRange.getRangeKey(rangeKey)
					).put(
						"rangeStart", rangeStart
					).put(
						"size", delta
					).build());

		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSessionBag
			userSessionBag =
				getWorkspaceGroupChannelUserSessionsPageResponse.
					getUserSessionBag();

		if ((userSessionBag == null) ||
			(userSessionBag.getUserSessions() == null)) {

			return Page.of(Collections.emptyList(), pagination, 0);
		}

		List<UserSession> userSessions = transform(
			userSessionBag.getUserSessions(),
			userSession -> _userSessionDTOConverter.toDTO(
				new FaroDTOConverterContext(
					contextAcceptLanguage.isAcceptAllLanguages(), entityId,
					contextAcceptLanguage.getPreferredLocale()),
				userSession));

		int totalCount = ((cur - 1) * delta) + userSessions.size();

		if (userSessions.size() >= delta) {
			totalCount++;
		}

		return Page.of(userSessions, pagination, totalCount);
	}

	private static final String _ENTITY_TYPE_INDIVIDUAL = "INDIVIDUAL";

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.UserSessionDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession,
		 UserSession> _userSessionDTOConverter;

}