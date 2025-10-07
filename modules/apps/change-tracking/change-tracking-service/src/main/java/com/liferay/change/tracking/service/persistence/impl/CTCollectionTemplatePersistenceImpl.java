/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchCollectionTemplateException;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.model.CTCollectionTemplateTable;
import com.liferay.change.tracking.model.impl.CTCollectionTemplateImpl;
import com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl;
import com.liferay.change.tracking.service.persistence.CTCollectionTemplatePersistence;
import com.liferay.change.tracking.service.persistence.CTCollectionTemplateUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ct collection template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTCollectionTemplatePersistence.class)
public class CTCollectionTemplatePersistenceImpl
	extends BasePersistenceImpl<CTCollectionTemplate>
	implements CTCollectionTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTCollectionTemplateUtil</code> to access the ct collection template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTCollectionTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the ct collection templates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct collection templates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @return the range of matching ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ct collection templates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ct collection templates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByCompanyId;
				finderArgs = new Object[] {companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCompanyId;
			finderArgs = new Object[] {
				companyId, start, end, orderByComparator
			};
		}

		List<CTCollectionTemplate> list = null;

		if (useFinderCache) {
			list = (List<CTCollectionTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CTCollectionTemplate ctCollectionTemplate : list) {
					if (companyId != ctCollectionTemplate.getCompanyId()) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CTCollectionTemplateModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<CTCollectionTemplate>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection template
	 * @throws NoSuchCollectionTemplateException if a matching ct collection template could not be found
	 */
	@Override
	public CTCollectionTemplate findByCompanyId_First(
			long companyId,
			OrderByComparator<CTCollectionTemplate> orderByComparator)
		throws NoSuchCollectionTemplateException {

		CTCollectionTemplate ctCollectionTemplate = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (ctCollectionTemplate != null) {
			return ctCollectionTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCollectionTemplateException(sb.toString());
	}

	/**
	 * Returns the first ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection template, or <code>null</code> if a matching ct collection template could not be found
	 */
	@Override
	public CTCollectionTemplate fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		List<CTCollectionTemplate> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection template
	 * @throws NoSuchCollectionTemplateException if a matching ct collection template could not be found
	 */
	@Override
	public CTCollectionTemplate findByCompanyId_Last(
			long companyId,
			OrderByComparator<CTCollectionTemplate> orderByComparator)
		throws NoSuchCollectionTemplateException {

		CTCollectionTemplate ctCollectionTemplate = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (ctCollectionTemplate != null) {
			return ctCollectionTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCollectionTemplateException(sb.toString());
	}

	/**
	 * Returns the last ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct collection template, or <code>null</code> if a matching ct collection template could not be found
	 */
	@Override
	public CTCollectionTemplate fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CTCollectionTemplate> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ct collection templates before and after the current ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param ctCollectionTemplateId the primary key of the current ct collection template
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection template
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate[] findByCompanyId_PrevAndNext(
			long ctCollectionTemplateId, long companyId,
			OrderByComparator<CTCollectionTemplate> orderByComparator)
		throws NoSuchCollectionTemplateException {

		CTCollectionTemplate ctCollectionTemplate = findByPrimaryKey(
			ctCollectionTemplateId);

		Session session = null;

		try {
			session = openSession();

			CTCollectionTemplate[] array = new CTCollectionTemplateImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, ctCollectionTemplate, companyId, orderByComparator,
				true);

			array[1] = ctCollectionTemplate;

			array[2] = getByCompanyId_PrevAndNext(
				session, ctCollectionTemplate, companyId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTCollectionTemplate getByCompanyId_PrevAndNext(
		Session session, CTCollectionTemplate ctCollectionTemplate,
		long companyId,
		OrderByComparator<CTCollectionTemplate> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CTCollectionTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						ctCollectionTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTCollectionTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ct collection templates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ct collection templates that the user has permission to view
	 */
	@Override
	public List<CTCollectionTemplate> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct collection templates that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @return the range of matching ct collection templates that the user has permission to view
	 */
	@Override
	public List<CTCollectionTemplate> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ct collection templates that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collection templates that the user has permission to view
	 */
	@Override
	public List<CTCollectionTemplate> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					CTCollectionTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CTCollectionTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CTCollectionTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CTCollectionTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CTCollectionTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<CTCollectionTemplate>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the ct collection templates before and after the current ct collection template in the ordered set of ct collection templates that the user has permission to view where companyId = &#63;.
	 *
	 * @param ctCollectionTemplateId the primary key of the current ct collection template
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct collection template
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate[] filterFindByCompanyId_PrevAndNext(
			long ctCollectionTemplateId, long companyId,
			OrderByComparator<CTCollectionTemplate> orderByComparator)
		throws NoSuchCollectionTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				ctCollectionTemplateId, companyId, orderByComparator);
		}

		CTCollectionTemplate ctCollectionTemplate = findByPrimaryKey(
			ctCollectionTemplateId);

		Session session = null;

		try {
			session = openSession();

			CTCollectionTemplate[] array = new CTCollectionTemplateImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, ctCollectionTemplate, companyId, orderByComparator,
				true);

			array[1] = ctCollectionTemplate;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, ctCollectionTemplate, companyId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTCollectionTemplate filterGetByCompanyId_PrevAndNext(
		Session session, CTCollectionTemplate ctCollectionTemplate,
		long companyId,
		OrderByComparator<CTCollectionTemplate> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					CTCollectionTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CTCollectionTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CTCollectionTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CTCollectionTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CTCollectionTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						ctCollectionTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTCollectionTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ct collection templates where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CTCollectionTemplate ctCollectionTemplate :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ctCollectionTemplate);
		}
	}

	/**
	 * Returns the number of ct collection templates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct collection templates
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_CTCOLLECTIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the number of ct collection templates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct collection templates that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CTCollectionTemplate> ctCollectionTemplates = findByCompanyId(
				companyId);

			ctCollectionTemplates = InlineSQLHelperUtil.filter(
				ctCollectionTemplates);

			return ctCollectionTemplates.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_CTCOLLECTIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CTCollectionTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"ctCollectionTemplate.companyId = ?";

	public CTCollectionTemplatePersistenceImpl() {
		setModelClass(CTCollectionTemplate.class);

		setModelImplClass(CTCollectionTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(CTCollectionTemplateTable.INSTANCE);
	}

	/**
	 * Caches the ct collection template in the entity cache if it is enabled.
	 *
	 * @param ctCollectionTemplate the ct collection template
	 */
	@Override
	public void cacheResult(CTCollectionTemplate ctCollectionTemplate) {
		entityCache.putResult(
			CTCollectionTemplateImpl.class,
			ctCollectionTemplate.getPrimaryKey(), ctCollectionTemplate);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the ct collection templates in the entity cache if it is enabled.
	 *
	 * @param ctCollectionTemplates the ct collection templates
	 */
	@Override
	public void cacheResult(List<CTCollectionTemplate> ctCollectionTemplates) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ctCollectionTemplates.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CTCollectionTemplate ctCollectionTemplate :
				ctCollectionTemplates) {

			if (entityCache.getResult(
					CTCollectionTemplateImpl.class,
					ctCollectionTemplate.getPrimaryKey()) == null) {

				cacheResult(ctCollectionTemplate);
			}
		}
	}

	/**
	 * Clears the cache for all ct collection templates.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CTCollectionTemplateImpl.class);

		finderCache.clearCache(CTCollectionTemplateImpl.class);
	}

	/**
	 * Clears the cache for the ct collection template.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CTCollectionTemplate ctCollectionTemplate) {
		entityCache.removeResult(
			CTCollectionTemplateImpl.class, ctCollectionTemplate);
	}

	@Override
	public void clearCache(List<CTCollectionTemplate> ctCollectionTemplates) {
		for (CTCollectionTemplate ctCollectionTemplate :
				ctCollectionTemplates) {

			entityCache.removeResult(
				CTCollectionTemplateImpl.class, ctCollectionTemplate);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CTCollectionTemplateImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CTCollectionTemplateImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new ct collection template with the primary key. Does not add the ct collection template to the database.
	 *
	 * @param ctCollectionTemplateId the primary key for the new ct collection template
	 * @return the new ct collection template
	 */
	@Override
	public CTCollectionTemplate create(long ctCollectionTemplateId) {
		CTCollectionTemplate ctCollectionTemplate =
			new CTCollectionTemplateImpl();

		ctCollectionTemplate.setNew(true);
		ctCollectionTemplate.setPrimaryKey(ctCollectionTemplateId);

		ctCollectionTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctCollectionTemplate;
	}

	/**
	 * Removes the ct collection template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template that was removed
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate remove(long ctCollectionTemplateId)
		throws NoSuchCollectionTemplateException {

		return remove((Serializable)ctCollectionTemplateId);
	}

	/**
	 * Removes the ct collection template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ct collection template
	 * @return the ct collection template that was removed
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate remove(Serializable primaryKey)
		throws NoSuchCollectionTemplateException {

		Session session = null;

		try {
			session = openSession();

			CTCollectionTemplate ctCollectionTemplate =
				(CTCollectionTemplate)session.get(
					CTCollectionTemplateImpl.class, primaryKey);

			if (ctCollectionTemplate == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCollectionTemplateException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ctCollectionTemplate);
		}
		catch (NoSuchCollectionTemplateException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected CTCollectionTemplate removeImpl(
		CTCollectionTemplate ctCollectionTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctCollectionTemplate)) {
				ctCollectionTemplate = (CTCollectionTemplate)session.get(
					CTCollectionTemplateImpl.class,
					ctCollectionTemplate.getPrimaryKeyObj());
			}

			if (ctCollectionTemplate != null) {
				session.delete(ctCollectionTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctCollectionTemplate != null) {
			clearCache(ctCollectionTemplate);
		}

		return ctCollectionTemplate;
	}

	@Override
	public CTCollectionTemplate updateImpl(
		CTCollectionTemplate ctCollectionTemplate) {

		boolean isNew = ctCollectionTemplate.isNew();

		if (!(ctCollectionTemplate instanceof CTCollectionTemplateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctCollectionTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ctCollectionTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctCollectionTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTCollectionTemplate implementation " +
					ctCollectionTemplate.getClass());
		}

		CTCollectionTemplateModelImpl ctCollectionTemplateModelImpl =
			(CTCollectionTemplateModelImpl)ctCollectionTemplate;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ctCollectionTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				ctCollectionTemplate.setCreateDate(date);
			}
			else {
				ctCollectionTemplate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!ctCollectionTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ctCollectionTemplate.setModifiedDate(date);
			}
			else {
				ctCollectionTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctCollectionTemplate);
			}
			else {
				ctCollectionTemplate = (CTCollectionTemplate)session.merge(
					ctCollectionTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CTCollectionTemplateImpl.class, ctCollectionTemplateModelImpl,
			false, true);

		if (isNew) {
			ctCollectionTemplate.setNew(false);
		}

		ctCollectionTemplate.resetOriginalValues();

		return ctCollectionTemplate;
	}

	/**
	 * Returns the ct collection template with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ct collection template
	 * @return the ct collection template
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCollectionTemplateException {

		CTCollectionTemplate ctCollectionTemplate = fetchByPrimaryKey(
			primaryKey);

		if (ctCollectionTemplate == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCollectionTemplateException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ctCollectionTemplate;
	}

	/**
	 * Returns the ct collection template with the primary key or throws a <code>NoSuchCollectionTemplateException</code> if it could not be found.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate findByPrimaryKey(long ctCollectionTemplateId)
		throws NoSuchCollectionTemplateException {

		return findByPrimaryKey((Serializable)ctCollectionTemplateId);
	}

	/**
	 * Returns the ct collection template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template, or <code>null</code> if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate fetchByPrimaryKey(long ctCollectionTemplateId) {
		return fetchByPrimaryKey((Serializable)ctCollectionTemplateId);
	}

	/**
	 * Returns all the ct collection templates.
	 *
	 * @return the ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct collection templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @return the range of ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the ct collection templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findAll(
		int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ct collection templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findAll(
		int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<CTCollectionTemplate> list = null;

		if (useFinderCache) {
			list = (List<CTCollectionTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_CTCOLLECTIONTEMPLATE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_CTCOLLECTIONTEMPLATE;

				sql = sql.concat(CTCollectionTemplateModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CTCollectionTemplate>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the ct collection templates from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CTCollectionTemplate ctCollectionTemplate : findAll()) {
			remove(ctCollectionTemplate);
		}
	}

	/**
	 * Returns the number of ct collection templates.
	 *
	 * @return the number of ct collection templates
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_CTCOLLECTIONTEMPLATE);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctCollectionTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTCOLLECTIONTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTCollectionTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct collection template persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		CTCollectionTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTCollectionTemplateUtil.setPersistence(null);

		entityCache.removeCache(CTCollectionTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CTCOLLECTIONTEMPLATE =
		"SELECT ctCollectionTemplate FROM CTCollectionTemplate ctCollectionTemplate";

	private static final String _SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE =
		"SELECT ctCollectionTemplate FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String _SQL_COUNT_CTCOLLECTIONTEMPLATE =
		"SELECT COUNT(ctCollectionTemplate) FROM CTCollectionTemplate ctCollectionTemplate";

	private static final String _SQL_COUNT_CTCOLLECTIONTEMPLATE_WHERE =
		"SELECT COUNT(ctCollectionTemplate) FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"ctCollectionTemplate.ctCollectionTemplateId";

	private static final String _FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE =
		"SELECT DISTINCT {ctCollectionTemplate.*} FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CTCollectionTemplate.*} FROM (SELECT DISTINCT ctCollectionTemplate.ctCollectionTemplateId FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CTCOLLECTIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CTCollectionTemplate ON TEMP_TABLE.ctCollectionTemplateId = CTCollectionTemplate.ctCollectionTemplateId";

	private static final String _FILTER_SQL_COUNT_CTCOLLECTIONTEMPLATE_WHERE =
		"SELECT COUNT(DISTINCT ctCollectionTemplate.ctCollectionTemplateId) AS COUNT_VALUE FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "ctCollectionTemplate";

	private static final String _FILTER_ENTITY_TABLE = "CTCollectionTemplate";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"ctCollectionTemplate.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"CTCollectionTemplate.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CTCollectionTemplate exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTCollectionTemplate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTCollectionTemplatePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}