/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class MasterLayoutUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Group depotGroup = Mockito.mock(Group.class);

		Group designLibraryGroup = Mockito.mock(Group.class);

		Mockito.when(
			designLibraryGroup.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Library"
		);

		Mockito.when(
			designLibraryGroup.getExternalReferenceCode()
		).thenReturn(
			"LIBRARY"
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(_DEPOT_GROUP_ID)
		).thenReturn(
			depotGroup
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(_DESIGN_LIBRARY_GROUP_ID)
		).thenReturn(
			designLibraryGroup
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(designLibraryGroup)
		).thenReturn(
			true
		);

		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.format(
				LocaleUtil.US, "x-from-x-design-library",
				new String[] {"Master", "Library"}, false)
		).thenReturn(
			"Master from Library"
		);

		languageUtil.setLanguage(_language);
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
		_layoutPageTemplateEntryLocalServiceUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-108827")
	public void testGetMasterLayoutName() {
		Assert.assertEquals(
			"Master from Library",
			MasterLayoutUtil.getMasterLayoutName(
				_GROUP_ID,
				_mockLayoutPageTemplateEntry(_DESIGN_LIBRARY_GROUP_ID),
				LocaleUtil.US));
		Assert.assertEquals(
			"Master",
			MasterLayoutUtil.getMasterLayoutName(
				_GROUP_ID, _mockLayoutPageTemplateEntry(_GROUP_ID),
				LocaleUtil.US));
		Assert.assertEquals(
			"Master",
			MasterLayoutUtil.getMasterLayoutName(
				_GROUP_ID, _mockLayoutPageTemplateEntry(0), LocaleUtil.US));
		Assert.assertEquals(
			"Master",
			MasterLayoutUtil.getMasterLayoutName(
				_GROUP_ID, _mockLayoutPageTemplateEntry(_DEPOT_GROUP_ID),
				LocaleUtil.US));
	}

	@Test
	@TestInfo("LPD-108827")
	public void testGetMasterLayoutPageTemplateEntryScopeERC() {
		Assert.assertEquals(
			"LIBRARY",
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeERC(
				_GROUP_ID,
				_mockLayout(
					1,
					_mockLayoutPageTemplateEntry(_DESIGN_LIBRARY_GROUP_ID))));
		Assert.assertEquals(
			StringPool.BLANK,
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeERC(
				_GROUP_ID,
				_mockLayout(2, _mockLayoutPageTemplateEntry(_GROUP_ID))));
		Assert.assertEquals(
			StringPool.BLANK,
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeERC(
				_GROUP_ID, _mockLayout(3, null)));
		Assert.assertEquals(
			StringPool.BLANK,
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeERC(
				_GROUP_ID, _mockLayout(0, null)));
	}

	private Layout _mockLayout(
		long masterLayoutPlid,
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry) {

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getMasterLayoutPlid()
		).thenReturn(
			masterLayoutPlid
		);

		_layoutPageTemplateEntryLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(masterLayoutPlid)
		).thenReturn(
			masterLayoutPageTemplateEntry
		);

		return layout;
	}

	private LayoutPageTemplateEntry _mockLayoutPageTemplateEntry(long groupId) {
		LayoutPageTemplateEntry layoutPageTemplateEntry = Mockito.mock(
			LayoutPageTemplateEntry.class);

		Mockito.when(
			layoutPageTemplateEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			layoutPageTemplateEntry.getName()
		).thenReturn(
			"Master"
		);

		return layoutPageTemplateEntry;
	}

	private static final long _DEPOT_GROUP_ID = 3;

	private static final long _DESIGN_LIBRARY_GROUP_ID = 2;

	private static final long _GROUP_ID = 1;

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final Language _language = Mockito.mock(Language.class);
	private final MockedStatic<LayoutPageTemplateEntryLocalServiceUtil>
		_layoutPageTemplateEntryLocalServiceUtilMockedStatic =
			Mockito.mockStatic(LayoutPageTemplateEntryLocalServiceUtil.class);

}