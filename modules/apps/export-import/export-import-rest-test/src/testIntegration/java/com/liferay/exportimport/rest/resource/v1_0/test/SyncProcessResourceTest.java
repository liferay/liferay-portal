/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.SyncProcessRequest;
import com.liferay.exportimport.rest.client.resource.v1_0.SyncProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 * @author Jaime Leon
 */
@FeatureFlag("LPD-82107")
@RunWith(Arquillian.class)
public class SyncProcessResourceTest extends BaseSyncProcessResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		_group = GroupTestUtil.addGroup();

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			true);

		_waitForMerge(layout);

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_syncProcessResource = SyncProcessResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_userLocalService.deleteUser(_user);
	}

	@Override
	@Test
	public void testPostSiteTemplateSyncProcess() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		assertHttpResponseStatusCode(
			204,
			syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
				_layoutSetPrototype.getLayoutSetPrototypeId(),
				_toSyncProcessRequest()));

		_waitForMerge(layout);

		_testPostSiteTemplateSyncProcessWithFeatureFlagDisabled();
		_testPostSiteTemplateSyncProcessWithInactiveLayoutSetPrototype();
		_testPostSiteTemplateSyncProcessWithLayoutSelection();
		_testPostSiteTemplateSyncProcessWithoutPermission();
		_testPostSiteTemplateSyncProcessWithoutPortletDataHandlers();
	}

	private void _testPostSiteTemplateSyncProcessWithFeatureFlagDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-82107")) {

			assertHttpResponseStatusCode(
				404,
				syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
					_layoutSetPrototype.getLayoutSetPrototypeId(),
					_toSyncProcessRequest()));
		}
	}

	private void _testPostSiteTemplateSyncProcessWithInactiveLayoutSetPrototype()
		throws Exception {

		_inactiveLayoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_inactiveLayoutSetPrototype.setActive(false);

		_inactiveLayoutSetPrototype =
			_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
				_inactiveLayoutSetPrototype);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				400,
				syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
					_inactiveLayoutSetPrototype.getLayoutSetPrototypeId(),
					_toSyncProcessRequest()));
		}
	}

	private void _testPostSiteTemplateSyncProcessWithLayoutSelection()
		throws Exception {

		Layout syncedLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);
		Layout skippedLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		int initialCount = _layoutLocalService.getLayoutsCount(
			_group.getGroupId(), false);

		syncProcessResource.postSiteTemplateSyncProcess(
			_layoutSetPrototype.getLayoutSetPrototypeId(),
			_toSyncProcessRequest(syncedLayout));

		_waitForMerge(syncedLayout);

		Assert.assertEquals(
			initialCount + 1,
			_layoutLocalService.getLayoutsCount(_group.getGroupId(), false));
		Assert.assertNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), false, skippedLayout.getFriendlyURL()));
	}

	private void _testPostSiteTemplateSyncProcessWithoutPermission()
		throws Exception {

		assertHttpResponseStatusCode(
			403,
			_syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
				_layoutSetPrototype.getLayoutSetPrototypeId(),
				_toSyncProcessRequest()));
	}

	private void _testPostSiteTemplateSyncProcessWithoutPortletDataHandlers()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				400,
				syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
					_layoutSetPrototype.getLayoutSetPrototypeId(),
					new SyncProcessRequest()));
			assertHttpResponseStatusCode(
				400,
				syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
					_layoutSetPrototype.getLayoutSetPrototypeId(),
					new SyncProcessRequest() {
						{
							portletDataHandlers = new PortletDataHandler[0];
						}
					}));
			assertHttpResponseStatusCode(
				400,
				syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
					_layoutSetPrototype.getLayoutSetPrototypeId(),
					new SyncProcessRequest() {
						{
							portletDataHandlers = new PortletDataHandler[] {
								null
							};
						}
					}));

			SyncProcessRequest syncProcessRequest = _toSyncProcessRequest();

			for (PortletDataHandler portletDataHandler :
					syncProcessRequest.getPortletDataHandlers()) {

				portletDataHandler.setSelected(false);
			}

			assertHttpResponseStatusCode(
				400,
				syncProcessResource.postSiteTemplateSyncProcessHttpResponse(
					_layoutSetPrototype.getLayoutSetPrototypeId(),
					syncProcessRequest));
		}
	}

	private SyncProcessRequest _toSyncProcessRequest(Layout... layouts) {
		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			new PortletDataHandlerTreeSelection() {
				{
					itemExternalReferenceCodes = TransformUtil.transform(
						layouts, Layout::getExternalReferenceCode,
						String.class);
					name = "_layout_set_pages";
					selectAll = ArrayUtil.isEmpty(layouts);
					selected = true;
					type = PortletDataHandlerControl.Type.TREE_SELECTION;
				}
			};

		return new SyncProcessRequest() {
			{
				portletDataHandlers = new PortletDataHandler[] {
					new PortletDataHandler() {
						{
							name =
								"PORTLET_DATA_" +
									LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;
							portletDataHandlerControls =
								new PortletDataHandlerControl[] {
									new PortletDataHandlerChoice() {
										{
											choice = "private-pages";
											name = "_layout_set_visibility";
											portletDataHandlerControls =
												new PortletDataHandlerControl[]
													 {
														portletDataHandlerTreeSelection
													};
											type =
												PortletDataHandlerControl.Type.
													CHOICE;
										}
									}
								};
						}
					}
				};
			}
		};
	}

	private void _waitForMerge(Layout layout) throws Exception {
		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
			() -> {
				Assert.assertNotNull(
					_layoutLocalService.fetchLayoutByFriendlyURL(
						_group.getGroupId(), false, layout.getFriendlyURL()));
				Assert.assertEquals(
					0,
					BackgroundTaskManagerUtil.getBackgroundTasksCount(
						_group.getGroupId(),
						BackgroundTaskExecutorNames.
							LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR,
						false));
			});
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private LayoutSetPrototype _inactiveLayoutSetPrototype;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	private Group _layoutSetPrototypeGroup;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private Sites _sites;

	private SyncProcessResource _syncProcessResource;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}