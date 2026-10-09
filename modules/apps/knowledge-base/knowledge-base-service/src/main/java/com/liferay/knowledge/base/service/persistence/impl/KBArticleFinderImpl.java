/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.impl;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBArticleTable;
import com.liferay.knowledge.base.model.impl.KBArticleImpl;
import com.liferay.knowledge.base.service.persistence.KBArticleFinder;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = KBArticleFinder.class)
public class KBArticleFinderImpl
	extends KBArticleFinderBaseImpl implements KBArticleFinder {

	@Override
	public int countByUrlTitle(
		long groupId, String kbFolderUrlTitle, String kbArticleUrlTitle,
		int[] status) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				KBArticleFinderImpl.class, _COUNT_BY_URL_TITLE);

			sql = replaceWorkflowStatus(sql, status);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(kbArticleUrlTitle);
			queryPos.add(kbFolderUrlTitle);

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public int filterCountByKeywords(
		long groupId, String keywords, int status) {

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				_getOrderByStep(
					DSLQueryFactoryUtil.select(
						DSLFunctionFactoryUtil.count(
							KBArticleTable.INSTANCE.kbArticleId
						).as(
							COUNT_COLUMN_NAME
						)),
					groupId, keywords, status));

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<KBArticle> filterFindByKeywords(
		long groupId, String keywords, int status, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			OrderByStep orderByStep = _getOrderByStep(
				DSLQueryFactoryUtil.select(KBArticleTable.INSTANCE), groupId,
				keywords, status);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				orderByStep.orderBy(
					KBArticleTable.INSTANCE.priority.ascending()));

			sqlQuery.addEntity("KBArticle", KBArticleImpl.class);

			return (List<KBArticle>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<KBArticle> findByUrlTitle(
		long groupId, String kbFolderUrlTitle, String kbArticleUrlTitle,
		int[] status, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				KBArticleFinderImpl.class, _FIND_BY_URL_TITLE);

			sql = replaceWorkflowStatus(sql, status);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(KBArticleImpl.TABLE_NAME, KBArticleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(kbArticleUrlTitle);
			queryPos.add(kbFolderUrlTitle);

			return (List)QueryUtil.list(sqlQuery, getDialect(), start, end);
		}
		finally {
			closeSession(session);
		}
	}

	protected String replaceWorkflowStatus(String sql, int[] status) {
		StringBundler sb = new StringBundler(status.length);

		for (int i = 0; i < status.length; i++) {
			sb.append(status[i]);

			if (i == (status.length - 1)) {
				continue;
			}

			sb.append(", ");
		}

		return StringUtil.replace(sql, "[$WORKFLOW_STATUS$]", sb.toString());
	}

	private Predicate _getKeywordsPredicate(
		Expression<String> expression, String[] keywords) {

		Predicate keywordsPredicate = null;

		expression = DSLFunctionFactoryUtil.lower(expression);

		for (String keyword : keywords) {
			if (keyword == null) {
				continue;
			}

			Predicate keywordPredicate = expression.like(keyword);

			if (keywordsPredicate == null) {
				keywordsPredicate = keywordPredicate;
			}
			else {
				keywordsPredicate = keywordsPredicate.or(keywordPredicate);
			}
		}

		return keywordsPredicate;
	}

	private OrderByStep _getOrderByStep(
		FromStep fromStep, long groupId, String keywords, int status) {

		return fromStep.from(
			KBArticleTable.INSTANCE
		).where(
			KBArticleTable.INSTANCE.groupId.eq(
				groupId
			).and(
				InlineSQLHelperUtil.getPermissionWherePredicate(
					KBArticle.class, KBArticleTable.INSTANCE.kbArticleId,
					groupId)
			).and(
				() -> {
					if (Validator.isNull(keywords)) {
						return null;
					}

					String[] keywordsArray = _customSQL.keywords(
						keywords, true);

					return Predicate.withParentheses(
						_getKeywordsPredicate(
							KBArticleTable.INSTANCE.title, keywordsArray
						).or(
							_getKeywordsPredicate(
								DSLFunctionFactoryUtil.castClobText(
									KBArticleTable.INSTANCE.content),
								keywordsArray)
						));
				}
			).and(
				() -> {
					if (status == WorkflowConstants.STATUS_ANY) {
						return KBArticleTable.INSTANCE.latest.eq(
							Boolean.TRUE
						).and(
							KBArticleTable.INSTANCE.status.neq(
								WorkflowConstants.STATUS_IN_TRASH)
						);
					}

					return KBArticleTable.INSTANCE.status.eq(status);
				}
			)
		);
	}

	private static final String _COUNT_BY_URL_TITLE =
		KBArticleFinder.class.getName() + ".countByUrlTitle";

	private static final String _FIND_BY_URL_TITLE =
		KBArticleFinder.class.getName() + ".findByUrlTitle";

	@Reference
	private CustomSQL _customSQL;

}