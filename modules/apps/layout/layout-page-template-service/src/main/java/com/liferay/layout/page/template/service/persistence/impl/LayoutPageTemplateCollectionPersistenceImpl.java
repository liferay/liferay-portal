/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollectionTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateCollectionPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateCollectionUtil;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
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
 * The persistence implementation for the layout page template collection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutPageTemplateCollectionPersistence.class)
public class LayoutPageTemplateCollectionPersistenceImpl
	extends BasePersistenceImpl<LayoutPageTemplateCollection>
	implements LayoutPageTemplateCollectionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateCollectionUtil</code> to access the layout page template collection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateCollectionImpl.class.getName();

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
	 * Returns all the layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if (!uuid.equals(
								layoutPageTemplateCollection.getUuid())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByUuid_First(uuid, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUuid_Last(
			String uuid,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByUuid_Last(uuid, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUuid_Last(
		String uuid,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByUuid_PrevAndNext(
			long layoutPageTemplateCollectionId, String uuid,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		uuid = Objects.toString(uuid, "");

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, layoutPageTemplateCollection, uuid, orderByComparator,
				true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByUuid_PrevAndNext(
				session, layoutPageTemplateCollection, uuid, orderByComparator,
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

	protected LayoutPageTemplateCollection getByUuid_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, String uuid,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
		"layoutPageTemplateCollection.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(layoutPageTemplateCollection.uuid IS NULL OR layoutPageTemplateCollection.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByUUID_G(uuid, groupId);

		if (layoutPageTemplateCollection == null) {
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

			throw new NoSuchPageTemplateCollectionException(sb.toString());
		}

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			if (result instanceof LayoutPageTemplateCollection) {
				LayoutPageTemplateCollection layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)result;

				if (!Objects.equals(
						uuid, layoutPageTemplateCollection.getUuid()) ||
					(groupId != layoutPageTemplateCollection.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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

					List<LayoutPageTemplateCollection> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						LayoutPageTemplateCollection
							layoutPageTemplateCollection = list.get(0);

						result = layoutPageTemplateCollection;

						cacheResult(layoutPageTemplateCollection);
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
				return (LayoutPageTemplateCollection)result;
			}
		}
	}

	/**
	 * Removes the layout page template collection where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByUUID_G(uuid, groupId);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByUUID_G(uuid, groupId);

		if (layoutPageTemplateCollection == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"layoutPageTemplateCollection.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(layoutPageTemplateCollection.uuid IS NULL OR layoutPageTemplateCollection.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"layoutPageTemplateCollection.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if (!uuid.equals(
								layoutPageTemplateCollection.getUuid()) ||
							(companyId !=
								layoutPageTemplateCollection.getCompanyId())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByUuid_C_PrevAndNext(
			long layoutPageTemplateCollectionId, String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		uuid = Objects.toString(uuid, "");

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, layoutPageTemplateCollection, uuid, companyId,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByUuid_C_PrevAndNext(
				session, layoutPageTemplateCollection, uuid, companyId,
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

	protected LayoutPageTemplateCollection getByUuid_C_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, String uuid,
		long companyId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
		"layoutPageTemplateCollection.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(layoutPageTemplateCollection.uuid IS NULL OR layoutPageTemplateCollection.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"layoutPageTemplateCollection.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if (groupId !=
								layoutPageTemplateCollection.getGroupId()) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByGroupId_First(groupId, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByGroupId_Last(
			long groupId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByGroupId_Last(groupId, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByGroupId_Last(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByGroupId_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByGroupId_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
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

	protected LayoutPageTemplateCollection getByGroupId_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					LayoutPageTemplateCollectionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					LayoutPageTemplateCollectionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] filterFindByGroupId_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				layoutPageTemplateCollectionId, groupId, orderByComparator);
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
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

	protected LayoutPageTemplateCollection filterGetByGroupId_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutPageTemplateCollectionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutPageTemplateCollectionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				findByGroupId(groupId);

			layoutPageTemplateCollections = InlineSQLHelperUtil.filter(
				layoutPageTemplateCollections, groupId);

			return layoutPageTemplateCollections.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
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
		"layoutPageTemplateCollection.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end) {

		return findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {
						groupId, parentLayoutPageTemplateCollectionId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, parentLayoutPageTemplateCollectionId, start, end,
					orderByComparator
				};
			}

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if ((groupId !=
								layoutPageTemplateCollection.getGroupId()) ||
							(parentLayoutPageTemplateCollectionId !=
								layoutPageTemplateCollection.
									getParentLayoutPageTemplateCollectionId())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentLayoutPageTemplateCollectionId);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_First(
			long groupId, long parentLayoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_P_First(
				groupId, parentLayoutPageTemplateCollectionId,
				orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentLayoutPageTemplateCollectionId=");
		sb.append(parentLayoutPageTemplateCollectionId);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_First(
		long groupId, long parentLayoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_Last(
			long groupId, long parentLayoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_P_Last(
				groupId, parentLayoutPageTemplateCollectionId,
				orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentLayoutPageTemplateCollectionId=");
		sb.append(parentLayoutPageTemplateCollectionId);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_Last(
		long groupId, long parentLayoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByG_P(groupId, parentLayoutPageTemplateCollectionId);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByG_P_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId,
			long parentLayoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByG_P_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutPageTemplateCollection getByG_P_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		long parentLayoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentLayoutPageTemplateCollectionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end) {

		return filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentLayoutPageTemplateCollectionId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentLayoutPageTemplateCollectionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(
			_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					LayoutPageTemplateCollectionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					LayoutPageTemplateCollectionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentLayoutPageTemplateCollectionId);

			return (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] filterFindByG_P_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId,
			long parentLayoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				layoutPageTemplateCollectionId, groupId,
				parentLayoutPageTemplateCollectionId, orderByComparator);
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = filterGetByG_P_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutPageTemplateCollection filterGetByG_P_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		long parentLayoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(
			_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutPageTemplateCollectionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutPageTemplateCollectionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentLayoutPageTemplateCollectionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 */
	@Override
	public void removeByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByG_P(
					groupId, parentLayoutPageTemplateCollectionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {
				groupId, parentLayoutPageTemplateCollectionId
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentLayoutPageTemplateCollectionId);

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
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentLayoutPageTemplateCollectionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				findByG_P(groupId, parentLayoutPageTemplateCollectionId);

			layoutPageTemplateCollections = InlineSQLHelperUtil.filter(
				layoutPageTemplateCollections, groupId);

			return layoutPageTemplateCollections.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(
			_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentLayoutPageTemplateCollectionId);

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
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2 =
			"layoutPageTemplateCollection.parentLayoutPageTemplateCollectionId = ?";

	private static final String
		_FINDER_COLUMN_G_P_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL =
			"layoutPageTemplateCollection.parentLPTCollectionId = ?";

	private FinderPath _finderPathWithPaginationFindByG_T;
	private FinderPath _finderPathWithoutPaginationFindByG_T;
	private FinderPath _finderPathCountByG_T;

	/**
	 * Returns all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type) {

		return findByG_T(
			groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end) {

		return findByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_T(groupId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if ((groupId !=
								layoutPageTemplateCollection.getGroupId()) ||
							(type != layoutPageTemplateCollection.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(type);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_T_First(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_T_First(groupId, type, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByG_T(
			groupId, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_T_Last(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_T_Last(groupId, type, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_T_Last(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByG_T(groupId, type);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByG_T(
			groupId, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByG_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByG_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, type,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByG_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, type,
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

	protected LayoutPageTemplateCollection getByG_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_T_TYPE_2);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type) {

		return filterFindByG_T(
			groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end) {

		return filterFindByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

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

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					LayoutPageTemplateCollectionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					LayoutPageTemplateCollectionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(type);

			return (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] filterFindByG_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T_PrevAndNext(
				layoutPageTemplateCollectionId, groupId, type,
				orderByComparator);
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = filterGetByG_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, type,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = filterGetByG_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, type,
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

	protected LayoutPageTemplateCollection filterGetByG_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutPageTemplateCollectionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutPageTemplateCollectionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, int type) {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByG_T(
					groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_T(long groupId, int type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			FinderPath finderPath = _finderPathCountByG_T;

			Object[] finderArgs = new Object[] {groupId, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(type);

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
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, int type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_T(groupId, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				findByG_T(groupId, type);

			layoutPageTemplateCollections = InlineSQLHelperUtil.filter(
				layoutPageTemplateCollections, groupId);

			return layoutPageTemplateCollections.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(type);

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
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_T_TYPE_2 =
		"layoutPageTemplateCollection.type = ?";

	private static final String _FINDER_COLUMN_G_T_TYPE_2_SQL =
		"layoutPageTemplateCollection.type_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_T;
	private FinderPath _finderPathWithoutPaginationFindByG_P_T;
	private FinderPath _finderPathCountByG_P_T;

	/**
	 * Returns all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end) {

		return findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_T;
					finderArgs = new Object[] {
						groupId, parentLayoutPageTemplateCollectionId, type
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_T;
				finderArgs = new Object[] {
					groupId, parentLayoutPageTemplateCollectionId, type, start,
					end, orderByComparator
				};
			}

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if ((groupId !=
								layoutPageTemplateCollection.getGroupId()) ||
							(parentLayoutPageTemplateCollectionId !=
								layoutPageTemplateCollection.
									getParentLayoutPageTemplateCollectionId()) ||
							(type != layoutPageTemplateCollection.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

				sb.append(_FINDER_COLUMN_G_P_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentLayoutPageTemplateCollectionId);

					queryPos.add(type);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_T_First(
			long groupId, long parentLayoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_P_T_First(
				groupId, parentLayoutPageTemplateCollectionId, type,
				orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentLayoutPageTemplateCollectionId=");
		sb.append(parentLayoutPageTemplateCollectionId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_T_First(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_T_Last(
			long groupId, long parentLayoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_P_T_Last(
				groupId, parentLayoutPageTemplateCollectionId, type,
				orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentLayoutPageTemplateCollectionId=");
		sb.append(parentLayoutPageTemplateCollectionId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_T_Last(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByG_P_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId,
			long parentLayoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByG_P_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, type, orderByComparator,
				true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByG_P_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, type, orderByComparator,
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

	protected LayoutPageTemplateCollection getByG_P_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		long parentLayoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

		sb.append(_FINDER_COLUMN_G_P_T_TYPE_2);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentLayoutPageTemplateCollectionId);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end) {

		return filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_T(
				groupId, parentLayoutPageTemplateCollectionId, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_T(
					groupId, parentLayoutPageTemplateCollectionId, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

		sb.append(
			_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					LayoutPageTemplateCollectionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					LayoutPageTemplateCollectionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentLayoutPageTemplateCollectionId);

			queryPos.add(type);

			return (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] filterFindByG_P_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId,
			long parentLayoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_T_PrevAndNext(
				layoutPageTemplateCollectionId, groupId,
				parentLayoutPageTemplateCollectionId, type, orderByComparator);
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = filterGetByG_P_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, type, orderByComparator,
				true);

			array[1] = layoutPageTemplateCollection;

			array[2] = filterGetByG_P_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId,
				parentLayoutPageTemplateCollectionId, type, orderByComparator,
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

	protected LayoutPageTemplateCollection filterGetByG_P_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		long parentLayoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

		sb.append(
			_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutPageTemplateCollectionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutPageTemplateCollectionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentLayoutPageTemplateCollectionId);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 */
	@Override
	public void removeByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByG_P_T(
					groupId, parentLayoutPageTemplateCollectionId, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			FinderPath finderPath = _finderPathCountByG_P_T;

			Object[] finderArgs = new Object[] {
				groupId, parentLayoutPageTemplateCollectionId, type
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

				sb.append(_FINDER_COLUMN_G_P_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentLayoutPageTemplateCollectionId);

					queryPos.add(type);

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
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_T(
				groupId, parentLayoutPageTemplateCollectionId, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				findByG_P_T(
					groupId, parentLayoutPageTemplateCollectionId, type);

			layoutPageTemplateCollections = InlineSQLHelperUtil.filter(
				layoutPageTemplateCollections, groupId);

			return layoutPageTemplateCollections.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

		sb.append(
			_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentLayoutPageTemplateCollectionId);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_G_P_T_GROUPID_2 =
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2 =
			"layoutPageTemplateCollection.parentLayoutPageTemplateCollectionId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_P_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2_SQL =
			"layoutPageTemplateCollection.parentLPTCollectionId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_2 =
		"layoutPageTemplateCollection.type = ?";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_2_SQL =
		"layoutPageTemplateCollection.type_ = ?";

	private FinderPath _finderPathFetchByG_LPTCK_T;

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_LPTCK_T(groupId, layoutPageTemplateCollectionKey, type);

		if (layoutPageTemplateCollection == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", layoutPageTemplateCollectionKey=");
			sb.append(layoutPageTemplateCollectionKey);

			sb.append(", type=");
			sb.append(type);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageTemplateCollectionException(sb.toString());
		}

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return fetchByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type, true);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			layoutPageTemplateCollectionKey = Objects.toString(
				layoutPageTemplateCollectionKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, layoutPageTemplateCollectionKey, type
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_LPTCK_T, finderArgs, this);
			}

			if (result instanceof LayoutPageTemplateCollection) {
				LayoutPageTemplateCollection layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)result;

				if ((groupId != layoutPageTemplateCollection.getGroupId()) ||
					!Objects.equals(
						layoutPageTemplateCollectionKey,
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionKey()) ||
					(type != layoutPageTemplateCollection.getType())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_LPTCK_T_GROUPID_2);

				boolean bindLayoutPageTemplateCollectionKey = false;

				if (layoutPageTemplateCollectionKey.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_G_LPTCK_T_LAYOUTPAGETEMPLATECOLLECTIONKEY_3);
				}
				else {
					bindLayoutPageTemplateCollectionKey = true;

					sb.append(
						_FINDER_COLUMN_G_LPTCK_T_LAYOUTPAGETEMPLATECOLLECTIONKEY_2);
				}

				sb.append(_FINDER_COLUMN_G_LPTCK_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindLayoutPageTemplateCollectionKey) {
						queryPos.add(layoutPageTemplateCollectionKey);
					}

					queryPos.add(type);

					List<LayoutPageTemplateCollection> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_LPTCK_T, finderArgs, list);
						}
					}
					else {
						LayoutPageTemplateCollection
							layoutPageTemplateCollection = list.get(0);

						result = layoutPageTemplateCollection;

						cacheResult(layoutPageTemplateCollection);
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
				return (LayoutPageTemplateCollection)result;
			}
		}
	}

	/**
	 * Removes the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByG_LPTCK_T(groupId, layoutPageTemplateCollectionKey, type);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_LPTCK_T(groupId, layoutPageTemplateCollectionKey, type);

		if (layoutPageTemplateCollection == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_LPTCK_T_GROUPID_2 =
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_LPTCK_T_LAYOUTPAGETEMPLATECOLLECTIONKEY_2 =
			"layoutPageTemplateCollection.layoutPageTemplateCollectionKey = ? AND ";

	private static final String
		_FINDER_COLUMN_G_LPTCK_T_LAYOUTPAGETEMPLATECOLLECTIONKEY_3 =
			"(layoutPageTemplateCollection.layoutPageTemplateCollectionKey IS NULL OR layoutPageTemplateCollection.layoutPageTemplateCollectionKey = '') AND ";

	private static final String _FINDER_COLUMN_G_LPTCK_T_TYPE_2 =
		"layoutPageTemplateCollection.type = ?";

	private FinderPath _finderPathWithPaginationFindByG_N_T;
	private FinderPath _finderPathWithoutPaginationFindByG_N_T;
	private FinderPath _finderPathCountByG_N_T;

	/**
	 * Returns all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_N_T(
		long groupId, String name, int type) {

		return findByG_N_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_N_T(
		long groupId, String name, int type, int start, int end) {

		return findByG_N_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_N_T(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_T;
					finderArgs = new Object[] {groupId, name, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_T;
				finderArgs = new Object[] {
					groupId, name, type, start, end, orderByComparator
				};
			}

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if ((groupId !=
								layoutPageTemplateCollection.getGroupId()) ||
							!name.equals(
								layoutPageTemplateCollection.getName()) ||
							(type != layoutPageTemplateCollection.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_N_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(type);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_N_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_N_T_First(groupId, name, type, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_N_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByG_N_T(
			groupId, name, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_N_T_Last(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_N_T_Last(groupId, name, type, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_N_T_Last(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByG_N_T(groupId, name, type);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByG_N_T(
			groupId, name, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByG_N_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId, String name,
			int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		name = Objects.toString(name, "");

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByG_N_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByG_N_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
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

	protected LayoutPageTemplateCollection getByG_N_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_TYPE_2);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_N_T(
		long groupId, String name, int type) {

		return filterFindByG_N_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end) {

		return filterFindByG_N_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_T(
				groupId, name, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_T(
					groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					LayoutPageTemplateCollectionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					LayoutPageTemplateCollectionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(name);
			}

			queryPos.add(type);

			return (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] filterFindByG_N_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId, String name,
			int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_T_PrevAndNext(
				layoutPageTemplateCollectionId, groupId, name, type,
				orderByComparator);
		}

		name = Objects.toString(name, "");

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = filterGetByG_N_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = filterGetByG_N_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
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

	protected LayoutPageTemplateCollection filterGetByG_N_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutPageTemplateCollectionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutPageTemplateCollectionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_N_T(long groupId, String name, int type) {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByG_N_T(
					groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_N_T(long groupId, String name, int type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByG_N_T;

			Object[] finderArgs = new Object[] {groupId, name, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_N_T_TYPE_2);

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

					queryPos.add(type);

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
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_T(long groupId, String name, int type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_T(groupId, name, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				findByG_N_T(groupId, name, type);

			layoutPageTemplateCollections = InlineSQLHelperUtil.filter(
				layoutPageTemplateCollections, groupId);

			return layoutPageTemplateCollections.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_N_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_T_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
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

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_G_N_T_GROUPID_2 =
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_NAME_2 =
		"layoutPageTemplateCollection.name = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_NAME_3 =
		"(layoutPageTemplateCollection.name IS NULL OR layoutPageTemplateCollection.name = '') AND ";

	private static final String _FINDER_COLUMN_G_N_T_TYPE_2 =
		"layoutPageTemplateCollection.type = ?";

	private static final String _FINDER_COLUMN_G_N_T_TYPE_2_SQL =
		"layoutPageTemplateCollection.type_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_LikeN_T;
	private FinderPath _finderPathWithPaginationCountByG_LikeN_T;

	/**
	 * Returns all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type) {

		return findByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end) {

		return findByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LikeN_T;
			finderArgs = new Object[] {
				groupId, name, type, start, end, orderByComparator
			};

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateCollection
							layoutPageTemplateCollection : list) {

						if ((groupId !=
								layoutPageTemplateCollection.getGroupId()) ||
							!StringUtil.wildcardMatches(
								layoutPageTemplateCollection.getName(), name,
								'_', '%', '\\', true) ||
							(type != layoutPageTemplateCollection.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(type);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_LikeN_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_LikeN_T_First(groupId, name, type, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
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

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_LikeN_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		List<LayoutPageTemplateCollection> list = findByG_LikeN_T(
			groupId, name, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_LikeN_T_Last(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_LikeN_T_Last(groupId, name, type, orderByComparator);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
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

		throw new NoSuchPageTemplateCollectionException(sb.toString());
	}

	/**
	 * Returns the last layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_LikeN_T_Last(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		int count = countByG_LikeN_T(groupId, name, type);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateCollection> list = findByG_LikeN_T(
			groupId, name, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] findByG_LikeN_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId, String name,
			int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		name = Objects.toString(name, "");

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = getByG_LikeN_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = getByG_LikeN_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
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

	protected LayoutPageTemplateCollection getByG_LikeN_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);

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
			sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
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

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type) {

		return filterFindByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end) {

		return filterFindByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

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

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
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

		sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					LayoutPageTemplateCollectionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					LayoutPageTemplateCollectionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(name);
			}

			queryPos.add(type);

			return (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection[] filterFindByG_LikeN_T_PrevAndNext(
			long layoutPageTemplateCollectionId, long groupId, String name,
			int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LikeN_T_PrevAndNext(
				layoutPageTemplateCollectionId, groupId, name, type,
				orderByComparator);
		}

		name = Objects.toString(name, "");

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByPrimaryKey(layoutPageTemplateCollectionId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection[] array =
				new LayoutPageTemplateCollectionImpl[3];

			array[0] = filterGetByG_LikeN_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
				orderByComparator, true);

			array[1] = layoutPageTemplateCollection;

			array[2] = filterGetByG_LikeN_T_PrevAndNext(
				session, layoutPageTemplateCollection, groupId, name, type,
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

	protected LayoutPageTemplateCollection filterGetByG_LikeN_T_PrevAndNext(
		Session session,
		LayoutPageTemplateCollection layoutPageTemplateCollection, long groupId,
		String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1);
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

		sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2);
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
					LayoutPageTemplateCollectionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutPageTemplateCollectionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, LayoutPageTemplateCollectionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, LayoutPageTemplateCollectionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateCollection)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateCollection> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_LikeN_T(long groupId, String name, int type) {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findByG_LikeN_T(
					groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, int type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_LikeN_T;

			Object[] finderArgs = new Object[] {groupId, name, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2);

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

					queryPos.add(type);

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
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_T(long groupId, String name, int type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LikeN_T(groupId, name, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
				findByG_LikeN_T(groupId, name, type);

			layoutPageTemplateCollections = InlineSQLHelperUtil.filter(
				layoutPageTemplateCollections, groupId);

			return layoutPageTemplateCollections.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

		sb.append(_FINDER_COLUMN_G_LIKEN_T_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_LIKEN_T_NAME_2);
		}

		sb.append(_FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), LayoutPageTemplateCollection.class.getName(),
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

			queryPos.add(type);

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
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_T_NAME_2 =
		"layoutPageTemplateCollection.name LIKE ? AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_T_NAME_3 =
		"(layoutPageTemplateCollection.name IS NULL OR layoutPageTemplateCollection.name LIKE '') AND ";

	private static final String _FINDER_COLUMN_G_LIKEN_T_TYPE_2 =
		"layoutPageTemplateCollection.type = ?";

	private static final String _FINDER_COLUMN_G_LIKEN_T_TYPE_2_SQL =
		"layoutPageTemplateCollection.type_ = ?";

	private FinderPath _finderPathFetchByG_P_N_T;

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_P_N_T(
				groupId, parentLayoutPageTemplateCollectionId, name, type);

		if (layoutPageTemplateCollection == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", parentLayoutPageTemplateCollectionId=");
			sb.append(parentLayoutPageTemplateCollectionId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", type=");
			sb.append(type);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageTemplateCollectionException(sb.toString());
		}

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type) {

		return fetchByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type, true);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, parentLayoutPageTemplateCollectionId, name, type
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_P_N_T, finderArgs, this);
			}

			if (result instanceof LayoutPageTemplateCollection) {
				LayoutPageTemplateCollection layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)result;

				if ((groupId != layoutPageTemplateCollection.getGroupId()) ||
					(parentLayoutPageTemplateCollectionId !=
						layoutPageTemplateCollection.
							getParentLayoutPageTemplateCollectionId()) ||
					!Objects.equals(
						name, layoutPageTemplateCollection.getName()) ||
					(type != layoutPageTemplateCollection.getType())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

				sb.append(_FINDER_COLUMN_G_P_N_T_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_G_P_N_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_N_T_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_P_N_T_NAME_2);
				}

				sb.append(_FINDER_COLUMN_G_P_N_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentLayoutPageTemplateCollectionId);

					if (bindName) {
						queryPos.add(name);
					}

					queryPos.add(type);

					List<LayoutPageTemplateCollection> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_P_N_T, finderArgs, list);
						}
					}
					else {
						LayoutPageTemplateCollection
							layoutPageTemplateCollection = list.get(0);

						result = layoutPageTemplateCollection;

						cacheResult(layoutPageTemplateCollection);
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
				return (LayoutPageTemplateCollection)result;
			}
		}
	}

	/**
	 * Removes the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByG_P_N_T(
				groupId, parentLayoutPageTemplateCollectionId, name, type);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type) {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByG_P_N_T(
				groupId, parentLayoutPageTemplateCollectionId, name, type);

		if (layoutPageTemplateCollection == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_N_T_GROUPID_2 =
		"layoutPageTemplateCollection.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_P_N_T_PARENTLAYOUTPAGETEMPLATECOLLECTIONID_2 =
			"layoutPageTemplateCollection.parentLayoutPageTemplateCollectionId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_T_NAME_2 =
		"layoutPageTemplateCollection.name = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_T_NAME_3 =
		"(layoutPageTemplateCollection.name IS NULL OR layoutPageTemplateCollection.name = '') AND ";

	private static final String _FINDER_COLUMN_G_P_N_T_TYPE_2 =
		"layoutPageTemplateCollection.type = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByERC_G(externalReferenceCode, groupId);

		if (layoutPageTemplateCollection == null) {
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

			throw new NoSuchPageTemplateCollectionException(sb.toString());
		}

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			if (result instanceof LayoutPageTemplateCollection) {
				LayoutPageTemplateCollection layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)result;

				if (!Objects.equals(
						externalReferenceCode,
						layoutPageTemplateCollection.
							getExternalReferenceCode()) ||
					(groupId != layoutPageTemplateCollection.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE);

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

					List<LayoutPageTemplateCollection> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						LayoutPageTemplateCollection
							layoutPageTemplateCollection = list.get(0);

						result = layoutPageTemplateCollection;

						cacheResult(layoutPageTemplateCollection);
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
				return (LayoutPageTemplateCollection)result;
			}
		}
	}

	/**
	 * Removes the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection = findByERC_G(
			externalReferenceCode, groupId);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByERC_G(externalReferenceCode, groupId);

		if (layoutPageTemplateCollection == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"layoutPageTemplateCollection.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(layoutPageTemplateCollection.externalReferenceCode IS NULL OR layoutPageTemplateCollection.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"layoutPageTemplateCollection.groupId = ?";

	public LayoutPageTemplateCollectionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"parentLayoutPageTemplateCollectionId", "parentLPTCollectionId");
		dbColumnNames.put(
			"layoutPageTemplateCollectionKey", "lptCollectionKey");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateCollection.class);

		setModelImplClass(LayoutPageTemplateCollectionImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateCollectionTable.INSTANCE);
	}

	/**
	 * Caches the layout page template collection in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateCollection the layout page template collection
	 */
	@Override
	public void cacheResult(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutPageTemplateCollection.getCtCollectionId())) {

			entityCache.putResult(
				LayoutPageTemplateCollectionImpl.class,
				layoutPageTemplateCollection.getPrimaryKey(),
				layoutPageTemplateCollection);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					layoutPageTemplateCollection.getUuid(),
					layoutPageTemplateCollection.getGroupId()
				},
				layoutPageTemplateCollection);

			finderCache.putResult(
				_finderPathFetchByG_LPTCK_T,
				new Object[] {
					layoutPageTemplateCollection.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionKey(),
					layoutPageTemplateCollection.getType()
				},
				layoutPageTemplateCollection);

			finderCache.putResult(
				_finderPathFetchByG_P_N_T,
				new Object[] {
					layoutPageTemplateCollection.getGroupId(),
					layoutPageTemplateCollection.
						getParentLayoutPageTemplateCollectionId(),
					layoutPageTemplateCollection.getName(),
					layoutPageTemplateCollection.getType()
				},
				layoutPageTemplateCollection);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					layoutPageTemplateCollection.getExternalReferenceCode(),
					layoutPageTemplateCollection.getGroupId()
				},
				layoutPageTemplateCollection);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the layout page template collections in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateCollections the layout page template collections
	 */
	@Override
	public void cacheResult(
		List<LayoutPageTemplateCollection> layoutPageTemplateCollections) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (layoutPageTemplateCollections.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				layoutPageTemplateCollections) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						layoutPageTemplateCollection.getCtCollectionId())) {

				if (entityCache.getResult(
						LayoutPageTemplateCollectionImpl.class,
						layoutPageTemplateCollection.getPrimaryKey()) == null) {

					cacheResult(layoutPageTemplateCollection);
				}
			}
		}
	}

	/**
	 * Clears the cache for all layout page template collections.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LayoutPageTemplateCollectionImpl.class);

		finderCache.clearCache(LayoutPageTemplateCollectionImpl.class);
	}

	/**
	 * Clears the cache for the layout page template collection.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		entityCache.removeResult(
			LayoutPageTemplateCollectionImpl.class,
			layoutPageTemplateCollection);
	}

	@Override
	public void clearCache(
		List<LayoutPageTemplateCollection> layoutPageTemplateCollections) {

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				layoutPageTemplateCollections) {

			entityCache.removeResult(
				LayoutPageTemplateCollectionImpl.class,
				layoutPageTemplateCollection);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(LayoutPageTemplateCollectionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				LayoutPageTemplateCollectionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LayoutPageTemplateCollectionModelImpl
			layoutPageTemplateCollectionModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutPageTemplateCollectionModelImpl.
						getCtCollectionId())) {

			Object[] args = new Object[] {
				layoutPageTemplateCollectionModelImpl.getUuid(),
				layoutPageTemplateCollectionModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args,
				layoutPageTemplateCollectionModelImpl);

			args = new Object[] {
				layoutPageTemplateCollectionModelImpl.getGroupId(),
				layoutPageTemplateCollectionModelImpl.
					getLayoutPageTemplateCollectionKey(),
				layoutPageTemplateCollectionModelImpl.getType()
			};

			finderCache.putResult(
				_finderPathFetchByG_LPTCK_T, args,
				layoutPageTemplateCollectionModelImpl);

			args = new Object[] {
				layoutPageTemplateCollectionModelImpl.getGroupId(),
				layoutPageTemplateCollectionModelImpl.
					getParentLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollectionModelImpl.getName(),
				layoutPageTemplateCollectionModelImpl.getType()
			};

			finderCache.putResult(
				_finderPathFetchByG_P_N_T, args,
				layoutPageTemplateCollectionModelImpl);

			args = new Object[] {
				layoutPageTemplateCollectionModelImpl.
					getExternalReferenceCode(),
				layoutPageTemplateCollectionModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args,
				layoutPageTemplateCollectionModelImpl);
		}
	}

	/**
	 * Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	 *
	 * @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	 * @return the new layout page template collection
	 */
	@Override
	public LayoutPageTemplateCollection create(
		long layoutPageTemplateCollectionId) {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			new LayoutPageTemplateCollectionImpl();

		layoutPageTemplateCollection.setNew(true);
		layoutPageTemplateCollection.setPrimaryKey(
			layoutPageTemplateCollectionId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateCollection.setUuid(uuid);

		layoutPageTemplateCollection.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateCollection;
	}

	/**
	 * Removes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection remove(
			long layoutPageTemplateCollectionId)
		throws NoSuchPageTemplateCollectionException {

		return remove((Serializable)layoutPageTemplateCollectionId);
	}

	/**
	 * Removes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection remove(Serializable primaryKey)
		throws NoSuchPageTemplateCollectionException {

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				(LayoutPageTemplateCollection)session.get(
					LayoutPageTemplateCollectionImpl.class, primaryKey);

			if (layoutPageTemplateCollection == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPageTemplateCollectionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(layoutPageTemplateCollection);
		}
		catch (NoSuchPageTemplateCollectionException noSuchEntityException) {
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
	protected LayoutPageTemplateCollection removeImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPageTemplateCollection)) {
				layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)session.get(
						LayoutPageTemplateCollectionImpl.class,
						layoutPageTemplateCollection.getPrimaryKeyObj());
			}

			if ((layoutPageTemplateCollection != null) &&
				ctPersistenceHelper.isRemove(layoutPageTemplateCollection)) {

				session.delete(layoutPageTemplateCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateCollection != null) {
			clearCache(layoutPageTemplateCollection);
		}

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection updateImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		boolean isNew = layoutPageTemplateCollection.isNew();

		if (!(layoutPageTemplateCollection instanceof
				LayoutPageTemplateCollectionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateCollection.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateCollection);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateCollection proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateCollection implementation " +
					layoutPageTemplateCollection.getClass());
		}

		LayoutPageTemplateCollectionModelImpl
			layoutPageTemplateCollectionModelImpl =
				(LayoutPageTemplateCollectionModelImpl)
					layoutPageTemplateCollection;

		if (Validator.isNull(layoutPageTemplateCollection.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateCollection.setUuid(uuid);
		}

		if (Validator.isNull(
				layoutPageTemplateCollection.getExternalReferenceCode())) {

			layoutPageTemplateCollection.setExternalReferenceCode(
				layoutPageTemplateCollection.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutPageTemplateCollectionModelImpl.
						getColumnOriginalValue("externalReferenceCode"),
					layoutPageTemplateCollection.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId =
						layoutPageTemplateCollection.getCompanyId();

					long groupId = layoutPageTemplateCollection.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layoutPageTemplateCollection.getPrimaryKey();
					}

					try {
						layoutPageTemplateCollection.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								LayoutPageTemplateCollection.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								layoutPageTemplateCollection.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutPageTemplateCollection ercLayoutPageTemplateCollection =
				fetchByERC_G(
					layoutPageTemplateCollection.getExternalReferenceCode(),
					layoutPageTemplateCollection.getGroupId());

			if (isNew) {
				if (ercLayoutPageTemplateCollection != null) {
					throw new DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException(
						"Duplicate layout page template collection with external reference code " +
							layoutPageTemplateCollection.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateCollection.getGroupId());
				}
			}
			else {
				if ((ercLayoutPageTemplateCollection != null) &&
					(layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId() !=
							ercLayoutPageTemplateCollection.
								getLayoutPageTemplateCollectionId())) {

					throw new DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException(
						"Duplicate layout page template collection with external reference code " +
							layoutPageTemplateCollection.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateCollection.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPageTemplateCollection.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPageTemplateCollection.setCreateDate(date);
			}
			else {
				layoutPageTemplateCollection.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateCollectionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPageTemplateCollection.setModifiedDate(date);
			}
			else {
				layoutPageTemplateCollection.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutPageTemplateCollection)) {
				if (!isNew) {
					session.evict(
						LayoutPageTemplateCollectionImpl.class,
						layoutPageTemplateCollection.getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateCollection);
			}
			else {
				layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)session.merge(
						layoutPageTemplateCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LayoutPageTemplateCollectionImpl.class,
			layoutPageTemplateCollectionModelImpl, false, true);

		cacheUniqueFindersCache(layoutPageTemplateCollectionModelImpl);

		if (isNew) {
			layoutPageTemplateCollection.setNew(false);
		}

		layoutPageTemplateCollection.resetOriginalValues();

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByPrimaryKey(
			Serializable primaryKey)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			fetchByPrimaryKey(primaryKey);

		if (layoutPageTemplateCollection == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPageTemplateCollectionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection with the primary key or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByPrimaryKey(
			long layoutPageTemplateCollectionId)
		throws NoSuchPageTemplateCollectionException {

		return findByPrimaryKey((Serializable)layoutPageTemplateCollectionId);
	}

	/**
	 * Returns the layout page template collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout page template collection
	 * @return the layout page template collection, or <code>null</code> if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByPrimaryKey(
		Serializable primaryKey) {

		if (ctPersistenceHelper.isProductionMode(
				LayoutPageTemplateCollection.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			(LayoutPageTemplateCollection)entityCache.getResult(
				LayoutPageTemplateCollectionImpl.class, primaryKey);

		if (layoutPageTemplateCollection != null) {
			return layoutPageTemplateCollection;
		}

		Session session = null;

		try {
			session = openSession();

			layoutPageTemplateCollection =
				(LayoutPageTemplateCollection)session.get(
					LayoutPageTemplateCollectionImpl.class, primaryKey);

			if (layoutPageTemplateCollection != null) {
				cacheResult(layoutPageTemplateCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection, or <code>null</code> if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByPrimaryKey(
		long layoutPageTemplateCollectionId) {

		return fetchByPrimaryKey((Serializable)layoutPageTemplateCollectionId);
	}

	@Override
	public Map<Serializable, LayoutPageTemplateCollection> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				LayoutPageTemplateCollection.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, LayoutPageTemplateCollection> map =
			new HashMap<Serializable, LayoutPageTemplateCollection>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				fetchByPrimaryKey(primaryKey);

			if (layoutPageTemplateCollection != null) {
				map.put(primaryKey, layoutPageTemplateCollection);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						LayoutPageTemplateCollection.class, primaryKey)) {

				LayoutPageTemplateCollection layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)entityCache.getResult(
						LayoutPageTemplateCollectionImpl.class, primaryKey);

				if (layoutPageTemplateCollection == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, layoutPageTemplateCollection);
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

			for (LayoutPageTemplateCollection layoutPageTemplateCollection :
					(List<LayoutPageTemplateCollection>)query.list()) {

				map.put(
					layoutPageTemplateCollection.getPrimaryKeyObj(),
					layoutPageTemplateCollection);

				cacheResult(layoutPageTemplateCollection);
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
	 * Returns all the layout page template collections.
	 *
	 * @return the layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findAll(
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findAll(
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

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

			List<LayoutPageTemplateCollection> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateCollection>)finderCache.getResult(
						finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION;

					sql = sql.concat(
						LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<LayoutPageTemplateCollection>)QueryUtil.list(
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
	 * Removes all the layout page template collections from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				findAll()) {

			remove(layoutPageTemplateCollection);
		}
	}

	/**
	 * Returns the number of layout page template collections.
	 *
	 * @return the number of layout page template collections
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateCollection.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION);

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
		return "layoutPageTemplateCollectionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION;
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
		return LayoutPageTemplateCollectionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPageTemplateCollection";
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
		ctMergeColumnNames.add("parentLPTCollectionId");
		ctMergeColumnNames.add("lptCollectionKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutPageTemplateCollectionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "lptCollectionKey", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "parentLPTCollectionId", "name", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout page template collection persistence.
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

		_finderPathWithPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentLPTCollectionId"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentLPTCollectionId"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentLPTCollectionId"}, false);

		_finderPathWithPaginationFindByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "type_"}, true);

		_finderPathWithoutPaginationFindByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "type_"}, true);

		_finderPathCountByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "type_"}, false);

		_finderPathWithPaginationFindByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentLPTCollectionId", "type_"}, true);

		_finderPathWithoutPaginationFindByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentLPTCollectionId", "type_"}, true);

		_finderPathCountByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentLPTCollectionId", "type_"}, false);

		_finderPathFetchByG_LPTCK_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_LPTCK_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "lptCollectionKey", "type_"}, true);

		_finderPathWithPaginationFindByG_N_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, true);

		_finderPathWithoutPaginationFindByG_N_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, true);

		_finderPathCountByG_N_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, false);

		_finderPathWithPaginationFindByG_LikeN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, true);

		_finderPathWithPaginationCountByG_LikeN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "name", "type_"}, false);

		_finderPathFetchByG_P_N_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "parentLPTCollectionId", "name", "type_"},
			true);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		LayoutPageTemplateCollectionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateCollectionUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateCollectionImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION =
		"SELECT layoutPageTemplateCollection FROM LayoutPageTemplateCollection layoutPageTemplateCollection";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE =
		"SELECT layoutPageTemplateCollection FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final String _SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION =
		"SELECT COUNT(layoutPageTemplateCollection) FROM LayoutPageTemplateCollection layoutPageTemplateCollection";

	private static final String _SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE =
		"SELECT COUNT(layoutPageTemplateCollection) FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"layoutPageTemplateCollection.layoutPageTemplateCollectionId";

	private static final String
		_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE =
			"SELECT DISTINCT {layoutPageTemplateCollection.*} FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {LayoutPageTemplateCollection.*} FROM (SELECT DISTINCT layoutPageTemplateCollection.layoutPageTemplateCollectionId FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN LayoutPageTemplateCollection ON TEMP_TABLE.layoutPageTemplateCollectionId = LayoutPageTemplateCollection.layoutPageTemplateCollectionId";

	private static final String
		_FILTER_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE =
			"SELECT COUNT(DISTINCT layoutPageTemplateCollection.layoutPageTemplateCollectionId) AS COUNT_VALUE FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"layoutPageTemplateCollection";

	private static final String _FILTER_ENTITY_TABLE =
		"LayoutPageTemplateCollection";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"layoutPageTemplateCollection.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"LayoutPageTemplateCollection.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LayoutPageTemplateCollection exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutPageTemplateCollection exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateCollectionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "parentLayoutPageTemplateCollectionId",
			"layoutPageTemplateCollectionKey", "type"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}