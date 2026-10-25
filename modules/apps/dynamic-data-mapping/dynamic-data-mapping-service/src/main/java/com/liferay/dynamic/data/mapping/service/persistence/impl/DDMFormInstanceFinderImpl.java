/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceFinder;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(service = DDMFormInstanceFinder.class)
public class DDMFormInstanceFinderImpl
	extends DDMFormInstanceFinderBaseImpl implements DDMFormInstanceFinder {

	public static final String COUNT_BY_C_G_N_D =
		DDMFormInstanceFinder.class.getName() + ".countByC_G_N_D";

	public static final String COUNT_BY_C_G_N_D_S =
		DDMFormInstanceFinder.class.getName() + ".countByC_G_N_D_S";

	public static final String FIND_BY_C_G_N_D =
		DDMFormInstanceFinder.class.getName() + ".findByC_G_N_D";

	public static final String FIND_BY_C_G_N_D_S =
		DDMFormInstanceFinder.class.getName() + ".findByC_G_N_D_S";

	@Override
	public int filterCountByKeywords(
		long companyId, long groupId, String keywords) {

		return _countByKeywords(companyId, groupId, keywords);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long groupId, String keywords, int status) {

		return _countByKeywords(companyId, groupId, keywords, status);
	}

	@Override
	public int filterCountByC_G(long companyId, long groupId) {
		return filterCountByKeywords(companyId, groupId, null);
	}

	@Override
	public int filterCountByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator) {

		return _countByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator);
	}

	@Override
	public List<DDMFormInstance> filterFindByKeywords(
		long companyId, long groupId, String keywords, int status, int start,
		int end, OrderByComparator<DDMFormInstance> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return filterFindByC_G_N_D_S(
			companyId, groupId, names, descriptions, status, andOperator, start,
			end, orderByComparator);
	}

	@Override
	public List<DDMFormInstance> filterFindByKeywords(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return filterFindByKeywords(
			companyId, groupId, keywords, WorkflowConstants.STATUS_ANY, start,
			end, orderByComparator);
	}

	@Override
	public List<DDMFormInstance> filterFindByC_G(
		long companyId, long groupId, int start, int end) {

		return filterFindByKeywords(companyId, groupId, null, start, end, null);
	}

	@Override
	public List<DDMFormInstance> filterFindByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return _findByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator, true);
	}

	@Override
	public List<DDMFormInstance> filterFindByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int status, boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return _findByC_G_N_D_S(
			companyId, groupId, names, descriptions, status, andOperator, start,
			end, orderByComparator, true);
	}

	@Override
	public List<DDMFormInstance> findByKeywords(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return _findByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator, false);
	}

	private int _countByKeywords(
		long companyId, long groupId, String keywords) {

		return _countByKeywords(
			companyId, groupId, keywords, WorkflowConstants.STATUS_ANY);
	}

	private int _countByKeywords(
		long companyId, long groupId, String keywords, int status) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return _countByC_G_N_D_S(
			companyId, groupId, names, descriptions, status, andOperator);
	}

	private int _countByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator) {

		return _countByC_G_N_D_S(
			companyId, groupId, names, descriptions,
			WorkflowConstants.STATUS_ANY, andOperator);
	}

	private int _countByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int status, boolean andOperator) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = "";

			if (status == WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.get(getClass(), COUNT_BY_C_G_N_D);
			}
			else {
				sql = _customSQL.get(getClass(), COUNT_BY_C_G_N_D_S);
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, DDMFormInstance.class.getName(),
				"DDMFormInstance.formInstanceId", groupId);

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(DDMFormInstance.groupId = ?) AND");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDMFormInstance.name)", StringPool.LIKE, false,
				names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMFormInstance.description", StringPool.LIKE, true,
				descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			queryPos.add(companyId);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
			}

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

	private List<DDMFormInstance> _findByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean inlineSQLHelper) {

		return _findByC_G_N_D_S(
			companyId, groupId, names, descriptions,
			WorkflowConstants.STATUS_ANY, andOperator, start, end,
			orderByComparator, inlineSQLHelper);
	}

	private List<DDMFormInstance> _findByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int status, boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean inlineSQLHelper) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = "";

			if (status == WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.get(getClass(), FIND_BY_C_G_N_D);
			}
			else {
				sql = _customSQL.get(getClass(), FIND_BY_C_G_N_D_S);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DDMFormInstance.class.getName(),
					"DDMFormInstance.formInstanceId", groupId);
			}

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(DDMFormInstance.groupId = ?) AND");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDMFormInstance.name)", StringPool.LIKE, false,
				names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMFormInstance.description", StringPool.LIKE, true,
				descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			boolean orderByName =
				orderByComparator instanceof DDMFormInstanceNameComparator;

			if (!orderByName) {
				sql = _customSQL.replaceOrderBy(sql, orderByComparator);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DDMFormInstance", DDMFormInstanceImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			queryPos.add(companyId);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
			}

			if (!orderByName) {
				return (List<DDMFormInstance>)QueryUtil.list(
					sqlQuery, getDialect(), start, end);
			}

			return ListUtil.subList(
				ListUtil.sort(
					(List<DDMFormInstance>)QueryUtil.list(
						sqlQuery, getDialect(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS),
					orderByComparator),
				start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Reference
	private CustomSQL _customSQL;

}