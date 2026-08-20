/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.net.URL;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Iván Zaera
 */
public class FrontendTokenDefinitionRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_resourceBundleLoaderUtilMockedStatic = Mockito.mockStatic(
			ResourceBundleLoaderUtil.class);

		_resourceBundleLoaderUtilMockedStatic.when(
			ResourceBundleLoaderUtil::getPortalResourceBundleLoader
		).thenReturn(
			Mockito.mock(ResourceBundleLoader.class)
		);
	}

	@After
	public void tearDown() {
		_resourceBundleLoaderUtilMockedStatic.close();
	}

	@Test
	public void testGetFrontendTokenDefinition() throws Exception {
		_withGlobalFrontendTokenDefinition(
			false,
			frontendTokenDefinitionRegistryImpl -> Assert.assertNull(
				frontendTokenDefinitionRegistryImpl.getFrontendTokenDefinition(
					123L, "com.liferay.frontend.js.clay.web")));
		_withGlobalFrontendTokenDefinition(
			true,
			frontendTokenDefinitionRegistryImpl -> {
				FrontendTokenDefinition frontendTokenDefinition =
					frontendTokenDefinitionRegistryImpl.
						getFrontendTokenDefinition(
							123L, "com.liferay.frontend.js.clay.web");

				Assert.assertNotNull(frontendTokenDefinition);

				Assert.assertEquals(
					FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL,
					frontendTokenDefinition.getThemeType());
			});
	}

	@Test
	public void testGetFrontendTokenDefinitions() throws Exception {
		_withGlobalFrontendTokenDefinition(
			false,
			frontendTokenDefinitionRegistryImpl -> {
				List<FrontendTokenDefinition> frontendTokenDefinitions =
					frontendTokenDefinitionRegistryImpl.
						getFrontendTokenDefinitions(123L);

				Assert.assertTrue(
					frontendTokenDefinitions.toString(),
					frontendTokenDefinitions.isEmpty());
			});
		_withGlobalFrontendTokenDefinition(
			true,
			frontendTokenDefinitionRegistryImpl -> {
				List<FrontendTokenDefinition> frontendTokenDefinitions =
					frontendTokenDefinitionRegistryImpl.
						getFrontendTokenDefinitions(123L);

				Assert.assertEquals(
					frontendTokenDefinitions.toString(), 1,
					frontendTokenDefinitions.size());

				FrontendTokenDefinition frontendTokenDefinition =
					frontendTokenDefinitions.get(0);

				Assert.assertEquals(
					FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL,
					frontendTokenDefinition.getThemeType());
			});
	}

	@Test
	public void testGetJSON() throws Exception {
		FrontendTokenDefinitionRegistryImpl
			frontendTokenDefinitionRegistryImpl =
				new FrontendTokenDefinitionRegistryImpl();

		frontendTokenDefinitionRegistryImpl.jsonFactory = new JSONFactoryImpl();
		frontendTokenDefinitionRegistryImpl.portal = new PortalImpl();

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getEntry("WEB-INF/frontend-token-definition.json")
		).thenReturn(
			_frontendTokenDefinitionJSONURL
		);

		Mockito.when(
			bundle.getEntry("WEB-INF/liferay-look-and-feel.xml")
		).thenReturn(
			_liferayLookAndFeelXMLURL
		);

		Mockito.when(
			bundle.getHeaders(Mockito.anyString())
		).thenReturn(
			new HashMapDictionary<>()
		);

		List<FrontendTokenDefinitionImpl> frontendTokenDefinitionImpls =
			frontendTokenDefinitionRegistryImpl.getFrontendTokenDefinitionImpls(
				bundle);

		Assert.assertEquals(
			frontendTokenDefinitionImpls.toString(), 2,
			frontendTokenDefinitionImpls.size());

		JSONFactory jsonFactory = new JSONFactoryImpl();

		JSONObject expectJSONObject = jsonFactory.createJSONObject(
			URLUtil.toString(_frontendTokenDefinitionJSONURL));

		for (FrontendTokenDefinitionImpl frontendTokenDefinitionImpl :
				frontendTokenDefinitionImpls) {

			JSONObject actualJSONObject =
				frontendTokenDefinitionImpl.getJSONObject(LocaleUtil.ENGLISH);

			Assert.assertEquals(
				expectJSONObject.toMap(), actualJSONObject.toMap());
		}
	}

	@Test
	public void testGetServletContextName() {
		FrontendTokenDefinitionRegistryImpl
			frontendTokenDefinitionRegistryImpl =
				new FrontendTokenDefinitionRegistryImpl();

		Bundle bundle = Mockito.mock(Bundle.class);

		Dictionary<String, String> headers = HashMapDictionaryBuilder.put(
			"Web-ContextPath", "/my-theme"
		).build();

		Mockito.when(
			bundle.getHeaders(Mockito.anyString())
		).thenReturn(
			headers
		);

		Assert.assertEquals(
			"my-theme",
			frontendTokenDefinitionRegistryImpl.getServletContextName(bundle));
	}

	@Test
	public void testGetThemeMapsWithNontheme() {
		FrontendTokenDefinitionRegistryImpl
			frontendTokenDefinitionRegistryImpl =
				new FrontendTokenDefinitionRegistryImpl();

		Bundle bundle = Mockito.mock(Bundle.class);

		List<Map<String, String>> themeMaps =
			frontendTokenDefinitionRegistryImpl.getThemeMaps(bundle);

		Assert.assertTrue(themeMaps.isEmpty());
	}

	@Test
	public void testGetThemeMapsWithoutServletContext() {
		FrontendTokenDefinitionRegistryImpl
			frontendTokenDefinitionRegistryImpl =
				new FrontendTokenDefinitionRegistryImpl();

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getEntry("WEB-INF/liferay-look-and-feel.xml")
		).thenReturn(
			_liferayLookAndFeelXMLURL
		);

		Mockito.when(
			bundle.getHeaders(Mockito.anyString())
		).thenReturn(
			new HashMapDictionary<>()
		);

		frontendTokenDefinitionRegistryImpl.portal = new PortalImpl();

		List<Map<String, String>> themeMaps =
			frontendTokenDefinitionRegistryImpl.getThemeMaps(bundle);

		Assert.assertEquals(themeMaps.toString(), 2, themeMaps.size());

		Map<String, String> themesData1 = themeMaps.get(0);

		Assert.assertEquals("classic", themesData1.get("id"));
		Assert.assertEquals("Classic", themesData1.get("name"));

		Map<String, String> themesData2 = themeMaps.get(1);

		Assert.assertEquals("modern", themesData2.get("id"));
		Assert.assertEquals("Modern", themesData2.get("name"));
	}

	@Test
	public void testGetThemeMapsWithServletContext() {
		FrontendTokenDefinitionRegistryImpl
			frontendTokenDefinitionRegistryImpl =
				new FrontendTokenDefinitionRegistryImpl();

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getEntry("WEB-INF/liferay-look-and-feel.xml")
		).thenReturn(
			_liferayLookAndFeelXMLURL
		);

		Dictionary<String, String> headers = HashMapDictionaryBuilder.put(
			"Web-ContextPath", "/two-themes"
		).build();

		Mockito.when(
			bundle.getHeaders(Mockito.anyString())
		).thenReturn(
			headers
		);

		frontendTokenDefinitionRegistryImpl.portal = new PortalImpl();

		List<Map<String, String>> themeMaps =
			frontendTokenDefinitionRegistryImpl.getThemeMaps(bundle);

		Assert.assertEquals(themeMaps.toString(), 2, themeMaps.size());

		Map<String, String> themesData1 = themeMaps.get(0);

		Assert.assertEquals("classic_WAR_twothemes", themesData1.get("id"));
		Assert.assertEquals("Classic", themesData1.get("name"));

		Map<String, String> themesData2 = themeMaps.get(1);

		Assert.assertEquals("modern_WAR_twothemes", themesData2.get("id"));
		Assert.assertEquals("Modern", themesData2.get("name"));
	}

	private void _withGlobalFrontendTokenDefinition(
			boolean featureFlagEnabled,
			UnsafeConsumer<FrontendTokenDefinitionRegistryImpl, Exception>
				unsafeConsumer)
		throws Exception {

		FrontendTokenDefinitionRegistryImpl
			frontendTokenDefinitionRegistryImpl =
				new FrontendTokenDefinitionRegistryImpl();

		frontendTokenDefinitionRegistryImpl.jsonFactory = new JSONFactoryImpl();
		frontendTokenDefinitionRegistryImpl.portal = new PortalImpl();

		ReflectionTestUtil.setFieldValue(
			frontendTokenDefinitionRegistryImpl, "_serviceTrackerMap",
			Mockito.mock(ServiceTrackerMap.class));

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getEntry("WEB-INF/frontend-token-definition.json")
		).thenReturn(
			_frontendTokenDefinitionJSONURL
		);

		Mockito.when(
			bundle.getEntry("WEB-INF/liferay-look-and-feel.xml")
		).thenReturn(
			null
		);

		Mockito.when(
			bundle.getSymbolicName()
		).thenReturn(
			"com.liferay.frontend.js.clay.web"
		);

		BundleTrackerCustomizer<List<FrontendTokenDefinitionImpl>>
			bundleTrackerCustomizer = ReflectionTestUtil.getFieldValue(
				frontendTokenDefinitionRegistryImpl,
				"_bundleTrackerCustomizer");

		bundleTrackerCustomizer.addingBundle(bundle, null);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(123L, "LPD-84497")
			).thenReturn(
				featureFlagEnabled
			);

			unsafeConsumer.accept(frontendTokenDefinitionRegistryImpl);
		}
	}

	private static final URL _frontendTokenDefinitionJSONURL =
		FrontendTokenDefinitionRegistryImplTest.class.getResource(
			"dependencies/frontend-token-definition.json");
	private static final URL _liferayLookAndFeelXMLURL =
		FrontendTokenDefinitionRegistryImplTest.class.getResource(
			"dependencies/liferay-look-and-feel.xml");

	private MockedStatic<ResourceBundleLoaderUtil>
		_resourceBundleLoaderUtilMockedStatic;

}