/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.cmis.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.service.RepositoryEntryLocalService;
import com.liferay.portal.kernel.service.RepositoryEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Mika Koivisto
 */
public class BaseCmisSearchQueryBuilder implements CMISSearchQueryBuilder {

	public BaseCmisSearchQueryBuilder() {
		this(
			RepositoryEntryLocalServiceUtil.getService(),
			UserLocalServiceUtil.getService());
	}

	@Override
	public String buildQuery(SearchContext searchContext, Query query)
		throws SearchException {

		StringBundler sb = new StringBundler();

		sb.append("SELECT cmis:objectId");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (queryConfig.isScoreEnabled()) {
			sb.append(", SCORE() AS HITS");
		}

		sb.append(" FROM cmis:document");

		CMISDisjunction cmisDisjunction = new CMISDisjunction();

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Repository query support " +
					queryConfig.getAttribute("capabilityQuery"));
		}

		if (!isSupportsOnlyFullText(queryConfig)) {
			traversePropertiesQuery(cmisDisjunction, query, queryConfig);
		}

		if (isSupportsFullText(queryConfig)) {
			CMISContainsExpression cmisContainsExpression =
				new CMISContainsExpression();

			traverseContentQuery(cmisContainsExpression, query, queryConfig);

			if (!cmisContainsExpression.isEmpty()) {
				cmisDisjunction.add(cmisContainsExpression);
			}
		}

		if (!cmisDisjunction.isEmpty()) {
			sb.append(" WHERE ");
			sb.append(cmisDisjunction.toQueryFragment());
		}

		Sort[] sorts = searchContext.getSorts();

		if (queryConfig.isScoreEnabled() || ArrayUtil.isNotEmpty(sorts)) {
			sb.append(" ORDER BY ");
		}

		if (ArrayUtil.isNotEmpty(sorts)) {
			int i = 0;

			for (Sort sort : sorts) {
				String fieldName = sort.getFieldName();

				if (!isSupportedField(fieldName)) {
					continue;
				}

				if (i > 0) {
					sb.append(", ");
				}

				sb.append(getCmisField(fieldName));

				if (sort.isReverse()) {
					sb.append(" DESC");
				}
				else {
					sb.append(" ASC");
				}

				i++;
			}
		}
		else if (queryConfig.isScoreEnabled()) {
			sb.append("HITS DESC");
		}

		if (_log.isDebugEnabled()) {
			_log.debug("CMIS query " + sb.toString());
		}

		return sb.toString();
	}

	protected BaseCmisSearchQueryBuilder(
		RepositoryEntryLocalService repositoryEntryLocalService,
		UserLocalService userLocalService) {

		_repositoryEntryLocalService = repositoryEntryLocalService;
		_userLocalService = userLocalService;
	}

	protected CMISCriterion buildFieldExpression(
			String field, String value,
			CMISSimpleExpressionOperator cmisSimpleExpressionOperator,
			QueryConfig queryConfig)
		throws SearchException {

		CMISCriterion cmisCriterion = null;

		boolean wildcard = false;

		if (CMISSimpleExpressionOperator.LIKE == cmisSimpleExpressionOperator) {
			wildcard = true;
		}

		if (field.equals(Field.FOLDER_ID)) {
			long folderId = GetterUtil.getLong(value);

			try {
				RepositoryEntry repositoryEntry =
					_repositoryEntryLocalService.fetchRepositoryEntry(folderId);

				if (repositoryEntry != null) {
					String objectId = repositoryEntry.getMappedId();

					objectId = CMISParameterValueUtil.formatParameterValue(
						field, objectId, wildcard, queryConfig);

					if (queryConfig.isSearchSubfolders()) {
						cmisCriterion = new CMISInTreeExpression(objectId);
					}
					else {
						cmisCriterion = new CMISInFolderExpression(objectId);
					}
				}
			}
			catch (SystemException systemException) {
				throw new SearchException(
					"Unable to determine folder {folderId=" + folderId + "}",
					systemException);
			}
		}
		else if (field.equals(Field.USER_ID)) {
			try {
				long userId = GetterUtil.getLong(value);

				User user = _userLocalService.getUserById(userId);

				String screenName = CMISParameterValueUtil.formatParameterValue(
					field, user.getScreenName(), wildcard, queryConfig);

				cmisCriterion = new CMISSimpleExpression(
					getCmisField(field), screenName,
					cmisSimpleExpressionOperator);
			}
			catch (Exception exception) {
				if (exception instanceof SearchException) {
					throw (SearchException)exception;
				}

				throw new SearchException(
					StringBundler.concat(
						"Unable to determine user {", field, "=", value, "}"),
					exception);
			}
		}
		else {
			value = CMISParameterValueUtil.formatParameterValue(
				field, value, wildcard, queryConfig);

			cmisCriterion = new CMISSimpleExpression(
				getCmisField(field), value, cmisSimpleExpressionOperator);
		}

		return cmisCriterion;
	}

	protected String getCmisField(String field) {
		return _cmisFields.get(field);
	}

	protected boolean isSupportedField(String field) {
		return _supportedFields.contains(field);
	}

	protected boolean isSupportsFullText(QueryConfig queryConfig) {
		String capabilityQuery = (String)queryConfig.getAttribute(
			"capabilityQuery");

		if (Validator.isNull(capabilityQuery)) {
			return false;
		}

		if (capabilityQuery.equals("bothcombined") ||
			capabilityQuery.equals("fulltextonly")) {

			return true;
		}

		return false;
	}

	protected boolean isSupportsOnlyFullText(QueryConfig queryConfig) {
		String capabilityQuery = (String)queryConfig.getAttribute(
			"capabilityQuery");

		if (Validator.isNull(capabilityQuery)) {
			return false;
		}

		return capabilityQuery.equals("fulltextonly");
	}

	protected void traverseContentQuery(
			CMISJunction cmisJunction, Query query, QueryConfig queryConfig)
		throws SearchException {

		if (query instanceof BooleanQuery) {
			BooleanQuery booleanQuery = (BooleanQuery)query;

			List<BooleanClause<Query>> booleanClauses = booleanQuery.clauses();

			CMISFullTextConjunction anyCMISConjunction =
				new CMISFullTextConjunction();
			CMISDisjunction cmisDisjunction = new CMISDisjunction();
			CMISFullTextConjunction notCMISConjunction =
				new CMISFullTextConjunction();

			for (BooleanClause<Query> booleanClause : booleanClauses) {
				CMISJunction currentCMISJunction = cmisDisjunction;

				BooleanClauseOccur booleanClauseOccur =
					booleanClause.getBooleanClauseOccur();

				if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
					currentCMISJunction = anyCMISConjunction;
				}
				else if (booleanClauseOccur.equals(
							BooleanClauseOccur.MUST_NOT)) {

					currentCMISJunction = notCMISConjunction;
				}

				Query booleanClauseQuery = booleanClause.getClause();

				traverseContentQuery(
					currentCMISJunction, booleanClauseQuery, queryConfig);
			}

			if (!anyCMISConjunction.isEmpty()) {
				cmisJunction.add(anyCMISConjunction);
			}

			if (!cmisDisjunction.isEmpty()) {
				cmisJunction.add(cmisDisjunction);
			}

			if (!notCMISConjunction.isEmpty()) {
				CMISContainsNotExpression cmisContainsNotExpression =
					new CMISContainsNotExpression(notCMISConjunction);

				cmisJunction.add(cmisContainsNotExpression);
			}
		}
		else if (query instanceof TermQuery) {
			TermQuery termQuery = (TermQuery)query;

			QueryTerm queryTerm = termQuery.getQueryTerm();

			if (!_isContentFieldQueryTerm(queryTerm)) {
				return;
			}

			String value = queryTerm.getValue();

			value = CMISParameterValueUtil.formatParameterValue(
				queryTerm.getField(), value, false, queryConfig);

			CMISContainsValueExpression cmisContainsValueExpression =
				new CMISContainsValueExpression(value);

			cmisJunction.add(cmisContainsValueExpression);
		}
		else if (query instanceof WildcardQuery) {
			WildcardQuery wildcardQuery = (WildcardQuery)query;

			QueryTerm queryTerm = wildcardQuery.getQueryTerm();

			if (!_isContentFieldQueryTerm(queryTerm)) {
				return;
			}

			String value = queryTerm.getValue();

			String[] terms = value.split(_STAR_PATTERN);

			CMISConjunction cmisConjunction = new CMISConjunction();

			for (String term : terms) {
				if (Validator.isNull(term)) {
					continue;
				}

				CMISContainsValueExpression containsValueExpression =
					new CMISContainsValueExpression(term);

				cmisConjunction.add(containsValueExpression);
			}

			cmisJunction.add(cmisConjunction);
		}
	}

	protected void traversePropertiesQuery(
			CMISJunction cmisJunction, Query query, QueryConfig queryConfig)
		throws SearchException {

		if (query instanceof BooleanQuery) {
			BooleanQuery booleanQuery = (BooleanQuery)query;

			List<BooleanClause<Query>> booleanClauses = booleanQuery.clauses();

			CMISConjunction anyCMISConjunction = new CMISConjunction();
			CMISDisjunction cmisDisjunction = new CMISDisjunction();
			CMISConjunction notCMISConjunction = new CMISConjunction();

			for (BooleanClause<Query> booleanClause : booleanClauses) {
				CMISJunction currentCMISJunction = cmisDisjunction;

				BooleanClauseOccur booleanClauseOccur =
					booleanClause.getBooleanClauseOccur();

				if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
					currentCMISJunction = anyCMISConjunction;
				}
				else if (booleanClauseOccur.equals(
							BooleanClauseOccur.MUST_NOT)) {

					currentCMISJunction = notCMISConjunction;
				}

				Query booleanClauseQuery = booleanClause.getClause();

				traversePropertiesQuery(
					currentCMISJunction, booleanClauseQuery, queryConfig);
			}

			if (!anyCMISConjunction.isEmpty()) {
				cmisJunction.add(anyCMISConjunction);
			}

			if (!cmisDisjunction.isEmpty()) {
				cmisJunction.add(cmisDisjunction);
			}

			if (!notCMISConjunction.isEmpty()) {
				cmisJunction.add(new CMISNotExpression(notCMISConjunction));
			}
		}
		else if (query instanceof MatchQuery) {
			MatchQuery matchQuery = (MatchQuery)query;

			if (!isSupportedField(matchQuery.getField())) {
				return;
			}

			CMISCriterion cmisCriterion = buildFieldExpression(
				matchQuery.getField(), matchQuery.getValue(),
				CMISSimpleExpressionOperator.EQ, queryConfig);

			if (cmisCriterion != null) {
				cmisJunction.add(cmisCriterion);
			}
		}
		else if (query instanceof TermQuery) {
			TermQuery termQuery = (TermQuery)query;

			QueryTerm queryTerm = termQuery.getQueryTerm();

			if (!isSupportedField(queryTerm.getField())) {
				return;
			}

			CMISCriterion cmisCriterion = buildFieldExpression(
				queryTerm.getField(), queryTerm.getValue(),
				CMISSimpleExpressionOperator.EQ, queryConfig);

			if (cmisCriterion != null) {
				cmisJunction.add(cmisCriterion);
			}
		}
		else if (query instanceof TermRangeQuery) {
			TermRangeQuery termRangeQuery = (TermRangeQuery)query;

			if (!isSupportedField(termRangeQuery.getField())) {
				return;
			}

			String fieldName = termRangeQuery.getField();

			String cmisField = getCmisField(fieldName);
			String cmisLowerTerm = CMISParameterValueUtil.formatParameterValue(
				fieldName, termRangeQuery.getLowerTerm(), false, queryConfig);
			String cmisUpperTerm = CMISParameterValueUtil.formatParameterValue(
				fieldName, termRangeQuery.getUpperTerm(), false, queryConfig);

			CMISCriterion cmisCriterion = new CMISBetweenExpression(
				cmisField, cmisLowerTerm, cmisUpperTerm,
				termRangeQuery.includesLower(), termRangeQuery.includesUpper());

			cmisJunction.add(cmisCriterion);
		}
		else if (query instanceof WildcardQuery) {
			WildcardQuery wildcardQuery = (WildcardQuery)query;

			QueryTerm queryTerm = wildcardQuery.getQueryTerm();

			if (!isSupportedField(queryTerm.getField())) {
				return;
			}

			CMISCriterion cmisCriterion = buildFieldExpression(
				queryTerm.getField(), queryTerm.getValue(),
				CMISSimpleExpressionOperator.LIKE, queryConfig);

			if (cmisCriterion != null) {
				cmisJunction.add(cmisCriterion);
			}
		}
	}

	private boolean _isContentFieldQueryTerm(QueryTerm queryTerm) {
		String fieldName = queryTerm.getField();

		return fieldName.equals(Field.CONTENT);
	}

	private static final String _STAR_PATTERN = Pattern.quote(StringPool.STAR);

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCmisSearchQueryBuilder.class);

	private static final Map<String, String> _cmisFields = HashMapBuilder.put(
		Field.CREATE_DATE, "cmis:creationDate"
	).put(
		Field.MODIFIED_DATE, "cmis:lastModificationDate"
	).put(
		Field.NAME, "cmis:name"
	).put(
		Field.TITLE, "cmis:name"
	).put(
		Field.USER_ID, "cmis:createdBy"
	).put(
		Field.USER_NAME, "cmis:createdBy"
	).build();
	private static final Set<String> _supportedFields = new HashSet<>(
		Arrays.asList(
			Field.CREATE_DATE, Field.FOLDER_ID, Field.MODIFIED_DATE, Field.NAME,
			Field.TITLE, Field.USER_ID, Field.USER_NAME));

	private final RepositoryEntryLocalService _repositoryEntryLocalService;
	private final UserLocalService _userLocalService;

}