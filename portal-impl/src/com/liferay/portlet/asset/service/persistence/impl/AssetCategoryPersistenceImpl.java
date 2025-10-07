/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.exception.DuplicateAssetCategoryExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryTable;
import com.liferay.asset.kernel.service.persistence.AssetCategoryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetCategoryUtil;
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
import com.liferay.portlet.asset.model.impl.AssetCategoryImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;

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
 * The persistence implementation for the asset category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AssetCategoryPersistenceImpl
	extends BasePersistenceImpl<AssetCategory>
	implements AssetCategoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetCategoryUtil</code> to access the asset category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetCategoryImpl.class.getName();

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
	 * Returns all the asset categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

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

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (!uuid.equals(assetCategory.getUuid())) {
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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUuid_First(
			String uuid, OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByUuid_First(
			uuid, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUuid_First(
		String uuid, OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUuid_Last(
			String uuid, OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByUuid_Last(uuid, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUuid_Last(
		String uuid, OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where uuid = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByUuid_PrevAndNext(
			long categoryId, String uuid,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		uuid = Objects.toString(uuid, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, assetCategory, uuid, orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByUuid_PrevAndNext(
				session, assetCategory, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetCategory getByUuid_PrevAndNext(
		Session session, AssetCategory assetCategory, String uuid,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (AssetCategory assetCategory :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

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
		"assetCategory.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(assetCategory.uuid IS NULL OR assetCategory.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByUUID_G(uuid, groupId);

		if (assetCategory == null) {
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

			throw new NoSuchCategoryException(sb.toString());
		}

		return assetCategory;
	}

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

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

			if (result instanceof AssetCategory) {
				AssetCategory assetCategory = (AssetCategory)result;

				if (!Objects.equals(uuid, assetCategory.getUuid()) ||
					(groupId != assetCategory.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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

					List<AssetCategory> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						AssetCategory assetCategory = list.get(0);

						result = assetCategory;

						cacheResult(assetCategory);
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
				return (AssetCategory)result;
			}
		}
	}

	/**
	 * Removes the asset category where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset category that was removed
	 */
	@Override
	public AssetCategory removeByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByUUID_G(uuid, groupId);

		return remove(assetCategory);
	}

	/**
	 * Returns the number of asset categories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		AssetCategory assetCategory = fetchByUUID_G(uuid, groupId);

		if (assetCategory == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"assetCategory.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(assetCategory.uuid IS NULL OR assetCategory.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"assetCategory.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

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

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (!uuid.equals(assetCategory.getUuid()) ||
							(companyId != assetCategory.getCompanyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByUuid_C_PrevAndNext(
			long categoryId, String uuid, long companyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		uuid = Objects.toString(uuid, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, assetCategory, uuid, companyId, orderByComparator,
				true);

			array[1] = assetCategory;

			array[2] = getByUuid_C_PrevAndNext(
				session, assetCategory, uuid, companyId, orderByComparator,
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

	protected AssetCategory getByUuid_C_PrevAndNext(
		Session session, AssetCategory assetCategory, String uuid,
		long companyId, OrderByComparator<AssetCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (AssetCategory assetCategory :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

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
		"assetCategory.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(assetCategory.uuid IS NULL OR assetCategory.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"assetCategory.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the asset categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

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

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (groupId != assetCategory.getGroupId()) {
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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByGroupId_First(
			long groupId, OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByGroupId_First(
			groupId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByGroupId_First(
		long groupId, OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByGroupId_Last(
			long groupId, OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByGroupId_Last(
		long groupId, OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where groupId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByGroupId_PrevAndNext(
			long categoryId, long groupId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, assetCategory, groupId, orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByGroupId_PrevAndNext(
				session, assetCategory, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetCategory getByGroupId_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the asset categories before and after the current asset category in the ordered set of asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] filterFindByGroupId_PrevAndNext(
			long categoryId, long groupId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				categoryId, groupId, orderByComparator);
		}

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, assetCategory, groupId, orderByComparator, true);

			array[1] = assetCategory;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, assetCategory, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetCategory filterGetByGroupId_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (AssetCategory assetCategory :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

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
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = findByGroupId(groupId);

			assetCategories = InlineSQLHelperUtil.filter(
				assetCategories, groupId);

			return assetCategories.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"assetCategory.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByParentCategoryId;
	private FinderPath _finderPathWithoutPaginationFindByParentCategoryId;
	private FinderPath _finderPathCountByParentCategoryId;

	/**
	 * Returns all the asset categories where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByParentCategoryId(long parentCategoryId) {
		return findByParentCategoryId(
			parentCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end) {

		return findByParentCategoryId(parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByParentCategoryId(
			parentCategoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByParentCategoryId;
					finderArgs = new Object[] {parentCategoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByParentCategoryId;
				finderArgs = new Object[] {
					parentCategoryId, start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (parentCategoryId !=
								assetCategory.getParentCategoryId()) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_PARENTCATEGORYID_PARENTCATEGORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByParentCategoryId_First(
			long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByParentCategoryId_First(
			parentCategoryId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByParentCategoryId_First(
		long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByParentCategoryId(
			parentCategoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByParentCategoryId_Last(
			long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByParentCategoryId_Last(
			parentCategoryId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByParentCategoryId_Last(
		long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByParentCategoryId(parentCategoryId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByParentCategoryId(
			parentCategoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByParentCategoryId_PrevAndNext(
			long categoryId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByParentCategoryId_PrevAndNext(
				session, assetCategory, parentCategoryId, orderByComparator,
				true);

			array[1] = assetCategory;

			array[2] = getByParentCategoryId_PrevAndNext(
				session, assetCategory, parentCategoryId, orderByComparator,
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

	protected AssetCategory getByParentCategoryId_PrevAndNext(
		Session session, AssetCategory assetCategory, long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_PARENTCATEGORYID_PARENTCATEGORYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentCategoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByParentCategoryId(long parentCategoryId) {
		for (AssetCategory assetCategory :
				findByParentCategoryId(
					parentCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByParentCategoryId(long parentCategoryId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByParentCategoryId;

			Object[] finderArgs = new Object[] {parentCategoryId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_PARENTCATEGORYID_PARENTCATEGORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

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

	private static final String
		_FINDER_COLUMN_PARENTCATEGORYID_PARENTCATEGORYID_2 =
			"assetCategory.parentCategoryId = ?";

	private FinderPath _finderPathWithPaginationFindByVocabularyId;
	private FinderPath _finderPathWithoutPaginationFindByVocabularyId;
	private FinderPath _finderPathCountByVocabularyId;

	/**
	 * Returns all the asset categories where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByVocabularyId(long vocabularyId) {
		return findByVocabularyId(
			vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end) {

		return findByVocabularyId(vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByVocabularyId(
			vocabularyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByVocabularyId;
					finderArgs = new Object[] {vocabularyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByVocabularyId;
				finderArgs = new Object[] {
					vocabularyId, start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (vocabularyId != assetCategory.getVocabularyId()) {
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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_VOCABULARYID_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByVocabularyId_First(
			long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByVocabularyId_First(
			vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByVocabularyId_First(
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByVocabularyId(
			vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByVocabularyId_Last(
			long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByVocabularyId_Last(
			vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByVocabularyId_Last(
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByVocabularyId(vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByVocabularyId(
			vocabularyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByVocabularyId_PrevAndNext(
			long categoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByVocabularyId_PrevAndNext(
				session, assetCategory, vocabularyId, orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByVocabularyId_PrevAndNext(
				session, assetCategory, vocabularyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AssetCategory getByVocabularyId_PrevAndNext(
		Session session, AssetCategory assetCategory, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_VOCABULARYID_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where vocabularyId = &#63; from the database.
	 *
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByVocabularyId(long vocabularyId) {
		for (AssetCategory assetCategory :
				findByVocabularyId(
					vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByVocabularyId(long vocabularyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByVocabularyId;

			Object[] finderArgs = new Object[] {vocabularyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_VOCABULARYID_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(vocabularyId);

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

	private static final String _FINDER_COLUMN_VOCABULARYID_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P(long groupId, long parentCategoryId) {
		return findByG_P(
			groupId, parentCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return findByG_P(groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_P(
			groupId, parentCategoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, parentCategoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, parentCategoryId, start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((groupId != assetCategory.getGroupId()) ||
							(parentCategoryId !=
								assetCategory.getParentCategoryId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_P_First(
			long groupId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_P_First(
			groupId, parentCategoryId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_P_First(
		long groupId, long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByG_P(
			groupId, parentCategoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_P_Last(
			long groupId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_P_Last(
			groupId, parentCategoryId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_P_Last(
		long groupId, long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByG_P(groupId, parentCategoryId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByG_P(
			groupId, parentCategoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByG_P_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByG_P_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId,
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

	protected AssetCategory getByG_P_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId) {

		return filterFindByG_P(
			groupId, parentCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return filterFindByG_P(groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentCategoryId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentCategoryId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the asset categories before and after the current asset category in the ordered set of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] filterFindByG_P_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				categoryId, groupId, parentCategoryId, orderByComparator);
		}

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = filterGetByG_P_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId,
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

	protected AssetCategory filterGetByG_P_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentCategoryId) {
		for (AssetCategory assetCategory :
				findByG_P(
					groupId, parentCategoryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_P(long groupId, long parentCategoryId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentCategoryId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

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
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentCategoryId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentCategoryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = findByG_P(
				groupId, parentCategoryId);

			assetCategories = InlineSQLHelperUtil.filter(
				assetCategories, groupId);

			return assetCategories.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

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

	private static final String _FINDER_COLUMN_G_P_GROUPID_2 =
		"assetCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTCATEGORYID_2 =
		"assetCategory.parentCategoryId = ?";

	private FinderPath _finderPathWithPaginationFindByG_V;
	private FinderPath _finderPathWithoutPaginationFindByG_V;
	private FinderPath _finderPathCountByG_V;
	private FinderPath _finderPathWithPaginationCountByG_V;

	/**
	 * Returns all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(long groupId, long vocabularyId) {
		return findByG_V(
			groupId, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end) {

		return findByG_V(groupId, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_V(
			groupId, vocabularyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_V;
					finderArgs = new Object[] {groupId, vocabularyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_V;
				finderArgs = new Object[] {
					groupId, vocabularyId, start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((groupId != assetCategory.getGroupId()) ||
							(vocabularyId != assetCategory.getVocabularyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_V_First(
			long groupId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_V_First(
			groupId, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_V_First(
		long groupId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByG_V(
			groupId, vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_V_Last(
			long groupId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_V_Last(
			groupId, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_V_Last(
		long groupId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByG_V(groupId, vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByG_V(
			groupId, vocabularyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByG_V_PrevAndNext(
			long categoryId, long groupId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByG_V_PrevAndNext(
				session, assetCategory, groupId, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByG_V_PrevAndNext(
				session, assetCategory, groupId, vocabularyId,
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

	protected AssetCategory getByG_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId) {

		return filterFindByG_V(
			groupId, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId, int start, int end) {

		return filterFindByG_V(groupId, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_V(
				groupId, vocabularyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_V(
					groupId, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(vocabularyId);

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the asset categories before and after the current asset category in the ordered set of asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] filterFindByG_V_PrevAndNext(
			long categoryId, long groupId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_V_PrevAndNext(
				categoryId, groupId, vocabularyId, orderByComparator);
		}

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = filterGetByG_V_PrevAndNext(
				session, assetCategory, groupId, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = filterGetByG_V_PrevAndNext(
				session, assetCategory, groupId, vocabularyId,
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

	protected AssetCategory filterGetByG_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds) {

		return filterFindByG_V(
			groupIds, vocabularyIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end) {

		return filterFindByG_V(groupIds, vocabularyIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_V(
				groupIds, vocabularyIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_V(
					groupIds, vocabularyIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		if (vocabularyIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_7);

			sb.append(StringUtil.merge(vocabularyIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns all the asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds) {

		return findByG_V(
			groupIds, vocabularyIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end) {

		return findByG_V(groupIds, vocabularyIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_V(
			groupIds, vocabularyIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		if ((groupIds.length == 1) && (vocabularyIds.length == 1)) {
			return findByG_V(
				groupIds[0], vocabularyIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds),
						StringUtil.merge(vocabularyIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), StringUtil.merge(vocabularyIds),
					start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_V, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (!ArrayUtil.contains(
								groupIds, assetCategory.getGroupId()) ||
							!ArrayUtil.contains(
								vocabularyIds,
								assetCategory.getVocabularyId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				if (vocabularyIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_7);

					sb.append(StringUtil.merge(vocabularyIds));

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
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Removes all the asset categories where groupId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_V(long groupId, long vocabularyId) {
		for (AssetCategory assetCategory :
				findByG_V(
					groupId, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_V(long groupId, long vocabularyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByG_V;

			Object[] finderArgs = new Object[] {groupId, vocabularyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(vocabularyId);

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
	 * Returns the number of asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_V(long[] groupIds, long[] vocabularyIds) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), StringUtil.merge(vocabularyIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_V, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				if (vocabularyIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_7);

					sb.append(StringUtil.merge(vocabularyIds));

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
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long groupId, long vocabularyId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_V(groupId, vocabularyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = findByG_V(
				groupId, vocabularyId);

			assetCategories = InlineSQLHelperUtil.filter(
				assetCategories, groupId);

			return assetCategories.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(vocabularyId);

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
	 * Returns the number of asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_V(long[] groupIds, long[] vocabularyIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_V(groupIds, vocabularyIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = InlineSQLHelperUtil.filter(
				findByG_V(groupIds, vocabularyIds), groupIds);

			return assetCategories.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		if (vocabularyIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_V_VOCABULARYID_7);

			sb.append(StringUtil.merge(vocabularyIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
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
		"assetCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_V_GROUPID_7 =
		"assetCategory.groupId IN (";

	private static final String _FINDER_COLUMN_G_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private static final String _FINDER_COLUMN_G_V_VOCABULARYID_7 =
		"assetCategory.vocabularyId IN (";

	private FinderPath _finderPathWithPaginationFindByP_N;
	private FinderPath _finderPathWithoutPaginationFindByP_N;
	private FinderPath _finderPathCountByP_N;

	/**
	 * Returns all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_N(long parentCategoryId, String name) {
		return findByP_N(
			parentCategoryId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end) {

		return findByP_N(parentCategoryId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByP_N(
			parentCategoryId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByP_N;
					finderArgs = new Object[] {parentCategoryId, name};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByP_N;
				finderArgs = new Object[] {
					parentCategoryId, name, start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((parentCategoryId !=
								assetCategory.getParentCategoryId()) ||
							!name.equals(assetCategory.getName())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_P_N_PARENTCATEGORYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_P_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_P_N_NAME_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

					if (bindName) {
						queryPos.add(name);
					}

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_N_First(
			long parentCategoryId, String name,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByP_N_First(
			parentCategoryId, name, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_N_First(
		long parentCategoryId, String name,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByP_N(
			parentCategoryId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_N_Last(
			long parentCategoryId, String name,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByP_N_Last(
			parentCategoryId, name, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_N_Last(
		long parentCategoryId, String name,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByP_N(parentCategoryId, name);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByP_N(
			parentCategoryId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByP_N_PrevAndNext(
			long categoryId, long parentCategoryId, String name,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		name = Objects.toString(name, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByP_N_PrevAndNext(
				session, assetCategory, parentCategoryId, name,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByP_N_PrevAndNext(
				session, assetCategory, parentCategoryId, name,
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

	protected AssetCategory getByP_N_PrevAndNext(
		Session session, AssetCategory assetCategory, long parentCategoryId,
		String name, OrderByComparator<AssetCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_P_N_PARENTCATEGORYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_P_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_P_N_NAME_2);
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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentCategoryId);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; and name = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 */
	@Override
	public void removeByP_N(long parentCategoryId, String name) {
		for (AssetCategory assetCategory :
				findByP_N(
					parentCategoryId, name, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByP_N(long parentCategoryId, String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByP_N;

			Object[] finderArgs = new Object[] {parentCategoryId, name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_P_N_PARENTCATEGORYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_P_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_P_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

					if (bindName) {
						queryPos.add(name);
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

	private static final String _FINDER_COLUMN_P_N_PARENTCATEGORYID_2 =
		"assetCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_P_N_NAME_2 =
		"assetCategory.name = ?";

	private static final String _FINDER_COLUMN_P_N_NAME_3 =
		"(assetCategory.name IS NULL OR assetCategory.name = '')";

	private FinderPath _finderPathWithPaginationFindByP_V;
	private FinderPath _finderPathWithoutPaginationFindByP_V;
	private FinderPath _finderPathCountByP_V;

	/**
	 * Returns all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId) {

		return findByP_V(
			parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end) {

		return findByP_V(parentCategoryId, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByP_V(
			parentCategoryId, vocabularyId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByP_V;
					finderArgs = new Object[] {parentCategoryId, vocabularyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByP_V;
				finderArgs = new Object[] {
					parentCategoryId, vocabularyId, start, end,
					orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((parentCategoryId !=
								assetCategory.getParentCategoryId()) ||
							(vocabularyId != assetCategory.getVocabularyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_P_V_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_P_V_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_V_First(
			long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByP_V_First(
			parentCategoryId, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_V_First(
		long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByP_V(
			parentCategoryId, vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_V_Last(
			long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByP_V_Last(
			parentCategoryId, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_V_Last(
		long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByP_V(parentCategoryId, vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByP_V(
			parentCategoryId, vocabularyId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByP_V_PrevAndNext(
			long categoryId, long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByP_V_PrevAndNext(
				session, assetCategory, parentCategoryId, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByP_V_PrevAndNext(
				session, assetCategory, parentCategoryId, vocabularyId,
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

	protected AssetCategory getByP_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long parentCategoryId,
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_P_V_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_P_V_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentCategoryId);

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByP_V(long parentCategoryId, long vocabularyId) {
		for (AssetCategory assetCategory :
				findByP_V(
					parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByP_V(long parentCategoryId, long vocabularyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByP_V;

			Object[] finderArgs = new Object[] {parentCategoryId, vocabularyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_P_V_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_P_V_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

					queryPos.add(vocabularyId);

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

	private static final String _FINDER_COLUMN_P_V_PARENTCATEGORYID_2 =
		"assetCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_P_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private FinderPath _finderPathWithPaginationFindByN_V;
	private FinderPath _finderPathWithoutPaginationFindByN_V;
	private FinderPath _finderPathCountByN_V;

	/**
	 * Returns all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByN_V(String name, long vocabularyId) {
		return findByN_V(
			name, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end) {

		return findByN_V(name, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByN_V(
			name, vocabularyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_V;
					finderArgs = new Object[] {name, vocabularyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_V;
				finderArgs = new Object[] {
					name, vocabularyId, start, end, orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (!name.equals(assetCategory.getName()) ||
							(vocabularyId != assetCategory.getVocabularyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_N_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_N_V_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByN_V_First(
			String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByN_V_First(
			name, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByN_V_First(
		String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByN_V(
			name, vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByN_V_Last(
			String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByN_V_Last(
			name, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByN_V_Last(
		String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByN_V(name, vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByN_V(
			name, vocabularyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByN_V_PrevAndNext(
			long categoryId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		name = Objects.toString(name, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByN_V_PrevAndNext(
				session, assetCategory, name, vocabularyId, orderByComparator,
				true);

			array[1] = assetCategory;

			array[2] = getByN_V_PrevAndNext(
				session, assetCategory, name, vocabularyId, orderByComparator,
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

	protected AssetCategory getByN_V_PrevAndNext(
		Session session, AssetCategory assetCategory, String name,
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_N_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_N_V_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where name = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByN_V(String name, long vocabularyId) {
		for (AssetCategory assetCategory :
				findByN_V(
					name, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByN_V(String name, long vocabularyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByN_V;

			Object[] finderArgs = new Object[] {name, vocabularyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_N_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_N_V_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindName) {
						queryPos.add(name);
					}

					queryPos.add(vocabularyId);

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

	private static final String _FINDER_COLUMN_N_V_NAME_2 =
		"assetCategory.name = ? AND ";

	private static final String _FINDER_COLUMN_N_V_NAME_3 =
		"(assetCategory.name IS NULL OR assetCategory.name = '') AND ";

	private static final String _FINDER_COLUMN_N_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_V;
	private FinderPath _finderPathWithoutPaginationFindByG_P_V;
	private FinderPath _finderPathCountByG_P_V;

	/**
	 * Returns all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return findByG_P_V(
			groupId, parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end) {

		return findByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_V;
					finderArgs = new Object[] {
						groupId, parentCategoryId, vocabularyId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_V;
				finderArgs = new Object[] {
					groupId, parentCategoryId, vocabularyId, start, end,
					orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((groupId != assetCategory.getGroupId()) ||
							(parentCategoryId !=
								assetCategory.getParentCategoryId()) ||
							(vocabularyId != assetCategory.getVocabularyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_V_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_P_V_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_P_V_First(
			long groupId, long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_P_V_First(
			groupId, parentCategoryId, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_P_V_First(
		long groupId, long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByG_P_V(
			groupId, parentCategoryId, vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_P_V_Last(
			long groupId, long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_P_V_Last(
			groupId, parentCategoryId, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_P_V_Last(
		long groupId, long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByG_P_V(groupId, parentCategoryId, vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByG_P_V(
			groupId, parentCategoryId, vocabularyId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByG_P_V_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId,
			long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByG_P_V_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByG_P_V_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId, vocabularyId,
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

	protected AssetCategory getByG_P_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_V_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return filterFindByG_P_V(
			groupId, parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end) {

		return filterFindByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_V(
				groupId, parentCategoryId, vocabularyId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_V(
					groupId, parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			queryPos.add(vocabularyId);

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the asset categories before and after the current asset category in the ordered set of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] filterFindByG_P_V_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId,
			long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_V_PrevAndNext(
				categoryId, groupId, parentCategoryId, vocabularyId,
				orderByComparator);
		}

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = filterGetByG_P_V_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = filterGetByG_P_V_PrevAndNext(
				session, assetCategory, groupId, parentCategoryId, vocabularyId,
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

	protected AssetCategory filterGetByG_P_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		for (AssetCategory assetCategory :
				findByG_P_V(
					groupId, parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			FinderPath finderPath = _finderPathCountByG_P_V;

			Object[] finderArgs = new Object[] {
				groupId, parentCategoryId, vocabularyId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_V_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_P_V_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					queryPos.add(vocabularyId);

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
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_V(groupId, parentCategoryId, vocabularyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = findByG_P_V(
				groupId, parentCategoryId, vocabularyId);

			assetCategories = InlineSQLHelperUtil.filter(
				assetCategories, groupId);

			return assetCategories.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_V_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_V_VOCABULARYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			queryPos.add(vocabularyId);

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

	private static final String _FINDER_COLUMN_G_P_V_GROUPID_2 =
		"assetCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_V_PARENTCATEGORYID_2 =
		"assetCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_LikeT_V;
	private FinderPath _finderPathWithPaginationCountByG_LikeT_V;

	/**
	 * Returns all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return findByG_LikeT_V(
			groupId, treePath, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end) {

		return findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LikeT_V;
			finderArgs = new Object[] {
				groupId, treePath, vocabularyId, start, end, orderByComparator
			};

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((groupId != assetCategory.getGroupId()) ||
							!StringUtil.wildcardMatches(
								assetCategory.getTreePath(), treePath, '_', '%',
								'\\', true) ||
							(vocabularyId != assetCategory.getVocabularyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKET_V_GROUPID_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_LikeT_V_First(
			long groupId, String treePath, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_LikeT_V_First(
			groupId, treePath, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_LikeT_V_First(
		long groupId, String treePath, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByG_LikeT_V(
			groupId, treePath, vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_LikeT_V_Last(
			long groupId, String treePath, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_LikeT_V_Last(
			groupId, treePath, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_LikeT_V_Last(
		long groupId, String treePath, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByG_LikeT_V(groupId, treePath, vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByG_LikeT_V(
			groupId, treePath, vocabularyId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByG_LikeT_V_PrevAndNext(
			long categoryId, long groupId, String treePath, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		treePath = Objects.toString(treePath, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByG_LikeT_V_PrevAndNext(
				session, assetCategory, groupId, treePath, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByG_LikeT_V_PrevAndNext(
				session, assetCategory, groupId, treePath, vocabularyId,
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

	protected AssetCategory getByG_LikeT_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		String treePath, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKET_V_GROUPID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end) {

		return filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeT_V(
				groupId, treePath, vocabularyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LikeT_V(
					groupId, treePath, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKET_V_GROUPID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			queryPos.add(vocabularyId);

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the asset categories before and after the current asset category in the ordered set of asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] filterFindByG_LikeT_V_PrevAndNext(
			long categoryId, long groupId, String treePath, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeT_V_PrevAndNext(
				categoryId, groupId, treePath, vocabularyId, orderByComparator);
		}

		treePath = Objects.toString(treePath, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = filterGetByG_LikeT_V_PrevAndNext(
				session, assetCategory, groupId, treePath, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = filterGetByG_LikeT_V_PrevAndNext(
				session, assetCategory, groupId, treePath, vocabularyId,
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

	protected AssetCategory filterGetByG_LikeT_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId,
		String treePath, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKET_V_GROUPID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		for (AssetCategory assetCategory :
				findByG_LikeT_V(
					groupId, treePath, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_LikeT_V;

			Object[] finderArgs = new Object[] {
				groupId, treePath, vocabularyId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKET_V_GROUPID_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(vocabularyId);

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
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LikeT_V(groupId, treePath, vocabularyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = findByG_LikeT_V(
				groupId, treePath, vocabularyId);

			assetCategories = InlineSQLHelperUtil.filter(
				assetCategories, groupId);

			return assetCategories.size();
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKET_V_GROUPID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_LIKET_V_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			queryPos.add(vocabularyId);

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

	private static final String _FINDER_COLUMN_G_LIKET_V_GROUPID_2 =
		"assetCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LIKET_V_TREEPATH_2 =
		"assetCategory.treePath LIKE ? AND ";

	private static final String _FINDER_COLUMN_G_LIKET_V_TREEPATH_3 =
		"(assetCategory.treePath IS NULL OR assetCategory.treePath LIKE '') AND ";

	private static final String _FINDER_COLUMN_G_LIKET_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_LikeN_V;
	private FinderPath _finderPathWithPaginationCountByG_LikeN_V;

	/**
	 * Returns all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return findByG_LikeN_V(
			groupId, name, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end) {

		return findByG_LikeN_V(groupId, name, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LikeN_V;
			finderArgs = new Object[] {
				groupId, name, vocabularyId, start, end, orderByComparator
			};

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if ((groupId != assetCategory.getGroupId()) ||
							!StringUtil.wildcardMatches(
								assetCategory.getName(), name, '_', '%', '\\',
								false) ||
							(vocabularyId != assetCategory.getVocabularyId())) {

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

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(vocabularyId);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the first asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_LikeN_V_First(
			long groupId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_LikeN_V_First(
			groupId, name, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_LikeN_V_First(
		long groupId, String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		List<AssetCategory> list = findByG_LikeN_V(
			groupId, name, vocabularyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByG_LikeN_V_Last(
			long groupId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByG_LikeN_V_Last(
			groupId, name, vocabularyId, orderByComparator);

		if (assetCategory != null) {
			return assetCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", vocabularyId=");
		sb.append(vocabularyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByG_LikeN_V_Last(
		long groupId, String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		int count = countByG_LikeN_V(groupId, name, vocabularyId);

		if (count == 0) {
			return null;
		}

		List<AssetCategory> list = findByG_LikeN_V(
			groupId, name, vocabularyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the asset categories before and after the current asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] findByG_LikeN_V_PrevAndNext(
			long categoryId, long groupId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		name = Objects.toString(name, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = getByG_LikeN_V_PrevAndNext(
				session, assetCategory, groupId, name, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = getByG_LikeN_V_PrevAndNext(
				session, assetCategory, groupId, name, vocabularyId,
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

	protected AssetCategory getByG_LikeN_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId, String name,
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2);

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
			sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
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

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return filterFindByG_LikeN_V(
			groupId, name, vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end) {

		return filterFindByG_LikeN_V(
			groupId, name, vocabularyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_V(
				groupId, name, vocabularyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LikeN_V(
					groupId, name, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		name = Objects.toString(name, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(StringUtil.toLowerCase(name));
			}

			queryPos.add(vocabularyId);

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns the asset categories before and after the current asset category in the ordered set of asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param categoryId the primary key of the current asset category
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory[] filterFindByG_LikeN_V_PrevAndNext(
			long categoryId, long groupId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_V_PrevAndNext(
				categoryId, groupId, name, vocabularyId, orderByComparator);
		}

		name = Objects.toString(name, "");

		AssetCategory assetCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			AssetCategory[] array = new AssetCategoryImpl[3];

			array[0] = filterGetByG_LikeN_V_PrevAndNext(
				session, assetCategory, groupId, name, vocabularyId,
				orderByComparator, true);

			array[1] = assetCategory;

			array[2] = filterGetByG_LikeN_V_PrevAndNext(
				session, assetCategory, groupId, name, vocabularyId,
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

	protected AssetCategory filterGetByG_LikeN_V_PrevAndNext(
		Session session, AssetCategory assetCategory, long groupId, String name,
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(StringUtil.toLowerCase(name));
		}

		queryPos.add(vocabularyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						assetCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AssetCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start,
		int end) {

		return filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	@Override
	public List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_LikeN_V(
				groupIds, name, vocabularyIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LikeN_V(
					groupIds, name, vocabularyIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ASSETCATEGORY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
		}

		if (vocabularyIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_7);

			sb.append(StringUtil.merge(vocabularyIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AssetCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, AssetCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, AssetCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindName) {
				queryPos.add(StringUtil.toLowerCase(name));
			}

			return (List<AssetCategory>)QueryUtil.list(
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
	 * Returns all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return findByG_LikeN_V(
			groupIds, name, vocabularyIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start,
		int end) {

		return findByG_LikeN_V(groupIds, name, vocabularyIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	@Override
	public List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		if ((groupIds.length == 1) && (vocabularyIds.length == 1)) {
			return findByG_LikeN_V(
				groupIds[0], name, vocabularyIds[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds), name,
						StringUtil.merge(vocabularyIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), name,
					StringUtil.merge(vocabularyIds), start, end,
					orderByComparator
				};
			}

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_LikeN_V, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (AssetCategory assetCategory : list) {
						if (!ArrayUtil.contains(
								groupIds, assetCategory.getGroupId()) ||
							!StringUtil.wildcardMatches(
								assetCategory.getName(), name, '_', '%', '\\',
								false) ||
							!ArrayUtil.contains(
								vocabularyIds,
								assetCategory.getVocabularyId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
				}

				if (vocabularyIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_7);

					sb.append(StringUtil.merge(vocabularyIds));

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
					sb.append(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					list = (List<AssetCategory>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_LikeN_V,
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
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 */
	@Override
	public void removeByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		for (AssetCategory assetCategory :
				findByG_LikeN_V(
					groupId, name, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_LikeN_V(long groupId, String name, long vocabularyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_LikeN_V;

			Object[] finderArgs = new Object[] {groupId, name, vocabularyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2);

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

					queryPos.add(vocabularyId);

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
	 * Returns the number of asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), name,
				StringUtil.merge(vocabularyIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_LikeN_V, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_ASSETCATEGORY_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
				}

				if (vocabularyIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_7);

					sb.append(StringUtil.merge(vocabularyIds));

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

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_LikeN_V, finderArgs,
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
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LikeN_V(groupId, name, vocabularyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = findByG_LikeN_V(
				groupId, name, vocabularyId);

			assetCategories = InlineSQLHelperUtil.filter(
				assetCategories, groupId);

			return assetCategories.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
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

			queryPos.add(vocabularyId);

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
	 * Returns the number of asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_LikeN_V(groupIds, name, vocabularyIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AssetCategory> assetCategories = InlineSQLHelperUtil.filter(
				findByG_LikeN_V(groupIds, name, vocabularyIds), groupIds);

			return assetCategories.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");

		if (vocabularyIds == null) {
			vocabularyIds = new long[0];
		}
		else if (vocabularyIds.length > 1) {
			vocabularyIds = ArrayUtil.sortedUnique(vocabularyIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_ASSETCATEGORY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_LIKEN_V_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_V_NAME_2);
		}

		if (vocabularyIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_7);

			sb.append(StringUtil.merge(vocabularyIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AssetCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

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

	private static final String _FINDER_COLUMN_G_LIKEN_V_GROUPID_2 =
		"assetCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_V_GROUPID_7 =
		"assetCategory.groupId IN (";

	private static final String _FINDER_COLUMN_G_LIKEN_V_NAME_2 =
		"lower(assetCategory.name) LIKE ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_V_NAME_3 =
		"(assetCategory.name IS NULL OR assetCategory.name LIKE '') AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private static final String _FINDER_COLUMN_G_LIKEN_V_VOCABULARYID_7 =
		"assetCategory.vocabularyId IN (";

	private FinderPath _finderPathFetchByP_N_V;

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByP_N_V(
			long parentCategoryId, String name, long vocabularyId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByP_N_V(
			parentCategoryId, name, vocabularyId);

		if (assetCategory == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("parentCategoryId=");
			sb.append(parentCategoryId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", vocabularyId=");
			sb.append(vocabularyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCategoryException(sb.toString());
		}

		return assetCategory;
	}

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_N_V(
		long parentCategoryId, String name, long vocabularyId) {

		return fetchByP_N_V(parentCategoryId, name, vocabularyId, true);
	}

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByP_N_V(
		long parentCategoryId, String name, long vocabularyId,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					parentCategoryId, name, vocabularyId
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByP_N_V, finderArgs, this);
			}

			if (result instanceof AssetCategory) {
				AssetCategory assetCategory = (AssetCategory)result;

				if ((parentCategoryId != assetCategory.getParentCategoryId()) ||
					!Objects.equals(name, assetCategory.getName()) ||
					(vocabularyId != assetCategory.getVocabularyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_P_N_V_PARENTCATEGORYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_P_N_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_P_N_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_P_N_V_VOCABULARYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCategoryId);

					if (bindName) {
						queryPos.add(name);
					}

					queryPos.add(vocabularyId);

					List<AssetCategory> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByP_N_V, finderArgs, list);
						}
					}
					else {
						AssetCategory assetCategory = list.get(0);

						result = assetCategory;

						cacheResult(assetCategory);
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
				return (AssetCategory)result;
			}
		}
	}

	/**
	 * Removes the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the asset category that was removed
	 */
	@Override
	public AssetCategory removeByP_N_V(
			long parentCategoryId, String name, long vocabularyId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByP_N_V(
			parentCategoryId, name, vocabularyId);

		return remove(assetCategory);
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByP_N_V(
		long parentCategoryId, String name, long vocabularyId) {

		AssetCategory assetCategory = fetchByP_N_V(
			parentCategoryId, name, vocabularyId);

		if (assetCategory == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_P_N_V_PARENTCATEGORYID_2 =
		"assetCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_P_N_V_NAME_2 =
		"assetCategory.name = ? AND ";

	private static final String _FINDER_COLUMN_P_N_V_NAME_3 =
		"(assetCategory.name IS NULL OR assetCategory.name = '') AND ";

	private static final String _FINDER_COLUMN_P_N_V_VOCABULARYID_2 =
		"assetCategory.vocabularyId = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	@Override
	public AssetCategory findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByERC_G(
			externalReferenceCode, groupId);

		if (assetCategory == null) {
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

			throw new NoSuchCategoryException(sb.toString());
		}

		return assetCategory;
	}

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	@Override
	public AssetCategory fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

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

			if (result instanceof AssetCategory) {
				AssetCategory assetCategory = (AssetCategory)result;

				if (!Objects.equals(
						externalReferenceCode,
						assetCategory.getExternalReferenceCode()) ||
					(groupId != assetCategory.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_ASSETCATEGORY_WHERE);

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

					List<AssetCategory> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						AssetCategory assetCategory = list.get(0);

						result = assetCategory;

						cacheResult(assetCategory);
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
				return (AssetCategory)result;
			}
		}
	}

	/**
	 * Removes the asset category where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset category that was removed
	 */
	@Override
	public AssetCategory removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = findByERC_G(
			externalReferenceCode, groupId);

		return remove(assetCategory);
	}

	/**
	 * Returns the number of asset categories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		AssetCategory assetCategory = fetchByERC_G(
			externalReferenceCode, groupId);

		if (assetCategory == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"assetCategory.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(assetCategory.externalReferenceCode IS NULL OR assetCategory.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"assetCategory.groupId = ?";

	public AssetCategoryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AssetCategory.class);

		setModelImplClass(AssetCategoryImpl.class);
		setModelPKClass(long.class);

		setTable(AssetCategoryTable.INSTANCE);
	}

	/**
	 * Caches the asset category in the entity cache if it is enabled.
	 *
	 * @param assetCategory the asset category
	 */
	@Override
	public void cacheResult(AssetCategory assetCategory) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					assetCategory.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				AssetCategoryImpl.class, assetCategory.getPrimaryKey(),
				assetCategory);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					assetCategory.getUuid(), assetCategory.getGroupId()
				},
				assetCategory);

			FinderCacheUtil.putResult(
				_finderPathFetchByP_N_V,
				new Object[] {
					assetCategory.getParentCategoryId(),
					assetCategory.getName(), assetCategory.getVocabularyId()
				},
				assetCategory);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					assetCategory.getExternalReferenceCode(),
					assetCategory.getGroupId()
				},
				assetCategory);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the asset categories in the entity cache if it is enabled.
	 *
	 * @param assetCategories the asset categories
	 */
	@Override
	public void cacheResult(List<AssetCategory> assetCategories) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (assetCategories.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (AssetCategory assetCategory : assetCategories) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						assetCategory.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						AssetCategoryImpl.class,
						assetCategory.getPrimaryKey()) == null) {

					cacheResult(assetCategory);
				}
			}
		}
	}

	/**
	 * Clears the cache for all asset categories.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(AssetCategoryImpl.class);

		FinderCacheUtil.clearCache(AssetCategoryImpl.class);
	}

	/**
	 * Clears the cache for the asset category.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(AssetCategory assetCategory) {
		EntityCacheUtil.removeResult(AssetCategoryImpl.class, assetCategory);
	}

	@Override
	public void clearCache(List<AssetCategory> assetCategories) {
		for (AssetCategory assetCategory : assetCategories) {
			EntityCacheUtil.removeResult(
				AssetCategoryImpl.class, assetCategory);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(AssetCategoryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(AssetCategoryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		AssetCategoryModelImpl assetCategoryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					assetCategoryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				assetCategoryModelImpl.getUuid(),
				assetCategoryModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args, assetCategoryModelImpl);

			args = new Object[] {
				assetCategoryModelImpl.getParentCategoryId(),
				assetCategoryModelImpl.getName(),
				assetCategoryModelImpl.getVocabularyId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByP_N_V, args, assetCategoryModelImpl);

			args = new Object[] {
				assetCategoryModelImpl.getExternalReferenceCode(),
				assetCategoryModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, assetCategoryModelImpl);
		}
	}

	/**
	 * Creates a new asset category with the primary key. Does not add the asset category to the database.
	 *
	 * @param categoryId the primary key for the new asset category
	 * @return the new asset category
	 */
	@Override
	public AssetCategory create(long categoryId) {
		AssetCategory assetCategory = new AssetCategoryImpl();

		assetCategory.setNew(true);
		assetCategory.setPrimaryKey(categoryId);

		String uuid = PortalUUIDUtil.generate();

		assetCategory.setUuid(uuid);

		assetCategory.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetCategory;
	}

	/**
	 * Removes the asset category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category that was removed
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory remove(long categoryId)
		throws NoSuchCategoryException {

		return remove((Serializable)categoryId);
	}

	/**
	 * Removes the asset category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the asset category
	 * @return the asset category that was removed
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory remove(Serializable primaryKey)
		throws NoSuchCategoryException {

		Session session = null;

		try {
			session = openSession();

			AssetCategory assetCategory = (AssetCategory)session.get(
				AssetCategoryImpl.class, primaryKey);

			if (assetCategory == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCategoryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(assetCategory);
		}
		catch (NoSuchCategoryException noSuchEntityException) {
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
	protected AssetCategory removeImpl(AssetCategory assetCategory) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetCategory)) {
				assetCategory = (AssetCategory)session.get(
					AssetCategoryImpl.class, assetCategory.getPrimaryKeyObj());
			}

			if ((assetCategory != null) &&
				CTPersistenceHelperUtil.isRemove(assetCategory)) {

				session.delete(assetCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetCategory != null) {
			clearCache(assetCategory);
		}

		return assetCategory;
	}

	@Override
	public AssetCategory updateImpl(AssetCategory assetCategory) {
		boolean isNew = assetCategory.isNew();

		if (!(assetCategory instanceof AssetCategoryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetCategory.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetCategory);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetCategory proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetCategory implementation " +
					assetCategory.getClass());
		}

		AssetCategoryModelImpl assetCategoryModelImpl =
			(AssetCategoryModelImpl)assetCategory;

		if (Validator.isNull(assetCategory.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			assetCategory.setUuid(uuid);
		}

		if (Validator.isNull(assetCategory.getExternalReferenceCode())) {
			assetCategory.setExternalReferenceCode(assetCategory.getUuid());
		}
		else {
			if (!Objects.equals(
					assetCategoryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					assetCategory.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = assetCategory.getCompanyId();

					long groupId = assetCategory.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = assetCategory.getPrimaryKey();
					}

					try {
						assetCategory.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AssetCategory.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								assetCategory.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AssetCategory ercAssetCategory = fetchByERC_G(
				assetCategory.getExternalReferenceCode(),
				assetCategory.getGroupId());

			if (isNew) {
				if (ercAssetCategory != null) {
					throw new DuplicateAssetCategoryExternalReferenceCodeException(
						"Duplicate asset category with external reference code " +
							assetCategory.getExternalReferenceCode() +
								" and group " + assetCategory.getGroupId());
				}
			}
			else {
				if ((ercAssetCategory != null) &&
					(assetCategory.getCategoryId() !=
						ercAssetCategory.getCategoryId())) {

					throw new DuplicateAssetCategoryExternalReferenceCodeException(
						"Duplicate asset category with external reference code " +
							assetCategory.getExternalReferenceCode() +
								" and group " + assetCategory.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetCategory.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetCategory.setCreateDate(date);
			}
			else {
				assetCategory.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!assetCategoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetCategory.setModifiedDate(date);
			}
			else {
				assetCategory.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(assetCategory)) {
				if (!isNew) {
					session.evict(
						AssetCategoryImpl.class,
						assetCategory.getPrimaryKeyObj());
				}

				session.save(assetCategory);
			}
			else {
				assetCategory = (AssetCategory)session.merge(assetCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			AssetCategoryImpl.class, assetCategoryModelImpl, false, true);

		cacheUniqueFindersCache(assetCategoryModelImpl);

		if (isNew) {
			assetCategory.setNew(false);
		}

		assetCategory.resetOriginalValues();

		return assetCategory;
	}

	/**
	 * Returns the asset category with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the asset category
	 * @return the asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCategoryException {

		AssetCategory assetCategory = fetchByPrimaryKey(primaryKey);

		if (assetCategory == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCategoryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return assetCategory;
	}

	/**
	 * Returns the asset category with the primary key or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory findByPrimaryKey(long categoryId)
		throws NoSuchCategoryException {

		return findByPrimaryKey((Serializable)categoryId);
	}

	/**
	 * Returns the asset category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the asset category
	 * @return the asset category, or <code>null</code> if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				AssetCategory.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		AssetCategory assetCategory = (AssetCategory)EntityCacheUtil.getResult(
			AssetCategoryImpl.class, primaryKey);

		if (assetCategory != null) {
			return assetCategory;
		}

		Session session = null;

		try {
			session = openSession();

			assetCategory = (AssetCategory)session.get(
				AssetCategoryImpl.class, primaryKey);

			if (assetCategory != null) {
				cacheResult(assetCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return assetCategory;
	}

	/**
	 * Returns the asset category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category, or <code>null</code> if a asset category with the primary key could not be found
	 */
	@Override
	public AssetCategory fetchByPrimaryKey(long categoryId) {
		return fetchByPrimaryKey((Serializable)categoryId);
	}

	@Override
	public Map<Serializable, AssetCategory> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(AssetCategory.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, AssetCategory> map =
			new HashMap<Serializable, AssetCategory>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			AssetCategory assetCategory = fetchByPrimaryKey(primaryKey);

			if (assetCategory != null) {
				map.put(primaryKey, assetCategory);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						AssetCategory.class, primaryKey)) {

				AssetCategory assetCategory =
					(AssetCategory)EntityCacheUtil.getResult(
						AssetCategoryImpl.class, primaryKey);

				if (assetCategory == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, assetCategory);
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

			for (AssetCategory assetCategory :
					(List<AssetCategory>)query.list()) {

				map.put(assetCategory.getPrimaryKeyObj(), assetCategory);

				cacheResult(assetCategory);
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
	 * Returns all the asset categories.
	 *
	 * @return the asset categories
	 */
	@Override
	public List<AssetCategory> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of asset categories
	 */
	@Override
	public List<AssetCategory> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the asset categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of asset categories
	 */
	@Override
	public List<AssetCategory> findAll(
		int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the asset categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of asset categories
	 */
	@Override
	public List<AssetCategory> findAll(
		int start, int end, OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

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

			List<AssetCategory> list = null;

			if (useFinderCache) {
				list = (List<AssetCategory>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_ASSETCATEGORY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_ASSETCATEGORY;

					sql = sql.concat(AssetCategoryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<AssetCategory>)QueryUtil.list(
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
	 * Removes all the asset categories from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (AssetCategory assetCategory : findAll()) {
			remove(assetCategory);
		}
	}

	/**
	 * Returns the number of asset categories.
	 *
	 * @return the number of asset categories
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					AssetCategory.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_ASSETCATEGORY);

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
		return "categoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETCATEGORY;
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
		return AssetCategoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetCategory";
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
		ctMergeColumnNames.add("parentCategoryId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("vocabularyId");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("categoryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"parentCategoryId", "name", "vocabularyId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the asset category persistence.
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

		_finderPathWithPaginationFindByParentCategoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByParentCategoryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parentCategoryId"}, true);

		_finderPathWithoutPaginationFindByParentCategoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByParentCategoryId",
			new String[] {Long.class.getName()},
			new String[] {"parentCategoryId"}, true);

		_finderPathCountByParentCategoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByParentCategoryId", new String[] {Long.class.getName()},
			new String[] {"parentCategoryId"}, false);

		_finderPathWithPaginationFindByVocabularyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByVocabularyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"vocabularyId"}, true);

		_finderPathWithoutPaginationFindByVocabularyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByVocabularyId",
			new String[] {Long.class.getName()}, new String[] {"vocabularyId"},
			true);

		_finderPathCountByVocabularyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByVocabularyId",
			new String[] {Long.class.getName()}, new String[] {"vocabularyId"},
			false);

		_finderPathWithPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentCategoryId"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentCategoryId"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentCategoryId"}, false);

		_finderPathWithPaginationFindByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "vocabularyId"}, true);

		_finderPathWithoutPaginationFindByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_V",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "vocabularyId"}, true);

		_finderPathCountByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_V",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "vocabularyId"}, false);

		_finderPathWithPaginationCountByG_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_V",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "vocabularyId"}, false);

		_finderPathWithPaginationFindByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"parentCategoryId", "name"}, true);

		_finderPathWithoutPaginationFindByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"parentCategoryId", "name"}, true);

		_finderPathCountByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"parentCategoryId", "name"}, false);

		_finderPathWithPaginationFindByP_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"parentCategoryId", "vocabularyId"}, true);

		_finderPathWithoutPaginationFindByP_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_V",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"parentCategoryId", "vocabularyId"}, true);

		_finderPathCountByP_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_V",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"parentCategoryId", "vocabularyId"}, false);

		_finderPathWithPaginationFindByN_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_V",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"name", "vocabularyId"}, true);

		_finderPathWithoutPaginationFindByN_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_V",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"name", "vocabularyId"}, true);

		_finderPathCountByN_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_V",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"name", "vocabularyId"}, false);

		_finderPathWithPaginationFindByG_P_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "vocabularyId"}, true);

		_finderPathWithoutPaginationFindByG_P_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_V",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "vocabularyId"}, true);

		_finderPathCountByG_P_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_V",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "vocabularyId"},
			false);

		_finderPathWithPaginationFindByG_LikeT_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeT_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "treePath", "vocabularyId"}, true);

		_finderPathWithPaginationCountByG_LikeT_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeT_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "treePath", "vocabularyId"}, false);

		_finderPathWithPaginationFindByG_LikeN_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "name", "vocabularyId"}, true);

		_finderPathWithPaginationCountByG_LikeN_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "name", "vocabularyId"}, false);

		_finderPathFetchByP_N_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByP_N_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"parentCategoryId", "name", "vocabularyId"}, true);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		AssetCategoryUtil.setPersistence(this);
	}

	public void destroy() {
		AssetCategoryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AssetCategoryImpl.class.getName());
	}

	private static final String _SQL_SELECT_ASSETCATEGORY =
		"SELECT assetCategory FROM AssetCategory assetCategory";

	private static final String _SQL_SELECT_ASSETCATEGORY_WHERE =
		"SELECT assetCategory FROM AssetCategory assetCategory WHERE ";

	private static final String _SQL_COUNT_ASSETCATEGORY =
		"SELECT COUNT(assetCategory) FROM AssetCategory assetCategory";

	private static final String _SQL_COUNT_ASSETCATEGORY_WHERE =
		"SELECT COUNT(assetCategory) FROM AssetCategory assetCategory WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"assetCategory.categoryId";

	private static final String _FILTER_SQL_SELECT_ASSETCATEGORY_WHERE =
		"SELECT DISTINCT {assetCategory.*} FROM AssetCategory assetCategory WHERE ";

	private static final String
		_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {AssetCategory.*} FROM (SELECT DISTINCT assetCategory.categoryId FROM AssetCategory assetCategory WHERE ";

	private static final String
		_FILTER_SQL_SELECT_ASSETCATEGORY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN AssetCategory ON TEMP_TABLE.categoryId = AssetCategory.categoryId";

	private static final String _FILTER_SQL_COUNT_ASSETCATEGORY_WHERE =
		"SELECT COUNT(DISTINCT assetCategory.categoryId) AS COUNT_VALUE FROM AssetCategory assetCategory WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "assetCategory";

	private static final String _FILTER_ENTITY_TABLE = "AssetCategory";

	private static final String _ORDER_BY_ENTITY_ALIAS = "assetCategory.";

	private static final String _ORDER_BY_ENTITY_TABLE = "AssetCategory.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No AssetCategory exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AssetCategory exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}