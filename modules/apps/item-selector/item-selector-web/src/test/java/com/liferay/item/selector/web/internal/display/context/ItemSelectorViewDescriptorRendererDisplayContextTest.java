/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.display.context;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
public class ItemSelectorViewDescriptorRendererDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDisplayStyle() {
		ItemSelectorViewDescriptorRendererDisplayContext
			itemSelectorViewDescriptorRendererDisplayContext =
				new ItemSelectorViewDescriptorRendererDisplayContext(
					new MockHttpServletRequest(), null,
					new ItemSelectorViewDescriptor<Object>() {

						@Override
						public ItemDescriptor getItemDescriptor(Object object) {
							return null;
						}

						@Override
						public ItemSelectorReturnType
							getItemSelectorReturnType() {

							return null;
						}

						@Override
						public SearchContainer<Object> getSearchContainer() {
							return null;
						}

					},
					null, null);

		Assert.assertEquals(
			"icon",
			itemSelectorViewDescriptorRendererDisplayContext.getDisplayStyle());
	}

	@Test
	public void testGetDisplayStyleWithDefaultDisplayStyle() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("displayStyle", "descriptive");

		ItemSelectorViewDescriptorRendererDisplayContext
			itemSelectorViewDescriptorRendererDisplayContext =
				new ItemSelectorViewDescriptorRendererDisplayContext(
					mockHttpServletRequest, null,
					new ItemSelectorViewDescriptor<Object>() {

						@Override
						public String getDefaultDisplayStyle() {
							return "descriptive";
						}

						@Override
						public ItemDescriptor getItemDescriptor(Object object) {
							return null;
						}

						@Override
						public ItemSelectorReturnType
							getItemSelectorReturnType() {

							return null;
						}

						@Override
						public SearchContainer<Object> getSearchContainer() {
							return null;
						}

					},
					null, null);

		Assert.assertEquals(
			"descriptive",
			itemSelectorViewDescriptorRendererDisplayContext.getDisplayStyle());
	}

	@Test
	public void testGetDisplayStyleWithDisplayStyleParameter() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("displayStyle", "descriptive");

		ItemSelectorViewDescriptorRendererDisplayContext
			itemSelectorViewDescriptorRendererDisplayContext =
				new ItemSelectorViewDescriptorRendererDisplayContext(
					mockHttpServletRequest, null,
					new ItemSelectorViewDescriptor<Object>() {

						@Override
						public ItemDescriptor getItemDescriptor(Object object) {
							return null;
						}

						@Override
						public ItemSelectorReturnType
							getItemSelectorReturnType() {

							return null;
						}

						@Override
						public SearchContainer<Object> getSearchContainer() {
							return null;
						}

					},
					null, null);

		Assert.assertEquals(
			"descriptive",
			itemSelectorViewDescriptorRendererDisplayContext.getDisplayStyle());
	}

}