/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherBuildTable;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.impl.PatcherBuildImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherBuildPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildUtil;
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
 * The persistence implementation for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherBuildPersistence.class)
public class PatcherBuildPersistenceImpl
	extends BasePersistenceImpl<PatcherBuild>
	implements PatcherBuildPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherBuildUtil</code> to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherBuildImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByPatcherFixId;
	private FinderPath _finderPathWithoutPaginationFindByPatcherFixId;
	private FinderPath _finderPathCountByPatcherFixId;

	/**
	 * Returns all the patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherFixId(long patcherFixId) {
		return findByPatcherFixId(
			patcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end) {

		return findByPatcherFixId(patcherFixId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByPatcherFixId(
			patcherFixId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByPatcherFixId;
				finderArgs = new Object[] {patcherFixId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPatcherFixId;
			finderArgs = new Object[] {
				patcherFixId, start, end, orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (patcherFixId != patcherBuild.getPatcherFixId()) {
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

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixId);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByPatcherFixId_First(
			long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByPatcherFixId_First(
			patcherFixId, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixId=");
		sb.append(patcherFixId);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByPatcherFixId_First(
		long patcherFixId, OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByPatcherFixId(
			patcherFixId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByPatcherFixId_Last(
			long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByPatcherFixId_Last(
			patcherFixId, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixId=");
		sb.append(patcherFixId);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByPatcherFixId_Last(
		long patcherFixId, OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByPatcherFixId(patcherFixId);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByPatcherFixId(
			patcherFixId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByPatcherFixId_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByPatcherFixId_PrevAndNext(
				session, patcherBuild, patcherFixId, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByPatcherFixId_PrevAndNext(
				session, patcherBuild, patcherFixId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild getByPatcherFixId_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherFixId,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherFixId(long patcherFixId) {
		return filterFindByPatcherFixId(
			patcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end) {

		return filterFindByPatcherFixId(patcherFixId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherFixId(
				patcherFixId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPatcherFixId(
					patcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixId);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByPatcherFixId_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherFixId_PrevAndNext(
				patcherBuildId, patcherFixId, orderByComparator);
		}

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByPatcherFixId_PrevAndNext(
				session, patcherBuild, patcherFixId, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByPatcherFixId_PrevAndNext(
				session, patcherBuild, patcherFixId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild filterGetByPatcherFixId_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherFixId,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 */
	@Override
	public void removeByPatcherFixId(long patcherFixId) {
		for (PatcherBuild patcherBuild :
				findByPatcherFixId(
					patcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByPatcherFixId(long patcherFixId) {
		FinderPath finderPath = _finderPathCountByPatcherFixId;

		Object[] finderArgs = new Object[] {patcherFixId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixId);

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
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherFixId(long patcherFixId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPatcherFixId(patcherFixId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByPatcherFixId(patcherFixId);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixId);

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

	private static final String _FINDER_COLUMN_PATCHERFIXID_PATCHERFIXID_2 =
		"patcherBuild.patcherFixId = ?";

	private FinderPath _finderPathWithPaginationFindByPatcherProjectVersionId;
	private FinderPath
		_finderPathWithoutPaginationFindByPatcherProjectVersionId;
	private FinderPath _finderPathCountByPatcherProjectVersionId;

	/**
	 * Returns all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return findByPatcherProjectVersionId(
			patcherProjectVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
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

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (patcherProjectVersionId !=
							patcherBuild.getPatcherProjectVersionId()) {

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

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByPatcherProjectVersionId_First(
			patcherProjectVersionId, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByPatcherProjectVersionId(
			patcherProjectVersionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByPatcherProjectVersionId_Last(
			long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByPatcherProjectVersionId_Last(
			patcherProjectVersionId, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByPatcherProjectVersionId_Last(
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByPatcherProjectVersionId(patcherProjectVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByPatcherProjectVersionId(
			patcherProjectVersionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByPatcherProjectVersionId_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByPatcherProjectVersionId_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByPatcherProjectVersionId_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
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

	protected PatcherBuild getByPatcherProjectVersionId_PrevAndNext(
		Session session, PatcherBuild patcherBuild,
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByPatcherProjectVersionId_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherProjectVersionId_PrevAndNext(
				patcherBuildId, patcherProjectVersionId, orderByComparator);
		}

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByPatcherProjectVersionId_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByPatcherProjectVersionId_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
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

	protected PatcherBuild filterGetByPatcherProjectVersionId_PrevAndNext(
		Session session, PatcherBuild patcherBuild,
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	@Override
	public void removeByPatcherProjectVersionId(long patcherProjectVersionId) {
		for (PatcherBuild patcherBuild :
				findByPatcherProjectVersionId(
					patcherProjectVersionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByPatcherProjectVersionId(long patcherProjectVersionId) {
		FinderPath finderPath = _finderPathCountByPatcherProjectVersionId;

		Object[] finderArgs = new Object[] {patcherProjectVersionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

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
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPatcherProjectVersionId(patcherProjectVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByPatcherProjectVersionId(
				patcherProjectVersionId);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(
			_FINDER_COLUMN_PATCHERPROJECTVERSIONID_PATCHERPROJECTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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
			"patcherBuild.patcherProjectVersionId = ?";

	private FinderPath _finderPathWithPaginationFindByKey;
	private FinderPath _finderPathWithoutPaginationFindByKey;
	private FinderPath _finderPathCountByKey;

	/**
	 * Returns all the patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByKey(String key) {
		return findByKey(key, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByKey(String key, int start, int end) {
		return findByKey(key, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByKey(key, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByKey;
				finderArgs = new Object[] {key};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByKey;
			finderArgs = new Object[] {key, start, end, orderByComparator};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (!key.equals(patcherBuild.getKey())) {
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

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_KEY_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_KEY_KEY_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByKey_First(
			String key, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByKey_First(key, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByKey_First(
		String key, OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByKey(key, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByKey_Last(
			String key, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByKey_Last(key, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByKey_Last(
		String key, OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByKey(key);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByKey(
			key, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByKey_PrevAndNext(
			long patcherBuildId, String key,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByKey_PrevAndNext(
				session, patcherBuild, key, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByKey_PrevAndNext(
				session, patcherBuild, key, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild getByKey_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_KEY_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_KEY_KEY_2);
		}

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindKey) {
			queryPos.add(key);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByKey(String key) {
		return filterFindByKey(key, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByKey(String key, int start, int end) {
		return filterFindByKey(key, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByKey(key, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByKey(
					key, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_KEY_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_KEY_KEY_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByKey_PrevAndNext(
			long patcherBuildId, String key,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByKey_PrevAndNext(
				patcherBuildId, key, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByKey_PrevAndNext(
				session, patcherBuild, key, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByKey_PrevAndNext(
				session, patcherBuild, key, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild filterGetByKey_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_KEY_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_KEY_KEY_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	@Override
	public void removeByKey(String key) {
		for (PatcherBuild patcherBuild :
				findByKey(key, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByKey(String key) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathCountByKey;

		Object[] finderArgs = new Object[] {key};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_KEY_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_KEY_KEY_2);
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
	 * Returns the number of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByKey(String key) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByKey(key);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByKey(key);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_KEY_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_KEY_KEY_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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

	private static final String _FINDER_COLUMN_KEY_KEY_2 =
		"patcherBuild.key = ?";

	private static final String _FINDER_COLUMN_KEY_KEY_3 =
		"(patcherBuild.key IS NULL OR patcherBuild.key = '')";

	private static final String _FINDER_COLUMN_KEY_KEY_2_SQL =
		"patcherBuild.key_ = ?";

	private static final String _FINDER_COLUMN_KEY_KEY_3_SQL =
		"(patcherBuild.key_ IS NULL OR patcherBuild.key_ = '')";

	private FinderPath _finderPathWithPaginationFindByP_P;
	private FinderPath _finderPathWithoutPaginationFindByP_P;
	private FinderPath _finderPathCountByP_P;

	/**
	 * Returns all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return findByP_P(
			patcherAccountId, patcherProductVersionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start,
		int end) {

		return findByP_P(
			patcherAccountId, patcherProductVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByP_P(
			patcherAccountId, patcherProductVersionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_P;
				finderArgs = new Object[] {
					patcherAccountId, patcherProductVersionId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_P;
			finderArgs = new Object[] {
				patcherAccountId, patcherProductVersionId, start, end,
				orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((patcherAccountId !=
							patcherBuild.getPatcherAccountId()) ||
						(patcherProductVersionId !=
							patcherBuild.getPatcherProductVersionId())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_P_PATCHERACCOUNTID_2);

			sb.append(_FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherAccountId);

				queryPos.add(patcherProductVersionId);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_P_First(
			long patcherAccountId, long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_P_First(
			patcherAccountId, patcherProductVersionId, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherAccountId=");
		sb.append(patcherAccountId);

		sb.append(", patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_P_First(
		long patcherAccountId, long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByP_P(
			patcherAccountId, patcherProductVersionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_P_Last(
			long patcherAccountId, long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_P_Last(
			patcherAccountId, patcherProductVersionId, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherAccountId=");
		sb.append(patcherAccountId);

		sb.append(", patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_P_Last(
		long patcherAccountId, long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByP_P(patcherAccountId, patcherProductVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByP_P(
			patcherAccountId, patcherProductVersionId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByP_P_PrevAndNext(
			long patcherBuildId, long patcherAccountId,
			long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByP_P_PrevAndNext(
				session, patcherBuild, patcherAccountId,
				patcherProductVersionId, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByP_P_PrevAndNext(
				session, patcherBuild, patcherAccountId,
				patcherProductVersionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild getByP_P_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherAccountId,
		long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_P_PATCHERACCOUNTID_2);

		sb.append(_FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherAccountId);

		queryPos.add(patcherProductVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return filterFindByP_P(
			patcherAccountId, patcherProductVersionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start,
		int end) {

		return filterFindByP_P(
			patcherAccountId, patcherProductVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_P(
				patcherAccountId, patcherProductVersionId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_P(
					patcherAccountId, patcherProductVersionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_P_PATCHERACCOUNTID_2);

		sb.append(_FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherAccountId);

			queryPos.add(patcherProductVersionId);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByP_P_PrevAndNext(
			long patcherBuildId, long patcherAccountId,
			long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_P_PrevAndNext(
				patcherBuildId, patcherAccountId, patcherProductVersionId,
				orderByComparator);
		}

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByP_P_PrevAndNext(
				session, patcherBuild, patcherAccountId,
				patcherProductVersionId, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByP_P_PrevAndNext(
				session, patcherBuild, patcherAccountId,
				patcherProductVersionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild filterGetByP_P_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherAccountId,
		long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_P_PATCHERACCOUNTID_2);

		sb.append(_FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherAccountId);

		queryPos.add(patcherProductVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 */
	@Override
	public void removeByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		for (PatcherBuild patcherBuild :
				findByP_P(
					patcherAccountId, patcherProductVersionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_P(long patcherAccountId, long patcherProductVersionId) {
		FinderPath finderPath = _finderPathCountByP_P;

		Object[] finderArgs = new Object[] {
			patcherAccountId, patcherProductVersionId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_P_PATCHERACCOUNTID_2);

			sb.append(_FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherAccountId);

				queryPos.add(patcherProductVersionId);

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
	 * Returns the number of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_P(patcherAccountId, patcherProductVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByP_P(
				patcherAccountId, patcherProductVersionId);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_P_PATCHERACCOUNTID_2);

		sb.append(_FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherAccountId);

			queryPos.add(patcherProductVersionId);

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

	private static final String _FINDER_COLUMN_P_P_PATCHERACCOUNTID_2 =
		"patcherBuild.patcherAccountId = ? AND ";

	private static final String _FINDER_COLUMN_P_P_PATCHERPRODUCTVERSIONID_2 =
		"patcherBuild.patcherProductVersionId = ?";

	private FinderPath _finderPathWithPaginationFindByP_C;
	private FinderPath _finderPathWithoutPaginationFindByP_C;
	private FinderPath _finderPathCountByP_C;

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_C(long patcherFixId, boolean childBuild) {
		return findByP_C(
			patcherFixId, childBuild, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end) {

		return findByP_C(patcherFixId, childBuild, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByP_C(
			patcherFixId, childBuild, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_C;
				finderArgs = new Object[] {patcherFixId, childBuild};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_C;
			finderArgs = new Object[] {
				patcherFixId, childBuild, start, end, orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((patcherFixId != patcherBuild.getPatcherFixId()) ||
						(childBuild != patcherBuild.isChildBuild())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_C_PATCHERFIXID_2);

			sb.append(_FINDER_COLUMN_P_C_CHILDBUILD_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixId);

				queryPos.add(childBuild);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_C_First(
			long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_C_First(
			patcherFixId, childBuild, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixId=");
		sb.append(patcherFixId);

		sb.append(", childBuild=");
		sb.append(childBuild);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_C_First(
		long patcherFixId, boolean childBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByP_C(
			patcherFixId, childBuild, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_C_Last(
			long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_C_Last(
			patcherFixId, childBuild, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixId=");
		sb.append(patcherFixId);

		sb.append(", childBuild=");
		sb.append(childBuild);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_C_Last(
		long patcherFixId, boolean childBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByP_C(patcherFixId, childBuild);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByP_C(
			patcherFixId, childBuild, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByP_C_PrevAndNext(
			long patcherBuildId, long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByP_C_PrevAndNext(
				session, patcherBuild, patcherFixId, childBuild,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByP_C_PrevAndNext(
				session, patcherBuild, patcherFixId, childBuild,
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

	protected PatcherBuild getByP_C_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherFixId,
		boolean childBuild, OrderByComparator<PatcherBuild> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_C_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_C_CHILDBUILD_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixId);

		queryPos.add(childBuild);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild) {

		return filterFindByP_C(
			patcherFixId, childBuild, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end) {

		return filterFindByP_C(patcherFixId, childBuild, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_C(
				patcherFixId, childBuild, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_C(
					patcherFixId, childBuild, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_C_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_C_CHILDBUILD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixId);

			queryPos.add(childBuild);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByP_C_PrevAndNext(
			long patcherBuildId, long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_C_PrevAndNext(
				patcherBuildId, patcherFixId, childBuild, orderByComparator);
		}

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByP_C_PrevAndNext(
				session, patcherBuild, patcherFixId, childBuild,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByP_C_PrevAndNext(
				session, patcherBuild, patcherFixId, childBuild,
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

	protected PatcherBuild filterGetByP_C_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherFixId,
		boolean childBuild, OrderByComparator<PatcherBuild> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_C_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_C_CHILDBUILD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixId);

		queryPos.add(childBuild);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and childBuild = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 */
	@Override
	public void removeByP_C(long patcherFixId, boolean childBuild) {
		for (PatcherBuild patcherBuild :
				findByP_C(
					patcherFixId, childBuild, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_C(long patcherFixId, boolean childBuild) {
		FinderPath finderPath = _finderPathCountByP_C;

		Object[] finderArgs = new Object[] {patcherFixId, childBuild};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_C_PATCHERFIXID_2);

			sb.append(_FINDER_COLUMN_P_C_CHILDBUILD_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixId);

				queryPos.add(childBuild);

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
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_C(long patcherFixId, boolean childBuild) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_C(patcherFixId, childBuild);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByP_C(
				patcherFixId, childBuild);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_C_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_C_CHILDBUILD_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixId);

			queryPos.add(childBuild);

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

	private static final String _FINDER_COLUMN_P_C_PATCHERFIXID_2 =
		"patcherBuild.patcherFixId = ? AND ";

	private static final String _FINDER_COLUMN_P_C_CHILDBUILD_2 =
		"patcherBuild.childBuild = ?";

	private FinderPath _finderPathFetchByK_KV;

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_KV(key, keyVersion);

		if (patcherBuild == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("key=");
			sb.append(key);

			sb.append(", keyVersion=");
			sb.append(keyVersion);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherBuildException(sb.toString());
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_KV(String key, double keyVersion) {
		return fetchByK_KV(key, keyVersion, true);
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_KV(
		String key, double keyVersion, boolean useFinderCache) {

		key = Objects.toString(key, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {key, keyVersion};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByK_KV, finderArgs, this);
		}

		if (result instanceof PatcherBuild) {
			PatcherBuild patcherBuild = (PatcherBuild)result;

			if (!Objects.equals(key, patcherBuild.getKey()) ||
				(keyVersion != patcherBuild.getKeyVersion())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_KV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_KV_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_KV_KEYVERSION_2);

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

				List<PatcherBuild> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByK_KV, finderArgs, list);
					}
				}
				else {
					PatcherBuild patcherBuild = list.get(0);

					result = patcherBuild;

					cacheResult(patcherBuild);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (PatcherBuild)result;
		}
	}

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 */
	@Override
	public PatcherBuild removeByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByK_KV(key, keyVersion);

		return remove(patcherBuild);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_KV(String key, double keyVersion) {
		PatcherBuild patcherBuild = fetchByK_KV(key, keyVersion);

		if (patcherBuild == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_K_KV_KEY_2 =
		"patcherBuild.key = ? AND ";

	private static final String _FINDER_COLUMN_K_KV_KEY_3 =
		"(patcherBuild.key IS NULL OR patcherBuild.key = '') AND ";

	private static final String _FINDER_COLUMN_K_KV_KEYVERSION_2 =
		"patcherBuild.keyVersion = ?";

	private FinderPath _finderPathWithPaginationFindByK_GtKV;
	private FinderPath _finderPathWithPaginationCountByK_GtKV;

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(String key, double keyVersion) {
		return findByK_GtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end) {

		return findByK_GtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByK_GtKV(
			key, keyVersion, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByK_GtKV;
		finderArgs = new Object[] {
			key, keyVersion, start, end, orderByComparator
		};

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (!key.equals(patcherBuild.getKey()) ||
						(keyVersion >= patcherBuild.getKeyVersion())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_GTKV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_GTKV_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_GTKV_KEYVERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_GtKV_First(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_GtKV_First(
			key, keyVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion>");
		sb.append(keyVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_GtKV_First(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByK_GtKV(
			key, keyVersion, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_GtKV_Last(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_GtKV_Last(
			key, keyVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion>");
		sb.append(keyVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_GtKV_Last(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByK_GtKV(key, keyVersion);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByK_GtKV(
			key, keyVersion, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByK_GtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByK_GtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = getByK_GtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
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

	protected PatcherBuild getByK_GtKV_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		double keyVersion, OrderByComparator<PatcherBuild> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_KEY_2);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_KEYVERSION_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion) {

		return filterFindByK_GtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end) {

		return filterFindByK_GtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_GtKV(key, keyVersion, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByK_GtKV(
					key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_KEYVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(keyVersion);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByK_GtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_GtKV_PrevAndNext(
				patcherBuildId, key, keyVersion, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByK_GtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = filterGetByK_GtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
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

	protected PatcherBuild filterGetByK_GtKV_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		double keyVersion, OrderByComparator<PatcherBuild> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_KEYVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(keyVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &gt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	@Override
	public void removeByK_GtKV(String key, double keyVersion) {
		for (PatcherBuild patcherBuild :
				findByK_GtKV(
					key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_GtKV(String key, double keyVersion) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathWithPaginationCountByK_GtKV;

		Object[] finderArgs = new Object[] {key, keyVersion};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_GTKV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_GTKV_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_GTKV_KEYVERSION_2);

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
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByK_GtKV(String key, double keyVersion) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByK_GtKV(key, keyVersion);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByK_GtKV(key, keyVersion);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_GTKV_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_GTKV_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_GTKV_KEYVERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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

	private static final String _FINDER_COLUMN_K_GTKV_KEY_2 =
		"patcherBuild.key = ? AND ";

	private static final String _FINDER_COLUMN_K_GTKV_KEY_3 =
		"(patcherBuild.key IS NULL OR patcherBuild.key = '') AND ";

	private static final String _FINDER_COLUMN_K_GTKV_KEY_2_SQL =
		"patcherBuild.key_ = ? AND ";

	private static final String _FINDER_COLUMN_K_GTKV_KEY_3_SQL =
		"(patcherBuild.key_ IS NULL OR patcherBuild.key_ = '') AND ";

	private static final String _FINDER_COLUMN_K_GTKV_KEYVERSION_2 =
		"patcherBuild.keyVersion > ?";

	private FinderPath _finderPathWithPaginationFindByK_LtKV;
	private FinderPath _finderPathWithPaginationCountByK_LtKV;

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(String key, double keyVersion) {
		return findByK_LtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end) {

		return findByK_LtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByK_LtKV(
			key, keyVersion, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByK_LtKV;
		finderArgs = new Object[] {
			key, keyVersion, start, end, orderByComparator
		};

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (!key.equals(patcherBuild.getKey()) ||
						(keyVersion <= patcherBuild.getKeyVersion())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_LTKV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_LTKV_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_LTKV_KEYVERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_LtKV_First(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_LtKV_First(
			key, keyVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion<");
		sb.append(keyVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_LtKV_First(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByK_LtKV(
			key, keyVersion, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_LtKV_Last(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_LtKV_Last(
			key, keyVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", keyVersion<");
		sb.append(keyVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_LtKV_Last(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByK_LtKV(key, keyVersion);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByK_LtKV(
			key, keyVersion, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByK_LtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByK_LtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = getByK_LtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
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

	protected PatcherBuild getByK_LtKV_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		double keyVersion, OrderByComparator<PatcherBuild> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_KEY_2);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_KEYVERSION_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion) {

		return filterFindByK_LtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end) {

		return filterFindByK_LtKV(key, keyVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_LtKV(key, keyVersion, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByK_LtKV(
					key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_KEYVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(keyVersion);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByK_LtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_LtKV_PrevAndNext(
				patcherBuildId, key, keyVersion, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByK_LtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = filterGetByK_LtKV_PrevAndNext(
				session, patcherBuild, key, keyVersion, orderByComparator,
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

	protected PatcherBuild filterGetByK_LtKV_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		double keyVersion, OrderByComparator<PatcherBuild> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_KEYVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(keyVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &lt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	@Override
	public void removeByK_LtKV(String key, double keyVersion) {
		for (PatcherBuild patcherBuild :
				findByK_LtKV(
					key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_LtKV(String key, double keyVersion) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathWithPaginationCountByK_LtKV;

		Object[] finderArgs = new Object[] {key, keyVersion};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_LTKV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_LTKV_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_LTKV_KEYVERSION_2);

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
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByK_LtKV(String key, double keyVersion) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByK_LtKV(key, keyVersion);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByK_LtKV(key, keyVersion);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_LTKV_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_LTKV_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_LTKV_KEYVERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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

	private static final String _FINDER_COLUMN_K_LTKV_KEY_2 =
		"patcherBuild.key = ? AND ";

	private static final String _FINDER_COLUMN_K_LTKV_KEY_3 =
		"(patcherBuild.key IS NULL OR patcherBuild.key = '') AND ";

	private static final String _FINDER_COLUMN_K_LTKV_KEY_2_SQL =
		"patcherBuild.key_ = ? AND ";

	private static final String _FINDER_COLUMN_K_LTKV_KEY_3_SQL =
		"(patcherBuild.key_ IS NULL OR patcherBuild.key_ = '') AND ";

	private static final String _FINDER_COLUMN_K_LTKV_KEYVERSION_2 =
		"patcherBuild.keyVersion < ?";

	private FinderPath _finderPathWithPaginationFindByK_L;
	private FinderPath _finderPathWithoutPaginationFindByK_L;
	private FinderPath _finderPathCountByK_L;

	/**
	 * Returns all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_L(String key, boolean latestKeyBuild) {
		return findByK_L(
			key, latestKeyBuild, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end) {

		return findByK_L(key, latestKeyBuild, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByK_L(
			key, latestKeyBuild, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByK_L;
				finderArgs = new Object[] {key, latestKeyBuild};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByK_L;
			finderArgs = new Object[] {
				key, latestKeyBuild, start, end, orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (!key.equals(patcherBuild.getKey()) ||
						(latestKeyBuild != patcherBuild.isLatestKeyBuild())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_L_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_L_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_L_LATESTKEYBUILD_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

				queryPos.add(latestKeyBuild);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_L_First(
			String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_L_First(
			key, latestKeyBuild, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", latestKeyBuild=");
		sb.append(latestKeyBuild);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_L_First(
		String key, boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByK_L(
			key, latestKeyBuild, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByK_L_Last(
			String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByK_L_Last(
			key, latestKeyBuild, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("key=");
		sb.append(key);

		sb.append(", latestKeyBuild=");
		sb.append(latestKeyBuild);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByK_L_Last(
		String key, boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByK_L(key, latestKeyBuild);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByK_L(
			key, latestKeyBuild, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByK_L_PrevAndNext(
			long patcherBuildId, String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByK_L_PrevAndNext(
				session, patcherBuild, key, latestKeyBuild, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = getByK_L_PrevAndNext(
				session, patcherBuild, key, latestKeyBuild, orderByComparator,
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

	protected PatcherBuild getByK_L_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_KEY_2);
		}

		sb.append(_FINDER_COLUMN_K_L_LATESTKEYBUILD_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(latestKeyBuild);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild) {

		return filterFindByK_L(
			key, latestKeyBuild, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end) {

		return filterFindByK_L(key, latestKeyBuild, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_L(
				key, latestKeyBuild, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByK_L(
					key, latestKeyBuild, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		key = Objects.toString(key, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_L_LATESTKEYBUILD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindKey) {
				queryPos.add(key);
			}

			queryPos.add(latestKeyBuild);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByK_L_PrevAndNext(
			long patcherBuildId, String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByK_L_PrevAndNext(
				patcherBuildId, key, latestKeyBuild, orderByComparator);
		}

		key = Objects.toString(key, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByK_L_PrevAndNext(
				session, patcherBuild, key, latestKeyBuild, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = filterGetByK_L_PrevAndNext(
				session, patcherBuild, key, latestKeyBuild, orderByComparator,
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

	protected PatcherBuild filterGetByK_L_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String key,
		boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_L_LATESTKEYBUILD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindKey) {
			queryPos.add(key);
		}

		queryPos.add(latestKeyBuild);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where key = &#63; and latestKeyBuild = &#63; from the database.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 */
	@Override
	public void removeByK_L(String key, boolean latestKeyBuild) {
		for (PatcherBuild patcherBuild :
				findByK_L(
					key, latestKeyBuild, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByK_L(String key, boolean latestKeyBuild) {
		key = Objects.toString(key, "");

		FinderPath finderPath = _finderPathCountByK_L;

		Object[] finderArgs = new Object[] {key, latestKeyBuild};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_K_L_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_K_L_KEY_2);
			}

			sb.append(_FINDER_COLUMN_K_L_LATESTKEYBUILD_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindKey) {
					queryPos.add(key);
				}

				queryPos.add(latestKeyBuild);

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
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByK_L(String key, boolean latestKeyBuild) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByK_L(key, latestKeyBuild);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByK_L(key, latestKeyBuild);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		key = Objects.toString(key, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_K_L_KEY_3_SQL);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_K_L_KEY_2_SQL);
		}

		sb.append(_FINDER_COLUMN_K_L_LATESTKEYBUILD_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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

			queryPos.add(latestKeyBuild);

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

	private static final String _FINDER_COLUMN_K_L_KEY_2 =
		"patcherBuild.key = ? AND ";

	private static final String _FINDER_COLUMN_K_L_KEY_3 =
		"(patcherBuild.key IS NULL OR patcherBuild.key = '') AND ";

	private static final String _FINDER_COLUMN_K_L_KEY_2_SQL =
		"patcherBuild.key_ = ? AND ";

	private static final String _FINDER_COLUMN_K_L_KEY_3_SQL =
		"(patcherBuild.key_ IS NULL OR patcherBuild.key_ = '') AND ";

	private static final String _FINDER_COLUMN_K_L_LATESTKEYBUILD_2 =
		"patcherBuild.latestKeyBuild = ?";

	private FinderPath _finderPathWithPaginationFindByL_S;
	private FinderPath _finderPathWithoutPaginationFindByL_S;
	private FinderPath _finderPathCountByL_S;

	/**
	 * Returns all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return findByL_S(
			latestSupportTicketBuild, supportTicket, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end) {

		return findByL_S(
			latestSupportTicketBuild, supportTicket, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return findByL_S(
			latestSupportTicketBuild, supportTicket, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		supportTicket = Objects.toString(supportTicket, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByL_S;
				finderArgs = new Object[] {
					latestSupportTicketBuild, supportTicket
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByL_S;
			finderArgs = new Object[] {
				latestSupportTicketBuild, supportTicket, start, end,
				orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((latestSupportTicketBuild !=
							patcherBuild.isLatestSupportTicketBuild()) ||
						!supportTicket.equals(
							patcherBuild.getSupportTicket())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2);

			boolean bindSupportTicket = false;

			if (supportTicket.isEmpty()) {
				sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_3);
			}
			else {
				bindSupportTicket = true;

				sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(latestSupportTicketBuild);

				if (bindSupportTicket) {
					queryPos.add(supportTicket);
				}

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByL_S_First(
			boolean latestSupportTicketBuild, String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByL_S_First(
			latestSupportTicketBuild, supportTicket, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("latestSupportTicketBuild=");
		sb.append(latestSupportTicketBuild);

		sb.append(", supportTicket=");
		sb.append(supportTicket);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByL_S_First(
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByL_S(
			latestSupportTicketBuild, supportTicket, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByL_S_Last(
			boolean latestSupportTicketBuild, String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByL_S_Last(
			latestSupportTicketBuild, supportTicket, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("latestSupportTicketBuild=");
		sb.append(latestSupportTicketBuild);

		sb.append(", supportTicket=");
		sb.append(supportTicket);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByL_S_Last(
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByL_S(latestSupportTicketBuild, supportTicket);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByL_S(
			latestSupportTicketBuild, supportTicket, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByL_S_PrevAndNext(
			long patcherBuildId, boolean latestSupportTicketBuild,
			String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		supportTicket = Objects.toString(supportTicket, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByL_S_PrevAndNext(
				session, patcherBuild, latestSupportTicketBuild, supportTicket,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByL_S_PrevAndNext(
				session, patcherBuild, latestSupportTicketBuild, supportTicket,
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

	protected PatcherBuild getByL_S_PrevAndNext(
		Session session, PatcherBuild patcherBuild,
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_2);
		}

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(latestSupportTicketBuild);

		if (bindSupportTicket) {
			queryPos.add(supportTicket);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return filterFindByL_S(
			latestSupportTicketBuild, supportTicket, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end) {

		return filterFindByL_S(
			latestSupportTicketBuild, supportTicket, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByL_S(
				latestSupportTicketBuild, supportTicket, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByL_S(
					latestSupportTicketBuild, supportTicket, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		supportTicket = Objects.toString(supportTicket, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(latestSupportTicketBuild);

			if (bindSupportTicket) {
				queryPos.add(supportTicket);
			}

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByL_S_PrevAndNext(
			long patcherBuildId, boolean latestSupportTicketBuild,
			String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByL_S_PrevAndNext(
				patcherBuildId, latestSupportTicketBuild, supportTicket,
				orderByComparator);
		}

		supportTicket = Objects.toString(supportTicket, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByL_S_PrevAndNext(
				session, patcherBuild, latestSupportTicketBuild, supportTicket,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByL_S_PrevAndNext(
				session, patcherBuild, latestSupportTicketBuild, supportTicket,
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

	protected PatcherBuild filterGetByL_S_PrevAndNext(
		Session session, PatcherBuild patcherBuild,
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(latestSupportTicketBuild);

		if (bindSupportTicket) {
			queryPos.add(supportTicket);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63; from the database.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 */
	@Override
	public void removeByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		for (PatcherBuild patcherBuild :
				findByL_S(
					latestSupportTicketBuild, supportTicket, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		supportTicket = Objects.toString(supportTicket, "");

		FinderPath finderPath = _finderPathCountByL_S;

		Object[] finderArgs = new Object[] {
			latestSupportTicketBuild, supportTicket
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2);

			boolean bindSupportTicket = false;

			if (supportTicket.isEmpty()) {
				sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_3);
			}
			else {
				bindSupportTicket = true;

				sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(latestSupportTicketBuild);

				if (bindSupportTicket) {
					queryPos.add(supportTicket);
				}

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
	 * Returns the number of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByL_S(latestSupportTicketBuild, supportTicket);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByL_S(
				latestSupportTicketBuild, supportTicket);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		supportTicket = Objects.toString(supportTicket, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_L_S_SUPPORTTICKET_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(latestSupportTicketBuild);

			if (bindSupportTicket) {
				queryPos.add(supportTicket);
			}

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

	private static final String _FINDER_COLUMN_L_S_LATESTSUPPORTTICKETBUILD_2 =
		"patcherBuild.latestSupportTicketBuild = ? AND ";

	private static final String _FINDER_COLUMN_L_S_SUPPORTTICKET_2 =
		"patcherBuild.supportTicket = ?";

	private static final String _FINDER_COLUMN_L_S_SUPPORTTICKET_3 =
		"(patcherBuild.supportTicket IS NULL OR patcherBuild.supportTicket = '')";

	private FinderPath _finderPathWithPaginationFindByS_GtS;
	private FinderPath _finderPathWithPaginationCountByS_GtS;

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return findByS_GtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return findByS_GtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByS_GtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		supportTicket = Objects.toString(supportTicket, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByS_GtS;
		finderArgs = new Object[] {
			supportTicket, supportTicketVersion, start, end, orderByComparator
		};

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (!supportTicket.equals(
							patcherBuild.getSupportTicket()) ||
						(supportTicketVersion >=
							patcherBuild.getSupportTicketVersion())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindSupportTicket = false;

			if (supportTicket.isEmpty()) {
				sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_3);
			}
			else {
				bindSupportTicket = true;

				sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_2);
			}

			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSupportTicket) {
					queryPos.add(supportTicket);
				}

				queryPos.add(supportTicketVersion);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByS_GtS_First(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByS_GtS_First(
			supportTicket, supportTicketVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("supportTicket=");
		sb.append(supportTicket);

		sb.append(", supportTicketVersion>");
		sb.append(supportTicketVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByS_GtS_First(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByS_GtS(
			supportTicket, supportTicketVersion, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByS_GtS_Last(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByS_GtS_Last(
			supportTicket, supportTicketVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("supportTicket=");
		sb.append(supportTicket);

		sb.append(", supportTicketVersion>");
		sb.append(supportTicketVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByS_GtS_Last(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByS_GtS(supportTicket, supportTicketVersion);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByS_GtS(
			supportTicket, supportTicketVersion, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByS_GtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		supportTicket = Objects.toString(supportTicket, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByS_GtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByS_GtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
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

	protected PatcherBuild getByS_GtS_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String supportTicket,
		double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindSupportTicket) {
			queryPos.add(supportTicket);
		}

		queryPos.add(supportTicketVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return filterFindByS_GtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return filterFindByS_GtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByS_GtS(
				supportTicket, supportTicketVersion, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByS_GtS(
					supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		supportTicket = Objects.toString(supportTicket, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindSupportTicket) {
				queryPos.add(supportTicket);
			}

			queryPos.add(supportTicketVersion);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByS_GtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByS_GtS_PrevAndNext(
				patcherBuildId, supportTicket, supportTicketVersion,
				orderByComparator);
		}

		supportTicket = Objects.toString(supportTicket, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByS_GtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByS_GtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
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

	protected PatcherBuild filterGetByS_GtS_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String supportTicket,
		double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindSupportTicket) {
			queryPos.add(supportTicket);
		}

		queryPos.add(supportTicketVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	@Override
	public void removeByS_GtS(
		String supportTicket, double supportTicketVersion) {

		for (PatcherBuild patcherBuild :
				findByS_GtS(
					supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByS_GtS(String supportTicket, double supportTicketVersion) {
		supportTicket = Objects.toString(supportTicket, "");

		FinderPath finderPath = _finderPathWithPaginationCountByS_GtS;

		Object[] finderArgs = new Object[] {
			supportTicket, supportTicketVersion
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindSupportTicket = false;

			if (supportTicket.isEmpty()) {
				sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_3);
			}
			else {
				bindSupportTicket = true;

				sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_2);
			}

			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSupportTicket) {
					queryPos.add(supportTicket);
				}

				queryPos.add(supportTicketVersion);

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
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByS_GtS(
		String supportTicket, double supportTicketVersion) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByS_GtS(supportTicket, supportTicketVersion);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByS_GtS(
				supportTicket, supportTicketVersion);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		supportTicket = Objects.toString(supportTicket, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindSupportTicket) {
				queryPos.add(supportTicket);
			}

			queryPos.add(supportTicketVersion);

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

	private static final String _FINDER_COLUMN_S_GTS_SUPPORTTICKET_2 =
		"patcherBuild.supportTicket = ? AND ";

	private static final String _FINDER_COLUMN_S_GTS_SUPPORTTICKET_3 =
		"(patcherBuild.supportTicket IS NULL OR patcherBuild.supportTicket = '') AND ";

	private static final String _FINDER_COLUMN_S_GTS_SUPPORTTICKETVERSION_2 =
		"patcherBuild.supportTicketVersion > ?";

	private FinderPath _finderPathWithPaginationFindByS_LtS;
	private FinderPath _finderPathWithPaginationCountByS_LtS;

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return findByS_LtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return findByS_LtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByS_LtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		supportTicket = Objects.toString(supportTicket, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByS_LtS;
		finderArgs = new Object[] {
			supportTicket, supportTicketVersion, start, end, orderByComparator
		};

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if (!supportTicket.equals(
							patcherBuild.getSupportTicket()) ||
						(supportTicketVersion <=
							patcherBuild.getSupportTicketVersion())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindSupportTicket = false;

			if (supportTicket.isEmpty()) {
				sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_3);
			}
			else {
				bindSupportTicket = true;

				sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_2);
			}

			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSupportTicket) {
					queryPos.add(supportTicket);
				}

				queryPos.add(supportTicketVersion);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByS_LtS_First(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByS_LtS_First(
			supportTicket, supportTicketVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("supportTicket=");
		sb.append(supportTicket);

		sb.append(", supportTicketVersion<");
		sb.append(supportTicketVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByS_LtS_First(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByS_LtS(
			supportTicket, supportTicketVersion, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByS_LtS_Last(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByS_LtS_Last(
			supportTicket, supportTicketVersion, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("supportTicket=");
		sb.append(supportTicket);

		sb.append(", supportTicketVersion<");
		sb.append(supportTicketVersion);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByS_LtS_Last(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByS_LtS(supportTicket, supportTicketVersion);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByS_LtS(
			supportTicket, supportTicketVersion, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByS_LtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		supportTicket = Objects.toString(supportTicket, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByS_LtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByS_LtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
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

	protected PatcherBuild getByS_LtS_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String supportTicket,
		double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindSupportTicket) {
			queryPos.add(supportTicket);
		}

		queryPos.add(supportTicketVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return filterFindByS_LtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return filterFindByS_LtS(
			supportTicket, supportTicketVersion, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByS_LtS(
				supportTicket, supportTicketVersion, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByS_LtS(
					supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		supportTicket = Objects.toString(supportTicket, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindSupportTicket) {
				queryPos.add(supportTicket);
			}

			queryPos.add(supportTicketVersion);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByS_LtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByS_LtS_PrevAndNext(
				patcherBuildId, supportTicket, supportTicketVersion,
				orderByComparator);
		}

		supportTicket = Objects.toString(supportTicket, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByS_LtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByS_LtS_PrevAndNext(
				session, patcherBuild, supportTicket, supportTicketVersion,
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

	protected PatcherBuild filterGetByS_LtS_PrevAndNext(
		Session session, PatcherBuild patcherBuild, String supportTicket,
		double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindSupportTicket) {
			queryPos.add(supportTicket);
		}

		queryPos.add(supportTicketVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	@Override
	public void removeByS_LtS(
		String supportTicket, double supportTicketVersion) {

		for (PatcherBuild patcherBuild :
				findByS_LtS(
					supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByS_LtS(String supportTicket, double supportTicketVersion) {
		supportTicket = Objects.toString(supportTicket, "");

		FinderPath finderPath = _finderPathWithPaginationCountByS_LtS;

		Object[] finderArgs = new Object[] {
			supportTicket, supportTicketVersion
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindSupportTicket = false;

			if (supportTicket.isEmpty()) {
				sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_3);
			}
			else {
				bindSupportTicket = true;

				sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_2);
			}

			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSupportTicket) {
					queryPos.add(supportTicket);
				}

				queryPos.add(supportTicketVersion);

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
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByS_LtS(
		String supportTicket, double supportTicketVersion) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByS_LtS(supportTicket, supportTicketVersion);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByS_LtS(
				supportTicket, supportTicketVersion);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		supportTicket = Objects.toString(supportTicket, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindSupportTicket = false;

		if (supportTicket.isEmpty()) {
			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_3);
		}
		else {
			bindSupportTicket = true;

			sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKET_2);
		}

		sb.append(_FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindSupportTicket) {
				queryPos.add(supportTicket);
			}

			queryPos.add(supportTicketVersion);

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

	private static final String _FINDER_COLUMN_S_LTS_SUPPORTTICKET_2 =
		"patcherBuild.supportTicket = ? AND ";

	private static final String _FINDER_COLUMN_S_LTS_SUPPORTTICKET_3 =
		"(patcherBuild.supportTicket IS NULL OR patcherBuild.supportTicket = '') AND ";

	private static final String _FINDER_COLUMN_S_LTS_SUPPORTTICKETVERSION_2 =
		"patcherBuild.supportTicketVersion < ?";

	private FinderPath _finderPathWithPaginationFindByLtM_N_S;
	private FinderPath _finderPathWithPaginationCountByLtM_N_S;

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return findByLtM_N_S(
			modifiedDate, notified, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end) {

		return findByLtM_N_S(modifiedDate, notified, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByLtM_N_S(
			modifiedDate, notified, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtM_N_S;
		finderArgs = new Object[] {
			_getTime(modifiedDate), notified, status, start, end,
			orderByComparator
		};

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((modifiedDate.getTime() <= patcherBuild.getModifiedDate(
						).getTime()) ||
						(notified != patcherBuild.isNotified()) ||
						(status != patcherBuild.getStatus())) {

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

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

			sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByLtM_N_S_First(
			Date modifiedDate, boolean notified, int status,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByLtM_N_S_First(
			modifiedDate, notified, status, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("modifiedDate<");
		sb.append(modifiedDate);

		sb.append(", notified=");
		sb.append(notified);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByLtM_N_S_First(
		Date modifiedDate, boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByLtM_N_S(
			modifiedDate, notified, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByLtM_N_S_Last(
			Date modifiedDate, boolean notified, int status,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByLtM_N_S_Last(
			modifiedDate, notified, status, orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("modifiedDate<");
		sb.append(modifiedDate);

		sb.append(", notified=");
		sb.append(notified);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByLtM_N_S_Last(
		Date modifiedDate, boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByLtM_N_S(modifiedDate, notified, status);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByLtM_N_S(
			modifiedDate, notified, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByLtM_N_S_PrevAndNext(
			long patcherBuildId, Date modifiedDate, boolean notified,
			int status, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByLtM_N_S_PrevAndNext(
				session, patcherBuild, modifiedDate, notified, status,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByLtM_N_S_PrevAndNext(
				session, patcherBuild, modifiedDate, notified, status,
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

	protected PatcherBuild getByLtM_N_S_PrevAndNext(
		Session session, PatcherBuild patcherBuild, Date modifiedDate,
		boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtM_N_S(
				modifiedDate, notified, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtM_N_S(
					modifiedDate, notified, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindModifiedDate) {
				queryPos.add(new Timestamp(modifiedDate.getTime()));
			}

			queryPos.add(notified);

			queryPos.add(status);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByLtM_N_S_PrevAndNext(
			long patcherBuildId, Date modifiedDate, boolean notified,
			int status, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtM_N_S_PrevAndNext(
				patcherBuildId, modifiedDate, notified, status,
				orderByComparator);
		}

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByLtM_N_S_PrevAndNext(
				session, patcherBuild, modifiedDate, notified, status,
				orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByLtM_N_S_PrevAndNext(
				session, patcherBuild, modifiedDate, notified, status,
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

	protected PatcherBuild filterGetByLtM_N_S_PrevAndNext(
		Session session, PatcherBuild patcherBuild, Date modifiedDate,
		boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindModifiedDate) {
			queryPos.add(new Timestamp(modifiedDate.getTime()));
		}

		queryPos.add(notified);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, statuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end) {

		return filterFindByLtM_N_S(
			modifiedDate, notified, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtM_N_S(
				modifiedDate, notified, statuses, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtM_N_S(
					modifiedDate, notified, statuses, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_7);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindModifiedDate) {
				queryPos.add(new Timestamp(modifiedDate.getTime()));
			}

			queryPos.add(notified);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return findByLtM_N_S(
			modifiedDate, notified, statuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end) {

		return findByLtM_N_S(
			modifiedDate, notified, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByLtM_N_S(
			modifiedDate, notified, statuses, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		if (statuses.length == 1) {
			return findByLtM_N_S(
				modifiedDate, notified, statuses[0], start, end,
				orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {
					_getTime(modifiedDate), notified, StringUtil.merge(statuses)
				};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				_getTime(modifiedDate), notified, StringUtil.merge(statuses),
				start, end, orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				_finderPathWithPaginationFindByLtM_N_S, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((modifiedDate.getTime() <= patcherBuild.getModifiedDate(
						).getTime()) ||
						(notified != patcherBuild.isNotified()) ||
						!ArrayUtil.contains(
							statuses, patcherBuild.getStatus())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

			if (statuses.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_7);

				sb.append(StringUtil.merge(statuses));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
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

				list = (List<PatcherBuild>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByLtM_N_S, finderArgs,
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
	 * Removes all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 */
	@Override
	public void removeByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		for (PatcherBuild patcherBuild :
				findByLtM_N_S(
					modifiedDate, notified, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByLtM_N_S(Date modifiedDate, boolean notified, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtM_N_S;

		Object[] finderArgs = new Object[] {
			_getTime(modifiedDate), notified, status
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

			sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_2);

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
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		Object[] finderArgs = new Object[] {
			_getTime(modifiedDate), notified, StringUtil.merge(statuses)
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByLtM_N_S, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindModifiedDate = false;

			if (modifiedDate == null) {
				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
			}
			else {
				bindModifiedDate = true;

				sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

			if (statuses.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_7);

				sb.append(StringUtil.merge(statuses));

				sb.append(")");

				sb.append(")");
			}

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

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathWithPaginationCountByLtM_N_S, finderArgs, count);
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
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtM_N_S(modifiedDate, notified, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByLtM_N_S(
				modifiedDate, notified, status);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

		sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtM_N_S(modifiedDate, notified, statuses);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = InlineSQLHelperUtil.filter(
				findByLtM_N_S(modifiedDate, notified, statuses));

			return patcherBuilds.size();
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		boolean bindModifiedDate = false;

		if (modifiedDate == null) {
			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1);
		}
		else {
			bindModifiedDate = true;

			sb.append(_FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTM_N_S_NOTIFIED_2);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_LTM_N_S_STATUS_7);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
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

	private static final String _FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_1 =
		"patcherBuild.modifiedDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTM_N_S_MODIFIEDDATE_2 =
		"patcherBuild.modifiedDate < ? AND ";

	private static final String _FINDER_COLUMN_LTM_N_S_NOTIFIED_2 =
		"patcherBuild.notified = ? AND ";

	private static final String _FINDER_COLUMN_LTM_N_S_STATUS_2 =
		"patcherBuild.status = ?";

	private static final String _FINDER_COLUMN_LTM_N_S_STATUS_7 =
		"patcherBuild.status IN (";

	private FinderPath _finderPathWithPaginationFindByP_NotP_C_NotT;
	private FinderPath _finderPathWithPaginationCountByP_NotP_C_NotT;

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end) {

		return findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByP_NotP_C_NotT;
		finderArgs = new Object[] {
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			orderByComparator
		};

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((patcherFixId != patcherBuild.getPatcherFixId()) ||
						(patcherProductVersionId ==
							patcherBuild.getPatcherProductVersionId()) ||
						(childBuild != patcherBuild.isChildBuild()) ||
						(type == patcherBuild.getType())) {

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

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixId);

				queryPos.add(patcherProductVersionId);

				queryPos.add(childBuild);

				queryPos.add(type);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_NotP_C_NotT_First(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_NotP_C_NotT_First(
			patcherFixId, patcherProductVersionId, childBuild, type,
			orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixId=");
		sb.append(patcherFixId);

		sb.append(", patcherProductVersionId!=");
		sb.append(patcherProductVersionId);

		sb.append(", childBuild=");
		sb.append(childBuild);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_NotP_C_NotT_First(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_NotP_C_NotT_Last(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type, OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_NotP_C_NotT_Last(
			patcherFixId, patcherProductVersionId, childBuild, type,
			orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixId=");
		sb.append(patcherFixId);

		sb.append(", patcherProductVersionId!=");
		sb.append(patcherProductVersionId);

		sb.append(", childBuild=");
		sb.append(childBuild);

		sb.append(", type!=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_NotP_C_NotT_Last(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByP_NotP_C_NotT_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			long patcherProductVersionId, boolean childBuild, int type,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByP_NotP_C_NotT_PrevAndNext(
				session, patcherBuild, patcherFixId, patcherProductVersionId,
				childBuild, type, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = getByP_NotP_C_NotT_PrevAndNext(
				session, patcherBuild, patcherFixId, patcherProductVersionId,
				childBuild, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild getByP_NotP_C_NotT_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherFixId,
		long patcherProductVersionId, boolean childBuild, int type,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2);

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixId);

		queryPos.add(patcherProductVersionId);

		queryPos.add(childBuild);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end) {

		return filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_NotP_C_NotT(
				patcherFixId, patcherProductVersionId, childBuild, type, start,
				end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_NotP_C_NotT(
					patcherFixId, patcherProductVersionId, childBuild, type,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixId);

			queryPos.add(patcherProductVersionId);

			queryPos.add(childBuild);

			queryPos.add(type);

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByP_NotP_C_NotT_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			long patcherProductVersionId, boolean childBuild, int type,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_NotP_C_NotT_PrevAndNext(
				patcherBuildId, patcherFixId, patcherProductVersionId,
				childBuild, type, orderByComparator);
		}

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByP_NotP_C_NotT_PrevAndNext(
				session, patcherBuild, patcherFixId, patcherProductVersionId,
				childBuild, type, orderByComparator, true);

			array[1] = patcherBuild;

			array[2] = filterGetByP_NotP_C_NotT_PrevAndNext(
				session, patcherBuild, patcherFixId, patcherProductVersionId,
				childBuild, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherBuild filterGetByP_NotP_C_NotT_PrevAndNext(
		Session session, PatcherBuild patcherBuild, long patcherFixId,
		long patcherProductVersionId, boolean childBuild, int type,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixId);

		queryPos.add(patcherProductVersionId);

		queryPos.add(childBuild);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 */
	@Override
	public void removeByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		for (PatcherBuild patcherBuild :
				findByP_NotP_C_NotT(
					patcherFixId, patcherProductVersionId, childBuild, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		FinderPath finderPath = _finderPathWithPaginationCountByP_NotP_C_NotT;

		Object[] finderArgs = new Object[] {
			patcherFixId, patcherProductVersionId, childBuild, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2);

			sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixId);

				queryPos.add(patcherProductVersionId);

				queryPos.add(childBuild);

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
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_NotP_C_NotT(
				patcherFixId, patcherProductVersionId, childBuild, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByP_NotP_C_NotT(
				patcherFixId, patcherProductVersionId, childBuild, type);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2);

		sb.append(_FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixId);

			queryPos.add(patcherProductVersionId);

			queryPos.add(childBuild);

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

	private static final String _FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERFIXID_2 =
		"patcherBuild.patcherFixId = ? AND ";

	private static final String
		_FINDER_COLUMN_P_NOTP_C_NOTT_PATCHERPRODUCTVERSIONID_2 =
			"patcherBuild.patcherProductVersionId != ? AND ";

	private static final String _FINDER_COLUMN_P_NOTP_C_NOTT_CHILDBUILD_2 =
		"patcherBuild.childBuild = ? AND ";

	private static final String _FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2 =
		"patcherBuild.type != ?";

	private static final String _FINDER_COLUMN_P_NOTP_C_NOTT_TYPE_2_SQL =
		"patcherBuild.type_ != ?";

	private FinderPath _finderPathWithPaginationFindByP_N_L_A;
	private FinderPath _finderPathWithoutPaginationFindByP_N_L_A;
	private FinderPath _finderPathCountByP_N_L_A;

	/**
	 * Returns all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end) {

		return findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	@Override
	public List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		accountEntryCode = Objects.toString(accountEntryCode, "");
		name = Objects.toString(name, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_N_L_A;
				finderArgs = new Object[] {
					patcherProjectVersionId, accountEntryCode, latestKeyBuild,
					name
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_N_L_A;
			finderArgs = new Object[] {
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
				start, end, orderByComparator
			};
		}

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherBuild patcherBuild : list) {
					if ((patcherProjectVersionId !=
							patcherBuild.getPatcherProjectVersionId()) ||
						!accountEntryCode.equals(
							patcherBuild.getAccountEntryCode()) ||
						(latestKeyBuild != patcherBuild.isLatestKeyBuild()) ||
						!name.equals(patcherBuild.getName())) {

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

			sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2);

			boolean bindAccountEntryCode = false;

			if (accountEntryCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3);
			}
			else {
				bindAccountEntryCode = true;

				sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2);
			}

			sb.append(_FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_N_L_A_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_P_N_L_A_NAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				if (bindAccountEntryCode) {
					queryPos.add(accountEntryCode);
				}

				queryPos.add(latestKeyBuild);

				if (bindName) {
					queryPos.add(name);
				}

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_N_L_A_First(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_N_L_A_First(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", accountEntryCode=");
		sb.append(accountEntryCode);

		sb.append(", latestKeyBuild=");
		sb.append(latestKeyBuild);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_N_L_A_First(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator) {

		List<PatcherBuild> list = findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name, 0,
			1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild findByP_N_L_A_Last(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByP_N_L_A_Last(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			orderByComparator);

		if (patcherBuild != null) {
			return patcherBuild;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", accountEntryCode=");
		sb.append(accountEntryCode);

		sb.append(", latestKeyBuild=");
		sb.append(latestKeyBuild);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchPatcherBuildException(sb.toString());
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	@Override
	public PatcherBuild fetchByP_N_L_A_Last(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator) {

		int count = countByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name);

		if (count == 0) {
			return null;
		}

		List<PatcherBuild> list = findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] findByP_N_L_A_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			String accountEntryCode, boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		accountEntryCode = Objects.toString(accountEntryCode, "");
		name = Objects.toString(name, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = getByP_N_L_A_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
				accountEntryCode, latestKeyBuild, name, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = getByP_N_L_A_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
				accountEntryCode, latestKeyBuild, name, orderByComparator,
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

	protected PatcherBuild getByP_N_L_A_PrevAndNext(
		Session session, PatcherBuild patcherBuild,
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2);
		}

		sb.append(_FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_2);
		}

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
			sb.append(PatcherBuildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		if (bindAccountEntryCode) {
			queryPos.add(accountEntryCode);
		}

		queryPos.add(latestKeyBuild);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return filterFindByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end) {

		return filterFindByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	@Override
	public List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_N_L_A(
				patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
				start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_N_L_A(
					patcherProjectVersionId, accountEntryCode, latestKeyBuild,
					name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		accountEntryCode = Objects.toString(accountEntryCode, "");
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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2);
		}

		sb.append(_FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			if (bindAccountEntryCode) {
				queryPos.add(accountEntryCode);
			}

			queryPos.add(latestKeyBuild);

			if (bindName) {
				queryPos.add(name);
			}

			return (List<PatcherBuild>)QueryUtil.list(
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
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild[] filterFindByP_N_L_A_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			String accountEntryCode, boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws NoSuchPatcherBuildException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_N_L_A_PrevAndNext(
				patcherBuildId, patcherProjectVersionId, accountEntryCode,
				latestKeyBuild, name, orderByComparator);
		}

		accountEntryCode = Objects.toString(accountEntryCode, "");
		name = Objects.toString(name, "");

		PatcherBuild patcherBuild = findByPrimaryKey(patcherBuildId);

		Session session = null;

		try {
			session = openSession();

			PatcherBuild[] array = new PatcherBuildImpl[3];

			array[0] = filterGetByP_N_L_A_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
				accountEntryCode, latestKeyBuild, name, orderByComparator,
				true);

			array[1] = patcherBuild;

			array[2] = filterGetByP_N_L_A_PrevAndNext(
				session, patcherBuild, patcherProjectVersionId,
				accountEntryCode, latestKeyBuild, name, orderByComparator,
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

	protected PatcherBuild filterGetByP_N_L_A_PrevAndNext(
		Session session, PatcherBuild patcherBuild,
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERBUILD_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2);
		}

		sb.append(_FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherBuildModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherBuildImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherBuildImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		if (bindAccountEntryCode) {
			queryPos.add(accountEntryCode);
		}

		queryPos.add(latestKeyBuild);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(patcherBuild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherBuild> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 */
	@Override
	public void removeByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		for (PatcherBuild patcherBuild :
				findByP_N_L_A(
					patcherProjectVersionId, accountEntryCode, latestKeyBuild,
					name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds
	 */
	@Override
	public int countByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		accountEntryCode = Objects.toString(accountEntryCode, "");
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByP_N_L_A;

		Object[] finderArgs = new Object[] {
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			sb.append(_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2);

			boolean bindAccountEntryCode = false;

			if (accountEntryCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3);
			}
			else {
				bindAccountEntryCode = true;

				sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2);
			}

			sb.append(_FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_N_L_A_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_P_N_L_A_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				if (bindAccountEntryCode) {
					queryPos.add(accountEntryCode);
				}

				queryPos.add(latestKeyBuild);

				if (bindName) {
					queryPos.add(name);
				}

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
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	@Override
	public int filterCountByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_N_L_A(
				patcherProjectVersionId, accountEntryCode, latestKeyBuild,
				name);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherBuild> patcherBuilds = findByP_N_L_A(
				patcherProjectVersionId, accountEntryCode, latestKeyBuild,
				name);

			patcherBuilds = InlineSQLHelperUtil.filter(patcherBuilds);

			return patcherBuilds.size();
		}

		accountEntryCode = Objects.toString(accountEntryCode, "");
		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PATCHERBUILD_WHERE);

		sb.append(_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2);
		}

		sb.append(_FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_N_L_A_NAME_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherBuild.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			if (bindAccountEntryCode) {
				queryPos.add(accountEntryCode);
			}

			queryPos.add(latestKeyBuild);

			if (bindName) {
				queryPos.add(name);
			}

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
		_FINDER_COLUMN_P_N_L_A_PATCHERPROJECTVERSIONID_2 =
			"patcherBuild.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_2 =
		"patcherBuild.accountEntryCode = ? AND ";

	private static final String _FINDER_COLUMN_P_N_L_A_ACCOUNTENTRYCODE_3 =
		"(patcherBuild.accountEntryCode IS NULL OR patcherBuild.accountEntryCode = '') AND ";

	private static final String _FINDER_COLUMN_P_N_L_A_LATESTKEYBUILD_2 =
		"patcherBuild.latestKeyBuild = ? AND ";

	private static final String _FINDER_COLUMN_P_N_L_A_NAME_2 =
		"patcherBuild.name = ?";

	private static final String _FINDER_COLUMN_P_N_L_A_NAME_3 =
		"(patcherBuild.name IS NULL OR patcherBuild.name = '')";

	public PatcherBuildPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PatcherBuild.class);

		setModelImplClass(PatcherBuildImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherBuildTable.INSTANCE);
	}

	/**
	 * Caches the patcher build in the entity cache if it is enabled.
	 *
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void cacheResult(PatcherBuild patcherBuild) {
		entityCache.putResult(
			PatcherBuildImpl.class, patcherBuild.getPrimaryKey(), patcherBuild);

		finderCache.putResult(
			_finderPathFetchByK_KV,
			new Object[] {patcherBuild.getKey(), patcherBuild.getKeyVersion()},
			patcherBuild);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher builds in the entity cache if it is enabled.
	 *
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void cacheResult(List<PatcherBuild> patcherBuilds) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherBuilds.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherBuild patcherBuild : patcherBuilds) {
			if (entityCache.getResult(
					PatcherBuildImpl.class, patcherBuild.getPrimaryKey()) ==
						null) {

				cacheResult(patcherBuild);
			}
		}
	}

	/**
	 * Clears the cache for all patcher builds.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherBuildImpl.class);

		finderCache.clearCache(PatcherBuildImpl.class);
	}

	/**
	 * Clears the cache for the patcher build.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherBuild patcherBuild) {
		entityCache.removeResult(PatcherBuildImpl.class, patcherBuild);
	}

	@Override
	public void clearCache(List<PatcherBuild> patcherBuilds) {
		for (PatcherBuild patcherBuild : patcherBuilds) {
			entityCache.removeResult(PatcherBuildImpl.class, patcherBuild);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherBuildImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherBuildImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherBuildModelImpl patcherBuildModelImpl) {

		Object[] args = new Object[] {
			patcherBuildModelImpl.getKey(),
			patcherBuildModelImpl.getKeyVersion()
		};

		finderCache.putResult(
			_finderPathFetchByK_KV, args, patcherBuildModelImpl);
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	@Override
	public PatcherBuild create(long patcherBuildId) {
		PatcherBuild patcherBuild = new PatcherBuildImpl();

		patcherBuild.setNew(true);
		patcherBuild.setPrimaryKey(patcherBuildId);

		patcherBuild.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherBuild;
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild remove(long patcherBuildId)
		throws NoSuchPatcherBuildException {

		return remove((Serializable)patcherBuildId);
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild remove(Serializable primaryKey)
		throws NoSuchPatcherBuildException {

		Session session = null;

		try {
			session = openSession();

			PatcherBuild patcherBuild = (PatcherBuild)session.get(
				PatcherBuildImpl.class, primaryKey);

			if (patcherBuild == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherBuildException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherBuild);
		}
		catch (NoSuchPatcherBuildException noSuchEntityException) {
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
	protected PatcherBuild removeImpl(PatcherBuild patcherBuild) {
		patcherBuildToPatcherAccountTableMapper.
			deleteLeftPrimaryKeyTableMappings(patcherBuild.getPrimaryKey());

		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherBuild.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuild)) {
				patcherBuild = (PatcherBuild)session.get(
					PatcherBuildImpl.class, patcherBuild.getPrimaryKeyObj());
			}

			if (patcherBuild != null) {
				session.delete(patcherBuild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuild != null) {
			clearCache(patcherBuild);
		}

		return patcherBuild;
	}

	@Override
	public PatcherBuild updateImpl(PatcherBuild patcherBuild) {
		boolean isNew = patcherBuild.isNew();

		if (!(patcherBuild instanceof PatcherBuildModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherBuild.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherBuild);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherBuild proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherBuild implementation " +
					patcherBuild.getClass());
		}

		PatcherBuildModelImpl patcherBuildModelImpl =
			(PatcherBuildModelImpl)patcherBuild;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherBuild.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherBuild.setCreateDate(date);
			}
			else {
				patcherBuild.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!patcherBuildModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherBuild.setModifiedDate(date);
			}
			else {
				patcherBuild.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherBuild);
			}
			else {
				patcherBuild = (PatcherBuild)session.merge(patcherBuild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherBuildImpl.class, patcherBuildModelImpl, false, true);

		cacheUniqueFindersCache(patcherBuildModelImpl);

		if (isNew) {
			patcherBuild.setNew(false);
		}

		patcherBuild.resetOriginalValues();

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherBuildException {

		PatcherBuild patcherBuild = fetchByPrimaryKey(primaryKey);

		if (patcherBuild == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherBuildException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws NoSuchPatcherBuildException {

		return findByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 */
	@Override
	public PatcherBuild fetchByPrimaryKey(long patcherBuildId) {
		return fetchByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns all the patcher builds.
	 *
	 * @return the patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll(
		int start, int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher builds
	 */
	@Override
	public List<PatcherBuild> findAll(
		int start, int end, OrderByComparator<PatcherBuild> orderByComparator,
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

		List<PatcherBuild> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuild>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERBUILD);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERBUILD;

				sql = sql.concat(PatcherBuildModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherBuild>)QueryUtil.list(
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
	 * Removes all the patcher builds from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherBuild patcherBuild : findAll()) {
			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERBUILD);

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
	 * Returns the primaryKeys of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher accounts associated with the patcher build
	 */
	@Override
	public long[] getPatcherAccountPrimaryKeys(long pk) {
		long[] pks =
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(long pk) {
		return getPatcherAccountPatcherBuilds(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end) {

		return getPatcherAccountPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 */
	@Override
	public List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildToPatcherAccountTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 */
	@Override
	public int getPatcherAccountsSize(long pk) {
		long[] pks =
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherAccount(long pk, long patcherAccountPK) {
		return patcherBuildToPatcherAccountTableMapper.containsTableMapping(
			pk, patcherAccountPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherAccounts(long pk) {
		if (getPatcherAccountsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherAccount(long pk, long patcherAccountPK) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherAccountPK);
		}
		else {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherAccountPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherAccount(long pk, PatcherAccount patcherAccount) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherAccount.getPrimaryKey());
		}
		else {
			return patcherBuildToPatcherAccountTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk,
				patcherAccount.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherAccounts(long pk, long[] patcherAccountPKs) {
		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		long[] addedKeys =
			patcherBuildToPatcherAccountTableMapper.addTableMappings(
				companyId, pk, patcherAccountPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		return addPatcherAccounts(
			pk,
			ListUtil.toLongArray(
				patcherAccounts, PatcherAccount.PATCHER_ACCOUNT_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 */
	@Override
	public void clearPatcherAccounts(long pk) {
		patcherBuildToPatcherAccountTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 */
	@Override
	public void removePatcherAccount(long pk, long patcherAccountPK) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(
			pk, patcherAccountPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 */
	@Override
	public void removePatcherAccount(long pk, PatcherAccount patcherAccount) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(
			pk, patcherAccount.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 */
	@Override
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs) {
		patcherBuildToPatcherAccountTableMapper.deleteTableMappings(
			pk, patcherAccountPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 */
	@Override
	public void removePatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		removePatcherAccounts(
			pk,
			ListUtil.toLongArray(
				patcherAccounts, PatcherAccount.PATCHER_ACCOUNT_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 */
	@Override
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs) {
		Set<Long> newPatcherAccountPKsSet = SetUtil.fromArray(
			patcherAccountPKs);
		Set<Long> oldPatcherAccountPKsSet = SetUtil.fromArray(
			patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherAccountPKsSet = new HashSet<Long>(
			oldPatcherAccountPKsSet);

		removePatcherAccountPKsSet.removeAll(newPatcherAccountPKsSet);

		patcherBuildToPatcherAccountTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherAccountPKsSet));

		newPatcherAccountPKsSet.removeAll(oldPatcherAccountPKsSet);

		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		patcherBuildToPatcherAccountTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherAccountPKsSet));
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 */
	@Override
	public void setPatcherAccounts(
		long pk, List<PatcherAccount> patcherAccounts) {

		try {
			long[] patcherAccountPKs = new long[patcherAccounts.size()];

			for (int i = 0; i < patcherAccounts.size(); i++) {
				PatcherAccount patcherAccount = patcherAccounts.get(i);

				patcherAccountPKs[i] = patcherAccount.getPrimaryKey();
			}

			setPatcherAccounts(pk, patcherAccountPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher build
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long pk) {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(long pk) {
		return getPatcherFixPatcherBuilds(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end) {

		return getPatcherFixPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 */
	@Override
	public List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildToPatcherFixTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixes associated with the patcher build
	 */
	@Override
	public int getPatcherFixesSize(long pk) {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK) {
		return patcherBuildToPatcherFixTableMapper.containsTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher build has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixes(long pk) {
		if (getPatcherFixesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, long patcherFixPK) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPK);
		}
		else {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, PatcherFix patcherFix) {
		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFix.getPrimaryKey());
		}
		else {
			return patcherBuildToPatcherFixTableMapper.addTableMapping(
				patcherBuild.getCompanyId(), pk, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		long[] addedKeys = patcherBuildToPatcherFixTableMapper.addTableMappings(
			companyId, pk, patcherFixPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		return addPatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher build and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixes from
	 */
	@Override
	public void clearPatcherFixes(long pk) {
		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK) {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, PatcherFix patcherFix) {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, long[] patcherFixPKs) {
		patcherBuildToPatcherFixTableMapper.deleteTableMappings(
			pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		removePatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher build
	 */
	@Override
	public void setPatcherFixes(long pk, long[] patcherFixPKs) {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(
			patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(
			oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		patcherBuildToPatcherFixTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPKsSet));

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		long companyId = 0;

		PatcherBuild patcherBuild = fetchByPrimaryKey(pk);

		if (patcherBuild == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherBuild.getCompanyId();
		}

		patcherBuildToPatcherFixTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPKsSet));
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes to be associated with the patcher build
	 */
	@Override
	public void setPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		try {
			long[] patcherFixPKs = new long[patcherFixes.size()];

			for (int i = 0; i < patcherFixes.size(); i++) {
				PatcherFix patcherFix = patcherFixes.get(i);

				patcherFixPKs[i] = patcherFix.getPrimaryKey();
			}

			setPatcherFixes(pk, patcherFixPKs);
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
		return "patcherBuildId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERBUILD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherBuildModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher build persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherBuildToPatcherAccountTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PAccounts_PBuilds#patcherBuildId",
				"OSBPatcher_PAccounts_PBuilds", "companyId", "patcherBuildId",
				"patcherAccountId", this, PatcherAccount.class);

		patcherBuildToPatcherFixTableMapper = TableMapperFactory.getTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherBuildId",
			"OSBPatcher_PBuilds_PFixes", "companyId", "patcherBuildId",
			"patcherFixId", this, PatcherFix.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPatcherFixId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"patcherFixId"}, true);

		_finderPathWithoutPaginationFindByPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPatcherFixId",
			new String[] {Long.class.getName()}, new String[] {"patcherFixId"},
			true);

		_finderPathCountByPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPatcherFixId",
			new String[] {Long.class.getName()}, new String[] {"patcherFixId"},
			false);

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

		_finderPathWithPaginationFindByKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKey",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"key_"}, true);

		_finderPathWithoutPaginationFindByKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKey",
			new String[] {String.class.getName()}, new String[] {"key_"}, true);

		_finderPathCountByKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKey",
			new String[] {String.class.getName()}, new String[] {"key_"},
			false);

		_finderPathWithPaginationFindByP_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"patcherAccountId", "patcherProductVersionId"}, true);

		_finderPathWithoutPaginationFindByP_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"patcherAccountId", "patcherProductVersionId"}, true);

		_finderPathCountByP_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"patcherAccountId", "patcherProductVersionId"},
			false);

		_finderPathWithPaginationFindByP_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_C",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"patcherFixId", "childBuild"}, true);

		_finderPathWithoutPaginationFindByP_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_C",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"patcherFixId", "childBuild"}, true);

		_finderPathCountByP_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_C",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"patcherFixId", "childBuild"}, false);

		_finderPathFetchByK_KV = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByK_KV",
			new String[] {String.class.getName(), Double.class.getName()},
			new String[] {"key_", "keyVersion"}, true);

		_finderPathWithPaginationFindByK_GtKV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_GtKV",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"key_", "keyVersion"}, true);

		_finderPathWithPaginationCountByK_GtKV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_GtKV",
			new String[] {String.class.getName(), Double.class.getName()},
			new String[] {"key_", "keyVersion"}, false);

		_finderPathWithPaginationFindByK_LtKV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_LtKV",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"key_", "keyVersion"}, true);

		_finderPathWithPaginationCountByK_LtKV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByK_LtKV",
			new String[] {String.class.getName(), Double.class.getName()},
			new String[] {"key_", "keyVersion"}, false);

		_finderPathWithPaginationFindByK_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByK_L",
			new String[] {
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"key_", "latestKeyBuild"}, true);

		_finderPathWithoutPaginationFindByK_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByK_L",
			new String[] {String.class.getName(), Boolean.class.getName()},
			new String[] {"key_", "latestKeyBuild"}, true);

		_finderPathCountByK_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByK_L",
			new String[] {String.class.getName(), Boolean.class.getName()},
			new String[] {"key_", "latestKeyBuild"}, false);

		_finderPathWithPaginationFindByL_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_S",
			new String[] {
				Boolean.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"latestSupportTicketBuild", "supportTicket"}, true);

		_finderPathWithoutPaginationFindByL_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_S",
			new String[] {Boolean.class.getName(), String.class.getName()},
			new String[] {"latestSupportTicketBuild", "supportTicket"}, true);

		_finderPathCountByL_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_S",
			new String[] {Boolean.class.getName(), String.class.getName()},
			new String[] {"latestSupportTicketBuild", "supportTicket"}, false);

		_finderPathWithPaginationFindByS_GtS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_GtS",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"supportTicket", "supportTicketVersion"}, true);

		_finderPathWithPaginationCountByS_GtS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByS_GtS",
			new String[] {String.class.getName(), Double.class.getName()},
			new String[] {"supportTicket", "supportTicketVersion"}, false);

		_finderPathWithPaginationFindByS_LtS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_LtS",
			new String[] {
				String.class.getName(), Double.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"supportTicket", "supportTicketVersion"}, true);

		_finderPathWithPaginationCountByS_LtS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByS_LtS",
			new String[] {String.class.getName(), Double.class.getName()},
			new String[] {"supportTicket", "supportTicketVersion"}, false);

		_finderPathWithPaginationFindByLtM_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtM_N_S",
			new String[] {
				Date.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"modifiedDate", "notified", "status"}, true);

		_finderPathWithPaginationCountByLtM_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtM_N_S",
			new String[] {
				Date.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"modifiedDate", "notified", "status"}, false);

		_finderPathWithPaginationFindByP_NotP_C_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_NotP_C_NotT",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"patcherFixId", "patcherProductVersionId", "childBuild", "type_"
			},
			true);

		_finderPathWithPaginationCountByP_NotP_C_NotT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_NotP_C_NotT",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {
				"patcherFixId", "patcherProductVersionId", "childBuild", "type_"
			},
			false);

		_finderPathWithPaginationFindByP_N_L_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N_L_A",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "accountEntryCode", "latestKeyBuild",
				"name"
			},
			true);

		_finderPathWithoutPaginationFindByP_N_L_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N_L_A",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), String.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "accountEntryCode", "latestKeyBuild",
				"name"
			},
			true);

		_finderPathCountByP_N_L_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N_L_A",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), String.class.getName()
			},
			new String[] {
				"patcherProjectVersionId", "accountEntryCode", "latestKeyBuild",
				"name"
			},
			false);

		PatcherBuildUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherBuildUtil.setPersistence(null);

		entityCache.removeCache(PatcherBuildImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PAccounts_PBuilds#patcherBuildId");
		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PBuilds_PFixes#patcherBuildId");
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

	protected TableMapper<PatcherBuild, PatcherAccount>
		patcherBuildToPatcherAccountTableMapper;
	protected TableMapper<PatcherBuild, PatcherFix>
		patcherBuildToPatcherFixTableMapper;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_PATCHERBUILD =
		"SELECT patcherBuild FROM PatcherBuild patcherBuild";

	private static final String _SQL_SELECT_PATCHERBUILD_WHERE =
		"SELECT patcherBuild FROM PatcherBuild patcherBuild WHERE ";

	private static final String _SQL_COUNT_PATCHERBUILD =
		"SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild";

	private static final String _SQL_COUNT_PATCHERBUILD_WHERE =
		"SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"patcherBuild.patcherBuildId";

	private static final String _FILTER_SQL_SELECT_PATCHERBUILD_WHERE =
		"SELECT DISTINCT {patcherBuild.*} FROM OSBPatcher_PatcherBuild patcherBuild WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OSBPatcher_PatcherBuild.*} FROM (SELECT DISTINCT patcherBuild.patcherBuildId FROM OSBPatcher_PatcherBuild patcherBuild WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERBUILD_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OSBPatcher_PatcherBuild ON TEMP_TABLE.patcherBuildId = OSBPatcher_PatcherBuild.patcherBuildId";

	private static final String _FILTER_SQL_COUNT_PATCHERBUILD_WHERE =
		"SELECT COUNT(DISTINCT patcherBuild.patcherBuildId) AS COUNT_VALUE FROM OSBPatcher_PatcherBuild patcherBuild WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "patcherBuild";

	private static final String _FILTER_ENTITY_TABLE =
		"OSBPatcher_PatcherBuild";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherBuild.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"OSBPatcher_PatcherBuild.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherBuild exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherBuild exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}