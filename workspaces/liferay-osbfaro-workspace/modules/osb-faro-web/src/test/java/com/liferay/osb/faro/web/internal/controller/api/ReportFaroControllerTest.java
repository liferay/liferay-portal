/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.api;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.DateUtil;
import com.liferay.osb.faro.web.internal.context.GroupInfo;

import jakarta.ws.rs.core.Response;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Eudaldo Alonso
 */
public class ReportFaroControllerTest {

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(
			_reportFaroController, "contactsEngineClient",
			_contactsEngineClient);

		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(Mockito.anyLong())
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtils.setField(
			_reportFaroController, "faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetWithInternalProblemInExportProcess() throws Exception {
		GroupInfo groupInfo = Mockito.mock(GroupInfo.class);

		Mockito.when(
			groupInfo.getGroupId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_contactsEngineClient.get(
				Mockito.eq(_faroProject), Mockito.anyMap(), Mockito.anyString(),
				Mockito.anyMap(), Mockito.eq(Map.class))
		).thenThrow(
			new RuntimeException()
		);

		Response response = (Response)_reportFaroController.get(
			"2020-01-01T12:00:00.000Z", groupInfo, "2020-01-31T12:00:00.000Z",
			"page");

		Assert.assertEquals(
			Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			response.getStatus());

		Map<String, String> entityMap =
			(Map<String, String>)response.getEntity();

		Assert.assertEquals(
			"An internal problem happened when trying to reach our services",
			entityMap.get("message"));
		Assert.assertEquals("ERROR", entityMap.get("status"));
	}

	@Test
	public void testGetWithInvalidType() throws Exception {
		Response response = (Response)_reportFaroController.get(
			"2020-01-01T12:00:00.000Z", null, "2020-01-31T12:00:00.000Z",
			"accounts");

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

		Map<String, String> entityMap =
			(Map<String, String>)response.getEntity();

		Assert.assertEquals(
			"The \"type\" query parameter must be either \"event\", " +
				"\"identity\", \"individual\", \"membership\", \"page\", or " +
					"\"segment\".",
			entityMap.get("message"));
		Assert.assertEquals("ERROR", entityMap.get("status"));
	}

	@Test
	public void testGetWithWrongDateFormat() throws Exception {
		Response response = (Response)_reportFaroController.get(
			"2022-02-01", null, "2022-05-16", "page");

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

		Map<String, String> entityMap =
			(Map<String, String>)response.getEntity();

		Assert.assertEquals(
			"Both dates in range must be ISO 8601 compliant " +
				DateUtil.PATTERN_DATE_TIME,
			entityMap.get("message"));
		Assert.assertEquals("ERROR", entityMap.get("status"));
	}

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final ReportFaroController _reportFaroController =
		new ReportFaroController();

}