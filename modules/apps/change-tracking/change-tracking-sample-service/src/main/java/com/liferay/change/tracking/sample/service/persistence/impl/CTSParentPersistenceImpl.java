/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.exception.NoSuchCTSParentException;
import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.change.tracking.sample.model.CTSParentTable;
import com.liferay.change.tracking.sample.model.impl.CTSParentImpl;
import com.liferay.change.tracking.sample.model.impl.CTSParentModelImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSParentPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSParentUtil;
import com.liferay.change.tracking.sample.service.persistence.impl.constants.CTSPersistenceConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cts parent service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSParentPersistence.class)
public class CTSParentPersistenceImpl
	extends BasePersistenceImpl<CTSParent> implements CTSParentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSParentUtil</code> to access the cts parent persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSParentImpl.class.getName();

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
	 * Returns all the cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts parents
	 */
	@Override
	public List<CTSParent> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSParent.class)) {

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

			List<CTSParent> list = null;

			if (useFinderCache) {
				list = (List<CTSParent>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CTSParent ctsParent : list) {
						if (companyId != ctsParent.getCompanyId()) {
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

				sb.append(_SQL_SELECT_CTSPARENT_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CTSParentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<CTSParent>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	@Override
	public CTSParent findByCompanyId_First(
			long companyId, OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (ctsParent != null) {
			return ctsParent;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCTSParentException(sb.toString());
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	@Override
	public CTSParent fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSParent> orderByComparator) {

		List<CTSParent> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	@Override
	public CTSParent findByCompanyId_Last(
			long companyId, OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (ctsParent != null) {
			return ctsParent;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCTSParentException(sb.toString());
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	@Override
	public CTSParent fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTSParent> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CTSParent> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cts parents before and after the current cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param ctsParentId the primary key of the current cts parent
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent[] findByCompanyId_PrevAndNext(
			long ctsParentId, long companyId,
			OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = findByPrimaryKey(ctsParentId);

		Session session = null;

		try {
			session = openSession();

			CTSParent[] array = new CTSParentImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, ctsParent, companyId, orderByComparator, true);

			array[1] = ctsParent;

			array[2] = getByCompanyId_PrevAndNext(
				session, ctsParent, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTSParent getByCompanyId_PrevAndNext(
		Session session, CTSParent ctsParent, long companyId,
		OrderByComparator<CTSParent> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CTSPARENT_WHERE);

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
			sb.append(CTSParentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ctsParent)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTSParent> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cts parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CTSParent ctsParent :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ctsParent);
		}
	}

	/**
	 * Returns the number of cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts parents
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSParent.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CTSPARENT_WHERE);

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
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"ctsParent.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the matching cts parents
	 */
	@Override
	public List<CTSParent> findByC_C(long companyId, long ctsGrandParentId) {
		return findByC_C(
			companyId, ctsGrandParentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end) {

		return findByC_C(companyId, ctsGrandParentId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator) {

		return findByC_C(
			companyId, ctsGrandParentId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	@Override
	public List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSParent.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C;
					finderArgs = new Object[] {companyId, ctsGrandParentId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C;
				finderArgs = new Object[] {
					companyId, ctsGrandParentId, start, end, orderByComparator
				};
			}

			List<CTSParent> list = null;

			if (useFinderCache) {
				list = (List<CTSParent>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CTSParent ctsParent : list) {
						if ((companyId != ctsParent.getCompanyId()) ||
							(ctsGrandParentId !=
								ctsParent.getCtsGrandParentId())) {

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

				sb.append(_SQL_SELECT_CTSPARENT_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CTSGRANDPARENTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CTSParentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(ctsGrandParentId);

					list = (List<CTSParent>)QueryUtil.list(
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
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	@Override
	public CTSParent findByC_C_First(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = fetchByC_C_First(
			companyId, ctsGrandParentId, orderByComparator);

		if (ctsParent != null) {
			return ctsParent;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", ctsGrandParentId=");
		sb.append(ctsGrandParentId);

		sb.append("}");

		throw new NoSuchCTSParentException(sb.toString());
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	@Override
	public CTSParent fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSParent> orderByComparator) {

		List<CTSParent> list = findByC_C(
			companyId, ctsGrandParentId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	@Override
	public CTSParent findByC_C_Last(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = fetchByC_C_Last(
			companyId, ctsGrandParentId, orderByComparator);

		if (ctsParent != null) {
			return ctsParent;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", ctsGrandParentId=");
		sb.append(ctsGrandParentId);

		sb.append("}");

		throw new NoSuchCTSParentException(sb.toString());
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	@Override
	public CTSParent fetchByC_C_Last(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSParent> orderByComparator) {

		int count = countByC_C(companyId, ctsGrandParentId);

		if (count == 0) {
			return null;
		}

		List<CTSParent> list = findByC_C(
			companyId, ctsGrandParentId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cts parents before and after the current cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param ctsParentId the primary key of the current cts parent
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent[] findByC_C_PrevAndNext(
			long ctsParentId, long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = findByPrimaryKey(ctsParentId);

		Session session = null;

		try {
			session = openSession();

			CTSParent[] array = new CTSParentImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, ctsParent, companyId, ctsGrandParentId,
				orderByComparator, true);

			array[1] = ctsParent;

			array[2] = getByC_C_PrevAndNext(
				session, ctsParent, companyId, ctsGrandParentId,
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

	protected CTSParent getByC_C_PrevAndNext(
		Session session, CTSParent ctsParent, long companyId,
		long ctsGrandParentId, OrderByComparator<CTSParent> orderByComparator,
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

		sb.append(_SQL_SELECT_CTSPARENT_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_CTSGRANDPARENTID_2);

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
			sb.append(CTSParentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(ctsGrandParentId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ctsParent)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTSParent> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cts parents where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	@Override
	public void removeByC_C(long companyId, long ctsGrandParentId) {
		for (CTSParent ctsParent :
				findByC_C(
					companyId, ctsGrandParentId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ctsParent);
		}
	}

	/**
	 * Returns the number of cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts parents
	 */
	@Override
	public int countByC_C(long companyId, long ctsGrandParentId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSParent.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {companyId, ctsGrandParentId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CTSPARENT_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CTSGRANDPARENTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(ctsGrandParentId);

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
	}

	private static final String _FINDER_COLUMN_C_C_COMPANYID_2 =
		"ctsParent.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CTSGRANDPARENTID_2 =
		"ctsParent.ctsGrandParentId = ?";

	public CTSParentPersistenceImpl() {
		setModelClass(CTSParent.class);

		setModelImplClass(CTSParentImpl.class);
		setModelPKClass(long.class);

		setTable(CTSParentTable.INSTANCE);
	}

	/**
	 * Caches the cts parent in the entity cache if it is enabled.
	 *
	 * @param ctsParent the cts parent
	 */
	@Override
	public void cacheResult(CTSParent ctsParent) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctsParent.getCtCollectionId())) {

			entityCache.putResult(
				CTSParentImpl.class, ctsParent.getPrimaryKey(), ctsParent);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cts parents in the entity cache if it is enabled.
	 *
	 * @param ctsParents the cts parents
	 */
	@Override
	public void cacheResult(List<CTSParent> ctsParents) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ctsParents.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CTSParent ctsParent : ctsParents) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctsParent.getCtCollectionId())) {

				if (entityCache.getResult(
						CTSParentImpl.class, ctsParent.getPrimaryKey()) ==
							null) {

					cacheResult(ctsParent);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cts parents.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CTSParentImpl.class);

		finderCache.clearCache(CTSParentImpl.class);
	}

	/**
	 * Clears the cache for the cts parent.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CTSParent ctsParent) {
		entityCache.removeResult(CTSParentImpl.class, ctsParent);
	}

	@Override
	public void clearCache(List<CTSParent> ctsParents) {
		for (CTSParent ctsParent : ctsParents) {
			entityCache.removeResult(CTSParentImpl.class, ctsParent);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CTSParentImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CTSParentImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new cts parent with the primary key. Does not add the cts parent to the database.
	 *
	 * @param ctsParentId the primary key for the new cts parent
	 * @return the new cts parent
	 */
	@Override
	public CTSParent create(long ctsParentId) {
		CTSParent ctsParent = new CTSParentImpl();

		ctsParent.setNew(true);
		ctsParent.setPrimaryKey(ctsParentId);

		ctsParent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsParent;
	}

	/**
	 * Removes the cts parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent that was removed
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent remove(long ctsParentId) throws NoSuchCTSParentException {
		return remove((Serializable)ctsParentId);
	}

	/**
	 * Removes the cts parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cts parent
	 * @return the cts parent that was removed
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent remove(Serializable primaryKey)
		throws NoSuchCTSParentException {

		Session session = null;

		try {
			session = openSession();

			CTSParent ctsParent = (CTSParent)session.get(
				CTSParentImpl.class, primaryKey);

			if (ctsParent == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCTSParentException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ctsParent);
		}
		catch (NoSuchCTSParentException noSuchEntityException) {
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
	protected CTSParent removeImpl(CTSParent ctsParent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsParent)) {
				ctsParent = (CTSParent)session.get(
					CTSParentImpl.class, ctsParent.getPrimaryKeyObj());
			}

			if ((ctsParent != null) &&
				ctPersistenceHelper.isRemove(ctsParent)) {

				session.delete(ctsParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsParent != null) {
			clearCache(ctsParent);
		}

		return ctsParent;
	}

	@Override
	public CTSParent updateImpl(CTSParent ctsParent) {
		boolean isNew = ctsParent.isNew();

		if (!(ctsParent instanceof CTSParentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsParent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctsParent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsParent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSParent implementation " +
					ctsParent.getClass());
		}

		CTSParentModelImpl ctsParentModelImpl = (CTSParentModelImpl)ctsParent;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ctsParent)) {
				if (!isNew) {
					session.evict(
						CTSParentImpl.class, ctsParent.getPrimaryKeyObj());
				}

				session.save(ctsParent);
			}
			else {
				ctsParent = (CTSParent)session.merge(ctsParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CTSParentImpl.class, ctsParentModelImpl, false, true);

		if (isNew) {
			ctsParent.setNew(false);
		}

		ctsParent.resetOriginalValues();

		return ctsParent;
	}

	/**
	 * Returns the cts parent with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cts parent
	 * @return the cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCTSParentException {

		CTSParent ctsParent = fetchByPrimaryKey(primaryKey);

		if (ctsParent == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCTSParentException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ctsParent;
	}

	/**
	 * Returns the cts parent with the primary key or throws a <code>NoSuchCTSParentException</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent findByPrimaryKey(long ctsParentId)
		throws NoSuchCTSParentException {

		return findByPrimaryKey((Serializable)ctsParentId);
	}

	/**
	 * Returns the cts parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cts parent
	 * @return the cts parent, or <code>null</code> if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(CTSParent.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CTSParent ctsParent = (CTSParent)entityCache.getResult(
			CTSParentImpl.class, primaryKey);

		if (ctsParent != null) {
			return ctsParent;
		}

		Session session = null;

		try {
			session = openSession();

			ctsParent = (CTSParent)session.get(CTSParentImpl.class, primaryKey);

			if (ctsParent != null) {
				cacheResult(ctsParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ctsParent;
	}

	/**
	 * Returns the cts parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent, or <code>null</code> if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent fetchByPrimaryKey(long ctsParentId) {
		return fetchByPrimaryKey((Serializable)ctsParentId);
	}

	@Override
	public Map<Serializable, CTSParent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CTSParent.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CTSParent> map =
			new HashMap<Serializable, CTSParent>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CTSParent ctsParent = fetchByPrimaryKey(primaryKey);

			if (ctsParent != null) {
				map.put(primaryKey, ctsParent);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CTSParent.class, primaryKey)) {

				CTSParent ctsParent = (CTSParent)entityCache.getResult(
					CTSParentImpl.class, primaryKey);

				if (ctsParent == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, ctsParent);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (CTSParent ctsParent : (List<CTSParent>)query.list()) {
				map.put(ctsParent.getPrimaryKeyObj(), ctsParent);

				cacheResult(ctsParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the cts parents.
	 *
	 * @return the cts parents
	 */
	@Override
	public List<CTSParent> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of cts parents
	 */
	@Override
	public List<CTSParent> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts parents
	 */
	@Override
	public List<CTSParent> findAll(
		int start, int end, OrderByComparator<CTSParent> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts parents
	 */
	@Override
	public List<CTSParent> findAll(
		int start, int end, OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSParent.class)) {

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

			List<CTSParent> list = null;

			if (useFinderCache) {
				list = (List<CTSParent>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CTSPARENT);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CTSPARENT;

					sql = sql.concat(CTSParentModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CTSParent>)QueryUtil.list(
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
	}

	/**
	 * Removes all the cts parents from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CTSParent ctsParent : findAll()) {
			remove(ctsParent);
		}
	}

	/**
	 * Returns the number of cts parents.
	 *
	 * @return the number of cts parents
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSParent.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_CTSPARENT);

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
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctsParentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSPARENT;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return CTSParentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CTSParent";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("ctsGrandParentId");
		ctMergeColumnNames.add("name");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("ctsParentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the cts parent persistence.
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

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "ctsGrandParentId"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "ctsGrandParentId"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "ctsGrandParentId"}, false);

		CTSParentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSParentUtil.setPersistence(null);

		entityCache.removeCache(CTSParentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CTSPARENT =
		"SELECT ctsParent FROM CTSParent ctsParent";

	private static final String _SQL_SELECT_CTSPARENT_WHERE =
		"SELECT ctsParent FROM CTSParent ctsParent WHERE ";

	private static final String _SQL_COUNT_CTSPARENT =
		"SELECT COUNT(ctsParent) FROM CTSParent ctsParent";

	private static final String _SQL_COUNT_CTSPARENT_WHERE =
		"SELECT COUNT(ctsParent) FROM CTSParent ctsParent WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ctsParent.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CTSParent exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTSParent exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTSParentPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}