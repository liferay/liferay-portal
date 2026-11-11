/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class ProductMenuSidebarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_productMenuSidebarDisplayContext = Mockito.mock(
			ProductMenuSidebarDisplayContext.class, Mockito.CALLS_REAL_METHODS);

		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_httpServletRequest",
			_httpServletRequest);
		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_panelAppRegistry",
			_panelAppRegistry);
		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_panelCategory",
			_createPanelCategory(PanelCategoryKeys.SITE_ADMINISTRATION));
		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_panelCategoryHelper",
			_panelCategoryHelper);
		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_showPageTree", false);

		_themeDisplay.setLocale(LocaleUtil.US);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_scopeGroup", _group);

		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_themeDisplay", _themeDisplay);
	}

	@Test
	public void testGetExpandedKeysExpandsEveryCategoryByDefault() {
		_setUpRootChildPanelCategories(
			_createPanelCategory(PanelCategoryKeys.SITE_ADMINISTRATION_BUILD),
			_createPanelCategory(
				PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT));

		try (MockedStatic<SessionClicks> sessionClicksMockedStatic =
				Mockito.mockStatic(SessionClicks.class)) {

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					Mockito.eq(_httpServletRequest),
					Mockito.eq(_SESSION_KEY_EXPANDED_KEYS), Mockito.anyString())
			).thenReturn(
				""
			);

			Assert.assertEquals(
				List.of(
					PanelCategoryKeys.SITE_ADMINISTRATION_BUILD,
					PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT),
				_getExpandedKeys());
		}
	}

	@Test
	public void testGetExpandedKeysReturnsTheSessionKeys() {
		try (MockedStatic<SessionClicks> sessionClicksMockedStatic =
				Mockito.mockStatic(SessionClicks.class)) {

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					Mockito.eq(_httpServletRequest),
					Mockito.eq(_SESSION_KEY_EXPANDED_KEYS), Mockito.anyString())
			).thenReturn(
				PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT
			);

			Assert.assertEquals(
				List.of(PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT),
				_getExpandedKeys());
		}
	}

	@Test
	public void testGetImageURLReturnsTheSiteLogo() {
		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.isSiteLogo()
		).thenReturn(
			true
		);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_company", company);

		String logoURL = RandomTestUtil.randomString();

		Mockito.when(
			_group.getLogoURL(_themeDisplay, true)
		).thenReturn(
			logoURL
		);

		Assert.assertEquals(logoURL, _getImageURL());
	}

	@Test
	public void testGetImageURLWhenSiteLogoIsDisabled() {
		ReflectionTestUtil.setFieldValue(
			_themeDisplay, "_company", Mockito.mock(Company.class));

		String companyLogo = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			_themeDisplay, "_companyLogo", companyLogo);

		Assert.assertEquals(companyLogo, _getImageURL());
	}

	@Test
	public void testGetLabelReturnsMySiteForThePersonalSite() throws Exception {
		long userId = RandomTestUtil.randomLong();

		_setUpPersonalSite(userId);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getUserId()
		).thenReturn(
			userId
		);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_user", user);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class), Mockito.eq("my-site"))
		).thenReturn(
			"My Site"
		);

		_setUpLanguage(language);

		Assert.assertEquals("My Site", _getLabel());
	}

	@Test
	public void testGetLabelReturnsTheOwnerNameForAnotherPersonalSite()
		throws Exception {

		long ownerUserId = RandomTestUtil.randomLong();

		_setUpPersonalSite(ownerUserId);

		ReflectionTestUtil.setFieldValue(
			_themeDisplay, "_user", Mockito.mock(User.class));

		String fullName = RandomTestUtil.randomString();
		User ownerUser = Mockito.mock(User.class);

		Mockito.when(
			ownerUser.getFullName()
		).thenReturn(
			fullName
		);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.format(LocaleUtil.US, "x-site", fullName)
		).thenReturn(
			fullName + "'s Site"
		);

		_setUpLanguage(language);

		try (MockedStatic<UserLocalServiceUtil>
				userLocalServiceUtilMockedStatic = Mockito.mockStatic(
					UserLocalServiceUtil.class)) {

			userLocalServiceUtilMockedStatic.when(
				() -> UserLocalServiceUtil.getUser(ownerUserId)
			).thenReturn(
				ownerUser
			);

			Assert.assertEquals(fullName + "'s Site", _getLabel());
		}
	}

	@Test
	public void testGetLabelReturnsTheSiteName() throws Exception {
		Mockito.when(
			_group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Liferay"
		);

		Assert.assertEquals("Liferay", _getLabel());
	}

	@Test
	public void testGetPropsItemsGroupsTheApplicationsByCategory()
		throws Exception {

		PanelApp panelApp = _createPanelApp(RandomTestUtil.randomString());

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				Mockito.eq(PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT),
				Mockito.nullable(PermissionChecker.class), Mockito.eq(_group))
		).thenReturn(
			List.of(panelApp)
		);

		_setUpRootChildPanelCategories(
			_createPanelCategory(
				PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertEquals(propsItems.toString(), 1, propsItems.size());

		Map<String, Object> propsItem = propsItems.get(0);

		Assert.assertEquals(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT, propsItem.get("id"));

		List<Map<String, Object>> childPropsItems =
			(List<Map<String, Object>>)propsItem.get("items");

		Assert.assertEquals(
			childPropsItems.toString(), 1, childPropsItems.size());
	}

	@Test
	public void testGetPropsItemsListsHomeAndThePageTreeFirst()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			_productMenuSidebarDisplayContext, "_showPageTree", true);

		Mockito.when(
			_group.getDisplayURL(_themeDisplay, false)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		_setUpLanguage(language);

		PanelApp panelApp = _createPanelApp("blogs");

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				Mockito.eq(PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT),
				Mockito.nullable(PermissionChecker.class), Mockito.eq(_group))
		).thenReturn(
			List.of(panelApp)
		);

		_setUpRootChildPanelCategories(
			_createPanelCategory(
				PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertEquals(propsItems.toString(), 3, propsItems.size());

		Map<String, Object> homePropsItem = propsItems.get(0);

		Assert.assertEquals("home", homePropsItem.get("id"));

		Map<String, Object> pageTreePropsItem = propsItems.get(1);

		Assert.assertEquals(Boolean.FALSE, pageTreePropsItem.get("disabled"));
		Assert.assertEquals("pageTree", pageTreePropsItem.get("id"));
		Assert.assertEquals(Boolean.TRUE, pageTreePropsItem.get("pageTree"));

		Map<String, Object> categoryPropsItem = propsItems.get(2);

		Assert.assertEquals(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
			categoryPropsItem.get("id"));
	}

	@Test
	public void testGetPropsItemsOmitsACategoryWithoutApplications() {
		_setUpRootChildPanelCategories(
			_createPanelCategory(
				PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertTrue(propsItems.isEmpty());
	}

	@Test
	public void testGetSelectedPortletIdKeepsThePortletOnAnotherPageSet() {
		String portletId = RandomTestUtil.randomString();

		_themeDisplay.setPpid(portletId);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.isPrivateLayout()
		).thenReturn(
			true
		);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_layout", layout);

		Mockito.when(
			_group.getDisplayURL(_themeDisplay, false)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Assert.assertEquals(portletId, _getSelectedPortletId());
	}

	@Test
	public void testGetSelectedPortletIdReturnsHomeOnTheFirstPage() {
		Layout layout = Mockito.mock(Layout.class);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			groupId
		);

		long plid = RandomTestUtil.randomLong();

		Mockito.when(
			layout.getPlid()
		).thenReturn(
			plid
		);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_layout", layout);

		Mockito.when(
			_group.getDisplayURL(_themeDisplay, false)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		try (MockedStatic<LayoutLocalServiceUtil>
				layoutLocalServiceUtilMockedStatic = Mockito.mockStatic(
					LayoutLocalServiceUtil.class)) {

			layoutLocalServiceUtilMockedStatic.when(
				() -> LayoutLocalServiceUtil.fetchFirstLayout(
					groupId, false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
					false)
			).thenReturn(
				layout
			);

			Assert.assertEquals("home", _getSelectedPortletId());
		}
	}

	@Test
	public void testIsPageTreeVisibleIgnoresTheStateOfAnotherSite() {
		ReflectionTestUtil.setFieldValue(
			_themeDisplay, "_scopeGroupId", RandomTestUtil.randomLong());

		try (MockedStatic<SessionClicks> sessionClicksMockedStatic =
				Mockito.mockStatic(SessionClicks.class)) {

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					Mockito.any(HttpServletRequest.class), Mockito.anyString(),
					Mockito.eq(StringPool.BLANK))
			).thenReturn(
				String.valueOf(RandomTestUtil.randomLong())
			);

			Assert.assertFalse(
				ReflectionTestUtil.invoke(
					_productMenuSidebarDisplayContext, "_isPageTreeVisible",
					new Class<?>[0]));

			sessionClicksMockedStatic.verify(
				() -> SessionClicks.put(
					Mockito.any(HttpServletRequest.class), Mockito.anyString(),
					Mockito.anyString()),
				Mockito.never());
		}
	}

	@Test
	public void testIsPageTreeVisibleKeepsTheStateOnThePagesAdministration() {
		_themeDisplay.setPpid(LayoutAdminPortletKeys.GROUP_PAGES);

		try (MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class);
			MockedStatic<SessionClicks> sessionClicksMockedStatic =
				Mockito.mockStatic(SessionClicks.class)) {

			portalUtilMockedStatic.when(
				() -> PortalUtil.getOriginalServletRequest(
					Mockito.any(HttpServletRequest.class))
			).thenReturn(
				Mockito.mock(HttpServletRequest.class)
			);

			Assert.assertFalse(
				ReflectionTestUtil.invoke(
					_productMenuSidebarDisplayContext, "_isPageTreeVisible",
					new Class<?>[0]));

			sessionClicksMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testIsVisibleReadsTheProductMenuState() {
		try (MockedStatic<SessionClicks> sessionClicksMockedStatic =
				Mockito.mockStatic(SessionClicks.class)) {

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					_httpServletRequest, _SESSION_KEY_VISIBLE, "open")
			).thenReturn(
				"open"
			);

			Assert.assertTrue(_productMenuSidebarDisplayContext.isVisible());

			sessionClicksMockedStatic.when(
				() -> SessionClicks.get(
					_httpServletRequest, _SESSION_KEY_VISIBLE, "open")
			).thenReturn(
				"closed"
			);

			Assert.assertFalse(_productMenuSidebarDisplayContext.isVisible());
		}
	}

	private PanelApp _createPanelApp(String portletId) throws Exception {
		PanelApp panelApp = Mockito.mock(PanelApp.class);

		Mockito.when(
			panelApp.getLabel(Mockito.nullable(Locale.class))
		).thenReturn(
			portletId
		);

		Mockito.when(
			panelApp.getPortletId()
		).thenReturn(
			portletId
		);

		Mockito.when(
			panelApp.getPortletURL(Mockito.nullable(HttpServletRequest.class))
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);

		return panelApp;
	}

	private PanelCategory _createPanelCategory(String key) {
		PanelCategory panelCategory = Mockito.mock(PanelCategory.class);

		Mockito.when(
			panelCategory.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			panelCategory.getLabel(Mockito.nullable(Locale.class))
		).thenReturn(
			key
		);

		return panelCategory;
	}

	private List<String> _getExpandedKeys() {
		return ReflectionTestUtil.invoke(
			_productMenuSidebarDisplayContext, "_getExpandedKeys",
			new Class<?>[0]);
	}

	private String _getImageURL() {
		return ReflectionTestUtil.invoke(
			_productMenuSidebarDisplayContext, "_getImageURL", new Class<?>[0]);
	}

	private String _getLabel() {
		return ReflectionTestUtil.invoke(
			_productMenuSidebarDisplayContext, "_getLabel", new Class<?>[0]);
	}

	private List<Map<String, Object>> _getPropsItems() {
		return ReflectionTestUtil.invoke(
			_productMenuSidebarDisplayContext, "_getPropsItems",
			new Class<?>[0]);
	}

	private String _getSelectedPortletId() {
		return ReflectionTestUtil.invoke(
			_productMenuSidebarDisplayContext, "_getSelectedPortletId",
			new Class<?>[0]);
	}

	private void _setUpLanguage(Language language) {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);
	}

	private void _setUpPersonalSite(long userId) {
		Mockito.when(
			_group.getClassPK()
		).thenReturn(
			userId
		);

		Mockito.when(
			_group.isUser()
		).thenReturn(
			true
		);
	}

	private void _setUpRootChildPanelCategories(
		PanelCategory... panelCategories) {

		Mockito.when(
			_panelCategoryHelper.getChildPanelCategories(
				PanelCategoryKeys.SITE_ADMINISTRATION, _themeDisplay)
		).thenReturn(
			List.of(panelCategories)
		);
	}

	private static final String _SESSION_KEY_EXPANDED_KEYS =
		"com.liferay.product.navigation.product.menu.web_sidebarExpandedKeys";

	private static final String _SESSION_KEY_VISIBLE =
		"com.liferay.product.navigation.product.menu.web_productMenuState";

	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final PanelAppRegistry _panelAppRegistry = Mockito.mock(
		PanelAppRegistry.class);
	private final PanelCategoryHelper _panelCategoryHelper = Mockito.mock(
		PanelCategoryHelper.class);
	private ProductMenuSidebarDisplayContext _productMenuSidebarDisplayContext;
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}