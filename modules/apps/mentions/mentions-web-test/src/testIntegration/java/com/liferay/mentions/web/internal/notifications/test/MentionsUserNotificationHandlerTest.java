/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-104086")
@RunWith(Arquillian.class)
public class MentionsUserNotificationHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testIsApplicable() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition);

		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_userLocalService.addRoleUser(role.getRoleId(), user);

		UserNotificationEvent userNotificationEvent =
			_userNotificationEventLocalService.addUserNotificationEvent(
				user.getUserId(), MentionsPortletKeys.MENTIONS,
				System.currentTimeMillis(),
				UserNotificationDeliveryConstants.TYPE_WEBSITE, 0,
				JSONUtil.put(
					"discussionClassName", objectDefinition.getClassName()
				).put(
					"discussionClassPK", objectEntry.getObjectEntryId()
				).put(
					"userId", TestPropsValues.getUserId()
				).toString(),
				false, ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(user);

		Assert.assertTrue(_isApplicable(userNotificationEvent));

		objectEntry = _objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertFalse(_isApplicable(userNotificationEvent));

		_objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(_isApplicable(userNotificationEvent));

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[0]);

		Assert.assertFalse(_isApplicable(userNotificationEvent));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
		_roleLocalService.deleteRole(role);
		_userLocalService.deleteUser(user);
	}

	private boolean _isApplicable(UserNotificationEvent userNotificationEvent)
		throws Exception {

		UserNotificationFeedEntry userNotificationFeedEntry =
			UserNotificationManagerUtil.interpret(
				StringPool.BLANK, userNotificationEvent,
				ServiceContextTestUtil.getServiceContext());

		return userNotificationFeedEntry.isApplicable();
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}