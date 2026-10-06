/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.MatchAllQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class ExpressionConvertTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_entryClassName = RandomTestUtil.randomString();

		_addDocument("n45alpha9omega");
		_addDocument("n45alpha*omega");
		_addDocument("n45controlxyz");
	}

	@After
	public void tearDown() throws Exception {
		for (String uid : _uids) {
			IndexWriterHelperUtil.deleteDocument(
				TestPropsValues.getCompanyId(), uid, true);
		}
	}

	@Test
	public void testContainsWithQuestionMark() throws Exception {
		Assert.assertEquals(
			Collections.emptyList(), _search("contains(title,'alpha?omega')"));
	}

	@Test
	public void testContainsWithStar() throws Exception {
		Assert.assertEquals(
			Collections.singletonList("n45alpha*omega"),
			_search("contains(title,'alpha*omega')"));
	}

	@Test
	public void testStartsWithLiteral() throws Exception {
		Assert.assertEquals(
			Collections.singletonList("n45alpha9omega"),
			_search("startswith(title,'n45alpha9omega')"));
	}

	@Test
	public void testStartsWithQuestionMark() throws Exception {
		Assert.assertEquals(
			Collections.emptyList(),
			_search("startswith(title,'n45alpha?omega')"));
	}

	@Test
	public void testStartsWithStar() throws Exception {
		Assert.assertEquals(
			Collections.singletonList("n45alpha*omega"),
			_search("startswith(title,'n45alpha*omega')"));
	}

	private void _addDocument(String title) throws Exception {
		Document document = new DocumentImpl();

		document.addUID(_entryClassName, RandomTestUtil.randomString());

		document.addKeyword(_TITLE_FIELD_NAME, title);
		document.addKeyword(Field.COMPANY_ID, TestPropsValues.getCompanyId());
		document.addKeyword(Field.ENTRY_CLASS_NAME, _entryClassName);
		document.addKeyword(
			Field.getSortableFieldName(_TITLE_FIELD_NAME),
			StringUtil.toLowerCase(title));

		IndexWriterHelperUtil.addDocument(
			TestPropsValues.getCompanyId(), document, true);

		_uids.add(document.getUID());
	}

	private List<String> _search(String filterString) throws Exception {
		List<String> titles = new ArrayList<>();

		FilterParser filterParser = _filterParserProvider.provide(_entityModel);

		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(
			new TermFilter(Field.ENTRY_CLASS_NAME, _entryClassName),
			BooleanClauseOccur.MUST);
		booleanFilter.add(
			_expressionConvert.convert(
				filterParser.parse(filterString), LocaleUtil.getDefault(),
				_entityModel),
			BooleanClauseOccur.MUST);

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(new MatchAllQuery(), BooleanClauseOccur.MUST);

		booleanQuery.setPreBooleanFilter(booleanFilter);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());

		Hits hits = IndexSearcherHelperUtil.search(searchContext, booleanQuery);

		for (Document document : hits.getDocs()) {
			titles.add(document.get(_TITLE_FIELD_NAME));
		}

		return titles;
	}

	private static final String _TITLE_FIELD_NAME = "title";

	private static final EntityModel _entityModel = new EntityModel() {

		@Override
		public Map<String, EntityField> getEntityFieldsMap() {
			return Collections.singletonMap(
				_TITLE_FIELD_NAME,
				new StringEntityField(
					_TITLE_FIELD_NAME,
					locale -> Field.getSortableFieldName(_TITLE_FIELD_NAME)));
		}

		@Override
		public String getName() {
			return "Test";
		}

	};

	private String _entryClassName;

	@Inject(
		filter = "result.class.name=com.liferay.portal.kernel.search.filter.Filter"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Inject
	private FilterParserProvider _filterParserProvider;

	private final List<String> _uids = new ArrayList<>();

}