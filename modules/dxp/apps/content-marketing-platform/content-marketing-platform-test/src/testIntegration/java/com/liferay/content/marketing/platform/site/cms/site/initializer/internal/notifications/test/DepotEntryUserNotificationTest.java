/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.site.cms.site.initializer.internal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
@FeatureFlags(featureFlags = @FeatureFlag("LPD-57283"))
@RunWith(Arquillian.class)
public class DepotEntryUserNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");
	}

	@Test
	public void testGetLink() throws Exception {
		CMPTestUtil.getOrAddGroup(DepotEntryUserNotificationTest.class);

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		_depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			cmpProjectObjectEntry.getGroupId());

		ObjectDefinition cmpProjectObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());

		UserNotificationFeedEntry userNotificationFeedEntry =
			_getUserNotificationFeedEntry(_depotEntry);

		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/path-friendly-url-public/cms/e/project/",
				PortalUtil.getClassNameId(
					cmpProjectObjectDefinition.getClassName()),
				StringPool.SLASH, cmpProjectObjectEntry.getObjectEntryId()),
			userNotificationFeedEntry.getLink());
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
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());

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
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(filter = "jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN)
	private UserNotificationHandler _userNotificationHandler;

}