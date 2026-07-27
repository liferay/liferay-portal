/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.item.selector.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class SelectAssetVocabularyDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpSiteConnectedGroupGroupProviderUtil();

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);
	}

	@After
	public void tearDown() {
		_assetVocabularyServiceUtilMockedStatic.close();
		_frameworkUtilMockedStatic.close();
		_siteConnectedGroupGroupProviderUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-99346")
	public void testGetAssetVocabularies() throws Exception {
		List<AssetVocabulary> assetVocabularies = Arrays.asList(
			Mockito.mock(AssetVocabulary.class),
			Mockito.mock(AssetVocabulary.class),
			Mockito.mock(AssetVocabulary.class),
			Mockito.mock(AssetVocabulary.class));

		long[] groupIds = {
			_GROUP_ID, RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong()
		};

		_assetVocabularyServiceUtilMockedStatic.when(
			() -> AssetVocabularyServiceUtil.getGroupVocabularies(
				Mockito.eq(groupIds),
				Mockito.eq(
					new int[] {
						AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC
					}))
		).thenReturn(
			assetVocabularies
		);

		_siteConnectedGroupGroupProviderUtilMockedStatic.when(
			() ->
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(_GROUP_ID)
		).thenReturn(
			groupIds
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		SelectAssetVocabularyDisplayContext
			selectAssetVocabularyDisplayContext =
				new SelectAssetVocabularyDisplayContext(
					mockHttpServletRequest, null);

		Assert.assertEquals(
			assetVocabularies,
			ReflectionTestUtil.invoke(
				selectAssetVocabularyDisplayContext, "_getAssetVocabularies",
				new Class<?>[0]));
	}

	private void _setUpSiteConnectedGroupGroupProviderUtil() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(
				SiteConnectedGroupGroupProviderUtil.class)
		).thenReturn(
			bundleContext.getBundle()
		);

		_siteConnectedGroupGroupProviderUtilMockedStatic = Mockito.mockStatic(
			SiteConnectedGroupGroupProviderUtil.class);
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<AssetVocabularyServiceUtil>
		_assetVocabularyServiceUtilMockedStatic = Mockito.mockStatic(
			AssetVocabularyServiceUtil.class);
	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private MockedStatic<SiteConnectedGroupGroupProviderUtil>
		_siteConnectedGroupGroupProviderUtilMockedStatic;
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}