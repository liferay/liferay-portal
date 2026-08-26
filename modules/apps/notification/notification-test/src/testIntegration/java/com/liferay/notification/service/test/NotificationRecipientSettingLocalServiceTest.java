/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcela Cunha
 */
@RunWith(Arquillian.class)
public class NotificationRecipientSettingLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testCreateNotificationRecipientSettings() throws Exception {
		String from = RandomTestUtil.randomString();
		String fromName = RandomTestUtil.randomString();

		List<NotificationRecipientSetting> notificationRecipientSettings =
			_notificationRecipientSettingLocalService.
				createNotificationRecipientSettings(
					0L,
					new Object[] {
						HashMapBuilder.<String, Object>put(
							NotificationRecipientSettingConstants.NAME_FROM,
							from
						).put(
							NotificationRecipientSettingConstants.
								NAME_FROM_NAME,
							HashMapBuilder.put(
								LocaleUtil.US.toString(), fromName
							).build()
						).build()
					},
					TestPropsValues.getUser());

		Assert.assertEquals(
			notificationRecipientSettings.toString(), 2,
			notificationRecipientSettings.size());

		Map<String, NotificationRecipientSetting>
			notificationRecipientSettingsMap = _toMap(
				notificationRecipientSettings);

		NotificationRecipientSetting notificationRecipientSetting =
			notificationRecipientSettingsMap.get(
				NotificationRecipientSettingConstants.NAME_FROM);

		Assert.assertEquals(from, notificationRecipientSetting.getValue());

		notificationRecipientSetting = notificationRecipientSettingsMap.get(
			NotificationRecipientSettingConstants.NAME_FROM_NAME);

		Assert.assertEquals(
			fromName, notificationRecipientSetting.getValue(LocaleUtil.US));
	}

	@Test
	public void testCreateNotificationRecipientSettingsWithSubscribers()
		throws Exception {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			_notificationRecipientSettingLocalService.
				createNotificationRecipientSettings(
					0L,
					new Object[] {
						HashMapBuilder.<String, Object>put(
							NotificationRecipientSettingConstants.NAME_TO,
							RandomTestUtil.randomString()
						).put(
							NotificationRecipientSettingConstants.
								getRecipientTypeName(
									NotificationRecipientSettingConstants.
										NAME_TO),
							NotificationRecipientConstants.TYPE_SUBSCRIBERS
						).build()
					},
					TestPropsValues.getUser());

		Assert.assertEquals(
			notificationRecipientSettings.toString(), 1,
			notificationRecipientSettings.size());

		Map<String, NotificationRecipientSetting>
			notificationRecipientSettingsMap = _toMap(
				notificationRecipientSettings);

		NotificationRecipientSetting notificationRecipientSetting =
			notificationRecipientSettingsMap.get(
				NotificationRecipientSettingConstants.NAME_TO_TYPE);

		Assert.assertEquals(
			NotificationRecipientConstants.TYPE_SUBSCRIBERS,
			notificationRecipientSetting.getValue());
	}

	private Map<String, NotificationRecipientSetting> _toMap(
		List<NotificationRecipientSetting> notificationRecipientSettings) {

		Map<String, NotificationRecipientSetting>
			notificationRecipientSettingsMap = new HashMap<>();

		for (NotificationRecipientSetting notificationRecipientSetting :
				notificationRecipientSettings) {

			notificationRecipientSettingsMap.put(
				notificationRecipientSetting.getName(),
				notificationRecipientSetting);
		}

		return notificationRecipientSettingsMap;
	}

	@Inject
	private NotificationRecipientSettingLocalService
		_notificationRecipientSettingLocalService;

}