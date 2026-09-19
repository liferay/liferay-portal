/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.test.util.CalendarUpgradeTestUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.model.impl.PortalPreferenceValueImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class UpgradePortalPreferencesTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_portalPreferences =
			_portalPreferencesLocalService.addPortalPreferences(
				_user.getUserId(), PortletKeys.PREFS_OWNER_TYPE_USER, "");

		setUpUpgradePortalPreferences();

		_portalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			"com.liferay.portal.internal.service.util." +
				"PortalPreferencesCacheUtil");
	}

	@Test
	public void testUpgradeCalendarColorPreferences() throws Exception {
		long calendarId = RandomTestUtil.randomLong();
		long color = RandomTestUtil.randomLong();

		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				_portalPreferences, true);

		portalPreferences.setValue(
			_NAMESPACE_OLD_SESSION_CLICKS,
			"calendar-portlet-calendar-" + calendarId + "-color",
			String.valueOf(color));

		_upgradeProcess.upgrade();

		portalPreferences = reloadPortalPreferences();

		Assert.assertEquals(
			String.valueOf(color),
			portalPreferences.getValue(
				_NAMESPACE_NEW_SESSION_CLICKS,
				"com.liferay.calendar.web_calendar" + calendarId + "Color"));
	}

	@Test
	public void testUpgradeCalendarVisiblePreferences() throws Exception {
		long calendarId = RandomTestUtil.randomLong();
		boolean visible = RandomTestUtil.randomBoolean();

		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				_portalPreferences, true);

		portalPreferences.setValue(
			_NAMESPACE_OLD_SESSION_CLICKS,
			"calendar-portlet-calendar-" + calendarId + "-visible",
			String.valueOf(visible));

		_upgradeProcess.upgrade();

		portalPreferences = reloadPortalPreferences();

		Assert.assertEquals(
			String.valueOf(visible),
			portalPreferences.getValue(
				_NAMESPACE_NEW_SESSION_CLICKS,
				"com.liferay.calendar.web_calendar" + calendarId + "Visible"));
	}

	@Test
	public void testUpgradeColumnOptionsVisiblePreferences() throws Exception {
		boolean visible = RandomTestUtil.randomBoolean();

		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				_portalPreferences, true);

		portalPreferences.setValue(
			_NAMESPACE_OLD_SESSION_CLICKS,
			"calendar-portlet-column-options-visible", String.valueOf(visible));

		_upgradeProcess.upgrade();

		portalPreferences = reloadPortalPreferences();

		Assert.assertEquals(
			String.valueOf(visible),
			portalPreferences.getValue(
				_NAMESPACE_NEW_SESSION_CLICKS,
				"com.liferay.calendar.web_columnOptionsVisible"));
	}

	@Test
	public void testUpgradeDefaultViewPreferences() throws Exception {
		String[] views = {"day", "month", "week", "agenda"};

		String view = views[RandomTestUtil.randomInt(0, views.length - 1)];

		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				_portalPreferences, true);

		portalPreferences.setValue(
			_NAMESPACE_OLD_SESSION_CLICKS, "calendar-portlet-default-view",
			view);

		_upgradeProcess.upgrade();

		portalPreferences = reloadPortalPreferences();

		Assert.assertEquals(
			view,
			portalPreferences.getValue(
				_NAMESPACE_NEW_SESSION_CLICKS,
				"com.liferay.calendar.web_defaultView"));
	}

	@Test
	public void testUpgradeOtherCalendarsPreferences() throws Exception {
		long otherCalendarId = RandomTestUtil.randomLong();

		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				_portalPreferences, true);

		portalPreferences.setValue(
			_NAMESPACE_OLD_SESSION_CLICKS, "calendar-portlet-other-calendars",
			String.valueOf(otherCalendarId));

		_upgradeProcess.upgrade();

		portalPreferences = reloadPortalPreferences();

		Assert.assertEquals(
			String.valueOf(otherCalendarId),
			portalPreferences.getValue(
				_NAMESPACE_NEW_SESSION_CLICKS,
				"com.liferay.calendar.web_otherCalendars"));
	}

	protected com.liferay.portal.kernel.portlet.PortalPreferences
		reloadPortalPreferences() {

		EntityCacheUtil.clearCache(PortalPreferenceValueImpl.class);

		_portalCache.removeAll();

		return _portalPreferenceValueLocalService.getPortalPreferences(
			_portalPreferences, true);
	}

	protected void setUpUpgradePortalPreferences() {
		_upgradeProcess = CalendarUpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, "UpgradePortalPreferences");
	}

	private static final String _NAMESPACE_NEW_SESSION_CLICKS =
		"com.liferay.portal.kernel.util.SessionClicks";

	private static final String _NAMESPACE_OLD_SESSION_CLICKS =
		"com.liferay.portal.util.SessionClicks";

	private PortalCache<?, ?> _portalCache;

	@Inject
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	@DeleteAfterTestRun
	private PortalPreferences _portalPreferences;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.calendar.web.internal.upgrade.registry.CalendarWebUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private User _user;

}