/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class GetWorkspaceGroupChannelUserSessionsPageResponseTest {

	@Test
	public void testDeserialize() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		GetWorkspaceGroupChannelUserSessionsPageResponse
			getWorkspaceGroupChannelUserSessionsPageResponse =
				objectMapper.readValue(
					"{\"userSessionBag\": {\"totalPageGroupsMetric\": " +
						"{\"value\": 7.0}, \"userSessions\": [{" +
							"\"completeDate\": \"Tue Oct 20 06:52:32 GMT " +
								"2026\", \"createDate\": \"Tue Oct 20 " +
									"06:41:05 GMT 2026\"}]}}",
					GetWorkspaceGroupChannelUserSessionsPageResponse.class);

		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSessionBag
			userSessionBag =
				getWorkspaceGroupChannelUserSessionsPageResponse.
					getUserSessionBag();

		GetWorkspaceGroupChannelEventMetricsResponse.Metric
			totalPageGroupsMetric = userSessionBag.getTotalPageGroupsMetric();

		Assert.assertEquals(
			Double.valueOf(7), totalPageGroupsMetric.getValue());

		List<GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession>
			userSessions = userSessionBag.getUserSessions();

		Assert.assertEquals(userSessions.toString(), 1, userSessions.size());

		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			userSession = userSessions.get(0);

		Assert.assertEquals(
			Date.from(Instant.parse("2026-10-20T06:52:32Z")),
			userSession.getCompleteDate());
		Assert.assertEquals(
			Date.from(Instant.parse("2026-10-20T06:41:05Z")),
			userSession.getCreateDate());
	}

}