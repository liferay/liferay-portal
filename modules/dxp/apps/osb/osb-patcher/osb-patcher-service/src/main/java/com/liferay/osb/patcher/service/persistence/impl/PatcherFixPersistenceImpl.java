/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixTable;
import com.liferay.osb.patcher.model.impl.PatcherFixImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher fix service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixPersistence.class)
public class PatcherFixPersistenceImpl
	extends BasePersistenceImpl<PatcherFix> implements PatcherFixPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixUtil</code> to access the patcher fix persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByPatcherProjectVersionId;
	private FinderPath
		_finderPathWithoutPaginationFindByPatcherProjectVersionId;
	private FinderPath _finderPathCountByPatcherProjectVersionId;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return findByPatcherProjectVersionId(
			patcherProjectVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByPatcherProjectVersionId;
				finderArgs = new Object[] {patcherProjectVersionId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPatcherProjectVersionId;
			finderArgs = new Object[] {
				patcherProjectVersionId, start, end, orderByComparator
			};
		}

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if (patcherProjectVersionId !=
							patcherFix.getPatcherProjectVersionId()) {

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

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByPatcherProjectVersionId_First(
			patcherProjectVersionId, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByPatcherProjectVersionId(
			patcherProjectVersionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByPatcherProjectVersionId_Last(
			long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByPatcherProjectVersionId_Last(
			patcherProjectVersionId, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByPatcherProjectVersionId_Last(
		long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByPatcherProjectVersionId(patcherProjectVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByPatcherProjectVersionId(
			patcherProjectVersionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByPatcherProjectVersionId_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByPatcherProjectVersionId_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = getByPatcherProjectVersionId_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, orderByComparator,
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

	protected PatcherFix getByPatcherProjectVersionId_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherProjectVersionId(
				patcherProjectVersionId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPatcherProjectVersionId(
					patcherProjectVersionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByPatcherProjectVersionId_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherProjectVersionId_PrevAndNext(
				patcherFixId, patcherProjectVersionId, orderByComparator);
		}

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByPatcherProjectVersionId_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = filterGetByPatcherProjectVersionId_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, orderByComparator,
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

	protected PatcherFix filterGetByPatcherProjectVersionId_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	@Override
	public void removeByPatcherProjectVersionId(long patcherProjectVersionId) {
		for (PatcherFix patcherFix :
				findByPatcherProjectVersionId(
					patcherProjectVersionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByPatcherProjectVersionId(long patcherProjectVersionId) {
		FinderPath finderPath = _finderPathCountByPatcherProjectVersionId;

		Object[] finderArgs = new Object[] {patcherProjectVersionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

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
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPatcherProjectVersionId(patcherProjectVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByPatcherProjectVersionId(
				patcherProjectVersionId);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

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

	private static final String
		_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2 =
			"patcherFix.patcherProjectVersionId = ?";

	private FinderPath _finderPathWithPaginationFindByP_L_T;
	private FinderPath _finderPathWithoutPaginationFindByP_L_T;
	private FinderPath _finderPathCountByP_L_T;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return findByP_L_T(
			patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return findByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_L_T;
				finderArgs = new Object[] {
					patcherProjectVersionId, latestFix, type
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_L_T;
			finderArgs = new Object[] {
				patcherProjectVersionId, latestFix, type, start, end,
				orderByComparator
			};
		}

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if ((patcherProjectVersionId !=
							patcherFix.getPatcherProjectVersionId()) ||
						(latestFix != patcherFix.isLatestFix()) ||
						(type != patcherFix.getType())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_T_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_P_L_T_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				queryPos.add(type);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_T_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_T_First(
			patcherProjectVersionId, latestFix, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_T_First(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByP_L_T(
			patcherProjectVersionId, latestFix, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_T_Last(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_T_Last(
			patcherProjectVersionId, latestFix, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_T_Last(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByP_L_T(patcherProjectVersionId, latestFix, type);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByP_L_T(
			patcherProjectVersionId, latestFix, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByP_L_T_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByP_L_T_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, true);

			array[1] = patcherFix;

			array[2] = getByP_L_T_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix getByP_L_T_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_T_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_T_TYPE_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return filterFindByP_L_T(
			patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return filterFindByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_T(
				patcherProjectVersionId, latestFix, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_L_T(
					patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_T_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			queryPos.add(type);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByP_L_T_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_T_PrevAndNext(
				patcherFixId, patcherProjectVersionId, latestFix, type,
				orderByComparator);
		}

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByP_L_T_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, true);

			array[1] = patcherFix;

			array[2] = filterGetByP_L_T_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix filterGetByP_L_T_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_T_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	@Override
	public void removeByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		for (PatcherFix patcherFix :
				findByP_L_T(
					patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		FinderPath finderPath = _finderPathCountByP_L_T;

		Object[] finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_T_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_P_L_T_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				queryPos.add(type);

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
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_L_T(patcherProjectVersionId, latestFix, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByP_L_T(
				patcherProjectVersionId, latestFix, type);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_T_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_P_L_T_PATCHERPROJECTVERSIONID_2 =
		"patcherFix.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_P_L_T_LATESTFIX_2 =
		"patcherFix.latestFix = ? AND ";

	private static final String _FINDER_COLUMN_P_L_T_TYPE_2 =
		"patcherFix.type = ?";

	private static final String _FINDER_COLUMN_P_L_T_TYPE_2_SQL =
		"patcherFix.type_ = ?";

	private FinderPath _finderPathWithPaginationFindByP_L_NotT;
	private FinderPath _finderPathWithPaginationCountByP_L_NotT;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByP_L_NotT;
		finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if ((patcherProjectVersionId !=
							patcherFix.getPatcherProjectVersionId()) ||
						(latestFix != patcherFix.isLatestFix()) ||
						(type == patcherFix.getType())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				queryPos.add(type);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_NotT_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_NotT_First(
			patcherProjectVersionId, latestFix, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_NotT_First(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_NotT_Last(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_NotT_Last(
			patcherProjectVersionId, latestFix, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_NotT_Last(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByP_L_NotT(patcherProjectVersionId, latestFix, type);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByP_L_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByP_L_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, true);

			array[1] = patcherFix;

			array[2] = getByP_L_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix getByP_L_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_TYPE_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_NotT(
				patcherProjectVersionId, latestFix, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_L_NotT(
					patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			queryPos.add(type);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByP_L_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_NotT_PrevAndNext(
				patcherFixId, patcherProjectVersionId, latestFix, type,
				orderByComparator);
		}

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByP_L_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, true);

			array[1] = patcherFix;

			array[2] = filterGetByP_L_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix filterGetByP_L_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	@Override
	public void removeByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		for (PatcherFix patcherFix :
				findByP_L_NotT(
					patcherProjectVersionId, latestFix, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		FinderPath finderPath = _finderPathWithPaginationCountByP_L_NotT;

		Object[] finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				queryPos.add(type);

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
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_L_NotT(patcherProjectVersionId, latestFix, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByP_L_NotT(
				patcherProjectVersionId, latestFix, type);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			queryPos.add(type);

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

	private static final String
		_FINDER_COLUMN_P_L_NOTT_PATCHERPROJECTVERSIONID_2 =
			"patcherFix.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_P_L_NOTT_LATESTFIX_2 =
		"patcherFix.latestFix = ? AND ";

	private static final String _FINDER_COLUMN_P_L_NOTT_TYPE_2 =
		"patcherFix.type != ?";

	private static final String _FINDER_COLUMN_P_L_NOTT_TYPE_2_SQL =
		"patcherFix.type_ != ?";

	private FinderPath _finderPathWithPaginationFindByK_GtKV_NotT;
	private FinderPath _finderPathWithPaginationCountByK_GtKV_NotT;

	/**
	 * Returns all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return findByK_GtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return findByK_GtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByK_GtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByK_GtKV_NotT;
		finderArgs = new Object[] {
			key, keyVersion, type, start, end, orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if (!key.equals(patcherFix.getKey()) ||
						(keyVersion >= patcherFix.getKeyVersion()) ||
						(type == patcherFix.getType())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2);

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(keyVersion);

				queryPos.add(type);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_GtKV_NotT_First(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByK_GtKV_NotT_First(
			key, keyVersion, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion>");
		sb.append(keyVersion);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_GtKV_NotT_First(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByK_GtKV_NotT(
			key, keyVersion, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_GtKV_NotT_Last(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByK_GtKV_NotT_Last(
			key, keyVersion, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion>");
		sb.append(keyVersion);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_GtKV_NotT_Last(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByK_GtKV_NotT(key, keyVersion, type);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByK_GtKV_NotT(
			key, keyVersion, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByK_GtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		key = Objects.toString(key, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByK_GtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = getByK_GtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
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

	protected PatcherFix getByK_GtKV_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, String key, double keyVersion,
		int type, OrderByComparator<PatcherFix> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_2);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_TYPE_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(keyVersion);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return filterFindByK_GtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return filterFindByK_GtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_GtKV_NotT(
				key, keyVersion, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByK_GtKV_NotT(
					key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(keyVersion);

			queryPos.add(type);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByK_GtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_GtKV_NotT_PrevAndNext(
				patcherFixId, key, keyVersion, type, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByK_GtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = filterGetByK_GtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
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

	protected PatcherFix filterGetByK_GtKV_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, String key, double keyVersion,
		int type, OrderByComparator<PatcherFix> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(keyVersion);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 */
	@Override
	public void removeByK_GtKV_NotT(String key, double keyVersion, int type) {
		for (PatcherFix patcherFix :
				findByK_GtKV_NotT(
					key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByK_GtKV_NotT(String key, double keyVersion, int type) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathWithPaginationCountByK_GtKV_NotT;

		Object[] finderArgs = new Object[] {key, keyVersion, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2);

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(keyVersion);

				queryPos.add(type);

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
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByK_GtKV_NotT(key, keyVersion, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByK_GtKV_NotT(
				key, keyVersion, type);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_GTKV_NOTT_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(keyVersion);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_KEY_2 =
		"patcherFix.key = ? AND ";

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_KEY_3 =
		"(patcherFix.key IS NULL OR patcherFix.key = '') AND ";

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_KEY_2_SQL =
		"patcherFix.key_ = ? AND ";

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_KEY_3_SQL =
		"(patcherFix.key_ IS NULL OR patcherFix.key_ = '') AND ";

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_KEYVERSION_2 =
		"patcherFix.keyVersion > ? AND ";

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_TYPE_2 =
		"patcherFix.type != ?";

	private static final String _FINDER_COLUMN_K_GTKV_NOTT_TYPE_2_SQL =
		"patcherFix.type_ != ?";

	private FinderPath _finderPathWithPaginationFindByK_LtKV_NotT;
	private FinderPath _finderPathWithPaginationCountByK_LtKV_NotT;

	/**
	 * Returns all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return findByK_LtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return findByK_LtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByK_LtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByK_LtKV_NotT;
		finderArgs = new Object[] {
			key, keyVersion, type, start, end, orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if (!key.equals(patcherFix.getKey()) ||
						(keyVersion <= patcherFix.getKeyVersion()) ||
						(type == patcherFix.getType())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2);

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(keyVersion);

				queryPos.add(type);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_LtKV_NotT_First(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByK_LtKV_NotT_First(
			key, keyVersion, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion<");
		sb.append(keyVersion);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_LtKV_NotT_First(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByK_LtKV_NotT(
			key, keyVersion, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_LtKV_NotT_Last(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByK_LtKV_NotT_Last(
			key, keyVersion, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion<");
		sb.append(keyVersion);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_LtKV_NotT_Last(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByK_LtKV_NotT(key, keyVersion, type);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByK_LtKV_NotT(
			key, keyVersion, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByK_LtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		key = Objects.toString(key, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByK_LtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = getByK_LtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
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

	protected PatcherFix getByK_LtKV_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, String key, double keyVersion,
		int type, OrderByComparator<PatcherFix> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_2);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_TYPE_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(keyVersion);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return filterFindByK_LtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return filterFindByK_LtKV_NotT(key, keyVersion, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_LtKV_NotT(
				key, keyVersion, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByK_LtKV_NotT(
					key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(keyVersion);

			queryPos.add(type);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByK_LtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_LtKV_NotT_PrevAndNext(
				patcherFixId, key, keyVersion, type, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByK_LtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = filterGetByK_LtKV_NotT_PrevAndNext(
				session, patcherFix, key, keyVersion, type, orderByComparator,
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

	protected PatcherFix filterGetByK_LtKV_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, String key, double keyVersion,
		int type, OrderByComparator<PatcherFix> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(keyVersion);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 */
	@Override
	public void removeByK_LtKV_NotT(String key, double keyVersion, int type) {
		for (PatcherFix patcherFix :
				findByK_LtKV_NotT(
					key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByK_LtKV_NotT(String key, double keyVersion, int type) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathWithPaginationCountByK_LtKV_NotT;

		Object[] finderArgs = new Object[] {key, keyVersion, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2);

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(keyVersion);

				queryPos.add(type);

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
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByK_LtKV_NotT(key, keyVersion, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByK_LtKV_NotT(
				key, keyVersion, type);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2);

		sb.append(_FINDER_COLUMN_K_LTKV_NOTT_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(keyVersion);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_KEY_2 =
		"patcherFix.key = ? AND ";

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_KEY_3 =
		"(patcherFix.key IS NULL OR patcherFix.key = '') AND ";

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_KEY_2_SQL =
		"patcherFix.key_ = ? AND ";

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_KEY_3_SQL =
		"(patcherFix.key_ IS NULL OR patcherFix.key_ = '') AND ";

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_KEYVERSION_2 =
		"patcherFix.keyVersion < ? AND ";

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_TYPE_2 =
		"patcherFix.type != ?";

	private static final String _FINDER_COLUMN_K_LTKV_NOTT_TYPE_2_SQL =
		"patcherFix.type_ != ?";

	private FinderPath _finderPathWithPaginationFindByK_L_NotT;
	private FinderPath _finderPathWithPaginationCountByK_L_NotT;

	/**
	 * Returns all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type) {

		return findByK_L_NotT(
			key, latestFix, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end) {

		return findByK_L_NotT(key, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return findByK_L_NotT(
			key, latestFix, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByK_L_NotT;
		finderArgs = new Object[] {
			key, latestFix, type, start, end, orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if (!key.equals(patcherFix.getKey()) ||
						(latestFix != patcherFix.isLatestFix()) ||
						(type == patcherFix.getType())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_L_NOTT_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_K_L_NOTT_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(latestFix);

				queryPos.add(type);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_L_NotT_First(
			String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByK_L_NotT_First(
			key, latestFix, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_L_NotT_First(
		String key, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByK_L_NotT(
			key, latestFix, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByK_L_NotT_Last(
			String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByK_L_NotT_Last(
			key, latestFix, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByK_L_NotT_Last(
		String key, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByK_L_NotT(key, latestFix, type);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByK_L_NotT(
			key, latestFix, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByK_L_NotT_PrevAndNext(
			long patcherFixId, String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		key = Objects.toString(key, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByK_L_NotT_PrevAndNext(
				session, patcherFix, key, latestFix, type, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = getByK_L_NotT_PrevAndNext(
				session, patcherFix, key, latestFix, type, orderByComparator,
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

	protected PatcherFix getByK_L_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, String key, boolean latestFix,
		int type, OrderByComparator<PatcherFix> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_2);
		}

		sb.append(_FINDER_COLUMN_K_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_K_L_NOTT_TYPE_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(latestFix);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type) {

		return filterFindByK_L_NotT(
			key, latestFix, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end) {

		return filterFindByK_L_NotT(key, latestFix, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_L_NotT(
				key, latestFix, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByK_L_NotT(
					key, latestFix, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_K_L_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(latestFix);

			queryPos.add(type);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByK_L_NotT_PrevAndNext(
			long patcherFixId, String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_L_NotT_PrevAndNext(
				patcherFixId, key, latestFix, type, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByK_L_NotT_PrevAndNext(
				session, patcherFix, key, latestFix, type, orderByComparator,
				true);

			array[1] = patcherFix;

			array[2] = filterGetByK_L_NotT_PrevAndNext(
				session, patcherFix, key, latestFix, type, orderByComparator,
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

	protected PatcherFix filterGetByK_L_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, String key, boolean latestFix,
		int type, OrderByComparator<PatcherFix> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_K_L_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(latestFix);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	@Override
	public void removeByK_L_NotT(String key, boolean latestFix, int type) {
		for (PatcherFix patcherFix :
				findByK_L_NotT(
					key, latestFix, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByK_L_NotT(String key, boolean latestFix, int type) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathWithPaginationCountByK_L_NotT;

		Object[] finderArgs = new Object[] {key, latestFix, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_L_NOTT_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_K_L_NOTT_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(latestFix);

				queryPos.add(type);

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
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByK_L_NotT(String key, boolean latestFix, int type) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByK_L_NotT(key, latestFix, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByK_L_NotT(
				key, latestFix, type);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_NOTT_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_L_NOTT_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_K_L_NOTT_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(latestFix);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_K_L_NOTT_KEY_2 =
		"patcherFix.key = ? AND ";

	private static final String _FINDER_COLUMN_K_L_NOTT_KEY_3 =
		"(patcherFix.key IS NULL OR patcherFix.key = '') AND ";

	private static final String _FINDER_COLUMN_K_L_NOTT_KEY_2_SQL =
		"patcherFix.key_ = ? AND ";

	private static final String _FINDER_COLUMN_K_L_NOTT_KEY_3_SQL =
		"(patcherFix.key_ IS NULL OR patcherFix.key_ = '') AND ";

	private static final String _FINDER_COLUMN_K_L_NOTT_LATESTFIX_2 =
		"patcherFix.latestFix = ? AND ";

	private static final String _FINDER_COLUMN_K_L_NOTT_TYPE_2 =
		"patcherFix.type != ?";

	private static final String _FINDER_COLUMN_K_L_NOTT_TYPE_2_SQL =
		"patcherFix.type_ != ?";

	private FinderPath _finderPathWithPaginationFindByLtM_N_T_S;
	private FinderPath _finderPathWithPaginationCountByLtM_N_T_S;

	/**
	 * Returns all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return findByLtM_N_T_S(
			modifiedDate, notified, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end) {

		return findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtM_N_T_S;
		finderArgs = new Object[] {
			_getTime(modifiedDate), notified, type, status, start, end,
			orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if ((modifiedDate.getTime() <= patcherFix.getModifiedDate(
						).getTime()) || (notified != patcherFix.isNotified()) ||
						(type != patcherFix.getType()) ||
						(status != patcherFix.getStatus())) {

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
					6 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(6);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

			sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_2);

			sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindModifiedDate) {
					queryPos.add(new Timestamp(modifiedDate.getTime()));
				}

				queryPos.add(notified);

				queryPos.add(type);

				queryPos.add(status);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByLtM_N_T_S_First(
			Date modifiedDate, boolean notified, int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByLtM_N_T_S_First(
			modifiedDate, notified, type, status, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("modifiedDate<");
		sb.append(modifiedDate);

		sb.append(", notified=");
		sb.append(notified);

		sb.append(", type=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByLtM_N_T_S_First(
		Date modifiedDate, boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByLtM_N_T_S(
			modifiedDate, notified, type, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByLtM_N_T_S_Last(
			Date modifiedDate, boolean notified, int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByLtM_N_T_S_Last(
			modifiedDate, notified, type, status, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("modifiedDate<");
		sb.append(modifiedDate);

		sb.append(", notified=");
		sb.append(notified);

		sb.append(", type=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByLtM_N_T_S_Last(
		Date modifiedDate, boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByLtM_N_T_S(modifiedDate, notified, type, status);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByLtM_N_T_S(
			modifiedDate, notified, type, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByLtM_N_T_S_PrevAndNext(
			long patcherFixId, Date modifiedDate, boolean notified, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByLtM_N_T_S_PrevAndNext(
				session, patcherFix, modifiedDate, notified, type, status,
				orderByComparator, true);

			array[1] = patcherFix;

			array[2] = getByLtM_N_T_S_PrevAndNext(
				session, patcherFix, modifiedDate, notified, type, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix getByLtM_N_T_S_PrevAndNext(
		Session session, PatcherFix patcherFix, Date modifiedDate,
		boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_2);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindModifiedDate) {
			queryPos.add(new Timestamp(modifiedDate.getTime()));
		}

		queryPos.add(notified);

		queryPos.add(type);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtM_N_T_S(
				modifiedDate, notified, type, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtM_N_T_S(
					modifiedDate, notified, type, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_2_SQL);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindModifiedDate) {
				queryPos.add(new Timestamp(modifiedDate.getTime()));
			}

			queryPos.add(notified);

			queryPos.add(type);

			queryPos.add(status);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByLtM_N_T_S_PrevAndNext(
			long patcherFixId, Date modifiedDate, boolean notified, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtM_N_T_S_PrevAndNext(
				patcherFixId, modifiedDate, notified, type, status,
				orderByComparator);
		}

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByLtM_N_T_S_PrevAndNext(
				session, patcherFix, modifiedDate, notified, type, status,
				orderByComparator, true);

			array[1] = patcherFix;

			array[2] = filterGetByLtM_N_T_S_PrevAndNext(
				session, patcherFix, modifiedDate, notified, type, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix filterGetByLtM_N_T_S_PrevAndNext(
		Session session, PatcherFix patcherFix, Date modifiedDate,
		boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_2_SQL);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindModifiedDate) {
			queryPos.add(new Timestamp(modifiedDate.getTime()));
		}

		queryPos.add(notified);

		queryPos.add(type);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end) {

		return filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtM_N_T_S(
				modifiedDate, notified, types, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtM_N_T_S(
					modifiedDate, notified, types, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		if (types == null) {
			types = new int[0];
		}
		else if (types.length > 1) {
			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

		if (types.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_7_SQL);

			sb.append(StringUtil.merge(types));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindModifiedDate) {
				queryPos.add(new Timestamp(modifiedDate.getTime()));
			}

			queryPos.add(notified);

			queryPos.add(status);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return findByLtM_N_T_S(
			modifiedDate, notified, types, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end) {

		return findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		if (types == null) {
			types = new int[0];
		}
		else if (types.length > 1) {
			types = ArrayUtil.sortedUnique(types);
		}

		if (types.length == 1) {
			return findByLtM_N_T_S(
				modifiedDate, notified, types[0], status, start, end,
				orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {
					_getTime(modifiedDate), notified, StringUtil.merge(types),
					status
				};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				_getTime(modifiedDate), notified, StringUtil.merge(types),
				status, start, end, orderByComparator
			};
		}

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				_finderPathWithPaginationFindByLtM_N_T_S, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if ((modifiedDate.getTime() <= patcherFix.getModifiedDate(
						).getTime()) || (notified != patcherFix.isNotified()) ||
						!ArrayUtil.contains(types, patcherFix.getType()) ||
						(status != patcherFix.getStatus())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

			if (types.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_7);

				sb.append(StringUtil.merge(types));

				sb.append(")");

				sb.append(")");

				sb.append(WHERE_AND);
			}

			sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindModifiedDate) {
					queryPos.add(new Timestamp(modifiedDate.getTime()));
				}

				queryPos.add(notified);

				queryPos.add(status);

				list = (List<PatcherFix>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByLtM_N_T_S, finderArgs,
						list);
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
	 * Removes all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		for (PatcherFix patcherFix :
				findByLtM_N_T_S(
					modifiedDate, notified, type, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		FinderPath finderPath = _finderPathWithPaginationCountByLtM_N_T_S;

		Object[] finderArgs = new Object[] {
			_getTime(modifiedDate), notified, type, status
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

			sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_2);

			sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindModifiedDate) {
					queryPos.add(new Timestamp(modifiedDate.getTime()));
				}

				queryPos.add(notified);

				queryPos.add(type);

				queryPos.add(status);

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
	 * Returns the number of patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		if (types == null) {
			types = new int[0];
		}
		else if (types.length > 1) {
			types = ArrayUtil.sortedUnique(types);
		}

		Object[] finderArgs = new Object[] {
			_getTime(modifiedDate), notified, StringUtil.merge(types), status
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByLtM_N_T_S, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

			if (types.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_7);

				sb.append(StringUtil.merge(types));

				sb.append(")");

				sb.append(")");

				sb.append(WHERE_AND);
			}

			sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindModifiedDate) {
					queryPos.add(new Timestamp(modifiedDate.getTime()));
				}

				queryPos.add(notified);

				queryPos.add(status);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathWithPaginationCountByLtM_N_T_S, finderArgs,
					count);
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
	 * Returns the number of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtM_N_T_S(modifiedDate, notified, type, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByLtM_N_T_S(
				modifiedDate, notified, type, status);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_2_SQL);

		sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindModifiedDate) {
				queryPos.add(new Timestamp(modifiedDate.getTime()));
			}

			queryPos.add(notified);

			queryPos.add(type);

			queryPos.add(status);

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

	/**
	 * Returns the number of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtM_N_T_S(modifiedDate, notified, types, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = InlineSQLHelperUtil.filter(
				findByLtM_N_T_S(modifiedDate, notified, types, status));

			return patcherFixes.size();
		}

		if (types == null) {
			types = new int[0];
		}
		else if (types.length > 1) {
			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2);

		if (types.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_LTM_N_T_S_TYPE_7_SQL);

			sb.append(StringUtil.merge(types));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_LTM_N_T_S_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindModifiedDate) {
				queryPos.add(new Timestamp(modifiedDate.getTime()));
			}

			queryPos.add(notified);

			queryPos.add(status);

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

	private static final String _FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_1 =
		"patcherFix.modifiedDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTM_N_T_S_MODIFIEDDATE_2 =
		"patcherFix.modifiedDate < ? AND ";

	private static final String _FINDER_COLUMN_LTM_N_T_S_NOTIFIED_2 =
		"patcherFix.notified = ? AND ";

	private static final String _FINDER_COLUMN_LTM_N_T_S_TYPE_2 =
		"patcherFix.type = ? AND ";

	private static final String _FINDER_COLUMN_LTM_N_T_S_TYPE_7 =
		"patcherFix.type IN (";

	private static final String _FINDER_COLUMN_LTM_N_T_S_TYPE_2_SQL =
		"patcherFix.type_ = ? AND ";

	private static final String _FINDER_COLUMN_LTM_N_T_S_TYPE_7_SQL =
		"patcherFix.type_ IN (";

	private static final String _FINDER_COLUMN_LTM_N_T_S_STATUS_2 =
		"patcherFix.status = ?";

	private FinderPath _finderPathWithPaginationFindByP_L_N_NotT;
	private FinderPath _finderPathWithPaginationCountByP_L_N_NotT;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end) {

		return findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByP_L_N_NotT;
		finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, name, type, start, end,
			orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if ((patcherProjectVersionId !=
							patcherFix.getPatcherProjectVersionId()) ||
						(latestFix != patcherFix.isLatestFix()) ||
						!name.equals(patcherFix.getName()) ||
						(type == patcherFix.getType())) {

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
					6 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(6);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_2);
			}

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				if (bindName) {
					queryPos.add(name);
				}

				queryPos.add(type);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_N_NotT_First(
			long patcherProjectVersionId, boolean latestFix, String name,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_N_NotT_First(
			patcherProjectVersionId, latestFix, name, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", name=");
		sb.append(name);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_N_NotT_First(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_N_NotT_Last(
			long patcherProjectVersionId, boolean latestFix, String name,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_N_NotT_Last(
			patcherProjectVersionId, latestFix, name, type, orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", name=");
		sb.append(name);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_N_NotT_Last(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByP_L_N_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			String name, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		name = Objects.toString(name, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByP_L_N_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, name,
				type, orderByComparator, true);

			array[1] = patcherFix;

			array[2] = getByP_L_N_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, name,
				type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix getByP_L_N_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_2);
		}

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_TYPE_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end) {

		return filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_N_NotT(
				patcherProjectVersionId, latestFix, name, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_L_N_NotT(
					patcherProjectVersionId, latestFix, name, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
		}

		name = Objects.toString(name, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_2);
		}

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			if (bindName) {
				queryPos.add(name);
			}

			queryPos.add(type);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByP_L_N_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			String name, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_N_NotT_PrevAndNext(
				patcherFixId, patcherProjectVersionId, latestFix, name, type,
				orderByComparator);
		}

		name = Objects.toString(name, "");

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByP_L_N_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, name,
				type, orderByComparator, true);

			array[1] = patcherFix;

			array[2] = filterGetByP_L_N_NotT_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, name,
				type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix filterGetByP_L_N_NotT_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_2);
		}

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		for (PatcherFix patcherFix :
				findByP_L_N_NotT(
					patcherProjectVersionId, latestFix, name, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathWithPaginationCountByP_L_N_NotT;

		Object[] finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, name, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_2);
			}

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				if (bindName) {
					queryPos.add(name);
				}

				queryPos.add(type);

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
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_L_N_NotT(
				patcherProjectVersionId, latestFix, name, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByP_L_N_NotT(
				patcherProjectVersionId, latestFix, name, type);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_L_N_NOTT_NAME_2);
		}

		sb.append(_FINDER_COLUMN_P_L_N_NOTT_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			if (bindName) {
				queryPos.add(name);
			}

			queryPos.add(type);

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

	private static final String
		_FINDER_COLUMN_P_L_N_NOTT_PATCHERPROJECTVERSIONID_2 =
			"patcherFix.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_P_L_N_NOTT_LATESTFIX_2 =
		"patcherFix.latestFix = ? AND ";

	private static final String _FINDER_COLUMN_P_L_N_NOTT_NAME_2 =
		"patcherFix.name = ? AND ";

	private static final String _FINDER_COLUMN_P_L_N_NOTT_NAME_3 =
		"(patcherFix.name IS NULL OR patcherFix.name = '') AND ";

	private static final String _FINDER_COLUMN_P_L_N_NOTT_TYPE_2 =
		"patcherFix.type != ?";

	private static final String _FINDER_COLUMN_P_L_N_NOTT_TYPE_2_SQL =
		"patcherFix.type_ != ?";

	private FinderPath _finderPathWithPaginationFindByP_L_NotT_S;
	private FinderPath _finderPathWithPaginationCountByP_L_NotT_S;

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end) {

		return findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	@Override
	public List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByP_L_NotT_S;
		finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, type, status, start, end,
			orderByComparator
		};

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFix patcherFix : list) {
					if ((patcherProjectVersionId !=
							patcherFix.getPatcherProjectVersionId()) ||
						(latestFix != patcherFix.isLatestFix()) ||
						(type == patcherFix.getType()) ||
						(status != patcherFix.getStatus())) {

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
					6 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(6);
			}

			sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_TYPE_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				queryPos.add(type);

				queryPos.add(status);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_NotT_S_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_NotT_S_First(
			patcherProjectVersionId, latestFix, type, status,
			orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type!=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_NotT_S_First(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		List<PatcherFix> list = findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix findByP_L_NotT_S_Last(
			long patcherProjectVersionId, boolean latestFix, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByP_L_NotT_S_Last(
			patcherProjectVersionId, latestFix, type, status,
			orderByComparator);

		if (patcherFix != null) {
			return patcherFix;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", latestFix=");
		sb.append(latestFix);

		sb.append(", type!=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixException(sb.toString());
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	@Override
	public PatcherFix fetchByP_L_NotT_S_Last(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		int count = countByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);

		if (count == 0) {
			return null;
		}

		List<PatcherFix> list = findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] findByP_L_NotT_S_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = getByP_L_NotT_S_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				status, orderByComparator, true);

			array[1] = patcherFix;

			array[2] = getByP_L_NotT_S_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix getByP_L_NotT_S_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_TYPE_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_STATUS_2);

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
			sb.append(PatcherFixModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		queryPos.add(type);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end) {

		return filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	@Override
	public List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_NotT_S(
				patcherProjectVersionId, latestFix, type, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_L_NotT_S(
					patcherProjectVersionId, latestFix, type, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_TYPE_2_SQL);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			queryPos.add(type);

			queryPos.add(status);

			return (List<PatcherFix>)QueryUtil.list(
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
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix[] filterFindByP_L_NotT_S_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws NoSuchPatcherFixException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_L_NotT_S_PrevAndNext(
				patcherFixId, patcherProjectVersionId, latestFix, type, status,
				orderByComparator);
		}

		PatcherFix patcherFix = findByPrimaryKey(patcherFixId);

		Session session = null;

		try {
			session = openSession();

			PatcherFix[] array = new PatcherFixImpl[3];

			array[0] = filterGetByP_L_NotT_S_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				status, orderByComparator, true);

			array[1] = patcherFix;

			array[2] = filterGetByP_L_NotT_S_PrevAndNext(
				session, patcherFix, patcherProjectVersionId, latestFix, type,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFix filterGetByP_L_NotT_S_PrevAndNext(
		Session session, PatcherFix patcherFix, long patcherProjectVersionId,
		boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_TYPE_2_SQL);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(latestFix);

		queryPos.add(type);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherFix)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFix> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		for (PatcherFix patcherFix :
				findByP_L_NotT_S(
					patcherProjectVersionId, latestFix, type, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	@Override
	public int countByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		FinderPath finderPath = _finderPathWithPaginationCountByP_L_NotT_S;

		Object[] finderArgs = new Object[] {
			patcherProjectVersionId, latestFix, type, status
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_PATCHERFIX_WHERE);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_TYPE_2);

			sb.append(_FINDER_COLUMN_P_L_NOTT_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(latestFix);

				queryPos.add(type);

				queryPos.add(status);

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
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	@Override
	public int filterCountByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_L_NotT_S(
				patcherProjectVersionId, latestFix, type, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFix> patcherFixes = findByP_L_NotT_S(
				patcherProjectVersionId, latestFix, type, status);

			patcherFixes = InlineSQLHelperUtil.filter(patcherFixes);

			return patcherFixes.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIX_WHERE);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_TYPE_2_SQL);

		sb.append(_FINDER_COLUMN_P_L_NOTT_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFix.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(latestFix);

			queryPos.add(type);

			queryPos.add(status);

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

	private static final String
		_FINDER_COLUMN_P_L_NOTT_S_PATCHERPROJECTVERSIONID_2 =
			"patcherFix.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_P_L_NOTT_S_LATESTFIX_2 =
		"patcherFix.latestFix = ? AND ";

	private static final String _FINDER_COLUMN_P_L_NOTT_S_TYPE_2 =
		"patcherFix.type != ? AND ";

	private static final String _FINDER_COLUMN_P_L_NOTT_S_TYPE_2_SQL =
		"patcherFix.type_ != ? AND ";

	private static final String _FINDER_COLUMN_P_L_NOTT_S_STATUS_2 =
		"patcherFix.status = ?";

	public PatcherFixPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PatcherFix.class);

		setModelImplClass(PatcherFixImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix in the entity cache if it is enabled.
	 *
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void cacheResult(PatcherFix patcherFix) {
		entityCache.putResult(
			PatcherFixImpl.class, patcherFix.getPrimaryKey(), patcherFix);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fixes in the entity cache if it is enabled.
	 *
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void cacheResult(List<PatcherFix> patcherFixes) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixes.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFix patcherFix : patcherFixes) {
			if (entityCache.getResult(
					PatcherFixImpl.class, patcherFix.getPrimaryKey()) == null) {

				cacheResult(patcherFix);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fixes.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixImpl.class);

		finderCache.clearCache(PatcherFixImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFix patcherFix) {
		entityCache.removeResult(PatcherFixImpl.class, patcherFix);
	}

	@Override
	public void clearCache(List<PatcherFix> patcherFixes) {
		for (PatcherFix patcherFix : patcherFixes) {
			entityCache.removeResult(PatcherFixImpl.class, patcherFix);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	@Override
	public PatcherFix create(long patcherFixId) {
		PatcherFix patcherFix = new PatcherFixImpl();

		patcherFix.setNew(true);
		patcherFix.setPrimaryKey(patcherFixId);

		patcherFix.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFix;
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix remove(long patcherFixId)
		throws NoSuchPatcherFixException {

		return remove((Serializable)patcherFixId);
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix remove(Serializable primaryKey)
		throws NoSuchPatcherFixException {

		Session session = null;

		try {
			session = openSession();

			PatcherFix patcherFix = (PatcherFix)session.get(
				PatcherFixImpl.class, primaryKey);

			if (patcherFix == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFix);
		}
		catch (NoSuchPatcherFixException noSuchEntityException) {
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
	protected PatcherFix removeImpl(PatcherFix patcherFix) {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFix.getPrimaryKey());

		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFix.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFix)) {
				patcherFix = (PatcherFix)session.get(
					PatcherFixImpl.class, patcherFix.getPrimaryKeyObj());
			}

			if (patcherFix != null) {
				session.delete(patcherFix);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFix != null) {
			clearCache(patcherFix);
		}

		return patcherFix;
	}

	@Override
	public PatcherFix updateImpl(PatcherFix patcherFix) {
		boolean isNew = patcherFix.isNew();

		if (!(patcherFix instanceof PatcherFixModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFix.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(patcherFix);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFix proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFix implementation " +
					patcherFix.getClass());
		}

		PatcherFixModelImpl patcherFixModelImpl =
			(PatcherFixModelImpl)patcherFix;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFix.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFix.setCreateDate(date);
			}
			else {
				patcherFix.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFix.setModifiedDate(date);
			}
			else {
				patcherFix.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFix);
			}
			else {
				patcherFix = (PatcherFix)session.merge(patcherFix);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherFixImpl.class, patcherFixModelImpl, false, true);

		if (isNew) {
			patcherFix.setNew(false);
		}

		patcherFix.resetOriginalValues();

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixException {

		PatcherFix patcherFix = fetchByPrimaryKey(primaryKey);

		if (patcherFix == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a <code>NoSuchPatcherFixException</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix findByPrimaryKey(long patcherFixId)
		throws NoSuchPatcherFixException {

		return findByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	 */
	@Override
	public PatcherFix fetchByPrimaryKey(long patcherFixId) {
		return fetchByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns all the patcher fixes.
	 *
	 * @return the patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll(
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fixes
	 */
	@Override
	public List<PatcherFix> findAll(
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
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

		List<PatcherFix> list = null;

		if (useFinderCache) {
			list = (List<PatcherFix>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIX);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIX;

				sql = sql.concat(PatcherFixModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFix>)QueryUtil.list(
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
	 * Removes all the patcher fixes from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFix patcherFix : findAll()) {
			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixes.
	 *
	 * @return the number of patcher fixes
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERFIX);

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

	/**
	 * Returns the primaryKeys of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher fix
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long pk) {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(long pk) {
		return getPatcherBuildPatcherFixes(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end) {

		return getPatcherBuildPatcherFixes(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher build
	 */
	@Override
	public List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return patcherFixToPatcherBuildTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher builds associated with the patcher fix
	 */
	@Override
	public int getPatcherBuildsSize(long pk) {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return patcherFixToPatcherBuildTableMapper.containsTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher builds
	 * @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuilds(long pk) {
		if (getPatcherBuildsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, long patcherBuildPK) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherBuildPK);
		}
		else {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, PatcherBuild patcherBuild) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
		else {
			return patcherFixToPatcherBuildTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		long[] addedKeys = patcherFixToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, patcherBuildPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		return addPatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher builds from
	 */
	@Override
	public void clearPatcherBuilds(long pk) {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK) {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, PatcherBuild patcherBuild) {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		patcherFixToPatcherBuildTableMapper.deleteTableMappings(
			pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		removePatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(
			patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(
			oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		patcherFixToPatcherBuildTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherBuildPKsSet));

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		patcherFixToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherBuildPKsSet));
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds to be associated with the patcher fix
	 */
	@Override
	public void setPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		try {
			long[] patcherBuildPKs = new long[patcherBuilds.size()];

			for (int i = 0; i < patcherBuilds.size(); i++) {
				PatcherBuild patcherBuild = patcherBuilds.get(i);

				patcherBuildPKs[i] = patcherBuild.getPrimaryKey();
			}

			setPatcherBuilds(pk, patcherBuildPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher fix packs associated with the patcher fix
	 */
	@Override
	public long[] getPatcherFixPackPrimaryKeys(long pk) {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(long pk) {
		return getPatcherFixPackPatcherFixes(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end) {

		return getPatcherFixPackPatcherFixes(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return patcherFixToPatcherFixPackTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher fix packs associated with the patcher fix
	 */
	@Override
	public int getPatcherFixPacksSize(long pk) {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixPack(long pk, long patcherFixPackPK) {
		return patcherFixToPatcherFixPackTableMapper.containsTableMapping(
			pk, patcherFixPackPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	 * @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixPacks(long pk) {
		if (getPatcherFixPacksSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFixPack(long pk, long patcherFixPackPK) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPackPK);
		}
		else {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherFixPackPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFixPack(long pk, PatcherFixPack patcherFixPack) {
		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFixPack.getPrimaryKey());
		}
		else {
			return patcherFixToPatcherFixPackTableMapper.addTableMapping(
				patcherFix.getCompanyId(), pk, patcherFixPack.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		long[] addedKeys =
			patcherFixToPatcherFixPackTableMapper.addTableMappings(
				companyId, pk, patcherFixPackPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		return addPatcherFixPacks(
			pk,
			ListUtil.toLongArray(
				patcherFixPacks, PatcherFixPack.PATCHER_FIX_PACK_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	 */
	@Override
	public void clearPatcherFixPacks(long pk) {
		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 */
	@Override
	public void removePatcherFixPack(long pk, long patcherFixPackPK) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(
			pk, patcherFixPackPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 */
	@Override
	public void removePatcherFixPack(long pk, PatcherFixPack patcherFixPack) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(
			pk, patcherFixPack.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 */
	@Override
	public void removePatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		patcherFixToPatcherFixPackTableMapper.deleteTableMappings(
			pk, patcherFixPackPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 */
	@Override
	public void removePatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		removePatcherFixPacks(
			pk,
			ListUtil.toLongArray(
				patcherFixPacks, PatcherFixPack.PATCHER_FIX_PACK_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	 */
	@Override
	public void setPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		Set<Long> newPatcherFixPackPKsSet = SetUtil.fromArray(
			patcherFixPackPKs);
		Set<Long> oldPatcherFixPackPKsSet = SetUtil.fromArray(
			patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPackPKsSet = new HashSet<Long>(
			oldPatcherFixPackPKsSet);

		removePatcherFixPackPKsSet.removeAll(newPatcherFixPackPKsSet);

		patcherFixToPatcherFixPackTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPackPKsSet));

		newPatcherFixPackPKsSet.removeAll(oldPatcherFixPackPKsSet);

		long companyId = 0;

		PatcherFix patcherFix = fetchByPrimaryKey(pk);

		if (patcherFix == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFix.getCompanyId();
		}

		patcherFixToPatcherFixPackTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPackPKsSet));
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	 */
	@Override
	public void setPatcherFixPacks(
		long pk, List<PatcherFixPack> patcherFixPacks) {

		try {
			long[] patcherFixPackPKs = new long[patcherFixPacks.size()];

			for (int i = 0; i < patcherFixPacks.size(); i++) {
				PatcherFixPack patcherFixPack = patcherFixPacks.get(i);

				patcherFixPackPKs[i] = patcherFixPack.getPrimaryKey();
			}

			setPatcherFixPacks(pk, patcherFixPackPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherFixId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIX;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherFixToPatcherBuildTableMapper = TableMapperFactory.getTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherFixId",
			"OSBPatcher_PBuilds_PFixes", "companyId", "patcherFixId",
			"patcherBuildId", this, PatcherBuild.class);

		patcherFixToPatcherFixPackTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PFixes_PFixPacks#patcherFixId",
				"OSBPatcher_PFixes_PFixPacks", "companyId", "patcherFixId",
				"patcherFixPackId", this, PatcherFixPack.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByPatcherProjectVersionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByPatcherProjectVersionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"patcherProjectVersionId"}, true);

		_finderPathWithoutPaginationFindByPatcherProjectVersionId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByPatcherProjectVersionId",
				new String[] {Long.class.getName()},
				new String[] {"patcherProjectVersionId"}, true);

		_finderPathCountByPatcherProjectVersionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPatcherProjectVersionId",
			new String[] {Long.class.getName()},
			new String[] {"patcherProjectVersionId"}, false);

		_finderPathWithPaginationFindByP_L_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"patcherProjectVersionId", "latestFix", "type_"},
			true);

		_finderPathWithoutPaginationFindByP_L_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_L_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"patcherProjectVersionId", "latestFix", "type_"},
			true);

		_finderPathCountByP_L_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_L_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"patcherProjectVersionId", "latestFix", "type_"},
			false);

		_finderPathWithPaginationFindByP_L_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_NotT",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"patcherProjectVersionId", "latestFix", "type_"},
			true);

		_finderPathWithPaginationCountByP_L_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_NotT",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"patcherProjectVersionId", "latestFix", "type_"},
			false);

		_finderPathWithPaginationFindByK_GtKV_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_GtKV_NotT",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"key_", "keyVersion", "type_"}, true);

		_finderPathWithPaginationCountByK_GtKV_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_GtKV_NotT",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName()
			},
			new String[] {"key_", "keyVersion", "type_"}, false);

		_finderPathWithPaginationFindByK_LtKV_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_LtKV_NotT",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"key_", "keyVersion", "type_"}, true);

		_finderPathWithPaginationCountByK_LtKV_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_LtKV_NotT",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName()
			},
			new String[] {"key_", "keyVersion", "type_"}, false);

		_finderPathWithPaginationFindByK_L_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_L_NotT",
			new String[] {
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"key_", "latestFix", "type_"}, true);

		_finderPathWithPaginationCountByK_L_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_L_NotT",
			new String[] {
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"key_", "latestFix", "type_"}, false);

		_finderPathWithPaginationFindByLtM_N_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtM_N_T_S",
			new String[] {
				Date.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"modifiedDate", "notified", "type_", "status"}, true);

		_finderPathWithPaginationCountByLtM_N_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtM_N_T_S",
			new String[] {
				Date.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName()
			},
			new String[] {"modifiedDate", "notified", "type_", "status"},
			false);

		_finderPathWithPaginationFindByP_L_N_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_N_NotT",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "latestFix", "name", "type_"
			},
			true);

		_finderPathWithPaginationCountByP_L_N_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_N_NotT",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "latestFix", "name", "type_"
			},
			false);

		_finderPathWithPaginationFindByP_L_NotT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L_NotT_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "latestFix", "type_", "status"
			},
			true);

		_finderPathWithPaginationCountByP_L_NotT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L_NotT_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "latestFix", "type_", "status"
			},
			false);

		PatcherFixUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherFixId");
		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PFixes_PFixPacks#patcherFixId");
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	protected TableMapper<PatcherFix, PatcherBuild>
		patcherFixToPatcherBuildTableMapper;
	protected TableMapper<PatcherFix, PatcherFixPack>
		patcherFixToPatcherFixPackTableMapper;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_PATCHERFIX =
		"SELECT patcherFix FROM PatcherFix patcherFix";

	private static final String _SQL_SELECT_PATCHERFIX_WHERE =
		"SELECT patcherFix FROM PatcherFix patcherFix WHERE ";

	private static final String _SQL_COUNT_PATCHERFIX =
		"SELECT COUNT(patcherFix) FROM PatcherFix patcherFix";

	private static final String _SQL_COUNT_PATCHERFIX_WHERE =
		"SELECT COUNT(patcherFix) FROM PatcherFix patcherFix WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"patcherFix.patcherFixId";

	private static final String _FILTER_SQL_SELECT_PATCHERFIX_WHERE =
		"SELECT DISTINCT {patcherFix.*} FROM OSBPatcher_PatcherFix patcherFix WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OSBPatcher_PatcherFix.*} FROM (SELECT DISTINCT patcherFix.patcherFixId FROM OSBPatcher_PatcherFix patcherFix WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERFIX_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OSBPatcher_PatcherFix ON TEMP_TABLE.patcherFixId = OSBPatcher_PatcherFix.patcherFixId";

	private static final String _FILTER_SQL_COUNT_PATCHERFIX_WHERE =
		"SELECT COUNT(DISTINCT patcherFix.patcherFixId) AS COUNT_VALUE FROM OSBPatcher_PatcherFix patcherFix WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "patcherFix";

	private static final String _FILTER_ENTITY_TABLE = "OSBPatcher_PatcherFix";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFix.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"OSBPatcher_PatcherFix.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFix exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherFix exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}