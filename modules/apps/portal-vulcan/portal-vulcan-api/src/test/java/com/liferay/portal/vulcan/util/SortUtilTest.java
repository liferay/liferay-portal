/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class SortUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_acceptLanguage = Mockito.mock(AcceptLanguage.class);

		Mockito.when(
			_acceptLanguage.getPreferredLocale()
		).thenReturn(
			LocaleUtil.US
		);
	}

	@Test
	public void testGetSorts() {
		_testGetSortsWithDateTimeField();
		_testGetSortsWithDoubleField();
		_testGetSortsWithIdField();
		_testGetSortsWithIdFieldDescending();
		_testGetSortsWithIntegerField();
		_testGetSortsWithStringField();
	}

	private Sort[] _getSorts(EntityField entityField, boolean ascending) {
		SortParser sortParser = Mockito.mock(SortParser.class);

		Mockito.when(
			sortParser.parse(Mockito.anyString())
		).thenReturn(
			Collections.singletonList(new SortField(entityField, ascending))
		);

		return SortUtil.getSorts(
			_acceptLanguage, null, sortParser, entityField.getName());
	}

	private void _testGetSortsWithDateTimeField() {
		Sort[] sorts = _getSorts(
			new DateTimeEntityField(
				"dateCreated", locale -> "createDate_sortable",
				locale -> "createDate"),
			true);

		Assert.assertEquals(Sort.STRING_TYPE, sorts[0].getType());
	}

	private void _testGetSortsWithDoubleField() {
		Sort[] sorts = _getSorts(
			new DoubleEntityField("latitude", locale -> "latitude"), true);

		Assert.assertEquals(Sort.DOUBLE_TYPE, sorts[0].getType());
	}

	private void _testGetSortsWithIdField() {
		Sort[] sorts = _getSorts(
			new IdEntityField("id", locale -> "entryClassPK", String::valueOf),
			true);

		Assert.assertEquals(Sort.LONG_TYPE, sorts[0].getType());
		Assert.assertEquals("entryClassPK", sorts[0].getFieldName());
		Assert.assertFalse(sorts[0].isReverse());
	}

	private void _testGetSortsWithIdFieldDescending() {
		Sort[] sorts = _getSorts(
			new IdEntityField("id", locale -> "entryClassPK", String::valueOf),
			false);

		Assert.assertEquals(Sort.LONG_TYPE, sorts[0].getType());
		Assert.assertTrue(sorts[0].isReverse());
	}

	private void _testGetSortsWithIntegerField() {
		Sort[] sorts = _getSorts(
			new IntegerEntityField("status", locale -> "status"), true);

		Assert.assertEquals(Sort.INT_TYPE, sorts[0].getType());
	}

	private void _testGetSortsWithStringField() {
		Sort[] sorts = _getSorts(
			new StringEntityField("name", locale -> "name_sortable"), true);

		Assert.assertEquals(Sort.STRING_TYPE, sorts[0].getType());
	}

	private AcceptLanguage _acceptLanguage;

}