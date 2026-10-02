/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Aswin Narayanadas
 */
public class EditableFragmentEntryProcessorUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetEditableTypes() {
		_testGetEditableTypesWithEditableIdAttribute();
		_testGetEditableTypesWithEditableTag();
	}

	private void _testGetEditableTypes(String html) {
		Map<String, String> editableTypes =
			EditableFragmentEntryProcessorUtil.getEditableTypes(html);

		Assert.assertEquals(
			Arrays.asList(
				"title", "image", "subtitle", "description", "link", "button"),
			new ArrayList<>(editableTypes.keySet()));
	}

	private void _testGetEditableTypesWithEditableIdAttribute() {
		_testGetEditableTypes(
			StringBundler.concat(
				"<div><div data-lfr-editable-id=\"title\" ",
				"data-lfr-editable-type=\"text\"></div><div ",
				"data-lfr-editable-id=\"image\" ",
				"data-lfr-editable-type=\"image\"></div><div ",
				"data-lfr-editable-id=\"subtitle\" ",
				"data-lfr-editable-type=\"text\"></div><div ",
				"data-lfr-editable-id=\"description\" ",
				"data-lfr-editable-type=\"rich-text\"></div><div ",
				"data-lfr-editable-id=\"link\" ",
				"data-lfr-editable-type=\"link\"></div><div ",
				"data-lfr-editable-id=\"button\" ",
				"data-lfr-editable-type=\"text\"></div></div>"));
	}

	private void _testGetEditableTypesWithEditableTag() {
		_testGetEditableTypes(
			StringBundler.concat(
				"<div><lfr-editable id=\"title\" type=\"text\"></lfr-editable>",
				"<lfr-editable id=\"image\" type=\"image\"></lfr-editable>",
				"<lfr-editable id=\"subtitle\" type=\"text\"></lfr-editable>",
				"<lfr-editable id=\"description\" type=\"rich-text\">",
				"</lfr-editable><lfr-editable id=\"link\" type=\"link\">",
				"</lfr-editable><lfr-editable id=\"button\" type=\"text\">",
				"</lfr-editable></div>"));
	}

}