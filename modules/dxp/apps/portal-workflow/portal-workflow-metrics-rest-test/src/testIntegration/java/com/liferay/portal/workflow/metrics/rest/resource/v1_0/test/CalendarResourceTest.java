/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Calendar;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;
import com.liferay.portal.workflow.metrics.sla.calendar.WorkflowMetricsSLACalendar;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CalendarResourceTest extends BaseCalendarResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseCalendarResourceTestCase.setUpClass();

		Bundle bundle = FrameworkUtil.getBundle(
			WorkflowMetricsRESTTestHelper.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_serviceRegistration == null) {
			return;
		}

		_serviceRegistration.unregister();
	}

	@Override
	@Test
	public void testGetCalendarsPage() throws Exception {
		Page<Calendar> calendarsPage = calendarResource.getCalendarsPage();

		List<Calendar> calendars = (List<Calendar>)calendarsPage.getItems();

		Assert.assertEquals(calendars.toString(), 1, calendars.size());

		assertEquals(_getDefaultCalendar(), calendars.get(0));

		_registerCustomCalendar();

		calendarsPage = calendarResource.getCalendarsPage();

		calendars = (List<Calendar>)calendarsPage.getItems();

		Assert.assertEquals(calendars.toString(), 2, calendars.size());

		assertEqualsIgnoringOrder(
			Arrays.asList(_getDefaultCalendar(), _getCustomCalendar()),
			calendars);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"defaultCalendar", "key", "title"};
	}

	private Calendar _getCustomCalendar() {
		return new Calendar() {
			{
				defaultCalendar = false;
				key = "custom";
				title = "Custom";
			}
		};
	}

	private Calendar _getDefaultCalendar() {
		return new Calendar() {
			{
				defaultCalendar = true;
				key = "default";
				title = "24/7";
			}
		};
	}

	private void _registerCustomCalendar() {
		_serviceRegistration = _bundleContext.registerService(
			WorkflowMetricsSLACalendar.class,
			new WorkflowMetricsSLACalendar() {

				@Override
				public Duration getDuration(
					LocalDateTime startLocalDateTime,
					LocalDateTime endLocalDateTime) {

					return Duration.ZERO;
				}

				@Override
				public String getKey() {
					return "custom";
				}

				@Override
				public LocalDateTime getOverdueLocalDateTime(
					LocalDateTime nowLocalDateTime,
					Duration remainingDuration) {

					return nowLocalDateTime;
				}

				@Override
				public String getTitle(Locale locale) {
					return "Custom";
				}

			},
			null);
	}

	private static BundleContext _bundleContext;

	private ServiceRegistration<WorkflowMetricsSLACalendar>
		_serviceRegistration;

}