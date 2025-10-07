/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.document;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.internal.document.DocumentBuilderImpl;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import org.elasticsearch.common.Strings;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class ElasticsearchDocumentFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_documentFixture.setUp();

		_elasticsearchDocumentFactory = new ElasticsearchDocumentFactory();
	}

	@After
	public void tearDown() throws Exception {
		_documentFixture.tearDown();
	}

	@Test
	public void testArrayOfArrays() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.startArray(
				"alpha"
			).startArray(
			).value(
				"one"
			).value(
				"two"
			).value(
				"three"
			).endArray(
			).startArray(
			).value(
				"four"
			).value(
				"five"
			).value(
				"six"
			).endArray(
			).endArray(),
			documentBuilder -> documentBuilder.setValues(
				"alpha",
				Arrays.asList(
					Arrays.asList("one", "two", "three"),
					Arrays.asList("four", "five", "six"))));
	}

	@Test
	public void testArrayOfObjects() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.field(
				"group", "fans"
			).field(
				"user",
				Arrays.asList(
					HashMapBuilder.<String, Object>put(
						"first", "John"
					).put(
						"last", "Smith"
					).build(),
					HashMapBuilder.<String, Object>put(
						"first", "Alice"
					).put(
						"last", "White"
					).build())
			),
			documentBuilder -> documentBuilder.setString(
				"group", "fans"
			).setValue(
				"user",
				Arrays.asList(
					HashMapBuilder.<String, Object>put(
						"first", "John"
					).put(
						"last", "Smith"
					).build(),
					HashMapBuilder.<String, Object>put(
						"first", "Alice"
					).put(
						"last", "White"
					).build())
			));
	}

	@Test
	public void testInnerObject() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.startObject(
				"alpha"
			).field(
				"position", "1"
			).endObject(),
			documentBuilder -> documentBuilder.setValue(
				"alpha", Collections.singletonMap("position", "1")));
	}

	@Test
	public void testMultipleInnerObjects() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.field(
				"region", "US"
			).field(
				"manager",
				HashMapBuilder.<String, Object>put(
					"age", 30
				).put(
					"name",
					HashMapBuilder.put(
						"first", "John"
					).put(
						"last", "Smith"
					).build()
				).build()
			),
			documentBuilder -> documentBuilder.setString(
				"region", "US"
			).setValue(
				"manager",
				HashMapBuilder.<String, Object>put(
					"age", 30
				).put(
					"name",
					HashMapBuilder.put(
						"first", "John"
					).put(
						"last", "Smith"
					).build()
				).build()
			));
	}

	@Test
	public void testMultipleValuesSetStrings() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.array(
				"alpha", new String[] {"one", "two", "three"}),
			documentBuilder -> documentBuilder.setStrings(
				"alpha", "one", "two", "three"));
	}

	@Test
	public void testMultipleValuesSetValue() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.array(
				"alpha", new String[] {"one", "two", "three"}),
			documentBuilder -> documentBuilder.setValue(
				"alpha", Arrays.asList("one", "two", "three")));
	}

	@Test
	public void testMultipleValuesSetValues() throws IOException {
		_assertDocument(
			xContentBuilder -> xContentBuilder.array(
				"alpha", new String[] {"one", "two", "three"}),
			documentBuilder -> documentBuilder.setValues(
				"alpha", Arrays.asList("one", "two", "three")));
	}

	@Test
	public void testNull() throws Exception {
		_assertDocumentSameAsLegacy(null, "{}");
	}

	@Test
	public void testNullValue() throws Exception {
		_assertDocument(
			"{\"field\":[null]}",
			builder().setValue(_FIELD, Collections.singleton(null)));
	}

	@Test
	public void testNullValues() throws Exception {
		_assertDocument(
			"{\"field\":[null,null]}",
			builder().setValues(_FIELD, Arrays.asList(null, null)));
	}

	@Test
	public void testSpaces() throws Exception {
		_assertDocument(StringPool.SPACE, "{\"field\":\" \"}");

		_assertDocument(StringPool.THREE_SPACES, "{\"field\":\"   \"}");
	}

	@Test
	public void testSpacesLegacy() throws Exception {
		assertDocumentLegacy(StringPool.SPACE, "{\"field\":\"\"}");

		assertDocumentLegacy(StringPool.THREE_SPACES, "{\"field\":\"\"}");
	}

	@Test
	public void testStringBlank() throws Exception {
		_assertDocumentSameAsLegacy(StringPool.BLANK, "{\"field\":\"\"}");
	}

	@Test
	public void testStringNull() throws Exception {
		_assertDocumentSameAsLegacy(StringPool.NULL, "{\"field\":\"null\"}");
	}

	public interface XContentBuilderConsumer {

		public void accept(XContentBuilder xContentBuilder) throws IOException;

	}

	@SuppressWarnings("deprecation")
	protected void assertDocumentLegacy(String value, String json) {
		com.liferay.portal.kernel.search.Document document = new DocumentImpl();

		document.addText(_FIELD, new String[] {value});

		Assert.assertEquals(
			json,
			_elasticsearchDocumentFactory.getElasticsearchDocument(document));
	}

	protected DocumentBuilder builder() {
		return new DocumentBuilderImpl();
	}

	private void _assertDocument(
		String expected, DocumentBuilder documentBuilder) {

		Assert.assertEquals(
			expected,
			Strings.toString(
				_elasticsearchDocumentFactory.getElasticsearchDocument(
					documentBuilder.build())));
	}

	private void _assertDocument(String value, String json) {
		_assertDocument(
			json, builder().setStrings(_FIELD, new String[] {value}));
	}

	private void _assertDocument(
		XContentBuilderConsumer expectedXContentBuilderConsumer,
		Consumer<DocumentBuilder> actualDocumentBuilderConsumer) {

		XContentBuilder expectedXContentBuilder = _createXContentBuilder(
			expectedXContentBuilderConsumer);

		XContentBuilder actualXContentBuilder =
			_elasticsearchDocumentFactory.getElasticsearchDocument(
				_buildDocument(actualDocumentBuilderConsumer));

		Assert.assertEquals(
			Strings.toString(expectedXContentBuilder),
			Strings.toString(actualXContentBuilder));
	}

	private void _assertDocumentSameAsLegacy(String value, String json) {
		_assertDocument(value, json);
		assertDocumentLegacy(value, json);
	}

	private Document _buildDocument(
		Consumer<DocumentBuilder> documentBuilderConsumer) {

		DocumentBuilder documentBuilder = builder();

		documentBuilderConsumer.accept(documentBuilder);

		return documentBuilder.build();
	}

	private XContentBuilder _createXContentBuilder(
		XContentBuilderConsumer xContentBuilderConsumer) {

		try {
			XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();

			xContentBuilder.startObject();

			xContentBuilderConsumer.accept(xContentBuilder);

			return xContentBuilder.endObject();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final String _FIELD = "field";

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private ElasticsearchDocumentFactory _elasticsearchDocumentFactory;

}