/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Connor McKay
 * @author Marcellus Tavares
 * @author Juan Fernández
 */
@Component(service = DDMTemplateFinder.class)
public class DDMTemplateFinderImpl
	extends DDMTemplateFinderBaseImpl implements DDMTemplateFinder {

	public static final String COUNT_BY_G_C_SC_S =
		DDMTemplateFinder.class.getName() + ".countByG_C_SC_S";

	public static final String COUNT_BY_C_G_C_C_R_N_D_T_M_L_S =
		DDMTemplateFinder.class.getName() + ".countByC_G_C_C_R_N_D_T_M_L_S";

	public static final String FIND_BY_G_C_SC_S =
		DDMTemplateFinder.class.getName() + ".findByG_C_SC_S";

	public static final String FIND_BY_C_G_C_C_R_N_D_T_M_L_S =
		DDMTemplateFinder.class.getName() + ".findByC_G_C_C_R_N_D_T_M_L_S";

	@Override
	public int filterCountByG_SC_S(
		long groupId, long structureClassNameId, int status) {

		return _countByG_C_SC_S(
			new long[] {groupId}, _portal.getClassNameId(DDMStructure.class),
			structureClassNameId, status);
	}

	@Override
	public int filterCountByC_G_C_C_R_T_M_S(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String type, String mode, int status) {

		String[] types = _customSQL.keywords(type, false);
		String[] modes = _customSQL.keywords(mode, false);

		return _countByC_G_C_C_R_N_D_T_M_L_S(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			null, null, types, modes, null, status, true);
	}

	@Override
	public List<DDMTemplate> filterFindByG_SC_S(
		long groupId, long structureClassNameId, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _findByG_C_SC_S(
			new long[] {groupId}, _portal.getClassNameId(DDMStructure.class),
			structureClassNameId, status, start, end, orderByComparator, true);
	}

	@Override
	public List<DDMTemplate> filterFindByC_G_C_C_R_T_M_S(
		long companyId, long[] groupIds, long classNameId, long classPK,
		long resourceClassNameId, String type, String mode, int status,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		String[] types = _customSQL.keywords(type, false);
		String[] modes = _customSQL.keywords(mode, false);

		return _findByC_G_C_C_R_N_D_T_M_L_S(
			companyId, groupIds, new long[] {classNameId}, new long[] {classPK},
			resourceClassNameId, null, null, types, modes, null, status, true,
			start, end, orderByComparator, true);
	}

	@Override
	public List<DDMTemplate> filterFindByC_G_C_C_R_T_M_S(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String type, String mode, int status,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		String[] types = _customSQL.keywords(type, false);
		String[] modes = _customSQL.keywords(mode, false);

		return _findByC_G_C_C_R_N_D_T_M_L_S(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			null, null, types, modes, null, status, true, start, end,
			orderByComparator, true);
	}

	@Override
	public List<DDMTemplate> findByG_SC_S(
		long groupId, long structureClassNameId, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _findByG_C_SC_S(
			new long[] {groupId}, _portal.getClassNameId(DDMStructure.class),
			structureClassNameId, status, start, end, orderByComparator, false);
	}

	@Override
	public List<DDMTemplate> findByC_G_C_C_R_T_M_S(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String type, String mode, int status,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		String[] types = _customSQL.keywords(type, false);
		String[] modes = _customSQL.keywords(mode, false);

		return _findByC_G_C_C_R_N_D_T_M_L_S(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			null, null, types, modes, null, status, true, start, end,
			orderByComparator, false);
	}

	private int _countByG_C_SC_S(
		long[] groupIds, long classNameId, long structureClassNameId,
		int status) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_C_SC_S);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql,
				_ddmPermissionSupport.getTemplateModelResourceName(
					structureClassNameId),
				"DDMTemplate.templateId", groupIds);
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", _getGroupIds(groupIds));
			sql = StringUtil.replace(sql, "[$STATUS$]", _getStatus(status));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupIds != null) {
				queryPos.add(groupIds);
			}

			queryPos.add(classNameId);
			queryPos.add(structureClassNameId);

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

	private int _countByC_G_C_C_R_N_D_T_M_L_S(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String[] names, String[] descriptions,
		String[] types, String[] modes, String[] languages, int status,
		boolean andOperator) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);
		types = _customSQL.keywords(types, false);
		modes = _customSQL.keywords(modes, false);
		languages = _customSQL.keywords(languages, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_C_G_C_C_R_N_D_T_M_L_S);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql,
				_ddmPermissionSupport.getTemplateModelResourceName(
					resourceClassNameId),
				"DDMTemplate.templateId", groupIds);
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", _getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CLASSNAME_ID$]", _getClassNameIds(classNameIds));
			sql = StringUtil.replace(
				sql, "[$CLASS_PK$]", _getClassPKs(classPKs));
			sql = StringUtil.replace(sql, "[$STATUS$]", _getStatus(status));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(DDMTemplate.name))", StringPool.LIKE,
				false, names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.description", StringPool.LIKE, false,
				descriptions);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.type", StringPool.LIKE, false, types);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.mode", StringPool.LIKE, false, modes);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.language", StringPool.LIKE, true, languages);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (groupIds != null) {
				queryPos.add(groupIds);
			}

			if (classNameIds != null) {
				queryPos.add(classNameIds);
			}

			if (classPKs != null) {
				queryPos.add(classPKs);
			}

			queryPos.add(resourceClassNameId);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(types, 2);
			queryPos.add(modes, 2);
			queryPos.add(languages, 2);

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

	private List<DDMTemplate> _findByG_C_SC_S(
		long[] groupIds, long classNameId, long structureClassNameId,
		int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_C_SC_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql,
					_ddmPermissionSupport.getTemplateModelResourceName(
						structureClassNameId),
					"DDMTemplate.templateId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", _getGroupIds(groupIds));
			sql = StringUtil.replace(sql, "[$STATUS$]", _getStatus(status));

			if (orderByComparator != null) {
				sql = _customSQL.replaceOrderBy(sql, orderByComparator);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DDMTemplate", DDMTemplateImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupIds != null) {
				queryPos.add(groupIds);
			}

			queryPos.add(classNameId);
			queryPos.add(structureClassNameId);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
			}

			return (List<DDMTemplate>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private List<DDMTemplate> _findByC_G_C_C_R_N_D_T_M_L_S(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String[] names, String[] descriptions,
		String[] types, String[] modes, String[] languages, int status,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean inlineSQLHelper) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);
		types = _customSQL.keywords(types, false);
		modes = _customSQL.keywords(modes, false);
		languages = _customSQL.keywords(languages, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_C_G_C_C_R_N_D_T_M_L_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql,
					_ddmPermissionSupport.getTemplateModelResourceName(
						resourceClassNameId),
					"DDMTemplate.templateId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", _getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CLASSNAME_ID$]", _getClassNameIds(classNameIds));
			sql = StringUtil.replace(
				sql, "[$CLASS_PK$]", _getClassPKs(classPKs));
			sql = StringUtil.replace(sql, "[$STATUS$]", _getStatus(status));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(DDMTemplate.name))", StringPool.LIKE,
				false, names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.description", StringPool.LIKE, false,
				descriptions);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.type", StringPool.LIKE, false, types);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.mode", StringPool.LIKE, false, modes);
			sql = _customSQL.replaceKeywords(
				sql, "DDMTemplate.language", StringPool.LIKE, true, languages);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			if (orderByComparator != null) {
				sql = _customSQL.replaceOrderBy(sql, orderByComparator);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DDMTemplate", DDMTemplateImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (groupIds != null) {
				queryPos.add(groupIds);
			}

			if (classNameIds != null) {
				queryPos.add(classNameIds);
			}

			if (classPKs != null) {
				queryPos.add(classPKs);
			}

			queryPos.add(resourceClassNameId);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(types, 2);
			queryPos.add(modes, 2);
			queryPos.add(languages, 2);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
			}

			return (List<DDMTemplate>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private String _getClassNameIds(long[] classNameIds) {
		if (ArrayUtil.isEmpty(classNameIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(classNameIds.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (classNameIds.length - 1); i++) {
			sb.append("classNameId = ? OR ");
		}

		sb.append("classNameId = ?) AND");

		return sb.toString();
	}

	private String _getClassPKs(long[] classPKs) {
		if (ArrayUtil.isEmpty(classPKs)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(classPKs.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (classPKs.length - 1); i++) {
			sb.append("classPK = ? OR ");
		}

		sb.append("classPK = ?) AND");

		return sb.toString();
	}

	private String _getGroupIds(long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(groupIds.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (groupIds.length - 1); i++) {
			sb.append("groupId = ? OR ");
		}

		sb.append("groupId = ?) AND");

		return sb.toString();
	}

	private String _getStatus(int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			"AND EXISTS (SELECT 1 FROM DDMTemplateVersion WHERE ",
			"(DDMTemplateVersion.templateId = DDMTemplate.templateId) AND ",
			"(DDMTemplateVersion.version = DDMTemplate.version) AND ",
			"(DDMTemplateVersion.status = ?))");
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference
	private Portal _portal;

}