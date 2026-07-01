/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.ratings.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.ratings.model.impl.RatingsEntryImpl;
import com.liferay.portlet.ratings.model.impl.RatingsEntryModelImpl;
import com.liferay.ratings.kernel.exception.NoSuchEntryException;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.model.RatingsEntryTable;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryPersistence;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the ratings entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RatingsEntryPersistenceImpl
	extends BasePersistenceImpl<RatingsEntry, NoSuchEntryException>
	implements RatingsEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RatingsEntryUtil</code> to access the ratings entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RatingsEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<RatingsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the ratings entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ratings entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry
	 * @throws NoSuchEntryException if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry findByUuid_First(
			String uuid, OrderByComparator<RatingsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first ratings entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry, or <code>null</code> if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry fetchByUuid_First(
		String uuid, OrderByComparator<RatingsEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the ratings entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of ratings entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ratings entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder<RatingsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the ratings entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ratings entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry
	 * @throws NoSuchEntryException if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<RatingsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first ratings entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry, or <code>null</code> if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<RatingsEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the ratings entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of ratings entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ratings entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<RatingsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the ratings entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ratings entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry
	 * @throws NoSuchEntryException if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<RatingsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first ratings entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry, or <code>null</code> if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<RatingsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the ratings entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of ratings entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ratings entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private FinderPath _finderPathWithPaginationFindByU_C_C;
	private FinderPath _finderPathWithoutPaginationFindByU_C_C;
	private FinderPath _finderPathFetchByU_C_C;
	private FinderPath _finderPathCountByU_C_C;
	private FinderPath _finderPathWithPaginationCountByU_C_C;

	/**
	 * Returns all the ratings entries where userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByU_C_C(
		long userId, long classNameId, long[] classPKs) {

		return findByU_C_C(
			userId, classNameId, classPKs, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ratings entries where userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @return the range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByU_C_C(
		long userId, long classNameId, long[] classPKs, int start, int end) {

		return findByU_C_C(userId, classNameId, classPKs, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ratings entries where userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByU_C_C(
		long userId, long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator) {

		return findByU_C_C(
			userId, classNameId, classPKs, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ratings entries where userId = &#63; and classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByU_C_C(
		long userId, long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator,
		boolean useFinderCache) {

		if (classPKs == null) {
			classPKs = new long[0];
		}
		else if (classPKs.length > 1) {
			classPKs = ArrayUtil.sortedUnique(classPKs);
		}

		if (classPKs.length == 1) {
			RatingsEntry ratingsEntry = fetchByU_C_C(
				userId, classNameId, classPKs[0]);

			if (ratingsEntry == null) {
				return Collections.emptyList();
			}
			else {
				return Collections.singletonList(ratingsEntry);
			}
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					RatingsEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						userId, classNameId, StringUtil.merge(classPKs)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					userId, classNameId, StringUtil.merge(classPKs), start, end,
					orderByComparator
				};
			}

			List<RatingsEntry> list = null;

			if (useFinderCache) {
				list = (List<RatingsEntry>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByU_C_C, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (RatingsEntry ratingsEntry : list) {
						if ((userId != ratingsEntry.getUserId()) ||
							(classNameId != ratingsEntry.getClassNameId()) ||
							!ArrayUtil.contains(
								classPKs, ratingsEntry.getClassPK())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(classPKs.length > databaseInMaxParameters)) {

						list = new ArrayList<RatingsEntry>();

						long[][] classPKsPages = (long[][])ArrayUtil.split(
							classPKs, databaseInMaxParameters);

						for (long[] classPKsPage : classPKsPages) {
							list.addAll(
								_findByU_C_C(
									userId, classNameId, classPKsPage, start,
									end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByU_C_C(
							userId, classNameId, classPKs, start, end,
							orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByU_C_C, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<RatingsEntry> _findByU_C_C(
		long userId, long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator) {

		List<RatingsEntry> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_RATINGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_U_C_C_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_C_CLASSNAMEID_2);

		if (classPKs.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_U_C_C_CLASSPK_7);

			sb.append(StringUtil.merge(classPKs));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(RatingsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(userId);

			queryPos.add(classNameId);

			list = (List<RatingsEntry>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Returns the ratings entry where userId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching ratings entry
	 * @throws NoSuchEntryException if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry findByU_C_C(long userId, long classNameId, long classPK)
		throws NoSuchEntryException {

		RatingsEntry ratingsEntry = fetchByU_C_C(userId, classNameId, classPK);

		if (ratingsEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("userId=");
			sb.append(userId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryException(sb.toString());
		}

		return ratingsEntry;
	}

	/**
	 * Returns the ratings entry where userId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ratings entry, or <code>null</code> if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry fetchByU_C_C(
		long userId, long classNameId, long classPK, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					RatingsEntry.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {userId, classNameId, classPK};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByU_C_C, finderArgs, this);
			}

			if (result instanceof RatingsEntry) {
				RatingsEntry ratingsEntry = (RatingsEntry)result;

				if ((userId != ratingsEntry.getUserId()) ||
					(classNameId != ratingsEntry.getClassNameId()) ||
					(classPK != ratingsEntry.getClassPK())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_RATINGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					List<RatingsEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByU_C_C, finderArgs, list);
						}
					}
					else {
						RatingsEntry ratingsEntry = list.get(0);

						result = ratingsEntry;

						cacheResult(ratingsEntry);
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
				return (RatingsEntry)result;
			}
		}
	}

	/**
	 * Removes the ratings entry where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the ratings entry that was removed
	 */
	@Override
	public RatingsEntry removeByU_C_C(
			long userId, long classNameId, long classPK)
		throws NoSuchEntryException {

		RatingsEntry ratingsEntry = findByU_C_C(userId, classNameId, classPK);

		return remove(ratingsEntry);
	}

	/**
	 * Returns the number of ratings entries where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ratings entries
	 */
	@Override
	public int countByU_C_C(long userId, long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					RatingsEntry.class)) {

			FinderPath finderPath = _finderPathCountByU_C_C;

			Object[] finderArgs = new Object[] {userId, classNameId, classPK};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_RATINGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	/**
	 * Returns the number of ratings entries where userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching ratings entries
	 */
	@Override
	public int countByU_C_C(long userId, long classNameId, long[] classPKs) {
		if (classPKs == null) {
			classPKs = new long[0];
		}
		else if (classPKs.length > 1) {
			classPKs = ArrayUtil.sortedUnique(classPKs);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					RatingsEntry.class)) {

			Object[] finderArgs = new Object[] {
				userId, classNameId, StringUtil.merge(classPKs)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByU_C_C, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(classPKs.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] classPKsPages = (long[][])ArrayUtil.split(
							classPKs, databaseInMaxParameters);

						for (long[] classPKsPage : classPKsPages) {
							count += Long.valueOf(
								_countByU_C_C(
									userId, classNameId, classPKsPage));
						}
					}
					else {
						count = Long.valueOf(
							_countByU_C_C(userId, classNameId, classPKs));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByU_C_C, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByU_C_C(long userId, long classNameId, long[] classPKs) {
		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_RATINGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_U_C_C_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_C_CLASSNAMEID_2);

		if (classPKs.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_U_C_C_CLASSPK_7);

			sb.append(StringUtil.merge(classPKs));

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

			queryPos.add(userId);

			queryPos.add(classNameId);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_U_C_C_USERID_2 =
		"ratingsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_CLASSNAMEID_2 =
		"ratingsEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_CLASSPK_2 =
		"ratingsEntry.classPK = ?";

	private static final String _FINDER_COLUMN_U_C_C_CLASSPK_7 =
		"ratingsEntry.classPK IN (";

	private CollectionPersistenceFinder<RatingsEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_C_S;

	/**
	 * Returns an ordered range of all the ratings entries where classNameId = &#63; and classPK = &#63; and score = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param score the score
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ratings entries
	 */
	@Override
	public List<RatingsEntry> findByC_C_S(
		long classNameId, long classPK, double score, int start, int end,
		OrderByComparator<RatingsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, score}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ratings entry in the ordered set where classNameId = &#63; and classPK = &#63; and score = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param score the score
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry
	 * @throws NoSuchEntryException if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry findByC_C_S_First(
			long classNameId, long classPK, double score,
			OrderByComparator<RatingsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_C_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, score}, orderByComparator);
	}

	/**
	 * Returns the first ratings entry in the ordered set where classNameId = &#63; and classPK = &#63; and score = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param score the score
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ratings entry, or <code>null</code> if a matching ratings entry could not be found
	 */
	@Override
	public RatingsEntry fetchByC_C_S_First(
		long classNameId, long classPK, double score,
		OrderByComparator<RatingsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, score}, orderByComparator);
	}

	/**
	 * Removes all the ratings entries where classNameId = &#63; and classPK = &#63; and score = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param score the score
	 */
	@Override
	public void removeByC_C_S(long classNameId, long classPK, double score) {
		_collectionPersistenceFinderByC_C_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, score});
	}

	/**
	 * Returns the number of ratings entries where classNameId = &#63; and classPK = &#63; and score = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param score the score
	 * @return the number of matching ratings entries
	 */
	@Override
	public int countByC_C_S(long classNameId, long classPK, double score) {
		return _collectionPersistenceFinderByC_C_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, score});
	}

	public RatingsEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(RatingsEntry.class);

		setModelImplClass(RatingsEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RatingsEntryTable.INSTANCE);
	}

	/**
	 * Creates a new ratings entry with the primary key. Does not add the ratings entry to the database.
	 *
	 * @param entryId the primary key for the new ratings entry
	 * @return the new ratings entry
	 */
	@Override
	public RatingsEntry create(long entryId) {
		RatingsEntry ratingsEntry = new RatingsEntryImpl();

		ratingsEntry.setNew(true);
		ratingsEntry.setPrimaryKey(entryId);

		String uuid = PortalUUIDUtil.generate();

		ratingsEntry.setUuid(uuid);

		ratingsEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ratingsEntry;
	}

	/**
	 * Removes the ratings entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the ratings entry
	 * @return the ratings entry that was removed
	 * @throws NoSuchEntryException if a ratings entry with the primary key could not be found
	 */
	@Override
	public RatingsEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	@Override
	protected RatingsEntry removeImpl(RatingsEntry ratingsEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ratingsEntry)) {
				ratingsEntry = (RatingsEntry)session.get(
					RatingsEntryImpl.class, ratingsEntry.getPrimaryKeyObj());
			}

			if ((ratingsEntry != null) &&
				CTPersistenceHelperUtil.isRemove(ratingsEntry)) {

				session.delete(ratingsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ratingsEntry != null) {
			clearCache(ratingsEntry);
		}

		return ratingsEntry;
	}

	@Override
	public RatingsEntry updateImpl(RatingsEntry ratingsEntry) {
		boolean isNew = ratingsEntry.isNew();

		if (!(ratingsEntry instanceof RatingsEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ratingsEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ratingsEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ratingsEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RatingsEntry implementation " +
					ratingsEntry.getClass());
		}

		RatingsEntryModelImpl ratingsEntryModelImpl =
			(RatingsEntryModelImpl)ratingsEntry;

		if (Validator.isNull(ratingsEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ratingsEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ratingsEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				ratingsEntry.setCreateDate(date);
			}
			else {
				ratingsEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ratingsEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ratingsEntry.setModifiedDate(date);
			}
			else {
				ratingsEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(ratingsEntry)) {
				if (!isNew) {
					session.evict(
						RatingsEntryImpl.class,
						ratingsEntry.getPrimaryKeyObj());
				}

				session.save(ratingsEntry);
			}
			else {
				ratingsEntry = (RatingsEntry)session.merge(ratingsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ratingsEntry, false);

		if (isNew) {
			ratingsEntry.setNew(false);
		}

		ratingsEntry.resetOriginalValues();

		return ratingsEntry;
	}

	/**
	 * Returns the ratings entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the ratings entry
	 * @return the ratings entry
	 * @throws NoSuchEntryException if a ratings entry with the primary key could not be found
	 */
	@Override
	public RatingsEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the ratings entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the ratings entry
	 * @return the ratings entry, or <code>null</code> if a ratings entry with the primary key could not be found
	 */
	@Override
	public RatingsEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RATINGSENTRY;
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
		return RatingsEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "RatingsEntry";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("score");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("entryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"userId", "classNameId", "classPK"});
	}

	/**
	 * Initializes the ratings entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_RATINGSENTRY_WHERE, _SQL_COUNT_RATINGSENTRY_WHERE,
			RatingsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ratingsEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, RatingsEntry::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_RATINGSENTRY_WHERE, _SQL_COUNT_RATINGSENTRY_WHERE,
				RatingsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ratingsEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, RatingsEntry::getUuid),
				new FinderColumn<>(
					"ratingsEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, RatingsEntry::getCompanyId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_RATINGSENTRY_WHERE, _SQL_COUNT_RATINGSENTRY_WHERE,
			RatingsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ratingsEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, RatingsEntry::getClassNameId),
			new FinderColumn<>(
				"ratingsEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, RatingsEntry::getClassPK));

		_finderPathWithPaginationFindByU_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByU_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK"}, true);

		_finderPathFetchByU_C_C = createUniqueFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByU_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK"}, 0, 0, false,
			RatingsEntry::getUserId, RatingsEntry::getClassNameId,
			RatingsEntry::getClassPK);

		_finderPathCountByU_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK"}, false);

		_finderPathWithPaginationCountByU_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK"}, false);

		_collectionPersistenceFinderByC_C_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Double.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "score"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Double.class.getName()
				},
				new String[] {"classNameId", "classPK", "score"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Double.class.getName()
				},
				new String[] {"classNameId", "classPK", "score"}, false),
			_SQL_SELECT_RATINGSENTRY_WHERE, _SQL_COUNT_RATINGSENTRY_WHERE,
			RatingsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ratingsEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, RatingsEntry::getClassNameId),
			new FinderColumn<>(
				"ratingsEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, RatingsEntry::getClassPK),
			new FinderColumn<>(
				"ratingsEntry.", "score", FinderColumn.Type.DOUBLE, "=", true,
				true, RatingsEntry::getScore));

		RatingsEntryUtil.setPersistence(this);
	}

	public void destroy() {
		RatingsEntryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RatingsEntryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RatingsEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_RATINGSENTRY =
		"SELECT ratingsEntry FROM RatingsEntry ratingsEntry";

	private static final String _SQL_SELECT_RATINGSENTRY_WHERE =
		"SELECT ratingsEntry FROM RatingsEntry ratingsEntry WHERE ";

	private static final String _SQL_COUNT_RATINGSENTRY_WHERE =
		"SELECT COUNT(ratingsEntry) FROM RatingsEntry ratingsEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RatingsEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RatingsEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-205748719