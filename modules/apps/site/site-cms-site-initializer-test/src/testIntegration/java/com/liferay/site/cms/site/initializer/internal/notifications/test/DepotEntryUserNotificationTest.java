/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-34594"),
		@FeatureFlag("LPD-57283")
	}
)
@RunWith(Arquillian.class)
public class DepotEntryUserNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_depotEntry1 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
		_depotEntry2 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
		_depotEntry3 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_PROJECT,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAddGroupUser() throws Exception {
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			_depotEntry1.getGroupId(), user.getUserId());

		_assertUserNotificationEvent(user);
	}

	@Test
	public void testAddUserUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(
			_depotEntry1.getGroupId(), userGroup);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		_assertUserNotificationEvent(user);
	}

	@Test
	public void testGetLink() throws Exception {
		UserNotificationFeedEntry userNotificationFeedEntry =
			_getUserNotificationFeedEntry(_depotEntry1);

		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/path-friendly-url-public/cms/e/space/",
				PortalUtil.getClassNameId(DepotEntry.class), StringPool.SLASH,
				_depotEntry1.getDepotEntryId()),
			userNotificationFeedEntry.getLink());

		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			_depotEntry2.getGroupId(), user.getUserId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		ServiceContext serviceContext = _getServiceContext(user);

		userNotificationFeedEntry = _userNotificationHandler.interpret(
			userNotificationEvents.get(0), serviceContext);

		Assert.assertEquals(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					serviceContext.getRequest(), serviceContext.getScopeGroup(),
					"com_liferay_design_library_web_internal_portlet_" +
						"DesignLibraryAdminPortlet",
					0, 0, PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/design_library/design_library_resources"
			).setParameter(
				"designLibraryEntryId", _depotEntry2.getDepotEntryId()
			).buildString(),
			userNotificationFeedEntry.getLink());

		userNotificationFeedEntry = _getUserNotificationFeedEntry(_depotEntry3);

		Assert.assertEquals(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/path-friendly-url-public/cms/projects",
			userNotificationFeedEntry.getLink());
	}

	@Test
	public void testGetTitle() throws Exception {
		Group group = _depotEntry1.getGroup();
		UserNotificationFeedEntry userNotificationFeedEntry =
			_getUserNotificationFeedEntry(_depotEntry1);

		Assert.assertEquals(
			LanguageUtil.format(
				LocaleUtil.US,
				"you-have-been-invited-to-collaborate-in-the-x-space",
				group.getName(LocaleUtil.US)),
			userNotificationFeedEntry.getTitle());

		group = _depotEntry2.getGroup();
		userNotificationFeedEntry = _getUserNotificationFeedEntry(_depotEntry2);

		Assert.assertEquals(
			LanguageUtil.format(
				LocaleUtil.US,
				"you-have-been-invited-to-collaborate-in-the-x-design-library",
				group.getName(LocaleUtil.US)),
			userNotificationFeedEntry.getTitle());

		group = _depotEntry3.getGroup();
		userNotificationFeedEntry = _getUserNotificationFeedEntry(_depotEntry3);

		Assert.assertEquals(
			LanguageUtil.format(
				LocaleUtil.US,
				"you-have-been-invited-to-collaborate-in-the-x-project",
				group.getName(LocaleUtil.US)),
			userNotificationFeedEntry.getTitle());
	}

	@Test
	public void testInterpret() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(
			_depotEntry1.getGroupId(), userGroup);

		User user1 = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user1.getUserId(), userGroup);

		User user2 = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(user2.getUserId(), userGroup);

		_assertUserNotificationEvent(user1);
		_assertUserNotificationEvent(user2);

		_userLocalService.deleteUserGroupUser(
			userGroup.getUserGroupId(), user1.getUserId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setLanguageId("en_US");
		serviceContext.setUserId(user1.getUserId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user1.getUserId());

		_userNotificationHandler.interpret(
			userNotificationEvents.get(0), serviceContext);

		userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user1.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 0,
			userNotificationEvents.size());

		userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user2.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		_userGroupLocalService.deleteGroupUserGroup(
			_depotEntry1.getGroupId(), userGroup.getUserGroupId());

		serviceContext.setUserId(user2.getUserId());

		_userNotificationHandler.interpret(
			userNotificationEvents.get(0), serviceContext);

		userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user2.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 0,
			userNotificationEvents.size());
	}

	private void _assertUserNotificationEvent(User user) {
		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		Assert.assertEquals(
			DepotPortletKeys.DEPOT_ADMIN, userNotificationEvent.getType());

		String payload = userNotificationEvent.getPayload();

		Assert.assertTrue(
			payload.contains("classPK\":" + _depotEntry1.getDepotEntryId()));
	}

	private ServiceContext _getServiceContext(User user) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setLanguageId("en_US");
		serviceContext.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));

		ThemeDisplay themeDisplay = _getThemeDisplay();

		serviceContext.setRequest(themeDisplay.getRequest());

		serviceContext.setScopeGroupId(TestPropsValues.getGroupId());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPathFriendlyURLPublic("/path-friendly-url-public");

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(httpServletRequest);

		return themeDisplay;
	}

	private UserNotificationFeedEntry _getUserNotificationFeedEntry(
			DepotEntry depotEntry)
		throws Exception {

		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			depotEntry.getGroupId(), user.getUserId());

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		return _userNotificationHandler.interpret(
			userNotificationEvents.get(0), _getServiceContext(user));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry1;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry2;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry3;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(filter = "jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN)
	private UserNotificationHandler _userNotificationHandler;

}