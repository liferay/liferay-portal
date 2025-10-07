/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.DuplicateMBCategoryExternalReferenceCodeException;
import com.liferay.message.boards.exception.NoSuchCategoryException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBCategoryTable;
import com.liferay.message.boards.model.impl.MBCategoryImpl;
import com.liferay.message.boards.model.impl.MBCategoryModelImpl;
import com.liferay.message.boards.service.persistence.MBCategoryPersistence;
import com.liferay.message.boards.service.persistence.MBCategoryUtil;
import com.liferay.message.boards.service.persistence.impl.constants.MBPersistenceConstants;
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
import com.liferay.portal.kernel.dao.orm.SQLQuery;
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
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
 * The persistence implementation for the message boards category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBCategoryPersistence.class)
public class MBCategoryPersistenceImpl
	extends BasePersistenceImpl<MBCategory> implements MBCategoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBCategoryUtil</code> to access the message boards category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBCategoryImpl.class.getName();

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
	 * Returns all the message boards categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

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

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if (!uuid.equals(mbCategory.getUuid())) {
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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
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

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUuid_First(
			String uuid, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByUuid_First(uuid, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUuid_First(
		String uuid, OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUuid_Last(
			String uuid, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByUuid_Last(uuid, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUuid_Last(
		String uuid, OrderByComparator<MBCategory> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByUuid_PrevAndNext(
			long categoryId, String uuid,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		uuid = Objects.toString(uuid, "");

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, mbCategory, uuid, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByUuid_PrevAndNext(
				session, mbCategory, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory getByUuid_PrevAndNext(
		Session session, MBCategory mbCategory, String uuid,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message boards categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (MBCategory mbCategory :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"mbCategory.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(mbCategory.uuid IS NULL OR mbCategory.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByUUID_G(uuid, groupId);

		if (mbCategory == null) {
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

		return mbCategory;
	}

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof MBCategory) {
				MBCategory mbCategory = (MBCategory)result;

				if (!Objects.equals(uuid, mbCategory.getUuid()) ||
					(groupId != mbCategory.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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

					List<MBCategory> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						MBCategory mbCategory = list.get(0);

						result = mbCategory;

						cacheResult(mbCategory);
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
				return (MBCategory)result;
			}
		}
	}

	/**
	 * Removes the message boards category where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory removeByUUID_G(String uuid, long groupId)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByUUID_G(uuid, groupId);

		return remove(mbCategory);
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		MBCategory mbCategory = fetchByUUID_G(uuid, groupId);

		if (mbCategory == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"mbCategory.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(mbCategory.uuid IS NULL OR mbCategory.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"mbCategory.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

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

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if (!uuid.equals(mbCategory.getUuid()) ||
							(companyId != mbCategory.getCompanyId())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
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

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
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
	 * Returns the first message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
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
	 * Returns the last message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByUuid_C_PrevAndNext(
			long categoryId, String uuid, long companyId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		uuid = Objects.toString(uuid, "");

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, mbCategory, uuid, companyId, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByUuid_C_PrevAndNext(
				session, mbCategory, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory getByUuid_C_PrevAndNext(
		Session session, MBCategory mbCategory, String uuid, long companyId,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message boards categories where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (MBCategory mbCategory :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"mbCategory.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(mbCategory.uuid IS NULL OR mbCategory.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"mbCategory.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the message boards categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

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

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if (groupId != mbCategory.getGroupId()) {
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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByGroupId_First(
			long groupId, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByGroupId_First(
			groupId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByGroupId_First(
		long groupId, OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByGroupId_Last(
			long groupId, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByGroupId_Last(groupId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByGroupId_Last(
		long groupId, OrderByComparator<MBCategory> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByGroupId_PrevAndNext(
			long categoryId, long groupId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, mbCategory, groupId, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByGroupId_PrevAndNext(
				session, mbCategory, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory getByGroupId_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns the message boards categories before and after the current message boards category in the ordered set of message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] filterFindByGroupId_PrevAndNext(
			long categoryId, long groupId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				categoryId, groupId, orderByComparator);
		}

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, mbCategory, groupId, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, mbCategory, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory filterGetByGroupId_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (MBCategory mbCategory :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByGroupId(groupId);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
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
		"mbCategory.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the message boards categories where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

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

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if (companyId != mbCategory.getCompanyId()) {
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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByCompanyId_First(
			long companyId, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByCompanyId_First(
		long companyId, OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByCompanyId_Last(
			long companyId, OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByCompanyId_Last(
		long companyId, OrderByComparator<MBCategory> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where companyId = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByCompanyId_PrevAndNext(
			long categoryId, long companyId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, mbCategory, companyId, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByCompanyId_PrevAndNext(
				session, mbCategory, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory getByCompanyId_PrevAndNext(
		Session session, MBCategory mbCategory, long companyId,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message boards categories where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (MBCategory mbCategory :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

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
		"mbCategory.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;
	private FinderPath _finderPathWithPaginationCountByG_P;

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(long groupId, long parentCategoryId) {
		return findByG_P(
			groupId, parentCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return findByG_P(groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P(
			groupId, parentCategoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

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

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							(parentCategoryId !=
								mbCategory.getParentCategoryId())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_First(
			long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_First(
			groupId, parentCategoryId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
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
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_First(
		long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByG_P(
			groupId, parentCategoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_Last(
			long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_Last(
			groupId, parentCategoryId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
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
	 * Returns the last message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_Last(
		long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByG_P(groupId, parentCategoryId);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByG_P(
			groupId, parentCategoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByG_P_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId,
				orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByG_P_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId,
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

	protected MBCategory getByG_P_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		long parentCategoryId, OrderByComparator<MBCategory> orderByComparator,
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

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId) {

		return filterFindByG_P(
			groupId, parentCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return filterFindByG_P(groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns the message boards categories before and after the current message boards category in the ordered set of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] filterFindByG_P_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				categoryId, groupId, parentCategoryId, orderByComparator);
		}

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId,
				orderByComparator, true);

			array[1] = mbCategory;

			array[2] = filterGetByG_P_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId,
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

	protected MBCategory filterGetByG_P_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		long parentCategoryId, OrderByComparator<MBCategory> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds) {

		return filterFindByG_P(
			groupId, parentCategoryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return filterFindByG_P(groupId, parentCategoryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentCategoryIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentCategoryIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(long groupId, long[] parentCategoryIds) {
		return findByG_P(
			groupId, parentCategoryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return findByG_P(groupId, parentCategoryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P(
			groupId, parentCategoryIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		if (parentCategoryIds.length == 1) {
			return findByG_P(
				groupId, parentCategoryIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(parentCategoryIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(parentCategoryIds), start, end,
					orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					_finderPathWithPaginationFindByG_P, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							!ArrayUtil.contains(
								parentCategoryIds,
								mbCategory.getParentCategoryId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

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
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<MBCategory>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_P, finderArgs,
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
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentCategoryId) {
		for (MBCategory mbCategory :
				findByG_P(
					groupId, parentCategoryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P(long groupId, long parentCategoryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentCategoryId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

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

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P(long groupId, long[] parentCategoryIds) {
		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(parentCategoryIds)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_P, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

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

					queryPos.add(groupId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_P, finderArgs, count);
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
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentCategoryId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentCategoryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByG_P(
				groupId, parentCategoryId);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
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

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long[] parentCategoryIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentCategoryIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = InlineSQLHelperUtil.filter(
				findByG_P(groupId, parentCategoryIds), groupId);

			return mbCategories.size();
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
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

	private static final String _FINDER_COLUMN_G_P_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTCATEGORYID_2 =
		"mbCategory.parentCategoryId = ?";

	private static final String _FINDER_COLUMN_G_P_PARENTCATEGORYID_7 =
		"mbCategory.parentCategoryId IN (";

	private FinderPath _finderPathFetchByG_F;

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_F(long groupId, String friendlyURL)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_F(groupId, friendlyURL);

		if (mbCategory == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", friendlyURL=");
			sb.append(friendlyURL);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCategoryException(sb.toString());
		}

		return mbCategory;
	}

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_F(long groupId, String friendlyURL) {
		return fetchByG_F(groupId, friendlyURL, true);
	}

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_F(
		long groupId, String friendlyURL, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			friendlyURL = Objects.toString(friendlyURL, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, friendlyURL};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_F, finderArgs, this);
			}

			if (result instanceof MBCategory) {
				MBCategory mbCategory = (MBCategory)result;

				if ((groupId != mbCategory.getGroupId()) ||
					!Objects.equals(friendlyURL, mbCategory.getFriendlyURL())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

				boolean bindFriendlyURL = false;

				if (friendlyURL.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_F_FRIENDLYURL_3);
				}
				else {
					bindFriendlyURL = true;

					sb.append(_FINDER_COLUMN_G_F_FRIENDLYURL_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindFriendlyURL) {
						queryPos.add(friendlyURL);
					}

					List<MBCategory> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_F, finderArgs, list);
						}
					}
					else {
						MBCategory mbCategory = list.get(0);

						result = mbCategory;

						cacheResult(mbCategory);
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
				return (MBCategory)result;
			}
		}
	}

	/**
	 * Removes the message boards category where groupId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory removeByG_F(long groupId, String friendlyURL)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByG_F(groupId, friendlyURL);

		return remove(mbCategory);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_F(long groupId, String friendlyURL) {
		MBCategory mbCategory = fetchByG_F(groupId, friendlyURL);

		if (mbCategory == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_F_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_FRIENDLYURL_2 =
		"mbCategory.friendlyURL = ?";

	private static final String _FINDER_COLUMN_G_F_FRIENDLYURL_3 =
		"(mbCategory.friendlyURL IS NULL OR mbCategory.friendlyURL = '')";

	private FinderPath _finderPathWithPaginationFindByG_S;
	private FinderPath _finderPathWithoutPaginationFindByG_S;
	private FinderPath _finderPathCountByG_S;

	/**
	 * Returns all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_S(long groupId, int status) {
		return findByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_S;
					finderArgs = new Object[] {groupId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_S;
				finderArgs = new Object[] {
					groupId, status, start, end, orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							(status != mbCategory.getStatus())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByG_S(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_S_Last(
			long groupId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_S_Last(
			groupId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByG_S(groupId, status);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByG_S(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByG_S_PrevAndNext(
			long categoryId, long groupId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByG_S_PrevAndNext(
				session, mbCategory, groupId, status, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByG_S_PrevAndNext(
				session, mbCategory, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory getByG_S_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_S(long groupId, int status) {
		return filterFindByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S(groupId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns the message boards categories before and after the current message boards category in the ordered set of message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] filterFindByG_S_PrevAndNext(
			long categoryId, long groupId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_PrevAndNext(
				categoryId, groupId, status, orderByComparator);
		}

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = filterGetByG_S_PrevAndNext(
				session, mbCategory, groupId, status, orderByComparator, true);

			array[1] = mbCategory;

			array[2] = filterGetByG_S_PrevAndNext(
				session, mbCategory, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBCategory filterGetByG_S_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		for (MBCategory mbCategory :
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathCountByG_S;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S(groupId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByG_S(groupId, status);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_S_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_STATUS_2 =
		"mbCategory.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByC_S(long companyId, int status) {
		return findByC_S(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByC_S(
		long companyId, int status, int start, int end) {

		return findByC_S(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByC_S(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S;
					finderArgs = new Object[] {companyId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S;
				finderArgs = new Object[] {
					companyId, status, start, end, orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((companyId != mbCategory.getCompanyId()) ||
							(status != mbCategory.getStatus())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByC_S_First(
			long companyId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByC_S_First(
			companyId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByC_S(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByC_S_Last(
			long companyId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByC_S_Last(
			companyId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByC_S(companyId, status);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByC_S(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByC_S_PrevAndNext(
			long categoryId, long companyId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, mbCategory, companyId, status, orderByComparator,
				true);

			array[1] = mbCategory;

			array[2] = getByC_S_PrevAndNext(
				session, mbCategory, companyId, status, orderByComparator,
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

	protected MBCategory getByC_S_PrevAndNext(
		Session session, MBCategory mbCategory, long companyId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_S_STATUS_2);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message boards categories where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		for (MBCategory mbCategory :
				findByC_S(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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
	}

	private static final String _FINDER_COLUMN_C_S_COMPANYID_2 =
		"mbCategory.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"mbCategory.status = ?";

	private FinderPath _finderPathWithPaginationFindByNotC_G_P;
	private FinderPath _finderPathWithPaginationCountByNotC_G_P;

	/**
	 * Returns all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return findByNotC_G_P(
			categoryId, groupId, parentCategoryId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end) {

		return findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByNotC_G_P;
			finderArgs = new Object[] {
				categoryId, groupId, parentCategoryId, start, end,
				orderByComparator
			};

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((categoryId == mbCategory.getCategoryId()) ||
							(groupId != mbCategory.getGroupId()) ||
							(parentCategoryId !=
								mbCategory.getParentCategoryId())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(categoryId);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByNotC_G_P_First(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByNotC_G_P_First(
			categoryId, groupId, parentCategoryId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("categoryId!=");
		sb.append(categoryId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByNotC_G_P_First(
		long categoryId, long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByNotC_G_P(
			categoryId, groupId, parentCategoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByNotC_G_P_Last(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByNotC_G_P_Last(
			categoryId, groupId, parentCategoryId, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("categoryId!=");
		sb.append(categoryId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByNotC_G_P_Last(
		long categoryId, long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByNotC_G_P(categoryId, groupId, parentCategoryId);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByNotC_G_P(
			categoryId, groupId, parentCategoryId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end) {

		return filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByNotC_G_P(
				categoryId, groupId, parentCategoryId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByNotC_G_P(
					categoryId, groupId, parentCategoryId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(categoryId);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end) {

		return filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByNotC_G_P(
				categoryIds, groupId, parentCategoryIds, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByNotC_G_P(
					categoryIds, groupId, parentCategoryIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (categoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_7);

			sb.append(StringUtil.merge(categoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end) {

		return findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		if ((categoryIds.length == 1) && (parentCategoryIds.length == 1)) {
			return findByNotC_G_P(
				categoryIds[0], groupId, parentCategoryIds[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(categoryIds), groupId,
						StringUtil.merge(parentCategoryIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(categoryIds), groupId,
					StringUtil.merge(parentCategoryIds), start, end,
					orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					_finderPathWithPaginationFindByNotC_G_P, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if (!ArrayUtil.contains(
								categoryIds, mbCategory.getCategoryId()) ||
							(groupId != mbCategory.getGroupId()) ||
							!ArrayUtil.contains(
								parentCategoryIds,
								mbCategory.getParentCategoryId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				if (categoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_7);

					sb.append(StringUtil.merge(categoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

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
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<MBCategory>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByNotC_G_P, finderArgs,
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
	 * Removes all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	@Override
	public void removeByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		for (MBCategory mbCategory :
				findByNotC_G_P(
					categoryId, groupId, parentCategoryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByNotC_G_P;

			Object[] finderArgs = new Object[] {
				categoryId, groupId, parentCategoryId
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(categoryId);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

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

	/**
	 * Returns the number of message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(categoryIds), groupId,
				StringUtil.merge(parentCategoryIds)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByNotC_G_P, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				if (categoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_7);

					sb.append(StringUtil.merge(categoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

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

					queryPos.add(groupId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByNotC_G_P, finderArgs,
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
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByNotC_G_P(categoryId, groupId, parentCategoryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByNotC_G_P(
				categoryId, groupId, parentCategoryId);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(categoryId);

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

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByNotC_G_P(categoryIds, groupId, parentCategoryIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = InlineSQLHelperUtil.filter(
				findByNotC_G_P(categoryIds, groupId, parentCategoryIds),
				groupId);

			return mbCategories.size();
		}

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		if (categoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_CATEGORYID_7);

			sb.append(StringUtil.merge(categoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
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

	private static final String _FINDER_COLUMN_NOTC_G_P_CATEGORYID_2 =
		"mbCategory.categoryId != ? AND ";

	private static final String _FINDER_COLUMN_NOTC_G_P_CATEGORYID_7 =
		"mbCategory.categoryId NOT IN (";

	private static final String _FINDER_COLUMN_NOTC_G_P_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_2 =
		"mbCategory.parentCategoryId = ?";

	private static final String _FINDER_COLUMN_NOTC_G_P_PARENTCATEGORYID_7 =
		"mbCategory.parentCategoryId IN (";

	private FinderPath _finderPathWithPaginationFindByG_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_S;
	private FinderPath _finderPathCountByG_P_S;
	private FinderPath _finderPathWithPaginationCountByG_P_S;

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return findByG_P_S(
			groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return findByG_P_S(groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P_S(
			groupId, parentCategoryId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_S;
					finderArgs = new Object[] {
						groupId, parentCategoryId, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_S;
				finderArgs = new Object[] {
					groupId, parentCategoryId, status, start, end,
					orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							(parentCategoryId !=
								mbCategory.getParentCategoryId()) ||
							(status != mbCategory.getStatus())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_S_First(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_S_First(
			groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_S_First(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByG_P_S(
			groupId, parentCategoryId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_S_Last(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_S_Last(
			groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_S_Last(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByG_P_S(groupId, parentCategoryId, status);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByG_P_S(
			groupId, parentCategoryId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByG_P_S_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByG_P_S_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
				orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByG_P_S_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
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

	protected MBCategory getByG_P_S_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return filterFindByG_P_S(
			groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return filterFindByG_P_S(
			groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S(
				groupId, parentCategoryId, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_S(
					groupId, parentCategoryId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns the message boards categories before and after the current message boards category in the ordered set of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] filterFindByG_P_S_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S_PrevAndNext(
				categoryId, groupId, parentCategoryId, status,
				orderByComparator);
		}

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = filterGetByG_P_S_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
				orderByComparator, true);

			array[1] = mbCategory;

			array[2] = filterGetByG_P_S_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
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

	protected MBCategory filterGetByG_P_S_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return filterFindByG_P_S(
			groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return filterFindByG_P_S(
			groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S(
				groupId, parentCategoryIds, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_S(
					groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return findByG_P_S(
			groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return findByG_P_S(
			groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P_S(
			groupId, parentCategoryIds, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		if (parentCategoryIds.length == 1) {
			return findByG_P_S(
				groupId, parentCategoryIds[0], status, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(parentCategoryIds), status
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(parentCategoryIds), status, start,
					end, orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					_finderPathWithPaginationFindByG_P_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							!ArrayUtil.contains(
								parentCategoryIds,
								mbCategory.getParentCategoryId()) ||
							(status != mbCategory.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_P_S, finderArgs,
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
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(long groupId, long parentCategoryId, int status) {
		for (MBCategory mbCategory :
				findByG_P_S(
					groupId, parentCategoryId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_S(long groupId, long parentCategoryId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathCountByG_P_S;

			Object[] finderArgs = new Object[] {
				groupId, parentCategoryId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

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
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(parentCategoryIds), status
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_P_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_P_S, finderArgs,
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
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentCategoryId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_S(groupId, parentCategoryId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByG_P_S(
				groupId, parentCategoryId, status);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
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
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_S(groupId, parentCategoryIds, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = InlineSQLHelperUtil.filter(
				findByG_P_S(groupId, parentCategoryIds, status), groupId);

			return mbCategories.size();
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_S_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_P_S_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_PARENTCATEGORYID_2 =
		"mbCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_PARENTCATEGORYID_7 =
		"mbCategory.parentCategoryId IN (";

	private static final String _FINDER_COLUMN_G_P_S_STATUS_2 =
		"mbCategory.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_NotS;
	private FinderPath _finderPathWithPaginationCountByG_P_NotS;

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return findByG_P_NotS(
			groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return findByG_P_NotS(
			groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P_NotS(
			groupId, parentCategoryId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_P_NotS;
			finderArgs = new Object[] {
				groupId, parentCategoryId, status, start, end, orderByComparator
			};

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							(parentCategoryId !=
								mbCategory.getParentCategoryId()) ||
							(status == mbCategory.getStatus())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_NotS_First(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_NotS_First(
			groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_NotS_First(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByG_P_NotS(
			groupId, parentCategoryId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByG_P_NotS_Last(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByG_P_NotS_Last(
			groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByG_P_NotS_Last(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByG_P_NotS(groupId, parentCategoryId, status);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByG_P_NotS(
			groupId, parentCategoryId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message boards categories before and after the current message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] findByG_P_NotS_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = getByG_P_NotS_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
				orderByComparator, true);

			array[1] = mbCategory;

			array[2] = getByG_P_NotS_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
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

	protected MBCategory getByG_P_NotS_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

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
			sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS(
				groupId, parentCategoryId, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_NotS(
					groupId, parentCategoryId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns the message boards categories before and after the current message boards category in the ordered set of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param categoryId the primary key of the current message boards category
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory[] filterFindByG_P_NotS_PrevAndNext(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS_PrevAndNext(
				categoryId, groupId, parentCategoryId, status,
				orderByComparator);
		}

		MBCategory mbCategory = findByPrimaryKey(categoryId);

		Session session = null;

		try {
			session = openSession();

			MBCategory[] array = new MBCategoryImpl[3];

			array[0] = filterGetByG_P_NotS_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
				orderByComparator, true);

			array[1] = mbCategory;

			array[2] = filterGetByG_P_NotS_PrevAndNext(
				session, mbCategory, groupId, parentCategoryId, status,
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

	protected MBCategory filterGetByG_P_NotS_PrevAndNext(
		Session session, MBCategory mbCategory, long groupId,
		long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentCategoryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbCategory)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBCategory> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return filterFindByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS(
				groupId, parentCategoryIds, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_NotS(
					groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return findByG_P_NotS(
			groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		if (parentCategoryIds.length == 1) {
			return findByG_P_NotS(
				groupId, parentCategoryIds[0], status, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(parentCategoryIds), status
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(parentCategoryIds), status, start,
					end, orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					_finderPathWithPaginationFindByG_P_NotS, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((groupId != mbCategory.getGroupId()) ||
							!ArrayUtil.contains(
								parentCategoryIds,
								mbCategory.getParentCategoryId()) ||
							(status == mbCategory.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_P_NotS, finderArgs,
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
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		for (MBCategory mbCategory :
				findByG_P_NotS(
					groupId, parentCategoryId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_P_NotS;

			Object[] finderArgs = new Object[] {
				groupId, parentCategoryId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

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
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(parentCategoryIds), status
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_P_NotS, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_P_NotS, finderArgs,
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
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_NotS(groupId, parentCategoryId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByG_P_NotS(
				groupId, parentCategoryId, status);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
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
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_NotS(groupId, parentCategoryIds, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = InlineSQLHelperUtil.filter(
				findByG_P_NotS(groupId, parentCategoryIds, status), groupId);

			return mbCategories.size();
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_P_NOTS_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_2 =
		"mbCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_NOTS_PARENTCATEGORYID_7 =
		"mbCategory.parentCategoryId IN (";

	private static final String _FINDER_COLUMN_G_P_NOTS_STATUS_2 =
		"mbCategory.status != ?";

	private FinderPath _finderPathWithPaginationFindByNotC_G_P_S;
	private FinderPath _finderPathWithPaginationCountByNotC_G_P_S;

	/**
	 * Returns all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end) {

		return findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByNotC_G_P_S;
			finderArgs = new Object[] {
				categoryId, groupId, parentCategoryId, status, start, end,
				orderByComparator
			};

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if ((categoryId == mbCategory.getCategoryId()) ||
							(groupId != mbCategory.getGroupId()) ||
							(parentCategoryId !=
								mbCategory.getParentCategoryId()) ||
							(status != mbCategory.getStatus())) {

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

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(categoryId);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByNotC_G_P_S_First(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByNotC_G_P_S_First(
			categoryId, groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("categoryId!=");
		sb.append(categoryId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByNotC_G_P_S_First(
		long categoryId, long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		List<MBCategory> list = findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByNotC_G_P_S_Last(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByNotC_G_P_S_Last(
			categoryId, groupId, parentCategoryId, status, orderByComparator);

		if (mbCategory != null) {
			return mbCategory;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("categoryId!=");
		sb.append(categoryId);

		sb.append(", groupId=");
		sb.append(groupId);

		sb.append(", parentCategoryId=");
		sb.append(parentCategoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchCategoryException(sb.toString());
	}

	/**
	 * Returns the last message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByNotC_G_P_S_Last(
		long categoryId, long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		int count = countByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status);

		if (count == 0) {
			return null;
		}

		List<MBCategory> list = findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end) {

		return filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByNotC_G_P_S(
				categoryId, groupId, parentCategoryId, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByNotC_G_P_S(
					categoryId, groupId, parentCategoryId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(categoryId);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end) {

		return filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	@Override
	public List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByNotC_G_P_S(
				categoryIds, groupId, parentCategoryIds, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByNotC_G_P_S(
					categoryIds, groupId, parentCategoryIds, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (categoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_7);

			sb.append(StringUtil.merge(categoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBCategoryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBCategoryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBCategoryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<MBCategory>)QueryUtil.list(
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
	 * Returns all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end) {

		return findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	@Override
	public List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		if ((categoryIds.length == 1) && (parentCategoryIds.length == 1)) {
			return findByNotC_G_P_S(
				categoryIds[0], groupId, parentCategoryIds[0], status, start,
				end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(categoryIds), groupId,
						StringUtil.merge(parentCategoryIds), status
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(categoryIds), groupId,
					StringUtil.merge(parentCategoryIds), status, start, end,
					orderByComparator
				};
			}

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					_finderPathWithPaginationFindByNotC_G_P_S, finderArgs,
					this);

				if ((list != null) && !list.isEmpty()) {
					for (MBCategory mbCategory : list) {
						if (!ArrayUtil.contains(
								categoryIds, mbCategory.getCategoryId()) ||
							(groupId != mbCategory.getGroupId()) ||
							!ArrayUtil.contains(
								parentCategoryIds,
								mbCategory.getParentCategoryId()) ||
							(status != mbCategory.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

				if (categoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_7);

					sb.append(StringUtil.merge(categoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<MBCategory>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByNotC_G_P_S,
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
	 * Removes all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	@Override
	public void removeByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		for (MBCategory mbCategory :
				findByNotC_G_P_S(
					categoryId, groupId, parentCategoryId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByNotC_G_P_S;

			Object[] finderArgs = new Object[] {
				categoryId, groupId, parentCategoryId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_2);

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(categoryId);

					queryPos.add(groupId);

					queryPos.add(parentCategoryId);

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
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(categoryIds), groupId,
				StringUtil.merge(parentCategoryIds), status
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByNotC_G_P_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBCATEGORY_WHERE);

				if (categoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_7);

					sb.append(StringUtil.merge(categoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

				if (parentCategoryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_7);

					sb.append(StringUtil.merge(parentCategoryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByNotC_G_P_S, finderArgs,
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
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByNotC_G_P_S(
				categoryId, groupId, parentCategoryId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = findByNotC_G_P_S(
				categoryId, groupId, parentCategoryId, status);

			mbCategories = InlineSQLHelperUtil.filter(mbCategories, groupId);

			return mbCategories.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_2);

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(categoryId);

			queryPos.add(groupId);

			queryPos.add(parentCategoryId);

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
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	@Override
	public int filterCountByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByNotC_G_P_S(
				categoryIds, groupId, parentCategoryIds, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBCategory> mbCategories = InlineSQLHelperUtil.filter(
				findByNotC_G_P_S(
					categoryIds, groupId, parentCategoryIds, status),
				groupId);

			return mbCategories.size();
		}

		if (categoryIds == null) {
			categoryIds = new long[0];
		}
		else if (categoryIds.length > 1) {
			categoryIds = ArrayUtil.sortedUnique(categoryIds);
		}

		if (parentCategoryIds == null) {
			parentCategoryIds = new long[0];
		}
		else if (parentCategoryIds.length > 1) {
			parentCategoryIds = ArrayUtil.sortedUnique(parentCategoryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_MBCATEGORY_WHERE);

		if (categoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_7);

			sb.append(StringUtil.merge(categoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_GROUPID_2);

		if (parentCategoryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_7);

			sb.append(StringUtil.merge(parentCategoryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_NOTC_G_P_S_STATUS_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBCategory.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_2 =
		"mbCategory.categoryId != ? AND ";

	private static final String _FINDER_COLUMN_NOTC_G_P_S_CATEGORYID_7 =
		"mbCategory.categoryId NOT IN (";

	private static final String _FINDER_COLUMN_NOTC_G_P_S_GROUPID_2 =
		"mbCategory.groupId = ? AND ";

	private static final String _FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_2 =
		"mbCategory.parentCategoryId = ? AND ";

	private static final String _FINDER_COLUMN_NOTC_G_P_S_PARENTCATEGORYID_7 =
		"mbCategory.parentCategoryId IN (";

	private static final String _FINDER_COLUMN_NOTC_G_P_S_STATUS_2 =
		"mbCategory.status = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByERC_G(externalReferenceCode, groupId);

		if (mbCategory == null) {
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

		return mbCategory;
	}

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByERC_G(String externalReferenceCode, long groupId) {
		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof MBCategory) {
				MBCategory mbCategory = (MBCategory)result;

				if (!Objects.equals(
						externalReferenceCode,
						mbCategory.getExternalReferenceCode()) ||
					(groupId != mbCategory.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_MBCATEGORY_WHERE);

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

					List<MBCategory> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						MBCategory mbCategory = list.get(0);

						result = mbCategory;

						cacheResult(mbCategory);
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
				return (MBCategory)result;
			}
		}
	}

	/**
	 * Removes the message boards category where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchCategoryException {

		MBCategory mbCategory = findByERC_G(externalReferenceCode, groupId);

		return remove(mbCategory);
	}

	/**
	 * Returns the number of message boards categories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		MBCategory mbCategory = fetchByERC_G(externalReferenceCode, groupId);

		if (mbCategory == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"mbCategory.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(mbCategory.externalReferenceCode IS NULL OR mbCategory.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"mbCategory.groupId = ?";

	public MBCategoryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBCategory.class);

		setModelImplClass(MBCategoryImpl.class);
		setModelPKClass(long.class);

		setTable(MBCategoryTable.INSTANCE);
	}

	/**
	 * Caches the message boards category in the entity cache if it is enabled.
	 *
	 * @param mbCategory the message boards category
	 */
	@Override
	public void cacheResult(MBCategory mbCategory) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					mbCategory.getCtCollectionId())) {

			entityCache.putResult(
				MBCategoryImpl.class, mbCategory.getPrimaryKey(), mbCategory);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {mbCategory.getUuid(), mbCategory.getGroupId()},
				mbCategory);

			finderCache.putResult(
				_finderPathFetchByG_F,
				new Object[] {
					mbCategory.getGroupId(), mbCategory.getFriendlyURL()
				},
				mbCategory);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					mbCategory.getExternalReferenceCode(),
					mbCategory.getGroupId()
				},
				mbCategory);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the message boards categories in the entity cache if it is enabled.
	 *
	 * @param mbCategories the message boards categories
	 */
	@Override
	public void cacheResult(List<MBCategory> mbCategories) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (mbCategories.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (MBCategory mbCategory : mbCategories) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						mbCategory.getCtCollectionId())) {

				if (entityCache.getResult(
						MBCategoryImpl.class, mbCategory.getPrimaryKey()) ==
							null) {

					cacheResult(mbCategory);
				}
			}
		}
	}

	/**
	 * Clears the cache for all message boards categories.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(MBCategoryImpl.class);

		finderCache.clearCache(MBCategoryImpl.class);
	}

	/**
	 * Clears the cache for the message boards category.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(MBCategory mbCategory) {
		entityCache.removeResult(MBCategoryImpl.class, mbCategory);
	}

	@Override
	public void clearCache(List<MBCategory> mbCategories) {
		for (MBCategory mbCategory : mbCategories) {
			entityCache.removeResult(MBCategoryImpl.class, mbCategory);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(MBCategoryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(MBCategoryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		MBCategoryModelImpl mbCategoryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					mbCategoryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				mbCategoryModelImpl.getUuid(), mbCategoryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, mbCategoryModelImpl);

			args = new Object[] {
				mbCategoryModelImpl.getGroupId(),
				mbCategoryModelImpl.getFriendlyURL()
			};

			finderCache.putResult(
				_finderPathFetchByG_F, args, mbCategoryModelImpl);

			args = new Object[] {
				mbCategoryModelImpl.getExternalReferenceCode(),
				mbCategoryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, mbCategoryModelImpl);
		}
	}

	/**
	 * Creates a new message boards category with the primary key. Does not add the message boards category to the database.
	 *
	 * @param categoryId the primary key for the new message boards category
	 * @return the new message boards category
	 */
	@Override
	public MBCategory create(long categoryId) {
		MBCategory mbCategory = new MBCategoryImpl();

		mbCategory.setNew(true);
		mbCategory.setPrimaryKey(categoryId);

		String uuid = PortalUUIDUtil.generate();

		mbCategory.setUuid(uuid);

		mbCategory.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbCategory;
	}

	/**
	 * Removes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory remove(long categoryId) throws NoSuchCategoryException {
		return remove((Serializable)categoryId);
	}

	/**
	 * Removes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory remove(Serializable primaryKey)
		throws NoSuchCategoryException {

		Session session = null;

		try {
			session = openSession();

			MBCategory mbCategory = (MBCategory)session.get(
				MBCategoryImpl.class, primaryKey);

			if (mbCategory == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCategoryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(mbCategory);
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
	protected MBCategory removeImpl(MBCategory mbCategory) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbCategory)) {
				mbCategory = (MBCategory)session.get(
					MBCategoryImpl.class, mbCategory.getPrimaryKeyObj());
			}

			if ((mbCategory != null) &&
				ctPersistenceHelper.isRemove(mbCategory)) {

				session.delete(mbCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbCategory != null) {
			clearCache(mbCategory);
		}

		return mbCategory;
	}

	@Override
	public MBCategory updateImpl(MBCategory mbCategory) {
		boolean isNew = mbCategory.isNew();

		if (!(mbCategory instanceof MBCategoryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbCategory.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(mbCategory);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbCategory proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBCategory implementation " +
					mbCategory.getClass());
		}

		MBCategoryModelImpl mbCategoryModelImpl =
			(MBCategoryModelImpl)mbCategory;

		if (Validator.isNull(mbCategory.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbCategory.setUuid(uuid);
		}

		if (Validator.isNull(mbCategory.getExternalReferenceCode())) {
			mbCategory.setExternalReferenceCode(mbCategory.getUuid());
		}
		else {
			if (!Objects.equals(
					mbCategoryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					mbCategory.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = mbCategory.getCompanyId();

					long groupId = mbCategory.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = mbCategory.getPrimaryKey();
					}

					try {
						mbCategory.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								MBCategory.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								mbCategory.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			MBCategory ercMBCategory = fetchByERC_G(
				mbCategory.getExternalReferenceCode(), mbCategory.getGroupId());

			if (isNew) {
				if (ercMBCategory != null) {
					throw new DuplicateMBCategoryExternalReferenceCodeException(
						"Duplicate message boards category with external reference code " +
							mbCategory.getExternalReferenceCode() +
								" and group " + mbCategory.getGroupId());
				}
			}
			else {
				if ((ercMBCategory != null) &&
					(mbCategory.getCategoryId() !=
						ercMBCategory.getCategoryId())) {

					throw new DuplicateMBCategoryExternalReferenceCodeException(
						"Duplicate message boards category with external reference code " +
							mbCategory.getExternalReferenceCode() +
								" and group " + mbCategory.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbCategory.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbCategory.setCreateDate(date);
			}
			else {
				mbCategory.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbCategoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbCategory.setModifiedDate(date);
			}
			else {
				mbCategory.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbCategory)) {
				if (!isNew) {
					session.evict(
						MBCategoryImpl.class, mbCategory.getPrimaryKeyObj());
				}

				session.save(mbCategory);
			}
			else {
				mbCategory = (MBCategory)session.merge(mbCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			MBCategoryImpl.class, mbCategoryModelImpl, false, true);

		cacheUniqueFindersCache(mbCategoryModelImpl);

		if (isNew) {
			mbCategory.setNew(false);
		}

		mbCategory.resetOriginalValues();

		return mbCategory;
	}

	/**
	 * Returns the message boards category with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the message boards category
	 * @return the message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCategoryException {

		MBCategory mbCategory = fetchByPrimaryKey(primaryKey);

		if (mbCategory == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCategoryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return mbCategory;
	}

	/**
	 * Returns the message boards category with the primary key or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory findByPrimaryKey(long categoryId)
		throws NoSuchCategoryException {

		return findByPrimaryKey((Serializable)categoryId);
	}

	/**
	 * Returns the message boards category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the message boards category
	 * @return the message boards category, or <code>null</code> if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				MBCategory.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		MBCategory mbCategory = (MBCategory)entityCache.getResult(
			MBCategoryImpl.class, primaryKey);

		if (mbCategory != null) {
			return mbCategory;
		}

		Session session = null;

		try {
			session = openSession();

			mbCategory = (MBCategory)session.get(
				MBCategoryImpl.class, primaryKey);

			if (mbCategory != null) {
				cacheResult(mbCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return mbCategory;
	}

	/**
	 * Returns the message boards category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category, or <code>null</code> if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory fetchByPrimaryKey(long categoryId) {
		return fetchByPrimaryKey((Serializable)categoryId);
	}

	@Override
	public Map<Serializable, MBCategory> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(MBCategory.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, MBCategory> map =
			new HashMap<Serializable, MBCategory>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			MBCategory mbCategory = fetchByPrimaryKey(primaryKey);

			if (mbCategory != null) {
				map.put(primaryKey, mbCategory);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						MBCategory.class, primaryKey)) {

				MBCategory mbCategory = (MBCategory)entityCache.getResult(
					MBCategoryImpl.class, primaryKey);

				if (mbCategory == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, mbCategory);
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

			for (MBCategory mbCategory : (List<MBCategory>)query.list()) {
				map.put(mbCategory.getPrimaryKeyObj(), mbCategory);

				cacheResult(mbCategory);
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
	 * Returns all the message boards categories.
	 *
	 * @return the message boards categories
	 */
	@Override
	public List<MBCategory> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message boards categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of message boards categories
	 */
	@Override
	public List<MBCategory> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the message boards categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of message boards categories
	 */
	@Override
	public List<MBCategory> findAll(
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message boards categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of message boards categories
	 */
	@Override
	public List<MBCategory> findAll(
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

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

			List<MBCategory> list = null;

			if (useFinderCache) {
				list = (List<MBCategory>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_MBCATEGORY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_MBCATEGORY;

					sql = sql.concat(MBCategoryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<MBCategory>)QueryUtil.list(
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
	 * Removes all the message boards categories from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (MBCategory mbCategory : findAll()) {
			remove(mbCategory);
		}
	}

	/**
	 * Returns the number of message boards categories.
	 *
	 * @return the number of message boards categories
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBCategory.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_MBCATEGORY);

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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "categoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBCATEGORY;
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
		return MBCategoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBCategory";
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
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("displayStyle");
		ctMergeColumnNames.add("friendlyURL");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

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

		_uniqueIndexColumnNames.add(new String[] {"groupId", "friendlyURL"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the message boards category persistence.
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

		_finderPathWithPaginationCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentCategoryId"}, false);

		_finderPathFetchByG_F = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_F",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "friendlyURL"}, true);

		_finderPathWithPaginationFindByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "status"}, true);

		_finderPathWithoutPaginationFindByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, true);

		_finderPathCountByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, false);

		_finderPathWithPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "status"}, true);

		_finderPathWithoutPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, true);

		_finderPathCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, false);

		_finderPathWithPaginationFindByNotC_G_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNotC_G_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"categoryId", "groupId", "parentCategoryId"}, true);

		_finderPathWithPaginationCountByNotC_G_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByNotC_G_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"categoryId", "groupId", "parentCategoryId"}, false);

		_finderPathWithPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "status"}, true);

		_finderPathWithoutPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "status"}, true);

		_finderPathCountByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "status"}, false);

		_finderPathWithPaginationCountByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "status"}, false);

		_finderPathWithPaginationFindByG_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "status"}, true);

		_finderPathWithPaginationCountByG_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentCategoryId", "status"}, false);

		_finderPathWithPaginationFindByNotC_G_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNotC_G_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"categoryId", "groupId", "parentCategoryId", "status"
			},
			true);

		_finderPathWithPaginationCountByNotC_G_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByNotC_G_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"categoryId", "groupId", "parentCategoryId", "status"
			},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		MBCategoryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBCategoryUtil.setPersistence(null);

		entityCache.removeCache(MBCategoryImpl.class.getName());
	}

	@Override
	@Reference(
		target = MBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = MBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_MBCATEGORY =
		"SELECT mbCategory FROM MBCategory mbCategory";

	private static final String _SQL_SELECT_MBCATEGORY_WHERE =
		"SELECT mbCategory FROM MBCategory mbCategory WHERE ";

	private static final String _SQL_COUNT_MBCATEGORY =
		"SELECT COUNT(mbCategory) FROM MBCategory mbCategory";

	private static final String _SQL_COUNT_MBCATEGORY_WHERE =
		"SELECT COUNT(mbCategory) FROM MBCategory mbCategory WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"mbCategory.categoryId";

	private static final String _FILTER_SQL_SELECT_MBCATEGORY_WHERE =
		"SELECT DISTINCT {mbCategory.*} FROM MBCategory mbCategory WHERE ";

	private static final String
		_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {MBCategory.*} FROM (SELECT DISTINCT mbCategory.categoryId FROM MBCategory mbCategory WHERE ";

	private static final String
		_FILTER_SQL_SELECT_MBCATEGORY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN MBCategory ON TEMP_TABLE.categoryId = MBCategory.categoryId";

	private static final String _FILTER_SQL_COUNT_MBCATEGORY_WHERE =
		"SELECT COUNT(DISTINCT mbCategory.categoryId) AS COUNT_VALUE FROM MBCategory mbCategory WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "mbCategory";

	private static final String _FILTER_ENTITY_TABLE = "MBCategory";

	private static final String _ORDER_BY_ENTITY_ALIAS = "mbCategory.";

	private static final String _ORDER_BY_ENTITY_TABLE = "MBCategory.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No MBCategory exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBCategory exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBCategoryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}