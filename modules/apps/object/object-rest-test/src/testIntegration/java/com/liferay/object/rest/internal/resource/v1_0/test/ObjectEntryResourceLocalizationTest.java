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
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
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

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition4().getRESTContextPath() + "/?search=textEng",
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition4().getRESTContextPath() + "/?search=textEng",
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build(),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition4().getRESTContextPath() + "/?search=textEsp",
			HashMapBuilder.put(
				"Accept-Language", "es-ES"
			).build(),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());
	}

	@Test
	public void testGetObjectEntryTranslation() throws Exception {
		_testGetObjectEntryTranslation(
			StringBundler.concat(
				getObjectDefinition4().getRESTContextPath(), StringPool.SLASH,
				getObjectEntry5().getObjectEntryId(),
				"/translations?sourceLanguageId=en_US",
				"&targetLanguageIds=es_ES"),
			null);
	}

	@Test
	public void testGetObjectEntryTranslationLanguage() throws Exception {
		_testGetObjectEntryTranslation(
			StringBundler.concat(
				getObjectDefinition4().getRESTContextPath(), StringPool.SLASH,
				getObjectEntry5().getObjectEntryId(),
				"/translations/en_US?targetLanguageId=es_ES"),
			HashMapBuilder.put(
				HttpHeaders.ACCEPT, "application/xliff+xml"
			).build());
	}

	@Test
	public void testGetObjectEntryWithLocalizedObjectField() throws Exception {

		// "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEsp"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEsp</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEsp"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEsp"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				HashMapBuilder.put(
					"Accept-Language", "es-ES"
				).build(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Empty "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEng</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEng"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				HashMapBuilder.put(
					"Accept-Language", ""
				).build(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Nonexistent "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEng</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEng"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				HashMapBuilder.put(
					"Accept-Language", "de-DE"
				).build(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Without "Accept-Language" header

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "longTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>richTextEng</p>"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "RawText", "richTextEng"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "textEng"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition4().getRESTContextPath() + StringPool.SLASH +
					getObjectEntry5().getObjectEntryId(),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@FeatureFlag("LPD-11388")
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
		File tempFile = FileUtil.createTempFile();

		File parentFile = tempFile.getParentFile();

		return ArrayUtil.getLength(parentFile.listFiles());
	}

	private void _testGetObjectEntryTranslation(
			String endpoint, Map<String, String> headers)
		throws Exception {

		int tempFilesCount = _getTempFilesCount();

		try (InputStream inputStream = HTTPTestUtil.invokeToInputStream(
				null, endpoint, headers, Http.Method.GET)) {

			Assert.assertNotNull(inputStream);

			try (ZipInputStream zipInputStream = new ZipInputStream(
					inputStream)) {

				zipInputStream.getNextEntry();

				Assert.assertNotNull(StringUtil.read(zipInputStream));
			}
		}

		Assert.assertEquals(tempFilesCount, _getTempFilesCount());
	}

}