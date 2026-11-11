/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.portlet.action;

import com.liferay.osb.faro.admin.web.internal.util.FaroProjectOwnerUtil;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.BigQueryPrice;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroProjectUsageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class ExportCostReportMVCResourceCommandTest {

	@Before
	public void setUp() {
		BigQueryPrice bigQueryPrice = new BigQueryPrice();

		bigQueryPrice.setActiveLogicalStoragePricePerGiBMonth(
			new BigDecimal("0.02"));
		bigQueryPrice.setLocation("us-west1");
		bigQueryPrice.setLongTermLogicalStoragePricePerGiBMonth(
			new BigDecimal("0.01"));
		bigQueryPrice.setOnDemandPricePerTiB(new BigDecimal("6.25"));
		bigQueryPrice.setReservationPricePerSlotHour(new BigDecimal("0.06"));

		Mockito.when(
			_contactsEngineClient.getBigQueryPrice(
				Mockito.any(FaroProject.class))
		).thenReturn(
			bigQueryPrice
		);

		Mockito.when(
			_faroProjectUsageLocalService.dynamicQuery()
		).thenReturn(
			_queryUsageTimesDynamicQuery, _storageUsageTimesDynamicQuery,
			_totalsDynamicQuery
		);

		ReflectionTestUtil.setFieldValue(
			_exportCostReportMVCResourceCommand, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtil.setFieldValue(
			_exportCostReportMVCResourceCommand, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_exportCostReportMVCResourceCommand,
			"_faroProjectUsageLocalService", _faroProjectUsageLocalService);
	}

	@Test
	public void testDoServeResource() throws Exception {
		String corpProjectName = RandomTestUtil.randomString();
		String corpProjectUuid = RandomTestUtil.randomString();
		long faroProjectId = RandomTestUtil.randomLong();
		String name = RandomTestUtil.randomString();
		String owner = RandomTestUtil.randomString();
		String projectId = RandomTestUtil.randomString();
		String shortSubscriptionName = RandomTestUtil.randomString();

		FaroProject faroProject = _mockFaroProject(
			corpProjectName, corpProjectUuid, faroProjectId, name, projectId,
			shortSubscriptionName);

		_mockDynamicQuery(
			_queryUsageTimesDynamicQuery,
			new Object[] {faroProjectId, _getTime("2026-05-14")});
		_mockDynamicQuery(
			_storageUsageTimesDynamicQuery,
			new Object[] {faroProjectId, _getTime("2026-11-03")});
		_mockDynamicQuery(
			_totalsDynamicQuery,
			new Object[] {
				faroProjectId, (1L << 30) * 365, 200L, 700L, 1000L, 300L, 100L,
				(1L << 30) * 365 * 2, 1L << 40, 500L, 360000000L
			});

		Assert.assertEquals(
			StringBundler.concat(
				_HEADER, name, ",", projectId, ",", corpProjectUuid, ",",
				corpProjectName, ",", owner, ",", shortSubscriptionName,
				",us-west1,1000,700,300,500,200,100,12.25,2026-05-14,0.48,",
				"2026-11-03,12.73\n"),
			_serveResource(faroProject, owner));
	}

	@Test
	public void testDoServeResourceWithoutUsage() throws Exception {
		long faroProjectId = RandomTestUtil.randomLong();
		String name = RandomTestUtil.randomString();
		String projectId = RandomTestUtil.randomString();
		String shortSubscriptionName = RandomTestUtil.randomString();

		FaroProject faroProject = _mockFaroProject(
			null, null, faroProjectId, name, projectId, shortSubscriptionName);

		_mockDynamicQuery(_queryUsageTimesDynamicQuery);
		_mockDynamicQuery(_storageUsageTimesDynamicQuery);
		_mockDynamicQuery(
			_totalsDynamicQuery,
			new Object[] {
				RandomTestUtil.randomLong(), 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L,
				1L, 1L
			},
			new Object[] {
				faroProjectId, null, null, null, null, null, null, null, null,
				null, null
			});

		Assert.assertEquals(
			StringBundler.concat(
				_HEADER, name, ",", projectId, ",,,,", shortSubscriptionName,
				",us-west1,0,0,0,0,0,0,0.00,,0.00,,0.00\n"),
			_serveResource(faroProject, null));
	}

	private long _getTime(String dateString) {
		LocalDate localDate = LocalDate.parse(dateString);

		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC);

		return zonedDateTime.toEpochSecond() * 1000;
	}

	private void _mockDynamicQuery(
		DynamicQuery dynamicQuery, Object[]... valuesArray) {

		List<Object[]> valuesList = Collections.emptyList();

		if (valuesArray.length > 0) {
			valuesList = List.of(valuesArray);
		}

		Mockito.doReturn(
			valuesList
		).when(
			_faroProjectUsageLocalService
		).dynamicQuery(
			dynamicQuery
		);
	}

	private FaroProject _mockFaroProject(
			String corpProjectName, String corpProjectUuid, long faroProjectId,
			String name, String projectId, String shortSubscriptionName)
		throws Exception {

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getCorpProjectName()
		).thenReturn(
			corpProjectName
		);

		Mockito.when(
			faroProject.getCorpProjectUuid()
		).thenReturn(
			corpProjectUuid
		);

		Mockito.when(
			faroProject.getFaroProjectId()
		).thenReturn(
			faroProjectId
		);

		Mockito.when(
			faroProject.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			faroProject.getProjectId()
		).thenReturn(
			projectId
		);

		Mockito.when(
			faroProject.getServerLocation()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			faroProject.getShortSubscriptionName()
		).thenReturn(
			shortSubscriptionName
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProject(faroProjectId)
		).thenReturn(
			faroProject
		);

		return faroProject;
	}

	private String _serveResource(FaroProject faroProject, String owner)
		throws Exception {

		ArgumentCaptor<byte[]> bytesArgumentCaptor = ArgumentCaptor.forClass(
			byte[].class);

		try (MockedStatic<FaroProjectOwnerUtil>
				faroProjectOwnerUtilMockedStatic = Mockito.mockStatic(
					FaroProjectOwnerUtil.class);
			MockedStatic<PortletResponseUtil> portletResponseUtilMockedStatic =
				Mockito.mockStatic(PortletResponseUtil.class);
			MockedStatic<ProjectionFactoryUtil>
				projectionFactoryUtilMockedStatic = Mockito.mockStatic(
					ProjectionFactoryUtil.class, Mockito.RETURNS_MOCKS);
			MockedStatic<PropertyFactoryUtil> propertyFactoryUtilMockedStatic =
				Mockito.mockStatic(
					PropertyFactoryUtil.class, Mockito.RETURNS_MOCKS);
			MockedStatic<RestrictionsFactoryUtil>
				restrictionsFactoryUtilMockedStatic = Mockito.mockStatic(
					RestrictionsFactoryUtil.class, Mockito.RETURNS_MOCKS)) {

			faroProjectOwnerUtilMockedStatic.when(
				() -> FaroProjectOwnerUtil.fetchOwner(faroProject)
			).thenReturn(
				owner
			);

			ResourceRequest resourceRequest = Mockito.mock(
				ResourceRequest.class);
			ResourceResponse resourceResponse = Mockito.mock(
				ResourceResponse.class);

			_exportCostReportMVCResourceCommand.doServeResource(
				resourceRequest, resourceResponse);

			portletResponseUtilMockedStatic.verify(
				() -> PortletResponseUtil.sendFile(
					Mockito.eq(resourceRequest), Mockito.eq(resourceResponse),
					Mockito.eq("cost-report.csv"), bytesArgumentCaptor.capture(),
					Mockito.eq(ContentTypes.TEXT_CSV_UTF8)));
		}

		return new String(bytesArgumentCaptor.getValue(), StringPool.UTF8);
	}

	private static final String _HEADER = StringBundler.concat(
		"Workspace,Project ID,Customer ID,Customer Name,Owner,Subscription,",
		"Location,Events,Billable Events,A/B Testing Events,Page Views,",
		"Anonymous Individuals,Known Individuals,Query Cost (USD),",
		"Query Cost Data Since,Storage Cost (USD),Storage Cost Data Since,",
		"Total Cost (USD)\n");

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final ExportCostReportMVCResourceCommand
		_exportCostReportMVCResourceCommand =
			new ExportCostReportMVCResourceCommand();
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final FaroProjectUsageLocalService _faroProjectUsageLocalService =
		Mockito.mock(FaroProjectUsageLocalService.class);
	private final DynamicQuery _queryUsageTimesDynamicQuery = Mockito.mock(
		DynamicQuery.class);
	private final DynamicQuery _storageUsageTimesDynamicQuery = Mockito.mock(
		DynamicQuery.class);
	private final DynamicQuery _totalsDynamicQuery = Mockito.mock(
		DynamicQuery.class);

}