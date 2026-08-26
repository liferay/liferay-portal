/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.query.contributor;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.search.NestedQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Nícolas Moura
 * @author Yuri Monteiro
 */
public class ObjectEntryKeywordQueryContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());

		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	@Test
	public void testContributeWithAssigneeObjectField() throws Exception {
		ObjectDefinition objectDefinition = _mockObjectDefinition();

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		ObjectField assigneeObjectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE,
			ObjectFieldConstants.DB_TYPE_LONG, RandomTestUtil.randomString());

		Mockito.when(
			objectFieldBag.getNestedIndexedObjectFields()
		).thenReturn(
			Arrays.asList(assigneeObjectField)
		);

		ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(
			Query.class);

		BooleanQuery booleanQuery = _mockBooleanQuery(argumentCaptor);

		String token = RandomTestUtil.randomString();

		ObjectEntryKeywordQueryContributor objectEntryKeywordQueryContributor =
			_createObjectEntryKeywordQueryContributor(objectDefinition);

		objectEntryKeywordQueryContributor.contribute(
			token, booleanQuery, _mockKeywordQueryContributorHelper());

		List<Query> queries = argumentCaptor.getAllValues();

		Assert.assertEquals(1, _countNestedQueries(queries));

		List<Query> assigneeQueries = _getAssigneeBooleanQueryClauses(queries);

		Assert.assertEquals(
			assigneeQueries.toString(), 2, assigneeQueries.size());

		MatchQuery matchQuery = null;
		TermQuery termQuery = null;

		for (Query assigneeQuery : assigneeQueries) {
			if (assigneeQuery instanceof MatchQuery) {
				matchQuery = (MatchQuery)assigneeQuery;
			}
			else if (assigneeQuery instanceof TermQuery) {
				termQuery = (TermQuery)assigneeQuery;
			}
		}

		Assert.assertNotNull(termQuery);

		QueryTerm queryTerm = termQuery.getQueryTerm();

		Assert.assertEquals(
			"nestedFieldArray.value_keyword_lowercase", queryTerm.getField());
		Assert.assertEquals(
			StringUtil.toLowerCase(token), queryTerm.getValue());

		Assert.assertNotNull(matchQuery);
		Assert.assertEquals(
			"nestedFieldArray.value_text", matchQuery.getField());
		Assert.assertEquals(token, matchQuery.getValue());
	}

	@Test
	public void testContributeWithNestedIndexedObjectField() throws Exception {
		ObjectDefinition objectDefinition = _mockObjectDefinition();

		_mockObjectFields(objectDefinition.getObjectFieldBag());

		ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(
			Query.class);

		BooleanQuery booleanQuery = _mockBooleanQuery(argumentCaptor);

		ObjectEntryKeywordQueryContributor objectEntryKeywordQueryContributor =
			_createObjectEntryKeywordQueryContributor(objectDefinition);

		objectEntryKeywordQueryContributor.contribute(
			RandomTestUtil.randomString(), booleanQuery,
			_mockKeywordQueryContributorHelper());

		List<Query> queries = argumentCaptor.getAllValues();

		Assert.assertEquals(1, _countNestedQueries(queries));
	}

	@Test
	public void testContributeWithNonlocalizedField() throws Exception {
		ObjectDefinition objectDefinition = _mockObjectDefinition();

		Mockito.when(
			objectDefinition.getDefaultLanguageId()
		).thenReturn(
			"en_US"
		);

		_mockObjectFields(objectDefinition.getObjectFieldBag());

		ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(
			Query.class);

		BooleanQuery booleanQuery = _mockBooleanQuery(argumentCaptor);

		ObjectEntryKeywordQueryContributor objectEntryKeywordQueryContributor =
			_createObjectEntryKeywordQueryContributor(objectDefinition);

		objectEntryKeywordQueryContributor.contribute(
			RandomTestUtil.randomString(), booleanQuery,
			_mockKeywordQueryContributorHelper(LocaleUtil.SPAIN));

		Set<String> matchQueryFields = new HashSet<>();

		for (Query query : argumentCaptor.getAllValues()) {
			if (query instanceof NestedQuery) {
				NestedQuery nestedQuery = (NestedQuery)query;

				_collectMatchQueryFields(
					matchQueryFields, nestedQuery.getQuery());
			}
		}

		String spainLocalizedFieldName = Field.getLocalizedName(
			LocaleUtil.SPAIN, "nestedFieldArray.value");

		Assert.assertTrue(
			StringBundler.concat(
				"Expected ", matchQueryFields, " to contain ",
				spainLocalizedFieldName),
			matchQueryFields.contains(spainLocalizedFieldName));

		String usLocalizedFieldName = Field.getLocalizedName(
			LocaleUtil.US, "nestedFieldArray.value");

		Assert.assertTrue(
			StringBundler.concat(
				"Expected ", matchQueryFields, " to contain ",
				usLocalizedFieldName),
			matchQueryFields.contains(usLocalizedFieldName));

		Assert.assertFalse(
			StringBundler.concat(
				"Expected ", matchQueryFields, " not to contain ",
				"nestedFieldArray.value_text"),
			matchQueryFields.contains("nestedFieldArray.value_text"));
	}

	private SearchContext _buildSearchContext(Locale locale) {
		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(false);
		searchContext.setAttribute("searchByObjectView", Boolean.FALSE);
		searchContext.setLocale(locale);

		searchContext.getQueryConfig();

		return searchContext;
	}

	private void _collectMatchQueryFields(
		Set<String> matchQueryFields, Query query) {

		if (query instanceof MatchQuery) {
			MatchQuery matchQuery = (MatchQuery)query;

			matchQueryFields.add(matchQuery.getField());
		}
		else if (query instanceof BooleanQuery) {
			BooleanQuery booleanQuery = (BooleanQuery)query;

			for (BooleanClause<Query> booleanClause : booleanQuery.clauses()) {
				_collectMatchQueryFields(
					matchQueryFields, booleanClause.getClause());
			}
		}
	}

	private int _countNestedQueries(List<Query> queries) {
		int count = 0;

		for (Query query : queries) {
			if (query instanceof NestedQuery) {
				count++;
			}
		}

		return count;
	}

	private ObjectEntryKeywordQueryContributor
		_createObjectEntryKeywordQueryContributor(
			ObjectDefinition objectDefinition) {

		SearchLocalizationHelper searchLocalizationHelper = Mockito.mock(
			SearchLocalizationHelper.class);

		Mockito.when(
			searchLocalizationHelper.getLocalizedFieldNames(
				Mockito.any(String[].class), Mockito.any())
		).thenReturn(
			new String[] {
				Field.getLocalizedName(
					LocaleUtil.SPAIN, "nestedFieldArray.value"),
				Field.getLocalizedName(LocaleUtil.US, "nestedFieldArray.value")
			}
		);

		return new ObjectEntryKeywordQueryContributor(
			objectDefinition, Mockito.mock(ObjectFieldLocalService.class),
			Mockito.mock(ObjectViewLocalService.class),
			searchLocalizationHelper);
	}

	private List<Query> _getAssigneeBooleanQueryClauses(List<Query> queries) {
		for (Query query : queries) {
			if (!(query instanceof NestedQuery)) {
				continue;
			}

			NestedQuery nestedQuery = (NestedQuery)query;

			BooleanQuery nestedBooleanQuery =
				(BooleanQuery)nestedQuery.getQuery();

			for (BooleanClause<Query> booleanClause :
					nestedBooleanQuery.clauses()) {

				Query innerQuery = booleanClause.getClause();

				if (!(innerQuery instanceof BooleanQuery)) {
					continue;
				}

				BooleanQuery assigneeBooleanQuery = (BooleanQuery)innerQuery;

				List<Query> assigneeQueries = new ArrayList<>();

				for (BooleanClause<Query> assigneeClause :
						assigneeBooleanQuery.clauses()) {

					assigneeQueries.add(assigneeClause.getClause());
				}

				return assigneeQueries;
			}
		}

		return Collections.emptyList();
	}

	private BooleanQuery _mockBooleanQuery(ArgumentCaptor<Query> argumentCaptor)
		throws Exception {

		BooleanQuery booleanQuery = Mockito.mock(BooleanQuery.class);

		Mockito.when(
			booleanQuery.add(
				argumentCaptor.capture(), Mockito.any(BooleanClauseOccur.class))
		).thenReturn(
			null
		);

		return booleanQuery;
	}

	private KeywordQueryContributorHelper _mockKeywordQueryContributorHelper() {
		return _mockKeywordQueryContributorHelper(LocaleUtil.US);
	}

	private KeywordQueryContributorHelper _mockKeywordQueryContributorHelper(
		Locale locale) {

		KeywordQueryContributorHelper keywordQueryContributorHelper =
			Mockito.mock(KeywordQueryContributorHelper.class);

		Mockito.when(
			keywordQueryContributorHelper.getSearchContext()
		).thenReturn(
			_buildSearchContext(locale)
		);

		return keywordQueryContributorHelper;
	}

	private ObjectDefinition _mockObjectDefinition() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		ObjectFieldBag objectFieldBag = Mockito.mock(ObjectFieldBag.class);

		Mockito.when(
			objectDefinition.getObjectFieldBag()
		).thenReturn(
			objectFieldBag
		);

		return objectDefinition;
	}

	private ObjectField _mockObjectField(
		String businessType, String dbType, String name) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			dbType
		);

		Mockito.when(
			objectField.getIndexedLanguageId()
		).thenReturn(
			null
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			objectField.isIndexed()
		).thenReturn(
			true
		);

		Mockito.when(
			objectField.isIndexedAsKeyword()
		).thenReturn(
			false
		);

		Mockito.when(
			objectField.isLocalized()
		).thenReturn(
			false
		);

		return objectField;
	}

	private void _mockObjectFields(ObjectFieldBag objectFieldBag) {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, RandomTestUtil.randomString());

		Mockito.when(
			objectFieldBag.getNestedIndexedObjectFields()
		).thenReturn(
			Arrays.asList(objectField)
		);
	}

}