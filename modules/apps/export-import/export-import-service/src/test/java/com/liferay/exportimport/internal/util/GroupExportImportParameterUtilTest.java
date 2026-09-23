/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.util;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class GroupExportImportParameterUtilTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetCurrentGroupExternalReferenceCodeWhenBlank() {
		Assert.assertNull(
			GroupExportImportParameterUtil.getCurrentGroupExternalReferenceCode(
				GroupExportImportParameterUtil.getGroupExportParameterMap(
					StringPool.BLANK, Collections.emptyMap())));
	}

	@Test
	public void testGetCurrentGroupExternalReferenceCodeWhenSet() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			externalReferenceCode,
			GroupExportImportParameterUtil.getCurrentGroupExternalReferenceCode(
				GroupExportImportParameterUtil.getGroupExportParameterMap(
					externalReferenceCode, Collections.emptyMap())));
	}

	@Test
	public void testGetGroupExportParameterMapRemovesGroupExternalReferenceCodes() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Map<String, String[]> groupParameterMap =
			GroupExportImportParameterUtil.getGroupExportParameterMap(
				externalReferenceCode,
				HashMapBuilder.put(
					PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
					new String[] {
						externalReferenceCode, RandomTestUtil.randomString()
					}
				).build());

		Assert.assertArrayEquals(
			new String[0],
			GroupExportImportParameterUtil.
				getSelectedGroupExternalReferenceCodes(groupParameterMap));
		Assert.assertEquals(
			externalReferenceCode,
			GroupExportImportParameterUtil.getCurrentGroupExternalReferenceCode(
				groupParameterMap));
		Assert.assertTrue(
			GroupExportImportParameterUtil.isGroupScoped(groupParameterMap));
	}

	@Test
	public void testGetGroupExportParameterMapWithCommentsAndRatings() {
		Map<String, String[]> groupParameterMap =
			GroupExportImportParameterUtil.getGroupExportParameterMap(
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					PortletDataHandlerKeys.COMMENTS,
					new String[] {Boolean.TRUE.toString()}
				).put(
					PortletDataHandlerKeys.RATINGS,
					new String[] {Boolean.TRUE.toString()}
				).build());

		Assert.assertTrue(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.COMMENTS));
		Assert.assertTrue(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.RATINGS));
	}

	@Test
	public void testGetGroupExportParameterMapWithDeletionsAndPermissionsEnabled() {
		Map<String, String[]> groupParameterMap =
			GroupExportImportParameterUtil.getGroupExportParameterMap(
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					PortletDataHandlerKeys.DELETIONS,
					new String[] {Boolean.TRUE.toString()}
				).put(
					PortletDataHandlerKeys.LAYOUT_SET_PRIVATE_LAYOUT,
					new String[] {Boolean.TRUE.toString()}
				).put(
					PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
					new String[] {Boolean.TRUE.toString()}
				).put(
					PortletDataHandlerKeys.PERMISSIONS,
					new String[] {Boolean.TRUE.toString()}
				).build());

		Assert.assertFalse(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.DELETIONS));
		Assert.assertFalse(
			MapUtil.getBoolean(
				groupParameterMap,
				PortletDataHandlerKeys.LAYOUT_SET_PRIVATE_LAYOUT));
		Assert.assertFalse(
			MapUtil.getBoolean(
				groupParameterMap,
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS));
		Assert.assertFalse(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.PERMISSIONS));
	}

	@Test
	public void testGetGroupExportParameterMapWithDisabledPortletData() {
		Map<String, String[]> groupParameterMap =
			GroupExportImportParameterUtil.getGroupExportParameterMap(
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					PortletDataHandlerKeys.PORTLET_DATA,
					new String[] {Boolean.FALSE.toString()}
				).put(
					PortletDataHandlerKeys.PORTLET_DATA_ALL,
					new String[] {Boolean.FALSE.toString()}
				).put(
					PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
					new String[] {Boolean.FALSE.toString()}
				).build());

		Assert.assertTrue(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.PORTLET_DATA));
		Assert.assertTrue(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL));
		Assert.assertTrue(
			MapUtil.getBoolean(
				groupParameterMap,
				PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT));
		Assert.assertTrue(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.LAYOUT_SET_SETTINGS));
	}

	@Test
	public void testGetGroupImportParameterMapWithCopyAsNewDataStrategy() {
		Map<String, String[]> groupParameterMap =
			GroupExportImportParameterUtil.getGroupImportParameterMap(
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					PortletDataHandlerKeys.DATA_STRATEGY,
					new String[] {
						PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW
					}
				).put(
					PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
					new String[] {
						PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_ADD_AS_NEW
					}
				).build());

		Assert.assertEquals(
			PortletDataHandlerKeys.DATA_STRATEGY_MIRROR,
			MapUtil.getString(
				groupParameterMap, PortletDataHandlerKeys.DATA_STRATEGY));
		Assert.assertEquals(
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_UUID,
			MapUtil.getString(
				groupParameterMap, PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE));
	}

	@Test
	public void testGetGroupImportParameterMapWithDeleteFlagsEnabled() {
		Map<String, String[]> groupParameterMap =
			GroupExportImportParameterUtil.getGroupImportParameterMap(
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
					new String[] {Boolean.TRUE.toString()}
				).put(
					PortletDataHandlerKeys.DELETE_PORTLET_DATA,
					new String[] {Boolean.TRUE.toString()}
				).build());

		Assert.assertFalse(
			MapUtil.getBoolean(
				groupParameterMap,
				PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS));
		Assert.assertFalse(
			MapUtil.getBoolean(
				groupParameterMap, PortletDataHandlerKeys.DELETE_PORTLET_DATA));
	}

	@Test
	public void testGetSelectedGroupExternalReferenceCodesWhenMissing() {
		Assert.assertArrayEquals(
			new String[0],
			GroupExportImportParameterUtil.
				getSelectedGroupExternalReferenceCodes(Collections.emptyMap()));
	}

	@Test
	public void testGetSelectedGroupExternalReferenceCodesWhenSet() {
		String[] externalReferenceCodes = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		Assert.assertArrayEquals(
			externalReferenceCodes,
			GroupExportImportParameterUtil.
				getSelectedGroupExternalReferenceCodes(
					HashMapBuilder.put(
						PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
						externalReferenceCodes
					).build()));
	}

	@Test
	public void testIsGroupScopedWithCurrentGroupExternalReferenceCode() {
		PortletDataContext portletDataContext = Mockito.mock(
			PortletDataContext.class);

		Mockito.when(
			portletDataContext.getParameterMap()
		).thenReturn(
			GroupExportImportParameterUtil.getGroupExportParameterMap(
				RandomTestUtil.randomString(), Collections.emptyMap())
		);

		Assert.assertTrue(
			GroupExportImportParameterUtil.isGroupScoped(portletDataContext));
	}

	@Test
	public void testIsGroupScopedWithoutCurrentGroupExternalReferenceCode() {
		Assert.assertFalse(
			GroupExportImportParameterUtil.isGroupScoped(
				HashMapBuilder.put(
					PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
					new String[] {RandomTestUtil.randomString()}
				).build()));
	}

}