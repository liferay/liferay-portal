/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.DateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.DateRangeDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.FromLastPublishDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.LastHoursDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcessRequest;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ScheduledPublishProcessResourceTest
	extends BaseScheduledPublishProcessResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());

		GroupTestUtil.enableLocalStaging(testGroup);
	}

	@Override
	@Test
	public void testDeleteSiteScheduledPublishProcess() throws Exception {
		ScheduledPublishProcess scheduledPublishProcess =
			_addScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString());

		assertHttpResponseStatusCode(
			204,
			scheduledPublishProcessResource.
				deleteSiteScheduledPublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(),
					scheduledPublishProcess.getId()));

		Page<ScheduledPublishProcess> page =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					testGroup.getExternalReferenceCode(), null,
					Pagination.of(1, 100), null);

		for (ScheduledPublishProcess remainingScheduledPublishProcess :
				page.getItems()) {

			Assert.assertNotEquals(
				scheduledPublishProcess.getId(),
				remainingScheduledPublishProcess.getId());
		}
	}

	@Override
	@Test
	public void testGetSiteScheduledPublishProcess() throws Exception {
		assertHttpResponseStatusCode(
			404,
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(),
					RandomTestUtil.randomLong()));

		ScheduledPublishProcess scheduledPublishProcess =
			_addScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString());

		ScheduledPublishProcess getScheduledPublishProcess =
			scheduledPublishProcessResource.getSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				scheduledPublishProcess.getId());

		Assert.assertEquals(
			scheduledPublishProcess.getName(),
			getScheduledPublishProcess.getName());
		Assert.assertNotNull(getScheduledPublishProcess.getDateCreated());
		Assert.assertNotNull(getScheduledPublishProcess.getScheduleStartDate());

		ScheduledPublishProcessRequest scheduledPublishProcessRequest =
			getScheduledPublishProcess.getScheduledPublishProcessRequest();

		Assert.assertEquals(
			_CRON_EXPRESSION,
			scheduledPublishProcessRequest.getCronExpression());
		Assert.assertEquals(
			getScheduledPublishProcess.getScheduleStartDate(),
			scheduledPublishProcessRequest.getScheduleStartDate());
		Assert.assertEquals(
			"UTC", scheduledPublishProcessRequest.getTimeZoneId());

		PublishProcessRequest publishProcessRequest =
			scheduledPublishProcessRequest.getPublishProcessRequest();

		Assert.assertNull(publishProcessRequest.getDateFilter());
		Assert.assertEquals(
			scheduledPublishProcess.getName(), publishProcessRequest.getName());
	}

	@Override
	@Test
	public void testPostSiteScheduledPublishProcess() throws Exception {
		String publishProcessName = RandomTestUtil.randomString();

		ScheduledPublishProcess scheduledPublishProcess =
			_addScheduledPublishProcess(
				testGroup.getExternalReferenceCode(), publishProcessName);

		Assert.assertNotNull(scheduledPublishProcess.getDateCreated());
		Assert.assertNotNull(scheduledPublishProcess.getId());
		Assert.assertEquals(
			publishProcessName, scheduledPublishProcess.getName());
		Assert.assertNotNull(scheduledPublishProcess.getNextFireDate());
		Assert.assertNotNull(scheduledPublishProcess.getScheduleStartDate());

		ScheduledPublishProcess getScheduledPublishProcess =
			scheduledPublishProcessResource.getSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				scheduledPublishProcess.getId());

		Assert.assertEquals(
			publishProcessName, getScheduledPublishProcess.getName());

		_testPostSiteScheduledPublishProcessWithDateFilter();
		_testPostSiteScheduledPublishProcessWithInvalidCronExpression();
		_testPostSiteScheduledPublishProcessWithInvalidTimeZoneId();
		_testPostSiteScheduledPublishProcessWithOneTimeCronExpression();
		_testPostSiteScheduledPublishProcessWithTreeSelection();
		_testPostSiteScheduledPublishProcessWithoutCronExpression();
		_testPostSiteScheduledPublishProcessWithoutPublishProcessRequest();
		_testPostSiteScheduledPublishProcessWithoutTimeZoneId();
	}

	@Override
	protected ScheduledPublishProcess
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				String siteExternalReferenceCode,
				ScheduledPublishProcess scheduledPublishProcess)
		throws Exception {

		return _addScheduledPublishProcess(
			siteExternalReferenceCode, scheduledPublishProcess.getName());
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteScheduledPublishProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected String
			testGetSiteScheduledPublishProcessesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return null;
	}

	private ScheduledPublishProcess _addScheduledPublishProcess(
			String siteExternalReferenceCode, String publishProcessName)
		throws Exception {

		return scheduledPublishProcessResource.postSiteScheduledPublishProcess(
			siteExternalReferenceCode,
			new ScheduledPublishProcessRequest() {
				{
					cronExpression = _CRON_EXPRESSION;
					publishProcessRequest = _toPublishProcessRequest(
						publishProcessName);
					timeZoneId = "UTC";
				}
			});
	}

	private void _assertScheduledPublishProcess(
			String expectedRange, DateFilter publishProcessDateFilter,
			PortletDataHandler... publishProcessPortletDataHandlers)
		throws Exception {

		ScheduledPublishProcess scheduledPublishProcess =
			scheduledPublishProcessResource.postSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				new ScheduledPublishProcessRequest() {
					{
						cronExpression = _CRON_EXPRESSION;
						publishProcessRequest = new PublishProcessRequest() {
							{
								dateFilter = publishProcessDateFilter;
								name = RandomTestUtil.randomString();
								portletDataHandlers =
									publishProcessPortletDataHandlers;
							}
						};
						timeZoneId = "UTC";
					}
				});

		ScheduledPublishProcessRequest scheduledPublishProcessRequest =
			scheduledPublishProcess.getScheduledPublishProcessRequest();

		PublishProcessRequest publishProcessRequest =
			scheduledPublishProcessRequest.getPublishProcessRequest();

		DateFilter dateFilter = publishProcessRequest.getDateFilter();

		if (publishProcessDateFilter == null) {
			Assert.assertNull(dateFilter);
		}
		else {
			Assert.assertEquals(
				publishProcessDateFilter.getType(), dateFilter.getType());
		}

		if (ArrayUtil.isNotEmpty(publishProcessPortletDataHandlers)) {
			PortletDataHandler portletDataHandler = null;

			for (PortletDataHandler curPortletDataHandler :
					publishProcessRequest.getPortletDataHandlers()) {

				if (Objects.equals(
						curPortletDataHandler.getName(),
						PortletDataHandlerKeys.PORTLET_DATA +
							StringPool.UNDERLINE +
								BlogsPortletKeys.BLOGS_ADMIN)) {

					portletDataHandler = curPortletDataHandler;

					break;
				}
			}

			PortletDataHandlerControl[] portletDataHandlerControls =
				portletDataHandler.getPortletDataHandlerControls();

			PortletDataHandlerBoolean portletDataHandlerBoolean =
				(PortletDataHandlerBoolean)portletDataHandlerControls[0];

			Assert.assertEquals(
				Boolean.TRUE, portletDataHandlerBoolean.getSelected());
		}

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				scheduledPublishProcess.getId());

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		Assert.assertEquals(
			expectedRange, MapUtil.getString(parameterMap, "range"));

		if (expectedRange.equals(
				ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE)) {

			Assert.assertTrue(
				MapUtil.getBoolean(
					parameterMap,
					PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
						ChangesetPortletKeys.CHANGESET));

			Assert.assertFalse(
				MapUtil.getBoolean(
					parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL));

			if (ArrayUtil.isEmpty(publishProcessPortletDataHandlers)) {
				Assert.assertFalse(
					parameterMap.containsKey(BlogsEntry.class.getName()));
			}
			else {
				Assert.assertTrue(
					MapUtil.getBoolean(
						parameterMap, BlogsEntry.class.getName()));
				Assert.assertFalse(
					parameterMap.containsKey(JournalArticle.class.getName()));
			}
		}

		if (expectedRange.equals(ExportImportDateUtil.RANGE_DATE_RANGE)) {
			DateRangeDateFilter dateRangeDateFilter =
				(DateRangeDateFilter)publishProcessDateFilter;

			Assert.assertEquals(
				dateRangeDateFilter.getEndDate() != null,
				parameterMap.containsKey("endDateSecond"));
			Assert.assertEquals(
				dateRangeDateFilter.getEndDate() != null,
				parameterMap.containsKey("endDateYear"));

			Assert.assertTrue(parameterMap.containsKey("startDateSecond"));
			Assert.assertTrue(parameterMap.containsKey("startDateYear"));
		}
		else if (expectedRange.equals(ExportImportDateUtil.RANGE_LAST)) {
			Assert.assertEquals("1", MapUtil.getString(parameterMap, "last"));
		}
	}

	private void _testPostSiteScheduledPublishProcessWithDateFilter()
		throws Exception {

		long time = System.currentTimeMillis();

		_assertScheduledPublishProcess(ExportImportDateUtil.RANGE_ALL, null);
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_DATE_RANGE,
			_toDateRangeDateFilter(
				new Date(time - (2 * Time.HOUR)), new Date(time - Time.HOUR)));
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_DATE_RANGE,
			_toDateRangeDateFilter(new Date(time - (2 * Time.HOUR)), null));
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE,
			_toFromLastPublishDateFilter());
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE,
			_toFromLastPublishDateFilter(),
			new PortletDataHandler() {
				{
					name =
						PortletDataHandlerKeys.PORTLET_DATA +
							StringPool.UNDERLINE + BlogsPortletKeys.BLOGS_ADMIN;
					portletDataHandlerControls =
						new PortletDataHandlerControl[] {
							new PortletDataHandlerBoolean() {
								{
									name =
										com.liferay.exportimport.kernel.lar.
											PortletDataHandlerControl.
												getNamespacedName(
													"blogs", "entries");
									selected = true;
									type =
										PortletDataHandlerControl.Type.BOOLEAN;
								}
							}
						};
				}
			});
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_LAST,
			new LastHoursDateFilter() {
				{
					hours = 1;
					type = Type.LAST_HOURS;
				}
			});
	}

	private void _testPostSiteScheduledPublishProcessWithInvalidCronExpression()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			for (String publishProcessCronExpression :
					new String[] {
						RandomTestUtil.randomString(), "0 30 9 22 8 ? 2020",
						"0 0 12 31 2 ? *"
					}) {

				assertHttpResponseStatusCode(
					400,
					scheduledPublishProcessResource.
						postSiteScheduledPublishProcessHttpResponse(
							testGroup.getExternalReferenceCode(),
							new ScheduledPublishProcessRequest() {
								{
									cronExpression =
										publishProcessCronExpression;
									publishProcessRequest =
										_toPublishProcessRequest(
											RandomTestUtil.randomString());
								}
							}));
			}
		}
	}

	private void _testPostSiteScheduledPublishProcessWithInvalidTimeZoneId()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				scheduledPublishProcessResource.
					postSiteScheduledPublishProcessHttpResponse(
						testGroup.getExternalReferenceCode(),
						new ScheduledPublishProcessRequest() {
							{
								cronExpression = _CRON_EXPRESSION;
								publishProcessRequest =
									_toPublishProcessRequest(
										RandomTestUtil.randomString());
								timeZoneId = RandomTestUtil.randomString();
							}
						}));
		}
	}

	private void _testPostSiteScheduledPublishProcessWithOneTimeCronExpression()
		throws Exception {

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			TimeZoneUtil.getTimeZone("UTC"));

		calendar.clear();

		calendar.set(2099, Calendar.JANUARY, 1, 3, 0, 0);

		String publishProcessName = RandomTestUtil.randomString();

		ScheduledPublishProcess scheduledPublishProcess =
			scheduledPublishProcessResource.postSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				new ScheduledPublishProcessRequest() {
					{
						cronExpression = "0 0 3 1 1 ? 2099";
						publishProcessRequest = _toPublishProcessRequest(
							publishProcessName);
						scheduleStartDate = calendar.getTime();
						timeZoneId = "UTC";
					}
				});

		Assert.assertEquals(
			publishProcessName, scheduledPublishProcess.getName());
		Assert.assertEquals(
			calendar.getTime(), scheduledPublishProcess.getNextFireDate());
		Assert.assertEquals(
			calendar.getTime(), scheduledPublishProcess.getScheduleStartDate());
	}

	private void _testPostSiteScheduledPublishProcessWithTreeSelection()
		throws Exception {

		PortletDataHandlerTreeSelection pagesPortletDataHandlerTreeSelection =
			new PortletDataHandlerTreeSelection() {
				{
					name = "_layout_set_pages";
					selectAll = true;
					selected = true;
					type = PortletDataHandlerControl.Type.TREE_SELECTION;
				}
			};

		PortletDataHandlerChoice visibilityPortletDataHandlerChoice =
			new PortletDataHandlerChoice() {
				{
					choice = "public-pages";
					name = "_layout_set_visibility";
					portletDataHandlerControls =
						new PortletDataHandlerControl[] {
							pagesPortletDataHandlerTreeSelection
						};
					type = PortletDataHandlerControl.Type.CHOICE;
				}
			};

		PortletDataHandler layoutSetPortletDataHandler =
			new PortletDataHandler() {
				{
					name =
						"PORTLET_DATA_" +
							LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;
					portletDataHandlerControls =
						new PortletDataHandlerControl[] {
							visibilityPortletDataHandlerChoice
						};
				}
			};

		ScheduledPublishProcess scheduledPublishProcess =
			scheduledPublishProcessResource.postSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				new ScheduledPublishProcessRequest() {
					{
						cronExpression = _CRON_EXPRESSION;
						publishProcessRequest = new PublishProcessRequest() {
							{
								name = RandomTestUtil.randomString();
								portletDataHandlers = new PortletDataHandler[] {
									layoutSetPortletDataHandler
								};
							}
						};
						timeZoneId = "UTC";
					}
				});

		ScheduledPublishProcessRequest scheduledPublishProcessRequest =
			scheduledPublishProcess.getScheduledPublishProcessRequest();

		PublishProcessRequest publishProcessRequest =
			scheduledPublishProcessRequest.getPublishProcessRequest();

		PortletDataHandler[] portletDataHandlers =
			publishProcessRequest.getPortletDataHandlers();

		PortletDataHandlerChoice portletDataHandlerChoice =
			(PortletDataHandlerChoice)
				portletDataHandlers[0].getPortletDataHandlerControls()[0];

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			(PortletDataHandlerTreeSelection)
				portletDataHandlerChoice.getPortletDataHandlerControls()[0];

		Assert.assertArrayEquals(
			new String[0],
			portletDataHandlerTreeSelection.getItemExternalReferenceCodes());
		Assert.assertTrue(portletDataHandlerTreeSelection.getSelectAll());
	}

	private void _testPostSiteScheduledPublishProcessWithoutCronExpression()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				scheduledPublishProcessResource.
					postSiteScheduledPublishProcessHttpResponse(
						testGroup.getExternalReferenceCode(),
						new ScheduledPublishProcessRequest() {
							{
								publishProcessRequest =
									_toPublishProcessRequest(
										RandomTestUtil.randomString());
							}
						}));
		}
	}

	private void _testPostSiteScheduledPublishProcessWithoutPublishProcessRequest()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				scheduledPublishProcessResource.
					postSiteScheduledPublishProcessHttpResponse(
						testGroup.getExternalReferenceCode(),
						new ScheduledPublishProcessRequest() {
							{
								cronExpression = _CRON_EXPRESSION;
							}
						}));
		}
	}

	private void _testPostSiteScheduledPublishProcessWithoutTimeZoneId()
		throws Exception {

		ScheduledPublishProcess scheduledPublishProcess =
			scheduledPublishProcessResource.postSiteScheduledPublishProcess(
				testGroup.getExternalReferenceCode(),
				new ScheduledPublishProcessRequest() {
					{
						cronExpression = _CRON_EXPRESSION;
						publishProcessRequest = _toPublishProcessRequest(
							RandomTestUtil.randomString());
					}
				});

		ScheduledPublishProcessRequest scheduledPublishProcessRequest =
			scheduledPublishProcess.getScheduledPublishProcessRequest();

		TimeZone timeZone = TimeZoneUtil.getDefault();

		Assert.assertEquals(
			timeZone.getID(), scheduledPublishProcessRequest.getTimeZoneId());
	}

	private DateRangeDateFilter _toDateRangeDateFilter(
		Date dateRangeDateFilterStartDate, Date dateRangeDateFilterEndDate) {

		return new DateRangeDateFilter() {
			{
				endDate = dateRangeDateFilterEndDate;
				startDate = dateRangeDateFilterStartDate;
				type = Type.DATE_RANGE;
			}
		};
	}

	private FromLastPublishDateFilter _toFromLastPublishDateFilter() {
		return new FromLastPublishDateFilter() {
			{
				type = Type.FROM_LAST_PUBLISH_DATE;
			}
		};
	}

	private PublishProcessRequest _toPublishProcessRequest(
		String publishProcessName) {

		return new PublishProcessRequest() {
			{
				name = publishProcessName;
			}
		};
	}

	private static final String _CRON_EXPRESSION = "0 0 3 * * ?";

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

}