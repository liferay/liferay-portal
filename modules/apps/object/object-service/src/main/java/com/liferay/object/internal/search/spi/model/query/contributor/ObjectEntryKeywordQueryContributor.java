/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.query.contributor;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectView;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.search.NestedQuery;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntryKeywordQueryContributor
	implements KeywordQueryContributor {

	public ObjectEntryKeywordQueryContributor(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectViewLocalService objectViewLocalService,
		SearchLocalizationHelper searchLocalizationHelper) {

		_objectDefinition = objectDefinition;
		_objectFieldLocalService = objectFieldLocalService;
		_objectViewLocalService = objectViewLocalService;
		_searchLocalizationHelper = searchLocalizationHelper;
	}

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		if (Validator.isBlank(keywords)) {
			return;
		}

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		AtomicBoolean addObjectEntryTitle = new AtomicBoolean(true);
		List<ObjectField> objectFields = null;

		if (GetterUtil.getBoolean(
				searchContext.getAttribute("searchByObjectView"))) {

			ObjectView defaultObjectView =
				_objectViewLocalService.fetchDefaultObjectView(
					_objectDefinition.getObjectDefinitionId());

			if (defaultObjectView != null) {
				addObjectEntryTitle.set(false);

				objectFields = TransformUtil.transform(
					defaultObjectView.getObjectViewColumns(),
					objectViewColumn -> {
						String objectFieldName =
							objectViewColumn.getObjectFieldName();

						if (Objects.equals(objectFieldName, "id")) {
							addObjectEntryTitle.set(true);
						}

						return _objectFieldLocalService.fetchObjectField(
							defaultObjectView.getObjectDefinitionId(),
							objectFieldName);
					});
			}
		}

		if (objectFields == null) {
			ObjectFieldBag objectFieldBag =
				_objectDefinition.getObjectFieldBag();

			objectFields = objectFieldBag.getNestedIndexedObjectFields();
		}

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			_objectDefinition.getDefaultLanguageId());
		String defaultLocalizedTitleFieldName = null;

		if (Objects.equals(defaultLocale, searchContext.getLocale())) {
			defaultLocale = null;
		}
		else {
			defaultLocalizedTitleFieldName = Field.getLocalizedName(
				defaultLocale, "objectEntryTitle");
		}

		String localizedTitleFieldName = null;

		if (searchContext.getLocale() != null) {
			localizedTitleFieldName = Field.getLocalizedName(
				searchContext.getLocale(), "objectEntryTitle");
		}

		_addHighlightFieldNames(
			defaultLocale, searchContext.getQueryConfig(), searchContext);
		_addTerm(booleanQuery, "extension", StringUtil.toLowerCase(keywords));

		if (StringUtil.startsWith(keywords, CharPool.QUOTE) &&
			StringUtil.endsWith(keywords, CharPool.QUOTE)) {

			if (defaultLocale != null) {
				_addTerm(
					booleanQuery, defaultLocalizedTitleFieldName, keywords);
			}

			if (searchContext.getLocale() != null) {
				_addTerm(booleanQuery, localizedTitleFieldName, keywords);
			}

			_addTerm(booleanQuery, "objectEntryTitle", keywords);

			try {
				for (ObjectField objectField : objectFields) {
					_contribute(
						booleanQuery, defaultLocale, objectField, searchContext,
						keywords);
				}
			}
			catch (ParseException parseException) {
				throw new SystemException(parseException);
			}
		}
		else {
			if (addObjectEntryTitle.get()) {
				if (defaultLocale != null) {
					_addTerm(
						booleanQuery, defaultLocalizedTitleFieldName, keywords);
				}

				if (searchContext.getLocale() != null) {
					_addTerm(booleanQuery, localizedTitleFieldName, keywords);
				}

				_addTerm(booleanQuery, "objectEntryTitle", keywords);
			}

			for (ObjectField objectField : objectFields) {
				if (!_isTextField(objectField)) {
					continue;
				}

				try {
					_contribute(
						booleanQuery, defaultLocale, objectField, searchContext,
						keywords);
				}
				catch (ParseException parseException) {
					throw new SystemException(parseException);
				}
			}

			for (String token : _tokenizeKeywords(keywords)) {
				if (addObjectEntryTitle.get() && !Validator.isBlank(token)) {
					booleanQuery.add(
						new TermQuery(Field.ENTRY_CLASS_PK, token),
						BooleanClauseOccur.SHOULD);

					booleanQuery.add(
						new WildcardQuery(
							"objectEntryTitle", token + StringPool.STAR),
						BooleanClauseOccur.SHOULD);
				}

				for (ObjectField objectField : objectFields) {
					if (_isTextField(objectField)) {
						continue;
					}

					try {
						_contribute(
							booleanQuery, defaultLocale, objectField,
							searchContext, token);
					}
					catch (ParseException parseException) {
						throw new SystemException(parseException);
					}
				}
			}
		}
	}

	private void _addHighlightFieldNames(
		Locale defaultLocale, QueryConfig queryConfig,
		SearchContext searchContext) {

		queryConfig.addHighlightFieldNames(
			"nestedFieldArray.value_boolean", "nestedFieldArray.value_keyword",
			"nestedFieldArray.value_text");

		for (String localizedNestedFieldName :
				_getLocalizedNestedFieldNames(defaultLocale, searchContext)) {

			queryConfig.addHighlightFieldNames(localizedNestedFieldName);
		}

		ObjectFieldBag objectFieldBag = _objectDefinition.getObjectFieldBag();

		ObjectField titleObjectField = objectFieldBag.getObjectField(
			_objectDefinition.getTitleObjectFieldId());

		if ((titleObjectField != null) && titleObjectField.isLocalized()) {
			queryConfig.addHighlightFieldNames(
				Field.getLocalizedName(
					searchContext.getLocale(), "objectEntryTitle"));

			if (defaultLocale != null) {
				queryConfig.addHighlightFieldNames(
					Field.getLocalizedName(defaultLocale, "objectEntryTitle"));
			}
		}
		else {
			queryConfig.addHighlightFieldNames("objectEntryTitle");
		}
	}

	private void _addNumericClause(
			String fieldName, BooleanQuery nestedBooleanQuery,
			ObjectField objectField, String token)
		throws ParseException {

		boolean addedRangeQuery = _addRangeQuery(
			nestedBooleanQuery, fieldName, token, objectField.getDBType());

		if (!addedRangeQuery && _isValidInput(token, objectField.getDBType())) {
			nestedBooleanQuery.add(
				new TermQuery(fieldName, token), BooleanClauseOccur.MUST);
		}
	}

	private boolean _addRangeQuery(
			BooleanQuery booleanQuery, String fieldName, String token,
			String type)
		throws ParseException {

		if (Validator.isBlank(token)) {
			return false;
		}

		String[] range = RangeParserUtil.parserRange(token);

		String lowerTerm = range[0];
		String upperTerm = range[1];

		if (!_isValidRange(lowerTerm, type, upperTerm)) {
			return false;
		}

		booleanQuery.add(
			new TermRangeQuery(fieldName, lowerTerm, upperTerm, true, true),
			BooleanClauseOccur.MUST);

		return true;
	}

	private void _addTerm(
		BooleanQuery booleanQuery, String fieldName, String value) {

		if (Validator.isBlank(fieldName) || Validator.isBlank(value)) {
			return;
		}

		try {
			booleanQuery.addTerm(fieldName, value, false);
		}
		catch (ParseException parseException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to add term for field ", fieldName,
						" and value ", value),
					parseException);
			}
		}
	}

	private void _configureNestedQuery(
		String[] highlightFieldNames, String innerHitsName,
		NestedQuery nestedQuery, SearchContext searchContext) {

		if (ArrayUtil.isEmpty(highlightFieldNames)) {
			return;
		}

		Set<String> configuredInnerHitsNames =
			(Set<String>)searchContext.getAttribute(
				"objectEntryConfiguredInnerHitsNames");

		if (configuredInnerHitsNames == null) {
			configuredInnerHitsNames = new HashSet<>();

			searchContext.setAttribute(
				"objectEntryConfiguredInnerHitsNames",
				(Serializable)configuredInnerHitsNames);
		}

		if (!configuredInnerHitsNames.add(innerHitsName)) {
			return;
		}

		QueryConfig nestedQueryConfig = nestedQuery.getQueryConfig();

		nestedQueryConfig.setHighlightEnabled(true);
		nestedQueryConfig.setHighlightFieldNames(highlightFieldNames);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		nestedQueryConfig.setHighlightFragmentSize(
			queryConfig.getHighlightFragmentSize());
		nestedQueryConfig.setHighlightRequireFieldMatch(
			queryConfig.isHighlightRequireFieldMatch());
		nestedQueryConfig.setHighlightSnippetSize(
			queryConfig.getHighlightSnippetSize());
		nestedQueryConfig.setLocale(queryConfig.getLocale());

		nestedQuery.setInnerHitsEnabled(true);
		nestedQuery.setInnerHitsName(innerHitsName);
	}

	private void _contribute(
			BooleanQuery booleanQuery, Locale defaultLocale,
			ObjectField objectField, SearchContext searchContext, String token)
		throws ParseException {

		if ((objectField == null) || !objectField.isIndexed()) {
			return;
		}

		token = _getToken(objectField.getName(), searchContext, token);

		if (Validator.isNull(token)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Add search term ", token, " for object field ",
					objectField.getName()));
		}

		String[] highlightFieldNames = null;
		BooleanQuery nestedBooleanQuery = new BooleanQuery();
		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (objectField.isIndexedAsKeyword()) {
			String fieldName = "nestedFieldArray.value_keyword";
			String lowerCaseToken = StringUtil.toLowerCase(token);

			nestedBooleanQuery.add(
				new WildcardQuery(fieldName, lowerCaseToken + StringPool.STAR),
				BooleanClauseOccur.MUST);
			nestedBooleanQuery.add(
				new TermQuery(fieldName, lowerCaseToken),
				BooleanClauseOccur.SHOULD);

			queryConfig.addHighlightFieldNames(fieldName);

			highlightFieldNames = new String[] {fieldName};
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			BooleanQuery assigneeBooleanQuery = new BooleanQuery();

			String keywordFieldName =
				"nestedFieldArray.value_keyword_lowercase";

			assigneeBooleanQuery.add(
				new TermQuery(keywordFieldName, StringUtil.toLowerCase(token)),
				BooleanClauseOccur.SHOULD);

			String textFieldName = "nestedFieldArray.value_text";

			assigneeBooleanQuery.add(
				new MatchQuery(textFieldName, token),
				BooleanClauseOccur.SHOULD);

			nestedBooleanQuery.add(
				assigneeBooleanQuery, BooleanClauseOccur.MUST);

			queryConfig.addHighlightFieldNames(keywordFieldName, textFieldName);
		}
		else if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) ||
				 Objects.equals(
					 objectField.getDBType(),
					 ObjectFieldConstants.DB_TYPE_CLOB) ||
				 Objects.equals(
					 objectField.getDBType(),
					 ObjectFieldConstants.DB_TYPE_STRING)) {

			if (objectField.isLocalized()) {
				BooleanQuery localizedNestedBooleanQuery = new BooleanQuery();

				String[] localizedFieldNames = _getLocalizedNestedFieldNames(
					defaultLocale, searchContext);

				for (String localizedFieldName : localizedFieldNames) {
					localizedNestedBooleanQuery.add(
						_createMatchQuery(
							localizedFieldName, searchContext, token),
						BooleanClauseOccur.SHOULD);

					queryConfig.addHighlightFieldNames(localizedFieldName);
				}

				nestedBooleanQuery.add(
					localizedNestedBooleanQuery, BooleanClauseOccur.MUST);

				highlightFieldNames = localizedFieldNames;
			}
			else {
				BooleanQuery localizedNestedBooleanQuery = new BooleanQuery();

				String[] localizedFieldNames =
					_searchLocalizationHelper.getLocalizedFieldNames(
						new String[] {"nestedFieldArray.value"}, searchContext);

				for (String localizedFieldName : localizedFieldNames) {
					localizedNestedBooleanQuery.add(
						_createMatchQuery(
							localizedFieldName, searchContext, token),
						BooleanClauseOccur.SHOULD);

					queryConfig.addHighlightFieldNames(localizedFieldName);
				}

				nestedBooleanQuery.add(
					localizedNestedBooleanQuery, BooleanClauseOccur.MUST);

				highlightFieldNames = localizedFieldNames;
			}
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_BIG_DECIMAL)) {

			_addNumericClause(
				"nestedFieldArray.value_double", nestedBooleanQuery,
				objectField, token);
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_BLOB)) {

			_log.error("Blob type is not indexable");
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_BOOLEAN)) {

			String fieldName = null;

			if (StringUtil.equalsIgnoreCase(token, "false") ||
				StringUtil.equalsIgnoreCase(token, "true")) {

				fieldName = "nestedFieldArray.value_boolean";
			}
			else if (StringUtil.equalsIgnoreCase(token, "no") ||
					 StringUtil.equalsIgnoreCase(token, "yes")) {

				fieldName = "nestedFieldArray.value_keyword";
			}

			if (fieldName != null) {
				nestedBooleanQuery.add(
					new TermQuery(fieldName, StringUtil.toLowerCase(token)),
					BooleanClauseOccur.MUST);

				queryConfig.addHighlightFieldNames(fieldName);
			}
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_DATE)) {

			_addNumericClause(
				"nestedFieldArray.value_date", nestedBooleanQuery, objectField,
				token);
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_DOUBLE)) {

			_addNumericClause(
				"nestedFieldArray.value_double", nestedBooleanQuery,
				objectField, token);
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_INTEGER)) {

			_addNumericClause(
				"nestedFieldArray.value_integer", nestedBooleanQuery,
				objectField, token);
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_LONG)) {

			_addNumericClause(
				"nestedFieldArray.value_long", nestedBooleanQuery, objectField,
				token);
		}

		if (nestedBooleanQuery.hasClauses()) {
			BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.SHOULD;

			if (searchContext.isAndSearch()) {
				booleanClauseOccur = BooleanClauseOccur.MUST;
			}

			nestedBooleanQuery.add(
				new TermQuery(
					"nestedFieldArray.fieldName", objectField.getName()),
				BooleanClauseOccur.MUST);

			NestedQuery nestedQuery = new NestedQuery(
				"nestedFieldArray", nestedBooleanQuery);

			_configureNestedQuery(
				highlightFieldNames, objectField.getName(), nestedQuery,
				searchContext);

			booleanQuery.add(nestedQuery, booleanClauseOccur);
		}
	}

	private MatchQuery _createMatchQuery(
		String field, SearchContext searchContext, String value) {

		MatchQuery matchQuery = new MatchQuery(field, value);

		if (searchContext.isAndSearch()) {
			matchQuery.setOperator(MatchQuery.Operator.AND);
		}

		return matchQuery;
	}

	private String[] _getLocalizedNestedFieldNames(
		Locale defaultLocale, SearchContext searchContext) {

		List<String> fieldNames = new ArrayList<>();

		Locale locale = searchContext.getLocale();

		if (locale != null) {
			fieldNames.add(
				Field.getLocalizedName(locale, "nestedFieldArray.value"));
		}

		if ((defaultLocale != null) &&
			((locale == null) || !locale.equals(defaultLocale))) {

			fieldNames.add(
				Field.getLocalizedName(
					defaultLocale, "nestedFieldArray.value"));
		}

		return fieldNames.toArray(new String[0]);
	}

	private String _getToken(
		String fieldName, SearchContext searchContext, String token) {

		if (Validator.isNotNull(token)) {
			return token;
		}

		String value = StringPool.BLANK;

		Serializable serializable = searchContext.getAttribute(fieldName);

		if (serializable != null) {
			Class<?> clazz = serializable.getClass();

			if (clazz.isArray()) {
				value = StringUtil.merge((Object[])serializable);
			}
			else {
				value = GetterUtil.getString(serializable);
			}
		}

		if (!Validator.isBlank(value) &&
			(searchContext.getFacet(fieldName) != null)) {

			return null;
		}

		if (Validator.isBlank(value)) {
			value = searchContext.getKeywords();
		}

		if (Validator.isBlank(value)) {
			return null;
		}

		return value;
	}

	private boolean _isTextField(ObjectField objectField) {
		if ((objectField == null) || !objectField.isIndexed() ||
			objectField.isIndexedAsKeyword()) {

			return false;
		}

		if (Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) ||
			Objects.equals(
				objectField.getDBType(), ObjectFieldConstants.DB_TYPE_CLOB) ||
			Objects.equals(
				objectField.getDBType(), ObjectFieldConstants.DB_TYPE_STRING)) {

			return true;
		}

		return false;
	}

	private boolean _isValidInput(String token, String type) {
		if (token == null) {
			return false;
		}

		try {
			if (Objects.equals(type, "BigDecimal") ||
				Objects.equals(type, "Double")) {

				Double.valueOf(token);
			}
			else if (Objects.equals(type, "Date")) {
				Matcher matcher = _pattern.matcher(token);

				if (!matcher.matches()) {
					return false;
				}
			}
			else if (Objects.equals(type, "Integer")) {
				Integer.valueOf(token);
			}
			else if (Objects.equals(type, "Long")) {
				Long.valueOf(token);
			}
			else {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	private boolean _isValidRange(
		String lowerTerm, String type, String upperTerm) {

		if (_isValidInput(lowerTerm, type) && _isValidInput(upperTerm, type)) {
			return true;
		}

		return false;
	}

	private List<String> _tokenizeKeywords(String keywords) {
		KeywordTokenizer keywordTokenizer = new KeywordTokenizer();

		return keywordTokenizer.tokenize(keywords);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryKeywordQueryContributor.class);

	private static final Pattern _pattern = Pattern.compile("\\d{14}");

	private final ObjectDefinition _objectDefinition;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectViewLocalService _objectViewLocalService;
	private final SearchLocalizationHelper _searchLocalizationHelper;

	private class KeywordTokenizer {

		public List<String> tokenize(String keywords) {
			keywords = _normalizeWhitespace(keywords);

			List<String> tokens = new ArrayList<>();

			int[] startAndEnd = getStartAndEnd(keywords);

			tokenize(keywords, tokens, startAndEnd[0], startAndEnd[1]);

			return tokens;
		}

		protected int[] getStartAndEnd(String keywords) {
			int quoteStart = keywords.indexOf(CharPool.QUOTE);
			int rangeStart = keywords.indexOf(CharPool.OPEN_BRACKET);

			if (quoteStart == QueryUtil.ALL_POS) {
				return new int[] {
					rangeStart,
					keywords.indexOf(CharPool.CLOSE_BRACKET, rangeStart + 1)
				};
			}
			else if (rangeStart == QueryUtil.ALL_POS) {
				return new int[] {
					quoteStart, keywords.indexOf(CharPool.QUOTE, quoteStart + 1)
				};
			}
			else if (quoteStart < rangeStart) {
				return new int[] {
					quoteStart, keywords.indexOf(CharPool.QUOTE, quoteStart + 1)
				};
			}

			return new int[] {
				rangeStart,
				keywords.indexOf(CharPool.CLOSE_BRACKET, rangeStart + 1)
			};
		}

		protected String[] split(String keywords) {
			if (Objects.equals(keywords, StringPool.NULL)) {
				return new String[] {keywords};
			}

			return StringUtil.split(keywords, CharPool.SPACE);
		}

		protected void tokenize(
			String keywords, List<String> tokens, int start, int end) {

			if ((start == QueryUtil.ALL_POS) || (end == QueryUtil.ALL_POS)) {
				keywords = keywords.trim();

				if (!keywords.isEmpty()) {
					tokenizeBySpace(keywords, tokens);
				}

				return;
			}

			String token = keywords.substring(0, start);

			token = token.trim();

			if (!token.isEmpty()) {
				tokenizeBySpace(token, tokens);
			}

			token = keywords.substring(start, end + 1);

			token = token.trim();

			if (!token.isEmpty()) {
				if (StringUtil.startsWith(token, CharPool.QUOTE)) {
					token = StringUtil.unquote(token);
				}

				tokens.add(token);
			}

			if ((end + 1) > keywords.length()) {
				return;
			}

			keywords = keywords.substring(end + 1);

			keywords = keywords.trim();

			if (keywords.isEmpty()) {
				return;
			}

			int[] startAndEnd = getStartAndEnd(keywords);

			tokenize(keywords, tokens, startAndEnd[0], startAndEnd[1]);
		}

		protected void tokenizeBySpace(String keywords, List<String> tokens) {
			String[] keywordTokens = split(keywords);

			for (String keywordToken : keywordTokens) {
				String token = keywordToken.trim();

				if (!token.isEmpty()) {
					tokens.add(token);
				}
			}
		}

		private String _normalizeWhitespace(String keywords) {
			return StringUtil.replace(
				keywords, _IDEOGRAPHIC_SPACE, CharPool.SPACE);
		}

		private static final char _IDEOGRAPHIC_SPACE = '\u3000';

	}

}