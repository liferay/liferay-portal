/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.DuplicateAssetVocabularyExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyTable;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.asset.model.impl.AssetVocabularyImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;

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

/**
 * The persistence implementation for the asset vocabulary service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetVocabularyPersistenceImpl
	extends BasePersistenceImpl<AssetVocabulary>
	implements AssetVocabularyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetVocabularyUtil</code> to access the asset vocabulary persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetVocabularyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the asset vocabularies where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid;
					finderArgs = new Object[] {uuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid;
				finderArgs = new Object[] {uuid, start, end, orderByComparator};
			}

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if (!uuid.equals(assetVocabulary.getUuid())) {
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

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUuid_First(
			String uuid, OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByUuid_First(
			uuid, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUuid_First(
		String uuid, OrderByComparator<AssetVocabulary> orderByComparator) {

		List<AssetVocabulary> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUuid_Last(
			String uuid, OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByUuid_Last(
			uuid, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUuid_Last(
		String uuid, OrderByComparator<AssetVocabulary> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<AssetVocabulary> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set where uuid = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] findByUuid_PrevAndNext(
			long vocabularyId, String uuid,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		uuid = Objects.toString(uuid, "");

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, assetVocabulary, uuid, orderByComparator, true);

			array[1] = assetVocabulary;

			array[2] = getByUuid_PrevAndNext(
				session, assetVocabulary, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetVocabulary getByUuid_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, String uuid,
		OrderByComparator<AssetVocabulary> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
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
			sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset vocabularies where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (AssetVocabulary assetVocabulary :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"assetVocabulary.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(assetVocabulary.uuid IS NULL OR assetVocabulary.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the asset vocabulary where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUUID_G(String uuid, long groupId)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByUUID_G(uuid, groupId);

		if (assetVocabulary == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchVocabularyException(sb.toString());
		}

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the asset vocabulary where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof AssetVocabulary) {
				AssetVocabulary assetVocabulary = (AssetVocabulary)result;

				if (!Objects.equals(uuid, assetVocabulary.getUuid()) ||
					(groupId != assetVocabulary.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(groupId);

					List<AssetVocabulary> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						AssetVocabulary assetVocabulary = list.get(0);

						result = assetVocabulary;

						cacheResult(assetVocabulary);
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
				return (AssetVocabulary)result;
			}
		}
	}

	/**
	 * Removes the asset vocabulary where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary removeByUUID_G(String uuid, long groupId)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByUUID_G(uuid, groupId);

		return remove(assetVocabulary);
	}

	/**
	 * Returns the number of asset vocabularies where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		AssetVocabulary assetVocabulary = fetchByUUID_G(uuid, groupId);

		if (assetVocabulary == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"assetVocabulary.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(assetVocabulary.uuid IS NULL OR assetVocabulary.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"assetVocabulary.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid_C;
					finderArgs = new Object[] {uuid, companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid_C;
				finderArgs = new Object[] {
					uuid, companyId, start, end, orderByComparator
				};
			}

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if (!uuid.equals(assetVocabulary.getUuid()) ||
							(companyId != assetVocabulary.getCompanyId())) {

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

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		List<AssetVocabulary> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<AssetVocabulary> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] findByUuid_C_PrevAndNext(
			long vocabularyId, String uuid, long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		uuid = Objects.toString(uuid, "");

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, assetVocabulary, uuid, companyId, orderByComparator,
				true);

			array[1] = assetVocabulary;

			array[2] = getByUuid_C_PrevAndNext(
				session, assetVocabulary, uuid, companyId, orderByComparator,
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

	protected AssetVocabulary getByUuid_C_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, String uuid,
		long companyId, OrderByComparator<AssetVocabulary> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

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
			sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset vocabularies where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (AssetVocabulary assetVocabulary :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"assetVocabulary.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(assetVocabulary.uuid IS NULL OR assetVocabulary.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"assetVocabulary.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;
	private FinderPath _finderPathWithPaginationCountByGroupId;

	/**
	 * Returns all the asset vocabularies where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByGroupId;
					finderArgs = new Object[] {groupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByGroupId;
				finderArgs = new Object[] {
					groupId, start, end, orderByComparator
				};
			}

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if (groupId != assetVocabulary.getGroupId()) {
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

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByGroupId_First(
			long groupId, OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByGroupId_First(
			groupId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByGroupId_First(
		long groupId, OrderByComparator<AssetVocabulary> orderByComparator) {

		List<AssetVocabulary> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByGroupId_Last(
			long groupId, OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByGroupId_Last(
		long groupId, OrderByComparator<AssetVocabulary> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<AssetVocabulary> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set where groupId = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] findByGroupId_PrevAndNext(
			long vocabularyId, long groupId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, assetVocabulary, groupId, orderByComparator, true);

			array[1] = assetVocabulary;

			array[2] = getByGroupId_PrevAndNext(
				session, assetVocabulary, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetVocabulary getByGroupId_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long groupId,
		OrderByComparator<AssetVocabulary> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset vocabularies that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId(groupId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<AssetVocabulary>)QueryUtil.list(
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
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set of asset vocabularies that the user has permission to view where groupId = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] filterFindByGroupId_PrevAndNext(
			long vocabularyId, long groupId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				vocabularyId, groupId, orderByComparator);
		}

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, assetVocabulary, groupId, orderByComparator, true);

			array[1] = assetVocabulary;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, assetVocabulary, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetVocabulary filterGetByGroupId_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long groupId,
		OrderByComparator<AssetVocabulary> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset vocabularies that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(long[] groupIds) {
		return filterFindByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return filterFindByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByGroupId(groupIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
			}

			return (List<AssetVocabulary>)QueryUtil.list(
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
	 * Returns all the asset vocabularies where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(long[] groupIds) {
		return findByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByGroupId(groupIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {StringUtil.merge(groupIds)};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), start, end, orderByComparator
				};
			}

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByGroupId, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if (!ArrayUtil.contains(
								groupIds, assetVocabulary.getGroupId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByGroupId, finderArgs,
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
	}

	/**
	 * Removes all the asset vocabularies where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (AssetVocabulary assetVocabulary :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	 * Returns the number of asset vocabularies where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			Object[] finderArgs = new Object[] {StringUtil.merge(groupIds)};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByGroupId, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByGroupId, finderArgs,
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
	}

	/**
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetVocabulary> assetVocabularies = findByGroupId(groupId);

			assetVocabularies = InlineSQLHelperUtil.filter(
				assetVocabularies, groupId);

			return assetVocabularies.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_ASSETVOCABULARY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByGroupId(groupIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetVocabulary> assetVocabularies =
				InlineSQLHelperUtil.filter(findByGroupId(groupIds), groupIds);

			return assetVocabularies.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_ASSETVOCABULARY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"assetVocabulary.groupId = ?";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_7 =
		"assetVocabulary.groupId IN (";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the asset vocabularies where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

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

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if (companyId != assetVocabulary.getCompanyId()) {
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

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByCompanyId_First(
			long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByCompanyId_First(
		long companyId, OrderByComparator<AssetVocabulary> orderByComparator) {

		List<AssetVocabulary> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByCompanyId_Last(
			long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByCompanyId_Last(
		long companyId, OrderByComparator<AssetVocabulary> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<AssetVocabulary> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set where companyId = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] findByCompanyId_PrevAndNext(
			long vocabularyId, long companyId,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, assetVocabulary, companyId, orderByComparator, true);

			array[1] = assetVocabulary;

			array[2] = getByCompanyId_PrevAndNext(
				session, assetVocabulary, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetVocabulary getByCompanyId_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long companyId,
		OrderByComparator<AssetVocabulary> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

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
			sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
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
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset vocabularies where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (AssetVocabulary assetVocabulary :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"assetVocabulary.companyId = ?";

	private FinderPath _finderPathFetchByG_N;

	/**
	 * Returns the asset vocabulary where groupId = &#63; and name = &#63; or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_N(long groupId, String name)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByG_N(groupId, name);

		if (assetVocabulary == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchVocabularyException(sb.toString());
		}

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_N(long groupId, String name) {
		return fetchByG_N(groupId, name, true);
	}

	/**
	 * Returns the asset vocabulary where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_N(
		long groupId, String name, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_N, finderArgs, this);
			}

			if (result instanceof AssetVocabulary) {
				AssetVocabulary assetVocabulary = (AssetVocabulary)result;

				if ((groupId != assetVocabulary.getGroupId()) ||
					!Objects.equals(name, assetVocabulary.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_G_N_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					List<AssetVocabulary> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_N, finderArgs, list);
						}
					}
					else {
						AssetVocabulary assetVocabulary = list.get(0);

						result = assetVocabulary;

						cacheResult(assetVocabulary);
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
				return (AssetVocabulary)result;
			}
		}
	}

	/**
	 * Removes the asset vocabulary where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary removeByG_N(long groupId, String name)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByG_N(groupId, name);

		return remove(assetVocabulary);
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		AssetVocabulary assetVocabulary = fetchByG_N(groupId, name);

		if (assetVocabulary == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_N_GROUPID_2 =
		"assetVocabulary.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_NAME_2 =
		"lower(assetVocabulary.name) = ?";

	private static final String _FINDER_COLUMN_G_N_NAME_3 =
		"(assetVocabulary.name IS NULL OR assetVocabulary.name = '')";

	private FinderPath _finderPathWithPaginationFindByG_LikeN;
	private FinderPath _finderPathWithPaginationCountByG_LikeN;

	/**
	 * Returns all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(long groupId, String name) {
		return findByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return findByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByG_LikeN(
			groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LikeN;
			finderArgs = new Object[] {
				groupId, name, start, end, orderByComparator
			};

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if ((groupId != assetVocabulary.getGroupId()) ||
							!StringUtil.wildcardMatches(
								assetVocabulary.getName(), name, '_', '%', '\\',
								false)) {

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

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_NAME_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByG_LikeN_First(
			groupId, name, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		List<AssetVocabulary> list = findByG_LikeN(
			groupId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_LikeN_Last(
			long groupId, String name,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByG_LikeN_Last(
			groupId, name, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_LikeN_Last(
		long groupId, String name,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		int count = countByG_LikeN(groupId, name);

		if (count == 0) {
			return null;
		}

		List<AssetVocabulary> list = findByG_LikeN(
			groupId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] findByG_LikeN_PrevAndNext(
			long vocabularyId, long groupId, String name,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		name = Objects.toString(name, "");

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = getByG_LikeN_PrevAndNext(
				session, assetVocabulary, groupId, name, orderByComparator,
				true);

			array[1] = assetVocabulary;

			array[2] = getByG_LikeN_PrevAndNext(
				session, assetVocabulary, groupId, name, orderByComparator,
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

	protected AssetVocabulary getByG_LikeN_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long groupId,
		String name, OrderByComparator<AssetVocabulary> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_2);
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
			sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(StringUtil.toLowerCase(name));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_LikeN(
		long groupId, String name) {

		return filterFindByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_LikeN(
		long groupId, String name, int start, int end) {

		return filterFindByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permissions to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN(groupId, name, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LikeN(
					groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		name = Objects.toString(name, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(StringUtil.toLowerCase(name));
			}

			return (List<AssetVocabulary>)QueryUtil.list(
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
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set of asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] filterFindByG_LikeN_PrevAndNext(
			long vocabularyId, long groupId, String name,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_PrevAndNext(
				vocabularyId, groupId, name, orderByComparator);
		}

		name = Objects.toString(name, "");

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = filterGetByG_LikeN_PrevAndNext(
				session, assetVocabulary, groupId, name, orderByComparator,
				true);

			array[1] = assetVocabulary;

			array[2] = filterGetByG_LikeN_PrevAndNext(
				session, assetVocabulary, groupId, name, orderByComparator,
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

	protected AssetVocabulary filterGetByG_LikeN_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long groupId,
		String name, OrderByComparator<AssetVocabulary> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(StringUtil.toLowerCase(name));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset vocabularies where groupId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_LikeN(long groupId, String name) {
		for (AssetVocabulary assetVocabulary :
				findByG_LikeN(
					groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_LikeN(long groupId, String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_LikeN;

			Object[] finderArgs = new Object[] {groupId, name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

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
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN(long groupId, String name) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LikeN(groupId, name);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetVocabulary> assetVocabularies = findByG_LikeN(
				groupId, name);

			assetVocabularies = InlineSQLHelperUtil.filter(
				assetVocabularies, groupId);

			return assetVocabularies.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_ASSETVOCABULARY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_NAME_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(StringUtil.toLowerCase(name));
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

	private static final String _FINDER_COLUMN_G_LIKEN_GROUPID_2 =
		"assetVocabulary.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_NAME_2 =
		"lower(assetVocabulary.name) LIKE ?";

	private static final String _FINDER_COLUMN_G_LIKEN_NAME_3 =
		"(assetVocabulary.name IS NULL OR assetVocabulary.name LIKE '')";

	private FinderPath _finderPathWithPaginationFindByG_V;
	private FinderPath _finderPathWithoutPaginationFindByG_V;
	private FinderPath _finderPathCountByG_V;
	private FinderPath _finderPathWithPaginationCountByG_V;

	/**
	 * Returns all the asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(long groupId, int visibilityType) {
		return findByG_V(
			groupId, visibilityType, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long groupId, int visibilityType, int start, int end) {

		return findByG_V(groupId, visibilityType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long groupId, int visibilityType, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByG_V(
			groupId, visibilityType, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long groupId, int visibilityType, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_V;
					finderArgs = new Object[] {groupId, visibilityType};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_V;
				finderArgs = new Object[] {
					groupId, visibilityType, start, end, orderByComparator
				};
			}

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if ((groupId != assetVocabulary.getGroupId()) ||
							(visibilityType !=
								assetVocabulary.getVisibilityType())) {

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

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(visibilityType);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_V_First(
			long groupId, int visibilityType,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByG_V_First(
			groupId, visibilityType, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", visibilityType=");
		sb.append(visibilityType);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the first asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_V_First(
		long groupId, int visibilityType,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		List<AssetVocabulary> list = findByG_V(
			groupId, visibilityType, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByG_V_Last(
			long groupId, int visibilityType,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByG_V_Last(
			groupId, visibilityType, orderByComparator);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", visibilityType=");
		sb.append(visibilityType);

		sb.append("}");

		throw new NoSuchVocabularyException(sb.toString());
	}

	/**
	 * Returns the last asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByG_V_Last(
		long groupId, int visibilityType,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		int count = countByG_V(groupId, visibilityType);

		if (count == 0) {
			return null;
		}

		List<AssetVocabulary> list = findByG_V(
			groupId, visibilityType, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] findByG_V_PrevAndNext(
			long vocabularyId, long groupId, int visibilityType,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = getByG_V_PrevAndNext(
				session, assetVocabulary, groupId, visibilityType,
				orderByComparator, true);

			array[1] = assetVocabulary;

			array[2] = getByG_V_PrevAndNext(
				session, assetVocabulary, groupId, visibilityType,
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

	protected AssetVocabulary getByG_V_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long groupId,
		int visibilityType,
		OrderByComparator<AssetVocabulary> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_2);

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
			sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(visibilityType);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset vocabularies that the user has permission to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @return the matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long groupId, int visibilityType) {

		return filterFindByG_V(
			groupId, visibilityType, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset vocabularies that the user has permission to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long groupId, int visibilityType, int start, int end) {

		return filterFindByG_V(groupId, visibilityType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permissions to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long groupId, int visibilityType, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_V(
				groupId, visibilityType, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_V(
					groupId, visibilityType, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(visibilityType);

			return (List<AssetVocabulary>)QueryUtil.list(
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
	 * Returns the asset vocabularies before and after the current asset vocabulary in the ordered set of asset vocabularies that the user has permission to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param vocabularyId the primary key of the current asset vocabulary
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary[] filterFindByG_V_PrevAndNext(
			long vocabularyId, long groupId, int visibilityType,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws NoSuchVocabularyException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_V_PrevAndNext(
				vocabularyId, groupId, visibilityType, orderByComparator);
		}

		AssetVocabulary assetVocabulary = findByPrimaryKey(vocabularyId);

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary[] array = new AssetVocabularyImpl[3];

			array[0] = filterGetByG_V_PrevAndNext(
				session, assetVocabulary, groupId, visibilityType,
				orderByComparator, true);

			array[1] = assetVocabulary;

			array[2] = filterGetByG_V_PrevAndNext(
				session, assetVocabulary, groupId, visibilityType,
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

	protected AssetVocabulary filterGetByG_V_PrevAndNext(
		Session session, AssetVocabulary assetVocabulary, long groupId,
		int visibilityType,
		OrderByComparator<AssetVocabulary> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(visibilityType);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetVocabulary)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetVocabulary> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset vocabularies that the user has permission to view where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @return the matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long[] groupIds, int[] visibilityTypes) {

		return filterFindByG_V(
			groupIds, visibilityTypes, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset vocabularies that the user has permission to view where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end) {

		return filterFindByG_V(groupIds, visibilityTypes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies that the user has permission to view where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public List<AssetVocabulary> filterFindByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_V(
				groupIds, visibilityTypes, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_V(
					groupIds, visibilityTypes, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (visibilityTypes == null) {
			visibilityTypes = new int[0];
		}
		else if (visibilityTypes.length > 1) {
			visibilityTypes = ArrayUtil.sortedUnique(visibilityTypes);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		if (visibilityTypes.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_7);

			sb.append(StringUtil.merge(visibilityTypes));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2);
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
					AssetVocabularyModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetVocabularyModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetVocabularyImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetVocabularyImpl.class);
			}

			return (List<AssetVocabulary>)QueryUtil.list(
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
	 * Returns all the asset vocabularies where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @return the matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long[] groupIds, int[] visibilityTypes) {

		return findByG_V(
			groupIds, visibilityTypes, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset vocabularies where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end) {

		return findByG_V(groupIds, visibilityTypes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findByG_V(
			groupIds, visibilityTypes, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies where groupId = &#63; and visibilityType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findByG_V(
		long[] groupIds, int[] visibilityTypes, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (visibilityTypes == null) {
			visibilityTypes = new int[0];
		}
		else if (visibilityTypes.length > 1) {
			visibilityTypes = ArrayUtil.sortedUnique(visibilityTypes);
		}

		if ((groupIds.length == 1) && (visibilityTypes.length == 1)) {
			return findByG_V(
				groupIds[0], visibilityTypes[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds),
						StringUtil.merge(visibilityTypes)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds),
					StringUtil.merge(visibilityTypes), start, end,
					orderByComparator
				};
			}

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_V, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetVocabulary assetVocabulary : list) {
						if (!ArrayUtil.contains(
								groupIds, assetVocabulary.getGroupId()) ||
							!ArrayUtil.contains(
								visibilityTypes,
								assetVocabulary.getVisibilityType())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				if (visibilityTypes.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_7);

					sb.append(StringUtil.merge(visibilityTypes));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_V, finderArgs,
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
	}

	/**
	 * Removes all the asset vocabularies where groupId = &#63; and visibilityType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 */
	@Override
	public void removeByG_V(long groupId, int visibilityType) {
		for (AssetVocabulary assetVocabulary :
				findByG_V(
					groupId, visibilityType, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_V(long groupId, int visibilityType) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			FinderPath finderPath = _finderPathCountByG_V;

			Object[] finderArgs = new Object[] {groupId, visibilityType};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(visibilityType);

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
	 * Returns the number of asset vocabularies where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByG_V(long[] groupIds, int[] visibilityTypes) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (visibilityTypes == null) {
			visibilityTypes = new int[0];
		}
		else if (visibilityTypes.length > 1) {
			visibilityTypes = ArrayUtil.sortedUnique(visibilityTypes);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), StringUtil.merge(visibilityTypes)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_V, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_ASSETVOCABULARY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				if (visibilityTypes.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_7);

					sb.append(StringUtil.merge(visibilityTypes));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_V, finderArgs, count);
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
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = &#63; and visibilityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param visibilityType the visibility type
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long groupId, int visibilityType) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_V(groupId, visibilityType);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetVocabulary> assetVocabularies = findByG_V(
				groupId, visibilityType);

			assetVocabularies = InlineSQLHelperUtil.filter(
				assetVocabularies, groupId);

			return assetVocabularies.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_ASSETVOCABULARY_WHERE);

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(visibilityType);

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
	 * Returns the number of asset vocabularies that the user has permission to view where groupId = any &#63; and visibilityType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param visibilityTypes the visibility types
	 * @return the number of matching asset vocabularies that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long[] groupIds, int[] visibilityTypes) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_V(groupIds, visibilityTypes);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetVocabulary> assetVocabularies =
				InlineSQLHelperUtil.filter(
					findByG_V(groupIds, visibilityTypes), groupIds);

			return assetVocabularies.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (visibilityTypes == null) {
			visibilityTypes = new int[0];
		}
		else if (visibilityTypes.length > 1) {
			visibilityTypes = ArrayUtil.sortedUnique(visibilityTypes);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_ASSETVOCABULARY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		if (visibilityTypes.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_VISIBILITYTYPE_7);

			sb.append(StringUtil.merge(visibilityTypes));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetVocabulary.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

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

	private static final String _FINDER_COLUMN_G_V_GROUPID_2 =
		"assetVocabulary.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_V_GROUPID_7 =
		"assetVocabulary.groupId IN (";

	private static final String _FINDER_COLUMN_G_V_VISIBILITYTYPE_2 =
		"assetVocabulary.visibilityType = ?";

	private static final String _FINDER_COLUMN_G_V_VISIBILITYTYPE_7 =
		"assetVocabulary.visibilityType IN (";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset vocabulary
	 * @throws NoSuchVocabularyException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByERC_G(
			externalReferenceCode, groupId);

		if (assetVocabulary == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchVocabularyException(sb.toString());
		}

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof AssetVocabulary) {
				AssetVocabulary assetVocabulary = (AssetVocabulary)result;

				if (!Objects.equals(
						externalReferenceCode,
						assetVocabulary.getExternalReferenceCode()) ||
					(groupId != assetVocabulary.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_ASSETVOCABULARY_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(groupId);

					List<AssetVocabulary> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						AssetVocabulary assetVocabulary = list.get(0);

						result = assetVocabulary;

						cacheResult(assetVocabulary);
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
				return (AssetVocabulary)result;
			}
		}
	}

	/**
	 * Removes the asset vocabulary where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = findByERC_G(
			externalReferenceCode, groupId);

		return remove(assetVocabulary);
	}

	/**
	 * Returns the number of asset vocabularies where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset vocabularies
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		AssetVocabulary assetVocabulary = fetchByERC_G(
			externalReferenceCode, groupId);

		if (assetVocabulary == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"assetVocabulary.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(assetVocabulary.externalReferenceCode IS NULL OR assetVocabulary.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"assetVocabulary.groupId = ?";

	public AssetVocabularyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetVocabulary.class);

		setModelImplClass(AssetVocabularyImpl.class);
		setModelPKClass(long.class);

		setTable(AssetVocabularyTable.INSTANCE);
	}

	/**
	 * Caches the asset vocabulary in the entity cache if it is enabled.
	 *
	 * @param assetVocabulary the asset vocabulary
	 */
	@Override
	public void cacheResult(AssetVocabulary assetVocabulary) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					assetVocabulary.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				AssetVocabularyImpl.class, assetVocabulary.getPrimaryKey(),
				assetVocabulary);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					assetVocabulary.getUuid(), assetVocabulary.getGroupId()
				},
				assetVocabulary);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_N,
				new Object[] {
					assetVocabulary.getGroupId(), assetVocabulary.getName()
				},
				assetVocabulary);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					assetVocabulary.getExternalReferenceCode(),
					assetVocabulary.getGroupId()
				},
				assetVocabulary);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the asset vocabularies in the entity cache if it is enabled.
	 *
	 * @param assetVocabularies the asset vocabularies
	 */
	@Override
	public void cacheResult(List<AssetVocabulary> assetVocabularies) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (assetVocabularies.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						assetVocabulary.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						AssetVocabularyImpl.class,
						assetVocabulary.getPrimaryKey()) == null) {

					cacheResult(assetVocabulary);
				}
			}
		}
	}

	/**
	 * Clears the cache for all asset vocabularies.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(AssetVocabularyImpl.class);

		FinderCacheUtil.clearCache(AssetVocabularyImpl.class);
	}

	/**
	 * Clears the cache for the asset vocabulary.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(AssetVocabulary assetVocabulary) {
		EntityCacheUtil.removeResult(
			AssetVocabularyImpl.class, assetVocabulary);
	}

	@Override
	public void clearCache(List<AssetVocabulary> assetVocabularies) {
		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			EntityCacheUtil.removeResult(
				AssetVocabularyImpl.class, assetVocabulary);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(AssetVocabularyImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(AssetVocabularyImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		AssetVocabularyModelImpl assetVocabularyModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					assetVocabularyModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				assetVocabularyModelImpl.getUuid(),
				assetVocabularyModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args, assetVocabularyModelImpl);

			args = new Object[] {
				assetVocabularyModelImpl.getGroupId(),
				assetVocabularyModelImpl.getName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_N, args, assetVocabularyModelImpl);

			args = new Object[] {
				assetVocabularyModelImpl.getExternalReferenceCode(),
				assetVocabularyModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, assetVocabularyModelImpl);
		}
	}

	/**
	 * Creates a new asset vocabulary with the primary key. Does not add the asset vocabulary to the database.
	 *
	 * @param vocabularyId the primary key for the new asset vocabulary
	 * @return the new asset vocabulary
	 */
	@Override
	public AssetVocabulary create(long vocabularyId) {
		AssetVocabulary assetVocabulary = new AssetVocabularyImpl();

		assetVocabulary.setNew(true);
		assetVocabulary.setPrimaryKey(vocabularyId);

		String uuid = PortalUUIDUtil.generate();

		assetVocabulary.setUuid(uuid);

		assetVocabulary.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetVocabulary;
	}

	/**
	 * Removes the asset vocabulary with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary that was removed
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary remove(long vocabularyId)
		throws NoSuchVocabularyException {

		return remove((Serializable)vocabularyId);
	}

	/**
	 * Removes the asset vocabulary with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the asset vocabulary
	 * @return the asset vocabulary that was removed
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary remove(Serializable primaryKey)
		throws NoSuchVocabularyException {

		Session session = null;

		try {
			session = openSession();

			AssetVocabulary assetVocabulary = (AssetVocabulary)session.get(
				AssetVocabularyImpl.class, primaryKey);

			if (assetVocabulary == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchVocabularyException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(assetVocabulary);
		}
		catch (NoSuchVocabularyException noSuchEntityException) {
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
	protected AssetVocabulary removeImpl(AssetVocabulary assetVocabulary) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetVocabulary)) {
				assetVocabulary = (AssetVocabulary)session.get(
					AssetVocabularyImpl.class,
					assetVocabulary.getPrimaryKeyObj());
			}

			if ((assetVocabulary != null) &&
				CTPersistenceHelperUtil.isRemove(assetVocabulary)) {

				session.delete(assetVocabulary);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetVocabulary != null) {
			clearCache(assetVocabulary);
		}

		return assetVocabulary;
	}

	@Override
	public AssetVocabulary updateImpl(AssetVocabulary assetVocabulary) {
		boolean isNew = assetVocabulary.isNew();

		if (!(assetVocabulary instanceof AssetVocabularyModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetVocabulary.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetVocabulary);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetVocabulary proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetVocabulary implementation " +
					assetVocabulary.getClass());
		}

		AssetVocabularyModelImpl assetVocabularyModelImpl =
			(AssetVocabularyModelImpl)assetVocabulary;

		if (Validator.isNull(assetVocabulary.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetVocabulary.setUuid(uuid);
		}

		if (Validator.isNull(assetVocabulary.getExternalReferenceCode())) {
			assetVocabulary.setExternalReferenceCode(assetVocabulary.getUuid());
		}
		else {
			if (!Objects.equals(
					assetVocabularyModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetVocabulary.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetVocabulary.getCompanyId();

					long groupId = assetVocabulary.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = assetVocabulary.getPrimaryKey();
					}

					try {
						assetVocabulary.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetVocabulary.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetVocabulary.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetVocabulary ercAssetVocabulary = fetchByERC_G(
				assetVocabulary.getExternalReferenceCode(),
				assetVocabulary.getGroupId());

			if (isNew) {
				if (ercAssetVocabulary != null) {
					throw new DuplicateAssetVocabularyExternalReferenceCodeException(
						"Duplicate asset vocabulary with external reference code " +
							assetVocabulary.getExternalReferenceCode() +
								" and group " + assetVocabulary.getGroupId());
				}
			}
			else {
				if ((ercAssetVocabulary != null) &&
					(assetVocabulary.getVocabularyId() !=
						ercAssetVocabulary.getVocabularyId())) {

					throw new DuplicateAssetVocabularyExternalReferenceCodeException(
						"Duplicate asset vocabulary with external reference code " +
							assetVocabulary.getExternalReferenceCode() +
								" and group " + assetVocabulary.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetVocabulary.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetVocabulary.setCreateDate(date);
			}
			else {
				assetVocabulary.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetVocabularyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetVocabulary.setModifiedDate(date);
			}
			else {
				assetVocabulary.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetVocabulary)) {
				if (!isNew) {
					session.evict(
						AssetVocabularyImpl.class,
						assetVocabulary.getPrimaryKeyObj());
				}

				session.save(assetVocabulary);
			}
			else {
				assetVocabulary = (AssetVocabulary)session.merge(
					assetVocabulary);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			AssetVocabularyImpl.class, assetVocabularyModelImpl, false, true);

		cacheUniqueFindersCache(assetVocabularyModelImpl);

		if (isNew) {
			assetVocabulary.setNew(false);
		}

		assetVocabulary.resetOriginalValues();

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the asset vocabulary
	 * @return the asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary findByPrimaryKey(Serializable primaryKey)
		throws NoSuchVocabularyException {

		AssetVocabulary assetVocabulary = fetchByPrimaryKey(primaryKey);

		if (assetVocabulary == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchVocabularyException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary with the primary key or throws a <code>NoSuchVocabularyException</code> if it could not be found.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary
	 * @throws NoSuchVocabularyException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary findByPrimaryKey(long vocabularyId)
		throws NoSuchVocabularyException {

		return findByPrimaryKey((Serializable)vocabularyId);
	}

	/**
	 * Returns the asset vocabulary with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the asset vocabulary
	 * @return the asset vocabulary, or <code>null</code> if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				AssetVocabulary.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		AssetVocabulary assetVocabulary =
			(AssetVocabulary)EntityCacheUtil.getResult(
				AssetVocabularyImpl.class, primaryKey);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		Session session = null;

		try {
			session = openSession();

			assetVocabulary = (AssetVocabulary)session.get(
				AssetVocabularyImpl.class, primaryKey);

			if (assetVocabulary != null) {
				cacheResult(assetVocabulary);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return assetVocabulary;
	}

	/**
	 * Returns the asset vocabulary with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary, or <code>null</code> if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary fetchByPrimaryKey(long vocabularyId) {
		return fetchByPrimaryKey((Serializable)vocabularyId);
	}

	@Override
	public Map<Serializable, AssetVocabulary> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(AssetVocabulary.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, AssetVocabulary> map =
			new HashMap<Serializable, AssetVocabulary>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			AssetVocabulary assetVocabulary = fetchByPrimaryKey(primaryKey);

			if (assetVocabulary != null) {
				map.put(primaryKey, assetVocabulary);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						AssetVocabulary.class, primaryKey)) {

				AssetVocabulary assetVocabulary =
					(AssetVocabulary)EntityCacheUtil.getResult(
						AssetVocabularyImpl.class, primaryKey);

				if (assetVocabulary == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, assetVocabulary);
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

			for (AssetVocabulary assetVocabulary :
					(List<AssetVocabulary>)query.list()) {

				map.put(assetVocabulary.getPrimaryKeyObj(), assetVocabulary);

				cacheResult(assetVocabulary);
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
	 * Returns all the asset vocabularies.
	 *
	 * @return the asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset vocabularies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findAll(
		int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset vocabularies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of asset vocabularies
	 */
	@Override
	public List<AssetVocabulary> findAll(
		int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

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

			List<AssetVocabulary> list = null;

			if (useFinderCache) {
				list = (List<AssetVocabulary>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_ASSETVOCABULARY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_ASSETVOCABULARY;

					sql = sql.concat(AssetVocabularyModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<AssetVocabulary>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Removes all the asset vocabularies from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (AssetVocabulary assetVocabulary : findAll()) {
			remove(assetVocabulary);
		}
	}

	/**
	 * Returns the number of asset vocabularies.
	 *
	 * @return the number of asset vocabularies
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetVocabulary.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_ASSETVOCABULARY);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "vocabularyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETVOCABULARY;
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
		return AssetVocabularyModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetVocabulary";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("visibilityType");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("vocabularyId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the asset vocabulary persistence.
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

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

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

		_finderPathFetchByG_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "name"}, true);

		_finderPathWithPaginationFindByG_LikeN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "name"}, true);

		_finderPathWithPaginationCountByG_LikeN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "name"}, false);

		_finderPathWithPaginationFindByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_V",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "visibilityType"}, true);

		_finderPathWithoutPaginationFindByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_V",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "visibilityType"}, true);

		_finderPathCountByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_V",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "visibilityType"}, false);

		_finderPathWithPaginationCountByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_V",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "visibilityType"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		AssetVocabularyUtil.setPersistence(this);
	}

	public void destroy() {
		AssetVocabularyUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetVocabularyImpl.class.getName());
	}

	private static final String _SQL_SELECT_ASSETVOCABULARY =
		"SELECT assetVocabulary FROM AssetVocabulary assetVocabulary";

	private static final String _SQL_SELECT_ASSETVOCABULARY_WHERE =
		"SELECT assetVocabulary FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String _SQL_COUNT_ASSETVOCABULARY =
		"SELECT COUNT(assetVocabulary) FROM AssetVocabulary assetVocabulary";

	private static final String _SQL_COUNT_ASSETVOCABULARY_WHERE =
		"SELECT COUNT(assetVocabulary) FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"assetVocabulary.vocabularyId";

	private static final String _FILTER_SQL_SELECT_ASSETVOCABULARY_WHERE =
		"SELECT DISTINCT {assetVocabulary.*} FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String
		_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {AssetVocabulary.*} FROM (SELECT DISTINCT assetVocabulary.vocabularyId FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String
		_FILTER_SQL_SELECT_ASSETVOCABULARY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN AssetVocabulary ON TEMP_TABLE.vocabularyId = AssetVocabulary.vocabularyId";

	private static final String _FILTER_SQL_COUNT_ASSETVOCABULARY_WHERE =
		"SELECT COUNT(DISTINCT assetVocabulary.vocabularyId) AS COUNT_VALUE FROM AssetVocabulary assetVocabulary WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "assetVocabulary";

	private static final String _FILTER_ENTITY_TABLE = "AssetVocabulary";

	private static final String _ORDER_BY_ENTITY_ALIAS = "assetVocabulary.";

	private static final String _ORDER_BY_ENTITY_TABLE = "AssetVocabulary.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No AssetVocabulary exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetVocabulary exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularyPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "settings"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}