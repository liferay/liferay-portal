/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.PutAliasRequest;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch8.internal.util.ResourceUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.test.util.AssertUtils;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 */
public class ElasticsearchIndexInformationTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				ElasticsearchIndexInformationTest.class.getSimpleName()
			).build();

		elasticsearchConnectionFixture.createNode();

		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchConnectionFixture.destroyNode();
	}

	@Before
	public void setUp() throws Exception {
		_indexFactoryFixture = _createIndexFactoryFixture(
			_elasticsearchConnectionFixture);

		_indexNameBuilder = _createIndexNameBuilder();

		_elasticsearchIndexInformation = _createElasticsearchIndexInformation(
			_elasticsearchConnectionFixture, _indexNameBuilder);
	}

	@After
	public void tearDown() {
		_indexFactoryFixture.deleteIndices();

		_indexFactoryFixture.tearDown();
	}

	@Test
	public void testGetCompanyIndexName() throws Exception {
		_indexFactoryFixture.createIndices();

		long companyId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			_indexNameBuilder.getIndexName(companyId),
			_elasticsearchIndexInformation.getCompanyIndexName(companyId));
	}

	@Test
	public void testGetFieldMappings() throws Exception {
		_indexFactoryFixture.createIndices();

		AssertUtils.assertEquals(
			"", _loadJSONObject(testName.getMethodName()),
			_jsonFactory.createJSONObject(
				_elasticsearchIndexInformation.getFieldMappings(
					_indexFactoryFixture.getIndexName())));
	}

	@Test
	public void testGetFieldMappingsWithAlias() throws Exception {
		_indexFactoryFixture.createIndices();

		String indexName = _indexFactoryFixture.getIndexName();

		String aliasName = indexName + "_alias";

		_putAlias(aliasName, indexName);

		JSONObject aliasJSONObject = _jsonFactory.createJSONObject(
			_elasticsearchIndexInformation.getFieldMappings(aliasName));

		Assert.assertEquals(
			Collections.singleton(aliasName), aliasJSONObject.keySet());

		JSONObject indexJSONObject = _jsonFactory.createJSONObject(
			_elasticsearchIndexInformation.getFieldMappings(indexName));

		AssertUtils.assertEquals(
			"", indexJSONObject.getJSONObject(indexName),
			aliasJSONObject.getJSONObject(aliasName));
	}

	@Test
	public void testGetFieldMappingsWithIndexName() throws Exception {
		_indexFactoryFixture.createIndices();

		String indexName = _indexFactoryFixture.getIndexName();

		_putAlias(indexName + "_alias", indexName);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_elasticsearchIndexInformation.getFieldMappings(indexName));

		Assert.assertEquals(
			Collections.singleton(indexName), jsonObject.keySet());
	}

	@Test
	public void testGetFieldMappingsWithWildcard() throws Exception {
		_indexFactoryFixture.createIndices();

		String indexName = _indexFactoryFixture.getIndexName();

		String aliasName = indexName + "_alias";

		_putAlias(aliasName, indexName);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_elasticsearchIndexInformation.getFieldMappings(StringPool.STAR));

		Assert.assertFalse(jsonObject.has(aliasName));
		Assert.assertTrue(jsonObject.has(indexName));
	}

	@Test
	public void testGetIndexNames() throws Exception {
		_indexFactoryFixture.createIndices();

		AssertUtils.assertEquals(
			"", Arrays.asList(_indexFactoryFixture.getIndexName()),
			Arrays.asList(_elasticsearchIndexInformation.getIndexNames()));
	}

	@Rule
	public TestName testName = new TestName();

	private ElasticsearchIndexInformation _createElasticsearchIndexInformation(
		ElasticsearchClientResolver elasticsearchClientResolver,
		IndexNameBuilder indexNameBuilder) {

		ElasticsearchIndexInformation elasticsearchIndexInformation =
			new ElasticsearchIndexInformation();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexInformation, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexInformation, "_indexNameBuilder",
			indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexInformation, "_jsonFactory",
			new JSONFactoryImpl());

		return elasticsearchIndexInformation;
	}

	private IndexFactoryFixture _createIndexFactoryFixture(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		return new IndexFactoryFixture(
			elasticsearchClientResolver, testName.getMethodName());
	}

	private IndexNameBuilder _createIndexNameBuilder() {
		IndexNameBuilder indexNameBuilder = Mockito.mock(
			IndexNameBuilder.class);

		Mockito.when(
			indexNameBuilder.getIndexName(Mockito.anyLong())
		).then(
			invocation ->
				"test-" + String.valueOf(invocation.getArgument(0, Long.class))
		);

		return indexNameBuilder;
	}

	private JSONObject _loadJSONObject(String suffix) throws Exception {
		String json = ResourceUtil.getResourceAsString(
			getClass(),
			"ElasticsearchIndexInformationTest-" + suffix + ".json");

		return _jsonFactory.createJSONObject(json);
	}

	private void _putAlias(String aliasName, String indexName)
		throws Exception {

		ElasticsearchClient elasticsearchClient =
			_elasticsearchConnectionFixture.getElasticsearchClient();

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		elasticsearchIndicesClient.putAlias(
			PutAliasRequest.of(
				putAliasRequest -> putAliasRequest.index(
					indexName
				).name(
					aliasName
				)));
	}

	private static ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;

	private ElasticsearchIndexInformation _elasticsearchIndexInformation;
	private IndexFactoryFixture _indexFactoryFixture;
	private IndexNameBuilder _indexNameBuilder;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}