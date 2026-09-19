/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.web.internal.display.context.SegmentsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class SegmentsEntryActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpLanguageUtil();
	}

	@Test
	public void testGetActionDropdownItemsWithShowActions() throws Exception {
		Mockito.when(
			_segmentsDisplayContext.isShowDeleteAction(_segmentsEntry)
		).thenReturn(
			true
		);

		Mockito.when(
			_segmentsDisplayContext.isShowPermissionAction(_segmentsEntry)
		).thenReturn(
			true
		);

		Mockito.when(
			_segmentsDisplayContext.isShowUpdateAction(_segmentsEntry)
		).thenReturn(
			true
		);

		Mockito.when(
			_segmentsDisplayContext.isShowViewAction(_segmentsEntry)
		).thenReturn(
			true
		);

		SegmentsEntryActionDropdownItemsProvider
			segmentsEntryActionDropdownItemsProvider =
				new SegmentsEntryActionDropdownItemsProvider(
					_httpServletRequest, _segmentsDisplayContext,
					_segmentsEntry);

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			segmentsEntryActionDropdownItemsProvider.getActionDropdownItems());

		Assert.assertEquals(dropdownItems.toString(), 4, dropdownItems.size());

		_assertDropdownItems(
			dropdownItems,
			new String[] {"edit", "view-members", "permissions", "delete"},
			new String[] {"pencil", "users", "password-policies", "trash"});
	}

	@Test
	public void testGetActionDropdownItemsWithoutShowActions()
		throws Exception {

		SegmentsEntryActionDropdownItemsProvider
			segmentsEntryActionDropdownItemsProvider =
				new SegmentsEntryActionDropdownItemsProvider(
					_httpServletRequest, _segmentsDisplayContext,
					_segmentsEntry);

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			segmentsEntryActionDropdownItemsProvider.getActionDropdownItems());

		Assert.assertEquals(dropdownItems.toString(), 0, dropdownItems.size());
	}

	private void _assertDropdownItems(
		List<DropdownItem> dropdownItems, String[] expectedLabels,
		String[] expectedIcons) {

		for (int i = 0; i < dropdownItems.size(); i++) {
			DropdownItem dropdownItem = dropdownItems.get(i);

			Assert.assertEquals(expectedLabels[i], dropdownItem.get("label"));
			Assert.assertEquals(expectedIcons[i], dropdownItem.get("icon"));
		}
	}

	private List<DropdownItem> _getActionDropdownItems(
		List<DropdownItem> dropdownItems) {

		List<DropdownItem> allDropdownItems = new ArrayList<>();

		for (DropdownItem dropdownItem : dropdownItems) {
			if (!StringUtil.equals((String)dropdownItem.get("type"), "group")) {
				allDropdownItems.add(dropdownItem);

				continue;
			}

			allDropdownItems.addAll(
				(List<DropdownItem>)dropdownItem.get("items"));
		}

		return allDropdownItems;
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));

		Mockito.when(
			languageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArguments()[1]
		);
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final SegmentsDisplayContext _segmentsDisplayContext = Mockito.mock(
		SegmentsDisplayContext.class);
	private final SegmentsEntry _segmentsEntry = Mockito.mock(
		SegmentsEntry.class);

}