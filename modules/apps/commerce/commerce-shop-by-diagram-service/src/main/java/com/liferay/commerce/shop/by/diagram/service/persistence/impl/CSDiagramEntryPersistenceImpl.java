/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.impl;

import com.liferay.commerce.shop.by.diagram.exception.DuplicateCSDiagramEntryExternalReferenceCodeException;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramEntryException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntryTable;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryImpl;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramEntryPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramEntryUtil;
import com.liferay.commerce.shop.by.diagram.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * The persistence implementation for the cs diagram entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CSDiagramEntryPersistence.class)
public class CSDiagramEntryPersistenceImpl
	extends BasePersistenceImpl<CSDiagramEntry>
	implements CSDiagramEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CSDiagramEntryUtil</code> to access the cs diagram entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CSDiagramEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCPDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByCPDefinitionId;
	private FinderPath _finderPathCountByCPDefinitionId;

	/**
	 * Returns all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPDefinitionId(long CPDefinitionId) {
		return findByCPDefinitionId(
			CPDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return findByCPDefinitionId(CPDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCPDefinitionId;
					finderArgs = new Object[] {CPDefinitionId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCPDefinitionId;
				finderArgs = new Object[] {
					CPDefinitionId, start, end, orderByComparator
				};
			}

			List<CSDiagramEntry> list = null;

			if (useFinderCache) {
				list = (List<CSDiagramEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CSDiagramEntry csDiagramEntry : list) {
						if (CPDefinitionId !=
								csDiagramEntry.getCPDefinitionId()) {

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

				sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					list = (List<CSDiagramEntry>)QueryUtil.list(
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
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append("}");

		throw new NoSuchCSDiagramEntryException(sb.toString());
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		List<CSDiagramEntry> list = findByCPDefinitionId(
			CPDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPDefinitionId_Last(
			long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCPDefinitionId_Last(
			CPDefinitionId, orderByComparator);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append("}");

		throw new NoSuchCSDiagramEntryException(sb.toString());
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPDefinitionId_Last(
		long CPDefinitionId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		int count = countByCPDefinitionId(CPDefinitionId);

		if (count == 0) {
			return null;
		}

		List<CSDiagramEntry> list = findByCPDefinitionId(
			CPDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry[] findByCPDefinitionId_PrevAndNext(
			long CSDiagramEntryId, long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByPrimaryKey(CSDiagramEntryId);

		Session session = null;

		try {
			session = openSession();

			CSDiagramEntry[] array = new CSDiagramEntryImpl[3];

			array[0] = getByCPDefinitionId_PrevAndNext(
				session, csDiagramEntry, CPDefinitionId, orderByComparator,
				true);

			array[1] = csDiagramEntry;

			array[2] = getByCPDefinitionId_PrevAndNext(
				session, csDiagramEntry, CPDefinitionId, orderByComparator,
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

	protected CSDiagramEntry getByCPDefinitionId_PrevAndNext(
		Session session, CSDiagramEntry csDiagramEntry, long CPDefinitionId,
		OrderByComparator<CSDiagramEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

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
			sb.append(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						csDiagramEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CSDiagramEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cs diagram entries where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		for (CSDiagramEntry csDiagramEntry :
				findByCPDefinitionId(
					CPDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(csDiagramEntry);
		}
	}

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			FinderPath finderPath = _finderPathCountByCPDefinitionId;

			Object[] finderArgs = new Object[] {CPDefinitionId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

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

	private static final String _FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2 =
		"csDiagramEntry.CPDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByCPInstanceId;
	private FinderPath _finderPathWithoutPaginationFindByCPInstanceId;
	private FinderPath _finderPathCountByCPInstanceId;

	/**
	 * Returns all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPInstanceId(long CPInstanceId) {
		return findByCPInstanceId(
			CPInstanceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end) {

		return findByCPInstanceId(CPInstanceId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return findByCPInstanceId(
			CPInstanceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCPInstanceId;
					finderArgs = new Object[] {CPInstanceId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCPInstanceId;
				finderArgs = new Object[] {
					CPInstanceId, start, end, orderByComparator
				};
			}

			List<CSDiagramEntry> list = null;

			if (useFinderCache) {
				list = (List<CSDiagramEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CSDiagramEntry csDiagramEntry : list) {
						if (CPInstanceId != csDiagramEntry.getCPInstanceId()) {
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

				sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPINSTANCEID_CPINSTANCEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPInstanceId);

					list = (List<CSDiagramEntry>)QueryUtil.list(
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
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCPInstanceId_First(
			CPInstanceId, orderByComparator);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPInstanceId=");
		sb.append(CPInstanceId);

		sb.append("}");

		throw new NoSuchCSDiagramEntryException(sb.toString());
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		List<CSDiagramEntry> list = findByCPInstanceId(
			CPInstanceId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPInstanceId_Last(
			long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCPInstanceId_Last(
			CPInstanceId, orderByComparator);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPInstanceId=");
		sb.append(CPInstanceId);

		sb.append("}");

		throw new NoSuchCSDiagramEntryException(sb.toString());
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPInstanceId_Last(
		long CPInstanceId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		int count = countByCPInstanceId(CPInstanceId);

		if (count == 0) {
			return null;
		}

		List<CSDiagramEntry> list = findByCPInstanceId(
			CPInstanceId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry[] findByCPInstanceId_PrevAndNext(
			long CSDiagramEntryId, long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByPrimaryKey(CSDiagramEntryId);

		Session session = null;

		try {
			session = openSession();

			CSDiagramEntry[] array = new CSDiagramEntryImpl[3];

			array[0] = getByCPInstanceId_PrevAndNext(
				session, csDiagramEntry, CPInstanceId, orderByComparator, true);

			array[1] = csDiagramEntry;

			array[2] = getByCPInstanceId_PrevAndNext(
				session, csDiagramEntry, CPInstanceId, orderByComparator,
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

	protected CSDiagramEntry getByCPInstanceId_PrevAndNext(
		Session session, CSDiagramEntry csDiagramEntry, long CPInstanceId,
		OrderByComparator<CSDiagramEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_CPINSTANCEID_CPINSTANCEID_2);

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
			sb.append(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPInstanceId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						csDiagramEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CSDiagramEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cs diagram entries where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByCPInstanceId(long CPInstanceId) {
		for (CSDiagramEntry csDiagramEntry :
				findByCPInstanceId(
					CPInstanceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(csDiagramEntry);
		}
	}

	/**
	 * Returns the number of cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCPInstanceId(long CPInstanceId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			FinderPath finderPath = _finderPathCountByCPInstanceId;

			Object[] finderArgs = new Object[] {CPInstanceId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPINSTANCEID_CPINSTANCEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPInstanceId);

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

	private static final String _FINDER_COLUMN_CPINSTANCEID_CPINSTANCEID_2 =
		"csDiagramEntry.CPInstanceId = ?";

	private FinderPath _finderPathWithPaginationFindByCProductId;
	private FinderPath _finderPathWithoutPaginationFindByCProductId;
	private FinderPath _finderPathCountByCProductId;

	/**
	 * Returns all the cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCProductId(long CProductId) {
		return findByCProductId(
			CProductId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end) {

		return findByCProductId(CProductId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return findByCProductId(
			CProductId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCProductId;
					finderArgs = new Object[] {CProductId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCProductId;
				finderArgs = new Object[] {
					CProductId, start, end, orderByComparator
				};
			}

			List<CSDiagramEntry> list = null;

			if (useFinderCache) {
				list = (List<CSDiagramEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CSDiagramEntry csDiagramEntry : list) {
						if (CProductId != csDiagramEntry.getCProductId()) {
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

				sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPRODUCTID_CPRODUCTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CProductId);

					list = (List<CSDiagramEntry>)QueryUtil.list(
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
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCProductId_First(
			long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCProductId_First(
			CProductId, orderByComparator);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CProductId=");
		sb.append(CProductId);

		sb.append("}");

		throw new NoSuchCSDiagramEntryException(sb.toString());
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCProductId_First(
		long CProductId, OrderByComparator<CSDiagramEntry> orderByComparator) {

		List<CSDiagramEntry> list = findByCProductId(
			CProductId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCProductId_Last(
			long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCProductId_Last(
			CProductId, orderByComparator);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CProductId=");
		sb.append(CProductId);

		sb.append("}");

		throw new NoSuchCSDiagramEntryException(sb.toString());
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCProductId_Last(
		long CProductId, OrderByComparator<CSDiagramEntry> orderByComparator) {

		int count = countByCProductId(CProductId);

		if (count == 0) {
			return null;
		}

		List<CSDiagramEntry> list = findByCProductId(
			CProductId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry[] findByCProductId_PrevAndNext(
			long CSDiagramEntryId, long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByPrimaryKey(CSDiagramEntryId);

		Session session = null;

		try {
			session = openSession();

			CSDiagramEntry[] array = new CSDiagramEntryImpl[3];

			array[0] = getByCProductId_PrevAndNext(
				session, csDiagramEntry, CProductId, orderByComparator, true);

			array[1] = csDiagramEntry;

			array[2] = getByCProductId_PrevAndNext(
				session, csDiagramEntry, CProductId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CSDiagramEntry getByCProductId_PrevAndNext(
		Session session, CSDiagramEntry csDiagramEntry, long CProductId,
		OrderByComparator<CSDiagramEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_CPRODUCTID_CPRODUCTID_2);

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
			sb.append(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CProductId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						csDiagramEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CSDiagramEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cs diagram entries where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCProductId(long CProductId) {
		for (CSDiagramEntry csDiagramEntry :
				findByCProductId(
					CProductId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(csDiagramEntry);
		}
	}

	/**
	 * Returns the number of cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCProductId(long CProductId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			FinderPath finderPath = _finderPathCountByCProductId;

			Object[] finderArgs = new Object[] {CProductId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPRODUCTID_CPRODUCTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CProductId);

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

	private static final String _FINDER_COLUMN_CPRODUCTID_CPRODUCTID_2 =
		"csDiagramEntry.CProductId = ?";

	private FinderPath _finderPathFetchByCPDI_S;

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPDI_S(long CPDefinitionId, String sequence)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByCPDI_S(CPDefinitionId, sequence);

		if (csDiagramEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("CPDefinitionId=");
			sb.append(CPDefinitionId);

			sb.append(", sequence=");
			sb.append(sequence);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCSDiagramEntryException(sb.toString());
		}

		return csDiagramEntry;
	}

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPDI_S(long CPDefinitionId, String sequence) {
		return fetchByCPDI_S(CPDefinitionId, sequence, true);
	}

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPDI_S(
		long CPDefinitionId, String sequence, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			sequence = Objects.toString(sequence, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {CPDefinitionId, sequence};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByCPDI_S, finderArgs, this);
			}

			if (result instanceof CSDiagramEntry) {
				CSDiagramEntry csDiagramEntry = (CSDiagramEntry)result;

				if ((CPDefinitionId != csDiagramEntry.getCPDefinitionId()) ||
					!Objects.equals(sequence, csDiagramEntry.getSequence())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CPDI_S_CPDEFINITIONID_2);

				boolean bindSequence = false;

				if (sequence.isEmpty()) {
					sb.append(_FINDER_COLUMN_CPDI_S_SEQUENCE_3);
				}
				else {
					bindSequence = true;

					sb.append(_FINDER_COLUMN_CPDI_S_SEQUENCE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					if (bindSequence) {
						queryPos.add(sequence);
					}

					List<CSDiagramEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByCPDI_S, finderArgs, list);
						}
					}
					else {
						CSDiagramEntry csDiagramEntry = list.get(0);

						result = csDiagramEntry;

						cacheResult(csDiagramEntry);
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
				return (CSDiagramEntry)result;
			}
		}
	}

	/**
	 * Removes the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the cs diagram entry that was removed
	 */
	@Override
	public CSDiagramEntry removeByCPDI_S(long CPDefinitionId, String sequence)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByCPDI_S(CPDefinitionId, sequence);

		return remove(csDiagramEntry);
	}

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63; and sequence = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCPDI_S(long CPDefinitionId, String sequence) {
		CSDiagramEntry csDiagramEntry = fetchByCPDI_S(CPDefinitionId, sequence);

		if (csDiagramEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_CPDI_S_CPDEFINITIONID_2 =
		"csDiagramEntry.CPDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_CPDI_S_SEQUENCE_2 =
		"csDiagramEntry.sequence = ?";

	private static final String _FINDER_COLUMN_CPDI_S_SEQUENCE_3 =
		"(csDiagramEntry.sequence IS NULL OR csDiagramEntry.sequence = '')";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (csDiagramEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCSDiagramEntryException(sb.toString());
		}

		return csDiagramEntry;
	}

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, companyId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_C, finderArgs, this);
			}

			if (result instanceof CSDiagramEntry) {
				CSDiagramEntry csDiagramEntry = (CSDiagramEntry)result;

				if (!Objects.equals(
						externalReferenceCode,
						csDiagramEntry.getExternalReferenceCode()) ||
					(companyId != csDiagramEntry.getCompanyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CSDIAGRAMENTRY_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(companyId);

					List<CSDiagramEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_C, finderArgs, list);
						}
					}
					else {
						CSDiagramEntry csDiagramEntry = list.get(0);

						result = csDiagramEntry;

						cacheResult(csDiagramEntry);
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
				return (CSDiagramEntry)result;
			}
		}
	}

	/**
	 * Removes the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cs diagram entry that was removed
	 */
	@Override
	public CSDiagramEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(csDiagramEntry);
	}

	/**
	 * Returns the number of cs diagram entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CSDiagramEntry csDiagramEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (csDiagramEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"csDiagramEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(csDiagramEntry.externalReferenceCode IS NULL OR csDiagramEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"csDiagramEntry.companyId = ?";

	public CSDiagramEntryPersistenceImpl() {
		setModelClass(CSDiagramEntry.class);

		setModelImplClass(CSDiagramEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CSDiagramEntryTable.INSTANCE);
	}

	/**
	 * Caches the cs diagram entry in the entity cache if it is enabled.
	 *
	 * @param csDiagramEntry the cs diagram entry
	 */
	@Override
	public void cacheResult(CSDiagramEntry csDiagramEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					csDiagramEntry.getCtCollectionId())) {

			entityCache.putResult(
				CSDiagramEntryImpl.class, csDiagramEntry.getPrimaryKey(),
				csDiagramEntry);

			finderCache.putResult(
				_finderPathFetchByCPDI_S,
				new Object[] {
					csDiagramEntry.getCPDefinitionId(),
					csDiagramEntry.getSequence()
				},
				csDiagramEntry);

			finderCache.putResult(
				_finderPathFetchByERC_C,
				new Object[] {
					csDiagramEntry.getExternalReferenceCode(),
					csDiagramEntry.getCompanyId()
				},
				csDiagramEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cs diagram entries in the entity cache if it is enabled.
	 *
	 * @param csDiagramEntries the cs diagram entries
	 */
	@Override
	public void cacheResult(List<CSDiagramEntry> csDiagramEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (csDiagramEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CSDiagramEntry csDiagramEntry : csDiagramEntries) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						csDiagramEntry.getCtCollectionId())) {

				if (entityCache.getResult(
						CSDiagramEntryImpl.class,
						csDiagramEntry.getPrimaryKey()) == null) {

					cacheResult(csDiagramEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cs diagram entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CSDiagramEntryImpl.class);

		finderCache.clearCache(CSDiagramEntryImpl.class);
	}

	/**
	 * Clears the cache for the cs diagram entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CSDiagramEntry csDiagramEntry) {
		entityCache.removeResult(CSDiagramEntryImpl.class, csDiagramEntry);
	}

	@Override
	public void clearCache(List<CSDiagramEntry> csDiagramEntries) {
		for (CSDiagramEntry csDiagramEntry : csDiagramEntries) {
			entityCache.removeResult(CSDiagramEntryImpl.class, csDiagramEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CSDiagramEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CSDiagramEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CSDiagramEntryModelImpl csDiagramEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					csDiagramEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				csDiagramEntryModelImpl.getCPDefinitionId(),
				csDiagramEntryModelImpl.getSequence()
			};

			finderCache.putResult(
				_finderPathFetchByCPDI_S, args, csDiagramEntryModelImpl);

			args = new Object[] {
				csDiagramEntryModelImpl.getExternalReferenceCode(),
				csDiagramEntryModelImpl.getCompanyId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_C, args, csDiagramEntryModelImpl);
		}
	}

	/**
	 * Creates a new cs diagram entry with the primary key. Does not add the cs diagram entry to the database.
	 *
	 * @param CSDiagramEntryId the primary key for the new cs diagram entry
	 * @return the new cs diagram entry
	 */
	@Override
	public CSDiagramEntry create(long CSDiagramEntryId) {
		CSDiagramEntry csDiagramEntry = new CSDiagramEntryImpl();

		csDiagramEntry.setNew(true);
		csDiagramEntry.setPrimaryKey(CSDiagramEntryId);

		csDiagramEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return csDiagramEntry;
	}

	/**
	 * Removes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry remove(long CSDiagramEntryId)
		throws NoSuchCSDiagramEntryException {

		return remove((Serializable)CSDiagramEntryId);
	}

	/**
	 * Removes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry remove(Serializable primaryKey)
		throws NoSuchCSDiagramEntryException {

		Session session = null;

		try {
			session = openSession();

			CSDiagramEntry csDiagramEntry = (CSDiagramEntry)session.get(
				CSDiagramEntryImpl.class, primaryKey);

			if (csDiagramEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCSDiagramEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(csDiagramEntry);
		}
		catch (NoSuchCSDiagramEntryException noSuchEntityException) {
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
	protected CSDiagramEntry removeImpl(CSDiagramEntry csDiagramEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(csDiagramEntry)) {
				csDiagramEntry = (CSDiagramEntry)session.get(
					CSDiagramEntryImpl.class,
					csDiagramEntry.getPrimaryKeyObj());
			}

			if ((csDiagramEntry != null) &&
				ctPersistenceHelper.isRemove(csDiagramEntry)) {

				session.delete(csDiagramEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (csDiagramEntry != null) {
			clearCache(csDiagramEntry);
		}

		return csDiagramEntry;
	}

	@Override
	public CSDiagramEntry updateImpl(CSDiagramEntry csDiagramEntry) {
		boolean isNew = csDiagramEntry.isNew();

		if (!(csDiagramEntry instanceof CSDiagramEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(csDiagramEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					csDiagramEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in csDiagramEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CSDiagramEntry implementation " +
					csDiagramEntry.getClass());
		}

		CSDiagramEntryModelImpl csDiagramEntryModelImpl =
			(CSDiagramEntryModelImpl)csDiagramEntry;

		if (Validator.isNull(csDiagramEntry.getExternalReferenceCode())) {
			csDiagramEntry.setExternalReferenceCode(
				String.valueOf(csDiagramEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					csDiagramEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					csDiagramEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = csDiagramEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = csDiagramEntry.getPrimaryKey();
					}

					try {
						csDiagramEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CSDiagramEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								csDiagramEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CSDiagramEntry ercCSDiagramEntry = fetchByERC_C(
				csDiagramEntry.getExternalReferenceCode(),
				csDiagramEntry.getCompanyId());

			if (isNew) {
				if (ercCSDiagramEntry != null) {
					throw new DuplicateCSDiagramEntryExternalReferenceCodeException(
						"Duplicate cs diagram entry with external reference code " +
							csDiagramEntry.getExternalReferenceCode() +
								" and company " +
									csDiagramEntry.getCompanyId());
				}
			}
			else {
				if ((ercCSDiagramEntry != null) &&
					(csDiagramEntry.getCSDiagramEntryId() !=
						ercCSDiagramEntry.getCSDiagramEntryId())) {

					throw new DuplicateCSDiagramEntryExternalReferenceCodeException(
						"Duplicate cs diagram entry with external reference code " +
							csDiagramEntry.getExternalReferenceCode() +
								" and company " +
									csDiagramEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (csDiagramEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				csDiagramEntry.setCreateDate(date);
			}
			else {
				csDiagramEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!csDiagramEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				csDiagramEntry.setModifiedDate(date);
			}
			else {
				csDiagramEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(csDiagramEntry)) {
				if (!isNew) {
					session.evict(
						CSDiagramEntryImpl.class,
						csDiagramEntry.getPrimaryKeyObj());
				}

				session.save(csDiagramEntry);
			}
			else {
				csDiagramEntry = (CSDiagramEntry)session.merge(csDiagramEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CSDiagramEntryImpl.class, csDiagramEntryModelImpl, false, true);

		cacheUniqueFindersCache(csDiagramEntryModelImpl);

		if (isNew) {
			csDiagramEntry.setNew(false);
		}

		csDiagramEntry.resetOriginalValues();

		return csDiagramEntry;
	}

	/**
	 * Returns the cs diagram entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = fetchByPrimaryKey(primaryKey);

		if (csDiagramEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCSDiagramEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return csDiagramEntry;
	}

	/**
	 * Returns the cs diagram entry with the primary key or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry findByPrimaryKey(long CSDiagramEntryId)
		throws NoSuchCSDiagramEntryException {

		return findByPrimaryKey((Serializable)CSDiagramEntryId);
	}

	/**
	 * Returns the cs diagram entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cs diagram entry
	 * @return the cs diagram entry, or <code>null</code> if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				CSDiagramEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CSDiagramEntry csDiagramEntry = (CSDiagramEntry)entityCache.getResult(
			CSDiagramEntryImpl.class, primaryKey);

		if (csDiagramEntry != null) {
			return csDiagramEntry;
		}

		Session session = null;

		try {
			session = openSession();

			csDiagramEntry = (CSDiagramEntry)session.get(
				CSDiagramEntryImpl.class, primaryKey);

			if (csDiagramEntry != null) {
				cacheResult(csDiagramEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return csDiagramEntry;
	}

	/**
	 * Returns the cs diagram entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry, or <code>null</code> if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry fetchByPrimaryKey(long CSDiagramEntryId) {
		return fetchByPrimaryKey((Serializable)CSDiagramEntryId);
	}

	@Override
	public Map<Serializable, CSDiagramEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CSDiagramEntry.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CSDiagramEntry> map =
			new HashMap<Serializable, CSDiagramEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CSDiagramEntry csDiagramEntry = fetchByPrimaryKey(primaryKey);

			if (csDiagramEntry != null) {
				map.put(primaryKey, csDiagramEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CSDiagramEntry.class, primaryKey)) {

				CSDiagramEntry csDiagramEntry =
					(CSDiagramEntry)entityCache.getResult(
						CSDiagramEntryImpl.class, primaryKey);

				if (csDiagramEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, csDiagramEntry);
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

			for (CSDiagramEntry csDiagramEntry :
					(List<CSDiagramEntry>)query.list()) {

				map.put(csDiagramEntry.getPrimaryKeyObj(), csDiagramEntry);

				cacheResult(csDiagramEntry);
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
	 * Returns all the cs diagram entries.
	 *
	 * @return the cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findAll(
		int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findAll(
		int start, int end, OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

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

			List<CSDiagramEntry> list = null;

			if (useFinderCache) {
				list = (List<CSDiagramEntry>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CSDIAGRAMENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CSDIAGRAMENTRY;

					sql = sql.concat(CSDiagramEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CSDiagramEntry>)QueryUtil.list(
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
	 * Removes all the cs diagram entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CSDiagramEntry csDiagramEntry : findAll()) {
			remove(csDiagramEntry);
		}
	}

	/**
	 * Returns the number of cs diagram entries.
	 *
	 * @return the number of cs diagram entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CSDiagramEntry.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_CSDIAGRAMENTRY);

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
		return "CSDiagramEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CSDIAGRAMENTRY;
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
		return CSDiagramEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CSDiagramEntry";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("CPInstanceId");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("diagram");
		ctMergeColumnNames.add("quantity");
		ctMergeColumnNames.add("sequence");
		ctMergeColumnNames.add("sku");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CSDiagramEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "sequence"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cs diagram entry persistence.
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

		_finderPathWithPaginationFindByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPDefinitionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPDefinitionId"}, true);

		_finderPathWithoutPaginationFindByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPDefinitionId",
			new String[] {Long.class.getName()},
			new String[] {"CPDefinitionId"}, true);

		_finderPathCountByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPDefinitionId",
			new String[] {Long.class.getName()},
			new String[] {"CPDefinitionId"}, false);

		_finderPathWithPaginationFindByCPInstanceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPInstanceId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPInstanceId"}, true);

		_finderPathWithoutPaginationFindByCPInstanceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPInstanceId",
			new String[] {Long.class.getName()}, new String[] {"CPInstanceId"},
			true);

		_finderPathCountByCPInstanceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPInstanceId",
			new String[] {Long.class.getName()}, new String[] {"CPInstanceId"},
			false);

		_finderPathWithPaginationFindByCProductId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCProductId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CProductId"}, true);

		_finderPathWithoutPaginationFindByCProductId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCProductId",
			new String[] {Long.class.getName()}, new String[] {"CProductId"},
			true);

		_finderPathCountByCProductId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCProductId",
			new String[] {Long.class.getName()}, new String[] {"CProductId"},
			false);

		_finderPathFetchByCPDI_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByCPDI_S",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"CPDefinitionId", "sequence"}, true);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CSDiagramEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CSDiagramEntryUtil.setPersistence(null);

		entityCache.removeCache(CSDiagramEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_CSDIAGRAMENTRY =
		"SELECT csDiagramEntry FROM CSDiagramEntry csDiagramEntry";

	private static final String _SQL_SELECT_CSDIAGRAMENTRY_WHERE =
		"SELECT csDiagramEntry FROM CSDiagramEntry csDiagramEntry WHERE ";

	private static final String _SQL_COUNT_CSDIAGRAMENTRY =
		"SELECT COUNT(csDiagramEntry) FROM CSDiagramEntry csDiagramEntry";

	private static final String _SQL_COUNT_CSDIAGRAMENTRY_WHERE =
		"SELECT COUNT(csDiagramEntry) FROM CSDiagramEntry csDiagramEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "csDiagramEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CSDiagramEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CSDiagramEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CSDiagramEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}