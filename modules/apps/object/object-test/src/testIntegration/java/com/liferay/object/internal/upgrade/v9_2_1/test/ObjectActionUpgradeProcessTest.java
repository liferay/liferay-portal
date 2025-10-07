/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v9_2_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class ObjectActionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()));

		ObjectAction objectAction1 = _addObjectAction(
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, objectDefinition,
			UnicodePropertiesBuilder.put(
				"notificationTemplateExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"type", NotificationConstants.TYPE_EMAIL
			).build());
		ObjectAction objectAction2 = _addObjectAction(
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, objectDefinition,
			UnicodePropertiesBuilder.put(
				"notificationTemplateExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"type", NotificationConstants.TYPE_EMAIL
			).build());
		ObjectAction objectAction3 = _addObjectAction(
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, objectDefinition,
			UnicodePropertiesBuilder.put(
				"notificationTemplateExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"type", NotificationConstants.TYPE_EMAIL
			).build());

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.addNotificationTemplate(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				NotificationConstants.TYPE_USER_NOTIFICATION);

		ObjectAction objectAction4 = _addObjectAction(
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, objectDefinition,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build());

		_notificationTemplateLocalService.deleteNotificationTemplate(
			notificationTemplate);

		ObjectAction objectAction5 = _addObjectAction(
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, objectDefinition,
			UnicodePropertiesBuilder.put(
				"url", RandomTestUtil.randomString()
			).build());
		ObjectAction objectAction6 = _addObjectAction(
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, objectDefinition,
			UnicodePropertiesBuilder.put(
				"url", RandomTestUtil.randomString()
			).build());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		_assertFalseUsePreferredLanguageForGuestsParameter(
			objectAction1.getObjectActionId());
		_assertNullUsePreferredLanguageForGuestsParameter(
			objectAction2.getObjectActionId());
		_assertFalseUsePreferredLanguageForGuestsParameter(
			objectAction3.getObjectActionId());
		_assertNullUsePreferredLanguageForGuestsParameter(
			objectAction4.getObjectActionId());
		_assertNullUsePreferredLanguageForGuestsParameter(
			objectAction5.getObjectActionId());
		_assertNullUsePreferredLanguageForGuestsParameter(
			objectAction6.getObjectActionId());
	}

	private ObjectAction _addObjectAction(
			String objectActionExecutorKey, String objectActionTriggerKey,
			ObjectDefinition objectDefinition,
			UnicodeProperties unicodeProperties)
		throws Exception {

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), objectActionExecutorKey,
			objectActionTriggerKey, unicodeProperties, false);
	}

	private void _assertFalseUsePreferredLanguageForGuestsParameter(
			long objectActionId)
		throws Exception {

		ObjectAction objectAction = _objectActionLocalService.getObjectAction(
			objectActionId);

		UnicodeProperties unicodeProperties =
			objectAction.getParametersUnicodeProperties();

		Assert.assertNotNull(
			unicodeProperties.get("usePreferredLanguageForGuests"));
		Assert.assertFalse(
			GetterUtil.getBoolean(
				unicodeProperties.get("usePreferredLanguageForGuests")));
	}

	private void _assertNullUsePreferredLanguageForGuestsParameter(
			long objectActionId)
		throws Exception {

		ObjectAction objectAction = _objectActionLocalService.getObjectAction(
			objectActionId);

		UnicodeProperties unicodeProperties =
			objectAction.getParametersUnicodeProperties();

		Assert.assertNull(
			unicodeProperties.get("usePreferredLanguageForGuests"));
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v9_2_1.ObjectActionUpgradeProcess";

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}