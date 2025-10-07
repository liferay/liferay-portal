/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserNotificationDeliveryLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.time.Month;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class UserNotificationTypeTest extends BaseNotificationTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws PortalException {
		_userNotificationEventLocalService.deleteUserNotificationEvents(
			user1.getUserId());
		_userNotificationEventLocalService.deleteUserNotificationEvents(
			user2.getUserId());
	}

	@Test
	public void testIsDeliver() throws Exception {
		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.addNotificationTemplate(
				_createNotificationContext(
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								"userScreenName", user1.getScreenName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								"userScreenName", user2.getScreenName())),
					NotificationRecipientConstants.TYPE_USER));

		_userNotificationDeliveryLocalService.addUserNotificationDelivery(
			user1.getUserId(), childObjectDefinition.getPortletId(),
			_classNameLocalService.getClassNameId(
				childObjectDefinition.getClassName()),
			UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY,
			UserNotificationDeliveryConstants.TYPE_WEBSITE, false);
		_userNotificationDeliveryLocalService.addUserNotificationDelivery(
			user2.getUserId(), childObjectDefinition.getPortletId(),
			_classNameLocalService.getClassNameId(
				childObjectDefinition.getClassName()),
			UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY,
			UserNotificationDeliveryConstants.TYPE_WEBSITE, true);

		_executeNotificationObjectAction(
			notificationTemplate, childObjectDefinition, group.getGroupKey(),
			user2);

		_assertNotificationQueueEntry(user2.getFullName());
	}

	@Test
	public void testSendNotificationRecipientTypeRole() throws Exception {
		_testSendNotification(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						RoleConstants.ADMINISTRATOR),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						role.getName())),
			NotificationRecipientConstants.TYPE_ROLE);
	}

	@Test
	public void testSendNotificationRecipientTypeRoleWithInheritedUsers()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_roleLocalService.addGroupRole(
			userGroup.getGroupId(), role.getRoleId());

		User user = userLocalService.addUser(
			user1.getUserId(), user1.getCompanyId(), true, null, null, true,
			null, RandomTestUtil.randomString() + "@liferay.com",
			user1.getLocale(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0, 0,
			true, Month.FEBRUARY.getValue(), 7, 1988, null,
			UserConstants.TYPE_REGULAR, null, null, null,
			new long[] {userGroup.getUserGroupId()}, true, null);

		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			childObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		executeNotificationObjectAction(
			0,
			notificationTemplateLocalService.addNotificationTemplate(
				_createNotificationContext(
					Collections.singletonList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								role.getName())),
					NotificationRecipientConstants.TYPE_ROLE)));

		_assertNotificationQueueEntry(user.getFullName());
	}

	@Test
	public void testSendNotificationRecipientTypeRoleWithSiteRoles()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_addSiteRoleUser(group, RoleTestUtil.addRole(RoleConstants.TYPE_SITE));

		Role siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		User user = _addSiteRoleUser(group, siteRole);

		_executeNotificationObjectAction(
			notificationTemplateLocalService.addNotificationTemplate(
				_createNotificationContext(
					Collections.singletonList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								siteRole.getName())),
					NotificationRecipientConstants.TYPE_ROLE)),
			childObjectDefinition, group.getGroupKey(), user);

		_assertNotificationQueueEntry(user.getFullName());
	}

	@FeatureFlag("LPD-6233")
	@Test
	public void testSendNotificationRecipientTypeTermAssignee()
		throws Exception {

		ObjectDefinition objectDefinition =
			addObjectDefinitionWithNotificationTemplateObjectAction(
				Collections.singletonList(
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							NotificationRecipientConstants.TYPE_TERM,
							"[%OBJECT_ENTRY_ASSIGNEE%]")),
				"[%OBJECT_ENTRY_ASSIGNEE%]",
				NotificationConstants.TYPE_USER_NOTIFICATION);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addViewResourcePermission(objectDefinition, role);

		User user1 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());

		objectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"assignee",
						HashMapBuilder.put(
							"externalReferenceCode",
							user1.getExternalReferenceCode()
						).put(
							"type", "User"
						).build()
					).build();
				}
			},
			null);

		Assert.assertEquals(
			1,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user1.getUserId()));
		assertNotificationQueueEntrySubject(user1.getFullName());

		User user2 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());

		objectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"assignee",
						HashMapBuilder.put(
							"externalReferenceCode",
							role.getExternalReferenceCode()
						).put(
							"type", "Role"
						).build()
					).build();
				}
			},
			null);

		Assert.assertEquals(
			2,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user1.getUserId()));
		Assert.assertEquals(
			1,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user2.getUserId()));
		assertNotificationQueueEntrySubject(role.getName());

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testSendNotificationRecipientTypeTermChildAuthorTerm()
		throws Exception {

		_testSendNotificationRecipientTypeTerm(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"term", getTermName("AUTHOR_ID"))),
			NotificationRecipientConstants.TYPE_TERM);
	}

	@Test
	public void testSendNotificationRecipientTypeTermCreator()
		throws Exception {

		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.addNotificationTemplate(
				_createNotificationContext(
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								"term", getTermName("creator"))),
					NotificationRecipientConstants.TYPE_TERM));

		objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			childObjectDefinition.getObjectDefinitionId(), true,
			StringPool.BLANK, RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build(),
			false);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			User user = _addUser();

			ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
				dtoConverterContext, childObjectDefinition,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.putAll(
							childObjectEntryValues
						).build();
					}
				},
				group.getGroupKey());

			List<NotificationQueueEntry> notificationQueueEntries =
				notificationQueueEntryLocalService.getNotificationQueueEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(
				notificationQueueEntries.toString(), 0,
				notificationQueueEntries.size());

			_addUser();

			objectEntryManager.updateObjectEntry(
				TestPropsValues.getCompanyId(), dtoConverterContext,
				objectEntry.getExternalReferenceCode(), childObjectDefinition,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.putAll(
							childObjectEntryValues
						).build();
					}
				},
				group.getGroupKey());

			_assertNotificationQueueEntry(user.getFullName());

			_userNotificationEventLocalService.deleteUserNotificationEvents(
				user.getUserId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	public void testSendNotificationRecipientTypeTermCurrentUserTerm()
		throws Exception {

		_testSendNotificationRecipientTypeTerm(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"term", "[%CURRENT_USER_ID%]")),
			NotificationRecipientConstants.TYPE_TERM);
	}

	@Test
	public void testSendNotificationRecipientTypeTermParentAuthorTerm()
		throws Exception {

		_testSendNotificationRecipientTypeTerm(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"term", getTermName(true, "AUTHOR_ID"))),
			NotificationRecipientConstants.TYPE_TERM);
	}

	@Test
	public void testSendNotificationRecipientTypeTermScreenName()
		throws Exception {

		_testSendNotification(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"term", getTermName("creator")),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"term", user1.getScreenName())),
			NotificationRecipientConstants.TYPE_TERM);
	}

	@Test
	public void testSendNotificationRecipientTypeUser() throws Exception {
		_testSendNotification(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"userScreenName", user1.getScreenName()),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"userScreenName", user2.getScreenName())),
			NotificationRecipientConstants.TYPE_USER);
	}

	@FeatureFlag("LPD-50091")
	@Test
	public void testSendNotificationRecipientTypeUserGroup() throws Exception {
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroup(
			user1.getUserId(), userGroup1.getUserGroupId());

		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroup(
			user1.getUserId(), userGroup2.getUserGroupId());
		_userGroupLocalService.addUserUserGroup(
			user2.getUserId(), userGroup2.getUserGroupId());

		try {
			_testSendNotification(
				Arrays.asList(
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"userGroupName", userGroup1.getName()),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"userGroupName", userGroup2.getName())),
				NotificationRecipientConstants.TYPE_USER_GROUP);

			Assert.assertEquals(
				1,
				_userNotificationEventLocalService.
					getUserNotificationEventsCount(user1.getUserId()));
			Assert.assertEquals(
				1,
				_userNotificationEventLocalService.
					getUserNotificationEventsCount(user2.getUserId()));
		}
		finally {
			_userGroupLocalService.setUserUserGroups(
				user1.getUserId(), new long[0]);

			_userGroupLocalService.deleteUserGroup(userGroup1);

			_userGroupLocalService.setUserUserGroups(
				user2.getUserId(), new long[0]);

			_userGroupLocalService.deleteUserGroup(userGroup2);
		}
	}

	private User _addSiteRoleUser(Group group, Role siteRole) throws Exception {
		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			childObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			siteRole.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);
		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			childObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			siteRole.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		User user = UserTestUtil.addUser(group.getGroupId());

		UserGroupRole userGroupRole =
			_userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), group.getGroupId(), siteRole.getRoleId());

		return userGroupRole.getUser();
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			childObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);
		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			childObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		userLocalService.addRoleUser(role.getRoleId(), user);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		return user;
	}

	private void _assertNotificationQueueEntry(String expectedUserFullName)
		throws PortalException {

		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntries.get(0);

		NotificationRecipient notificationRecipient =
			notificationQueueEntry.getNotificationRecipient();

		List<NotificationRecipientSetting> notificationRecipientSettings =
			notificationRecipient.getNotificationRecipientSettings();

		Assert.assertEquals(
			notificationRecipientSettings.toString(), 1,
			notificationRecipientSettings.size());
		_assertNotificationRecipientSetting(
			notificationRecipientSettings.get(0), expectedUserFullName);

		notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntry);
	}

	private void _assertNotificationRecipientSetting(
		NotificationRecipientSetting notificationRecipientSetting,
		String userFullName) {

		Assert.assertEquals(
			"userFullName", notificationRecipientSetting.getName());
		Assert.assertEquals(
			userFullName, notificationRecipientSetting.getValue());
	}

	private NotificationContext _createNotificationContext(
		List<NotificationRecipientSetting> notificationRecipientSettings,
		String recipientType) {

		NotificationContext notificationContext = new NotificationContext();

		notificationContext.setClassName(RandomTestUtil.randomString());
		notificationContext.setClassPK(RandomTestUtil.randomLong());

		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.createNotificationTemplate(0L);

		notificationTemplate.setEditorType(
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT);
		notificationTemplate.setName(RandomTestUtil.randomString());
		notificationTemplate.setRecipientType(recipientType);
		notificationTemplate.setSubject(
			ListUtil.toString(
				getTermNames(), StringPool.BLANK, StringPool.SEMICOLON));
		notificationTemplate.setType(
			NotificationConstants.TYPE_USER_NOTIFICATION);

		notificationContext.setNotificationTemplate(notificationTemplate);

		notificationContext.setNotificationRecipient(
			notificationRecipientLocalService.createNotificationRecipient(0L));
		notificationContext.setNotificationRecipientSettings(
			notificationRecipientSettings);
		notificationContext.setType(
			NotificationConstants.TYPE_USER_NOTIFICATION);

		return notificationContext;
	}

	private void _executeNotificationObjectAction(
			NotificationTemplate notificationTemplate,
			ObjectDefinition objectDefinition, String scopeKey, User user)
		throws Exception {

		ObjectAction objectAction = objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build(),
			false);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		try {
			objectEntryManager.addObjectEntry(
				dtoConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						properties = Collections.emptyMap();
					}
				},
				scopeKey);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}

		objectActionLocalService.deleteObjectAction(
			objectAction.getObjectActionId());
	}

	private void _testSendNotification(
			List<NotificationRecipientSetting> notificationRecipientSettings,
			String recipientType)
		throws Exception {

		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 0,
			notificationQueueEntries.size());

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user1.getUserId()));

		executeNotificationObjectAction(
			0,
			notificationTemplateLocalService.addNotificationTemplate(
				_createNotificationContext(
					notificationRecipientSettings, recipientType)));

		notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		notificationQueueEntry = notificationQueueEntries.get(0);

		assertTermValues(
			getTermValues(),
			ListUtil.fromString(
				notificationQueueEntry.getSubject(), StringPool.SEMICOLON));

		NotificationRecipient notificationRecipient =
			notificationQueueEntry.getNotificationRecipient();

		notificationRecipientSettings =
			notificationRecipient.getNotificationRecipientSettings();

		Assert.assertEquals(
			notificationRecipientSettings.toString(), 2,
			notificationRecipientSettings.size());
		_assertNotificationRecipientSetting(
			notificationRecipientSettings.get(0), user1.getFullName());
		_assertNotificationRecipientSetting(
			notificationRecipientSettings.get(1), user2.getFullName());

		Assert.assertEquals(
			1,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user1.getUserId()));
	}

	private void _testSendNotificationRecipientTypeTerm(
			List<NotificationRecipientSetting> notificationRecipientSettings,
			String recipientType)
		throws Exception {

		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 0,
			notificationQueueEntries.size());

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user2.getUserId()));

		executeNotificationObjectAction(
			0,
			notificationTemplateLocalService.addNotificationTemplate(
				_createNotificationContext(
					notificationRecipientSettings, recipientType)));

		notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		notificationQueueEntry = notificationQueueEntries.get(0);

		assertTermValues(
			getTermValues(),
			ListUtil.fromString(
				notificationQueueEntry.getSubject(), StringPool.SEMICOLON));

		NotificationRecipient notificationRecipient =
			notificationQueueEntry.getNotificationRecipient();

		notificationRecipientSettings =
			notificationRecipient.getNotificationRecipientSettings();

		Assert.assertEquals(
			notificationRecipientSettings.toString(), 1,
			notificationRecipientSettings.size());
		_assertNotificationRecipientSetting(
			notificationRecipientSettings.get(0), user2.getFullName());

		Assert.assertEquals(
			1,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user2.getUserId()));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserNotificationDeliveryLocalService
		_userNotificationDeliveryLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}