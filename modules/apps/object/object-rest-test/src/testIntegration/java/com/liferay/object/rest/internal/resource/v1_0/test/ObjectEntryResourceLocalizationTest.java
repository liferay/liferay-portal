/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.map.test.util.GeocoderTestUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.LocationObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.ws.rs.core.HttpHeaders;

import java.io.File;
import java.io.InputStream;

import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceLocalizationTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetObjectEntriesPageWithLocalizedObjectField()
		throws Exception {

		_testGetObjectEntriesPageWithLocalizedObjectField(
			0,
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build(),
			"textEng");
		_testGetObjectEntriesPageWithLocalizedObjectField(
			1,
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build(),
			"textEsp");
		_testGetObjectEntriesPageWithLocalizedObjectField(1, null, "textEng");
	}

	@Test
	public void testGetObjectEntryTranslation() throws Exception {
		ObjectDefinition objectDefinition = getObjectDefinition4();
		ObjectEntry objectEntry = getLocalizedObjectEntry();

		_testGetObjectEntryTranslation(
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/translations?sourceLanguageId=en_US&targetLanguageIds=es_ES"),
			null);
	}

	@Test
	public void testGetObjectEntryTranslationLanguage() throws Exception {
		ObjectDefinition objectDefinition = getObjectDefinition4();
		ObjectEntry objectEntry = getLocalizedObjectEntry();

		_testGetObjectEntryTranslation(
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/translations/en_US?targetLanguageId=es_ES"),
			HashMapBuilder.put(
				HttpHeaders.ACCEPT, "application/xliff+xml"
			).build());
	}

	@Test
	public void testGetObjectEntryWithLocalizedObjectField() throws Exception {
		_testGetObjectEntryWithLocalizedObjectField(
			"Eng",
			HashMapBuilder.put(
				"Accept-Language", ""
			).build());
		_testGetObjectEntryWithLocalizedObjectField(
			"Eng",
			HashMapBuilder.put(
				"Accept-Language", "de-DE"
			).build());
		_testGetObjectEntryWithLocalizedObjectField("Eng", null);
		_testGetObjectEntryWithLocalizedObjectField(
			"Esp",
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build());
	}

	@Test
	public void testPostCustomObjectEntryWithLocalizedLocationObjectField()
		throws Exception {

		GeocoderResult geocoderResult = new GeocoderResult(
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomDouble());

		try (SafeCloseable safeCloseable =
				GeocoderTestUtil.swapWithSafeCloseable(
					geocoderResult,
					objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_LOCATION))) {

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						new LocationObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).localized(
							true
						).name(
							"location"
						).build()));

			String address = RandomTestUtil.randomString();
			double latitude = RandomTestUtil.randomDouble();
			double longitude = RandomTestUtil.randomDouble();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"location_i18n",
					JSONUtil.put(
						"en_US", JSONUtil.put("address", address)
					).put(
						"pt_BR",
						JSONUtil.put(
							"coordinates",
							JSONUtil.put(
								"latitude", latitude
							).put(
								"longitude", longitude
							))
					)
				).toString(),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			JSONObject i18nJSONObject = jsonObject.getJSONObject(
				"location_i18n");

			assertLocationJSONObject(
				address, geocoderResult.getLatitude(),
				geocoderResult.getLongitude(),
				i18nJSONObject.getJSONObject("en_US"));
			assertLocationJSONObject(
				geocoderResult.getAddress(), latitude, longitude,
				i18nJSONObject.getJSONObject("pt_BR"));
		}
	}

	private int _getTempFilesCount() {
		File tempDir = new File(SystemProperties.get(SystemProperties.TMP_DIR));

		return ArrayUtil.getLength(tempDir.listFiles());
	}

	private void _testGetObjectEntriesPageWithLocalizedObjectField(
			int expectedItemsCount, Map<String, String> headers, String search)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition4();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath() + "/?search=" + search,
			headers, Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(expectedItemsCount, itemsJSONArray.length());
	}

	private void _testGetObjectEntryTranslation(
			String endpoint, Map<String, String> headers)
		throws Exception {

		int tempFilesCount = _getTempFilesCount();

		try (InputStream inputStream = HTTPTestUtil.invokeToInputStream(
				null, endpoint, headers, Http.Method.GET);

			ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

			zipInputStream.getNextEntry();

			Assert.assertNotNull(StringUtil.read(zipInputStream));
		}

		Assert.assertEquals(tempFilesCount, _getTempFilesCount());
	}

	private void _testGetObjectEntryWithLocalizedObjectField(
			String expectedValueSuffix, Map<String, String> headers)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition4();
		ObjectEntry objectEntry = getLocalizedObjectEntry();

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT,
				"longText" + expectedValueSuffix
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT,
				"<p>richText" + expectedValueSuffix + "</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText",
				"richText" + expectedValueSuffix
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "text" + expectedValueSuffix
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				objectDefinition.getRESTContextPath() + StringPool.SLASH +
					objectEntry.getObjectEntryId(),
				headers, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

}