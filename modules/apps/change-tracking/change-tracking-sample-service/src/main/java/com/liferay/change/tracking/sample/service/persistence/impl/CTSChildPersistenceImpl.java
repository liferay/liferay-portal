/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.exception.NoSuchCTSChildException;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.change.tracking.sample.model.CTSChildTable;
import com.liferay.change.tracking.sample.model.impl.CTSChildImpl;
import com.liferay.change.tracking.sample.model.impl.CTSChildModelImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSChildPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSChildUtil;
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
 * The persistence implementation for the cts child service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSChildPersistence.class)
public class CTSChildPersistenceImpl
	extends BasePersistenceImpl<CTSChild> implements CTSChildPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSChildUtil</code> to access the cts child persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSChildImpl.class.getName();

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
	 * Returns all the cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts childs
	 */
	@Override
	public List<CTSChild> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

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

			List<CTSChild> list = null;

			if (useFinderCache) {
				list = (List<CTSChild>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CTSChild ctsChild : list) {
						if (companyId != ctsChild.getCompanyId()) {
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

				sb.append(_SQL_SELECT_CTSCHILD_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CTSChildModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<CTSChild>)QueryUtil.list(
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
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByCompanyId_First(
			long companyId, OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (ctsChild != null) {
			return ctsChild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCTSChildException(sb.toString());
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSChild> orderByComparator) {

		List<CTSChild> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByCompanyId_Last(
			long companyId, OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByCompanyId_Last(companyId, orderByComparator);

		if (ctsChild != null) {
			return ctsChild;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCTSChildException(sb.toString());
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTSChild> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CTSChild> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild[] findByCompanyId_PrevAndNext(
			long ctsChildId, long companyId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = findByPrimaryKey(ctsChildId);

		Session session = null;

		try {
			session = openSession();

			CTSChild[] array = new CTSChildImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, ctsChild, companyId, orderByComparator, true);

			array[1] = ctsChild;

			array[2] = getByCompanyId_PrevAndNext(
				session, ctsChild, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTSChild getByCompanyId_PrevAndNext(
		Session session, CTSChild ctsChild, long companyId,
		OrderByComparator<CTSChild> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CTSCHILD_WHERE);

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
			sb.append(CTSChildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ctsChild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTSChild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cts childs where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CTSChild ctsChild :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ctsChild);
		}
	}

	/**
	 * Returns the number of cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts childs
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CTSCHILD_WHERE);

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
		"ctsChild.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_C(long companyId, long ctsGrandParentId) {
		return findByC_C(
			companyId, ctsGrandParentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end) {

		return findByC_C(companyId, ctsGrandParentId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return findByC_C(
			companyId, ctsGrandParentId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

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

			List<CTSChild> list = null;

			if (useFinderCache) {
				list = (List<CTSChild>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CTSChild ctsChild : list) {
						if ((companyId != ctsChild.getCompanyId()) ||
							(ctsGrandParentId !=
								ctsChild.getCtsGrandParentId())) {

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

				sb.append(_SQL_SELECT_CTSCHILD_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CTSGRANDPARENTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CTSChildModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(ctsGrandParentId);

					list = (List<CTSChild>)QueryUtil.list(
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
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByC_C_First(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByC_C_First(
			companyId, ctsGrandParentId, orderByComparator);

		if (ctsChild != null) {
			return ctsChild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", ctsGrandParentId=");
		sb.append(ctsGrandParentId);

		sb.append("}");

		throw new NoSuchCTSChildException(sb.toString());
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSChild> orderByComparator) {

		List<CTSChild> list = findByC_C(
			companyId, ctsGrandParentId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByC_C_Last(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByC_C_Last(
			companyId, ctsGrandParentId, orderByComparator);

		if (ctsChild != null) {
			return ctsChild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", ctsGrandParentId=");
		sb.append(ctsGrandParentId);

		sb.append("}");

		throw new NoSuchCTSChildException(sb.toString());
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByC_C_Last(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSChild> orderByComparator) {

		int count = countByC_C(companyId, ctsGrandParentId);

		if (count == 0) {
			return null;
		}

		List<CTSChild> list = findByC_C(
			companyId, ctsGrandParentId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild[] findByC_C_PrevAndNext(
			long ctsChildId, long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = findByPrimaryKey(ctsChildId);

		Session session = null;

		try {
			session = openSession();

			CTSChild[] array = new CTSChildImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, ctsChild, companyId, ctsGrandParentId,
				orderByComparator, true);

			array[1] = ctsChild;

			array[2] = getByC_C_PrevAndNext(
				session, ctsChild, companyId, ctsGrandParentId,
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

	protected CTSChild getByC_C_PrevAndNext(
		Session session, CTSChild ctsChild, long companyId,
		long ctsGrandParentId, OrderByComparator<CTSChild> orderByComparator,
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

		sb.append(_SQL_SELECT_CTSCHILD_WHERE);

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
			sb.append(CTSChildModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ctsChild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTSChild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cts childs where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	@Override
	public void removeByC_C(long companyId, long ctsGrandParentId) {
		for (CTSChild ctsChild :
				findByC_C(
					companyId, ctsGrandParentId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ctsChild);
		}
	}

	/**
	 * Returns the number of cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts childs
	 */
	@Override
	public int countByC_C(long companyId, long ctsGrandParentId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {companyId, ctsGrandParentId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CTSCHILD_WHERE);

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
		"ctsChild.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CTSGRANDPARENTID_2 =
		"ctsChild.ctsGrandParentId = ?";

	private FinderPath _finderPathWithPaginationFindByC_P;
	private FinderPath _finderPathWithoutPaginationFindByC_P;
	private FinderPath _finderPathCountByC_P;

	/**
	 * Returns all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_P(long companyId, long parentCTSChildId) {
		return findByC_P(
			companyId, parentCTSChildId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end) {

		return findByC_P(companyId, parentCTSChildId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return findByC_P(
			companyId, parentCTSChildId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_P;
					finderArgs = new Object[] {companyId, parentCTSChildId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_P;
				finderArgs = new Object[] {
					companyId, parentCTSChildId, start, end, orderByComparator
				};
			}

			List<CTSChild> list = null;

			if (useFinderCache) {
				list = (List<CTSChild>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CTSChild ctsChild : list) {
						if ((companyId != ctsChild.getCompanyId()) ||
							(parentCTSChildId !=
								ctsChild.getParentCTSChildId())) {

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

				sb.append(_SQL_SELECT_CTSCHILD_WHERE);

				sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTCTSCHILDID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CTSChildModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentCTSChildId);

					list = (List<CTSChild>)QueryUtil.list(
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
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByC_P_First(
			long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByC_P_First(
			companyId, parentCTSChildId, orderByComparator);

		if (ctsChild != null) {
			return ctsChild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentCTSChildId=");
		sb.append(parentCTSChildId);

		sb.append("}");

		throw new NoSuchCTSChildException(sb.toString());
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByC_P_First(
		long companyId, long parentCTSChildId,
		OrderByComparator<CTSChild> orderByComparator) {

		List<CTSChild> list = findByC_P(
			companyId, parentCTSChildId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByC_P_Last(
			long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByC_P_Last(
			companyId, parentCTSChildId, orderByComparator);

		if (ctsChild != null) {
			return ctsChild;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentCTSChildId=");
		sb.append(parentCTSChildId);

		sb.append("}");

		throw new NoSuchCTSChildException(sb.toString());
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByC_P_Last(
		long companyId, long parentCTSChildId,
		OrderByComparator<CTSChild> orderByComparator) {

		int count = countByC_P(companyId, parentCTSChildId);

		if (count == 0) {
			return null;
		}

		List<CTSChild> list = findByC_P(
			companyId, parentCTSChildId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild[] findByC_P_PrevAndNext(
			long ctsChildId, long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = findByPrimaryKey(ctsChildId);

		Session session = null;

		try {
			session = openSession();

			CTSChild[] array = new CTSChildImpl[3];

			array[0] = getByC_P_PrevAndNext(
				session, ctsChild, companyId, parentCTSChildId,
				orderByComparator, true);

			array[1] = ctsChild;

			array[2] = getByC_P_PrevAndNext(
				session, ctsChild, companyId, parentCTSChildId,
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

	protected CTSChild getByC_P_PrevAndNext(
		Session session, CTSChild ctsChild, long companyId,
		long parentCTSChildId, OrderByComparator<CTSChild> orderByComparator,
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

		sb.append(_SQL_SELECT_CTSCHILD_WHERE);

		sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_PARENTCTSCHILDID_2);

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
			sb.append(CTSChildModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(parentCTSChildId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ctsChild)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTSChild> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cts childs where companyId = &#63; and parentCTSChildId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 */
	@Override
	public void removeByC_P(long companyId, long parentCTSChildId) {
		for (CTSChild ctsChild :
				findByC_P(
					companyId, parentCTSChildId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ctsChild);
		}
	}

	/**
	 * Returns the number of cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the number of matching cts childs
	 */
	@Override
	public int countByC_P(long companyId, long parentCTSChildId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

			FinderPath finderPath = _finderPathCountByC_P;

			Object[] finderArgs = new Object[] {companyId, parentCTSChildId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CTSCHILD_WHERE);

				sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTCTSCHILDID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentCTSChildId);

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

	private static final String _FINDER_COLUMN_C_P_COMPANYID_2 =
		"ctsChild.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_PARENTCTSCHILDID_2 =
		"ctsChild.parentCTSChildId = ?";

	public CTSChildPersistenceImpl() {
		setModelClass(CTSChild.class);

		setModelImplClass(CTSChildImpl.class);
		setModelPKClass(long.class);

		setTable(CTSChildTable.INSTANCE);
	}

	/**
	 * Caches the cts child in the entity cache if it is enabled.
	 *
	 * @param ctsChild the cts child
	 */
	@Override
	public void cacheResult(CTSChild ctsChild) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctsChild.getCtCollectionId())) {

			entityCache.putResult(
				CTSChildImpl.class, ctsChild.getPrimaryKey(), ctsChild);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cts childs in the entity cache if it is enabled.
	 *
	 * @param ctsChilds the cts childs
	 */
	@Override
	public void cacheResult(List<CTSChild> ctsChilds) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ctsChilds.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CTSChild ctsChild : ctsChilds) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctsChild.getCtCollectionId())) {

				if (entityCache.getResult(
						CTSChildImpl.class, ctsChild.getPrimaryKey()) == null) {

					cacheResult(ctsChild);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cts childs.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CTSChildImpl.class);

		finderCache.clearCache(CTSChildImpl.class);
	}

	/**
	 * Clears the cache for the cts child.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CTSChild ctsChild) {
		entityCache.removeResult(CTSChildImpl.class, ctsChild);
	}

	@Override
	public void clearCache(List<CTSChild> ctsChilds) {
		for (CTSChild ctsChild : ctsChilds) {
			entityCache.removeResult(CTSChildImpl.class, ctsChild);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CTSChildImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CTSChildImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new cts child with the primary key. Does not add the cts child to the database.
	 *
	 * @param ctsChildId the primary key for the new cts child
	 * @return the new cts child
	 */
	@Override
	public CTSChild create(long ctsChildId) {
		CTSChild ctsChild = new CTSChildImpl();

		ctsChild.setNew(true);
		ctsChild.setPrimaryKey(ctsChildId);

		ctsChild.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsChild;
	}

	/**
	 * Removes the cts child with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child that was removed
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild remove(long ctsChildId) throws NoSuchCTSChildException {
		return remove((Serializable)ctsChildId);
	}

	/**
	 * Removes the cts child with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cts child
	 * @return the cts child that was removed
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild remove(Serializable primaryKey)
		throws NoSuchCTSChildException {

		Session session = null;

		try {
			session = openSession();

			CTSChild ctsChild = (CTSChild)session.get(
				CTSChildImpl.class, primaryKey);

			if (ctsChild == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCTSChildException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ctsChild);
		}
		catch (NoSuchCTSChildException noSuchEntityException) {
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
	protected CTSChild removeImpl(CTSChild ctsChild) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsChild)) {
				ctsChild = (CTSChild)session.get(
					CTSChildImpl.class, ctsChild.getPrimaryKeyObj());
			}

			if ((ctsChild != null) && ctPersistenceHelper.isRemove(ctsChild)) {
				session.delete(ctsChild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsChild != null) {
			clearCache(ctsChild);
		}

		return ctsChild;
	}

	@Override
	public CTSChild updateImpl(CTSChild ctsChild) {
		boolean isNew = ctsChild.isNew();

		if (!(ctsChild instanceof CTSChildModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsChild.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctsChild);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsChild proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSChild implementation " +
					ctsChild.getClass());
		}

		CTSChildModelImpl ctsChildModelImpl = (CTSChildModelImpl)ctsChild;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ctsChild)) {
				if (!isNew) {
					session.evict(
						CTSChildImpl.class, ctsChild.getPrimaryKeyObj());
				}

				session.save(ctsChild);
			}
			else {
				ctsChild = (CTSChild)session.merge(ctsChild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CTSChildImpl.class, ctsChildModelImpl, false, true);

		if (isNew) {
			ctsChild.setNew(false);
		}

		ctsChild.resetOriginalValues();

		return ctsChild;
	}

	/**
	 * Returns the cts child with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cts child
	 * @return the cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCTSChildException {

		CTSChild ctsChild = fetchByPrimaryKey(primaryKey);

		if (ctsChild == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCTSChildException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ctsChild;
	}

	/**
	 * Returns the cts child with the primary key or throws a <code>NoSuchCTSChildException</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild findByPrimaryKey(long ctsChildId)
		throws NoSuchCTSChildException {

		return findByPrimaryKey((Serializable)ctsChildId);
	}

	/**
	 * Returns the cts child with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cts child
	 * @return the cts child, or <code>null</code> if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(CTSChild.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CTSChild ctsChild = (CTSChild)entityCache.getResult(
			CTSChildImpl.class, primaryKey);

		if (ctsChild != null) {
			return ctsChild;
		}

		Session session = null;

		try {
			session = openSession();

			ctsChild = (CTSChild)session.get(CTSChildImpl.class, primaryKey);

			if (ctsChild != null) {
				cacheResult(ctsChild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ctsChild;
	}

	/**
	 * Returns the cts child with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child, or <code>null</code> if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild fetchByPrimaryKey(long ctsChildId) {
		return fetchByPrimaryKey((Serializable)ctsChildId);
	}

	@Override
	public Map<Serializable, CTSChild> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CTSChild.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CTSChild> map = new HashMap<Serializable, CTSChild>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CTSChild ctsChild = fetchByPrimaryKey(primaryKey);

			if (ctsChild != null) {
				map.put(primaryKey, ctsChild);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CTSChild.class, primaryKey)) {

				CTSChild ctsChild = (CTSChild)entityCache.getResult(
					CTSChildImpl.class, primaryKey);

				if (ctsChild == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, ctsChild);
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

			for (CTSChild ctsChild : (List<CTSChild>)query.list()) {
				map.put(ctsChild.getPrimaryKeyObj(), ctsChild);

				cacheResult(ctsChild);
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
	 * Returns all the cts childs.
	 *
	 * @return the cts childs
	 */
	@Override
	public List<CTSChild> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of cts childs
	 */
	@Override
	public List<CTSChild> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts childs
	 */
	@Override
	public List<CTSChild> findAll(
		int start, int end, OrderByComparator<CTSChild> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts childs
	 */
	@Override
	public List<CTSChild> findAll(
		int start, int end, OrderByComparator<CTSChild> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

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

			List<CTSChild> list = null;

			if (useFinderCache) {
				list = (List<CTSChild>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CTSCHILD);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CTSCHILD;

					sql = sql.concat(CTSChildModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CTSChild>)QueryUtil.list(
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
	 * Removes all the cts childs from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CTSChild ctsChild : findAll()) {
			remove(ctsChild);
		}
	}

	/**
	 * Returns the number of cts childs.
	 *
	 * @return the number of cts childs
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CTSChild.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_CTSCHILD);

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
		return "ctsChildId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSCHILD;
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
		return CTSChildModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CTSChild";
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
		ctMergeColumnNames.add("parentCTSChildId");
		ctMergeColumnNames.add("ctsParentName");
		ctMergeColumnNames.add("name");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("ctsChildId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the cts child persistence.
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

		_finderPathWithPaginationFindByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "parentCTSChildId"}, true);

		_finderPathWithoutPaginationFindByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "parentCTSChildId"}, true);

		_finderPathCountByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "parentCTSChildId"}, false);

		CTSChildUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSChildUtil.setPersistence(null);

		entityCache.removeCache(CTSChildImpl.class.getName());
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

	private static final String _SQL_SELECT_CTSCHILD =
		"SELECT ctsChild FROM CTSChild ctsChild";

	private static final String _SQL_SELECT_CTSCHILD_WHERE =
		"SELECT ctsChild FROM CTSChild ctsChild WHERE ";

	private static final String _SQL_COUNT_CTSCHILD =
		"SELECT COUNT(ctsChild) FROM CTSChild ctsChild";

	private static final String _SQL_COUNT_CTSCHILD_WHERE =
		"SELECT COUNT(ctsChild) FROM CTSChild ctsChild WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ctsChild.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CTSChild exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTSChild exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTSChildPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}