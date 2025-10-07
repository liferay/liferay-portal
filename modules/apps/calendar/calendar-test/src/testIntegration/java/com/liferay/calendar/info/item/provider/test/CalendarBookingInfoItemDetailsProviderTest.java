/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class CalendarBookingInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_calendarBooking = CalendarBookingTestUtil.addRegularCalendarBooking(
			CalendarTestUtil.addCalendar(_group));
	}

	@Test
	public void testGetInfoItemDetails() throws Exception {
		InfoItemDetailsProvider<CalendarBooking> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, CalendarBooking.class.getName());

		InfoItemDetails classPKInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_calendarBooking);

		Assert.assertEquals(
			CalendarBooking.class.getName(),
			classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CalendarBooking.class.getName(),
				_calendarBooking.getCalendarBookingId()),
			classPKInfoItemDetails.getInfoItemReference());

		InfoItemDetails ercInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ERCInfoItemIdentifier.class,
				_calendarBooking);

		Assert.assertEquals(
			new InfoItemReference(
				CalendarBooking.class.getName(),
				new ERCInfoItemIdentifier(
					_calendarBooking.getExternalReferenceCode())),
			ercInfoItemDetails.getInfoItemReference());

		InfoItemDetails randomGroupERCInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
				_calendarBooking);

		Assert.assertEquals(
			new InfoItemReference(
				CalendarBooking.class.getName(),
				new ERCInfoItemIdentifier(
					_calendarBooking.getExternalReferenceCode(),
					_group.getExternalReferenceCode())),
			randomGroupERCInfoItemDetails.getInfoItemReference());

		Assert.assertNull(
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupKeyInfoItemIdentifier.class,
				_calendarBooking));
		Assert.assertNull(
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupUrlTitleInfoItemIdentifier.class,
				_calendarBooking));
	}

	private CalendarBooking _calendarBooking;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}