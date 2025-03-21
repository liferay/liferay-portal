/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.portlet;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.seo.template.LayoutSEOTemplateProcessor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutQueryStringComposite;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class CustomAssetDisplayPageFriendlyURLResolverTest {

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
		_setUpAssetServices();
		_setUpInfoServices();
		_setUpLayoutServices();
		_setUpPortal();
	}

	@Test
	public void testGetActualURL() throws Exception {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		_customAssetDisplayPageFriendlyURLResolver.getActualURL(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), Collections.emptyMap(),
			HashMapBuilder.<String, Object>put(
				"request", httpServletRequest
			).build());

		Mockito.verify(
			httpServletRequest
		).setAttribute(
			WebKeys.PAGE_ROBOTS, "noindex, nofollow"
		);
	}

	private void _setUpAssetServices() {
		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"assetDisplayPageEntryLocalService",
			Mockito.mock(AssetDisplayPageEntryLocalService.class));
		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"assetEntryLocalService", Mockito.mock(AssetEntryService.class));
	}

	private void _setUpInfoServices() {
		InfoItemServiceRegistry infoItemServiceRegistry = Mockito.mock(
			InfoItemServiceRegistry.class);

		Mockito.when(
			infoItemServiceRegistry.getFirstInfoItemService(
				Mockito.eq(InfoItemDetailsProvider.class), Mockito.anyString())
		).thenReturn(
			Mockito.mock(InfoItemDetailsProvider.class)
		);

		Mockito.when(
			infoItemServiceRegistry.getFirstInfoItemService(
				Mockito.eq(InfoItemFieldValuesProvider.class),
				Mockito.anyString())
		).thenReturn(
			Mockito.mock(InfoItemFieldValuesProvider.class)
		);

		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"infoItemServiceRegistry", infoItemServiceRegistry);

		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"infoSearchClassMapperRegistry",
			Mockito.mock(InfoSearchClassMapperRegistry.class));
	}

	private void _setUpLayoutServices() {
		LayoutDisplayPageProvider layoutDisplayPageProvider = Mockito.mock(
			LayoutDisplayPageProvider.class);

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			Mockito.mock(LayoutDisplayPageObjectProvider.class);

		Mockito.when(
			layoutDisplayPageObjectProvider.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				Mockito.any(InfoItemReference.class))
		).thenReturn(
			layoutDisplayPageObjectProvider
		);

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			Mockito.mock(LayoutDisplayPageProviderRegistry.class);

		Mockito.when(
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(Mockito.anyString())
		).thenReturn(
			layoutDisplayPageProvider
		);

		Mockito.when(
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(null)
		).thenReturn(
			layoutDisplayPageProvider
		);

		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"layoutDisplayPageProviderRegistry",
			layoutDisplayPageProviderRegistry);

		LayoutLocalService layoutLocalService = Mockito.mock(
			LayoutLocalService.class);

		Mockito.when(
			layoutLocalService.fetchLayoutByFriendlyURL(
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyString())
		).thenReturn(
			Mockito.mock(Layout.class)
		);

		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver, "layoutLocalService",
			layoutLocalService);

		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"layoutPageTemplateEntryService",
			Mockito.mock(LayoutPageTemplateEntryService.class));
		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver,
			"layoutSEOTemplateProcessor",
			Mockito.mock(LayoutSEOTemplateProcessor.class));
	}

	private void _setUpPortal() {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getClassName(Mockito.anyLong())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		LayoutQueryStringComposite layoutQueryStringComposite = Mockito.mock(
			LayoutQueryStringComposite.class);

		Mockito.when(
			layoutQueryStringComposite.getFriendlyURL()
		).thenReturn(
			StringBundler.concat(
				"/e/dptFriendlyURL/", RandomTestUtil.randomLong(),
				StringPool.SLASH, RandomTestUtil.randomLong())
		);

		Mockito.when(
			portal.getPortletFriendlyURLMapperLayoutQueryStringComposite(
				Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())
		).thenReturn(
			layoutQueryStringComposite
		);

		ReflectionTestUtil.setFieldValue(
			_customAssetDisplayPageFriendlyURLResolver, "portal", portal);
	}

	private static final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);

	private final CustomAssetDisplayPageFriendlyURLResolver
		_customAssetDisplayPageFriendlyURLResolver =
			new CustomAssetDisplayPageFriendlyURLResolver();

}