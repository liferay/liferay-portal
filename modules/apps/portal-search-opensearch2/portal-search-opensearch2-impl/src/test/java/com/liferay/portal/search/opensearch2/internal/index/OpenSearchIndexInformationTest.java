/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.util.ResourceUtil;
import com.liferay.portal.search.test.util.AssertUtils;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 * @author Petteri Karttunen
 */
public class OpenSearchIndexInformationTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_indexFactoryFixture = _createIndexFactoryFixture(
			openSearchConnectionManager);

		_indexNameBuilder = _createIndexNameBuilder();

		_openSearchIndexInformation = _createOpenSearchIndexInformation(
			_indexNameBuilder, openSearchConnectionManager);
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
			_openSearchIndexInformation.getCompanyIndexName(companyId));
	}

	@Test
	public void testGetFieldMappings() throws Exception {
		_indexFactoryFixture.createIndices();

		JSONObject fieldMappingsJSONObject = _jsonFactory.createJSONObject(
			_openSearchIndexInformation.getFieldMappings(
				_indexFactoryFixture.getIndexName()));

		AssertUtils.assertEquals(
			"", _loadJSONObject(testName.getMethodName()),
			fieldMappingsJSONObject);
	}

	@Test
	public void testGetIndexName() throws Exception {
		_indexFactoryFixture.createIndices();

		Assert.assertTrue(
			ArrayUtil.contains(
				_openSearchIndexInformation.getIndexNames(),
				_indexFactoryFixture.getIndexName()));
	}

	@Rule
	public TestName testName = new TestName();

	private IndexFactoryFixture _createIndexFactoryFixture(
		OpenSearchConnectionManager openSearchConnectionManager) {

		return new IndexFactoryFixture(
			testName.getMethodName(), openSearchConnectionManager);
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

	private OpenSearchIndexInformation _createOpenSearchIndexInformation(
		IndexNameBuilder indexNameBuilder,
		OpenSearchConnectionManager openSearchConnectionManager) {

		OpenSearchIndexInformation openSearchIndexInformation =
			new OpenSearchIndexInformation();

		ReflectionTestUtil.setFieldValue(
			openSearchIndexInformation, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexInformation, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			openSearchIndexInformation, "_openSearchConnectionManager",
			openSearchConnectionManager);

		return openSearchIndexInformation;
	}

	private JSONObject _loadJSONObject(String suffix) throws Exception {
		String json = ResourceUtil.getResourceAsString(
			getClass(), "OpenSearchIndexInformationTest-" + suffix + ".json");

		return _jsonFactory.createJSONObject(json);
	}

	private IndexFactoryFixture _indexFactoryFixture;
	private IndexNameBuilder _indexNameBuilder;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private OpenSearchIndexInformation _openSearchIndexInformation;

}