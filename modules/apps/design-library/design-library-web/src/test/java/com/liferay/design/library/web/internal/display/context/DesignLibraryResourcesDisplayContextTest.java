/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Prates
 * @author Lourdes Fernández Besada
 */
public class DesignLibraryResourcesDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpDesignLibraryResourcesDisplayContext();
		_setUpJSONFactoryUtil();
	}

	@After
	public void tearDown() {
		_depotEntryLocalServiceUtilMockedStatic.close();
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testGetAPIURL() throws Exception {
		long designLibraryEntryId = RandomTestUtil.randomLong();

		DepotEntry depotEntry = _mockDepotEntry(designLibraryEntryId);

		String url = _designLibraryResourcesDisplayContext.getAPIURL(
			designLibraryEntryId);

		Assert.assertTrue(
			url,
			url.contains(
				StringBundler.concat(
					"entryClassNames=", FragmentCollection.class.getName(), ",",
					StyleBookEntry.class.getName(), "&filter=groupIds/any(g:g ",
					"eq ", depotEntry.getGroupId(), ")")));
	}

	@Test
	public void testGetBreadcrumbPropsActionItemsWithAssignMembersPermission()
		throws Exception {

		List<String> labels = _getBreadcrumbPropsActionItemsLabels(
			true, false, false);

		Assert.assertFalse(labels.toString(), labels.contains("view-members"));
		Assert.assertTrue(labels.toString(), labels.contains("manage-members"));
	}

	@Test
	public void testGetBreadcrumbPropsActionItemsWithDeletePermission()
		throws Exception {

		List<String> labels = _getBreadcrumbPropsActionItemsLabels(
			false, true, false);

		Assert.assertFalse(labels.toString(), labels.contains("export"));
		Assert.assertFalse(labels.toString(), labels.contains("import"));
		Assert.assertFalse(labels.toString(), labels.contains("settings"));
		Assert.assertTrue(labels.toString(), labels.contains("delete"));
	}

	@Test
	public void testGetBreadcrumbPropsActionItemsWithNoPermissions()
		throws Exception {

		List<String> labels = _getBreadcrumbPropsActionItemsLabels(
			false, false, false);

		Assert.assertFalse(labels.toString(), labels.contains("delete"));
		Assert.assertFalse(labels.toString(), labels.contains("export"));
		Assert.assertFalse(labels.toString(), labels.contains("import"));
		Assert.assertFalse(
			labels.toString(), labels.contains("manage-members"));
		Assert.assertFalse(labels.toString(), labels.contains("settings"));
		Assert.assertTrue(
			labels.toString(), labels.contains("connected-sites"));
		Assert.assertTrue(labels.toString(), labels.contains("view-members"));
	}

	@Test
	public void testGetBreadcrumbPropsActionItemsWithUpdateAndDeletePermission()
		throws Exception {

		List<String> labels = _getBreadcrumbPropsActionItemsLabels(
			true, true, true);

		Assert.assertTrue(
			labels.toString(), labels.contains("connected-sites"));
		Assert.assertTrue(labels.toString(), labels.contains("delete"));
		Assert.assertTrue(labels.toString(), labels.contains("export"));
		Assert.assertTrue(labels.toString(), labels.contains("import"));
		Assert.assertTrue(labels.toString(), labels.contains("manage-members"));
		Assert.assertTrue(labels.toString(), labels.contains("settings"));
	}

	@Test
	public void testGetBreadcrumbPropsActionItemsWithUpdatePermission()
		throws Exception {

		List<String> labels = _getBreadcrumbPropsActionItemsLabels(
			false, false, true);

		Assert.assertFalse(labels.toString(), labels.contains("delete"));
		Assert.assertTrue(labels.toString(), labels.contains("export"));
		Assert.assertTrue(labels.toString(), labels.contains("import"));
		Assert.assertTrue(labels.toString(), labels.contains("settings"));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		long designLibraryEntryId = RandomTestUtil.randomLong();

		_mockDepotEntry(designLibraryEntryId);

		_setUpPortletURLMocks();

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_designLibraryResourcesDisplayContext.getFDSActionDropdownItems(
				designLibraryEntryId);

		_assertFDSActionDropdownItem(
			FragmentCollection.class.getName(), "view", "link",
			fdsActionDropdownItems.get(0));
		_assertFDSActionDropdownItem(
			FragmentCollection.class.getName(), "edit", "link",
			fdsActionDropdownItems.get(1));
		_assertFDSActionDropdownItem(
			StyleBookEntry.class.getName(), "edit", "link",
			fdsActionDropdownItems.get(2));
		_assertFDSActionDropdownItem(
			FragmentCollection.class.getName(), "delete", "async",
			fdsActionDropdownItems.get(3));
		_assertFDSActionDropdownItem(
			StyleBookEntry.class.getName(), "delete", "async",
			fdsActionDropdownItems.get(4));

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 5,
			fdsActionDropdownItems.size());
	}

	@Test
	public void testGetFDSAdditionalProps() throws Exception {
		_setUpPortletURLMocks();

		long designLibraryEntryId = RandomTestUtil.randomLong();

		DepotEntry depotEntry = _mockDepotEntry(designLibraryEntryId);

		_assertFDSAdditionalProps(
			designLibraryEntryId, depotEntry.getGroupId(), false);
		_assertFDSAdditionalProps(
			designLibraryEntryId, depotEntry.getGroupId(), true);
	}

	private void _assertFDSActionDropdownItem(
		String expectedEntryClassName, String expectedId, String expectedTarget,
		FDSActionDropdownItem fdsActionDropdownItem) {

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		Map<String, Object> visibilityFilters = (Map<String, Object>)data.get(
			"visibilityFilters");

		Assert.assertEquals(
			expectedEntryClassName, visibilityFilters.get("entryClassName"));

		Assert.assertEquals(expectedId, data.get("id"));
		Assert.assertEquals(
			expectedTarget, fdsActionDropdownItem.get("target"));
	}

	private void _assertFDSAdditionalProps(
			long designLibraryEntryId, long groupId,
			boolean manageFragmentEntriesPermission)
		throws Exception {

		Mockito.when(
			_fragmentPortletResourcePermission.contains(
				_permissionChecker, groupId,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)
		).thenReturn(
			manageFragmentEntriesPermission
		);

		Map<String, Object> fdsAdditionalProps =
			_designLibraryResourcesDisplayContext.getFDSAdditionalProps(
				designLibraryEntryId);

		Assert.assertNull(fdsAdditionalProps.get("addStyleBookEntryURL"));
		Assert.assertFalse((Boolean)fdsAdditionalProps.get("canAddStyleBook"));
		Assert.assertNull(
			fdsAdditionalProps.get("frontendTokenDefinitionProviders"));
		Assert.assertNull(fdsAdditionalProps.get("styleBookNamespace"));

		Assert.assertEquals(
			manageFragmentEntriesPermission,
			fdsAdditionalProps.get("canManageFragments"));

		if (manageFragmentEntriesPermission) {
			Assert.assertNotNull(
				fdsAdditionalProps.get("addFragmentCollectionURL"));
			Assert.assertNotNull(fdsAdditionalProps.get("addFragmentEntryURL"));
			Assert.assertNotNull(fdsAdditionalProps.get("fragmentCollections"));
			Assert.assertNotNull(fdsAdditionalProps.get("fragmentNamespace"));
		}
		else {
			Assert.assertNull(
				fdsAdditionalProps.get("addFragmentCollectionURL"));
			Assert.assertNull(fdsAdditionalProps.get("addFragmentEntryURL"));
			Assert.assertNull(fdsAdditionalProps.get("fragmentCollections"));
			Assert.assertNull(fdsAdditionalProps.get("fragmentNamespace"));
		}
	}

	private List<String> _getBreadcrumbPropsActionItemsLabels(
			boolean hasAssignMembersPermission, boolean hasDeletePermission,
			boolean hasUpdatePermission)
		throws Exception {

		try (MockedStatic<GroupPermissionUtil> groupPermissionUtilMockedStatic =
				Mockito.mockStatic(GroupPermissionUtil.class);
			MockedStatic<PortletURLBuilder> portletURLBuilderMockedStatic =
				Mockito.mockStatic(PortletURLBuilder.class)) {

			groupPermissionUtilMockedStatic.when(
				() -> GroupPermissionUtil.contains(
					Mockito.any(PermissionChecker.class), Mockito.anyLong(),
					Mockito.eq(ActionKeys.ASSIGN_MEMBERS))
			).thenReturn(
				hasAssignMembersPermission
			);

			PortletURLBuilder.PortletURLStep portletURLStep = Mockito.mock(
				PortletURLBuilder.PortletURLStep.class, Mockito.RETURNS_SELF);

			portletURLBuilderMockedStatic.when(
				() -> PortletURLBuilder.createActionURL(
					Mockito.any(LiferayPortletResponse.class))
			).thenReturn(
				portletURLStep
			);

			portletURLBuilderMockedStatic.when(
				() -> PortletURLBuilder.create(Mockito.any())
			).thenReturn(
				portletURLStep
			);

			PermissionChecker permissionChecker = Mockito.mock(
				PermissionChecker.class);

			Mockito.when(
				permissionChecker.hasPermission(
					Mockito.any(Group.class),
					Mockito.eq(DepotEntry.class.getName()), Mockito.anyLong(),
					Mockito.eq(ActionKeys.DELETE))
			).thenReturn(
				hasDeletePermission
			);

			Mockito.when(
				permissionChecker.hasPermission(
					Mockito.any(Group.class),
					Mockito.eq(DepotEntry.class.getName()), Mockito.anyLong(),
					Mockito.eq(ActionKeys.UPDATE))
			).thenReturn(
				hasUpdatePermission
			);

			Group group = Mockito.mock(Group.class);

			Mockito.when(
				group.getClassPK()
			).thenReturn(
				_DEPOT_ENTRY_ID
			);

			Mockito.when(
				group.getName(Mockito.any(Locale.class))
			).thenReturn(
				"design-library-name"
			);

			DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

			Mockito.when(
				depotEntry.getGroup()
			).thenReturn(
				group
			);

			_depotEntryLocalServiceUtilMockedStatic.when(
				() -> DepotEntryLocalServiceUtil.getDepotEntry(
					Mockito.anyLong())
			).thenReturn(
				depotEntry
			);

			_languageUtilMockedStatic.when(
				() -> LanguageUtil.get(
					Mockito.any(HttpServletRequest.class), Mockito.anyString())
			).thenAnswer(
				invocation -> invocation.getArgument(1)
			);

			HttpServletRequest httpServletRequest = Mockito.mock(
				HttpServletRequest.class);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setPermissionChecker(permissionChecker);

			Mockito.when(
				httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
			).thenReturn(
				themeDisplay
			);

			Mockito.when(
				httpServletRequest.getLocale()
			).thenReturn(
				LocaleUtil.US
			);

			DesignLibraryResourcesDisplayContext
				designLibraryResourcesDisplayContext =
					new DesignLibraryResourcesDisplayContext(
						httpServletRequest,
						Mockito.mock(LiferayPortletResponse.class));

			List<String> labels = new ArrayList<>();

			Map<String, Object> breadcrumbProps =
				designLibraryResourcesDisplayContext.getBreadcrumbProps(
					group.getClassPK());

			JSONArray jsonArray = (JSONArray)breadcrumbProps.get("actionItems");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				labels.add(jsonObject.getString("label"));
			}

			return labels;
		}
	}

	private DepotEntry _mockDepotEntry(long designLibraryEntryId)
		throws Exception {

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getGroup()
		).thenReturn(
			_group
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			depotEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			groupId
		);

		_depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.getDepotEntry(designLibraryEntryId)
		).thenReturn(
			depotEntry
		);

		return depotEntry;
	}

	private void _setUpDesignLibraryResourcesDisplayContext() {
		ReflectionTestUtil.setFieldValue(
			DesignLibraryResourcesDisplayContext.class,
			"_fragmentCollectionLocalServiceSnapshot",
			new Snapshot<FragmentCollectionLocalService>(
				DesignLibraryResourcesDisplayContext.class,
				FragmentCollectionLocalService.class) {

				@Override
				public FragmentCollectionLocalService get() {
					return null;
				}

			});
		ReflectionTestUtil.setFieldValue(
			DesignLibraryResourcesDisplayContext.class,
			"_fragmentPortletResourcePermissionSnapshot",
			new Snapshot<PortletResourcePermission>(
				DesignLibraryResourcesDisplayContext.class,
				PortletResourcePermission.class) {

				@Override
				public PortletResourcePermission get() {
					return _fragmentPortletResourcePermission;
				}

			});
		ReflectionTestUtil.setFieldValue(
			DesignLibraryResourcesDisplayContext.class,
			"_styleBookPortletResourcePermissionSnapshot",
			new Snapshot<PortletResourcePermission>(
				DesignLibraryResourcesDisplayContext.class,
				PortletResourcePermission.class) {

				@Override
				public PortletResourcePermission get() {
					return null;
				}

			});

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_designLibraryResourcesDisplayContext =
			new DesignLibraryResourcesDisplayContext(
				_mockHttpServletRequest, _liferayPortletResponse);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpPortletURLMocks() {
		Mockito.when(
			_liferayPortletResponse.createRenderURL()
		).thenReturn(
			_liferayPortletURL
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getControlPanelPortletURL(
				Mockito.eq(_mockHttpServletRequest), Mockito.eq(_group),
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			_liferayPortletURL
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getPortletNamespace(FragmentPortletKeys.FRAGMENT)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getPortletNamespace(
				StyleBookPortletKeys.STYLE_BOOK)
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private static final long _DEPOT_ENTRY_ID = 12345;

	private final MockedStatic<DepotEntryLocalServiceUtil>
		_depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			DepotEntryLocalServiceUtil.class);
	private DesignLibraryResourcesDisplayContext
		_designLibraryResourcesDisplayContext;
	private final PortletResourcePermission _fragmentPortletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final Group _group = Mockito.mock(Group.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}