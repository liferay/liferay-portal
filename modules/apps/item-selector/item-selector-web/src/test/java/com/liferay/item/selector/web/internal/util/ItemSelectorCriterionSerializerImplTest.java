/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.util;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.web.internal.TestFileEntryItemSelectorReturnType;
import com.liferay.item.selector.web.internal.TestStringItemSelectorReturnType;
import com.liferay.item.selector.web.internal.TestURLItemSelectorReturnType;
import com.liferay.item.selector.web.internal.item.selector.FlickrItemSelectorCriterion;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;

/**
 * @author Iván Zaera
 * @author Roberto Díaz
 */
public class ItemSelectorCriterionSerializerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_flickrItemSelectorCriterion = new FlickrItemSelectorCriterion();

		_flickrItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			_testStringItemSelectorReturnType, _testURLItemSelectorReturnType);

		_stubItemSelectorCriterionSerializerImpl.activate(
			SystemBundleUtil.getBundleContext());

		_stubItemSelectorCriterionSerializerImpl.addItemSelectorReturnType(
			_testFileEntryItemSelectorReturnType);
		_stubItemSelectorCriterionSerializerImpl.addItemSelectorReturnType(
			_testStringItemSelectorReturnType);
		_stubItemSelectorCriterionSerializerImpl.addItemSelectorReturnType(
			_testURLItemSelectorReturnType);

		ReflectionTestUtil.setFieldValue(
			_stubItemSelectorCriterionSerializerImpl, "_jsonFactory",
			new JSONFactoryImpl());
	}

	@Test
	public void testGetProperties() {
		_flickrItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			_testURLItemSelectorReturnType);

		String json = _stubItemSelectorCriterionSerializerImpl.serialize(
			_flickrItemSelectorCriterion);

		String itemSelectorReturnTypeKey =
			ItemSelectorKeyUtil.getItemSelectorReturnTypeKey(
				TestURLItemSelectorReturnType.class);

		json = _assert(
			"\"desiredItemSelectorReturnTypes\":\"" +
				itemSelectorReturnTypeKey + "\"",
			json);

		json = _assert("\"tags\":[\"me\",\"photo\",\"picture\"]", json);
		json = _assert("\"user\":\"anonymous\"", json);

		Assert.assertEquals("{,,}", json);
	}

	@Test
	public void testSetProperties() {
		Class<? extends ItemSelectorReturnType>
			testURLItemSelectorReturnTypeClass =
				_testURLItemSelectorReturnType.getClass();

		String json = StringBundler.concat(
			"{\"desiredItemSelectorReturnTypes\":\"",
			testURLItemSelectorReturnTypeClass.getName(), "\",\"tags\":",
			"[\"tag1\",\"tag2\",\"tag3\"],\"user\":\"Joe Bloggs\"}");

		_flickrItemSelectorCriterion =
			_stubItemSelectorCriterionSerializerImpl.deserialize(
				_flickrItemSelectorCriterion.getClass(), json);

		Assert.assertEquals(
			"Joe Bloggs", _flickrItemSelectorCriterion.getUser());
		Assert.assertArrayEquals(
			new String[] {"tag1", "tag2", "tag3"},
			_flickrItemSelectorCriterion.getTags());

		Assert.assertEquals(
			ListUtil.fromArray(_testURLItemSelectorReturnType),
			_flickrItemSelectorCriterion.getDesiredItemSelectorReturnTypes());
	}

	private String _assert(String expected, String json) {
		Assert.assertTrue(json, json.contains(expected));

		return json.replaceAll(Pattern.quote(expected), "");
	}

	private FlickrItemSelectorCriterion _flickrItemSelectorCriterion;
	private final StubItemSelectorCriterionSerializerImpl
		_stubItemSelectorCriterionSerializerImpl =
			new StubItemSelectorCriterionSerializerImpl();
	private final ItemSelectorReturnType _testFileEntryItemSelectorReturnType =
		new TestFileEntryItemSelectorReturnType();
	private final ItemSelectorReturnType _testStringItemSelectorReturnType =
		new TestStringItemSelectorReturnType();
	private final ItemSelectorReturnType _testURLItemSelectorReturnType =
		new TestURLItemSelectorReturnType();

	private class StubItemSelectorCriterionSerializerImpl
		extends ItemSelectorCriterionSerializerImpl {

		@Override
		public void addItemSelectorReturnType(
			ItemSelectorReturnType itemSelectorReturnType) {

			super.addItemSelectorReturnType(itemSelectorReturnType);
		}

		@Override
		protected void activate(BundleContext bundleContext) {
			super.activate(bundleContext);
		}

	}

}