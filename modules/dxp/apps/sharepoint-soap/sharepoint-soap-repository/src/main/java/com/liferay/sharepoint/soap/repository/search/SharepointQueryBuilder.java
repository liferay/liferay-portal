/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.soap.repository.search;

import com.liferay.document.library.repository.external.ExtRepositoryObjectType;
import com.liferay.document.library.repository.external.search.ExtRepositoryQueryMapper;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.sharepoint.soap.repository.SharepointWSRepository;
import com.liferay.sharepoint.soap.repository.connector.SharepointConnection;
import com.liferay.sharepoint.soap.repository.connector.SharepointConnectionInfo;
import com.liferay.sharepoint.soap.repository.connector.SharepointObject;
import com.liferay.sharepoint.soap.repository.connector.schema.query.QueryClause;
import com.liferay.sharepoint.soap.repository.connector.schema.query.QueryField;
import com.liferay.sharepoint.soap.repository.connector.schema.query.QueryOptionsList;
import com.liferay.sharepoint.soap.repository.connector.schema.query.QueryValue;
import com.liferay.sharepoint.soap.repository.connector.schema.query.join.AndJoin;
import com.liferay.sharepoint.soap.repository.connector.schema.query.join.BaseJoin;
import com.liferay.sharepoint.soap.repository.connector.schema.query.join.OrJoin;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.BaseMultiValueOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.BaseNoValueOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.BaseSingleValueOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.BeginsWithOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.ContainsOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.EqOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.GeqOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.GtOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.IncludesOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.IsNotNullOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.IsNullOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.LeqOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.LtOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.NeqOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.operator.NotIncludesOperator;
import com.liferay.sharepoint.soap.repository.connector.schema.query.option.FolderQueryOption;
import com.liferay.sharepoint.soap.repository.connector.schema.query.option.ViewAttributesQueryOption;
import com.liferay.sharepoint.soap.repository.model.SharepointWSFolder;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Iván Zaera
 */
public class SharepointQueryBuilder {

	public SharepointQueryBuilder(
			SharepointWSRepository sharepointWSRepository,
			SearchContext searchContext, Query query,
			ExtRepositoryQueryMapper extRepositoryQueryMapper)
		throws PortalException {

		_sharepointWSRepository = sharepointWSRepository;
		_extRepositoryQueryMapper = extRepositoryQueryMapper;

		SharepointConnection sharepointConnection =
			sharepointWSRepository.getSharepointConnection();

		_sharepointConnectionInfo =
			sharepointConnection.getSharepointConnectionInfo();

		_query =
			new com.liferay.sharepoint.soap.repository.connector.schema.query.
				Query(traverseQuery(query));

		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (queryConfig.isSearchSubfolders()) {
			if (isServerVersion(
					SharepointConnection.ServerVersion.SHAREPOINT_2013)) {

				_queryOptionsList = new QueryOptionsList(
					new FolderQueryOption(StringPool.BLANK),
					new ViewAttributesQueryOption(true));
			}
			else {
				_queryOptionsList = new QueryOptionsList(
					new FolderQueryOption(StringPool.BLANK));
			}
		}
		else {
			_queryOptionsList = new QueryOptionsList();
		}

		log(query);
	}

	public com.liferay.sharepoint.soap.repository.connector.schema.query.Query
		getQuery() {

		return _query;
	}

	public QueryOptionsList getQueryOptionsList() {
		return _queryOptionsList;
	}

	protected QueryClause buildFieldQueryClause(
			String fieldName, String fieldValue,
			SharepointQueryOperator sharepointQueryOperator)
		throws SearchException {

		QueryField queryField = new QueryField(
			getSharepointFieldName(fieldName));

		String formattedFieldValue = formatFieldValue(fieldName, fieldValue);

		QueryValue queryValue = new QueryValue(formattedFieldValue);

		if (sharepointQueryOperator == SharepointQueryOperator.EQ) {
			return new EqOperator(queryField, queryValue);
		}
		else if (sharepointQueryOperator == SharepointQueryOperator.GEQ) {
			return new GeqOperator(queryField, queryValue);
		}
		else if (sharepointQueryOperator == SharepointQueryOperator.GT) {
			return new GtOperator(queryField, queryValue);
		}
		else if (sharepointQueryOperator == SharepointQueryOperator.LEQ) {
			return new LeqOperator(queryField, queryValue);
		}
		else if (sharepointQueryOperator == SharepointQueryOperator.LIKE) {
			return buildLikeQueryClause(queryField, formattedFieldValue);
		}
		else if (sharepointQueryOperator == SharepointQueryOperator.LT) {
			return new LtOperator(queryField, queryValue);
		}
		else if (sharepointQueryOperator == SharepointQueryOperator.NEQ) {
			return new NeqOperator(queryField, queryValue);
		}

		throw new SearchException(
			"Unsupported Sharepoint query operator " + sharepointQueryOperator);
	}

	protected QueryClause buildLikeQueryClause(
			QueryField queryField, String fieldValue)
		throws SearchException {

		QueryValue queryValue = new QueryValue(
			StringUtil.replace(fieldValue, CharPool.STAR, StringPool.BLANK));

		if (fieldValue.startsWith(StringPool.STAR) &&
			fieldValue.endsWith(StringPool.STAR)) {

			return new ContainsOperator(queryField, queryValue);
		}
		else if (fieldValue.endsWith(StringPool.STAR)) {
			return new BeginsWithOperator(queryField, queryValue);
		}
		else if (fieldValue.startsWith(StringPool.STAR)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Converting and ENDS-WITH query to a CONTAINS query due " +
						"to repository limitations");
			}

			return new ContainsOperator(queryField, queryValue);
		}
		else if (fieldValue.contains(StringPool.STAR)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Converting an INTERMEDIATE WILDCARD query to MULTIPLE " +
						"CONTAINS queries due to repository limitations");
			}

			List<QueryClause> queryClauses = new ArrayList<>();

			String[] parts = StringUtil.split(fieldValue, StringPool.STAR);

			for (String part : parts) {
				queryClauses.add(
					new ContainsOperator(queryField, new QueryValue(part)));
			}

			return joinWithAnd(queryClauses);
		}

		throw new SearchException("Unsupported LIKE value " + fieldValue);
	}

	protected String formatFieldValue(String fieldName, String fieldValue)
		throws SearchException {

		if (fieldName.equals(Field.FOLDER_ID)) {
			String folderId = _extRepositoryQueryMapper.formatParameterValue(
				fieldName, fieldValue);

			try {
				SharepointWSFolder sharepointWSFolder =
					(SharepointWSFolder)
						_sharepointWSRepository.getExtRepositoryObject(
							ExtRepositoryObjectType.FOLDER, folderId);

				SharepointObject folderSharepointObject =
					sharepointWSFolder.getSharepointObject();

				String folderPath = folderSharepointObject.getPath();

				SharepointConnection sharepointConnection =
					_sharepointWSRepository.getSharepointConnection();

				SharepointConnectionInfo sharepointConnectionInfo =
					sharepointConnection.getSharepointConnectionInfo();

				String prefixPath = sharepointConnectionInfo.getLibraryPath();

				if (isServerVersion(
						SharepointConnection.ServerVersion.SHAREPOINT_2013)) {

					prefixPath =
						_sharepointConnectionInfo.getSitePath() +
							StringPool.SLASH + prefixPath;
				}

				if (folderPath.equals(StringPool.SLASH)) {
					return prefixPath;
				}

				return prefixPath + folderPath;
			}
			catch (PortalException portalException) {
				throw new SearchException(
					"Unable to get folder with folder ID" + folderId,
					portalException);
			}
			catch (SystemException systemException) {
				throw new SearchException(
					"Unable to get folder with folder ID" + folderId,
					systemException);
			}
		}
		else if (fieldName.equals(Field.CREATE_DATE) ||
				 fieldName.equals(Field.MODIFIED_DATE)) {

			Date date = _extRepositoryQueryMapper.formatDateParameterValue(
				fieldName, fieldValue);

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				_SHAREPOINT_DATE_FORMAT_PATTERN);

			return dateFormat.format(date);
		}
		else if (fieldName.equals(Field.USER_ID) ||
				 fieldName.equals(Field.USER_NAME)) {

			String screenName = _extRepositoryQueryMapper.formatParameterValue(
				fieldName, fieldValue);

			return _sharepointWSRepository.getSharepointLogin(screenName);
		}
		else {
			return _extRepositoryQueryMapper.formatParameterValue(
				fieldName, fieldValue);
		}
	}

	protected String getSharepointFieldName(String fieldName) {
		return _sharepointFields.get(fieldName);
	}

	protected boolean isServerVersion(
		SharepointConnection.ServerVersion serverVersion) {

		return serverVersion.equals(
			_sharepointConnectionInfo.getServerVersion());
	}

	protected boolean isSupportedField(String field) {
		return _supportedFields.contains(field);
	}

	protected QueryClause joinBooleanQueryClauses(
			List<QueryClause> andQueryClauses,
			List<QueryClause> notQueryClauses, List<QueryClause> orQueryClauses)
		throws SearchException {

		List<QueryClause> queryClauses = new ArrayList<>();

		QueryClause queryClause = joinWithAnd(andQueryClauses);

		if (queryClause != null) {
			queryClauses.add(queryClause);
		}

		queryClause = joinWithOr(orQueryClauses);

		if (queryClause != null) {
			queryClauses.add(queryClause);
		}

		queryClause = joinWithNot(notQueryClauses);

		if (queryClause != null) {
			queryClauses.add(queryClause);
		}

		return joinWithAnd(queryClauses);
	}

	protected QueryClause joinWithAnd(List<QueryClause> queryClauses) {
		if (queryClauses.isEmpty()) {
			return null;
		}
		else if (queryClauses.size() == 1) {
			return queryClauses.get(0);
		}

		QueryClause firstQueryClause = queryClauses.get(0);

		List<QueryClause> remainingQueryClauses = queryClauses.subList(
			1, queryClauses.size());

		return new AndJoin(
			firstQueryClause, joinWithAnd(remainingQueryClauses));
	}

	protected QueryClause joinWithNot(List<QueryClause> queryClauses)
		throws SearchException {

		QueryClause queryClause = joinWithAnd(queryClauses);

		if (queryClause == null) {
			return null;
		}

		return negate(queryClause);
	}

	protected QueryClause joinWithOr(List<QueryClause> queryClauses) {
		if (queryClauses.isEmpty()) {
			return null;
		}
		else if (queryClauses.size() == 1) {
			return queryClauses.get(0);
		}

		QueryClause firstQueryClause = queryClauses.get(0);

		List<QueryClause> remainingQueryClauses = queryClauses.subList(
			1, queryClauses.size());

		return new OrJoin(firstQueryClause, joinWithOr(remainingQueryClauses));
	}

	protected void log(Query query) {
		if (!_log.isDebugEnabled()) {
			return;
		}

		_log.debug("Liferay query: " + _liferayQueryExplainer.explain(query));
		_log.debug("Sharepoint query: " + _query);
		_log.debug("Sharepoint query options list: " + _queryOptionsList);
	}

	protected QueryClause negate(QueryClause queryClause)
		throws SearchException {

		if (queryClause instanceof BaseJoin) {
			return negateBaseJoin((BaseJoin)queryClause);
		}
		else if (queryClause instanceof BaseMultiValueOperator) {
			return negateBaseMultiValueOperator(
				(BaseMultiValueOperator)queryClause);
		}
		else if (queryClause instanceof BaseNoValueOperator) {
			return negateBaseNoValueOperator((BaseNoValueOperator)queryClause);
		}
		else if (queryClause instanceof BaseSingleValueOperator) {
			return negateBaseSingleValueOperator(
				(BaseSingleValueOperator)queryClause);
		}

		throw new SearchException(
			"Unable to negate query clause " + queryClause);
	}

	protected QueryClause negateBaseJoin(BaseJoin baseJoin)
		throws SearchException {

		if (baseJoin instanceof AndJoin) {
			AndJoin andJoin = (AndJoin)baseJoin;

			return new OrJoin(
				negate(andJoin.getLeftQueryClause()),
				negate(andJoin.getRightQueryClause()));
		}
		else if (baseJoin instanceof OrJoin) {
			OrJoin orJoin = (OrJoin)baseJoin;

			return new AndJoin(
				negate(orJoin.getLeftQueryClause()),
				negate(orJoin.getRightQueryClause()));
		}

		throw new SearchException("Unable to negate base join " + baseJoin);
	}

	protected QueryClause negateBaseMultiValueOperator(
			BaseMultiValueOperator baseMultiValueOperator)
		throws SearchException {

		throw new SearchException(
			"Unable to negate base multi value operator " +
				baseMultiValueOperator);
	}

	protected QueryClause negateBaseNoValueOperator(
			BaseNoValueOperator baseNoValueOperator)
		throws SearchException {

		if (baseNoValueOperator instanceof IsNotNullOperator) {
			IsNotNullOperator isNotNullOperator =
				(IsNotNullOperator)baseNoValueOperator;

			return new IsNullOperator(isNotNullOperator.getQueryField());
		}
		else if (baseNoValueOperator instanceof IsNullOperator) {
			IsNullOperator isNullOperator = (IsNullOperator)baseNoValueOperator;

			return new IsNotNullOperator(isNullOperator.getQueryField());
		}

		throw new SearchException(
			"Unable to negate base no value operator " + baseNoValueOperator);
	}

	protected QueryClause negateBaseSingleValueOperator(
			BaseSingleValueOperator baseSingleValueOperator)
		throws SearchException {

		if (baseSingleValueOperator instanceof EqOperator) {
			EqOperator eqOperator = (EqOperator)baseSingleValueOperator;

			return new NeqOperator(
				eqOperator.getQueryField(), eqOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof GeqOperator) {
			GeqOperator geqOperator = (GeqOperator)baseSingleValueOperator;

			return new LtOperator(
				geqOperator.getQueryField(), geqOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof GtOperator) {
			GtOperator gtOperator = (GtOperator)baseSingleValueOperator;

			return new LeqOperator(
				gtOperator.getQueryField(), gtOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof IncludesOperator) {
			IncludesOperator includesOperator =
				(IncludesOperator)baseSingleValueOperator;

			return new NotIncludesOperator(
				includesOperator.getQueryField(),
				includesOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof LeqOperator) {
			LeqOperator leqOperator = (LeqOperator)baseSingleValueOperator;

			return new GtOperator(
				leqOperator.getQueryField(), leqOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof LtOperator) {
			LtOperator ltOperator = (LtOperator)baseSingleValueOperator;

			return new GeqOperator(
				ltOperator.getQueryField(), ltOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof NeqOperator) {
			NeqOperator neqOperator = (NeqOperator)baseSingleValueOperator;

			return new EqOperator(
				neqOperator.getQueryField(), neqOperator.getQueryValue());
		}
		else if (baseSingleValueOperator instanceof NotIncludesOperator) {
			NotIncludesOperator notIncludesOperator =
				(NotIncludesOperator)baseSingleValueOperator;

			return new IncludesOperator(
				notIncludesOperator.getQueryField(),
				notIncludesOperator.getQueryValue());
		}

		throw new SearchException(
			"Unable to negate base single value operator " +
				baseSingleValueOperator);
	}

	protected QueryClause traverseBooleanQuery(BooleanQuery booleanQuery)
		throws SearchException {

		List<QueryClause> andQueryClauses = new ArrayList<>();
		List<QueryClause> notQueryClauses = new ArrayList<>();
		List<QueryClause> orQueryClauses = new ArrayList<>();

		for (BooleanClause<Query> booleanClause : booleanQuery.clauses()) {
			List<QueryClause> queryClauses = orQueryClauses;

			BooleanClauseOccur booleanClauseOccur =
				booleanClause.getBooleanClauseOccur();

			if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
				queryClauses = andQueryClauses;
			}
			else if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
				queryClauses = notQueryClauses;
			}

			Query query = booleanClause.getClause();

			QueryClause queryClause = traverseQuery(query);

			if (queryClause == null) {
				continue;
			}

			queryClauses.add(queryClause);
		}

		return joinBooleanQueryClauses(
			andQueryClauses, notQueryClauses, orQueryClauses);
	}

	protected QueryClause traverseQuery(Query query) throws SearchException {
		if (query instanceof BooleanQuery) {
			return traverseBooleanQuery((BooleanQuery)query);
		}
		else if (query instanceof TermQuery) {
			return traverseTermQuery((TermQuery)query);
		}
		else if (query instanceof TermRangeQuery) {
			return traverseTermRangeQuery((TermRangeQuery)query);
		}
		else if (query instanceof WildcardQuery) {
			return traverseWildcardQuery((WildcardQuery)query);
		}

		Class<?> clazz = query.getClass();

		throw new SearchException("Unsupported query type " + clazz.getName());
	}

	protected QueryClause traverseTermQuery(TermQuery termQuery)
		throws SearchException {

		QueryTerm queryTerm = termQuery.getQueryTerm();

		if (!isSupportedField(queryTerm.getField())) {
			return null;
		}

		return buildFieldQueryClause(
			queryTerm.getField(), queryTerm.getValue(),
			SharepointQueryOperator.EQ);
	}

	protected QueryClause traverseTermRangeQuery(TermRangeQuery termRangeQuery)
		throws SearchException {

		if (!isSupportedField(termRangeQuery.getField())) {
			return null;
		}

		QueryClause lowerTermQueryClause = null;

		String fieldName = termRangeQuery.getField();

		QueryField queryField = new QueryField(
			getSharepointFieldName(fieldName));

		String lowerTermFieldValue = formatFieldValue(
			fieldName, termRangeQuery.getLowerTerm());

		QueryValue lowerTermQueryValue = new QueryValue(lowerTermFieldValue);

		if (termRangeQuery.includesLower()) {
			lowerTermQueryClause = new GeqOperator(
				queryField, lowerTermQueryValue);
		}
		else {
			lowerTermQueryClause = new GtOperator(
				queryField, lowerTermQueryValue);
		}

		QueryClause upperTermQueryClause = null;

		String upperTermFieldValue = formatFieldValue(
			fieldName, termRangeQuery.getUpperTerm());

		QueryValue upperTermQueryValue = new QueryValue(upperTermFieldValue);

		if (termRangeQuery.includesUpper()) {
			upperTermQueryClause = new LeqOperator(
				queryField, upperTermQueryValue);
		}
		else {
			upperTermQueryClause = new LtOperator(
				queryField, upperTermQueryValue);
		}

		return new AndJoin(lowerTermQueryClause, upperTermQueryClause);
	}

	protected QueryClause traverseWildcardQuery(WildcardQuery wildcardQuery)
		throws SearchException {

		QueryTerm queryTerm = wildcardQuery.getQueryTerm();

		if (!isSupportedField(queryTerm.getField())) {
			return null;
		}

		return buildFieldQueryClause(
			queryTerm.getField(), queryTerm.getValue(),
			SharepointQueryOperator.LIKE);
	}

	private static final String _SHAREPOINT_DATE_FORMAT_PATTERN =
		"yyyy-MM-dd' 'HH:mm:ss";

	private static final Log _log = LogFactoryUtil.getLog(
		SharepointQueryBuilder.class);

	private static final LiferayQueryExplainer _liferayQueryExplainer =
		new LiferayQueryExplainer();
	private static final Map<String, String> _sharepointFields =
		HashMapBuilder.put(
			Field.CREATE_DATE, SharepointField.CREATE_DATE
		).put(
			Field.FOLDER_ID, SharepointField.FOLDER_PATH
		).put(
			Field.MODIFIED_DATE, SharepointField.MODIFIED_DATE
		).put(
			Field.NAME, SharepointField.NAME
		).put(
			Field.TITLE, SharepointField.NAME
		).put(
			Field.USER_ID, SharepointField.MODIFIED_BY
		).put(
			Field.USER_NAME, SharepointField.MODIFIED_BY
		).build();
	private static final Set<String> _supportedFields = new HashSet<>(
		Arrays.asList(
			Field.CREATE_DATE, Field.FOLDER_ID, Field.MODIFIED_DATE, Field.NAME,
			Field.TITLE, Field.USER_ID, Field.USER_NAME));

	private final ExtRepositoryQueryMapper _extRepositoryQueryMapper;
	private final
		com.liferay.sharepoint.soap.repository.connector.schema.query.Query
			_query;
	private final QueryOptionsList _queryOptionsList;
	private final SharepointConnectionInfo _sharepointConnectionInfo;
	private final SharepointWSRepository _sharepointWSRepository;

}