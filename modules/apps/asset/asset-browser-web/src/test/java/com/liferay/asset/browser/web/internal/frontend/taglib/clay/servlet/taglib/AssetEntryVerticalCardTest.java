/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.browser.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.asset.browser.web.internal.display.context.AssetBrowserDisplayContext;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author David Reyes
 */
public class AssetEntryVerticalCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_groupLocalServiceUtilMockedStatic.reset();

		Mockito.doReturn(
			_assetRendererFactory
		).when(
			_assetBrowserDisplayContext
		).getAssetRendererFactory();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	@Test
	@TestInfo("LPD-111121")
	public void testGetSubtitleWhenSearchEverywhereIsTrue() throws Exception {
		Mockito.when(
			_assetBrowserDisplayContext.getTypeSelection()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_assetBrowserDisplayContext.isSearchEverywhere()
		).thenReturn(
			true
		);

		Mockito.when(
			_assetEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Test & Co"
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(_GROUP_ID)
		).thenReturn(
			group
		);

		Assert.assertEquals("Test & Co", _getSubtitle());
	}

	@Test
	@TestInfo("LPD-111121")
	public void testGetSubtitleWhenTypeSelectionIsNull() {
		Mockito.when(
			_assetBrowserDisplayContext.getSubtypeSelectionId()
		).thenReturn(
			_SUBTYPE_SELECTION_ID
		);

		Mockito.when(
			_assetRendererFactory.getTypeName(
				LocaleUtil.US, _SUBTYPE_SELECTION_ID)
		).thenReturn(
			"Test & Co"
		);

		Assert.assertEquals("Test & Co", _getSubtitle());
	}

	private String _getSubtitle() {
		AssetEntryVerticalCard assetEntryVerticalCard =
			new AssetEntryVerticalCard(
				_assetEntry, _renderRequest, _assetBrowserDisplayContext);

		return assetEntryVerticalCard.getSubtitle();
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _SUBTYPE_SELECTION_ID =
		RandomTestUtil.randomLong();

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private final AssetBrowserDisplayContext _assetBrowserDisplayContext =
		Mockito.mock(AssetBrowserDisplayContext.class);
	private final AssetEntry _assetEntry = Mockito.mock(AssetEntry.class);
	private final AssetRendererFactory<?> _assetRendererFactory = Mockito.mock(
		AssetRendererFactory.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);

}