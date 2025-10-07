/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.system.event.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.SystemEventLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class SystemEventCheckTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	public void doTestSystemEventCheck() throws Exception {
		_group = GroupTestUtil.addGroup();

		Calendar calendar = Calendar.getInstance();

		calendar.add(
			Calendar.HOUR, -PropsValues.STAGING_SYSTEM_EVENT_MAX_AGE - 1);

		Date createDate = calendar.getTime();

		List<SystemEvent> expiredSystemEvents = new LinkedList<>();

		for (int i = 0; i < 5; i++) {
			SystemEvent systemEvent = addSystemEvent();

			systemEvent.setCreateDate(createDate);

			systemEvent = SystemEventLocalServiceUtil.updateSystemEvent(
				systemEvent);

			expiredSystemEvents.add(systemEvent);
		}

		validate(expiredSystemEvents, false);

		List<SystemEvent> nonexpiredSystemEvents = new LinkedList<>();

		for (int i = 0; i < 7; i++) {
			nonexpiredSystemEvents.add(addSystemEvent());
		}

		validate(nonexpiredSystemEvents, false);

		SystemEventLocalServiceUtil.checkSystemEvents();

		if (PropsValues.STAGING_SYSTEM_EVENT_MAX_AGE <= 0) {
			validate(expiredSystemEvents, false);
		}
		else {
			validate(expiredSystemEvents, true);
		}

		validate(nonexpiredSystemEvents, false);
	}

	@Test
	public void testSystemEventCheckWithMaxAge0() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"STAGING_SYSTEM_EVENT_MAX_AGE", 0)) {

			doTestSystemEventCheck();
		}
	}

	@Test
	public void testSystemEventCheckWithMaxAge1() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"STAGING_SYSTEM_EVENT_MAX_AGE", 1)) {

			doTestSystemEventCheck();
		}
	}

	protected SystemEvent addSystemEvent() throws Exception {
		return SystemEventLocalServiceUtil.addSystemEvent(
			TestPropsValues.getUserId(), _group.getGroupId(), StringPool.BLANK,
			Group.class.getName(), RandomTestUtil.nextLong(),
			PortalUUIDUtil.generate(), StringPool.BLANK,
			SystemEventConstants.TYPE_DELETE, StringPool.BLANK);
	}

	protected void validate(List<SystemEvent> systemEvents, boolean missing) {
		for (SystemEvent systemEvent : systemEvents) {
			SystemEvent actualSystemEvent =
				SystemEventLocalServiceUtil.fetchSystemEvent(
					_group.getGroupId(), PortalUtil.getClassNameId(Group.class),
					systemEvent.getClassPK(), SystemEventConstants.TYPE_DELETE);

			if (missing) {
				Assert.assertEquals(null, actualSystemEvent);
			}
			else {
				Assert.assertEquals(systemEvent, actualSystemEvent);
			}
		}
	}

	@DeleteAfterTestRun
	private Group _group;

}