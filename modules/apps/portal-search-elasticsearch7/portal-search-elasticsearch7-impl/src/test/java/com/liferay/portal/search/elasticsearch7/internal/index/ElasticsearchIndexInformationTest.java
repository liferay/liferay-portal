/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.test.util.AssertUtils;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

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

	private static ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;

	private ElasticsearchIndexInformation _elasticsearchIndexInformation;
	private IndexFactoryFixture _indexFactoryFixture;
	private IndexNameBuilder _indexNameBuilder;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}