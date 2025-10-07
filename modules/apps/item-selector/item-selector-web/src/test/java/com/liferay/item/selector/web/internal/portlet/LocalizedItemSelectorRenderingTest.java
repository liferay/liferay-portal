/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.portlet;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorRendering;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class LocalizedItemSelectorRenderingTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetVerticalNavItemList() throws Exception {
		String title = RandomTestUtil.randomString();

		ItemSelectorViewRenderer itemSelectorViewRenderer1 =
			_getItemSelectorViewRenderer(new TestItemSelectorView1(title));
		ItemSelectorViewRenderer itemSelectorViewRenderer2 =
			_getItemSelectorViewRenderer(new TestItemSelectorView2(title));

		Locale locale = LocaleUtil.getDefault();

		_testGetVerticalNavItemList(
			Arrays.asList(itemSelectorViewRenderer1, itemSelectorViewRenderer2),
			locale, title);

		ItemSelectorViewRenderer itemSelectorViewRenderer3 =
			_getItemSelectorViewRenderer(new TestItemSelectorView1(title));
		ItemSelectorViewRenderer itemSelectorViewRenderer4 =
			_getItemSelectorViewRenderer(new TestItemSelectorView2(title));

		_testGetVerticalNavItemList(
			Arrays.asList(
				itemSelectorViewRenderer1, itemSelectorViewRenderer2,
				itemSelectorViewRenderer3, itemSelectorViewRenderer4),
			locale, title);
	}

	private void _assertNavigationItem(
		Class<?> clazz, NavigationItem navigationItem, String title) {

		String id = StringBundler.concat(
			clazz.getName(), StringPool.UNDERLINE, title);

		Assert.assertEquals(title, navigationItem.get("label"));

		Map<String, String> data = (Map<String, String>)navigationItem.get(
			"data");

		Assert.assertEquals(id, String.valueOf(data.get("id")));
	}

	private ItemSelectorViewRenderer _getItemSelectorViewRenderer(
		ItemSelectorView<ItemSelectorCriterion> itemSelectorView) {

		ItemSelectorViewRenderer itemSelectorViewRenderer = Mockito.mock(
			ItemSelectorViewRenderer.class);

		Mockito.when(
			itemSelectorViewRenderer.getItemSelectorView()
		).thenReturn(
			itemSelectorView
		);

		return itemSelectorViewRenderer;
	}

	private void _testGetVerticalNavItemList(
		List<ItemSelectorViewRenderer> itemSelectorViewRenderers, Locale locale,
		String title) {

		ItemSelectorRendering itemSelectorRendering = Mockito.mock(
			ItemSelectorRendering.class);

		Mockito.when(
			itemSelectorRendering.getItemSelectorViewRenderers()
		).thenReturn(
			itemSelectorViewRenderers
		);

		Mockito.when(
			itemSelectorRendering.getSelectedTab()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		LocalizedItemSelectorRendering localizedItemSelectorRendering =
			new LocalizedItemSelectorRendering(locale, itemSelectorRendering);

		List<NavigationItem> navigationItems =
			localizedItemSelectorRendering.getNavigationItems();

		Assert.assertEquals(
			navigationItems.toString(), 2, navigationItems.size());

		_assertNavigationItem(
			TestItemSelectorView1.class, navigationItems.get(0), title);
		_assertNavigationItem(
			TestItemSelectorView2.class, navigationItems.get(1), title);
	}

	private class BaseItemSelectorView
		implements ItemSelectorView<ItemSelectorCriterion> {

		public BaseItemSelectorView(String title) {
			_title = title;
		}

		@Override
		public Class<? extends ItemSelectorCriterion>
			getItemSelectorCriterionClass() {

			return null;
		}

		@Override
		public List<ItemSelectorReturnType>
			getSupportedItemSelectorReturnTypes() {

			return Collections.emptyList();
		}

		@Override
		public String getTitle(Locale locale) {
			return _title;
		}

		@Override
		public void renderHTML(
				ServletRequest servletRequest, ServletResponse servletResponse,
				ItemSelectorCriterion itemSelectorCriterion,
				PortletURL portletURL, String itemSelectedEventName,
				boolean search)
			throws IOException, ServletException {
		}

		private final String _title;

	}

	private class TestItemSelectorView1 extends BaseItemSelectorView {

		public TestItemSelectorView1(String title) {
			super(title);
		}

	}

	private class TestItemSelectorView2 extends BaseItemSelectorView {

		public TestItemSelectorView2(String title) {
			super(title);
		}

	}

}