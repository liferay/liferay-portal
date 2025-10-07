/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.exception.NotificationRecipientSettingNameException;
import com.liferay.notification.exception.NotificationTemplateDescriptionException;
import com.liferay.notification.exception.NotificationTemplateExternalReferenceCodeException;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.test.util.NotificationTemplateUtil;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Murilo Stodolni
 */
@RunWith(Arquillian.class)
public class NotificationTemplateLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_externalReferenceCode = RandomTestUtil.randomString();
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddNotificationTemplate() throws Exception {
		User user = TestPropsValues.getUser();

		String notificationRecipientSettingName = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NotificationRecipientSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ", notificationRecipientSettingName,
				StringPool.COMMA_AND_SPACE,
				NotificationRecipientSettingConstants.NAME_ROLE_NAME,
				" are not allowed"),
			() -> _notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(), StringUtil.randomString(255),
					RandomTestUtil.randomString(),
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								notificationRecipientSettingName,
								RandomTestUtil.randomString()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								RandomTestUtil.randomString())),
					StringUtil.randomString(256),
					NotificationConstants.TYPE_EMAIL)));
		AssertUtils.assertFailure(
			NotificationRecipientSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ", notificationRecipientSettingName,
				StringPool.COMMA_AND_SPACE,
				NotificationRecipientSettingConstants.NAME_SINGLE_RECIPIENT,
				" are not allowed"),
			() -> _notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(), StringUtil.randomString(255),
					RandomTestUtil.randomString(),
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								notificationRecipientSettingName,
								RandomTestUtil.randomString()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_SINGLE_RECIPIENT,
								RandomTestUtil.randomString())),
					StringUtil.randomString(256),
					NotificationConstants.TYPE_USER_NOTIFICATION)));

		AssertUtils.assertFailure(
			NotificationTemplateDescriptionException.class,
			"The description cannot contain more than 255 characters",
			() -> _notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					user, StringUtil.randomString(256),
					NotificationConstants.TYPE_USER_NOTIFICATION)));

		NotificationContext notificationContext =
			NotificationTemplateUtil.createNotificationContext(
				user, StringUtil.randomString(255),
				NotificationConstants.TYPE_USER_NOTIFICATION);

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		notificationTemplate.setExternalReferenceCode("L_INVALID_ERC_TEST");

		AssertUtils.assertFailure(
			NotificationTemplateExternalReferenceCodeException.
				MustNotStartWithPrefix.class,
			"The prefix L_ is reserved",
			() -> _notificationTemplateLocalService.addNotificationTemplate(
				notificationContext));

		_notificationTemplateLocalService.addNotificationTemplate(
			NotificationTemplateUtil.createNotificationContext(
				user, StringUtil.randomString(255),
				NotificationConstants.TYPE_USER_NOTIFICATION));

		notificationTemplate =
			_notificationTemplateLocalService.addNotificationTemplate(
				_externalReferenceCode, user.getUserId(),
				NotificationConstants.TYPE_EMAIL);

		Assert.assertEquals(
			_externalReferenceCode,
			notificationTemplate.getExternalReferenceCode());
		Assert.assertEquals(user.getUserId(), notificationTemplate.getUserId());
		Assert.assertEquals(
			user.getFullName(), notificationTemplate.getUserName());
		Assert.assertEquals(0, notificationTemplate.getObjectDefinitionId());
		Assert.assertEquals(
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
			notificationTemplate.getEditorType());
		Assert.assertEquals(
			_externalReferenceCode, notificationTemplate.getName());
		Assert.assertEquals(
			NotificationConstants.TYPE_EMAIL, notificationTemplate.getType());

		NotificationRecipient notificationRecipient =
			notificationTemplate.getNotificationRecipient();

		Assert.assertNotNull(notificationRecipient);

		long notificationRecipientId =
			notificationRecipient.getNotificationRecipientId();

		_assertNotificationRecipientSetting(
			NotificationRecipientSettingConstants.NAME_FROM,
			notificationRecipientId);
		_assertNotificationRecipientSetting(
			NotificationRecipientSettingConstants.NAME_FROM_NAME,
			notificationRecipientId);
		_assertNotificationRecipientSetting(
			NotificationRecipientSettingConstants.NAME_TO,
			notificationRecipientId);

		_notificationTemplateLocalService.deleteNotificationTemplate(
			notificationTemplate);
	}

	private void _assertNotificationRecipientSetting(
			String name, long notificationRecipientId)
		throws Exception {

		NotificationRecipientSetting notificationRecipientSetting =
			_notificationRecipientSettingLocalService.
				fetchNotificationRecipientSetting(
					notificationRecipientId, name);

		Assert.assertEquals(name, notificationRecipientSetting.getName());
		Assert.assertEquals(
			_externalReferenceCode,
			notificationRecipientSetting.getValue(LocaleUtil.getDefault()));
	}

	private String _externalReferenceCode;

	@Inject
	private NotificationRecipientSettingLocalService
		_notificationRecipientSettingLocalService;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

}