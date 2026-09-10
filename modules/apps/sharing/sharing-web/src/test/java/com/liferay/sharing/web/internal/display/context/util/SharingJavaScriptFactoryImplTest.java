/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.display.context.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class SharingJavaScriptFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_assetRendererFactoryRegistryUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_assetRendererFactory = Mockito.mock(AssetRendererFactory.class);

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() ->
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(Mockito.anyString())
		).thenReturn(
			_assetRendererFactory
		);
	}

	@Test
	public void testGetAssetTitle() throws Exception {
		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		String title = RandomTestUtil.randomString();

		Mockito.when(
			assetRenderer.getTitle(Mockito.any(Locale.class))
		).thenReturn(
			title
		);

		Mockito.doReturn(
			assetRenderer
		).when(
			_assetRendererFactory
		).getAssetRenderer(
			Mockito.anyLong()
		);

		Assert.assertEquals(title, _getAssetTitle());
	}

	@Test
	public void testGetAssetTitleWithPortalException() throws Exception {
		Mockito.doThrow(
			PortalException.class
		).when(
			_assetRendererFactory
		).getAssetRenderer(
			Mockito.anyLong()
		);

		Assert.assertNull(_getAssetTitle());
	}

	@Test
	public void testGetAssetTitleWithRuntimeException() throws Exception {
		Mockito.doThrow(
			UnsupportedOperationException.class
		).when(
			_assetRendererFactory
		).getAssetRenderer(
			Mockito.anyLong()
		);

		Assert.assertNull(_getAssetTitle());
	}

	private String _getAssetTitle() {
		return ReflectionTestUtil.invoke(
			new SharingJavaScriptFactoryImpl(), "_getAssetTitle",
			new Class<?>[] {String.class, long.class, Locale.class},
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			LocaleUtil.US);
	}

	private static final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);

	private AssetRendererFactory<?> _assetRendererFactory;

}