/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.test.util.asset.renderer.factory.TestAssetRendererFactory;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Lourdes Fernández Besada
 */
public class EditAssetListDisplayContextTest {

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
		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		_themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		_portletRequest = Mockito.mock(PortletRequest.class);

		_setUpJSONFactoryUtil();
		_setUpLanguageUtil();
		_setUpPortalUtil();
	}

	@Test
	public void testGetAssetEntryTypesJSONArray() throws Exception {
		String className = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		String expectedLabel = RandomTestUtil.randomString();

		_setUpAssetRendererFactoryRegistryUtil(
			className, classNameId, expectedLabel);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"classNameIds", classNameId
		).put(
			"selectionStyle", "manual"
		).build();

		_setUpAssetListEntryLocalServiceUtil(
			StringPool.BLANK, className, unicodeProperties.toString());

		EditAssetListDisplayContext editAssetListDisplayContext =
			_getEditAssetListDisplayContext(unicodeProperties);

		JSONArray assetEntryTypesJSONArray =
			editAssetListDisplayContext.getAssetEntryTypesJSONArray();

		Assert.assertEquals(
			assetEntryTypesJSONArray.toString(), 1,
			assetEntryTypesJSONArray.length());

		JSONObject assetEntryTypeJSONObject =
			assetEntryTypesJSONArray.getJSONObject(0);

		Assert.assertEquals(
			classNameId, assetEntryTypeJSONObject.getLong("classNameId"));
		Assert.assertEquals(
			expectedLabel, assetEntryTypeJSONObject.getString("label"));
	}

	@Test
	public void testGetAssetEntryTypesJSONArrayNoAvailableClassNameId()
		throws Exception {

		long classNameId = RandomTestUtil.randomLong();

		_setUpAssetRendererFactoryRegistryUtil(null, classNameId, null);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"classNameIds", classNameId
		).put(
			"selectionStyle", "manual"
		).build();

		_setUpAssetListEntryLocalServiceUtil(
			StringPool.BLANK, RandomTestUtil.randomString(),
			unicodeProperties.toString());

		EditAssetListDisplayContext editAssetListDisplayContext =
			_getEditAssetListDisplayContext(unicodeProperties);

		JSONArray assetEntryTypesJSONArray =
			editAssetListDisplayContext.getAssetEntryTypesJSONArray();

		Assert.assertEquals(
			assetEntryTypesJSONArray.toString(), 0,
			assetEntryTypesJSONArray.length());
	}

	@Test
	public void testGetClassNameIdsDynamicSelection() {
		long classNameId = RandomTestUtil.randomLong();

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"anyAssetType", classNameId
		).put(
			"selectionStyle", "dynamic"
		).build();

		EditAssetListDisplayContext editAssetListDisplayContext =
			_getEditAssetListDisplayContext(unicodeProperties);

		Assert.assertArrayEquals(
			new long[] {classNameId},
			editAssetListDisplayContext.getClassNameIds(
				unicodeProperties,
				new long[] {
					RandomTestUtil.randomLong(), classNameId,
					RandomTestUtil.randomLong()
				}));
	}

	@Test
	public void testGetClassNameIdsDynamicSelectionNoAvailableClassNameId() {
		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"anyAssetType", RandomTestUtil.randomLong()
		).put(
			"selectionStyle", "dynamic"
		).build();

		EditAssetListDisplayContext editAssetListDisplayContext =
			_getEditAssetListDisplayContext(unicodeProperties);

		Assert.assertArrayEquals(
			new long[0],
			editAssetListDisplayContext.getClassNameIds(
				unicodeProperties,
				new long[] {
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomLong()
				}));
	}

	@Test
	public void testGetOrderByColumnsWhenLegacyModifiedDateIsSaved() {
		EditAssetListDisplayContext editAssetListDisplayContext =
			_getEditAssetListDisplayContext(
				UnicodePropertiesBuilder.put(
					"orderByColumn1", "modifiedDate"
				).put(
					"orderByColumn2", "modifiedDate"
				).build());

		Assert.assertEquals(
			Field.MODIFIED_DATE,
			editAssetListDisplayContext.getOrderByColumn1());
		Assert.assertEquals(
			Field.MODIFIED_DATE,
			editAssetListDisplayContext.getOrderByColumn2());
	}

	@Test
	public void testGetOrderByColumnsWhenUnset() {
		EditAssetListDisplayContext editAssetListDisplayContext =
			_getEditAssetListDisplayContext(new UnicodeProperties());

		Assert.assertEquals(
			Field.MODIFIED_DATE,
			editAssetListDisplayContext.getOrderByColumn1());
		Assert.assertEquals(
			"title", editAssetListDisplayContext.getOrderByColumn2());
	}

	@Test
	public void testGetVocabularyIdsWithMissingClassName() throws Exception {
		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		long missingClassNameId = RandomTestUtil.randomLong();

		Mockito.when(
			assetVocabulary.getSelectedClassNameIds()
		).thenReturn(
			new long[] {missingClassNameId}
		);

		Mockito.when(
			_portal.getClassName(missingClassNameId)
		).thenThrow(
			new RuntimeException()
		);

		long[] groupIds = {RandomTestUtil.randomLong()};

		Mockito.when(
			_portal.getCurrentAndAncestorSiteGroupIds(Mockito.any(long[].class))
		).thenReturn(
			groupIds
		);

		Mockito.when(
			_portal.getCurrentAndAncestorSiteGroupIds(
				Mockito.any(long[].class), Mockito.eq(true))
		).thenReturn(
			groupIds
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			Mockito.mock(Group.class)
		);

		try (MockedStatic<AssetVocabularyServiceUtil>
				assetVocabularyServiceUtilMockedStatic = Mockito.mockStatic(
					AssetVocabularyServiceUtil.class)) {

			assetVocabularyServiceUtilMockedStatic.when(
				() -> AssetVocabularyServiceUtil.getGroupsVocabularies(
					Mockito.any(long[].class))
			).thenReturn(
				ListUtil.fromArray(assetVocabulary)
			);

			EditAssetListDisplayContext editAssetListDisplayContext =
				_getEditAssetListDisplayContext(new UnicodeProperties());

			Assert.assertEquals(
				Collections.emptyList(),
				editAssetListDisplayContext.getVocabularyIds());
		}
	}

	private EditAssetListDisplayContext _getEditAssetListDisplayContext(
		UnicodeProperties unicodeProperties) {

		AssetRendererFactoryClassProvider assetRendererFactoryClassProvider =
			Mockito.mock(AssetRendererFactoryClassProvider.class);

		Mockito.when(
			assetRendererFactoryClassProvider.getClass(
				Mockito.any(AssetRendererFactory.class))
		).thenAnswer(
			(Answer<Class<? extends AssetRendererFactory<?>>>)
				invocationOnMock -> TestAssetRendererFactory.class
		);

		return new EditAssetListDisplayContext(
			assetRendererFactoryClassProvider,
			Mockito.mock(InfoSearchClassMapperRegistry.class),
			Mockito.mock(ItemSelector.class),
			Mockito.mock(ObjectDefinitionLocalService.class), _portletRequest,
			Mockito.mock(PortletResponse.class),
			Mockito.mock(SegmentsConfigurationProvider.class),
			unicodeProperties);
	}

	private void _setUpAssetListEntryLocalServiceUtil(
		String assetEntrySubtype, String assetEntryType, String typeSettings) {

		AssetListEntry assetListEntry = Mockito.mock(AssetListEntry.class);

		Mockito.when(
			assetListEntry.getAssetEntrySubtype()
		).thenReturn(
			assetEntrySubtype
		);

		Mockito.when(
			assetListEntry.getAssetEntryType()
		).thenReturn(
			assetEntryType
		);

		Mockito.when(
			assetListEntry.getTypeSettings(Mockito.anyLong())
		).thenReturn(
			typeSettings
		);

		AssetListEntryLocalService assetListEntryLocalService = Mockito.mock(
			AssetListEntryLocalService.class);

		Mockito.when(
			assetListEntryLocalService.fetchAssetListEntry(Mockito.anyLong())
		).thenReturn(
			assetListEntry
		);

		ReflectionTestUtil.setFieldValue(
			AssetListEntryLocalServiceUtil.class, "_serviceSnapshot",
			new Snapshot<AssetListEntryLocalService>(
				AssetListEntryLocalServiceUtil.class,
				AssetListEntryLocalService.class) {

				@Override
				public AssetListEntryLocalService get() {
					return assetListEntryLocalService;
				}

			});
	}

	private void _setUpAssetRendererFactoryRegistryUtil(
		String className, long classNameId, ClassTypeReader classTypeReader,
		boolean supportsClassTypes, String typeName) {

		AssetRendererFactory<?> assetRendererFactory = null;

		if (className != null) {
			assetRendererFactory = Mockito.mock(AssetRendererFactory.class);

			Mockito.when(
				assetRendererFactory.isActive(Mockito.anyLong())
			).thenReturn(
				true
			);

			Mockito.when(
				assetRendererFactory.getClassName()
			).thenReturn(
				className
			);

			Mockito.when(
				assetRendererFactory.getClassNameId()
			).thenReturn(
				classNameId
			);

			Mockito.when(
				assetRendererFactory.getClassTypeReader()
			).thenReturn(
				classTypeReader
			);

			Mockito.when(
				assetRendererFactory.getTypeName(LocaleUtil.US)
			).thenReturn(
				typeName
			);

			Mockito.when(
				assetRendererFactory.isSelectable()
			).thenReturn(
				true
			);

			Mockito.when(
				assetRendererFactory.isSupportsClassTypes()
			).thenReturn(
				supportsClassTypes
			);
		}

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() ->
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassNameId(classNameId)
		).thenReturn(
			assetRendererFactory
		);

		ArrayList<AssetRendererFactory<?>> assetRendererFactories =
			new ArrayList<>();

		assetRendererFactories.add(assetRendererFactory);

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() -> AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				Mockito.anyLong(), Mockito.eq(true))
		).thenReturn(
			assetRendererFactories
		);
	}

	private void _setUpAssetRendererFactoryRegistryUtil(
		String className, long classNameId, String typeName) {

		_setUpAssetRendererFactoryRegistryUtil(
			className, classNameId, null, false, typeName);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getHttpServletRequest(_portletRequest)
		).thenReturn(
			_httpServletRequest
		);

		portalUtil.setPortal(_portal);
	}

	private static final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);

	private HttpServletRequest _httpServletRequest;
	private Portal _portal;
	private PortletRequest _portletRequest;
	private ThemeDisplay _themeDisplay;

}