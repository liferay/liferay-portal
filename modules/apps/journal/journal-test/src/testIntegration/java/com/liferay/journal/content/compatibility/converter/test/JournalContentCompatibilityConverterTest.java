/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.compatibility.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.content.compatibility.converter.JournalContentCompatibilityConverter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class JournalContentCompatibilityConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAllFieldsCompatibilityLayer() throws Exception {
		String content = read(
			"test-journal-content-all-fields-compatibility.xml");

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = read(
			"test-journal-content-all-fields-compatibility-expected-" +
				"results.xml");

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			_getFormattedString(expectedDocument),
			_getFormattedString(document));
	}

	@Test
	public void testComplexNestedFieldsCompatibilityLayer() throws Exception {
		String content = read(
			"test-journal-content-complex-nested-fields-compatibility.xml");

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = read(
			"test-journal-content-complex-nested-fields-compatibility-" +
				"expected-results.xml");

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			_getFormattedString(expectedDocument),
			_getFormattedString(document));
	}

	@Test
	public void testGetLinkToLayoutValue() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String value = ReflectionTestUtil.invoke(
			_journalContentCompatibilityConverter, "_convertLinkToLayoutValue",
			new Class<?>[] {Locale.class, String.class}, LocaleUtil.US,
			StringBundler.concat(
				layout.getLayoutId(), StringPool.AT,
				layout.isPublicLayout() ? "public" : "private", StringPool.AT,
				layout.getGroupId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

		Assert.assertEquals(layout.getGroupId(), jsonObject.getLong("groupId"));
		Assert.assertEquals(
			layout.getLayoutId(), jsonObject.getLong("layoutId"));
		Assert.assertEquals(
			layout.getName(LocaleUtil.US), jsonObject.getString("name"));
		Assert.assertFalse(jsonObject.getBoolean("privateLayout"));
	}

	@Test
	public void testGetLinkToLayoutValueWithoutGroupId() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String value = ReflectionTestUtil.invoke(
			_journalContentCompatibilityConverter, "_convertLinkToLayoutValue",
			new Class<?>[] {Locale.class, String.class}, LocaleUtil.US,
			StringBundler.concat(
				layout.getLayoutId(), StringPool.AT,
				layout.isPublicLayout() ? "public" : "private"));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

		Assert.assertFalse(jsonObject.has("groupId"));
		Assert.assertEquals(
			layout.getLayoutId(), jsonObject.getLong("layoutId"));
		Assert.assertFalse(jsonObject.has("name"));
		Assert.assertFalse(jsonObject.getBoolean("privateLayout"));
	}

	@Test
	public void testLinkToPageFieldCompatibilityLayer() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group, "layout1");
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group, "layout2");
		Layout layout3 = LayoutTestUtil.addTypeContentLayout(_group, "layout3");
		Layout layout4 = LayoutTestUtil.addTypeContentLayout(_group, "layout4");

		String content = StringUtil.replace(
			read("test-journal-content-link-to-page-field-compatibility.xml"),
			new String[] {
				"$GROUP_ID", "$LAYOUT_ID_1", "$LAYOUT_ID_2", "$LAYOUT_ID_3",
				"$LAYOUT_ID_4"
			},
			new String[] {
				String.valueOf(_group.getGroupId()),
				String.valueOf(layout1.getLayoutId()),
				String.valueOf(layout2.getLayoutId()),
				String.valueOf(layout3.getLayoutId()),
				String.valueOf(layout4.getLayoutId())
			});

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = StringUtil.replace(
			read(
				"test-journal-content-link-to-page-field-compatibility-" +
					"expected-results.xml"),
			new String[] {
				"$GROUP_ID", "$LAYOUT_ID_1", "$LAYOUT_ID_2", "$LAYOUT_ID_3",
				"$LAYOUT_ID_4", "$UUID_1", "$UUID_2", "$UUID_3", "$UUID_4"
			},
			new String[] {
				String.valueOf(_group.getGroupId()),
				String.valueOf(layout1.getLayoutId()),
				String.valueOf(layout2.getLayoutId()),
				String.valueOf(layout3.getLayoutId()),
				String.valueOf(layout4.getLayoutId()),
				String.valueOf(layout1.getUuid()),
				String.valueOf(layout2.getUuid()),
				String.valueOf(layout3.getUuid()),
				String.valueOf(layout4.getUuid())
			});

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			expectedDocument.formattedString(), document.formattedString());
	}

	@Test
	public void testMultiListFieldCompatibilityLayer() throws Exception {
		String content = read(
			"test-journal-content-multi-list-field-compatibility.xml");

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = read(
			"test-journal-content-multi-list-field-compatibility-expected-" +
				"results.xml");

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			_getFormattedString(expectedDocument),
			_getFormattedString(document));
	}

	@Test
	public void testNestedFieldsCompatibilityLayer() throws Exception {
		String content = read(
			"test-journal-content-nested-fields-compatibility.xml");

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = read(
			"test-journal-content-nested-fields-compatibility-expected-" +
				"results.xml");

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			_getFormattedString(expectedDocument),
			_getFormattedString(document));
	}

	@Test
	public void testRepeatableNestedFieldsCompatibilityLayer()
		throws Exception {

		String content = read(
			"test-journal-content-repeatable-nested-fields-compatibility.xml");

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = read(
			"test-journal-content-repeatable-nested-fields-compatibility-" +
				"expected-results.xml");

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			_getFormattedString(expectedDocument),
			_getFormattedString(document));
	}

	@Test
	public void testVersionCompatibilityLayer() throws Exception {
		String content = read("test-journal-content-version-compatibility.xml");

		content = _journalContentCompatibilityConverter.convert(content);

		Document document = SAXReaderUtil.read(content);

		String expectedContent = read(
			"test-journal-content-version-compatibility-expected-results.xml");

		Document expectedDocument = SAXReaderUtil.read(expectedContent);

		Assert.assertEquals(
			_getFormattedString(expectedDocument),
			_getFormattedString(document));
	}

	protected String read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/journal/dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private String _getFormattedString(Document document) throws Exception {
		String formattedString = document.formattedString();

		return formattedString.replaceAll("instance-id=\"[a-zA-Z0-9]+\"", "");
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalContentCompatibilityConverter
		_journalContentCompatibilityConverter;

}