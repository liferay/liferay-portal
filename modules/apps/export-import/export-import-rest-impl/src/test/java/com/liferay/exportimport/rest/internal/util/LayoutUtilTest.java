/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.BadRequestException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Daniel Raposo
 */
public class LayoutUtilTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetLayoutIds() {
		Assert.assertArrayEquals(
			new long[] {1, 2, 3, 4, 5},
			LayoutUtil.getLayoutIds(
				_layouts, _toParameterMap(null, null, null, true, null)));
		Assert.assertArrayEquals(
			new long[] {4, 5},
			LayoutUtil.getLayoutIds(
				_layouts,
				_toParameterMap(null, new String[] {"a"}, null, true, null)));
		Assert.assertArrayEquals(
			new long[] {2, 4, 5},
			LayoutUtil.getLayoutIds(
				_layouts,
				_toParameterMap(
					null, new String[] {"a"}, new String[] {"b"}, true, null)));
		Assert.assertArrayEquals(
			new long[] {1, 3},
			LayoutUtil.getLayoutIds(
				_layouts,
				_toParameterMap(
					new String[] {"b"}, null, null, false,
					new String[] {"a"})));
		Assert.assertArrayEquals(
			new long[] {2, 3},
			LayoutUtil.getLayoutIds(
				_layouts,
				_toParameterMap(
					new String[] {"a"}, null, null, false,
					new String[] {"a"})));
		Assert.assertArrayEquals(
			new long[] {3},
			LayoutUtil.getLayoutIds(
				_layouts,
				_toParameterMap(null, null, new String[] {"c"}, false, null)));
	}

	@Test
	public void testGetLayoutIdsWhenLayoutSetIsNotSelected() {
		Assert.assertArrayEquals(
			new long[0], LayoutUtil.getLayoutIds(_layouts, Map.of()));
	}

	@Test
	public void testGetLayoutIdsWhenPageDoesNotExist() {
		AssertUtils.assertFailure(
			BadRequestException.class,
			"No page exists with external reference code \"z\"",
			() -> LayoutUtil.getLayoutIds(
				_layouts,
				_toParameterMap(null, null, null, false, new String[] {"z"})));
	}

	private static Layout _mockLayout(
		String externalReferenceCode, long layoutId, long parentLayoutId) {

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			layout.getLayoutId()
		).thenReturn(
			layoutId
		);

		Mockito.when(
			layout.getParentLayoutId()
		).thenReturn(
			parentLayoutId
		);

		return layout;
	}

	private Map<String, String[]> _toParameterMap(
		String[] excludedItemExternalReferenceCodes,
		String[] excludedSubtreeExternalReferenceCodes,
		String[] itemExternalReferenceCodes, boolean selectAll,
		String[] subtreeExternalReferenceCodes) {

		return HashMapBuilder.put(
			PortletDataHandlerUtil.CONTROL_NAME_PAGES,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerUtil.CONTROL_NAME_PAGES + "_all",
			new String[] {String.valueOf(selectAll)}
		).put(
			PortletDataHandlerUtil.CONTROL_NAME_PAGES + "_excluded-items",
			excludedItemExternalReferenceCodes
		).put(
			PortletDataHandlerUtil.CONTROL_NAME_PAGES + "_excluded-subtrees",
			excludedSubtreeExternalReferenceCodes
		).put(
			PortletDataHandlerUtil.CONTROL_NAME_PAGES + "_items",
			itemExternalReferenceCodes
		).put(
			PortletDataHandlerUtil.CONTROL_NAME_PAGES + "_subtrees",
			subtreeExternalReferenceCodes
		).put(
			PortletDataHandlerUtil.PORTLET_DATA_HANDLER_NAME_LAYOUT_SET,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private static final List<Layout> _layouts = Arrays.asList(
		_mockLayout("a", 1, 0), _mockLayout("b", 2, 1), _mockLayout("c", 3, 2),
		_mockLayout("d", 4, 0), _mockLayout("e", 5, 4));

}