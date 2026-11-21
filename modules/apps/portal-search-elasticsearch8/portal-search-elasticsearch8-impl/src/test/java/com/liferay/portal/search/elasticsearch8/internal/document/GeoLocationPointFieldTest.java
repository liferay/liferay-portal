/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.connection.helper.IndexCreationHelper;
import com.liferay.portal.search.elasticsearch8.internal.indexing.ElasticsearchIndexingFixture;
import com.liferay.portal.search.elasticsearch8.internal.indexing.LiferayElasticsearchIndexingFixtureFactory;
import com.liferay.portal.search.elasticsearch8.internal.settings.SettingsHelper;
import com.liferay.portal.search.elasticsearch8.internal.util.IndexUtil;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class GeoLocationPointFieldTest extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

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
		ElasticsearchIndexingFixture elasticsearchIndexingFixture =
			LiferayElasticsearchIndexingFixtureFactory.builder(
			).build();

		elasticsearchIndexingFixture.setIndexCreationHelper(
			new CustomFieldLiferayIndexCreationHelper(
				elasticsearchIndexingFixture.getElasticsearchClientResolver()));

		return elasticsearchIndexingFixture;
	}

	private void _assertGeoLocationPointField(String fieldName) {
		double latitude = 33.99772698059678;
		double longitude = -117.814457193017;

		String expected = "(33.99772698059678,-117.814457193017)";

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
			ElasticsearchClientResolver elasticsearchClientResolver) {

			_elasticsearchClientResolver = elasticsearchClientResolver;
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

			ElasticsearchClient elasticsearchClient =
				_elasticsearchClientResolver.getElasticsearchClient();

			ElasticsearchIndicesClient elasticsearchIndicesClient =
				elasticsearchClient.indices();

			try {
				elasticsearchIndicesClient.putMapping(
					putMappingRequestBuilder.build());
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private final ElasticsearchClientResolver _elasticsearchClientResolver;

	}

}