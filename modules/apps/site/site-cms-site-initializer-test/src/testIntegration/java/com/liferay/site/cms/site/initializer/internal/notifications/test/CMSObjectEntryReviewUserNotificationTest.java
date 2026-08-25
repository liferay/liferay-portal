/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Veronica Gonzalez
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CMSObjectEntryReviewUserNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			_depotEntry.getGroupId(), _user.getUserId());
	}

	@Test
	public void testCheckObjectEntriesTwice() throws Exception {
		ObjectEntry objectEntry = _addCMSObjectEntry(
			RandomTestUtil.randomString());

		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());
		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());

		_assertUserNotificationEventsCount(1, objectEntry, _user);
	}

	@Test
	public void testCheckObjectEntriesWithContentReviewer() throws Exception {
		User contentReviewerUser = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			_depotEntry.getGroupId(), contentReviewerUser.getUserId());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		_userGroupRoleLocalService.addUserGroupRoles(
			contentReviewerUser.getUserId(), _depotEntry.getGroupId(),
			new long[] {role.getRoleId()});

		Assert.assertTrue(
			_userGroupRoleLocalService.hasUserGroupRole(
				contentReviewerUser.getUserId(), _depotEntry.getGroupId(),
				role.getRoleId()));

		ObjectEntry objectEntry = _addCMSObjectEntry(
			RandomTestUtil.randomString());

		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());

		_assertUserNotificationEventsCount(1, objectEntry, _user);
		_assertUserNotificationEventsCount(0, objectEntry, contentReviewerUser);
	}

	@Test
	public void testGetLink() throws Exception {
		ObjectEntry objectEntry = _addCMSObjectEntry(
			RandomTestUtil.randomString());

		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_getUserNotificationFeedEntry(objectEntry, "en_US");

		Assert.assertEquals(
			StringBundler.concat(
				_portal.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/view-asset?objectEntryId=",
				objectEntry.getObjectEntryId()),
			userNotificationFeedEntry.getLink());
	}

	@Test
	public void testGetTitle() throws Exception {
		String title = RandomTestUtil.randomString() + "<&>";

		ObjectEntry objectEntry = _addCMSObjectEntry(title);

		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_getUserNotificationFeedEntry(objectEntry, "en_US");

		Assert.assertEquals(
			HtmlUtil.escape(
				LanguageUtil.format(
					LocaleUtil.US, "x-has-reached-its-review-date", title)),
			userNotificationFeedEntry.getTitle());
	}

	@Test
	public void testGetTitleWithSpanishLocale() throws Exception {
		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addCMSObjectEntry(title);

		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_getUserNotificationFeedEntry(objectEntry, "es_ES");

		Assert.assertEquals(
			HtmlUtil.escape(
				LanguageUtil.format(
					LocaleUtil.SPAIN, "x-has-reached-its-review-date", title)),
			userNotificationFeedEntry.getTitle());
	}

	private ObjectEntry _addCMSObjectEntry(String title) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), _user.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			LocaleUtil.toLanguageId(LocaleUtil.US),
			HashMapBuilder.<String, Serializable>put(
				"reviewDate", new Date()
			).put(
				"title_i18n",
				HashMapBuilder.<String, Object>put(
					LocaleUtil.toLanguageId(LocaleUtil.US), title
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertUserNotificationEventsCount(
			int count, ObjectEntry objectEntry, User user)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents =
			_getUserNotificationEvents(objectEntry, user);

		Assert.assertEquals(
			userNotificationEvents.toString(), count,
			userNotificationEvents.size());
	}

	private List<UserNotificationEvent> _getUserNotificationEvents(
			ObjectEntry objectEntry, User user)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents = new ArrayList<>();

		for (UserNotificationEvent userNotificationEvent :
				_userNotificationEventLocalService.getUserNotificationEvents(
					user.getUserId())) {

			JSONObject payloadJSONObject = JSONFactoryUtil.createJSONObject(
				userNotificationEvent.getPayload());

			if ((payloadJSONObject.getLong("classPK") ==
					objectEntry.getObjectEntryId()) &&
				Objects.equals(
					payloadJSONObject.getString("notificationMessageKey"),
					"x-has-reached-its-review-date")) {

				userNotificationEvents.add(userNotificationEvent);
			}
		}

		return userNotificationEvents;
	}

	private UserNotificationFeedEntry _getUserNotificationFeedEntry(
			ObjectEntry objectEntry, String languageId)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents =
			_getUserNotificationEvents(objectEntry, _user);

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setLanguageId(languageId);
		serviceContext.setUserId(_user.getUserId());

		return UserNotificationManagerUtil.interpret(
			StringPool.BLANK, userNotificationEvent, serviceContext);
	}

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}