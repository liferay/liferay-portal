/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib.util;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class BasicFragmentEntryActionDropdownItemsProviderTest
	extends BaseActionDropdownItemsProviderTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetActionDropdownItemsForDraftFragmentEntry()
		throws Exception {

		_setUpFragmentEntry(true, false, false);
		setUpFragmentPermission(true);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		assertDropdownItemsInCorrectOrder(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems(),
			"edit", "change-thumbnail", "discard-draft", "rename",
			"mark-as-cacheable", "view-site-usages", "export", "make-a-copy",
			"move", "delete");
	}

	@Test
	@TestInfo("LPD-63087")
	public void testGetActionDropdownItemsForMarketplaceFragmentEntry()
		throws Exception {

		setUpFragmentPermission(true);

		Mockito.when(
			_fragmentEntry.isMarketplace()
		).thenReturn(
			true
		);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		assertDropdownItemsInCorrectOrder(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems(),
			"rename", "view-site-usages", "move", "delete");
	}

	@Test
	@TestInfo({"LPS-122082", "LPS-122641"})
	public void testGetActionDropdownItemsForReactFragmentEntry()
		throws Exception {

		_setUpFragmentEntry(false, false, true);
		setUpFragmentPermission(true);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		assertDropdownItemsInCorrectOrder(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems(),
			"change-thumbnail", "rename", "view-site-usages", "make-a-copy",
			"move", "delete");
	}

	@Test
	public void testGetActionDropdownItemsForReadonlyFragmentEntry()
		throws Exception {

		_setUpFragmentEntry(false, true, false);
		setUpFragmentPermission(true);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		assertDropdownItemsInCorrectOrder(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems(),
			"edit", "make-a-copy");
	}

	@Test
	@TestInfo({"LPD-98538", "LPD-98882"})
	public void testGetActionDropdownItemsForSiteScopedFragmentEntry()
		throws Exception {

		_setUpFragmentEntry(false, false, false);
		setUpFragmentPermission(true);

		Mockito.when(
			_fragmentEntry.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		try (MockedStatic<DesignLibraryUtil> designLibraryUtilMockedStatic =
				Mockito.mockStatic(DesignLibraryUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			designLibraryUtilMockedStatic.when(
				() -> DesignLibraryUtil.isDesignLibraryScope(
					Mockito.nullable(Group.class))
			).thenReturn(
				false
			);

			assertDropdownItemsInCorrectOrder(
				basicFragmentEntryActionDropdownItemsProvider.
					getActionDropdownItems(),
				"edit", "change-thumbnail", "rename", "mark-as-cacheable",
				"view-usages", "export", "make-a-copy", "move", "delete");

			designLibraryUtilMockedStatic.when(
				() -> DesignLibraryUtil.isDesignLibraryScope(
					Mockito.nullable(Group.class))
			).thenReturn(
				true
			);

			assertDropdownItemsInCorrectOrder(
				basicFragmentEntryActionDropdownItemsProvider.
					getActionDropdownItems(),
				"edit", "change-thumbnail", "rename", "mark-as-cacheable",
				"export", "make-a-copy", "move", "delete");

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-57283"))
			).thenReturn(
				true
			);

			assertDropdownItemsInCorrectOrder(
				basicFragmentEntryActionDropdownItemsProvider.
					getActionDropdownItems(),
				"edit", "change-thumbnail", "rename", "mark-as-cacheable",
				"view-site-usages", "export", "make-a-copy", "move", "delete");
		}
	}

	@Test
	public void testGetActionDropdownItemsWithManageFragmentEntries()
		throws Exception {

		_setUpFragmentEntry(false, false, false);
		setUpFragmentPermission(true);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		assertDropdownItemsInCorrectOrder(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems(),
			"edit", "change-thumbnail", "rename", "mark-as-cacheable",
			"view-site-usages", "export", "make-a-copy", "move", "delete");
	}

	@Test
	@TestInfo("LPD-101584")
	public void testGetActionDropdownItemsWithUsageCount() throws Exception {
		_assertViewSiteUsagesDropdownItemDisabled(true, 0);
		_assertViewSiteUsagesDropdownItemDisabled(
			false, RandomTestUtil.randomInt());
	}

	@Test
	public void testGetActionDropdownItemsWithoutManageFragmentEntries()
		throws Exception {

		_setUpFragmentEntry(false, false, false);
		setUpFragmentPermission(false);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse,
					RandomTestUtil.randomInt());

		assertDropdownItemsInCorrectOrder(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems());
	}

	private void _assertViewSiteUsagesDropdownItemDisabled(
			boolean disabled, int usageCount)
		throws Exception {

		_setUpFragmentEntry(false, false, false);
		setUpFragmentPermission(true);

		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					_fragmentEntry, renderRequest, renderResponse, usageCount);

		DropdownItem dropdownItem = _getViewSiteUsagesDropdownItem(
			basicFragmentEntryActionDropdownItemsProvider.
				getActionDropdownItems());

		Assert.assertEquals("view-site-usages", dropdownItem.get("label"));
		Assert.assertEquals(disabled, dropdownItem.get("disabled"));
	}

	private DropdownItem _getViewSiteUsagesDropdownItem(
		List<DropdownItem> dropdownItems) {

		for (DropdownItem dropdownItem :
				getActionDropdownItems(dropdownItems)) {

			if (StringUtil.equals(
					(String)dropdownItem.get("label"), "view-site-usages")) {

				return dropdownItem;
			}
		}

		throw new AssertionError(
			"Unable to find the \"view-site-usages\" dropdown item in " +
				dropdownItems);
	}

	private void _setUpFragmentEntry(
		boolean draft, boolean readOnly, boolean typeReact) {

		Mockito.when(
			_fragmentEntry.isDraft()
		).thenReturn(
			draft
		);

		Mockito.when(
			_fragmentEntry.isReadOnly()
		).thenReturn(
			readOnly
		);

		Mockito.when(
			_fragmentEntry.isTypeReact()
		).thenReturn(
			typeReact
		);
	}

	private final FragmentEntry _fragmentEntry = Mockito.mock(
		FragmentEntry.class);

}