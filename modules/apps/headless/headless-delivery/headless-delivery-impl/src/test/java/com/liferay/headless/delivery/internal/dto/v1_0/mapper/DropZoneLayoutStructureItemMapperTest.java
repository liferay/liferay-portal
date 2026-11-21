/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.mapper;

import com.liferay.headless.delivery.dto.v1_0.Fragment;
import com.liferay.headless.delivery.dto.v1_0.PageDropZoneDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.helper.DropZoneAllowedFragmentEntriesHelper;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class DropZoneLayoutStructureItemMapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_companyIdSafeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(_COMPANY_ID);
		_dropZoneLayoutStructureItemMapper =
			new DropZoneLayoutStructureItemMapper(
				_dropZoneAllowedFragmentEntriesHelper);

		Mockito.reset(_dropZoneAllowedFragmentEntriesHelper);
	}

	@After
	public void tearDown() {
		_companyIdSafeCloseable.close();
	}

	@Test
	@TestInfo("LPD-108786")
	public void testGetPageElement() throws Exception {
		String allowedFragmentEntryKey = RandomTestUtil.randomString();
		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			_getDropZoneLayoutStructureItem(false);

		Mockito.when(
			_dropZoneAllowedFragmentEntriesHelper.getFragmentEntryKeys(
				_COMPANY_ID, dropZoneLayoutStructureItem, _GROUP_ID)
		).thenReturn(
			Arrays.asList(allowedFragmentEntryKey)
		);

		_testGetPageElement(
			dropZoneLayoutStructureItem, Arrays.asList(allowedFragmentEntryKey),
			"allowedFragments");

		String unallowedFragmentEntryKey = RandomTestUtil.randomString();

		dropZoneLayoutStructureItem = _getDropZoneLayoutStructureItem(true);

		Mockito.when(
			_dropZoneAllowedFragmentEntriesHelper.getFragmentEntryKeys(
				_COMPANY_ID, dropZoneLayoutStructureItem, _GROUP_ID)
		).thenReturn(
			Arrays.asList(unallowedFragmentEntryKey)
		);

		_testGetPageElement(
			dropZoneLayoutStructureItem,
			Arrays.asList(unallowedFragmentEntryKey), "unallowedFragments");
	}

	private DropZoneLayoutStructureItem _getDropZoneLayoutStructureItem(
		boolean allowNewFragmentEntries) {

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			new DropZoneLayoutStructureItem(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

		dropZoneLayoutStructureItem.setAllowNewFragmentEntries(
			allowNewFragmentEntries);

		return dropZoneLayoutStructureItem;
	}

	private void _testGetPageElement(
		DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		List<String> expectedFragmentEntryKeys,
		String expectedFragmentSettingsKey) {

		PageElement pageElement =
			_dropZoneLayoutStructureItemMapper.getPageElement(
				_GROUP_ID, dropZoneLayoutStructureItem, false, false);

		PageDropZoneDefinition pageDropZoneDefinition =
			(PageDropZoneDefinition)pageElement.getDefinition();

		Map<?, ?> fragmentSettings =
			(Map<?, ?>)pageDropZoneDefinition.getFragmentSettings();

		Fragment[] fragments = (Fragment[])fragmentSettings.get(
			expectedFragmentSettingsKey);

		Assert.assertEquals(
			Arrays.toString(fragments), expectedFragmentEntryKeys.size(),
			fragments.length);

		for (int i = 0; i < fragments.length; i++) {
			Assert.assertEquals(
				expectedFragmentEntryKeys.get(i), fragments[i].getKey());
		}

		Assert.assertEquals(
			fragmentSettings.toString(), 1, fragmentSettings.size());
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private SafeCloseable _companyIdSafeCloseable;
	private final DropZoneAllowedFragmentEntriesHelper
		_dropZoneAllowedFragmentEntriesHelper = Mockito.mock(
			DropZoneAllowedFragmentEntriesHelper.class);
	private DropZoneLayoutStructureItemMapper
		_dropZoneLayoutStructureItemMapper;

}