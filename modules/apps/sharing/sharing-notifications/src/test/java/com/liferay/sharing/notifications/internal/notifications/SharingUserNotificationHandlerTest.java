/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.notifications.internal.notifications;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.UserNotificationEventWrapper;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

/**
 * @author Alicia García
 */
public class SharingUserNotificationHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_assetRendererFactoryRegistryUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws PortalException {
		ReflectionTestUtil.setFieldValue(
			_sharingUserNotificationHandler, "_jsonFactory",
			new JSONFactoryImpl());

		_setUpServiceContext();
		_setUpSharingEntryLocalService();
	}

	@Test
	public void testGetLink() throws Exception {
		_testGetLink(false);
		_testGetLink(true);
		_testGetLinkWhenSharingEntryIsNull();
	}

	@Test
	@TestInfo("LPD-114364")
	public void testGetLinkWhenAssetRendererIsNull() throws Exception {
		_setUpSharingEntry();

		_setUpAssetRendererFactoryRegistryUtil(
			_sharingEntry.getClassName(), null, _sharingEntry.getClassPK());

		String entryURL = _getEntryURL("/shared_assets/view_sharing_entry");

		Assert.assertEquals(entryURL, _getLink(entryURL));
	}

	@Test
	@TestInfo("LPD-114364")
	public void testGetLinkWhenEntryURLMVCRenderCommandNameIsCurrent()
		throws Exception {

		_setUpSharingEntry();

		_setUpAssetRendererFactoryRegistryUtil(
			_sharingEntry.getClassName(), Mockito.mock(AssetRenderer.class),
			_sharingEntry.getClassPK());

		String entryURL = _getEntryURL("/sharing/view_sharing_entry");

		try (MockedStatic<PortletProviderUtil> portletProviderUtilMockedStatic =
				Mockito.mockStatic(PortletProviderUtil.class)) {

			Assert.assertEquals(entryURL, _getLink(entryURL));

			portletProviderUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	@TestInfo("LPD-114364")
	public void testGetLinkWhenEntryURLMVCRenderCommandNameIsRemoved()
		throws Exception {

		_setUpSharingEntry();

		_setUpAssetRendererFactoryRegistryUtil(
			_sharingEntry.getClassName(), Mockito.mock(AssetRenderer.class),
			_sharingEntry.getClassPK());

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			_themeDisplay.getRequest()
		).thenReturn(
			httpServletRequest
		);

		MockLiferayPortletURL mockLiferayPortletURL =
			new MockLiferayPortletURL();

		try (MockedStatic<PortletProviderUtil> portletProviderUtilMockedStatic =
				Mockito.mockStatic(PortletProviderUtil.class)) {

			portletProviderUtilMockedStatic.when(
				() -> PortletProviderUtil.getPortletURL(
					httpServletRequest, SharingEntry.class.getName(),
					PortletProvider.Action.PREVIEW)
			).thenReturn(
				mockLiferayPortletURL
			);

			String link = _getLink(
				_getEntryURL("/shared_assets/view_sharing_entry"));

			Assert.assertEquals(
				String.valueOf(_sharingEntry.getClassNameId()),
				mockLiferayPortletURL.getParameter("classNameId"));
			Assert.assertEquals(
				String.valueOf(_sharingEntry.getClassPK()),
				mockLiferayPortletURL.getParameter("classPK"));
			Assert.assertEquals(
				String.valueOf(_sharingEntry.getSharingEntryId()),
				mockLiferayPortletURL.getParameter("sharingEntryId"));

			Assert.assertEquals(mockLiferayPortletURL.toString(), link);
		}
	}

	@Test
	@TestInfo("LPD-114364")
	public void testGetLinkWhenPreviewPortletURLIsNull() throws Exception {
		_setUpSharingEntry();

		_setUpAssetRendererFactoryRegistryUtil(
			_sharingEntry.getClassName(), Mockito.mock(AssetRenderer.class),
			_sharingEntry.getClassPK());

		String entryURL = _getEntryURL("/shared_assets/view_sharing_entry");

		try (MockedStatic<PortletProviderUtil> portletProviderUtilMockedStatic =
				Mockito.mockStatic(PortletProviderUtil.class)) {

			Assert.assertEquals(entryURL, _getLink(entryURL));
		}
	}

	@Test
	@TestInfo("LPD-108755")
	public void testGetTitleWhenAssetRendererIsNull() throws Exception {
		_testGetTitleWhenAssetRendererIsNull(
			Mockito.mock(AssetEntry.class), Mockito.never());
		_testGetTitleWhenAssetRendererIsNull(null, Mockito.times(1));
	}

	protected UserNotificationEvent mockUserNotificationEvent(long classPK) {
		return _mockUserNotificationEvent(JSONUtil.put("classPK", classPK));
	}

	private String _getEntryURL(String mvcRenderCommandName) {
		return StringBundler.concat(
			"http://localhost:8080/group/guest/~/control_panel/manage",
			"?p_p_id=com_liferay_sharing_web_portlet_SharedAssetsPortlet",
			"&p_p_lifecycle=0&_com_liferay_sharing_web_portlet_",
			"SharedAssetsPortlet_mvcRenderCommandName=",
			URLCodec.encodeURL(mvcRenderCommandName),
			"&_com_liferay_sharing_web_portlet_SharedAssetsPortlet_",
			"sharingEntryId=", _sharingEntry.getSharingEntryId());
	}

	private String _getLink(String entryURL) {
		return ReflectionTestUtil.invoke(
			_sharingUserNotificationHandler, "getLink",
			new Class<?>[] {UserNotificationEvent.class, ServiceContext.class},
			_mockUserNotificationEvent(
				JSONUtil.put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"entryURL", entryURL
				)),
			_serviceContext);
	}

	private UserNotificationEvent _mockUserNotificationEvent(
		JSONObject jsonObject) {

		return new UserNotificationEventWrapper(null) {

			@Override
			public String getPayload() {
				return jsonObject.toString();
			}

			@Override
			public long getUserNotificationEventId() {
				return 0;
			}

		};
	}

	private void _setUpAssetRendererFactoryRegistryUtil(
			String className, AssetRenderer<Object> assetRenderer, long classPK)
		throws Exception {

		AssetRendererFactory<Object> assetRendererFactory = Mockito.mock(
			AssetRendererFactory.class);

		Mockito.when(
			assetRendererFactory.getAssetRenderer(classPK)
		).thenReturn(
			assetRenderer
		);

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() ->
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className)
		).thenReturn(
			assetRendererFactory
		);
	}

	private void _setUpServiceContext() throws PortalException {
		Mockito.when(
			_serviceContext.getCurrentURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_serviceContext.getPortalURL()
		).thenReturn(
			"http://localhost:8080"
		);

		Mockito.when(
			_serviceContext.getRequest()
		).thenReturn(
			Mockito.mock(HttpServletRequest.class)
		);

		Mockito.when(
			_serviceContext.getScopeGroup()
		).thenReturn(
			Mockito.mock(Group.class)
		);

		Mockito.when(
			_serviceContext.getThemeDisplay()
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_serviceContext.getUserId()
		).thenReturn(
			_USER_ID
		);
	}

	private void _setUpSharingEntry() {
		Mockito.reset(_sharingEntry);

		Mockito.when(
			_sharingEntry.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_sharingEntry.getClassNameId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_sharingEntry.getClassPK()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_sharingEntry.getSharingEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	private void _setUpSharingEntryLocalService() {
		SharingEntryLocalService sharingEntryLocalService = Mockito.mock(
			SharingEntryLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_sharingUserNotificationHandler, "_sharingEntryLocalService",
			sharingEntryLocalService);

		Mockito.when(
			sharingEntryLocalService.fetchSharingEntry(Mockito.anyLong())
		).thenReturn(
			_sharingEntry
		);
	}

	private void _testGetLink(boolean hasUpdatePermission) throws Exception {
		Mockito.reset(_sharingEntry);

		String className = RandomTestUtil.randomString();

		Mockito.when(
			_sharingEntry.getClassName()
		).thenReturn(
			className
		);

		long classPK = RandomTestUtil.randomLong();

		Mockito.when(
			_sharingEntry.getClassPK()
		).thenReturn(
			classPK
		);

		Mockito.when(
			_sharingEntry.hasSharingPermission(SharingEntryAction.UPDATE)
		).thenReturn(
			hasUpdatePermission
		);

		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		_setUpAssetRendererFactoryRegistryUtil(
			className, assetRenderer, classPK);

		String expectedURL = null;

		if (hasUpdatePermission) {
			expectedURL = StringBundler.concat(
				"http://localhost:8080/c/portal/cms",
				"/edit_content_item?objectEntryId=", classPK,
				"&p_l_mode=edit&redirect=http://localhost:8080");
		}
		else {
			expectedURL = StringBundler.concat(
				"http://localhost:8080/web/cms/view-asset?objectEntryId=",
				classPK, "&backURL=http://localhost:8080");
		}

		Mockito.when(
			assetRenderer.getURLSharingNotification(
				Mockito.eq(hasUpdatePermission),
				Mockito.any(ThemeDisplay.class))
		).thenReturn(
			expectedURL
		);

		String link = ReflectionTestUtil.invoke(
			_sharingUserNotificationHandler, "getLink",
			new Class<?>[] {UserNotificationEvent.class, ServiceContext.class},
			mockUserNotificationEvent(classPK), _serviceContext);

		Assert.assertEquals(expectedURL, link);
	}

	private void _testGetLinkWhenSharingEntryIsNull() {
		SharingEntryLocalService sharingEntryLocalService = Mockito.mock(
			SharingEntryLocalService.class);

		Mockito.when(
			sharingEntryLocalService.fetchSharingEntry(Mockito.anyLong())
		).thenReturn(
			null
		);

		ReflectionTestUtil.setFieldValue(
			_sharingUserNotificationHandler, "_sharingEntryLocalService",
			sharingEntryLocalService);

		String link = ReflectionTestUtil.invoke(
			_sharingUserNotificationHandler, "getLink",
			new Class<?>[] {UserNotificationEvent.class, ServiceContext.class},
			mockUserNotificationEvent(RandomTestUtil.randomLong()),
			_serviceContext);

		Assert.assertEquals(StringPool.BLANK, link);
	}

	private void _testGetTitleWhenAssetRendererIsNull(
			AssetEntry assetEntry, VerificationMode verificationMode)
		throws Exception {

		Mockito.reset(_sharingEntry);

		String className = RandomTestUtil.randomString();

		Mockito.when(
			_sharingEntry.getClassName()
		).thenReturn(
			className
		);

		long classPK = RandomTestUtil.randomLong();

		Mockito.when(
			_sharingEntry.getClassPK()
		).thenReturn(
			classPK
		);

		_setUpAssetRendererFactoryRegistryUtil(className, null, classPK);

		_setUpSharingEntryLocalService();

		AssetEntryLocalService assetEntryLocalService = Mockito.mock(
			AssetEntryLocalService.class);

		Mockito.when(
			assetEntryLocalService.fetchEntry(className, classPK)
		).thenReturn(
			assetEntry
		);

		ReflectionTestUtil.setFieldValue(
			_sharingUserNotificationHandler, "_assetEntryLocalService",
			assetEntryLocalService);

		UserNotificationEventLocalService userNotificationEventLocalService =
			Mockito.mock(UserNotificationEventLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_sharingUserNotificationHandler,
			"_userNotificationEventLocalService",
			userNotificationEventLocalService);

		Assert.assertNull(
			ReflectionTestUtil.invoke(
				_sharingUserNotificationHandler, "getTitle",
				new Class<?>[] {
					UserNotificationEvent.class, ServiceContext.class
				},
				mockUserNotificationEvent(classPK), _serviceContext));

		Mockito.verify(
			userNotificationEventLocalService, verificationMode
		).deleteUserNotificationEvent(
			Mockito.anyLong()
		);
	}

	private static final Long _USER_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);
	private static final SharingEntry _sharingEntry = Mockito.mock(
		SharingEntry.class);
	private static final SharingUserNotificationHandler
		_sharingUserNotificationHandler = new SharingUserNotificationHandler();
	private static final ThemeDisplay _themeDisplay = Mockito.mock(
		ThemeDisplay.class);

	private final ServiceContext _serviceContext = Mockito.mock(
		ServiceContext.class);

}