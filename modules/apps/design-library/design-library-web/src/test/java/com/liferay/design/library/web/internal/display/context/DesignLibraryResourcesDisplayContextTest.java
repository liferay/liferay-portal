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
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;

import java.util.List;
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
 * @author Lourdes Fernández Besada
 */
public class DesignLibraryResourcesDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpHttpServletRequest();
		_setUpSnapshots();

		_designLibraryResourcesDisplayContext =
			new DesignLibraryResourcesDisplayContext(
				_mockHttpServletRequest, _liferayPortletResponse);
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
	public void testGetFDSActionDropdownItems() throws Exception {
		long designLibraryEntryId = RandomTestUtil.randomLong();

		_mockDepotEntry(designLibraryEntryId);

		_setUpPortletURLMocks();

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_designLibraryResourcesDisplayContext.getFDSActionDropdownItems(
				designLibraryEntryId);

		_assertFDSActionDropdownItem(
			FragmentCollection.class.getName(), "edit", "link",
			fdsActionDropdownItems.get(0));
		_assertFDSActionDropdownItem(
			StyleBookEntry.class.getName(), "edit", "link",
			fdsActionDropdownItems.get(1));
		_assertFDSActionDropdownItem(
			FragmentCollection.class.getName(), "view", "link",
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

	private void _setUpHttpServletRequest() {
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

	private void _setUpSnapshots() {
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
	}

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