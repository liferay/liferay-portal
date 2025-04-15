/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.display.page.internal.layout.type.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class DisplayPageLayoutTypeControllerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_guestUser = _userLocalService.getGuestUser(_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testDisplayPageTypeControllerGetFriendlyURL() throws Exception {
		_assertDisplayPageTypeControllerGetFriendlyURL(StringPool.BLANK);
	}

	@Test
	public void testDisplayPageTypeControllerGetFriendlyURLWithFriendlyURLSeparator()
		throws Exception {

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_ASSET_DISPLAY);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "/w/basic-web-content/-/document_library/");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				StringPool.BLANK, mockHttpServletRequest,
				TestPropsValues.getUser()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Assert.assertEquals(
			"/w/basic-web-content",
			layoutTypeController.getFriendlyURL(
				mockHttpServletRequest,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid())));
	}

	@Test
	public void testDisplayPageTypeControllerGetFriendlyURLWithLocale()
		throws Exception {

		_assertDisplayPageTypeControllerGetFriendlyURL(
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testDisplayPageTypeControllerGetFriendlyURLWithXSS()
		throws Exception {

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_ASSET_DISPLAY);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "x</script><svg/onload=alert(origin)>");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				StringPool.BLANK, mockHttpServletRequest,
				TestPropsValues.getUser()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Assert.assertEquals(
			"x&lt;/script&gt;&lt;svg/onload=alert(origin)&gt;",
			layoutTypeController.getFriendlyURL(
				mockHttpServletRequest,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid())));
	}

	@Test
	public void testDisplayPageTypeControllerWithInfoItem() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		_setUpInfoItem(true);

		_addFragmentEntryLink(draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		Assert.assertTrue(layout.isPublished());

		_assertIncludeLayoutContent(false, layout.getPlid(), _guestUser);
	}

	@Test
	@TestInfo("LPS-136421")
	public void testDisplayPageTypeControllerWithInfoItemWithoutGuestPermissionsWithPromptDisabled()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		_setUpInfoItem(false);

		_addFragmentEntryLink(draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		Assert.assertTrue(layout.isPublished());

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _guestUser.getUserId());

			serviceContext.setRequest(
				_getMockHttpServletRequest(layout, _guestUser));

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_assertIncludeLayoutContent(true, layout.getPlid(), _guestUser);
		}
		finally {
			ServiceContextThreadLocal.pushServiceContext(_serviceContext);
		}
	}

	@Test
	@TestInfo("LPS-136421")
	public void testDisplayPageTypeControllerWithInfoItemWithoutGuestPermissionsWithPromptEnabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"promptEnabled", true
					).build())) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
					null, _group.getGroupId(), 0, null,
					_portal.getClassNameId(AssetCategory.class.getName()), 0,
					RandomTestUtil.randomString(), 0,
					WorkflowConstants.STATUS_DRAFT, _serviceContext);

			Layout layout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			Layout draftLayout = layout.fetchDraftLayout();

			Assert.assertNotNull(draftLayout);

			_setUpInfoItem(false);

			_addFragmentEntryLink(draftLayout);

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			Assert.assertTrue(layout.isPublished());

			LayoutTypeController layoutTypeController =
				LayoutTypeControllerTracker.getLayoutTypeController(
					LayoutConstants.TYPE_ASSET_DISPLAY);

			try {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _guestUser.getUserId());

				MockHttpServletRequest mockHttpServletRequest =
					_getMockHttpServletRequest(layout, _guestUser);

				serviceContext.setRequest(mockHttpServletRequest);

				ServiceContextThreadLocal.pushServiceContext(serviceContext);

				MockHttpServletResponse mockHttpServletResponse =
					new MockHttpServletResponse();

				layoutTypeController.includeLayoutContent(
					mockHttpServletRequest, mockHttpServletResponse, layout);

				Assert.assertEquals(
					HttpServletResponse.SC_FOUND,
					mockHttpServletResponse.getStatus());

				String redirectURL = mockHttpServletResponse.getRedirectedUrl();

				Assert.assertTrue(redirectURL.contains("redirect"));
			}
			finally {
				ServiceContextThreadLocal.pushServiceContext(_serviceContext);
			}
		}
	}

	@Test
	public void testDisplayPageTypeControllerWithLockedLayout()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setStatus(WorkflowConstants.STATUS_DRAFT);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		_lockLayout(draftLayout, TestPropsValues.getUser());

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_ASSET_DISPLAY);

		HttpServletRequest httpServletRequest = _getHttpServletRequest(
			Constants.EDIT, UserTestUtil.addGroupAdminUser(_group));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		layoutTypeController.includeLayoutContent(
			httpServletRequest, mockHttpServletResponse, draftLayout);

		Assert.assertEquals(
			_layoutLockManager.getLockedLayoutURL(httpServletRequest),
			mockHttpServletResponse.getRedirectedUrl());
	}

	@Test
	public void testDisplayPageTypeControllerWithoutContextInfoItem()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		Assert.assertTrue(layout.isPublished());

		_assertIncludeLayoutContent(
			false, draftLayout.getPlid(), TestPropsValues.getUser());
		_assertIncludeLayoutContent(
			false, layout.getPlid(), TestPropsValues.getUser());
		_assertIncludeLayoutContent(true, draftLayout.getPlid(), _guestUser);
		_assertIncludeLayoutContent(true, layout.getPlid(), _guestUser);
	}

	private void _addFragmentEntryLink(Layout layout) throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, _serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK,
				"<h1 data-lfr-editable-id=\"element-text\" " +
					"data-lfr-editable-type=\"text\">Heading Example</h1>",
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put("mappedField", "AssetCategory_name"))
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), null, 0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private void _assertDisplayPageTypeControllerGetFriendlyURL(
			String languageId)
		throws Exception {

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_ASSET_DISPLAY);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(
			languageId, mockHttpServletRequest, TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL,
			themeDisplay.getPathMain() +
				"/portal/comment/discussion/get_comments");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Assert.assertNull(
			layoutTypeController.getFriendlyURL(
				mockHttpServletRequest, layout));
		Assert.assertNull(
			layoutTypeController.getFriendlyURL(
				mockHttpServletRequest, layout.fetchDraftLayout()));
	}

	private void _assertIncludeLayoutContent(
			boolean noSuchLayoutExceptionExpected, long plid, User user)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_ASSET_DISPLAY);

		try {
			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(layout, user);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId());

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			layoutTypeController.includeLayoutContent(
				mockHttpServletRequest, mockHttpServletResponse, layout);

			Assert.assertFalse(noSuchLayoutExceptionExpected);

			Assert.assertEquals(
				HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			Assert.assertTrue(noSuchLayoutExceptionExpected);
		}
		finally {
			ServiceContextThreadLocal.pushServiceContext(_serviceContext);
		}
	}

	private HttpServletRequest _getHttpServletRequest(
			String layoutMode, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://www.liferay.com");

		UserTestUtil.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(StringPool.BLANK, mockHttpServletRequest, user));

		if (Validator.isNotNull(layoutMode)) {
			mockHttpServletRequest.setParameter("p_l_mode", layoutMode);
		}

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Layout layout, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, _assetCategory);
		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM_DETAILS, _infoItemDetails);
		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, _portal.getLayoutActualURL(layout));
		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		if (_assetCategory != null) {
			mockHttpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_group.getGroupId(),
					String.valueOf(_assetCategory.getCategoryId())));
		}

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(_portal.getSiteDefaultLocale(_group));

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setPermissionChecker(
			_permissionCheckerFactory.create(user));
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setPortalDomain(_company.getVirtualHostname());
		themeDisplay.setPortalURL(_company.getPortalURL(_group.getGroupId()));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerPort(8080);
		themeDisplay.setSignedIn(!user.isGuestUser());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setURLCurrent("redirect");
		themeDisplay.setURLSignIn(
			"/c/portal/layout?p_l_id=" + layout.getPlid());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			String i18nPath, HttpServletRequest mockHttpServletRequest,
			User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		themeDisplay.setLayout(LayoutTestUtil.addTypePortletLayout(_group));
		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		themeDisplay.setPathMain(i18nPath.concat(_portal.getPathMain()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPortalDomain(company.getVirtualHostname());
		themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerPort(8080);
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private void _lockLayout(Layout layout, User user) throws Exception {
		MockActionRequest mockActionRequest = new MockActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(layout);
		themeDisplay.setUser(user);

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_layoutLockManager.getLock(mockActionRequest);
	}

	private void _setUpInfoItem(boolean addGuestPermissions) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(addGuestPermissions);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
				StringPool.BLANK, serviceContext);

		_assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		InfoItemDetailsProvider infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, AssetCategory.class.getName());

		_infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			_assetCategory);

		Assert.assertEquals(
			addGuestPermissions,
			_infoItemPermissionProvider.hasPermission(
				_permissionCheckerFactory.create(_guestUser), _assetCategory,
				ActionKeys.VIEW));
	}

	private static final String _PID =
		"com.liferay.login.web.internal.configuration.AuthLoginConfiguration";

	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private User _guestUser;
	private InfoItemDetails _infoItemDetails;

	@Inject(
		filter = "component.name=com.liferay.asset.categories.admin.web.internal.info.item.provider.AssetCategoryInfoItemPermissionProvider"
	)
	private InfoItemPermissionProvider<AssetCategory>
		_infoItemPermissionProvider;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject(
		filter = "component.name=com.liferay.asset.categories.internal.layout.display.page.AssetCategoryLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<AssetCategory> _layoutDisplayPageProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

}