/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.time.Instant;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class GetWorkspaceGroupChannelUserSessionsPageResponseTest {

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testDeserialize() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		JSONObject jsonObject = JSONUtil.put(
			"userSessionBag",
			JSONUtil.put(
				"totalPageGroupsMetric", JSONUtil.put("value", 7.0)
			).put(
				"userSessions",
				JSONUtil.putAll(
					JSONUtil.put(
						"completeDate", "Tue Oct 20 06:52:32 GMT 2026"
					).put(
						"createDate", "Tue Oct 20 06:41:05 GMT 2026"
					))
			));

		GetWorkspaceGroupChannelUserSessionsPageResponse
			getWorkspaceGroupChannelUserSessionsPageResponse =
				objectMapper.readValue(
					jsonObject.toString(),
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