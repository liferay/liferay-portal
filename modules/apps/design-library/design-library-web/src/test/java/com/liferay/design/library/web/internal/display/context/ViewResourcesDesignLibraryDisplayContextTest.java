/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributorRegistry;
import com.liferay.design.library.web.internal.constants.DesignLibraryWebKeys;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
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
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * @author Thiago Buarque
 */
public class ViewResourcesDesignLibraryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDepotEntry();
		_setUpJSONFactoryUtil();
		_setUpViewResourcesDesignLibraryDisplayContext();
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testGetAPIURL() throws Exception {
		_setUpClassNameIds();

		_testGetAPIURL();
		_testGetAPIURLNarrowsByClassNameId();
		_testGetAPIURLNarrowsByType();
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
	public void testGetBulkActionDropdownItems() {
		_setUpLanguageUtil();

		List<DropdownItem> bulkActionDropdownItems =
			_viewResourcesDesignLibraryDisplayContext.
				getBulkActionDropdownItems();

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 1,
			bulkActionDropdownItems.size());

		DropdownItem dropdownItem = bulkActionDropdownItems.get(0);

		Assert.assertEquals("trash", dropdownItem.get("icon"));
		Assert.assertEquals("delete", dropdownItem.get("label"));

		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		Assert.assertEquals("delete", data.get("id"));
		Assert.assertNull(data.get("method"));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		_setUpPortletURLMocks();

		_setUpRegistry(
			_mockContributor(false, _FRAGMENT_CLASS_NAME, "fragment", null));

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_viewResourcesDesignLibraryDisplayContext.
				getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 1,
			fdsActionDropdownItems.size());

		Map<String, Object> visibilityFilters = _getVisibilityFilters(
			fdsActionDropdownItems.get(0));

		Assert.assertEquals(
			_FRAGMENT_CLASS_NAME, visibilityFilters.get("entryClassName"));
		Assert.assertFalse(
			visibilityFilters.toString(),
			visibilityFilters.containsKey("type"));
	}

	@Test
	public void testGetFDSActionDropdownItemsStampsTheTypeDiscriminator()
		throws Exception {

		_setUpPortletURLMocks();

		_setUpRegistry(
			_mockContributor(false, _LAYOUT_CLASS_NAME, "master", "3"));

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_viewResourcesDesignLibraryDisplayContext.
				getFDSActionDropdownItems();

		Map<String, Object> visibilityFilters = _getVisibilityFilters(
			fdsActionDropdownItems.get(0));

		Assert.assertEquals(
			_LAYOUT_CLASS_NAME, visibilityFilters.get("entryClassName"));
		Assert.assertEquals("3", visibilityFilters.get("type"));
	}

	@Test
	public void testGetFDSAdditionalProps() throws Exception {
		_setUpPortletURLMocks();

		_setUpRegistry(
			_mockContributor(
				false, _STYLE_BOOK_CLASS_NAME, "style-book", null));

		List<Map<String, Object>> resourceTypes = _getResourceTypes();

		Assert.assertEquals(resourceTypes.toString(), 1, resourceTypes.size());

		Map<String, Object> resourceType = resourceTypes.get(0);

		Assert.assertEquals("style-book-color", resourceType.get("color"));
		Assert.assertEquals("edit", resourceType.get("defaultActionId"));
		Assert.assertEquals(
			_STYLE_BOOK_CLASS_NAME, resourceType.get("entryClassName"));
		Assert.assertEquals("style-book", resourceType.get("key"));
		Assert.assertEquals("style-book-label", resourceType.get("label"));
		Assert.assertEquals("style-book-icon", resourceType.get("symbol"));
		Assert.assertNull(resourceType.get("creationItems"));
		Assert.assertNull(resourceType.get("type"));
	}

	@Test
	public void testGetFDSAdditionalPropsAddsCreationItemsWithAddPermission()
		throws Exception {

		_setUpPortletURLMocks();

		_setUpRegistry(
			_mockContributor(true, _STYLE_BOOK_CLASS_NAME, "style-book", null));

		List<Map<String, Object>> resourceTypes = _getResourceTypes();

		List<Map<String, Object>> creationItems =
			(List<Map<String, Object>>)resourceTypes.get(
				0
			).get(
				"creationItems"
			);

		Assert.assertEquals(creationItems.toString(), 1, creationItems.size());

		Map<String, Object> creationItem = creationItems.get(0);

		Assert.assertEquals("style-book-add", creationItem.get("id"));
		Assert.assertEquals("style-book-add-label", creationItem.get("label"));
	}

	@Test
	@TestInfo("LPD-96528")
	public void testHasContentAccess() throws Exception {
		_setUpRegistry();

		Assert.assertFalse(
			_viewResourcesDesignLibraryDisplayContext.hasContentAccess());

		_setUpRegistry(
			_mockContributor(
				false, _STYLE_BOOK_CLASS_NAME, "style-book", null));

		Assert.assertTrue(
			_viewResourcesDesignLibraryDisplayContext.hasContentAccess());
	}

	private FDSActionDropdownItem _createFDSActionDropdownItem(String key) {
		return new FDSActionDropdownItem(
			"/" + key, null, "edit", key + "-edit", "get", null, "link");
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
				depotEntry.getDepotEntryId()
			).thenReturn(
				_DEPOT_ENTRY_ID
			);

			Mockito.when(
				depotEntry.getGroup()
			).thenReturn(
				group
			);

			_setUpLanguageUtil();

			HttpServletRequest httpServletRequest = Mockito.mock(
				HttpServletRequest.class);

			Mockito.when(
				httpServletRequest.getAttribute(
					DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY)
			).thenReturn(
				depotEntry
			);

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

			ViewResourcesDesignLibraryDisplayContext
				viewResourcesDesignLibraryDisplayContext =
					new ViewResourcesDesignLibraryDisplayContext(
						httpServletRequest,
						Mockito.mock(LiferayPortletResponse.class));

			List<String> labels = new ArrayList<>();

			Map<String, Object> breadcrumbProps =
				viewResourcesDesignLibraryDisplayContext.getBreadcrumbProps();

			JSONArray jsonArray = (JSONArray)breadcrumbProps.get("actionItems");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				labels.add(jsonObject.getString("label"));
			}

			return labels;
		}
	}

	private List<Map<String, Object>> _getResourceTypes() throws Exception {
		Map<String, Object> fdsAdditionalProps =
			_viewResourcesDesignLibraryDisplayContext.getFDSAdditionalProps();

		return (List<Map<String, Object>>)fdsAdditionalProps.get(
			"resourceTypes");
	}

	private Map<String, Object> _getVisibilityFilters(
		FDSActionDropdownItem fdsActionDropdownItem) {

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		return (Map<String, Object>)data.get("visibilityFilters");
	}

	private DesignLibraryResourceTypeContributor _mockContributor(
			boolean addPermission, String entryClassName, String key,
			String type)
		throws Exception {

		DesignLibraryResourceTypeContributor
			designLibraryResourceTypeContributor = Mockito.mock(
				DesignLibraryResourceTypeContributor.class);

		Mockito.when(
			designLibraryResourceTypeContributor.getColor()
		).thenReturn(
			key + "-color"
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getCreationItems(
				Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			Collections.singletonList(
				new DesignLibraryResourceCreationItem(
					key + "-add", key + "-add-label",
					"{Modal} from " + key + "-web",
					Collections.singletonMap("backURL", "backURL")))
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getDefaultActionId()
		).thenReturn(
			"edit"
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getEntryClassName()
		).thenReturn(
			entryClassName
		);

		FDSActionDropdownItem fdsActionDropdownItem =
			_createFDSActionDropdownItem(key);

		Mockito.when(
			designLibraryResourceTypeContributor.getFDSActionDropdownItems(
				Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			Collections.singletonList(fdsActionDropdownItem)
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getIcon()
		).thenReturn(
			key + "-icon"
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getLabel(
				Mockito.any(Locale.class))
		).thenReturn(
			key + "-label"
		);

		Mockito.when(
			designLibraryResourceTypeContributor.getType()
		).thenReturn(
			type
		);

		Mockito.when(
			designLibraryResourceTypeContributor.hasAddPermission(
				Mockito.any(), Mockito.any())
		).thenReturn(
			addPermission
		);

		return designLibraryResourceTypeContributor;
	}

	private void _setUpClassNameIds() {
		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(_FRAGMENT_CLASS_NAME)
		).thenReturn(
			_FRAGMENT_CLASS_NAME_ID
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(_LAYOUT_CLASS_NAME)
		).thenReturn(
			_LAYOUT_CLASS_NAME_ID
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(_STYLE_BOOK_CLASS_NAME)
		).thenReturn(
			_STYLE_BOOK_CLASS_NAME_ID
		);
	}

	private void _setUpDepotEntry() throws Exception {
		Mockito.when(
			_depotEntry.getGroup()
		).thenReturn(
			_group
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_depotEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			groupId
		);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpLanguageUtil() {
		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);
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
	}

	private void _setUpRegistry(
		DesignLibraryResourceTypeContributor...
			designLibraryResourceTypeContributors) {

		List<DesignLibraryResourceTypeContributor> contributors = Arrays.asList(
			designLibraryResourceTypeContributors);

		DesignLibraryResourceTypeContributorRegistry
			designLibraryResourceTypeContributorRegistry = Mockito.mock(
				DesignLibraryResourceTypeContributorRegistry.class);

		Mockito.when(
			designLibraryResourceTypeContributorRegistry.
				getDesignLibraryResourceTypeContributors(
					Mockito.any(PermissionChecker.class),
					Mockito.any(DepotEntry.class))
		).thenReturn(
			contributors
		);

		ReflectionTestUtil.setFieldValue(
			ViewResourcesDesignLibraryDisplayContext.class,
			"_designLibraryResourceTypeContributorRegistrySnapshot",
			new Snapshot<DesignLibraryResourceTypeContributorRegistry>(
				ViewResourcesDesignLibraryDisplayContext.class,
				DesignLibraryResourceTypeContributorRegistry.class) {

				@Override
				public DesignLibraryResourceTypeContributorRegistry get() {
					return designLibraryResourceTypeContributorRegistry;
				}

			});

		_setUpViewResourcesDesignLibraryDisplayContext();
	}

	private void _setUpViewResourcesDesignLibraryDisplayContext() {
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
			DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY, _depotEntry);
		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_viewResourcesDesignLibraryDisplayContext =
			new ViewResourcesDesignLibraryDisplayContext(
				_mockHttpServletRequest, _liferayPortletResponse);
	}

	private void _testGetAPIURL() throws Exception {
		_setUpRegistry(
			_mockContributor(false, _FRAGMENT_CLASS_NAME, "fragment", null),
			_mockContributor(
				false, _STYLE_BOOK_CLASS_NAME, "style-book", null));

		String url = _viewResourcesDesignLibraryDisplayContext.getAPIURL();

		Assert.assertTrue(
			url,
			url.contains(
				StringBundler.concat(
					"entryClassNames=", _FRAGMENT_CLASS_NAME, ",",
					_STYLE_BOOK_CLASS_NAME)));
		Assert.assertTrue(
			url,
			url.contains(
				"filter=groupIds/any(g:g eq " + _depotEntry.getGroupId() +
					")"));
	}

	private void _testGetAPIURLNarrowsByClassNameId() throws Exception {
		_setUpRegistry(
			_mockContributor(false, _FRAGMENT_CLASS_NAME, "fragment", null),
			_mockContributor(
				false, _STYLE_BOOK_CLASS_NAME, "style-book", null));

		String url = _viewResourcesDesignLibraryDisplayContext.getAPIURL();

		Assert.assertTrue(
			url,
			url.contains(
				StringBundler.concat(
					"(classNameId eq ", _FRAGMENT_CLASS_NAME_ID,
					" or classNameId eq ", _STYLE_BOOK_CLASS_NAME_ID, ")")));
	}

	private void _testGetAPIURLNarrowsByType() throws Exception {
		_setUpRegistry(
			_mockContributor(false, _LAYOUT_CLASS_NAME, "master", "3"),
			_mockContributor(false, _LAYOUT_CLASS_NAME, "display-page", "1"));

		String url = _viewResourcesDesignLibraryDisplayContext.getAPIURL();

		Assert.assertTrue(
			url,
			url.contains(
				StringBundler.concat(
					"((classNameId eq ", _LAYOUT_CLASS_NAME_ID,
					" and type eq '3') or (classNameId eq ",
					_LAYOUT_CLASS_NAME_ID, " and type eq '1'))")));
	}

	private static final long _DEPOT_ENTRY_ID = 12345;

	private static final String _FRAGMENT_CLASS_NAME =
		"com.liferay.fragment.model.FragmentCollection";

	private static final long _FRAGMENT_CLASS_NAME_ID = 101;

	private static final String _LAYOUT_CLASS_NAME =
		"com.liferay.layout.page.template.model.LayoutPageTemplateEntry";

	private static final long _LAYOUT_CLASS_NAME_ID = 103;

	private static final String _STYLE_BOOK_CLASS_NAME =
		"com.liferay.style.book.model.StyleBookEntry";

	private static final long _STYLE_BOOK_CLASS_NAME_ID = 102;

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
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
	private ViewResourcesDesignLibraryDisplayContext
		_viewResourcesDesignLibraryDisplayContext;

}