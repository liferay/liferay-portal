/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.internal.test.util.CTCollectionTestUtil;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gislayne Vitorino
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class PublicationUserNotificationHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testGetBodyForChangeSizeClassification() throws Exception {
		User user = UserTestUtil.addUser();

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), user.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		CTCollectionTestUtil.updateCTCollectionSizeClassification(
			ctCollection.getCtCollectionId(), _group.getGroupId(), 9999, user);

		ServiceContext serviceContext = _getServiceContext();

		Locale locale = serviceContext.getLocale();

		_assertUserNotificationFeedEntryBody(
			ctCollection.getCtCollectionId(),
			_language.format(
				locale, "the-size-of-publication-x-has-changed-from-x-to-x",
				new Object[] {
					ctCollection.getName(), _language.get(locale, "small"),
					_language.get(locale, "medium")
				},
				false),
			false, user.getUserId());
	}

	@Test
	public void testGetBodyForConflict() throws Exception {
		CTCollection ctCollection =
			CTCollectionTestUtil.createCTCollectionWithConflict(
				TestPropsValues.getUser());

		CTCollectionTestUtil.publishCTCollectionWithError(
			ctCollection.getCtCollectionId());

		_assertUserNotificationFeedEntryBody(
			ctCollection.getCtCollectionId(),
			StringBundler.concat(
				"<div class=\"title\">", ctCollection.getName(),
				" scheduled publication failed.</div><div class=\"body\">",
				"Click on this notification to see the list of conflicts that ",
				"need to be manually resolved.</div>"),
			true, TestPropsValues.getUserId());
	}

	@Test
	public void testGetBodyForDeletedPublication() throws Exception {
		CTCollection ctCollection =
			CTCollectionTestUtil.createCTCollectionWithConflict(
				TestPropsValues.getUser());

		CTCollectionTestUtil.publishCTCollectionWithError(
			ctCollection.getCtCollectionId());

		_ctCollectionLocalService.deleteCTCollection(ctCollection);

		_assertUserNotificationFeedEntryBody(
			ctCollection.getCtCollectionId(),
			StringBundler.concat(
				"<div class=\"title\">Notification no longer applies.",
				"</div><div class=\"body\">Notification for Publications was ",
				"deleted.</div>"),
			true, TestPropsValues.getUserId());
	}

	@Test
	public void testGetBodyForStoppedService() throws Exception {
		User user = UserTestUtil.addUser();

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, user.getCompanyId(), user.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK);
		}

		Bundle bundle = null;

		try {
			bundle = _stopJournalServiceBundle();

			CTCollectionTestUtil.publishCTCollectionWithError(
				ctCollection.getCtCollectionId());

			_assertUserNotificationFeedEntryBody(
				ctCollection.getCtCollectionId(),
				StringBundler.concat(
					"<div class=\"title\">", ctCollection.getName(),
					" scheduled publication failed.</div><div class=",
					"\"body\">An unexpected error occurred while publishing ",
					"the scheduled publication. Please contact your system ",
					"administrator to resolve the issue.</div>"),
				false, user.getUserId());

			_assertUserNotificationFeedEntryBody(
				ctCollection.getCtCollectionId(),
				StringBundler.concat(
					"<div class=\"title\">", ctCollection.getName(),
					" scheduled publication failed with an unexpected system ",
					"error.</div><div class=\"body\">Click on this ",
					"notification to see the stack trace.</div>"),
				false, TestPropsValues.getUserId());
		}
		finally {
			if (bundle != null) {
				bundle.start();

				Thread.sleep(5000);
			}
		}
	}

	@Test
	public void testGetLinkToChangeSizeClassification() throws Exception {
		User user = UserTestUtil.addUser();

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), user.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		CTCollectionTestUtil.updateCTCollectionSizeClassification(
			ctCollection.getCtCollectionId(), _group.getGroupId(), 19999, user);

		ServiceContext serviceContext = _getServiceContext();

		_assertUserNotificationFeedEntryLink(
			ctCollection.getCtCollectionId(),
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					serviceContext.getRequest(), serviceContext.getScopeGroup(),
					CTPortletKeys.PUBLICATIONS, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/change_tracking/view_changes"
			).setParameter(
				"ctCollectionId", ctCollection.getCtCollectionId()
			).buildString(),
			serviceContext, false, user.getUserId());
	}

	@Test
	public void testGetLinkToViewConflicts() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName());

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			ddmStructure = _ddmStructureLocalService.updateStructure(
				TestPropsValues.getUserId(), ddmStructure.getStructureId(),
				ddmStructure.getDDMForm(), ddmStructure.getDDMFormLayout(),
				ServiceContextTestUtil.getServiceContext());
		}

		_ddmStructureLocalService.deleteDDMStructure(
			ddmStructure.getStructureId());

		CTCollectionTestUtil.publishCTCollectionWithError(
			ctCollection.getCtCollectionId());

		ServiceContext serviceContext = _getServiceContext();

		_assertUserNotificationFeedEntryLink(
			ctCollection.getCtCollectionId(),
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					serviceContext.getRequest(), serviceContext.getScopeGroup(),
					CTPortletKeys.PUBLICATIONS, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/change_tracking/view_conflicts"
			).setParameter(
				"ctCollectionId", ctCollection.getCtCollectionId()
			).buildString(),
			serviceContext, true, TestPropsValues.getUserId());
	}

	@Test
	public void testGetLinkToViewStackTrace() throws Exception {
		User user = UserTestUtil.addUser();

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, user.getCompanyId(), user.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK);
		}

		Bundle bundle = null;

		try {
			bundle = _stopJournalServiceBundle();

			CTCollectionTestUtil.publishCTCollectionWithError(
				ctCollection.getCtCollectionId());

			ServiceContext serviceContext = _getServiceContext();

			_assertUserNotificationFeedEntryLink(
				ctCollection.getCtCollectionId(), StringPool.BLANK,
				serviceContext, false, user.getUserId());

			List<CTProcess> ctProcesses = _ctProcessLocalService.getCTProcesses(
				ctCollection.getCtCollectionId());

			CTProcess ctProcess = ctProcesses.get(0);

			_assertUserNotificationFeedEntryLink(
				ctCollection.getCtCollectionId(),
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						serviceContext.getRequest(),
						serviceContext.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/change_tracking/view_stack_trace"
				).setParameter(
					"backgroundTaskId", ctProcess.getBackgroundTaskId()
				).setParameter(
					"ctCollectionName", ctCollection.getName()
				).buildString(),
				serviceContext, false, TestPropsValues.getUserId());
		}
		finally {
			if (bundle != null) {
				bundle.start();

				Thread.sleep(5000);
			}
		}
	}

	private void _assertUserNotificationFeedEntryBody(
			long ctCollectionId, String expectedBody, boolean showConflicts,
			long userId)
		throws Exception {

		UserNotificationFeedEntry userNotificationFeedEntry =
			_userNotificationHandler.interpret(
				_getUserNotificationEvent(
					ctCollectionId, showConflicts, userId),
				_getServiceContext());

		Assert.assertEquals(expectedBody, userNotificationFeedEntry.getBody());
	}

	private void _assertUserNotificationFeedEntryLink(
			long ctCollectionId, String expectedLink,
			ServiceContext serviceContext, boolean showConflicts, long userId)
		throws Exception {

		UserNotificationFeedEntry userNotificationFeedEntry =
			_userNotificationHandler.interpret(
				_getUserNotificationEvent(
					ctCollectionId, showConflicts, userId),
				serviceContext);

		Assert.assertEquals(expectedLink, userNotificationFeedEntry.getLink());
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		return serviceContext;
	}

	private UserNotificationEvent _getUserNotificationEvent(
			long ctCollectionId, boolean showConflicts, long userId)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				userId);

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			if (!Objects.equals(
					CTPortletKeys.PUBLICATIONS,
					userNotificationEvent.getType())) {

				continue;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());

			if (jsonObject.getLong("ctCollectionId") == ctCollectionId) {
				int notificationType = jsonObject.getInt("notificationType");

				if (((notificationType ==
						UserNotificationDefinition.
							NOTIFICATION_TYPE_REVIEW_ENTRY) &&
					 (jsonObject.getBoolean("showConflicts") ==
						 showConflicts)) ||
					(notificationType ==
						UserNotificationDefinition.
							NOTIFICATION_TYPE_UPDATE_ENTRY)) {

					return userNotificationEvent;
				}
			}
		}

		return null;
	}

	private Bundle _stopJournalServiceBundle() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			PublicationUserNotificationHandlerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (Bundle curBundle : bundleContext.getBundles()) {
			if (Objects.equals(
					curBundle.getSymbolicName(),
					"com.liferay.journal.service") &&
				(curBundle.getState() == Bundle.ACTIVE)) {

				curBundle.stop();

				return curBundle;
			}
		}

		return null;
	}

	@Inject
	private static Portal _portal;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(filter = "jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS)
	private UserNotificationHandler _userNotificationHandler;

}