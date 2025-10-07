/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersionTable;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher project version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherProjectVersionPersistence.class)
public class PatcherProjectVersionPersistenceImpl
	extends BasePersistenceImpl<PatcherProjectVersion>
	implements PatcherProjectVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherProjectVersionUtil</code> to access the patcher project version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherProjectVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByPatcherProductVersionId;
	private FinderPath
		_finderPathWithoutPaginationFindByPatcherProductVersionId;
	private FinderPath _finderPathCountByPatcherProductVersionId;

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId) {

		return findByPatcherProductVersionId(
			patcherProductVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end) {

		return findByPatcherProductVersionId(
			patcherProductVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return findByPatcherProductVersionId(
			patcherProductVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByPatcherProductVersionId;
				finderArgs = new Object[] {patcherProductVersionId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPatcherProductVersionId;
			finderArgs = new Object[] {
				patcherProductVersionId, start, end, orderByComparator
			};
		}

		List<PatcherProjectVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProjectVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherProjectVersion patcherProjectVersion : list) {
					if (patcherProductVersionId !=
							patcherProjectVersion.
								getPatcherProductVersionId()) {

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

			sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProductVersionId);

				list = (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByPatcherProductVersionId_First(
			long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion =
			fetchByPatcherProductVersionId_First(
				patcherProductVersionId, orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByPatcherProductVersionId_First(
		long patcherProductVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		List<PatcherProjectVersion> list = findByPatcherProductVersionId(
			patcherProductVersionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByPatcherProductVersionId_Last(
			long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion =
			fetchByPatcherProductVersionId_Last(
				patcherProductVersionId, orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByPatcherProductVersionId_Last(
		long patcherProductVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		int count = countByPatcherProductVersionId(patcherProductVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherProjectVersion> list = findByPatcherProductVersionId(
			patcherProductVersionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[] findByPatcherProductVersionId_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = getByPatcherProductVersionId_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = getByPatcherProductVersionId_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
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

	protected PatcherProjectVersion getByPatcherProductVersionId_PrevAndNext(
		Session session, PatcherProjectVersion patcherProjectVersion,
		long patcherProductVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

		sb.append(
			_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

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
			sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProductVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByPatcherProductVersionId(
		long patcherProductVersionId) {

		return filterFindByPatcherProductVersionId(
			patcherProductVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end) {

		return filterFindByPatcherProductVersionId(
			patcherProductVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherProductVersionId(
				patcherProductVersionId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPatcherProductVersionId(
					patcherProductVersionId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProductVersionId);

			return (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[]
			filterFindByPatcherProductVersionId_PrevAndNext(
				long patcherProjectVersionId, long patcherProductVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherProductVersionId_PrevAndNext(
				patcherProjectVersionId, patcherProductVersionId,
				orderByComparator);
		}

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = filterGetByPatcherProductVersionId_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = filterGetByPatcherProductVersionId_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
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

	protected PatcherProjectVersion
		filterGetByPatcherProductVersionId_PrevAndNext(
			Session session, PatcherProjectVersion patcherProjectVersion,
			long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProductVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 */
	@Override
	public void removeByPatcherProductVersionId(long patcherProductVersionId) {
		for (PatcherProjectVersion patcherProjectVersion :
				findByPatcherProductVersionId(
					patcherProductVersionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByPatcherProductVersionId(long patcherProductVersionId) {
		FinderPath finderPath = _finderPathCountByPatcherProductVersionId;

		Object[] finderArgs = new Object[] {patcherProductVersionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

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
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherProductVersionId(
		long patcherProductVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPatcherProductVersionId(patcherProductVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherProjectVersion> patcherProjectVersions =
				findByPatcherProductVersionId(patcherProductVersionId);

			patcherProjectVersions = InlineSQLHelperUtil.filter(
				patcherProjectVersions);

			return patcherProjectVersions.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

		sb.append(
			_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

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

	private static final String
		_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2 =
			"patcherProjectVersion.patcherProductVersionId = ?";

	private FinderPath
		_finderPathWithPaginationFindByRootPatcherProjectVersionId;
	private FinderPath
		_finderPathWithoutPaginationFindByRootPatcherProjectVersionId;
	private FinderPath _finderPathCountByRootPatcherProjectVersionId;

	/**
	 * Returns all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end) {

		return findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByRootPatcherProjectVersionId;
				finderArgs = new Object[] {rootPatcherProjectVersionId};
			}
		}
		else if (useFinderCache) {
			finderPath =
				_finderPathWithPaginationFindByRootPatcherProjectVersionId;
			finderArgs = new Object[] {
				rootPatcherProjectVersionId, start, end, orderByComparator
			};
		}

		List<PatcherProjectVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProjectVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherProjectVersion patcherProjectVersion : list) {
					if (rootPatcherProjectVersionId !=
							patcherProjectVersion.
								getRootPatcherProjectVersionId()) {

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

			sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

			sb.append(
				_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(rootPatcherProjectVersionId);

				list = (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByRootPatcherProjectVersionId_First(
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion =
			fetchByRootPatcherProjectVersionId_First(
				rootPatcherProjectVersionId, orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("rootPatcherProjectVersionId=");
		sb.append(rootPatcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByRootPatcherProjectVersionId_First(
		long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		List<PatcherProjectVersion> list = findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByRootPatcherProjectVersionId_Last(
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion =
			fetchByRootPatcherProjectVersionId_Last(
				rootPatcherProjectVersionId, orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("rootPatcherProjectVersionId=");
		sb.append(rootPatcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the last patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByRootPatcherProjectVersionId_Last(
		long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		int count = countByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherProjectVersion> list = findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[]
			findByRootPatcherProjectVersionId_PrevAndNext(
				long patcherProjectVersionId, long rootPatcherProjectVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = getByRootPatcherProjectVersionId_PrevAndNext(
				session, patcherProjectVersion, rootPatcherProjectVersionId,
				orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = getByRootPatcherProjectVersionId_PrevAndNext(
				session, patcherProjectVersion, rootPatcherProjectVersionId,
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

	protected PatcherProjectVersion
		getByRootPatcherProjectVersionId_PrevAndNext(
			Session session, PatcherProjectVersion patcherProjectVersion,
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

		sb.append(
			_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2);

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
			sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(rootPatcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return filterFindByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end) {

		return filterFindByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByRootPatcherProjectVersionId(
				rootPatcherProjectVersionId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByRootPatcherProjectVersionId(
					rootPatcherProjectVersionId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(rootPatcherProjectVersionId);

			return (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[]
			filterFindByRootPatcherProjectVersionId_PrevAndNext(
				long patcherProjectVersionId, long rootPatcherProjectVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByRootPatcherProjectVersionId_PrevAndNext(
				patcherProjectVersionId, rootPatcherProjectVersionId,
				orderByComparator);
		}

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = filterGetByRootPatcherProjectVersionId_PrevAndNext(
				session, patcherProjectVersion, rootPatcherProjectVersionId,
				orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = filterGetByRootPatcherProjectVersionId_PrevAndNext(
				session, patcherProjectVersion, rootPatcherProjectVersionId,
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

	protected PatcherProjectVersion
		filterGetByRootPatcherProjectVersionId_PrevAndNext(
			Session session, PatcherProjectVersion patcherProjectVersion,
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(rootPatcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher project versions where rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	@Override
	public void removeByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		for (PatcherProjectVersion patcherProjectVersion :
				findByRootPatcherProjectVersionId(
					rootPatcherProjectVersionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		FinderPath finderPath = _finderPathCountByRootPatcherProjectVersionId;

		Object[] finderArgs = new Object[] {rootPatcherProjectVersionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

			sb.append(
				_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(rootPatcherProjectVersionId);

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
	 * Returns the number of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByRootPatcherProjectVersionId(
				rootPatcherProjectVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherProjectVersion> patcherProjectVersions =
				findByRootPatcherProjectVersionId(rootPatcherProjectVersionId);

			patcherProjectVersions = InlineSQLHelperUtil.filter(
				patcherProjectVersions);

			return patcherProjectVersions.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

		sb.append(
			_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(rootPatcherProjectVersionId);

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
		_FINDER_COLUMN_ROOTPATCHERPROJECTVERSIONID_ROOTPATCHERPROJECTVERSIONID_2 =
			"patcherProjectVersion.rootPatcherProjectVersionId = ?";

	private FinderPath _finderPathFetchByCommittish;

	/**
	 * Returns the patcher project version where committish = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByCommittish(String committish)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByCommittish(
			committish);

		if (patcherProjectVersion == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("committish=");
			sb.append(committish);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherProjectVersionException(sb.toString());
		}

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByCommittish(String committish) {
		return fetchByCommittish(committish, true);
	}

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param committish the committish
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByCommittish(
		String committish, boolean useFinderCache) {

		committish = Objects.toString(committish, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {committish};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByCommittish, finderArgs, this);
		}

		if (result instanceof PatcherProjectVersion) {
			PatcherProjectVersion patcherProjectVersion =
				(PatcherProjectVersion)result;

			if (!Objects.equals(
					committish, patcherProjectVersion.getCommittish())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

			boolean bindCommittish = false;

			if (committish.isEmpty()) {
				sb.append(_FINDER_COLUMN_COMMITTISH_COMMITTISH_3);
			}
			else {
				bindCommittish = true;

				sb.append(_FINDER_COLUMN_COMMITTISH_COMMITTISH_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindCommittish) {
					queryPos.add(committish);
				}

				List<PatcherProjectVersion> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByCommittish, finderArgs, list);
					}
				}
				else {
					PatcherProjectVersion patcherProjectVersion = list.get(0);

					result = patcherProjectVersion;

					cacheResult(patcherProjectVersion);
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
			return (PatcherProjectVersion)result;
		}
	}

	/**
	 * Removes the patcher project version where committish = &#63; from the database.
	 *
	 * @param committish the committish
	 * @return the patcher project version that was removed
	 */
	@Override
	public PatcherProjectVersion removeByCommittish(String committish)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByCommittish(
			committish);

		return remove(patcherProjectVersion);
	}

	/**
	 * Returns the number of patcher project versions where committish = &#63;.
	 *
	 * @param committish the committish
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByCommittish(String committish) {
		PatcherProjectVersion patcherProjectVersion = fetchByCommittish(
			committish);

		if (patcherProjectVersion == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_COMMITTISH_COMMITTISH_2 =
		"patcherProjectVersion.committish = ?";

	private static final String _FINDER_COLUMN_COMMITTISH_COMMITTISH_3 =
		"(patcherProjectVersion.committish IS NULL OR patcherProjectVersion.committish = '')";

	private FinderPath _finderPathFetchByName;

	/**
	 * Returns the patcher project version where name = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByName(String name)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByName(name);

		if (patcherProjectVersion == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherProjectVersionException(sb.toString());
		}

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByName(String name) {
		return fetchByName(name, true);
	}

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByName(
		String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByName, finderArgs, this);
		}

		if (result instanceof PatcherProjectVersion) {
			PatcherProjectVersion patcherProjectVersion =
				(PatcherProjectVersion)result;

			if (!Objects.equals(name, patcherProjectVersion.getName())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_NAME_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_NAME_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindName) {
					queryPos.add(name);
				}

				List<PatcherProjectVersion> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByName, finderArgs, list);
					}
				}
				else {
					PatcherProjectVersion patcherProjectVersion = list.get(0);

					result = patcherProjectVersion;

					cacheResult(patcherProjectVersion);
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
			return (PatcherProjectVersion)result;
		}
	}

	/**
	 * Removes the patcher project version where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher project version that was removed
	 */
	@Override
	public PatcherProjectVersion removeByName(String name)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByName(name);

		return remove(patcherProjectVersion);
	}

	/**
	 * Returns the number of patcher project versions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByName(String name) {
		PatcherProjectVersion patcherProjectVersion = fetchByName(name);

		if (patcherProjectVersion == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_NAME_NAME_2 =
		"patcherProjectVersion.name = ?";

	private static final String _FINDER_COLUMN_NAME_NAME_3 =
		"(patcherProjectVersion.name IS NULL OR patcherProjectVersion.name = '')";

	private FinderPath _finderPathWithPaginationFindByP_R;
	private FinderPath _finderPathWithoutPaginationFindByP_R;
	private FinderPath _finderPathCountByP_R;

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end) {

		return findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_R;
				finderArgs = new Object[] {
					patcherProductVersionId, rootPatcherProjectVersionId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_R;
			finderArgs = new Object[] {
				patcherProductVersionId, rootPatcherProjectVersionId, start,
				end, orderByComparator
			};
		}

		List<PatcherProjectVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProjectVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherProjectVersion patcherProjectVersion : list) {
					if ((patcherProductVersionId !=
							patcherProjectVersion.
								getPatcherProductVersionId()) ||
						(rootPatcherProjectVersionId !=
							patcherProjectVersion.
								getRootPatcherProjectVersionId())) {

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

			sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

			sb.append(_FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProductVersionId);

				queryPos.add(rootPatcherProjectVersionId);

				list = (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByP_R_First(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByP_R_First(
			patcherProductVersionId, rootPatcherProjectVersionId,
			orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append(", rootPatcherProjectVersionId=");
		sb.append(rootPatcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByP_R_First(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		List<PatcherProjectVersion> list = findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByP_R_Last(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByP_R_Last(
			patcherProductVersionId, rootPatcherProjectVersionId,
			orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append(", rootPatcherProjectVersionId=");
		sb.append(rootPatcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByP_R_Last(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		int count = countByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherProjectVersion> list = findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[] findByP_R_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = getByP_R_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				rootPatcherProjectVersionId, orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = getByP_R_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				rootPatcherProjectVersionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherProjectVersion getByP_R_PrevAndNext(
		Session session, PatcherProjectVersion patcherProjectVersion,
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

		sb.append(_FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2);

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
			sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProductVersionId);

		queryPos.add(rootPatcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return filterFindByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end) {

		return filterFindByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_R(
				patcherProductVersionId, rootPatcherProjectVersionId, start,
				end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_R(
					patcherProductVersionId, rootPatcherProjectVersionId,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProductVersionId);

			queryPos.add(rootPatcherProjectVersionId);

			return (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[] filterFindByP_R_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_R_PrevAndNext(
				patcherProjectVersionId, patcherProductVersionId,
				rootPatcherProjectVersionId, orderByComparator);
		}

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = filterGetByP_R_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				rootPatcherProjectVersionId, orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = filterGetByP_R_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				rootPatcherProjectVersionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherProjectVersion filterGetByP_R_PrevAndNext(
		Session session, PatcherProjectVersion patcherProjectVersion,
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProductVersionId);

		queryPos.add(rootPatcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	@Override
	public void removeByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		for (PatcherProjectVersion patcherProjectVersion :
				findByP_R(
					patcherProductVersionId, rootPatcherProjectVersionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		FinderPath finderPath = _finderPathCountByP_R;

		Object[] finderArgs = new Object[] {
			patcherProductVersionId, rootPatcherProjectVersionId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

			sb.append(_FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2);

			sb.append(_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProductVersionId);

				queryPos.add(rootPatcherProjectVersionId);

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
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_R(
				patcherProductVersionId, rootPatcherProjectVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherProjectVersion> patcherProjectVersions = findByP_R(
				patcherProductVersionId, rootPatcherProjectVersionId);

			patcherProjectVersions = InlineSQLHelperUtil.filter(
				patcherProjectVersions);

			return patcherProjectVersions.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

		sb.append(_FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2);

		sb.append(_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProductVersionId);

			queryPos.add(rootPatcherProjectVersionId);

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

	private static final String _FINDER_COLUMN_P_R_PATCHERPRODUCTVERSIONID_2 =
		"patcherProjectVersion.patcherProductVersionId = ? AND ";

	private static final String
		_FINDER_COLUMN_P_R_ROOTPATCHERPROJECTVERSIONID_2 =
			"patcherProjectVersion.rootPatcherProjectVersionId = ?";

	private FinderPath _finderPathWithPaginationFindByP_RN;
	private FinderPath _finderPathWithoutPaginationFindByP_RN;
	private FinderPath _finderPathCountByP_RN;

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return findByP_RN(
			patcherProductVersionId, repositoryName, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start,
		int end) {

		return findByP_RN(
			patcherProductVersionId, repositoryName, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return findByP_RN(
			patcherProductVersionId, repositoryName, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		repositoryName = Objects.toString(repositoryName, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_RN;
				finderArgs = new Object[] {
					patcherProductVersionId, repositoryName
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_RN;
			finderArgs = new Object[] {
				patcherProductVersionId, repositoryName, start, end,
				orderByComparator
			};
		}

		List<PatcherProjectVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProjectVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherProjectVersion patcherProjectVersion : list) {
					if ((patcherProductVersionId !=
							patcherProjectVersion.
								getPatcherProductVersionId()) ||
						!repositoryName.equals(
							patcherProjectVersion.getRepositoryName())) {

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

			sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

			sb.append(_FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2);

			boolean bindRepositoryName = false;

			if (repositoryName.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_3);
			}
			else {
				bindRepositoryName = true;

				sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProductVersionId);

				if (bindRepositoryName) {
					queryPos.add(repositoryName);
				}

				list = (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByP_RN_First(
			long patcherProductVersionId, String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByP_RN_First(
			patcherProductVersionId, repositoryName, orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append(", repositoryName=");
		sb.append(repositoryName);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByP_RN_First(
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		List<PatcherProjectVersion> list = findByP_RN(
			patcherProductVersionId, repositoryName, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion findByP_RN_Last(
			long patcherProductVersionId, String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByP_RN_Last(
			patcherProductVersionId, repositoryName, orderByComparator);

		if (patcherProjectVersion != null) {
			return patcherProjectVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProductVersionId=");
		sb.append(patcherProductVersionId);

		sb.append(", repositoryName=");
		sb.append(repositoryName);

		sb.append("}");

		throw new NoSuchPatcherProjectVersionException(sb.toString());
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByP_RN_Last(
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		int count = countByP_RN(patcherProductVersionId, repositoryName);

		if (count == 0) {
			return null;
		}

		List<PatcherProjectVersion> list = findByP_RN(
			patcherProductVersionId, repositoryName, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[] findByP_RN_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		repositoryName = Objects.toString(repositoryName, "");

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = getByP_RN_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				repositoryName, orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = getByP_RN_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				repositoryName, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherProjectVersion getByP_RN_PrevAndNext(
		Session session, PatcherProjectVersion patcherProjectVersion,
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);

		sb.append(_FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2);

		boolean bindRepositoryName = false;

		if (repositoryName.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_3);
		}
		else {
			bindRepositoryName = true;

			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_2);
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
			sb.append(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProductVersionId);

		if (bindRepositoryName) {
			queryPos.add(repositoryName);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return filterFindByP_RN(
			patcherProductVersionId, repositoryName, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start,
		int end) {

		return filterFindByP_RN(
			patcherProductVersionId, repositoryName, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	@Override
	public List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_RN(
				patcherProductVersionId, repositoryName, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByP_RN(
					patcherProductVersionId, repositoryName, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		repositoryName = Objects.toString(repositoryName, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2);

		boolean bindRepositoryName = false;

		if (repositoryName.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_3);
		}
		else {
			bindRepositoryName = true;

			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProductVersionId);

			if (bindRepositoryName) {
				queryPos.add(repositoryName);
			}

			return (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion[] filterFindByP_RN_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByP_RN_PrevAndNext(
				patcherProjectVersionId, patcherProductVersionId,
				repositoryName, orderByComparator);
		}

		repositoryName = Objects.toString(repositoryName, "");

		PatcherProjectVersion patcherProjectVersion = findByPrimaryKey(
			patcherProjectVersionId);

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion[] array = new PatcherProjectVersionImpl[3];

			array[0] = filterGetByP_RN_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				repositoryName, orderByComparator, true);

			array[1] = patcherProjectVersion;

			array[2] = filterGetByP_RN_PrevAndNext(
				session, patcherProjectVersion, patcherProductVersionId,
				repositoryName, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherProjectVersion filterGetByP_RN_PrevAndNext(
		Session session, PatcherProjectVersion patcherProjectVersion,
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2);

		boolean bindRepositoryName = false;

		if (repositoryName.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_3);
		}
		else {
			bindRepositoryName = true;

			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2);
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
					PatcherProjectVersionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherProjectVersionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, PatcherProjectVersionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, PatcherProjectVersionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProductVersionId);

		if (bindRepositoryName) {
			queryPos.add(repositoryName);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherProjectVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherProjectVersion> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 */
	@Override
	public void removeByP_RN(
		long patcherProductVersionId, String repositoryName) {

		for (PatcherProjectVersion patcherProjectVersion :
				findByP_RN(
					patcherProductVersionId, repositoryName, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions
	 */
	@Override
	public int countByP_RN(
		long patcherProductVersionId, String repositoryName) {

		repositoryName = Objects.toString(repositoryName, "");

		FinderPath finderPath = _finderPathCountByP_RN;

		Object[] finderArgs = new Object[] {
			patcherProductVersionId, repositoryName
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

			sb.append(_FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2);

			boolean bindRepositoryName = false;

			if (repositoryName.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_3);
			}
			else {
				bindRepositoryName = true;

				sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProductVersionId);

				if (bindRepositoryName) {
					queryPos.add(repositoryName);
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
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	@Override
	public int filterCountByP_RN(
		long patcherProductVersionId, String repositoryName) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByP_RN(patcherProductVersionId, repositoryName);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherProjectVersion> patcherProjectVersions = findByP_RN(
				patcherProductVersionId, repositoryName);

			patcherProjectVersions = InlineSQLHelperUtil.filter(
				patcherProjectVersions);

			return patcherProjectVersions.size();
		}

		repositoryName = Objects.toString(repositoryName, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERPROJECTVERSION_WHERE);

		sb.append(_FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2);

		boolean bindRepositoryName = false;

		if (repositoryName.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_3);
		}
		else {
			bindRepositoryName = true;

			sb.append(_FINDER_COLUMN_P_RN_REPOSITORYNAME_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherProjectVersion.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProductVersionId);

			if (bindRepositoryName) {
				queryPos.add(repositoryName);
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

	private static final String _FINDER_COLUMN_P_RN_PATCHERPRODUCTVERSIONID_2 =
		"patcherProjectVersion.patcherProductVersionId = ? AND ";

	private static final String _FINDER_COLUMN_P_RN_REPOSITORYNAME_2 =
		"patcherProjectVersion.repositoryName = ?";

	private static final String _FINDER_COLUMN_P_RN_REPOSITORYNAME_3 =
		"(patcherProjectVersion.repositoryName IS NULL OR patcherProjectVersion.repositoryName = '')";

	public PatcherProjectVersionPersistenceImpl() {
		setModelClass(PatcherProjectVersion.class);

		setModelImplClass(PatcherProjectVersionImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherProjectVersionTable.INSTANCE);
	}

	/**
	 * Caches the patcher project version in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersion the patcher project version
	 */
	@Override
	public void cacheResult(PatcherProjectVersion patcherProjectVersion) {
		entityCache.putResult(
			PatcherProjectVersionImpl.class,
			patcherProjectVersion.getPrimaryKey(), patcherProjectVersion);

		finderCache.putResult(
			_finderPathFetchByCommittish,
			new Object[] {patcherProjectVersion.getCommittish()},
			patcherProjectVersion);

		finderCache.putResult(
			_finderPathFetchByName,
			new Object[] {patcherProjectVersion.getName()},
			patcherProjectVersion);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher project versions in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersions the patcher project versions
	 */
	@Override
	public void cacheResult(
		List<PatcherProjectVersion> patcherProjectVersions) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherProjectVersions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherProjectVersion patcherProjectVersion :
				patcherProjectVersions) {

			if (entityCache.getResult(
					PatcherProjectVersionImpl.class,
					patcherProjectVersion.getPrimaryKey()) == null) {

				cacheResult(patcherProjectVersion);
			}
		}
	}

	/**
	 * Clears the cache for all patcher project versions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherProjectVersionImpl.class);

		finderCache.clearCache(PatcherProjectVersionImpl.class);
	}

	/**
	 * Clears the cache for the patcher project version.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherProjectVersion patcherProjectVersion) {
		entityCache.removeResult(
			PatcherProjectVersionImpl.class, patcherProjectVersion);
	}

	@Override
	public void clearCache(List<PatcherProjectVersion> patcherProjectVersions) {
		for (PatcherProjectVersion patcherProjectVersion :
				patcherProjectVersions) {

			entityCache.removeResult(
				PatcherProjectVersionImpl.class, patcherProjectVersion);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherProjectVersionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				PatcherProjectVersionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherProjectVersionModelImpl patcherProjectVersionModelImpl) {

		Object[] args = new Object[] {
			patcherProjectVersionModelImpl.getCommittish()
		};

		finderCache.putResult(
			_finderPathFetchByCommittish, args, patcherProjectVersionModelImpl);

		args = new Object[] {patcherProjectVersionModelImpl.getName()};

		finderCache.putResult(
			_finderPathFetchByName, args, patcherProjectVersionModelImpl);
	}

	/**
	 * Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	 *
	 * @param patcherProjectVersionId the primary key for the new patcher project version
	 * @return the new patcher project version
	 */
	@Override
	public PatcherProjectVersion create(long patcherProjectVersionId) {
		PatcherProjectVersion patcherProjectVersion =
			new PatcherProjectVersionImpl();

		patcherProjectVersion.setNew(true);
		patcherProjectVersion.setPrimaryKey(patcherProjectVersionId);

		patcherProjectVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherProjectVersion;
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion remove(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException {

		return remove((Serializable)patcherProjectVersionId);
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion remove(Serializable primaryKey)
		throws NoSuchPatcherProjectVersionException {

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion patcherProjectVersion =
				(PatcherProjectVersion)session.get(
					PatcherProjectVersionImpl.class, primaryKey);

			if (patcherProjectVersion == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherProjectVersionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherProjectVersion);
		}
		catch (NoSuchPatcherProjectVersionException noSuchEntityException) {
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
	protected PatcherProjectVersion removeImpl(
		PatcherProjectVersion patcherProjectVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProjectVersion)) {
				patcherProjectVersion = (PatcherProjectVersion)session.get(
					PatcherProjectVersionImpl.class,
					patcherProjectVersion.getPrimaryKeyObj());
			}

			if (patcherProjectVersion != null) {
				session.delete(patcherProjectVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherProjectVersion != null) {
			clearCache(patcherProjectVersion);
		}

		return patcherProjectVersion;
	}

	@Override
	public PatcherProjectVersion updateImpl(
		PatcherProjectVersion patcherProjectVersion) {

		boolean isNew = patcherProjectVersion.isNew();

		if (!(patcherProjectVersion instanceof
				PatcherProjectVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherProjectVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherProjectVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherProjectVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherProjectVersion implementation " +
					patcherProjectVersion.getClass());
		}

		PatcherProjectVersionModelImpl patcherProjectVersionModelImpl =
			(PatcherProjectVersionModelImpl)patcherProjectVersion;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherProjectVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherProjectVersion.setCreateDate(date);
			}
			else {
				patcherProjectVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherProjectVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherProjectVersion.setModifiedDate(date);
			}
			else {
				patcherProjectVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherProjectVersion);
			}
			else {
				patcherProjectVersion = (PatcherProjectVersion)session.merge(
					patcherProjectVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherProjectVersionImpl.class, patcherProjectVersionModelImpl,
			false, true);

		cacheUniqueFindersCache(patcherProjectVersionModelImpl);

		if (isNew) {
			patcherProjectVersion.setNew(false);
		}

		patcherProjectVersion.resetOriginalValues();

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByPrimaryKey(
			primaryKey);

		if (patcherProjectVersion == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherProjectVersionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException {

		return findByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByPrimaryKey(
		long patcherProjectVersionId) {

		return fetchByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher project versions.
	 *
	 * @return the patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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

		List<PatcherProjectVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProjectVersion>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERPROJECTVERSION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERPROJECTVERSION;

				sql = sql.concat(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Removes all the patcher project versions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherProjectVersion patcherProjectVersion : findAll()) {
			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions.
	 *
	 * @return the number of patcher project versions
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
					_SQL_COUNT_PATCHERPROJECTVERSION);

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
		return "patcherProjectVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERPROJECTVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherProjectVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher project version persistence.
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

		_finderPathWithPaginationFindByPatcherProductVersionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByPatcherProductVersionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"patcherProductVersionId"}, true);

		_finderPathWithoutPaginationFindByPatcherProductVersionId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByPatcherProductVersionId",
				new String[] {Long.class.getName()},
				new String[] {"patcherProductVersionId"}, true);

		_finderPathCountByPatcherProductVersionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPatcherProductVersionId",
			new String[] {Long.class.getName()},
			new String[] {"patcherProductVersionId"}, false);

		_finderPathWithPaginationFindByRootPatcherProjectVersionId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findByRootPatcherProjectVersionId",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"rootPatcherProjectVersionId"}, true);

		_finderPathWithoutPaginationFindByRootPatcherProjectVersionId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByRootPatcherProjectVersionId",
				new String[] {Long.class.getName()},
				new String[] {"rootPatcherProjectVersionId"}, true);

		_finderPathCountByRootPatcherProjectVersionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByRootPatcherProjectVersionId",
			new String[] {Long.class.getName()},
			new String[] {"rootPatcherProjectVersionId"}, false);

		_finderPathFetchByCommittish = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByCommittish",
			new String[] {String.class.getName()}, new String[] {"committish"},
			true);

		_finderPathFetchByName = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByName",
			new String[] {String.class.getName()}, new String[] {"name"}, true);

		_finderPathWithPaginationFindByP_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_R",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"patcherProductVersionId", "rootPatcherProjectVersionId"
			},
			true);

		_finderPathWithoutPaginationFindByP_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_R",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {
				"patcherProductVersionId", "rootPatcherProjectVersionId"
			},
			true);

		_finderPathCountByP_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_R",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {
				"patcherProductVersionId", "rootPatcherProjectVersionId"
			},
			false);

		_finderPathWithPaginationFindByP_RN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_RN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"patcherProductVersionId", "repositoryName"}, true);

		_finderPathWithoutPaginationFindByP_RN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_RN",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"patcherProductVersionId", "repositoryName"}, true);

		_finderPathCountByP_RN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_RN",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"patcherProductVersionId", "repositoryName"}, false);

		PatcherProjectVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherProjectVersionUtil.setPersistence(null);

		entityCache.removeCache(PatcherProjectVersionImpl.class.getName());
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

	private static final String _SQL_SELECT_PATCHERPROJECTVERSION =
		"SELECT patcherProjectVersion FROM PatcherProjectVersion patcherProjectVersion";

	private static final String _SQL_SELECT_PATCHERPROJECTVERSION_WHERE =
		"SELECT patcherProjectVersion FROM PatcherProjectVersion patcherProjectVersion WHERE ";

	private static final String _SQL_COUNT_PATCHERPROJECTVERSION =
		"SELECT COUNT(patcherProjectVersion) FROM PatcherProjectVersion patcherProjectVersion";

	private static final String _SQL_COUNT_PATCHERPROJECTVERSION_WHERE =
		"SELECT COUNT(patcherProjectVersion) FROM PatcherProjectVersion patcherProjectVersion WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"patcherProjectVersion.patcherProjectVersionId";

	private static final String _FILTER_SQL_SELECT_PATCHERPROJECTVERSION_WHERE =
		"SELECT DISTINCT {patcherProjectVersion.*} FROM OSBPatcher_PProjectVersion patcherProjectVersion WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OSBPatcher_PProjectVersion.*} FROM (SELECT DISTINCT patcherProjectVersion.patcherProjectVersionId FROM OSBPatcher_PProjectVersion patcherProjectVersion WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERPROJECTVERSION_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OSBPatcher_PProjectVersion ON TEMP_TABLE.patcherProjectVersionId = OSBPatcher_PProjectVersion.patcherProjectVersionId";

	private static final String _FILTER_SQL_COUNT_PATCHERPROJECTVERSION_WHERE =
		"SELECT COUNT(DISTINCT patcherProjectVersion.patcherProjectVersionId) AS COUNT_VALUE FROM OSBPatcher_PProjectVersion patcherProjectVersion WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "patcherProjectVersion";

	private static final String _FILTER_ENTITY_TABLE =
		"OSBPatcher_PProjectVersion";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"patcherProjectVersion.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"OSBPatcher_PProjectVersion.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherProjectVersion exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherProjectVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherProjectVersionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}