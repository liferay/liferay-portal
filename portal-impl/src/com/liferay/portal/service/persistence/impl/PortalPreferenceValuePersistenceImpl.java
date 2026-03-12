/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPreferenceValueException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferenceValueTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValuePersistence;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValueUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.PortalPreferenceValueImpl;
import com.liferay.portal.model.impl.PortalPreferenceValueModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the portal preference value service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortalPreferenceValuePersistenceImpl
	extends BasePersistenceImpl<PortalPreferenceValue>
	implements PortalPreferenceValuePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortalPreferenceValueUtil</code> to access the portal preference value persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortalPreferenceValueImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByPortalPreferencesId;
	private FinderPath _finderPathWithoutPaginationFindByPortalPreferencesId;
	private FinderPath _finderPathCountByPortalPreferencesId;
	private FinderPath _finderPathWithPaginationCountByPortalPreferencesId;

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @return the matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId) {

		return findByPortalPreferencesId(
			portalPreferencesId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end) {

		return findByPortalPreferencesId(portalPreferencesId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return findByPortalPreferencesId(
			portalPreferencesId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByPortalPreferencesId;
				finderArgs = new Object[] {portalPreferencesId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPortalPreferencesId;
			finderArgs = new Object[] {
				portalPreferencesId, start, end, orderByComparator
			};
		}

		List<PortalPreferenceValue> list = null;

		if (useFinderCache) {
			list = (List<PortalPreferenceValue>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PortalPreferenceValue portalPreferenceValue : list) {
					if (portalPreferencesId !=
							portalPreferenceValue.getPortalPreferencesId()) {

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

			sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				list = (List<PortalPreferenceValue>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByPortalPreferencesId_First(
			long portalPreferencesId,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue =
			fetchByPortalPreferencesId_First(
				portalPreferencesId, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByPortalPreferencesId_First(
		long portalPreferencesId,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		List<PortalPreferenceValue> list = findByPortalPreferencesId(
			portalPreferencesId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByPortalPreferencesId_Last(
			long portalPreferencesId,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue =
			fetchByPortalPreferencesId_Last(
				portalPreferencesId, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByPortalPreferencesId_Last(
		long portalPreferencesId,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		int count = countByPortalPreferencesId(portalPreferencesId);

		if (count == 0) {
			return null;
		}

		List<PortalPreferenceValue> list = findByPortalPreferencesId(
			portalPreferencesId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue[] findByPortalPreferencesId_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = findByPrimaryKey(
			portalPreferenceValueId);

		Session session = null;

		try {
			session = openSession();

			PortalPreferenceValue[] array = new PortalPreferenceValueImpl[3];

			array[0] = getByPortalPreferencesId_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId,
				orderByComparator, true);

			array[1] = portalPreferenceValue;

			array[2] = getByPortalPreferencesId_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId,
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

	protected PortalPreferenceValue getByPortalPreferencesId_PrevAndNext(
		Session session, PortalPreferenceValue portalPreferenceValue,
		long portalPreferencesId,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
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

		sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

		sb.append(_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_2);

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
			sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(portalPreferencesId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						portalPreferenceValue)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PortalPreferenceValue> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @return the matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds) {

		return findByPortalPreferencesId(
			portalPreferencesIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end) {

		return findByPortalPreferencesId(
			portalPreferencesIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return findByPortalPreferencesId(
			portalPreferencesIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		if (portalPreferencesIds == null) {
			portalPreferencesIds = new long[0];
		}
		else if (portalPreferencesIds.length > 1) {
			portalPreferencesIds = ArrayUtil.sortedUnique(portalPreferencesIds);
		}

		if (portalPreferencesIds.length == 1) {
			return findByPortalPreferencesId(
				portalPreferencesIds[0], start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(portalPreferencesIds)
				};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				StringUtil.merge(portalPreferencesIds), start, end,
				orderByComparator
			};
		}

		List<PortalPreferenceValue> list = null;

		if (useFinderCache) {
			list = (List<PortalPreferenceValue>)dummyFinderCache.getResult(
				_finderPathWithPaginationFindByPortalPreferencesId, finderArgs,
				this);

			if ((list != null) && !list.isEmpty()) {
				for (PortalPreferenceValue portalPreferenceValue : list) {
					if (!ArrayUtil.contains(
							portalPreferencesIds,
							portalPreferenceValue.getPortalPreferencesId())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

			if (portalPreferencesIds.length > 0) {
				sb.append("(");

				sb.append(
					_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_7);

				sb.append(StringUtil.merge(portalPreferencesIds));

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
				sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PortalPreferenceValue>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(
						_finderPathWithPaginationFindByPortalPreferencesId,
						finderArgs, list);
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
	 * Removes all the portal preference values where portalPreferencesId = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 */
	@Override
	public void removeByPortalPreferencesId(long portalPreferencesId) {
		for (PortalPreferenceValue portalPreferenceValue :
				findByPortalPreferencesId(
					portalPreferencesId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(portalPreferenceValue);
		}
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @return the number of matching portal preference values
	 */
	@Override
	public int countByPortalPreferencesId(long portalPreferencesId) {
		FinderPath finderPath = _finderPathCountByPortalPreferencesId;

		Object[] finderArgs = new Object[] {portalPreferencesId};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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
	 * Returns the number of portal preference values where portalPreferencesId = any &#63;.
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @return the number of matching portal preference values
	 */
	@Override
	public int countByPortalPreferencesId(long[] portalPreferencesIds) {
		if (portalPreferencesIds == null) {
			portalPreferencesIds = new long[0];
		}
		else if (portalPreferencesIds.length > 1) {
			portalPreferencesIds = ArrayUtil.sortedUnique(portalPreferencesIds);
		}

		Object[] finderArgs = new Object[] {
			StringUtil.merge(portalPreferencesIds)
		};

		Long count = (Long)dummyFinderCache.getResult(
			_finderPathWithPaginationCountByPortalPreferencesId, finderArgs,
			this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_PORTALPREFERENCEVALUE_WHERE);

			if (portalPreferencesIds.length > 0) {
				sb.append("(");

				sb.append(
					_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_7);

				sb.append(StringUtil.merge(portalPreferencesIds));

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

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(
					_finderPathWithPaginationCountByPortalPreferencesId,
					finderArgs, count);
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

	private static final String
		_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_2 =
			"portalPreferenceValue.portalPreferencesId = ?";

	private static final String
		_FINDER_COLUMN_PORTALPREFERENCESID_PORTALPREFERENCESID_7 =
			"portalPreferenceValue.portalPreferencesId IN (";

	private FinderPath _finderPathWithPaginationFindByP_N;
	private FinderPath _finderPathWithoutPaginationFindByP_N;
	private FinderPath _finderPathCountByP_N;

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @return the matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace) {

		return findByP_N(
			portalPreferencesId, namespace, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end) {

		return findByP_N(portalPreferencesId, namespace, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return findByP_N(
			portalPreferencesId, namespace, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		namespace = Objects.toString(namespace, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_N;
				finderArgs = new Object[] {portalPreferencesId, namespace};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_N;
			finderArgs = new Object[] {
				portalPreferencesId, namespace, start, end, orderByComparator
			};
		}

		List<PortalPreferenceValue> list = null;

		if (useFinderCache) {
			list = (List<PortalPreferenceValue>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PortalPreferenceValue portalPreferenceValue : list) {
					if ((portalPreferencesId !=
							portalPreferenceValue.getPortalPreferencesId()) ||
						!namespace.equals(
							portalPreferenceValue.getNamespace())) {

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

			sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_N_PORTALPREFERENCESID_2);

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_N_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_N_NAMESPACE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				list = (List<PortalPreferenceValue>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_N_First(
			long portalPreferencesId, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_N_First(
			portalPreferencesId, namespace, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append(", namespace=");
		sb.append(namespace);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_N_First(
		long portalPreferencesId, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		List<PortalPreferenceValue> list = findByP_N(
			portalPreferencesId, namespace, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_N_Last(
			long portalPreferencesId, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_N_Last(
			portalPreferencesId, namespace, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append(", namespace=");
		sb.append(namespace);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_N_Last(
		long portalPreferencesId, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		int count = countByP_N(portalPreferencesId, namespace);

		if (count == 0) {
			return null;
		}

		List<PortalPreferenceValue> list = findByP_N(
			portalPreferencesId, namespace, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue[] findByP_N_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId,
			String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		namespace = Objects.toString(namespace, "");

		PortalPreferenceValue portalPreferenceValue = findByPrimaryKey(
			portalPreferenceValueId);

		Session session = null;

		try {
			session = openSession();

			PortalPreferenceValue[] array = new PortalPreferenceValueImpl[3];

			array[0] = getByP_N_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId, namespace,
				orderByComparator, true);

			array[1] = portalPreferenceValue;

			array[2] = getByP_N_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId, namespace,
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

	protected PortalPreferenceValue getByP_N_PrevAndNext(
		Session session, PortalPreferenceValue portalPreferenceValue,
		long portalPreferencesId, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
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

		sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

		sb.append(_FINDER_COLUMN_P_N_PORTALPREFERENCESID_2);

		boolean bindNamespace = false;

		if (namespace.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_NAMESPACE_3);
		}
		else {
			bindNamespace = true;

			sb.append(_FINDER_COLUMN_P_N_NAMESPACE_2);
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
			sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(portalPreferencesId);

		if (bindNamespace) {
			queryPos.add(namespace);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						portalPreferenceValue)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PortalPreferenceValue> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 */
	@Override
	public void removeByP_N(long portalPreferencesId, String namespace) {
		for (PortalPreferenceValue portalPreferenceValue :
				findByP_N(
					portalPreferencesId, namespace, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(portalPreferenceValue);
		}
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	@Override
	public int countByP_N(long portalPreferencesId, String namespace) {
		namespace = Objects.toString(namespace, "");

		FinderPath finderPath = _finderPathCountByP_N;

		Object[] finderArgs = new Object[] {portalPreferencesId, namespace};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_N_PORTALPREFERENCESID_2);

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_N_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_N_NAMESPACE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_P_N_PORTALPREFERENCESID_2 =
		"portalPreferenceValue.portalPreferencesId = ? AND ";

	private static final String _FINDER_COLUMN_P_N_NAMESPACE_2 =
		"portalPreferenceValue.namespace = ?";

	private static final String _FINDER_COLUMN_P_N_NAMESPACE_3 =
		"(portalPreferenceValue.namespace IS NULL OR portalPreferenceValue.namespace = '')";

	private FinderPath _finderPathWithPaginationFindByP_K_N;
	private FinderPath _finderPathWithoutPaginationFindByP_K_N;
	private FinderPath _finderPathCountByP_K_N;

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace) {

		return findByP_K_N(
			portalPreferencesId, key, namespace, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end) {

		return findByP_K_N(
			portalPreferencesId, key, namespace, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end, OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return findByP_K_N(
			portalPreferencesId, key, namespace, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end, OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_K_N;
				finderArgs = new Object[] {portalPreferencesId, key, namespace};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_K_N;
			finderArgs = new Object[] {
				portalPreferencesId, key, namespace, start, end,
				orderByComparator
			};
		}

		List<PortalPreferenceValue> list = null;

		if (useFinderCache) {
			list = (List<PortalPreferenceValue>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PortalPreferenceValue portalPreferenceValue : list) {
					if ((portalPreferencesId !=
							portalPreferenceValue.getPortalPreferencesId()) ||
						!key.equals(portalPreferenceValue.getKey()) ||
						!namespace.equals(
							portalPreferenceValue.getNamespace())) {

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

			sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_K_N_PORTALPREFERENCESID_2);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_P_K_N_KEY_2);
			}

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_K_N_NAMESPACE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				if (bindKey) {
					queryPos.add(key);
				}

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				list = (List<PortalPreferenceValue>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_K_N_First(
			long portalPreferencesId, String key, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_K_N_First(
			portalPreferencesId, key, namespace, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append(", key=");
		sb.append(key);

		sb.append(", namespace=");
		sb.append(namespace);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_K_N_First(
		long portalPreferencesId, String key, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		List<PortalPreferenceValue> list = findByP_K_N(
			portalPreferencesId, key, namespace, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_K_N_Last(
			long portalPreferencesId, String key, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_K_N_Last(
			portalPreferencesId, key, namespace, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append(", key=");
		sb.append(key);

		sb.append(", namespace=");
		sb.append(namespace);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_K_N_Last(
		long portalPreferencesId, String key, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		int count = countByP_K_N(portalPreferencesId, key, namespace);

		if (count == 0) {
			return null;
		}

		List<PortalPreferenceValue> list = findByP_K_N(
			portalPreferencesId, key, namespace, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue[] findByP_K_N_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId, String key,
			String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");

		PortalPreferenceValue portalPreferenceValue = findByPrimaryKey(
			portalPreferenceValueId);

		Session session = null;

		try {
			session = openSession();

			PortalPreferenceValue[] array = new PortalPreferenceValueImpl[3];

			array[0] = getByP_K_N_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId, key,
				namespace, orderByComparator, true);

			array[1] = portalPreferenceValue;

			array[2] = getByP_K_N_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId, key,
				namespace, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PortalPreferenceValue getByP_K_N_PrevAndNext(
		Session session, PortalPreferenceValue portalPreferenceValue,
		long portalPreferencesId, String key, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
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

		sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

		sb.append(_FINDER_COLUMN_P_K_N_PORTALPREFERENCESID_2);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_K_N_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_P_K_N_KEY_2);
		}

		boolean bindNamespace = false;

		if (namespace.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_K_N_NAMESPACE_3);
		}
		else {
			bindNamespace = true;

			sb.append(_FINDER_COLUMN_P_K_N_NAMESPACE_2);
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
			sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(portalPreferencesId);

		if (bindKey) {
			queryPos.add(key);
		}

		if (bindNamespace) {
			queryPos.add(namespace);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						portalPreferenceValue)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PortalPreferenceValue> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 */
	@Override
	public void removeByP_K_N(
		long portalPreferencesId, String key, String namespace) {

		for (PortalPreferenceValue portalPreferenceValue :
				findByP_K_N(
					portalPreferencesId, key, namespace, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(portalPreferenceValue);
		}
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	@Override
	public int countByP_K_N(
		long portalPreferencesId, String key, String namespace) {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");

		FinderPath finderPath = _finderPathCountByP_K_N;

		Object[] finderArgs = new Object[] {
			portalPreferencesId, key, namespace
		};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_K_N_PORTALPREFERENCESID_2);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_P_K_N_KEY_2);
			}

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_K_N_NAMESPACE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				if (bindKey) {
					queryPos.add(key);
				}

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_P_K_N_PORTALPREFERENCESID_2 =
		"portalPreferenceValue.portalPreferencesId = ? AND ";

	private static final String _FINDER_COLUMN_P_K_N_KEY_2 =
		"portalPreferenceValue.key = ? AND ";

	private static final String _FINDER_COLUMN_P_K_N_KEY_3 =
		"(portalPreferenceValue.key IS NULL OR portalPreferenceValue.key = '') AND ";

	private static final String _FINDER_COLUMN_P_K_N_NAMESPACE_2 =
		"portalPreferenceValue.namespace = ?";

	private static final String _FINDER_COLUMN_P_K_N_NAMESPACE_3 =
		"(portalPreferenceValue.namespace IS NULL OR portalPreferenceValue.namespace = '')";

	private FinderPath _finderPathFetchByP_I_K_N;

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or throws a <code>NoSuchPreferenceValueException</code> if it could not be found.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_I_K_N(
			long portalPreferencesId, int index, String key, String namespace)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_I_K_N(
			portalPreferencesId, index, key, namespace);

		if (portalPreferenceValue == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("portalPreferencesId=");
			sb.append(portalPreferencesId);

			sb.append(", index=");
			sb.append(index);

			sb.append(", key=");
			sb.append(key);

			sb.append(", namespace=");
			sb.append(namespace);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPreferenceValueException(sb.toString());
		}

		return portalPreferenceValue;
	}

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace) {

		return fetchByP_I_K_N(portalPreferencesId, index, key, namespace, true);
	}

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace,
		boolean useFinderCache) {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				portalPreferencesId, index, key, namespace
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = dummyFinderCache.getResult(
				_finderPathFetchByP_I_K_N, finderArgs, this);
		}

		if (result instanceof PortalPreferenceValue) {
			PortalPreferenceValue portalPreferenceValue =
				(PortalPreferenceValue)result;

			if ((portalPreferencesId !=
					portalPreferenceValue.getPortalPreferencesId()) ||
				(index != portalPreferenceValue.getIndex()) ||
				!Objects.equals(key, portalPreferenceValue.getKey()) ||
				!Objects.equals(
					namespace, portalPreferenceValue.getNamespace())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_I_K_N_PORTALPREFERENCESID_2);

			sb.append(_FINDER_COLUMN_P_I_K_N_INDEX_2);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_I_K_N_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_P_I_K_N_KEY_2);
			}

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_I_K_N_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_I_K_N_NAMESPACE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				queryPos.add(index);

				if (bindKey) {
					queryPos.add(key);
				}

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				List<PortalPreferenceValue> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						dummyFinderCache.putResult(
							_finderPathFetchByP_I_K_N, finderArgs, list);
					}
				}
				else {
					PortalPreferenceValue portalPreferenceValue = list.get(0);

					result = portalPreferenceValue;

					cacheResult(portalPreferenceValue);
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
			return (PortalPreferenceValue)result;
		}
	}

	/**
	 * Removes the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the portal preference value that was removed
	 */
	@Override
	public PortalPreferenceValue removeByP_I_K_N(
			long portalPreferencesId, int index, String key, String namespace)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = findByP_I_K_N(
			portalPreferencesId, index, key, namespace);

		return remove(portalPreferenceValue);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	@Override
	public int countByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace) {

		PortalPreferenceValue portalPreferenceValue = fetchByP_I_K_N(
			portalPreferencesId, index, key, namespace);

		if (portalPreferenceValue == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_P_I_K_N_PORTALPREFERENCESID_2 =
		"portalPreferenceValue.portalPreferencesId = ? AND ";

	private static final String _FINDER_COLUMN_P_I_K_N_INDEX_2 =
		"portalPreferenceValue.index = ? AND ";

	private static final String _FINDER_COLUMN_P_I_K_N_KEY_2 =
		"portalPreferenceValue.key = ? AND ";

	private static final String _FINDER_COLUMN_P_I_K_N_KEY_3 =
		"(portalPreferenceValue.key IS NULL OR portalPreferenceValue.key = '') AND ";

	private static final String _FINDER_COLUMN_P_I_K_N_NAMESPACE_2 =
		"portalPreferenceValue.namespace = ?";

	private static final String _FINDER_COLUMN_P_I_K_N_NAMESPACE_3 =
		"(portalPreferenceValue.namespace IS NULL OR portalPreferenceValue.namespace = '')";

	private FinderPath _finderPathWithPaginationFindByP_K_N_SV;
	private FinderPath _finderPathWithoutPaginationFindByP_K_N_SV;
	private FinderPath _finderPathCountByP_K_N_SV;

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @return the matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue) {

		return findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end) {

		return findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");
		smallValue = Objects.toString(smallValue, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByP_K_N_SV;
				finderArgs = new Object[] {
					portalPreferencesId, key, namespace, smallValue
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByP_K_N_SV;
			finderArgs = new Object[] {
				portalPreferencesId, key, namespace, smallValue, start, end,
				orderByComparator
			};
		}

		List<PortalPreferenceValue> list = null;

		if (useFinderCache) {
			list = (List<PortalPreferenceValue>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PortalPreferenceValue portalPreferenceValue : list) {
					if ((portalPreferencesId !=
							portalPreferenceValue.getPortalPreferencesId()) ||
						!key.equals(portalPreferenceValue.getKey()) ||
						!namespace.equals(
							portalPreferenceValue.getNamespace()) ||
						!smallValue.equals(
							portalPreferenceValue.getSmallValue())) {

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

			sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_K_N_SV_PORTALPREFERENCESID_2);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_SV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_P_K_N_SV_KEY_2);
			}

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_SV_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_K_N_SV_NAMESPACE_2);
			}

			boolean bindSmallValue = false;

			if (smallValue.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_SV_SMALLVALUE_3);
			}
			else {
				bindSmallValue = true;

				sb.append(_FINDER_COLUMN_P_K_N_SV_SMALLVALUE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				if (bindKey) {
					queryPos.add(key);
				}

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				if (bindSmallValue) {
					queryPos.add(smallValue);
				}

				list = (List<PortalPreferenceValue>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_K_N_SV_First(
			long portalPreferencesId, String key, String namespace,
			String smallValue,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_K_N_SV_First(
			portalPreferencesId, key, namespace, smallValue, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append(", key=");
		sb.append(key);

		sb.append(", namespace=");
		sb.append(namespace);

		sb.append(", smallValue=");
		sb.append(smallValue);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_K_N_SV_First(
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		List<PortalPreferenceValue> list = findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue findByP_K_N_SV_Last(
			long portalPreferencesId, String key, String namespace,
			String smallValue,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByP_K_N_SV_Last(
			portalPreferencesId, key, namespace, smallValue, orderByComparator);

		if (portalPreferenceValue != null) {
			return portalPreferenceValue;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("portalPreferencesId=");
		sb.append(portalPreferencesId);

		sb.append(", key=");
		sb.append(key);

		sb.append(", namespace=");
		sb.append(namespace);

		sb.append(", smallValue=");
		sb.append(smallValue);

		sb.append("}");

		throw new NoSuchPreferenceValueException(sb.toString());
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByP_K_N_SV_Last(
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		int count = countByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue);

		if (count == 0) {
			return null;
		}

		List<PortalPreferenceValue> list = findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue[] findByP_K_N_SV_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId, String key,
			String namespace, String smallValue,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws NoSuchPreferenceValueException {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");
		smallValue = Objects.toString(smallValue, "");

		PortalPreferenceValue portalPreferenceValue = findByPrimaryKey(
			portalPreferenceValueId);

		Session session = null;

		try {
			session = openSession();

			PortalPreferenceValue[] array = new PortalPreferenceValueImpl[3];

			array[0] = getByP_K_N_SV_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId, key,
				namespace, smallValue, orderByComparator, true);

			array[1] = portalPreferenceValue;

			array[2] = getByP_K_N_SV_PrevAndNext(
				session, portalPreferenceValue, portalPreferencesId, key,
				namespace, smallValue, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PortalPreferenceValue getByP_K_N_SV_PrevAndNext(
		Session session, PortalPreferenceValue portalPreferenceValue,
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
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

		sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE_WHERE);

		sb.append(_FINDER_COLUMN_P_K_N_SV_PORTALPREFERENCESID_2);

		boolean bindKey = false;

		if (key.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_K_N_SV_KEY_3);
		}
		else {
			bindKey = true;

			sb.append(_FINDER_COLUMN_P_K_N_SV_KEY_2);
		}

		boolean bindNamespace = false;

		if (namespace.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_K_N_SV_NAMESPACE_3);
		}
		else {
			bindNamespace = true;

			sb.append(_FINDER_COLUMN_P_K_N_SV_NAMESPACE_2);
		}

		boolean bindSmallValue = false;

		if (smallValue.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_K_N_SV_SMALLVALUE_3);
		}
		else {
			bindSmallValue = true;

			sb.append(_FINDER_COLUMN_P_K_N_SV_SMALLVALUE_2);
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
			sb.append(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(portalPreferencesId);

		if (bindKey) {
			queryPos.add(key);
		}

		if (bindNamespace) {
			queryPos.add(namespace);
		}

		if (bindSmallValue) {
			queryPos.add(smallValue);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						portalPreferenceValue)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PortalPreferenceValue> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 */
	@Override
	public void removeByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue) {

		for (PortalPreferenceValue portalPreferenceValue :
				findByP_K_N_SV(
					portalPreferencesId, key, namespace, smallValue,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(portalPreferenceValue);
		}
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @return the number of matching portal preference values
	 */
	@Override
	public int countByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue) {

		key = Objects.toString(key, "");
		namespace = Objects.toString(namespace, "");
		smallValue = Objects.toString(smallValue, "");

		FinderPath finderPath = _finderPathCountByP_K_N_SV;

		Object[] finderArgs = new Object[] {
			portalPreferencesId, key, namespace, smallValue
		};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_PORTALPREFERENCEVALUE_WHERE);

			sb.append(_FINDER_COLUMN_P_K_N_SV_PORTALPREFERENCESID_2);

			boolean bindKey = false;

			if (key.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_SV_KEY_3);
			}
			else {
				bindKey = true;

				sb.append(_FINDER_COLUMN_P_K_N_SV_KEY_2);
			}

			boolean bindNamespace = false;

			if (namespace.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_SV_NAMESPACE_3);
			}
			else {
				bindNamespace = true;

				sb.append(_FINDER_COLUMN_P_K_N_SV_NAMESPACE_2);
			}

			boolean bindSmallValue = false;

			if (smallValue.isEmpty()) {
				sb.append(_FINDER_COLUMN_P_K_N_SV_SMALLVALUE_3);
			}
			else {
				bindSmallValue = true;

				sb.append(_FINDER_COLUMN_P_K_N_SV_SMALLVALUE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(portalPreferencesId);

				if (bindKey) {
					queryPos.add(key);
				}

				if (bindNamespace) {
					queryPos.add(namespace);
				}

				if (bindSmallValue) {
					queryPos.add(smallValue);
				}

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_P_K_N_SV_PORTALPREFERENCESID_2 =
		"portalPreferenceValue.portalPreferencesId = ? AND ";

	private static final String _FINDER_COLUMN_P_K_N_SV_KEY_2 =
		"portalPreferenceValue.key = ? AND ";

	private static final String _FINDER_COLUMN_P_K_N_SV_KEY_3 =
		"(portalPreferenceValue.key IS NULL OR portalPreferenceValue.key = '') AND ";

	private static final String _FINDER_COLUMN_P_K_N_SV_NAMESPACE_2 =
		"portalPreferenceValue.namespace = ? AND ";

	private static final String _FINDER_COLUMN_P_K_N_SV_NAMESPACE_3 =
		"(portalPreferenceValue.namespace IS NULL OR portalPreferenceValue.namespace = '') AND ";

	private static final String _FINDER_COLUMN_P_K_N_SV_SMALLVALUE_2 =
		"portalPreferenceValue.smallValue = ?";

	private static final String _FINDER_COLUMN_P_K_N_SV_SMALLVALUE_3 =
		"(portalPreferenceValue.smallValue IS NULL OR portalPreferenceValue.smallValue = '')";

	public PortalPreferenceValuePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("index", "index_");
		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PortalPreferenceValue.class);

		setModelImplClass(PortalPreferenceValueImpl.class);
		setModelPKClass(long.class);

		setTable(PortalPreferenceValueTable.INSTANCE);
	}

	/**
	 * Caches the portal preference value in the entity cache if it is enabled.
	 *
	 * @param portalPreferenceValue the portal preference value
	 */
	@Override
	public void cacheResult(PortalPreferenceValue portalPreferenceValue) {
		dummyEntityCache.putResult(
			PortalPreferenceValueImpl.class,
			portalPreferenceValue.getPrimaryKey(), portalPreferenceValue);

		dummyFinderCache.putResult(
			_finderPathFetchByP_I_K_N,
			new Object[] {
				portalPreferenceValue.getPortalPreferencesId(),
				portalPreferenceValue.getIndex(),
				portalPreferenceValue.getKey(),
				portalPreferenceValue.getNamespace()
			},
			portalPreferenceValue);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the portal preference values in the entity cache if it is enabled.
	 *
	 * @param portalPreferenceValues the portal preference values
	 */
	@Override
	public void cacheResult(
		List<PortalPreferenceValue> portalPreferenceValues) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (portalPreferenceValues.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PortalPreferenceValue portalPreferenceValue :
				portalPreferenceValues) {

			if (dummyEntityCache.getResult(
					PortalPreferenceValueImpl.class,
					portalPreferenceValue.getPrimaryKey()) == null) {

				cacheResult(portalPreferenceValue);
			}
		}
	}

	/**
	 * Clears the cache for all portal preference values.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		dummyEntityCache.clearCache(PortalPreferenceValueImpl.class);

		dummyFinderCache.clearCache(PortalPreferenceValueImpl.class);
	}

	/**
	 * Clears the cache for the portal preference value.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PortalPreferenceValue portalPreferenceValue) {
		dummyEntityCache.removeResult(
			PortalPreferenceValueImpl.class, portalPreferenceValue);
	}

	@Override
	public void clearCache(List<PortalPreferenceValue> portalPreferenceValues) {
		for (PortalPreferenceValue portalPreferenceValue :
				portalPreferenceValues) {

			dummyEntityCache.removeResult(
				PortalPreferenceValueImpl.class, portalPreferenceValue);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		dummyFinderCache.clearCache(PortalPreferenceValueImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			dummyEntityCache.removeResult(
				PortalPreferenceValueImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PortalPreferenceValueModelImpl portalPreferenceValueModelImpl) {

		Object[] args = new Object[] {
			portalPreferenceValueModelImpl.getPortalPreferencesId(),
			portalPreferenceValueModelImpl.getIndex(),
			portalPreferenceValueModelImpl.getKey(),
			portalPreferenceValueModelImpl.getNamespace()
		};

		dummyFinderCache.putResult(
			_finderPathFetchByP_I_K_N, args, portalPreferenceValueModelImpl);
	}

	/**
	 * Creates a new portal preference value with the primary key. Does not add the portal preference value to the database.
	 *
	 * @param portalPreferenceValueId the primary key for the new portal preference value
	 * @return the new portal preference value
	 */
	@Override
	public PortalPreferenceValue create(long portalPreferenceValueId) {
		PortalPreferenceValue portalPreferenceValue =
			new PortalPreferenceValueImpl();

		portalPreferenceValue.setNew(true);
		portalPreferenceValue.setPrimaryKey(portalPreferenceValueId);

		portalPreferenceValue.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portalPreferenceValue;
	}

	/**
	 * Removes the portal preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value that was removed
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue remove(long portalPreferenceValueId)
		throws NoSuchPreferenceValueException {

		return remove((Serializable)portalPreferenceValueId);
	}

	/**
	 * Removes the portal preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the portal preference value
	 * @return the portal preference value that was removed
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue remove(Serializable primaryKey)
		throws NoSuchPreferenceValueException {

		Session session = null;

		try {
			session = openSession();

			PortalPreferenceValue portalPreferenceValue =
				(PortalPreferenceValue)session.get(
					PortalPreferenceValueImpl.class, primaryKey);

			if (portalPreferenceValue == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPreferenceValueException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(portalPreferenceValue);
		}
		catch (NoSuchPreferenceValueException noSuchEntityException) {
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
	protected PortalPreferenceValue removeImpl(
		PortalPreferenceValue portalPreferenceValue) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portalPreferenceValue)) {
				portalPreferenceValue = (PortalPreferenceValue)session.get(
					PortalPreferenceValueImpl.class,
					portalPreferenceValue.getPrimaryKeyObj());
			}

			if (portalPreferenceValue != null) {
				session.delete(portalPreferenceValue);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portalPreferenceValue != null) {
			clearCache(portalPreferenceValue);
		}

		return portalPreferenceValue;
	}

	@Override
	public PortalPreferenceValue updateImpl(
		PortalPreferenceValue portalPreferenceValue) {

		boolean isNew = portalPreferenceValue.isNew();

		if (!(portalPreferenceValue instanceof
				PortalPreferenceValueModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portalPreferenceValue.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					portalPreferenceValue);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portalPreferenceValue proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PortalPreferenceValue implementation " +
					portalPreferenceValue.getClass());
		}

		PortalPreferenceValueModelImpl portalPreferenceValueModelImpl =
			(PortalPreferenceValueModelImpl)portalPreferenceValue;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(portalPreferenceValue);
			}
			else {
				portalPreferenceValue = (PortalPreferenceValue)session.merge(
					portalPreferenceValue);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		dummyEntityCache.putResult(
			PortalPreferenceValueImpl.class, portalPreferenceValueModelImpl,
			false, true);

		cacheUniqueFindersCache(portalPreferenceValueModelImpl);

		if (isNew) {
			portalPreferenceValue.setNew(false);
		}

		portalPreferenceValue.resetOriginalValues();

		return portalPreferenceValue;
	}

	/**
	 * Returns the portal preference value with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the portal preference value
	 * @return the portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPreferenceValueException {

		PortalPreferenceValue portalPreferenceValue = fetchByPrimaryKey(
			primaryKey);

		if (portalPreferenceValue == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPreferenceValueException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return portalPreferenceValue;
	}

	/**
	 * Returns the portal preference value with the primary key or throws a <code>NoSuchPreferenceValueException</code> if it could not be found.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue findByPrimaryKey(long portalPreferenceValueId)
		throws NoSuchPreferenceValueException {

		return findByPrimaryKey((Serializable)portalPreferenceValueId);
	}

	/**
	 * Returns the portal preference value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value, or <code>null</code> if a portal preference value with the primary key could not be found
	 */
	@Override
	public PortalPreferenceValue fetchByPrimaryKey(
		long portalPreferenceValueId) {

		return fetchByPrimaryKey((Serializable)portalPreferenceValueId);
	}

	/**
	 * Returns all the portal preference values.
	 *
	 * @return the portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findAll(
		int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of portal preference values
	 */
	@Override
	public List<PortalPreferenceValue> findAll(
		int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
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

		List<PortalPreferenceValue> list = null;

		if (useFinderCache) {
			list = (List<PortalPreferenceValue>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PORTALPREFERENCEVALUE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PORTALPREFERENCEVALUE;

				sql = sql.concat(PortalPreferenceValueModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PortalPreferenceValue>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Removes all the portal preference values from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PortalPreferenceValue portalPreferenceValue : findAll()) {
			remove(portalPreferenceValue);
		}
	}

	/**
	 * Returns the number of portal preference values.
	 *
	 * @return the number of portal preference values
	 */
	@Override
	public int countAll() {
		Long count = (Long)dummyFinderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_PORTALPREFERENCEVALUE);

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(
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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "portalPreferenceValueId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTALPREFERENCEVALUE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PortalPreferenceValueModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the portal preference value persistence.
	 */
	public void afterPropertiesSet() {
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

		_finderPathWithPaginationFindByPortalPreferencesId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPortalPreferencesId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"portalPreferencesId"}, true);

		_finderPathWithoutPaginationFindByPortalPreferencesId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByPortalPreferencesId", new String[] {Long.class.getName()},
			new String[] {"portalPreferencesId"}, true);

		_finderPathCountByPortalPreferencesId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPortalPreferencesId", new String[] {Long.class.getName()},
			new String[] {"portalPreferencesId"}, false);

		_finderPathWithPaginationCountByPortalPreferencesId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"countByPortalPreferencesId", new String[] {Long.class.getName()},
			new String[] {"portalPreferencesId"}, false);

		_finderPathWithPaginationFindByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"portalPreferencesId", "namespace"}, true);

		_finderPathWithoutPaginationFindByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"portalPreferencesId", "namespace"}, true);

		_finderPathCountByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"portalPreferencesId", "namespace"}, false);

		_finderPathWithPaginationFindByP_K_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_K_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"portalPreferencesId", "key_", "namespace"}, true);

		_finderPathWithoutPaginationFindByP_K_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_K_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"portalPreferencesId", "key_", "namespace"}, true);

		_finderPathCountByP_K_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_K_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"portalPreferencesId", "key_", "namespace"}, false);

		_finderPathFetchByP_I_K_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByP_I_K_N",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"portalPreferencesId", "index_", "key_", "namespace"},
			true);

		_finderPathWithPaginationFindByP_K_N_SV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_K_N_SV",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"portalPreferencesId", "key_", "namespace", "smallValue"
			},
			true);

		_finderPathWithoutPaginationFindByP_K_N_SV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_K_N_SV",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {
				"portalPreferencesId", "key_", "namespace", "smallValue"
			},
			true);

		_finderPathCountByP_K_N_SV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_K_N_SV",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {
				"portalPreferencesId", "key_", "namespace", "smallValue"
			},
			false);

		PortalPreferenceValueUtil.setPersistence(this);
	}

	public void destroy() {
		PortalPreferenceValueUtil.setPersistence(null);

		dummyEntityCache.removeCache(PortalPreferenceValueImpl.class.getName());
	}

	private static final String _SQL_SELECT_PORTALPREFERENCEVALUE =
		"SELECT portalPreferenceValue FROM PortalPreferenceValue portalPreferenceValue";

	private static final String _SQL_SELECT_PORTALPREFERENCEVALUE_WHERE =
		"SELECT portalPreferenceValue FROM PortalPreferenceValue portalPreferenceValue WHERE ";

	private static final String _SQL_COUNT_PORTALPREFERENCEVALUE =
		"SELECT COUNT(portalPreferenceValue) FROM PortalPreferenceValue portalPreferenceValue";

	private static final String _SQL_COUNT_PORTALPREFERENCEVALUE_WHERE =
		"SELECT COUNT(portalPreferenceValue) FROM PortalPreferenceValue portalPreferenceValue WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"portalPreferenceValue.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PortalPreferenceValue exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PortalPreferenceValue exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortalPreferenceValuePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"index", "key"});

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}