/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.display.context;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class RenderMaxStateLayoutStructureDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_layoutPageTemplateEntryLocalServiceUtilMockedStatic.close();
		_layoutPageTemplateStructureLocalServiceUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-111336")
	public void testGetLayoutStructure() {
		_testGetLayoutStructure();
		_testGetLayoutStructureWithMasterLayout();
	}

	private LayoutStructure _getLayoutStructure(Layout layout) {
		RenderMaxStateLayoutStructureDisplayContext
			renderMaxStateLayoutStructureDisplayContext =
				new RenderMaxStateLayoutStructureDisplayContext();

		return renderMaxStateLayoutStructureDisplayContext.getLayoutStructure(
			layout);
	}

	private Layout _mockLayout(long masterLayoutPlid) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getMasterLayoutPlid()
		).thenReturn(
			masterLayoutPlid
		);

		return layout;
	}

	private void _mockLayoutPageTemplateEntry(long groupId, long plid) {
		LayoutPageTemplateEntry layoutPageTemplateEntry = Mockito.mock(
			LayoutPageTemplateEntry.class);

		Mockito.when(
			layoutPageTemplateEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			layoutPageTemplateEntry.getPlid()
		).thenReturn(
			plid
		);

		Mockito.when(
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(plid)
		).thenReturn(
			layoutPageTemplateEntry
		);
	}

	private void _mockLayoutPageTemplateStructure(
		long groupId, String mainItemId, long plid) {

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem(mainItemId);

		LayoutPageTemplateStructure layoutPageTemplateStructure = Mockito.mock(
			LayoutPageTemplateStructure.class);

		Mockito.when(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData()
		).thenReturn(
			layoutStructure.toString()
		);

		Mockito.when(
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(groupId, plid)
		).thenReturn(
			layoutPageTemplateStructure
		);
	}

	private void _testGetLayoutStructure() {
		Mockito.when(
			PortalUtil.getUniqueElementId()
		).thenReturn(
			"default-root", RandomTestUtil.randomString()
		);

		LayoutStructure layoutStructure = _getLayoutStructure(
			Mockito.mock(Layout.class));

		Assert.assertEquals("default-root", layoutStructure.getMainItemId());
	}

	private void _testGetLayoutStructureWithMasterLayout() {
		long groupId = RandomTestUtil.randomLong();
		long plid = RandomTestUtil.randomLong();

		_mockLayoutPageTemplateEntry(groupId, plid);
		_mockLayoutPageTemplateStructure(groupId, "master-root", plid);

		LayoutStructure layoutStructure = _getLayoutStructure(
			_mockLayout(plid));

		Assert.assertEquals("master-root", layoutStructure.getMainItemId());
	}

	private static final MockedStatic<LayoutPageTemplateEntryLocalServiceUtil>
		_layoutPageTemplateEntryLocalServiceUtilMockedStatic =
			Mockito.mockStatic(LayoutPageTemplateEntryLocalServiceUtil.class);
	private static final MockedStatic
		<LayoutPageTemplateStructureLocalServiceUtil>
			_layoutPageTemplateStructureLocalServiceUtilMockedStatic =
				Mockito.mockStatic(
					LayoutPageTemplateStructureLocalServiceUtil.class);
	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}