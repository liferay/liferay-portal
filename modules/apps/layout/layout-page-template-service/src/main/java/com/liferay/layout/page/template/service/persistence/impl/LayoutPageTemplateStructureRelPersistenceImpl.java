/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelUtil;
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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
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
 * The persistence implementation for the layout page template structure rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutPageTemplateStructureRelPersistence.class)
public class LayoutPageTemplateStructureRelPersistenceImpl
	extends BasePersistenceImpl<LayoutPageTemplateStructureRel>
	implements LayoutPageTemplateStructureRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateStructureRelUtil</code> to access the layout page template structure rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateStructureRelImpl.class.getName();

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
	 * Returns all the layout page template structure rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template structure rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

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

			List<LayoutPageTemplateStructureRel> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateStructureRel>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateStructureRel
							layoutPageTemplateStructureRel : list) {

						if (!uuid.equals(
								layoutPageTemplateStructureRel.getUuid())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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
						LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutPageTemplateStructureRel>)QueryUtil.list(
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
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByUuid_First(uuid, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		List<LayoutPageTemplateStructureRel> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUuid_Last(
			String uuid,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByUuid_Last(uuid, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUuid_Last(
		String uuid,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateStructureRel> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template structure rels before and after the current layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the current layout page template structure rel
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel[] findByUuid_PrevAndNext(
			long layoutPageTemplateStructureRelId, String uuid,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		uuid = Objects.toString(uuid, "");

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByPrimaryKey(layoutPageTemplateStructureRelId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateStructureRel[] array =
				new LayoutPageTemplateStructureRelImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, layoutPageTemplateStructureRel, uuid,
				orderByComparator, true);

			array[1] = layoutPageTemplateStructureRel;

			array[2] = getByUuid_PrevAndNext(
				session, layoutPageTemplateStructureRel, uuid,
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

	protected LayoutPageTemplateStructureRel getByUuid_PrevAndNext(
		Session session,
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel,
		String uuid,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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
			sb.append(LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
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
						layoutPageTemplateStructureRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateStructureRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template structure rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutPageTemplateStructureRel);
		}
	}

	/**
	 * Returns the number of layout page template structure rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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
		"layoutPageTemplateStructureRel.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(layoutPageTemplateStructureRel.uuid IS NULL OR layoutPageTemplateStructureRel.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the layout page template structure rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByUUID_G(uuid, groupId);

		if (layoutPageTemplateStructureRel == null) {
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

			throw new NoSuchPageTemplateStructureRelException(sb.toString());
		}

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Returns the layout page template structure rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout page template structure rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

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

			if (result instanceof LayoutPageTemplateStructureRel) {
				LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)result;

				if (!Objects.equals(
						uuid, layoutPageTemplateStructureRel.getUuid()) ||
					(groupId != layoutPageTemplateStructureRel.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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

					List<LayoutPageTemplateStructureRel> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						LayoutPageTemplateStructureRel
							layoutPageTemplateStructureRel = list.get(0);

						result = layoutPageTemplateStructureRel;

						cacheResult(layoutPageTemplateStructureRel);
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
				return (LayoutPageTemplateStructureRel)result;
			}
		}
	}

	/**
	 * Removes the layout page template structure rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByUUID_G(uuid, groupId);

		return remove(layoutPageTemplateStructureRel);
	}

	/**
	 * Returns the number of layout page template structure rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByUUID_G(uuid, groupId);

		if (layoutPageTemplateStructureRel == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"layoutPageTemplateStructureRel.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(layoutPageTemplateStructureRel.uuid IS NULL OR layoutPageTemplateStructureRel.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"layoutPageTemplateStructureRel.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

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

			List<LayoutPageTemplateStructureRel> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateStructureRel>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateStructureRel
							layoutPageTemplateStructureRel : list) {

						if (!uuid.equals(
								layoutPageTemplateStructureRel.getUuid()) ||
							(companyId !=
								layoutPageTemplateStructureRel.
									getCompanyId())) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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
						LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutPageTemplateStructureRel>)QueryUtil.list(
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
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		List<LayoutPageTemplateStructureRel> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateStructureRel> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template structure rels before and after the current layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the current layout page template structure rel
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel[] findByUuid_C_PrevAndNext(
			long layoutPageTemplateStructureRelId, String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		uuid = Objects.toString(uuid, "");

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByPrimaryKey(layoutPageTemplateStructureRelId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateStructureRel[] array =
				new LayoutPageTemplateStructureRelImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, layoutPageTemplateStructureRel, uuid, companyId,
				orderByComparator, true);

			array[1] = layoutPageTemplateStructureRel;

			array[2] = getByUuid_C_PrevAndNext(
				session, layoutPageTemplateStructureRel, uuid, companyId,
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

	protected LayoutPageTemplateStructureRel getByUuid_C_PrevAndNext(
		Session session,
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel,
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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
			sb.append(LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
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
						layoutPageTemplateStructureRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateStructureRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template structure rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutPageTemplateStructureRel);
		}
	}

	/**
	 * Returns the number of layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

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
		"layoutPageTemplateStructureRel.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(layoutPageTemplateStructureRel.uuid IS NULL OR layoutPageTemplateStructureRel.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"layoutPageTemplateStructureRel.companyId = ?";

	private FinderPath
		_finderPathWithPaginationFindByLayoutPageTemplateStructureId;
	private FinderPath
		_finderPathWithoutPaginationFindByLayoutPageTemplateStructureId;
	private FinderPath _finderPathCountByLayoutPageTemplateStructureId;

	/**
	 * Returns all the layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @return the matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel>
		findByLayoutPageTemplateStructureId(
			long layoutPageTemplateStructureId) {

		return findByLayoutPageTemplateStructureId(
			layoutPageTemplateStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel>
		findByLayoutPageTemplateStructureId(
			long layoutPageTemplateStructureId, int start, int end) {

		return findByLayoutPageTemplateStructureId(
			layoutPageTemplateStructureId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel>
		findByLayoutPageTemplateStructureId(
			long layoutPageTemplateStructureId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRel>
				orderByComparator) {

		return findByLayoutPageTemplateStructureId(
			layoutPageTemplateStructureId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel>
		findByLayoutPageTemplateStructureId(
			long layoutPageTemplateStructureId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
			boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByLayoutPageTemplateStructureId;
					finderArgs = new Object[] {layoutPageTemplateStructureId};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByLayoutPageTemplateStructureId;
				finderArgs = new Object[] {
					layoutPageTemplateStructureId, start, end, orderByComparator
				};
			}

			List<LayoutPageTemplateStructureRel> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateStructureRel>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateStructureRel
							layoutPageTemplateStructureRel : list) {

						if (layoutPageTemplateStructureId !=
								layoutPageTemplateStructureRel.
									getLayoutPageTemplateStructureId()) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

				sb.append(
					_FINDER_COLUMN_LAYOUTPAGETEMPLATESTRUCTUREID_LAYOUTPAGETEMPLATESTRUCTUREID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(layoutPageTemplateStructureId);

					list = (List<LayoutPageTemplateStructureRel>)QueryUtil.list(
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
	 * Returns the first layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
			findByLayoutPageTemplateStructureId_First(
				long layoutPageTemplateStructureId,
				OrderByComparator<LayoutPageTemplateStructureRel>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByLayoutPageTemplateStructureId_First(
				layoutPageTemplateStructureId, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutPageTemplateStructureId=");
		sb.append(layoutPageTemplateStructureId);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
		fetchByLayoutPageTemplateStructureId_First(
			long layoutPageTemplateStructureId,
			OrderByComparator<LayoutPageTemplateStructureRel>
				orderByComparator) {

		List<LayoutPageTemplateStructureRel> list =
			findByLayoutPageTemplateStructureId(
				layoutPageTemplateStructureId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
			findByLayoutPageTemplateStructureId_Last(
				long layoutPageTemplateStructureId,
				OrderByComparator<LayoutPageTemplateStructureRel>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByLayoutPageTemplateStructureId_Last(
				layoutPageTemplateStructureId, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutPageTemplateStructureId=");
		sb.append(layoutPageTemplateStructureId);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
		fetchByLayoutPageTemplateStructureId_Last(
			long layoutPageTemplateStructureId,
			OrderByComparator<LayoutPageTemplateStructureRel>
				orderByComparator) {

		int count = countByLayoutPageTemplateStructureId(
			layoutPageTemplateStructureId);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateStructureRel> list =
			findByLayoutPageTemplateStructureId(
				layoutPageTemplateStructureId, count - 1, count,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template structure rels before and after the current layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the current layout page template structure rel
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel[]
			findByLayoutPageTemplateStructureId_PrevAndNext(
				long layoutPageTemplateStructureRelId,
				long layoutPageTemplateStructureId,
				OrderByComparator<LayoutPageTemplateStructureRel>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByPrimaryKey(layoutPageTemplateStructureRelId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateStructureRel[] array =
				new LayoutPageTemplateStructureRelImpl[3];

			array[0] = getByLayoutPageTemplateStructureId_PrevAndNext(
				session, layoutPageTemplateStructureRel,
				layoutPageTemplateStructureId, orderByComparator, true);

			array[1] = layoutPageTemplateStructureRel;

			array[2] = getByLayoutPageTemplateStructureId_PrevAndNext(
				session, layoutPageTemplateStructureRel,
				layoutPageTemplateStructureId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutPageTemplateStructureRel
		getByLayoutPageTemplateStructureId_PrevAndNext(
			Session session,
			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel,
			long layoutPageTemplateStructureId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

		sb.append(
			_FINDER_COLUMN_LAYOUTPAGETEMPLATESTRUCTUREID_LAYOUTPAGETEMPLATESTRUCTUREID_2);

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
			sb.append(LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(layoutPageTemplateStructureId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateStructureRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateStructureRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template structure rels where layoutPageTemplateStructureId = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 */
	@Override
	public void removeByLayoutPageTemplateStructureId(
		long layoutPageTemplateStructureId) {

		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				findByLayoutPageTemplateStructureId(
					layoutPageTemplateStructureId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layoutPageTemplateStructureRel);
		}
	}

	/**
	 * Returns the number of layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByLayoutPageTemplateStructureId(
		long layoutPageTemplateStructureId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			FinderPath finderPath =
				_finderPathCountByLayoutPageTemplateStructureId;

			Object[] finderArgs = new Object[] {layoutPageTemplateStructureId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

				sb.append(
					_FINDER_COLUMN_LAYOUTPAGETEMPLATESTRUCTUREID_LAYOUTPAGETEMPLATESTRUCTUREID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(layoutPageTemplateStructureId);

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

	private static final String
		_FINDER_COLUMN_LAYOUTPAGETEMPLATESTRUCTUREID_LAYOUTPAGETEMPLATESTRUCTUREID_2 =
			"layoutPageTemplateStructureRel.layoutPageTemplateStructureId = ?";

	private FinderPath _finderPathWithPaginationFindBySegmentsExperienceId;
	private FinderPath _finderPathWithoutPaginationFindBySegmentsExperienceId;
	private FinderPath _finderPathCountBySegmentsExperienceId;

	/**
	 * Returns all the layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @return the matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findBySegmentsExperienceId(
		long segmentsExperienceId) {

		return findBySegmentsExperienceId(
			segmentsExperienceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findBySegmentsExperienceId(
		long segmentsExperienceId, int start, int end) {

		return findBySegmentsExperienceId(
			segmentsExperienceId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findBySegmentsExperienceId(
		long segmentsExperienceId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return findBySegmentsExperienceId(
			segmentsExperienceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findBySegmentsExperienceId(
		long segmentsExperienceId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindBySegmentsExperienceId;
					finderArgs = new Object[] {segmentsExperienceId};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindBySegmentsExperienceId;
				finderArgs = new Object[] {
					segmentsExperienceId, start, end, orderByComparator
				};
			}

			List<LayoutPageTemplateStructureRel> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateStructureRel>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutPageTemplateStructureRel
							layoutPageTemplateStructureRel : list) {

						if (segmentsExperienceId !=
								layoutPageTemplateStructureRel.
									getSegmentsExperienceId()) {

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

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

				sb.append(
					_FINDER_COLUMN_SEGMENTSEXPERIENCEID_SEGMENTSEXPERIENCEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(segmentsExperienceId);

					list = (List<LayoutPageTemplateStructureRel>)QueryUtil.list(
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
	 * Returns the first layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findBySegmentsExperienceId_First(
			long segmentsExperienceId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchBySegmentsExperienceId_First(
				segmentsExperienceId, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("segmentsExperienceId=");
		sb.append(segmentsExperienceId);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchBySegmentsExperienceId_First(
		long segmentsExperienceId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		List<LayoutPageTemplateStructureRel> list = findBySegmentsExperienceId(
			segmentsExperienceId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findBySegmentsExperienceId_Last(
			long segmentsExperienceId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchBySegmentsExperienceId_Last(
				segmentsExperienceId, orderByComparator);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("segmentsExperienceId=");
		sb.append(segmentsExperienceId);

		sb.append("}");

		throw new NoSuchPageTemplateStructureRelException(sb.toString());
	}

	/**
	 * Returns the last layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchBySegmentsExperienceId_Last(
		long segmentsExperienceId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		int count = countBySegmentsExperienceId(segmentsExperienceId);

		if (count == 0) {
			return null;
		}

		List<LayoutPageTemplateStructureRel> list = findBySegmentsExperienceId(
			segmentsExperienceId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout page template structure rels before and after the current layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the current layout page template structure rel
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel[]
			findBySegmentsExperienceId_PrevAndNext(
				long layoutPageTemplateStructureRelId,
				long segmentsExperienceId,
				OrderByComparator<LayoutPageTemplateStructureRel>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByPrimaryKey(layoutPageTemplateStructureRelId);

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateStructureRel[] array =
				new LayoutPageTemplateStructureRelImpl[3];

			array[0] = getBySegmentsExperienceId_PrevAndNext(
				session, layoutPageTemplateStructureRel, segmentsExperienceId,
				orderByComparator, true);

			array[1] = layoutPageTemplateStructureRel;

			array[2] = getBySegmentsExperienceId_PrevAndNext(
				session, layoutPageTemplateStructureRel, segmentsExperienceId,
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

	protected LayoutPageTemplateStructureRel
		getBySegmentsExperienceId_PrevAndNext(
			Session session,
			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel,
			long segmentsExperienceId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

		sb.append(_FINDER_COLUMN_SEGMENTSEXPERIENCEID_SEGMENTSEXPERIENCEID_2);

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
			sb.append(LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(segmentsExperienceId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutPageTemplateStructureRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutPageTemplateStructureRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout page template structure rels where segmentsExperienceId = &#63; from the database.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 */
	@Override
	public void removeBySegmentsExperienceId(long segmentsExperienceId) {
		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				findBySegmentsExperienceId(
					segmentsExperienceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutPageTemplateStructureRel);
		}
	}

	/**
	 * Returns the number of layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countBySegmentsExperienceId(long segmentsExperienceId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			FinderPath finderPath = _finderPathCountBySegmentsExperienceId;

			Object[] finderArgs = new Object[] {segmentsExperienceId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

				sb.append(
					_FINDER_COLUMN_SEGMENTSEXPERIENCEID_SEGMENTSEXPERIENCEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(segmentsExperienceId);

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

	private static final String
		_FINDER_COLUMN_SEGMENTSEXPERIENCEID_SEGMENTSEXPERIENCEID_2 =
			"layoutPageTemplateStructureRel.segmentsExperienceId = ?";

	private FinderPath _finderPathFetchByL_S;

	/**
	 * Returns the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; or throws a <code>NoSuchPageTemplateStructureRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByL_S(
			long layoutPageTemplateStructureId, long segmentsExperienceId)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByL_S(layoutPageTemplateStructureId, segmentsExperienceId);

		if (layoutPageTemplateStructureRel == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("layoutPageTemplateStructureId=");
			sb.append(layoutPageTemplateStructureId);

			sb.append(", segmentsExperienceId=");
			sb.append(segmentsExperienceId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageTemplateStructureRelException(sb.toString());
		}

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Returns the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByL_S(
		long layoutPageTemplateStructureId, long segmentsExperienceId) {

		return fetchByL_S(
			layoutPageTemplateStructureId, segmentsExperienceId, true);
	}

	/**
	 * Returns the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByL_S(
		long layoutPageTemplateStructureId, long segmentsExperienceId,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					layoutPageTemplateStructureId, segmentsExperienceId
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByL_S, finderArgs, this);
			}

			if (result instanceof LayoutPageTemplateStructureRel) {
				LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)result;

				if ((layoutPageTemplateStructureId !=
						layoutPageTemplateStructureRel.
							getLayoutPageTemplateStructureId()) ||
					(segmentsExperienceId !=
						layoutPageTemplateStructureRel.
							getSegmentsExperienceId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE);

				sb.append(_FINDER_COLUMN_L_S_LAYOUTPAGETEMPLATESTRUCTUREID_2);

				sb.append(_FINDER_COLUMN_L_S_SEGMENTSEXPERIENCEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(layoutPageTemplateStructureId);

					queryPos.add(segmentsExperienceId);

					List<LayoutPageTemplateStructureRel> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByL_S, finderArgs, list);
						}
					}
					else {
						LayoutPageTemplateStructureRel
							layoutPageTemplateStructureRel = list.get(0);

						result = layoutPageTemplateStructureRel;

						cacheResult(layoutPageTemplateStructureRel);
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
				return (LayoutPageTemplateStructureRel)result;
			}
		}
	}

	/**
	 * Removes the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the layout page template structure rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRel removeByL_S(
			long layoutPageTemplateStructureId, long segmentsExperienceId)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByL_S(layoutPageTemplateStructureId, segmentsExperienceId);

		return remove(layoutPageTemplateStructureRel);
	}

	/**
	 * Returns the number of layout page template structure rels where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByL_S(
		long layoutPageTemplateStructureId, long segmentsExperienceId) {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByL_S(layoutPageTemplateStructureId, segmentsExperienceId);

		if (layoutPageTemplateStructureRel == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_L_S_LAYOUTPAGETEMPLATESTRUCTUREID_2 =
			"layoutPageTemplateStructureRel.layoutPageTemplateStructureId = ? AND ";

	private static final String _FINDER_COLUMN_L_S_SEGMENTSEXPERIENCEID_2 =
		"layoutPageTemplateStructureRel.segmentsExperienceId = ?";

	public LayoutPageTemplateStructureRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"layoutPageTemplateStructureRelId", "lPageTemplateStructureRelId");
		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateStructureRel.class);

		setModelImplClass(LayoutPageTemplateStructureRelImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateStructureRelTable.INSTANCE);
	}

	/**
	 * Caches the layout page template structure rel in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateStructureRel the layout page template structure rel
	 */
	@Override
	public void cacheResult(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutPageTemplateStructureRel.getCtCollectionId())) {

			entityCache.putResult(
				LayoutPageTemplateStructureRelImpl.class,
				layoutPageTemplateStructureRel.getPrimaryKey(),
				layoutPageTemplateStructureRel);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					layoutPageTemplateStructureRel.getUuid(),
					layoutPageTemplateStructureRel.getGroupId()
				},
				layoutPageTemplateStructureRel);

			finderCache.putResult(
				_finderPathFetchByL_S,
				new Object[] {
					layoutPageTemplateStructureRel.
						getLayoutPageTemplateStructureId(),
					layoutPageTemplateStructureRel.getSegmentsExperienceId()
				},
				layoutPageTemplateStructureRel);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the layout page template structure rels in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateStructureRels the layout page template structure rels
	 */
	@Override
	public void cacheResult(
		List<LayoutPageTemplateStructureRel> layoutPageTemplateStructureRels) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (layoutPageTemplateStructureRels.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				layoutPageTemplateStructureRels) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						layoutPageTemplateStructureRel.getCtCollectionId())) {

				LayoutPageTemplateStructureRel
					cachedLayoutPageTemplateStructureRel =
						(LayoutPageTemplateStructureRel)entityCache.getResult(
							LayoutPageTemplateStructureRelImpl.class,
							layoutPageTemplateStructureRel.getPrimaryKey());

				if (cachedLayoutPageTemplateStructureRel == null) {
					cacheResult(layoutPageTemplateStructureRel);
				}
				else {
					LayoutPageTemplateStructureRelModelImpl
						layoutPageTemplateStructureRelModelImpl =
							(LayoutPageTemplateStructureRelModelImpl)
								layoutPageTemplateStructureRel;
					LayoutPageTemplateStructureRelModelImpl
						cachedLayoutPageTemplateStructureRelModelImpl =
							(LayoutPageTemplateStructureRelModelImpl)
								cachedLayoutPageTemplateStructureRel;

					layoutPageTemplateStructureRelModelImpl.setDataJSONObject(
						cachedLayoutPageTemplateStructureRelModelImpl.
							getDataJSONObject());
				}
			}
		}
	}

	/**
	 * Clears the cache for all layout page template structure rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LayoutPageTemplateStructureRelImpl.class);

		finderCache.clearCache(LayoutPageTemplateStructureRelImpl.class);
	}

	/**
	 * Clears the cache for the layout page template structure rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		entityCache.removeResult(
			LayoutPageTemplateStructureRelImpl.class,
			layoutPageTemplateStructureRel);
	}

	@Override
	public void clearCache(
		List<LayoutPageTemplateStructureRel> layoutPageTemplateStructureRels) {

		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				layoutPageTemplateStructureRels) {

			entityCache.removeResult(
				LayoutPageTemplateStructureRelImpl.class,
				layoutPageTemplateStructureRel);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(LayoutPageTemplateStructureRelImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				LayoutPageTemplateStructureRelImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LayoutPageTemplateStructureRelModelImpl
			layoutPageTemplateStructureRelModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutPageTemplateStructureRelModelImpl.
						getCtCollectionId())) {

			Object[] args = new Object[] {
				layoutPageTemplateStructureRelModelImpl.getUuid(),
				layoutPageTemplateStructureRelModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args,
				layoutPageTemplateStructureRelModelImpl);

			args = new Object[] {
				layoutPageTemplateStructureRelModelImpl.
					getLayoutPageTemplateStructureId(),
				layoutPageTemplateStructureRelModelImpl.
					getSegmentsExperienceId()
			};

			finderCache.putResult(
				_finderPathFetchByL_S, args,
				layoutPageTemplateStructureRelModelImpl);
		}
	}

	/**
	 * Creates a new layout page template structure rel with the primary key. Does not add the layout page template structure rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key for the new layout page template structure rel
	 * @return the new layout page template structure rel
	 */
	@Override
	public LayoutPageTemplateStructureRel create(
		long layoutPageTemplateStructureRelId) {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			new LayoutPageTemplateStructureRelImpl();

		layoutPageTemplateStructureRel.setNew(true);
		layoutPageTemplateStructureRel.setPrimaryKey(
			layoutPageTemplateStructureRelId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateStructureRel.setUuid(uuid);

		layoutPageTemplateStructureRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Removes the layout page template structure rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel remove(
			long layoutPageTemplateStructureRelId)
		throws NoSuchPageTemplateStructureRelException {

		return remove((Serializable)layoutPageTemplateStructureRelId);
	}

	/**
	 * Removes the layout page template structure rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel remove(Serializable primaryKey)
		throws NoSuchPageTemplateStructureRelException {

		Session session = null;

		try {
			session = openSession();

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				(LayoutPageTemplateStructureRel)session.get(
					LayoutPageTemplateStructureRelImpl.class, primaryKey);

			if (layoutPageTemplateStructureRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPageTemplateStructureRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(layoutPageTemplateStructureRel);
		}
		catch (NoSuchPageTemplateStructureRelException noSuchEntityException) {
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
	protected LayoutPageTemplateStructureRel removeImpl(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPageTemplateStructureRel)) {
				layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)session.get(
						LayoutPageTemplateStructureRelImpl.class,
						layoutPageTemplateStructureRel.getPrimaryKeyObj());
			}

			if ((layoutPageTemplateStructureRel != null) &&
				ctPersistenceHelper.isRemove(layoutPageTemplateStructureRel)) {

				session.delete(layoutPageTemplateStructureRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateStructureRel != null) {
			clearCache(layoutPageTemplateStructureRel);
		}

		return layoutPageTemplateStructureRel;
	}

	@Override
	public LayoutPageTemplateStructureRel updateImpl(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		boolean isNew = layoutPageTemplateStructureRel.isNew();

		if (!(layoutPageTemplateStructureRel instanceof
				LayoutPageTemplateStructureRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateStructureRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateStructureRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateStructureRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateStructureRel implementation " +
					layoutPageTemplateStructureRel.getClass());
		}

		LayoutPageTemplateStructureRelModelImpl
			layoutPageTemplateStructureRelModelImpl =
				(LayoutPageTemplateStructureRelModelImpl)
					layoutPageTemplateStructureRel;

		if (Validator.isNull(layoutPageTemplateStructureRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateStructureRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPageTemplateStructureRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPageTemplateStructureRel.setCreateDate(date);
			}
			else {
				layoutPageTemplateStructureRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateStructureRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPageTemplateStructureRel.setModifiedDate(date);
			}
			else {
				layoutPageTemplateStructureRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutPageTemplateStructureRel)) {
				if (!isNew) {
					session.evict(
						LayoutPageTemplateStructureRelImpl.class,
						layoutPageTemplateStructureRel.getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateStructureRel);
			}
			else {
				layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)session.merge(
						layoutPageTemplateStructureRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LayoutPageTemplateStructureRelImpl.class,
			layoutPageTemplateStructureRelModelImpl, false, true);

		cacheUniqueFindersCache(layoutPageTemplateStructureRelModelImpl);

		if (isNew) {
			layoutPageTemplateStructureRel.setNew(false);
		}

		layoutPageTemplateStructureRel.resetOriginalValues();

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Returns the layout page template structure rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout page template structure rel
	 * @return the layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByPrimaryKey(
			Serializable primaryKey)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			fetchByPrimaryKey(primaryKey);

		if (layoutPageTemplateStructureRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPageTemplateStructureRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Returns the layout page template structure rel with the primary key or throws a <code>NoSuchPageTemplateStructureRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByPrimaryKey(
			long layoutPageTemplateStructureRelId)
		throws NoSuchPageTemplateStructureRelException {

		return findByPrimaryKey((Serializable)layoutPageTemplateStructureRelId);
	}

	/**
	 * Returns the layout page template structure rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout page template structure rel
	 * @return the layout page template structure rel, or <code>null</code> if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByPrimaryKey(
		Serializable primaryKey) {

		if (ctPersistenceHelper.isProductionMode(
				LayoutPageTemplateStructureRel.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			(LayoutPageTemplateStructureRel)entityCache.getResult(
				LayoutPageTemplateStructureRelImpl.class, primaryKey);

		if (layoutPageTemplateStructureRel != null) {
			return layoutPageTemplateStructureRel;
		}

		Session session = null;

		try {
			session = openSession();

			layoutPageTemplateStructureRel =
				(LayoutPageTemplateStructureRel)session.get(
					LayoutPageTemplateStructureRelImpl.class, primaryKey);

			if (layoutPageTemplateStructureRel != null) {
				cacheResult(layoutPageTemplateStructureRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Returns the layout page template structure rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel, or <code>null</code> if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByPrimaryKey(
		long layoutPageTemplateStructureRelId) {

		return fetchByPrimaryKey(
			(Serializable)layoutPageTemplateStructureRelId);
	}

	@Override
	public Map<Serializable, LayoutPageTemplateStructureRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				LayoutPageTemplateStructureRel.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, LayoutPageTemplateStructureRel> map =
			new HashMap<Serializable, LayoutPageTemplateStructureRel>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				fetchByPrimaryKey(primaryKey);

			if (layoutPageTemplateStructureRel != null) {
				map.put(primaryKey, layoutPageTemplateStructureRel);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						LayoutPageTemplateStructureRel.class, primaryKey)) {

				LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)entityCache.getResult(
						LayoutPageTemplateStructureRelImpl.class, primaryKey);

				if (layoutPageTemplateStructureRel == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, layoutPageTemplateStructureRel);
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

			for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
					(List<LayoutPageTemplateStructureRel>)query.list()) {

				map.put(
					layoutPageTemplateStructureRel.getPrimaryKeyObj(),
					layoutPageTemplateStructureRel);

				cacheResult(layoutPageTemplateStructureRel);
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
	 * Returns all the layout page template structure rels.
	 *
	 * @return the layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template structure rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @return the range of layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findAll(
		int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findAll(
		int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

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

			List<LayoutPageTemplateStructureRel> list = null;

			if (useFinderCache) {
				list =
					(List<LayoutPageTemplateStructureRel>)finderCache.getResult(
						finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL;

					sql = sql.concat(
						LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<LayoutPageTemplateStructureRel>)QueryUtil.list(
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
	 * Removes all the layout page template structure rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				findAll()) {

			remove(layoutPageTemplateStructureRel);
		}
	}

	/**
	 * Returns the number of layout page template structure rels.
	 *
	 * @return the number of layout page template structure rels
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutPageTemplateStructureRel.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL);

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
		return "lPageTemplateStructureRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL;
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
		return LayoutPageTemplateStructureRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPageTemplateStructureRel";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("layoutPageTemplateStructureId");
		ctMergeColumnNames.add("segmentsExperienceId");
		ctMergeColumnNames.add("data_");
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
			CTColumnResolutionType.PK,
			Collections.singleton("lPageTemplateStructureRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"layoutPageTemplateStructureId", "segmentsExperienceId"
			});
	}

	/**
	 * Initializes the layout page template structure rel persistence.
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

		_finderPathWithPaginationFindByLayoutPageTemplateStructureId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findByLayoutPageTemplateStructureId",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"layoutPageTemplateStructureId"}, true);

		_finderPathWithoutPaginationFindByLayoutPageTemplateStructureId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByLayoutPageTemplateStructureId",
				new String[] {Long.class.getName()},
				new String[] {"layoutPageTemplateStructureId"}, true);

		_finderPathCountByLayoutPageTemplateStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByLayoutPageTemplateStructureId",
			new String[] {Long.class.getName()},
			new String[] {"layoutPageTemplateStructureId"}, false);

		_finderPathWithPaginationFindBySegmentsExperienceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findBySegmentsExperienceId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"segmentsExperienceId"}, true);

		_finderPathWithoutPaginationFindBySegmentsExperienceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findBySegmentsExperienceId", new String[] {Long.class.getName()},
			new String[] {"segmentsExperienceId"}, true);

		_finderPathCountBySegmentsExperienceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countBySegmentsExperienceId", new String[] {Long.class.getName()},
			new String[] {"segmentsExperienceId"}, false);

		_finderPathFetchByL_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByL_S",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {
				"layoutPageTemplateStructureId", "segmentsExperienceId"
			},
			true);

		LayoutPageTemplateStructureRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateStructureRelUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateStructureRelImpl.class.getName());
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

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL =
		"SELECT layoutPageTemplateStructureRel FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel";

	private static final String
		_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE =
			"SELECT layoutPageTemplateStructureRel FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel WHERE ";

	private static final String _SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL =
		"SELECT COUNT(layoutPageTemplateStructureRel) FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel";

	private static final String
		_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE =
			"SELECT COUNT(layoutPageTemplateStructureRel) FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"layoutPageTemplateStructureRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LayoutPageTemplateStructureRel exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutPageTemplateStructureRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "layoutPageTemplateStructureRelId", "data"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}