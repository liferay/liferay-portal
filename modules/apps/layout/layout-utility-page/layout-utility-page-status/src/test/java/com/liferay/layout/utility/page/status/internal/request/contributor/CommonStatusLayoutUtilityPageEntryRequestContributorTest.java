/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.status.internal.request.contributor;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.I18nServlet;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalInstances;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class CommonStatusLayoutUtilityPageEntryRequestContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_i18nServletMockedStatic.when(
			I18nServlet::getLanguageIds
		).thenReturn(
			SetUtil.fromString(
				StringPool.SLASH +
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()))
		);

		_originalPermissionChecker = Mockito.mock(PermissionChecker.class);

		_permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			_originalPermissionChecker
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_i18nServletMockedStatic.close();
		_permissionThreadLocalMockedStatic.close();
		_portalInstancesMockedStatic.close();
	}

	@Before
	public void setUp() {
		_setUpCommonStatusLayoutUtilityPageEntryRequestContributor();

		_permissionThreadLocalMockedStatic.clearInvocations();
		_portalInstancesMockedStatic.reset();
	}

	@Test
	public void testAddAttributesAndParametersWithDefaultVirtualHostAndWithoutCurrentURL()
		throws PortalException {

		_testAddAttributesAndParameters(
			null,
			_getDynamicServletRequest(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			null, null, null, null, null, 0);
	}

	@Test
	public void testAddAttributesAndParametersWithoutVirtualHostAndWithoutCurrentURL()
		throws PortalException {

		_testAddAttributesAndParameters(
			null, _getDynamicServletRequest(RandomTestUtil.randomString(), 0L),
			null, null, null, null, null, 0);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithContextPath()
		throws PortalException {

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		_testAddAttributesAndParameters(
			null,
			_getDynamicServletRequest(_PATH_CONTEXT, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), null,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			RandomTestUtil.randomString(), 1);
	}

	@Test
	@TestInfo("LPD-56619")
	public void testAddAttributesAndParametersWithVirtualHostAndWithCurrentURLWithInactiveGroup()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		String groupFriendlyURL =
			StringPool.SLASH + RandomTestUtil.randomString();

		_mockGroupLocalService(
			layout.getCompanyId(),
			_mockGroup(
				false, layout.getCompanyId(), RandomTestUtil.randomLong(),
				groupFriendlyURL),
			groupFriendlyURL);

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				StringPool.SLASH, RandomTestUtil.randomString(), "/test/test"),
			_getDynamicServletRequest(_PATH_CONTEXT, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), languageId,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			_PATH_PROXY, 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithCurrentURLWithoutValidGroup()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		String groupFriendlyURL =
			StringPool.SLASH + RandomTestUtil.randomString();

		_mockGroupLocalService(layout.getCompanyId(), null, groupFriendlyURL);

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				StringPool.SLASH, RandomTestUtil.randomString(), "/test/test"),
			_getDynamicServletRequest(_PATH_CONTEXT, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), languageId,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			_PATH_PROXY, 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithCurrentURLWithValidGroupWithLayouts()
		throws PortalException {

		long companyId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();
		String groupFriendlyURL =
			StringPool.SLASH + RandomTestUtil.randomString();
		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		Layout layout = _mockLayout(companyId, groupId);
		Layout virtualHostGroupLayout = _mockLayout(
			companyId, RandomTestUtil.randomLong());

		Group group = _mockGroup(true, companyId, groupId, groupFriendlyURL);

		_mockGroupLocalService(companyId, group, groupFriendlyURL);

		_mockLayoutLocalService(groupId, layout, null);

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				groupFriendlyURL, "/test/test"),
			_getDynamicServletRequest(_PATH_CONTEXT, companyId),
			String.valueOf(group.getGroupId()), languageId,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				companyId, virtualHostGroupLayout.getGroupId(),
				virtualHostGroupLayout, null),
			_PATH_PROXY, 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithCurrentURLWithValidGroupWithLayoutsWithoutViewPermission()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		String groupFriendlyURL =
			StringPool.SLASH + RandomTestUtil.randomString();

		Layout virtualHostGroupLayout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		Group group = _mockGroup(
			true, virtualHostGroupLayout.getCompanyId(),
			RandomTestUtil.randomLong(), groupFriendlyURL);

		_mockGroupLocalService(
			virtualHostGroupLayout.getCompanyId(), group, groupFriendlyURL);

		_mockLayoutLocalService(group.getGroupId(), null, null);

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				groupFriendlyURL, "/test/test"),
			_getDynamicServletRequest(
				_PATH_CONTEXT, virtualHostGroupLayout.getCompanyId()),
			String.valueOf(virtualHostGroupLayout.getGroupId()), languageId,
			String.valueOf(virtualHostGroupLayout.getLayoutId()),
			_getLayoutSet(
				virtualHostGroupLayout.getCompanyId(),
				virtualHostGroupLayout.getGroupId(), virtualHostGroupLayout,
				null),
			_PATH_PROXY, 2);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithCurrentURLWithValidGroupWithoutLayouts()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		String groupFriendlyURL =
			StringPool.SLASH + RandomTestUtil.randomString();

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		Group group = _mockGroup(
			true, layout.getCompanyId(), RandomTestUtil.randomLong(),
			groupFriendlyURL);

		_mockGroupLocalService(layout.getCompanyId(), group, groupFriendlyURL);

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				groupFriendlyURL, "/test/test"),
			_getDynamicServletRequest(_PATH_CONTEXT, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), languageId,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			_PATH_PROXY, 2);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithInvalidCurrentURL()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				StringPool.SLASH + RandomTestUtil.randomString()),
			_getDynamicServletRequest(_PATH_CONTEXT, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), languageId,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			_PATH_PROXY, 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostAndWithLanguageId()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				StringPool.SLASH),
			_getDynamicServletRequest(_PATH_CONTEXT, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), languageId,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			_PATH_PROXY, 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostWithoutLayoutsAndWithCurrentURLWithValidGroupWithoutLayouts()
		throws PortalException {

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		String groupFriendlyURL =
			StringPool.SLASH + RandomTestUtil.randomString();

		Group group = _mockGroup(
			true, RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			groupFriendlyURL);

		_mockGroupLocalService(group.getCompanyId(), group, groupFriendlyURL);

		_testAddAttributesAndParameters(
			StringBundler.concat(
				_PATH_PROXY, _PATH_CONTEXT, StringPool.SLASH, languageId,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				groupFriendlyURL, "/test/test"),
			_getDynamicServletRequest(_PATH_CONTEXT, group.getCompanyId()),
			null, null, null,
			_getLayoutSet(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong(), null,
				null),
			_PATH_PROXY, 2);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostWithoutLayoutsAndWithoutCurrentURL()
		throws PortalException {

		_testAddAttributesAndParameters(
			null,
			_getDynamicServletRequest(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			null, null, null, null, null, 0);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostWithPathProxy()
		throws PortalException {

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		_testAddAttributesAndParameters(
			null, _getDynamicServletRequest(null, layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), null,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			RandomTestUtil.randomString(), 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostWithPrivateLayoutAndWithoutCurrentURL()
		throws PortalException {

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		_testAddAttributesAndParameters(
			null,
			_getDynamicServletRequest(
				RandomTestUtil.randomString(), layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), null,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), null, layout),
			null, 1);
	}

	@Test
	public void testAddAttributesAndParametersWithVirtualHostWithPublicLayoutAndWithoutCurrentURL()
		throws PortalException {

		Layout layout = _mockLayout(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		_testAddAttributesAndParameters(
			null,
			_getDynamicServletRequest(
				RandomTestUtil.randomString(), layout.getCompanyId()),
			String.valueOf(layout.getGroupId()), null,
			String.valueOf(layout.getLayoutId()),
			_getLayoutSet(
				layout.getCompanyId(), layout.getGroupId(), layout, null),
			null, 1);
	}

	private DynamicServletRequest _getDynamicServletRequest(
		String contextPath, Long companyId) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContextPath(contextPath);

		mockHttpServletRequest.setAttribute(WebKeys.COMPANY_ID, companyId);

		return new DynamicServletRequest(mockHttpServletRequest);
	}

	private LayoutSet _getLayoutSet(
			long companyId, long groupId, Layout privateLayout,
			Layout publicLayout)
		throws PortalException {

		Group group = _mockGroup(true, companyId, groupId, null);

		_mockLayoutLocalService(groupId, publicLayout, privateLayout);

		LayoutSet layoutSet = Mockito.mock(LayoutSet.class);

		Mockito.when(
			layoutSet.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			layoutSet.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			layoutSet.getGroup()
		).thenReturn(
			group
		);

		_setUpPortalInstancesMockedStatic(layoutSet);

		return layoutSet;
	}

	private Group _mockGroup(
		boolean active, long companyId, long groupId, String friendlyURL) {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			friendlyURL
		);

		Mockito.when(
			group.isActive()
		).thenReturn(
			active
		);

		return group;
	}

	private void _mockGroupLocalService(
		long companyId, Group group, String groupFriendlyURL) {

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				companyId, groupFriendlyURL)
		).thenReturn(
			group
		);
	}

	private Layout _mockLayout(long companyId, long groupId) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			layout.getLayoutId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return layout;
	}

	private void _mockLayoutLocalService(
		long groupId, Layout privateLayout, Layout publicLayout) {

		Mockito.when(
			_layoutService.fetchFirstLayout(groupId, false, false)
		).thenReturn(
			publicLayout
		);

		Mockito.when(
			_layoutService.fetchFirstLayout(groupId, true, false)
		).thenReturn(
			privateLayout
		);
	}

	private void _mockPortal(String currentURL, String pathProxy)
		throws PortalException {

		Mockito.when(
			_portal.getCurrentURL(Mockito.any(DynamicServletRequest.class))
		).thenReturn(
			currentURL
		);

		Mockito.when(
			_portal.getPathProxy()
		).thenReturn(
			pathProxy
		);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_portal.getUser(Mockito.any(DynamicServletRequest.class))
		).thenReturn(
			user
		);
	}

	private void _setUpCommonStatusLayoutUtilityPageEntryRequestContributor() {
		_commonStatusLayoutUtilityPageEntryRequestContributor =
			new CommonStatusLayoutUtilityPageEntryRequestContributor();

		_groupLocalService = Mockito.mock(GroupLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_commonStatusLayoutUtilityPageEntryRequestContributor,
			"_groupLocalService", _groupLocalService);

		_layoutService = Mockito.mock(LayoutService.class);

		ReflectionTestUtil.setFieldValue(
			_commonStatusLayoutUtilityPageEntryRequestContributor,
			"_layoutService", _layoutService);

		_setUpPermissionCheckerFactory();

		ReflectionTestUtil.setFieldValue(
			_commonStatusLayoutUtilityPageEntryRequestContributor,
			"_permissionCheckerFactory", _permissionCheckerFactory);

		_portal = Mockito.mock(Portal.class);

		ReflectionTestUtil.setFieldValue(
			_commonStatusLayoutUtilityPageEntryRequestContributor, "_portal",
			_portal);

		_userLocalService = Mockito.mock(UserLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_commonStatusLayoutUtilityPageEntryRequestContributor,
			"_userLocalService", _userLocalService);
	}

	private void _setUpPermissionCheckerFactory() {
		_permissionCheckerFactory = Mockito.mock(
			PermissionCheckerFactory.class);

		_permissionChecker = Mockito.mock(PermissionChecker.class);

		Mockito.when(
			_permissionCheckerFactory.create(Mockito.any(User.class))
		).thenReturn(
			_permissionChecker
		);
	}

	private void _setUpPortalInstancesMockedStatic(LayoutSet layoutSet) {
		_portalInstancesMockedStatic.when(
			() -> PortalInstances.getCompanyId(
				Mockito.any(HttpServletRequest.class))
		).thenAnswer(
			invocationOnMock -> {
				HttpServletRequest httpServletRequest =
					invocationOnMock.getArgument(0, HttpServletRequest.class);

				httpServletRequest.setAttribute(
					WebKeys.VIRTUAL_HOST_LAYOUT_SET, layoutSet);

				return httpServletRequest.getAttribute(WebKeys.COMPANY_ID);
			}
		);
	}

	private void _testAddAttributesAndParameters(
			String currentURL, DynamicServletRequest dynamicServletRequest,
			String groupId, String languageId, String layoutId,
			LayoutSet layoutSet, String pathProxy,
			int wantedNumberOfInvocations)
		throws PortalException {

		_mockPortal(currentURL, pathProxy);

		_commonStatusLayoutUtilityPageEntryRequestContributor.
			addAttributesAndParameters(dynamicServletRequest);

		Assert.assertEquals(
			groupId, dynamicServletRequest.getParameter("groupId"));
		Assert.assertEquals(
			layoutId, dynamicServletRequest.getParameter("layoutId"));
		Assert.assertEquals(
			layoutSet,
			dynamicServletRequest.getAttribute(
				WebKeys.VIRTUAL_HOST_LAYOUT_SET));
		Assert.assertEquals(
			languageId,
			dynamicServletRequest.getAttribute(WebKeys.I18N_LANGUAGE_ID));

		_permissionThreadLocalMockedStatic.verify(
			() -> PermissionThreadLocal.setPermissionChecker(
				_permissionChecker),
			Mockito.times(wantedNumberOfInvocations));

		_permissionThreadLocalMockedStatic.verify(
			() -> PermissionThreadLocal.setPermissionChecker(
				_originalPermissionChecker),
			Mockito.times(wantedNumberOfInvocations));

		_portalInstancesMockedStatic.verify(
			() -> PortalInstances.getCompanyId(dynamicServletRequest));
	}

	private static final String _PATH_CONTEXT = "/context";

	private static final String _PATH_PROXY = "/proxy";

	private static final MockedStatic<I18nServlet> _i18nServletMockedStatic =
		Mockito.mockStatic(I18nServlet.class);
	private static PermissionChecker _originalPermissionChecker;
	private static final MockedStatic<PermissionThreadLocal>
		_permissionThreadLocalMockedStatic = Mockito.mockStatic(
			PermissionThreadLocal.class);
	private static final MockedStatic<PortalInstances>
		_portalInstancesMockedStatic = Mockito.mockStatic(
			PortalInstances.class);

	private CommonStatusLayoutUtilityPageEntryRequestContributor
		_commonStatusLayoutUtilityPageEntryRequestContributor;
	private GroupLocalService _groupLocalService;
	private LayoutService _layoutService;
	private PermissionChecker _permissionChecker;
	private PermissionCheckerFactory _permissionCheckerFactory;
	private Portal _portal;
	private UserLocalService _userLocalService;

}