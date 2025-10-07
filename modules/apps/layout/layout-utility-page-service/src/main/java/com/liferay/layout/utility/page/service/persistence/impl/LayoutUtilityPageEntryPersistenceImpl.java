/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.persistence.impl;

import com.liferay.layout.utility.page.exception.DuplicateLayoutUtilityPageEntryExternalReferenceCodeException;
import com.liferay.layout.utility.page.exception.NoSuchLayoutUtilityPageEntryException;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntryTable;
import com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryImpl;
import com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl;
import com.liferay.layout.utility.page.service.persistence.LayoutUtilityPageEntryPersistence;
import com.liferay.layout.utility.page.service.persistence.LayoutUtilityPageEntryUtil;
import com.liferay.layout.utility.page.service.persistence.impl.constants.LayoutUtilityPagePersistenceConstants;
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
 * The persistence implementation for the layout utility page entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutUtilityPageEntryPersistence.class)
public class LayoutUtilityPageEntryPersistenceImpl
	extends BasePersistenceImpl<LayoutUtilityPageEntry>
	implements LayoutUtilityPageEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutUtilityPageEntryUtil</code> to access the layout utility page entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutUtilityPageEntryImpl.class.getName();

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
	 * Returns all the layout utility page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

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

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if (!uuid.equals(layoutUtilityPageEntry.getUuid())) {
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

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUuid_First(
			String uuid,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		List<LayoutUtilityPageEntry> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUuid_Last(
			String uuid,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUuid_Last(
		String uuid,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<LayoutUtilityPageEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set where uuid = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] findByUuid_PrevAndNext(
			long LayoutUtilityPageEntryId, String uuid,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		uuid = Objects.toString(uuid, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, layoutUtilityPageEntry, uuid, orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = getByUuid_PrevAndNext(
				session, layoutUtilityPageEntry, uuid, orderByComparator,
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

	protected LayoutUtilityPageEntry getByUuid_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		String uuid,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
			sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
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
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout utility page entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
		"layoutUtilityPageEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(layoutUtilityPageEntry.uuid IS NULL OR layoutUtilityPageEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the layout utility page entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByUUID_G(
			uuid, groupId);

		if (layoutUtilityPageEntry == null) {
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

			throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
		}

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout utility page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

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

			if (result instanceof LayoutUtilityPageEntry) {
				LayoutUtilityPageEntry layoutUtilityPageEntry =
					(LayoutUtilityPageEntry)result;

				if (!Objects.equals(uuid, layoutUtilityPageEntry.getUuid()) ||
					(groupId != layoutUtilityPageEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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

					List<LayoutUtilityPageEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						LayoutUtilityPageEntry layoutUtilityPageEntry =
							list.get(0);

						result = layoutUtilityPageEntry;

						cacheResult(layoutUtilityPageEntry);
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
				return (LayoutUtilityPageEntry)result;
			}
		}
	}

	/**
	 * Removes the layout utility page entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByUUID_G(
			uuid, groupId);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByUUID_G(
			uuid, groupId);

		if (layoutUtilityPageEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"layoutUtilityPageEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(layoutUtilityPageEntry.uuid IS NULL OR layoutUtilityPageEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"layoutUtilityPageEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

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

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if (!uuid.equals(layoutUtilityPageEntry.getUuid()) ||
							(companyId !=
								layoutUtilityPageEntry.getCompanyId())) {

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

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		List<LayoutUtilityPageEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<LayoutUtilityPageEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] findByUuid_C_PrevAndNext(
			long LayoutUtilityPageEntryId, String uuid, long companyId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		uuid = Objects.toString(uuid, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, layoutUtilityPageEntry, uuid, companyId,
				orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, layoutUtilityPageEntry, uuid, companyId,
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

	protected LayoutUtilityPageEntry getByUuid_C_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		String uuid, long companyId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
			sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
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
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout utility page entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
		"layoutUtilityPageEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(layoutUtilityPageEntry.uuid IS NULL OR layoutUtilityPageEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"layoutUtilityPageEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the layout utility page entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

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

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if (groupId != layoutUtilityPageEntry.getGroupId()) {
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

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		List<LayoutUtilityPageEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<LayoutUtilityPageEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set where groupId = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] findByGroupId_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, orderByComparator,
				true);

			array[1] = layoutUtilityPageEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, orderByComparator,
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

	protected LayoutUtilityPageEntry getByGroupId_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
			sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
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
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set of layout utility page entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] filterFindByGroupId_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				LayoutUtilityPageEntryId, groupId, orderByComparator);
		}

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, orderByComparator,
				true);

			array[1] = layoutUtilityPageEntry;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, orderByComparator,
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

	protected LayoutUtilityPageEntry filterGetByGroupId_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout utility page entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

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
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutUtilityPageEntry> layoutUtilityPageEntries =
				findByGroupId(groupId);

			layoutUtilityPageEntries = InlineSQLHelperUtil.filter(
				layoutUtilityPageEntries, groupId);

			return layoutUtilityPageEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
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
		"layoutUtilityPageEntry.groupId = ?";

	private FinderPath _finderPathFetchByPlid;

	/**
	 * Returns the layout utility page entry where plid = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByPlid(long plid)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByPlid(plid);

		if (layoutUtilityPageEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("plid=");
			sb.append(plid);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
		}

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry where plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByPlid(long plid) {
		return fetchByPlid(plid, true);
	}

	/**
	 * Returns the layout utility page entry where plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByPlid(
		long plid, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {plid};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByPlid, finderArgs, this);
			}

			if (result instanceof LayoutUtilityPageEntry) {
				LayoutUtilityPageEntry layoutUtilityPageEntry =
					(LayoutUtilityPageEntry)result;

				if (plid != layoutUtilityPageEntry.getPlid()) {
					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_PLID_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(plid);

					List<LayoutUtilityPageEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByPlid, finderArgs, list);
						}
					}
					else {
						LayoutUtilityPageEntry layoutUtilityPageEntry =
							list.get(0);

						result = layoutUtilityPageEntry;

						cacheResult(layoutUtilityPageEntry);
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
				return (LayoutUtilityPageEntry)result;
			}
		}
	}

	/**
	 * Removes the layout utility page entry where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByPlid(long plid)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPlid(plid);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByPlid(long plid) {
		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByPlid(plid);

		if (layoutUtilityPageEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_PLID_PLID_2 =
		"layoutUtilityPageEntry.plid = ?";

	private FinderPath _finderPathWithPaginationFindByG_T;
	private FinderPath _finderPathWithoutPaginationFindByG_T;
	private FinderPath _finderPathCountByG_T;
	private FinderPath _finderPathWithPaginationCountByG_T;

	/**
	 * Returns all the layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(long groupId, String type) {
		return findByG_T(
			groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String type, int start, int end) {

		return findByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_T(groupId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_T;
					finderArgs = new Object[] {groupId, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_T;
				finderArgs = new Object[] {
					groupId, type, start, end, orderByComparator
				};
			}

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if ((groupId != layoutUtilityPageEntry.getGroupId()) ||
							!type.equals(layoutUtilityPageEntry.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_T_First(
			long groupId, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_T_First(
			groupId, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_T_First(
		long groupId, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		List<LayoutUtilityPageEntry> list = findByG_T(
			groupId, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_T_Last(
			long groupId, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_T_Last(
			groupId, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_T_Last(
		long groupId, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		int count = countByG_T(groupId, type);

		if (count == 0) {
			return null;
		}

		List<LayoutUtilityPageEntry> list = findByG_T(
			groupId, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] findByG_T_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		type = Objects.toString(type, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = getByG_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, type,
				orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = getByG_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, type,
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

	protected LayoutUtilityPageEntry getByG_T_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2);
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
			sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String type) {

		return filterFindByG_T(
			groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String type, int start, int end) {

		return filterFindByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T(groupId, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_T(
					groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set of layout utility page entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] filterFindByG_T_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T_PrevAndNext(
				LayoutUtilityPageEntryId, groupId, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = filterGetByG_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, type,
				orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = filterGetByG_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, type,
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

	protected LayoutUtilityPageEntry filterGetByG_T_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String[] types) {

		return filterFindByG_T(
			groupId, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String[] types, int start, int end) {

		return filterFindByG_T(groupId, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_T(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T(groupId, types, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_T(
					groupId, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
			}

			return (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns all the layout utility page entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String[] types) {

		return findByG_T(
			groupId, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String[] types, int start, int end) {

		return findByG_T(groupId, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_T(groupId, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_T(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		if (types.length == 1) {
			return findByG_T(groupId, types[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(types)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(types), start, end,
					orderByComparator
				};
			}

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					_finderPathWithPaginationFindByG_T, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if ((groupId != layoutUtilityPageEntry.getGroupId()) ||
							!ArrayUtil.contains(
								types, layoutUtilityPageEntry.getType())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				if (types.length > 0) {
					sb.append("(");

					for (int i = 0; i < types.length; i++) {
						String type = types[i];

						if (type.isEmpty()) {
							sb.append(_FINDER_COLUMN_G_T_TYPE_3);
						}
						else {
							sb.append(_FINDER_COLUMN_G_T_TYPE_2);
						}

						if ((i + 1) < types.length) {
							sb.append(WHERE_OR);
						}
					}

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
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					for (String type : types) {
						if ((type != null) && !type.isEmpty()) {
							queryPos.add(type);
						}
					}

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_T, finderArgs,
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
	 * Removes all the layout utility page entries where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, String type) {
		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				findByG_T(
					groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_T(long groupId, String type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByG_T;

			Object[] finderArgs = new Object[] {groupId, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindType) {
						queryPos.add(type);
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

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_T(long groupId, String[] types) {
		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(types)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_T, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				if (types.length > 0) {
					sb.append("(");

					for (int i = 0; i < types.length; i++) {
						String type = types[i];

						if (type.isEmpty()) {
							sb.append(_FINDER_COLUMN_G_T_TYPE_3);
						}
						else {
							sb.append(_FINDER_COLUMN_G_T_TYPE_2);
						}

						if ((i + 1) < types.length) {
							sb.append(WHERE_OR);
						}
					}

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

					for (String type : types) {
						if ((type != null) && !type.isEmpty()) {
							queryPos.add(type);
						}
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_T, finderArgs, count);
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
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, String type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_T(groupId, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutUtilityPageEntry> layoutUtilityPageEntries = findByG_T(
				groupId, type);

			layoutUtilityPageEntries = InlineSQLHelperUtil.filter(
				layoutUtilityPageEntries, groupId);

			return layoutUtilityPageEntries.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindType) {
				queryPos.add(type);
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

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, String[] types) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_T(groupId, types);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutUtilityPageEntry> layoutUtilityPageEntries =
				InlineSQLHelperUtil.filter(findByG_T(groupId, types), groupId);

			return layoutUtilityPageEntries.size();
		}

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
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

	private static final String _FINDER_COLUMN_G_T_GROUPID_2 =
		"layoutUtilityPageEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_T_TYPE_2 =
		"layoutUtilityPageEntry.type = ?";

	private static final String _FINDER_COLUMN_G_T_TYPE_3 =
		"(layoutUtilityPageEntry.type IS NULL OR layoutUtilityPageEntry.type = '')";

	private static final String _FINDER_COLUMN_G_T_TYPE_2_SQL =
		"layoutUtilityPageEntry.type_ = ?";

	private static final String _FINDER_COLUMN_G_T_TYPE_3_SQL =
		"(layoutUtilityPageEntry.type_ IS NULL OR layoutUtilityPageEntry.type_ = '')";

	private FinderPath _finderPathWithPaginationFindByG_D_T;
	private FinderPath _finderPathWithoutPaginationFindByG_D_T;
	private FinderPath _finderPathCountByG_D_T;

	/**
	 * Returns all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		return findByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end) {

		return findByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_D_T;
					finderArgs = new Object[] {
						groupId, defaultLayoutUtilityPageEntry, type
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_D_T;
				finderArgs = new Object[] {
					groupId, defaultLayoutUtilityPageEntry, type, start, end,
					orderByComparator
				};
			}

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if ((groupId != layoutUtilityPageEntry.getGroupId()) ||
							(defaultLayoutUtilityPageEntry !=
								layoutUtilityPageEntry.
									isDefaultLayoutUtilityPageEntry()) ||
							!type.equals(layoutUtilityPageEntry.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_D_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_D_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_D_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(defaultLayoutUtilityPageEntry);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_D_T_First(
			long groupId, boolean defaultLayoutUtilityPageEntry, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_D_T_First(
			groupId, defaultLayoutUtilityPageEntry, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", defaultLayoutUtilityPageEntry=");
		sb.append(defaultLayoutUtilityPageEntry);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_D_T_First(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		List<LayoutUtilityPageEntry> list = findByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_D_T_Last(
			long groupId, boolean defaultLayoutUtilityPageEntry, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_D_T_Last(
			groupId, defaultLayoutUtilityPageEntry, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", defaultLayoutUtilityPageEntry=");
		sb.append(defaultLayoutUtilityPageEntry);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_D_T_Last(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		int count = countByG_D_T(groupId, defaultLayoutUtilityPageEntry, type);

		if (count == 0) {
			return null;
		}

		List<LayoutUtilityPageEntry> list = findByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] findByG_D_T_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId,
			boolean defaultLayoutUtilityPageEntry, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		type = Objects.toString(type, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = getByG_D_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId,
				defaultLayoutUtilityPageEntry, type, orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = getByG_D_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId,
				defaultLayoutUtilityPageEntry, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutUtilityPageEntry getByG_D_T_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_D_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_D_T_TYPE_2);
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
			sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(defaultLayoutUtilityPageEntry);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		return filterFindByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end) {

		return filterFindByG_D_T(
			groupId, defaultLayoutUtilityPageEntry, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_D_T(
				groupId, defaultLayoutUtilityPageEntry, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_D_T(
					groupId, defaultLayoutUtilityPageEntry, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_D_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_D_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(defaultLayoutUtilityPageEntry);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set of layout utility page entries that the user has permission to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] filterFindByG_D_T_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId,
			boolean defaultLayoutUtilityPageEntry, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_D_T_PrevAndNext(
				LayoutUtilityPageEntryId, groupId,
				defaultLayoutUtilityPageEntry, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = filterGetByG_D_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId,
				defaultLayoutUtilityPageEntry, type, orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = filterGetByG_D_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId,
				defaultLayoutUtilityPageEntry, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutUtilityPageEntry filterGetByG_D_T_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId, boolean defaultLayoutUtilityPageEntry, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_D_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_D_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(defaultLayoutUtilityPageEntry);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 */
	@Override
	public void removeByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				findByG_D_T(
					groupId, defaultLayoutUtilityPageEntry, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByG_D_T;

			Object[] finderArgs = new Object[] {
				groupId, defaultLayoutUtilityPageEntry, type
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_D_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_D_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_D_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(defaultLayoutUtilityPageEntry);

					if (bindType) {
						queryPos.add(type);
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

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and defaultLayoutUtilityPageEntry = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultLayoutUtilityPageEntry the default layout utility page entry
	 * @param type the type
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_D_T(
		long groupId, boolean defaultLayoutUtilityPageEntry, String type) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_D_T(groupId, defaultLayoutUtilityPageEntry, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutUtilityPageEntry> layoutUtilityPageEntries = findByG_D_T(
				groupId, defaultLayoutUtilityPageEntry, type);

			layoutUtilityPageEntries = InlineSQLHelperUtil.filter(
				layoutUtilityPageEntries, groupId);

			return layoutUtilityPageEntries.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_D_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_D_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(defaultLayoutUtilityPageEntry);

			if (bindType) {
				queryPos.add(type);
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

	private static final String _FINDER_COLUMN_G_D_T_GROUPID_2 =
		"layoutUtilityPageEntry.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_D_T_DEFAULTLAYOUTUTILITYPAGEENTRY_2 =
			"layoutUtilityPageEntry.defaultLayoutUtilityPageEntry = ? AND ";

	private static final String _FINDER_COLUMN_G_D_T_TYPE_2 =
		"layoutUtilityPageEntry.type = ?";

	private static final String _FINDER_COLUMN_G_D_T_TYPE_3 =
		"(layoutUtilityPageEntry.type IS NULL OR layoutUtilityPageEntry.type = '')";

	private static final String _FINDER_COLUMN_G_D_T_TYPE_2_SQL =
		"layoutUtilityPageEntry.type_ = ?";

	private static final String _FINDER_COLUMN_G_D_T_TYPE_3_SQL =
		"(layoutUtilityPageEntry.type_ IS NULL OR layoutUtilityPageEntry.type_ = '')";

	private FinderPath _finderPathFetchByG_N_T;

	/**
	 * Returns the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_N_T(
			long groupId, String name, String type)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_N_T(
			groupId, name, type);

		if (layoutUtilityPageEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", type=");
			sb.append(type);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
		}

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_N_T(
		long groupId, String name, String type) {

		return fetchByG_N_T(groupId, name, type, true);
	}

	/**
	 * Returns the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_N_T(
		long groupId, String name, String type, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			name = Objects.toString(name, "");
			type = Objects.toString(type, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, name, type};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_N_T, finderArgs, this);
			}

			if (result instanceof LayoutUtilityPageEntry) {
				LayoutUtilityPageEntry layoutUtilityPageEntry =
					(LayoutUtilityPageEntry)result;

				if ((groupId != layoutUtilityPageEntry.getGroupId()) ||
					!Objects.equals(name, layoutUtilityPageEntry.getName()) ||
					!Objects.equals(type, layoutUtilityPageEntry.getType())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
				}

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_N_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindType) {
						queryPos.add(type);
					}

					List<LayoutUtilityPageEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_N_T, finderArgs, list);
						}
					}
					else {
						LayoutUtilityPageEntry layoutUtilityPageEntry =
							list.get(0);

						result = layoutUtilityPageEntry;

						cacheResult(layoutUtilityPageEntry);
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
				return (LayoutUtilityPageEntry)result;
			}
		}
	}

	/**
	 * Removes the layout utility page entry where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByG_N_T(
			long groupId, String name, String type)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByG_N_T(
			groupId, name, type);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_N_T(long groupId, String name, String type) {
		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_N_T(
			groupId, name, type);

		if (layoutUtilityPageEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_N_T_GROUPID_2 =
		"layoutUtilityPageEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_NAME_2 =
		"layoutUtilityPageEntry.name = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_NAME_3 =
		"(layoutUtilityPageEntry.name IS NULL OR layoutUtilityPageEntry.name = '') AND ";

	private static final String _FINDER_COLUMN_G_N_T_TYPE_2 =
		"layoutUtilityPageEntry.type = ?";

	private static final String _FINDER_COLUMN_G_N_T_TYPE_3 =
		"(layoutUtilityPageEntry.type IS NULL OR layoutUtilityPageEntry.type = '')";

	private FinderPath _finderPathWithPaginationFindByG_LikeN_T;
	private FinderPath _finderPathWithPaginationCountByG_LikeN_T;

	/**
	 * Returns all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type) {

		return findByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type, int start, int end) {

		return findByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			name = Objects.toString(name, "");
			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LikeN_T;
			finderArgs = new Object[] {
				groupId, name, type, start, end, orderByComparator
			};

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if ((groupId != layoutUtilityPageEntry.getGroupId()) ||
							!StringUtil.wildcardMatches(
								layoutUtilityPageEntry.getName(), name, '_',
								'%', '\\', true) ||
							!type.equals(layoutUtilityPageEntry.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
				}

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_LikeN_T_First(
			long groupId, String name, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_LikeN_T_First(
			groupId, name, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the first layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_LikeN_T_First(
		long groupId, String name, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		List<LayoutUtilityPageEntry> list = findByG_LikeN_T(
			groupId, name, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByG_LikeN_T_Last(
			long groupId, String name, String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByG_LikeN_T_Last(
			groupId, name, type, orderByComparator);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
	}

	/**
	 * Returns the last layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByG_LikeN_T_Last(
		long groupId, String name, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		int count = countByG_LikeN_T(groupId, name, type);

		if (count == 0) {
			return null;
		}

		List<LayoutUtilityPageEntry> list = findByG_LikeN_T(
			groupId, name, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] findByG_LikeN_T_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId, String name,
			String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = getByG_LikeN_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, name, type,
				orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = getByG_LikeN_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, name, type,
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

	protected LayoutUtilityPageEntry getByG_LikeN_T_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId, String name, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);
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
			sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String type) {

		return filterFindByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String type, int start, int end) {

		return filterFindByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_T(
				groupId, name, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LikeN_T(
					groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(name);
			}

			if (bindType) {
				queryPos.add(type);
			}

			return (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns the layout utility page entries before and after the current layout utility page entry in the ordered set of layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the current layout utility page entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry[] filterFindByG_LikeN_T_PrevAndNext(
			long LayoutUtilityPageEntryId, long groupId, String name,
			String type,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator)
		throws NoSuchLayoutUtilityPageEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_T_PrevAndNext(
				LayoutUtilityPageEntryId, groupId, name, type,
				orderByComparator);
		}

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByPrimaryKey(
			LayoutUtilityPageEntryId);

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry[] array = new LayoutUtilityPageEntryImpl[3];

			array[0] = filterGetByG_LikeN_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, name, type,
				orderByComparator, true);

			array[1] = layoutUtilityPageEntry;

			array[2] = filterGetByG_LikeN_T_PrevAndNext(
				session, layoutUtilityPageEntry, groupId, name, type,
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

	protected LayoutUtilityPageEntry filterGetByG_LikeN_T_PrevAndNext(
		Session session, LayoutUtilityPageEntry layoutUtilityPageEntry,
		long groupId, String name, String type,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutUtilityPageEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutUtilityPageEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String[] types) {

		return filterFindByG_LikeN_T(
			groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end) {

		return filterFindByG_LikeN_T(groupId, name, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public List<LayoutUtilityPageEntry> filterFindByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_T(
				groupId, name, types, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LikeN_T(
					groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		name = Objects.toString(name, "");

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutUtilityPageEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, LayoutUtilityPageEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, LayoutUtilityPageEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(name);
			}

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
			}

			return (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Returns all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types) {

		return findByG_LikeN_T(
			groupId, name, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end) {

		return findByG_LikeN_T(groupId, name, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findByG_LikeN_T(
		long groupId, String name, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		if (types.length == 1) {
			return findByG_LikeN_T(
				groupId, name, types[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, name, StringUtil.merge(types)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, name, StringUtil.merge(types), start, end,
					orderByComparator
				};
			}

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					_finderPathWithPaginationFindByG_LikeN_T, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutUtilityPageEntry layoutUtilityPageEntry : list) {
						if ((groupId != layoutUtilityPageEntry.getGroupId()) ||
							!StringUtil.wildcardMatches(
								layoutUtilityPageEntry.getName(), name, '_',
								'%', '\\', true) ||
							!ArrayUtil.contains(
								types, layoutUtilityPageEntry.getType())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
				}

				if (types.length > 0) {
					sb.append("(");

					for (int i = 0; i < types.length; i++) {
						String type = types[i];

						if (type.isEmpty()) {
							sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3);
						}
						else {
							sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);
						}

						if ((i + 1) < types.length) {
							sb.append(WHERE_OR);
						}
					}

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
					sb.append(LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(name);
					}

					for (String type : types) {
						if ((type != null) && !type.isEmpty()) {
							queryPos.add(type);
						}
					}

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_LikeN_T,
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
	 * Removes all the layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_LikeN_T(long groupId, String name, String type) {
		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				findByG_LikeN_T(
					groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, String type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			name = Objects.toString(name, "");
			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_LikeN_T;

			Object[] finderArgs = new Object[] {groupId, name, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
				}

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindType) {
						queryPos.add(type);
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

	/**
	 * Returns the number of layout utility page entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, String[] types) {
		name = Objects.toString(name, "");

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			Object[] finderArgs = new Object[] {
				groupId, name, StringUtil.merge(types)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_LikeN_T, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
				}

				if (types.length > 0) {
					sb.append("(");

					for (int i = 0; i < types.length; i++) {
						String type = types[i];

						if (type.isEmpty()) {
							sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3);
						}
						else {
							sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);
						}

						if ((i + 1) < types.length) {
							sb.append(WHERE_OR);
						}
					}

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

					if (bindName) {
						queryPos.add(name);
					}

					for (String type : types) {
						if ((type != null) && !type.isEmpty()) {
							queryPos.add(type);
						}
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_LikeN_T, finderArgs,
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
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_T(long groupId, String name, String type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LikeN_T(groupId, name, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutUtilityPageEntry> layoutUtilityPageEntries =
				findByG_LikeN_T(groupId, name, type);

			layoutUtilityPageEntries = InlineSQLHelperUtil.filter(
				layoutUtilityPageEntries, groupId);

			return layoutUtilityPageEntries.size();
		}

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
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
				queryPos.add(name);
			}

			if (bindType) {
				queryPos.add(type);
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

	/**
	 * Returns the number of layout utility page entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout utility page entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_T(
		long groupId, String name, String[] types) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LikeN_T(groupId, name, types);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutUtilityPageEntry> layoutUtilityPageEntries =
				InlineSQLHelperUtil.filter(
					findByG_LikeN_T(groupId, name, types), groupId);

			return layoutUtilityPageEntries.size();
		}

		name = Objects.toString(name, "");

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_3_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutUtilityPageEntry.class.getName(),
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
				queryPos.add(name);
			}

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
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

	private static final String _FINDER_COLUMN_G_LIKEN_T_GROUPID_2 =
		"layoutUtilityPageEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_T_NAME_2 =
		"layoutUtilityPageEntry.name LIKE ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_T_NAME_3 =
		"(layoutUtilityPageEntry.name IS NULL OR layoutUtilityPageEntry.name LIKE '') AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_T_TYPE_2 =
		"layoutUtilityPageEntry.type = ?";

	private static final String _FINDER_COLUMN_G_LIKEN_T_TYPE_3 =
		"(layoutUtilityPageEntry.type IS NULL OR layoutUtilityPageEntry.type = '')";

	private static final String _FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL =
		"layoutUtilityPageEntry.type_ = ?";

	private static final String _FINDER_COLUMN_G_LIKEN_T_TYPE_3_SQL =
		"(layoutUtilityPageEntry.type_ IS NULL OR layoutUtilityPageEntry.type_ = '')";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByERC_G(
			externalReferenceCode, groupId);

		if (layoutUtilityPageEntry == null) {
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

			throw new NoSuchLayoutUtilityPageEntryException(sb.toString());
		}

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

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

			if (result instanceof LayoutUtilityPageEntry) {
				LayoutUtilityPageEntry layoutUtilityPageEntry =
					(LayoutUtilityPageEntry)result;

				if (!Objects.equals(
						externalReferenceCode,
						layoutUtilityPageEntry.getExternalReferenceCode()) ||
					(groupId != layoutUtilityPageEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE);

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

					List<LayoutUtilityPageEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						LayoutUtilityPageEntry layoutUtilityPageEntry =
							list.get(0);

						result = layoutUtilityPageEntry;

						cacheResult(layoutUtilityPageEntry);
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
				return (LayoutUtilityPageEntry)result;
			}
		}
	}

	/**
	 * Removes the layout utility page entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout utility page entry that was removed
	 */
	@Override
	public LayoutUtilityPageEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = findByERC_G(
			externalReferenceCode, groupId);

		return remove(layoutUtilityPageEntry);
	}

	/**
	 * Returns the number of layout utility page entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout utility page entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByERC_G(
			externalReferenceCode, groupId);

		if (layoutUtilityPageEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"layoutUtilityPageEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(layoutUtilityPageEntry.externalReferenceCode IS NULL OR layoutUtilityPageEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"layoutUtilityPageEntry.groupId = ?";

	public LayoutUtilityPageEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutUtilityPageEntry.class);

		setModelImplClass(LayoutUtilityPageEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutUtilityPageEntryTable.INSTANCE);
	}

	/**
	 * Caches the layout utility page entry in the entity cache if it is enabled.
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 */
	@Override
	public void cacheResult(LayoutUtilityPageEntry layoutUtilityPageEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutUtilityPageEntry.getCtCollectionId())) {

			entityCache.putResult(
				LayoutUtilityPageEntryImpl.class,
				layoutUtilityPageEntry.getPrimaryKey(), layoutUtilityPageEntry);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					layoutUtilityPageEntry.getUuid(),
					layoutUtilityPageEntry.getGroupId()
				},
				layoutUtilityPageEntry);

			finderCache.putResult(
				_finderPathFetchByPlid,
				new Object[] {layoutUtilityPageEntry.getPlid()},
				layoutUtilityPageEntry);

			finderCache.putResult(
				_finderPathFetchByG_N_T,
				new Object[] {
					layoutUtilityPageEntry.getGroupId(),
					layoutUtilityPageEntry.getName(),
					layoutUtilityPageEntry.getType()
				},
				layoutUtilityPageEntry);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					layoutUtilityPageEntry.getExternalReferenceCode(),
					layoutUtilityPageEntry.getGroupId()
				},
				layoutUtilityPageEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the layout utility page entries in the entity cache if it is enabled.
	 *
	 * @param layoutUtilityPageEntries the layout utility page entries
	 */
	@Override
	public void cacheResult(
		List<LayoutUtilityPageEntry> layoutUtilityPageEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (layoutUtilityPageEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				layoutUtilityPageEntries) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						layoutUtilityPageEntry.getCtCollectionId())) {

				if (entityCache.getResult(
						LayoutUtilityPageEntryImpl.class,
						layoutUtilityPageEntry.getPrimaryKey()) == null) {

					cacheResult(layoutUtilityPageEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all layout utility page entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LayoutUtilityPageEntryImpl.class);

		finderCache.clearCache(LayoutUtilityPageEntryImpl.class);
	}

	/**
	 * Clears the cache for the layout utility page entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(LayoutUtilityPageEntry layoutUtilityPageEntry) {
		entityCache.removeResult(
			LayoutUtilityPageEntryImpl.class, layoutUtilityPageEntry);
	}

	@Override
	public void clearCache(
		List<LayoutUtilityPageEntry> layoutUtilityPageEntries) {

		for (LayoutUtilityPageEntry layoutUtilityPageEntry :
				layoutUtilityPageEntries) {

			entityCache.removeResult(
				LayoutUtilityPageEntryImpl.class, layoutUtilityPageEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(LayoutUtilityPageEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				LayoutUtilityPageEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LayoutUtilityPageEntryModelImpl layoutUtilityPageEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutUtilityPageEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				layoutUtilityPageEntryModelImpl.getUuid(),
				layoutUtilityPageEntryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args,
				layoutUtilityPageEntryModelImpl);

			args = new Object[] {layoutUtilityPageEntryModelImpl.getPlid()};

			finderCache.putResult(
				_finderPathFetchByPlid, args, layoutUtilityPageEntryModelImpl);

			args = new Object[] {
				layoutUtilityPageEntryModelImpl.getGroupId(),
				layoutUtilityPageEntryModelImpl.getName(),
				layoutUtilityPageEntryModelImpl.getType()
			};

			finderCache.putResult(
				_finderPathFetchByG_N_T, args, layoutUtilityPageEntryModelImpl);

			args = new Object[] {
				layoutUtilityPageEntryModelImpl.getExternalReferenceCode(),
				layoutUtilityPageEntryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, layoutUtilityPageEntryModelImpl);
		}
	}

	/**
	 * Creates a new layout utility page entry with the primary key. Does not add the layout utility page entry to the database.
	 *
	 * @param LayoutUtilityPageEntryId the primary key for the new layout utility page entry
	 * @return the new layout utility page entry
	 */
	@Override
	public LayoutUtilityPageEntry create(long LayoutUtilityPageEntryId) {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			new LayoutUtilityPageEntryImpl();

		layoutUtilityPageEntry.setNew(true);
		layoutUtilityPageEntry.setPrimaryKey(LayoutUtilityPageEntryId);

		String uuid = PortalUUIDUtil.generate();

		layoutUtilityPageEntry.setUuid(uuid);

		layoutUtilityPageEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutUtilityPageEntry;
	}

	/**
	 * Removes the layout utility page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry remove(long LayoutUtilityPageEntryId)
		throws NoSuchLayoutUtilityPageEntryException {

		return remove((Serializable)LayoutUtilityPageEntryId);
	}

	/**
	 * Removes the layout utility page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry remove(Serializable primaryKey)
		throws NoSuchLayoutUtilityPageEntryException {

		Session session = null;

		try {
			session = openSession();

			LayoutUtilityPageEntry layoutUtilityPageEntry =
				(LayoutUtilityPageEntry)session.get(
					LayoutUtilityPageEntryImpl.class, primaryKey);

			if (layoutUtilityPageEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLayoutUtilityPageEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(layoutUtilityPageEntry);
		}
		catch (NoSuchLayoutUtilityPageEntryException noSuchEntityException) {
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
	protected LayoutUtilityPageEntry removeImpl(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutUtilityPageEntry)) {
				layoutUtilityPageEntry = (LayoutUtilityPageEntry)session.get(
					LayoutUtilityPageEntryImpl.class,
					layoutUtilityPageEntry.getPrimaryKeyObj());
			}

			if ((layoutUtilityPageEntry != null) &&
				ctPersistenceHelper.isRemove(layoutUtilityPageEntry)) {

				session.delete(layoutUtilityPageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutUtilityPageEntry != null) {
			clearCache(layoutUtilityPageEntry);
		}

		return layoutUtilityPageEntry;
	}

	@Override
	public LayoutUtilityPageEntry updateImpl(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		boolean isNew = layoutUtilityPageEntry.isNew();

		if (!(layoutUtilityPageEntry instanceof
				LayoutUtilityPageEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutUtilityPageEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutUtilityPageEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutUtilityPageEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutUtilityPageEntry implementation " +
					layoutUtilityPageEntry.getClass());
		}

		LayoutUtilityPageEntryModelImpl layoutUtilityPageEntryModelImpl =
			(LayoutUtilityPageEntryModelImpl)layoutUtilityPageEntry;

		if (Validator.isNull(layoutUtilityPageEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutUtilityPageEntry.setUuid(uuid);
		}

		if (Validator.isNull(
				layoutUtilityPageEntry.getExternalReferenceCode())) {

			layoutUtilityPageEntry.setExternalReferenceCode(
				layoutUtilityPageEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutUtilityPageEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					layoutUtilityPageEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = layoutUtilityPageEntry.getCompanyId();

					long groupId = layoutUtilityPageEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layoutUtilityPageEntry.getPrimaryKey();
					}

					try {
						layoutUtilityPageEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								LayoutUtilityPageEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								layoutUtilityPageEntry.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutUtilityPageEntry ercLayoutUtilityPageEntry = fetchByERC_G(
				layoutUtilityPageEntry.getExternalReferenceCode(),
				layoutUtilityPageEntry.getGroupId());

			if (isNew) {
				if (ercLayoutUtilityPageEntry != null) {
					throw new DuplicateLayoutUtilityPageEntryExternalReferenceCodeException(
						"Duplicate layout utility page entry with external reference code " +
							layoutUtilityPageEntry.getExternalReferenceCode() +
								" and group " +
									layoutUtilityPageEntry.getGroupId());
				}
			}
			else {
				if ((ercLayoutUtilityPageEntry != null) &&
					(layoutUtilityPageEntry.getLayoutUtilityPageEntryId() !=
						ercLayoutUtilityPageEntry.
							getLayoutUtilityPageEntryId())) {

					throw new DuplicateLayoutUtilityPageEntryExternalReferenceCodeException(
						"Duplicate layout utility page entry with external reference code " +
							layoutUtilityPageEntry.getExternalReferenceCode() +
								" and group " +
									layoutUtilityPageEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutUtilityPageEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutUtilityPageEntry.setCreateDate(date);
			}
			else {
				layoutUtilityPageEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutUtilityPageEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutUtilityPageEntry.setModifiedDate(date);
			}
			else {
				layoutUtilityPageEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutUtilityPageEntry)) {
				if (!isNew) {
					session.evict(
						LayoutUtilityPageEntryImpl.class,
						layoutUtilityPageEntry.getPrimaryKeyObj());
				}

				session.save(layoutUtilityPageEntry);
			}
			else {
				layoutUtilityPageEntry = (LayoutUtilityPageEntry)session.merge(
					layoutUtilityPageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LayoutUtilityPageEntryImpl.class, layoutUtilityPageEntryModelImpl,
			false, true);

		cacheUniqueFindersCache(layoutUtilityPageEntryModelImpl);

		if (isNew) {
			layoutUtilityPageEntry.setNew(false);
		}

		layoutUtilityPageEntry.resetOriginalValues();

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout utility page entry
	 * @return the layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLayoutUtilityPageEntryException {

		LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByPrimaryKey(
			primaryKey);

		if (layoutUtilityPageEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLayoutUtilityPageEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry with the primary key or throws a <code>NoSuchLayoutUtilityPageEntryException</code> if it could not be found.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry
	 * @throws NoSuchLayoutUtilityPageEntryException if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry findByPrimaryKey(
			long LayoutUtilityPageEntryId)
		throws NoSuchLayoutUtilityPageEntryException {

		return findByPrimaryKey((Serializable)LayoutUtilityPageEntryId);
	}

	/**
	 * Returns the layout utility page entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout utility page entry
	 * @return the layout utility page entry, or <code>null</code> if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				LayoutUtilityPageEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			(LayoutUtilityPageEntry)entityCache.getResult(
				LayoutUtilityPageEntryImpl.class, primaryKey);

		if (layoutUtilityPageEntry != null) {
			return layoutUtilityPageEntry;
		}

		Session session = null;

		try {
			session = openSession();

			layoutUtilityPageEntry = (LayoutUtilityPageEntry)session.get(
				LayoutUtilityPageEntryImpl.class, primaryKey);

			if (layoutUtilityPageEntry != null) {
				cacheResult(layoutUtilityPageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return layoutUtilityPageEntry;
	}

	/**
	 * Returns the layout utility page entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry, or <code>null</code> if a layout utility page entry with the primary key could not be found
	 */
	@Override
	public LayoutUtilityPageEntry fetchByPrimaryKey(
		long LayoutUtilityPageEntryId) {

		return fetchByPrimaryKey((Serializable)LayoutUtilityPageEntryId);
	}

	@Override
	public Map<Serializable, LayoutUtilityPageEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				LayoutUtilityPageEntry.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, LayoutUtilityPageEntry> map =
			new HashMap<Serializable, LayoutUtilityPageEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			LayoutUtilityPageEntry layoutUtilityPageEntry = fetchByPrimaryKey(
				primaryKey);

			if (layoutUtilityPageEntry != null) {
				map.put(primaryKey, layoutUtilityPageEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						LayoutUtilityPageEntry.class, primaryKey)) {

				LayoutUtilityPageEntry layoutUtilityPageEntry =
					(LayoutUtilityPageEntry)entityCache.getResult(
						LayoutUtilityPageEntryImpl.class, primaryKey);

				if (layoutUtilityPageEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, layoutUtilityPageEntry);
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

			for (LayoutUtilityPageEntry layoutUtilityPageEntry :
					(List<LayoutUtilityPageEntry>)query.list()) {

				map.put(
					layoutUtilityPageEntry.getPrimaryKeyObj(),
					layoutUtilityPageEntry);

				cacheResult(layoutUtilityPageEntry);
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
	 * Returns all the layout utility page entries.
	 *
	 * @return the layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout utility page entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findAll(
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout utility page entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout utility page entries
	 */
	@Override
	public List<LayoutUtilityPageEntry> findAll(
		int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

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

			List<LayoutUtilityPageEntry> list = null;

			if (useFinderCache) {
				list = (List<LayoutUtilityPageEntry>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_LAYOUTUTILITYPAGEENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_LAYOUTUTILITYPAGEENTRY;

					sql = sql.concat(
						LayoutUtilityPageEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<LayoutUtilityPageEntry>)QueryUtil.list(
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
	 * Removes all the layout utility page entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LayoutUtilityPageEntry layoutUtilityPageEntry : findAll()) {
			remove(layoutUtilityPageEntry);
		}
	}

	/**
	 * Returns the number of layout utility page entries.
	 *
	 * @return the number of layout utility page entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutUtilityPageEntry.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_LAYOUTUTILITYPAGEENTRY);

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
		return "LayoutUtilityPageEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTUTILITYPAGEENTRY;
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
		return LayoutUtilityPageEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutUtilityPageEntry";
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
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("previewFileEntryId");
		ctMergeColumnNames.add("defaultLayoutUtilityPageEntry");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("LayoutUtilityPageEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"plid"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "name", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout utility page entry persistence.
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

		_finderPathFetchByPlid = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByPlid",
			new String[] {Long.class.getName()}, new String[] {"plid"}, true);

		_finderPathWithPaginationFindByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "type_"}, true);

		_finderPathWithoutPaginationFindByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "type_"}, true);

		_finderPathCountByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "type_"}, false);

		_finderPathWithPaginationCountByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "type_"}, false);

		_finderPathWithPaginationFindByG_D_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "defaultLayoutUtilityPageEntry", "type_"},
			true);

		_finderPathWithoutPaginationFindByG_D_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "defaultLayoutUtilityPageEntry", "type_"},
			true);

		_finderPathCountByG_D_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "defaultLayoutUtilityPageEntry", "type_"},
			false);

		_finderPathFetchByG_N_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_N_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, true);

		_finderPathWithPaginationFindByG_LikeN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, true);

		_finderPathWithPaginationCountByG_LikeN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		LayoutUtilityPageEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutUtilityPageEntryUtil.setPersistence(null);

		entityCache.removeCache(LayoutUtilityPageEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutUtilityPagePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutUtilityPagePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutUtilityPagePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_LAYOUTUTILITYPAGEENTRY =
		"SELECT layoutUtilityPageEntry FROM LayoutUtilityPageEntry layoutUtilityPageEntry";

	private static final String _SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE =
		"SELECT layoutUtilityPageEntry FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String _SQL_COUNT_LAYOUTUTILITYPAGEENTRY =
		"SELECT COUNT(layoutUtilityPageEntry) FROM LayoutUtilityPageEntry layoutUtilityPageEntry";

	private static final String _SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE =
		"SELECT COUNT(layoutUtilityPageEntry) FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"layoutUtilityPageEntry.LayoutUtilityPageEntryId";

	private static final String
		_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_WHERE =
			"SELECT DISTINCT {layoutUtilityPageEntry.*} FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {LayoutUtilityPageEntry.*} FROM (SELECT DISTINCT layoutUtilityPageEntry.LayoutUtilityPageEntryId FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUTUTILITYPAGEENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN LayoutUtilityPageEntry ON TEMP_TABLE.LayoutUtilityPageEntryId = LayoutUtilityPageEntry.LayoutUtilityPageEntryId";

	private static final String _FILTER_SQL_COUNT_LAYOUTUTILITYPAGEENTRY_WHERE =
		"SELECT COUNT(DISTINCT layoutUtilityPageEntry.LayoutUtilityPageEntryId) AS COUNT_VALUE FROM LayoutUtilityPageEntry layoutUtilityPageEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "layoutUtilityPageEntry";

	private static final String _FILTER_ENTITY_TABLE = "LayoutUtilityPageEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"layoutUtilityPageEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"LayoutUtilityPageEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LayoutUtilityPageEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutUtilityPageEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}