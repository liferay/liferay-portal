/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.document;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.connection.helper.IndexCreationHelper;
import com.liferay.portal.search.opensearch2.internal.indexing.LiferayOpenSearchIndexingFixtureFactory;
import com.liferay.portal.search.opensearch2.internal.indexing.OpenSearchIndexingFixture;
import com.liferay.portal.search.opensearch2.internal.settings.SettingsHelper;
import com.liferay.portal.search.opensearch2.internal.util.IndexUtil;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.PutMappingRequest;

/**
 * @author André de Oliveira
 */
public class GeoLocationPointFieldTest extends BaseIndexingTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCustomField() throws Exception {
		_assertGeoLocationPointField(_CUSTOM_FIELD);
	}

	@Test
	public void testDefaultField() throws Exception {
		_assertGeoLocationPointField(Field.GEO_LOCATION);
	}

	@Test
	public void testDefaultTemplate() throws Exception {
		_assertGeoLocationPointField(_CUSTOM_FIELD.concat("_geolocation"));
	}

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		OpenSearchIndexingFixture openSearchIndexingFixture =
			LiferayOpenSearchIndexingFixtureFactory.builder(
			).build();

		openSearchIndexingFixture.setIndexCreationHelper(
			new CustomFieldLiferayIndexCreationHelper(
				openSearchIndexingFixture.getOpenSearchConnectionManager()));

		return openSearchIndexingFixture;
	}

	private void _assertGeoLocationPointField(String fieldName) {
		double latitude = 33.99772698059678;
		double longitude = -117.814457193017;

		String expected = StringBundler.concat(
			"(", latitude, ",", longitude, ")");

		addDocument(
			DocumentCreationHelpers.singleGeoLocation(
				fieldName, latitude, longitude));

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.search();

				indexingTestHelper.verifyResponse(
					searchResponse -> DocumentsAssert.assertValues(
						searchResponse.getRequestString(),
						searchResponse.getDocuments(), fieldName,
						"[" + expected + "]"));
			});
	}

	private static final String _CUSTOM_FIELD = "customField";

	private static class CustomFieldLiferayIndexCreationHelper
		implements IndexCreationHelper {

		public CustomFieldLiferayIndexCreationHelper(
			OpenSearchConnectionManager openSearchConnectionManager) {

			_openSearchConnectionManager = openSearchConnectionManager;
		}

		@Override
		public void contribute(
			CreateIndexRequest.Builder createIndexRequestBuilder) {
		}

		@Override
		public void contributeIndexSettings(SettingsHelper settingsHelper) {
		}

		@Override
		public void whenIndexCreated(String indexName) {
			PutMappingRequest.Builder putMappingRequestBuilder =
				new PutMappingRequest.Builder();

			putMappingRequestBuilder.index(indexName);

			putMappingRequestBuilder.properties(
				IndexUtil.getPropertiesMap(
					JSONUtil.put(
						"properties",
						JSONUtil.put(
							_CUSTOM_FIELD,
							JSONUtil.put(
								"fields",
								JSONUtil.put(
									"geopoint",
									JSONUtil.put(
										"store", true
									).put(
										"type", "keyword"
									))
							).put(
								"store", "true"
							).put(
								"type", "geo_point"
							)))));

			OpenSearchClient openSearchClient =
				_openSearchConnectionManager.getOpenSearchClient();

			OpenSearchIndicesClient openSearchIndicesClient =
				openSearchClient.indices();

			try {
				openSearchIndicesClient.putMapping(
					putMappingRequestBuilder.build());
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private final OpenSearchConnectionManager _openSearchConnectionManager;

	}

}