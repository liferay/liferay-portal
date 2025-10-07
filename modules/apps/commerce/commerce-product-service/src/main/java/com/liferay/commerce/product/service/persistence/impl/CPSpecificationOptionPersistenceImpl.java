/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPSpecificationOptionExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionException;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CPSpecificationOptionTable;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionImpl;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionPersistence;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the cp specification option service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPSpecificationOptionPersistence.class)
public class CPSpecificationOptionPersistenceImpl
	extends BasePersistenceImpl<CPSpecificationOption>
	implements CPSpecificationOptionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPSpecificationOptionUtil</code> to access the cp specification option persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPSpecificationOptionImpl.class.getName();

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
	 * Returns all the cp specification options where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp specification options where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

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

			List<CPSpecificationOption> list = null;

			if (useFinderCache) {
				list = (List<CPSpecificationOption>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPSpecificationOption cpSpecificationOption : list) {
						if (!uuid.equals(cpSpecificationOption.getUuid())) {
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

				sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

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
					sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
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

					list = (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the first cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByUuid_First(
			String uuid,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByUuid_First(
			uuid, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the first cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByUuid_First(
		String uuid,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		List<CPSpecificationOption> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByUuid_Last(
			String uuid,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByUuid_Last(
			uuid, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the last cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByUuid_Last(
		String uuid,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CPSpecificationOption> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp specification options before and after the current cp specification option in the ordered set where uuid = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] findByUuid_PrevAndNext(
			long CPSpecificationOptionId, String uuid,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		uuid = Objects.toString(uuid, "");

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, cpSpecificationOption, uuid, orderByComparator, true);

			array[1] = cpSpecificationOption;

			array[2] = getByUuid_PrevAndNext(
				session, cpSpecificationOption, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPSpecificationOption getByUuid_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		String uuid, OrderByComparator<CPSpecificationOption> orderByComparator,
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

		sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

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
			sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
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
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the cp specification options that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid(
		String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid(uuid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid(
					uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			return (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the cp specification options before and after the current cp specification option in the ordered set of cp specification options that the user has permission to view where uuid = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] filterFindByUuid_PrevAndNext(
			long CPSpecificationOptionId, String uuid,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid_PrevAndNext(
				CPSpecificationOptionId, uuid, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = filterGetByUuid_PrevAndNext(
				session, cpSpecificationOption, uuid, orderByComparator, true);

			array[1] = cpSpecificationOption;

			array[2] = filterGetByUuid_PrevAndNext(
				session, cpSpecificationOption, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CPSpecificationOption filterGetByUuid_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		String uuid, OrderByComparator<CPSpecificationOption> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp specification options where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CPSpecificationOption cpSpecificationOption :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpSpecificationOption);
		}
	}

	/**
	 * Returns the number of cp specification options where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

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

	/**
	 * Returns the number of cp specification options that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUuid(uuid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CPSpecificationOption> cpSpecificationOptions = findByUuid(
				uuid);

			cpSpecificationOptions = InlineSQLHelperUtil.filter(
				cpSpecificationOptions);

			return cpSpecificationOptions.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"cpSpecificationOption.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(cpSpecificationOption.uuid IS NULL OR cpSpecificationOption.uuid = '')";

	private static final String _FINDER_COLUMN_UUID_UUID_2_SQL =
		"cpSpecificationOption.uuid_ = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3_SQL =
		"(cpSpecificationOption.uuid_ IS NULL OR cpSpecificationOption.uuid_ = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

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

			List<CPSpecificationOption> list = null;

			if (useFinderCache) {
				list = (List<CPSpecificationOption>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPSpecificationOption cpSpecificationOption : list) {
						if (!uuid.equals(cpSpecificationOption.getUuid()) ||
							(companyId !=
								cpSpecificationOption.getCompanyId())) {

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

				sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

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
					sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
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

					list = (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the first cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the first cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		List<CPSpecificationOption> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the last cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CPSpecificationOption> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp specification options before and after the current cp specification option in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] findByUuid_C_PrevAndNext(
			long CPSpecificationOptionId, String uuid, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		uuid = Objects.toString(uuid, "");

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, cpSpecificationOption, uuid, companyId,
				orderByComparator, true);

			array[1] = cpSpecificationOption;

			array[2] = getByUuid_C_PrevAndNext(
				session, cpSpecificationOption, uuid, companyId,
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

	protected CPSpecificationOption getByUuid_C_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		String uuid, long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator,
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

		sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

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
			sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
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
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the cp specification options that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid_C(
		String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C(uuid, companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

			return (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the cp specification options before and after the current cp specification option in the ordered set of cp specification options that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] filterFindByUuid_C_PrevAndNext(
			long CPSpecificationOptionId, String uuid, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C_PrevAndNext(
				CPSpecificationOptionId, uuid, companyId, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = filterGetByUuid_C_PrevAndNext(
				session, cpSpecificationOption, uuid, companyId,
				orderByComparator, true);

			array[1] = cpSpecificationOption;

			array[2] = filterGetByUuid_C_PrevAndNext(
				session, cpSpecificationOption, uuid, companyId,
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

	protected CPSpecificationOption filterGetByUuid_C_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		String uuid, long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp specification options where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CPSpecificationOption cpSpecificationOption :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpSpecificationOption);
		}
	}

	/**
	 * Returns the number of cp specification options where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

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

	/**
	 * Returns the number of cp specification options that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByUuid_C(uuid, companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CPSpecificationOption> cpSpecificationOptions = findByUuid_C(
				uuid, companyId);

			cpSpecificationOptions = InlineSQLHelperUtil.filter(
				cpSpecificationOptions);

			return cpSpecificationOptions.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"cpSpecificationOption.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(cpSpecificationOption.uuid IS NULL OR cpSpecificationOption.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_2_SQL =
		"cpSpecificationOption.uuid_ = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3_SQL =
		"(cpSpecificationOption.uuid_ IS NULL OR cpSpecificationOption.uuid_ = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"cpSpecificationOption.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the cp specification options where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp specification options where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

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

			List<CPSpecificationOption> list = null;

			if (useFinderCache) {
				list = (List<CPSpecificationOption>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPSpecificationOption cpSpecificationOption : list) {
						if (companyId != cpSpecificationOption.getCompanyId()) {
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

				sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the first cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByCompanyId_First(
			long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the first cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		List<CPSpecificationOption> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByCompanyId_Last(
			long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the last cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CPSpecificationOption> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp specification options before and after the current cp specification option in the ordered set where companyId = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] findByCompanyId_PrevAndNext(
			long CPSpecificationOptionId, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, cpSpecificationOption, companyId, orderByComparator,
				true);

			array[1] = cpSpecificationOption;

			array[2] = getByCompanyId_PrevAndNext(
				session, cpSpecificationOption, companyId, orderByComparator,
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

	protected CPSpecificationOption getByCompanyId_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator,
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

		sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

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
			sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
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
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the cp specification options that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the cp specification options before and after the current cp specification option in the ordered set of cp specification options that the user has permission to view where companyId = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] filterFindByCompanyId_PrevAndNext(
			long CPSpecificationOptionId, long companyId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				CPSpecificationOptionId, companyId, orderByComparator);
		}

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, cpSpecificationOption, companyId, orderByComparator,
				true);

			array[1] = cpSpecificationOption;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, cpSpecificationOption, companyId, orderByComparator,
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

	protected CPSpecificationOption filterGetByCompanyId_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		long companyId,
		OrderByComparator<CPSpecificationOption> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp specification options where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CPSpecificationOption cpSpecificationOption :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cpSpecificationOption);
		}
	}

	/**
	 * Returns the number of cp specification options where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

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

	/**
	 * Returns the number of cp specification options that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CPSpecificationOption> cpSpecificationOptions =
				findByCompanyId(companyId);

			cpSpecificationOptions = InlineSQLHelperUtil.filter(
				cpSpecificationOptions);

			return cpSpecificationOptions.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"cpSpecificationOption.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCPOptionCategoryId;
	private FinderPath _finderPathWithoutPaginationFindByCPOptionCategoryId;
	private FinderPath _finderPathCountByCPOptionCategoryId;

	/**
	 * Returns all the cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCPOptionCategoryId(
		long CPOptionCategoryId) {

		return findByCPOptionCategoryId(
			CPOptionCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end) {

		return findByCPOptionCategoryId(CPOptionCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return findByCPOptionCategoryId(
			CPOptionCategoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCPOptionCategoryId;
					finderArgs = new Object[] {CPOptionCategoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCPOptionCategoryId;
				finderArgs = new Object[] {
					CPOptionCategoryId, start, end, orderByComparator
				};
			}

			List<CPSpecificationOption> list = null;

			if (useFinderCache) {
				list = (List<CPSpecificationOption>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPSpecificationOption cpSpecificationOption : list) {
						if (CPOptionCategoryId !=
								cpSpecificationOption.getCPOptionCategoryId()) {

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

				sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

				sb.append(
					_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPOptionCategoryId);

					list = (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the first cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByCPOptionCategoryId_First(
			long CPOptionCategoryId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption =
			fetchByCPOptionCategoryId_First(
				CPOptionCategoryId, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPOptionCategoryId=");
		sb.append(CPOptionCategoryId);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the first cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByCPOptionCategoryId_First(
		long CPOptionCategoryId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		List<CPSpecificationOption> list = findByCPOptionCategoryId(
			CPOptionCategoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByCPOptionCategoryId_Last(
			long CPOptionCategoryId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption =
			fetchByCPOptionCategoryId_Last(
				CPOptionCategoryId, orderByComparator);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPOptionCategoryId=");
		sb.append(CPOptionCategoryId);

		sb.append("}");

		throw new NoSuchCPSpecificationOptionException(sb.toString());
	}

	/**
	 * Returns the last cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByCPOptionCategoryId_Last(
		long CPOptionCategoryId,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		int count = countByCPOptionCategoryId(CPOptionCategoryId);

		if (count == 0) {
			return null;
		}

		List<CPSpecificationOption> list = findByCPOptionCategoryId(
			CPOptionCategoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp specification options before and after the current cp specification option in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] findByCPOptionCategoryId_PrevAndNext(
			long CPSpecificationOptionId, long CPOptionCategoryId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = getByCPOptionCategoryId_PrevAndNext(
				session, cpSpecificationOption, CPOptionCategoryId,
				orderByComparator, true);

			array[1] = cpSpecificationOption;

			array[2] = getByCPOptionCategoryId_PrevAndNext(
				session, cpSpecificationOption, CPOptionCategoryId,
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

	protected CPSpecificationOption getByCPOptionCategoryId_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		long CPOptionCategoryId,
		OrderByComparator<CPSpecificationOption> orderByComparator,
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

		sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

		sb.append(_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2);

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
			sb.append(CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPOptionCategoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the cp specification options that the user has permission to view where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCPOptionCategoryId(
		long CPOptionCategoryId) {

		return filterFindByCPOptionCategoryId(
			CPOptionCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options that the user has permission to view where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end) {

		return filterFindByCPOptionCategoryId(
			CPOptionCategoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options that the user has permissions to view where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp specification options that the user has permission to view
	 */
	@Override
	public List<CPSpecificationOption> filterFindByCPOptionCategoryId(
		long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByCPOptionCategoryId(
				CPOptionCategoryId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCPOptionCategoryId(
					CPOptionCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(CPOptionCategoryId);

			return (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Returns the cp specification options before and after the current cp specification option in the ordered set of cp specification options that the user has permission to view where CPOptionCategoryId = &#63;.
	 *
	 * @param CPSpecificationOptionId the primary key of the current cp specification option
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption[] filterFindByCPOptionCategoryId_PrevAndNext(
			long CPSpecificationOptionId, long CPOptionCategoryId,
			OrderByComparator<CPSpecificationOption> orderByComparator)
		throws NoSuchCPSpecificationOptionException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByCPOptionCategoryId_PrevAndNext(
				CPSpecificationOptionId, CPOptionCategoryId, orderByComparator);
		}

		CPSpecificationOption cpSpecificationOption = findByPrimaryKey(
			CPSpecificationOptionId);

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption[] array = new CPSpecificationOptionImpl[3];

			array[0] = filterGetByCPOptionCategoryId_PrevAndNext(
				session, cpSpecificationOption, CPOptionCategoryId,
				orderByComparator, true);

			array[1] = cpSpecificationOption;

			array[2] = filterGetByCPOptionCategoryId_PrevAndNext(
				session, cpSpecificationOption, CPOptionCategoryId,
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

	protected CPSpecificationOption filterGetByCPOptionCategoryId_PrevAndNext(
		Session session, CPSpecificationOption cpSpecificationOption,
		long CPOptionCategoryId,
		OrderByComparator<CPSpecificationOption> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2);
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
					CPSpecificationOptionModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CPSpecificationOptionModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CPSpecificationOptionImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CPSpecificationOptionImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(CPOptionCategoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpSpecificationOption)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPSpecificationOption> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp specification options where CPOptionCategoryId = &#63; from the database.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 */
	@Override
	public void removeByCPOptionCategoryId(long CPOptionCategoryId) {
		for (CPSpecificationOption cpSpecificationOption :
				findByCPOptionCategoryId(
					CPOptionCategoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpSpecificationOption);
		}
	}

	/**
	 * Returns the number of cp specification options where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByCPOptionCategoryId(long CPOptionCategoryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			FinderPath finderPath = _finderPathCountByCPOptionCategoryId;

			Object[] finderArgs = new Object[] {CPOptionCategoryId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

				sb.append(
					_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPOptionCategoryId);

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
	 * Returns the number of cp specification options that the user has permission to view where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the number of matching cp specification options that the user has permission to view
	 */
	@Override
	public int filterCountByCPOptionCategoryId(long CPOptionCategoryId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByCPOptionCategoryId(CPOptionCategoryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CPSpecificationOption> cpSpecificationOptions =
				findByCPOptionCategoryId(CPOptionCategoryId);

			cpSpecificationOptions = InlineSQLHelperUtil.filter(
				cpSpecificationOptions);

			return cpSpecificationOptions.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE);

		sb.append(_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CPSpecificationOption.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(CPOptionCategoryId);

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

	private static final String
		_FINDER_COLUMN_CPOPTIONCATEGORYID_CPOPTIONCATEGORYID_2 =
			"cpSpecificationOption.CPOptionCategoryId = ?";

	private FinderPath _finderPathFetchByC_K;

	/**
	 * Returns the cp specification option where companyId = &#63; and key = &#63; or throws a <code>NoSuchCPSpecificationOptionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByC_K(long companyId, String key)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByC_K(
			companyId, key);

		if (cpSpecificationOption == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", key=");
			sb.append(key);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCPSpecificationOptionException(sb.toString());
		}

		return cpSpecificationOption;
	}

	/**
	 * Returns the cp specification option where companyId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByC_K(long companyId, String key) {
		return fetchByC_K(companyId, key, true);
	}

	/**
	 * Returns the cp specification option where companyId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByC_K(
		long companyId, String key, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			key = Objects.toString(key, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {companyId, key};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByC_K, finderArgs, this);
			}

			if (result instanceof CPSpecificationOption) {
				CPSpecificationOption cpSpecificationOption =
					(CPSpecificationOption)result;

				if ((companyId != cpSpecificationOption.getCompanyId()) ||
					!Objects.equals(key, cpSpecificationOption.getKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

				sb.append(_FINDER_COLUMN_C_K_COMPANYID_2);

				boolean bindKey = false;

				if (key.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_K_KEY_3);
				}
				else {
					bindKey = true;

					sb.append(_FINDER_COLUMN_C_K_KEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindKey) {
						queryPos.add(key);
					}

					List<CPSpecificationOption> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByC_K, finderArgs, list);
						}
					}
					else {
						CPSpecificationOption cpSpecificationOption = list.get(
							0);

						result = cpSpecificationOption;

						cacheResult(cpSpecificationOption);
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
				return (CPSpecificationOption)result;
			}
		}
	}

	/**
	 * Removes the cp specification option where companyId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the cp specification option that was removed
	 */
	@Override
	public CPSpecificationOption removeByC_K(long companyId, String key)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = findByC_K(companyId, key);

		return remove(cpSpecificationOption);
	}

	/**
	 * Returns the number of cp specification options where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByC_K(long companyId, String key) {
		CPSpecificationOption cpSpecificationOption = fetchByC_K(
			companyId, key);

		if (cpSpecificationOption == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_K_COMPANYID_2 =
		"cpSpecificationOption.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_K_KEY_2 =
		"cpSpecificationOption.key = ?";

	private static final String _FINDER_COLUMN_C_K_KEY_3 =
		"(cpSpecificationOption.key IS NULL OR cpSpecificationOption.key = '')";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the cp specification option where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPSpecificationOptionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByERC_C(
			externalReferenceCode, companyId);

		if (cpSpecificationOption == null) {
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

			throw new NoSuchCPSpecificationOptionException(sb.toString());
		}

		return cpSpecificationOption;
	}

	/**
	 * Returns the cp specification option where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the cp specification option where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Override
	public CPSpecificationOption fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

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

			if (result instanceof CPSpecificationOption) {
				CPSpecificationOption cpSpecificationOption =
					(CPSpecificationOption)result;

				if (!Objects.equals(
						externalReferenceCode,
						cpSpecificationOption.getExternalReferenceCode()) ||
					(companyId != cpSpecificationOption.getCompanyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE);

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

					List<CPSpecificationOption> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_C, finderArgs, list);
						}
					}
					else {
						CPSpecificationOption cpSpecificationOption = list.get(
							0);

						result = cpSpecificationOption;

						cacheResult(cpSpecificationOption);
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
				return (CPSpecificationOption)result;
			}
		}
	}

	/**
	 * Removes the cp specification option where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp specification option that was removed
	 */
	@Override
	public CPSpecificationOption removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpSpecificationOption);
	}

	/**
	 * Returns the number of cp specification options where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp specification options
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CPSpecificationOption cpSpecificationOption = fetchByERC_C(
			externalReferenceCode, companyId);

		if (cpSpecificationOption == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"cpSpecificationOption.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(cpSpecificationOption.externalReferenceCode IS NULL OR cpSpecificationOption.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"cpSpecificationOption.companyId = ?";

	public CPSpecificationOptionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPSpecificationOption.class);

		setModelImplClass(CPSpecificationOptionImpl.class);
		setModelPKClass(long.class);

		setTable(CPSpecificationOptionTable.INSTANCE);
	}

	/**
	 * Caches the cp specification option in the entity cache if it is enabled.
	 *
	 * @param cpSpecificationOption the cp specification option
	 */
	@Override
	public void cacheResult(CPSpecificationOption cpSpecificationOption) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpSpecificationOption.getCtCollectionId())) {

			entityCache.putResult(
				CPSpecificationOptionImpl.class,
				cpSpecificationOption.getPrimaryKey(), cpSpecificationOption);

			finderCache.putResult(
				_finderPathFetchByC_K,
				new Object[] {
					cpSpecificationOption.getCompanyId(),
					cpSpecificationOption.getKey()
				},
				cpSpecificationOption);

			finderCache.putResult(
				_finderPathFetchByERC_C,
				new Object[] {
					cpSpecificationOption.getExternalReferenceCode(),
					cpSpecificationOption.getCompanyId()
				},
				cpSpecificationOption);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cp specification options in the entity cache if it is enabled.
	 *
	 * @param cpSpecificationOptions the cp specification options
	 */
	@Override
	public void cacheResult(
		List<CPSpecificationOption> cpSpecificationOptions) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (cpSpecificationOptions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CPSpecificationOption cpSpecificationOption :
				cpSpecificationOptions) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						cpSpecificationOption.getCtCollectionId())) {

				if (entityCache.getResult(
						CPSpecificationOptionImpl.class,
						cpSpecificationOption.getPrimaryKey()) == null) {

					cacheResult(cpSpecificationOption);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cp specification options.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CPSpecificationOptionImpl.class);

		finderCache.clearCache(CPSpecificationOptionImpl.class);
	}

	/**
	 * Clears the cache for the cp specification option.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CPSpecificationOption cpSpecificationOption) {
		entityCache.removeResult(
			CPSpecificationOptionImpl.class, cpSpecificationOption);
	}

	@Override
	public void clearCache(List<CPSpecificationOption> cpSpecificationOptions) {
		for (CPSpecificationOption cpSpecificationOption :
				cpSpecificationOptions) {

			entityCache.removeResult(
				CPSpecificationOptionImpl.class, cpSpecificationOption);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CPSpecificationOptionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CPSpecificationOptionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CPSpecificationOptionModelImpl cpSpecificationOptionModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpSpecificationOptionModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				cpSpecificationOptionModelImpl.getCompanyId(),
				cpSpecificationOptionModelImpl.getKey()
			};

			finderCache.putResult(
				_finderPathFetchByC_K, args, cpSpecificationOptionModelImpl);

			args = new Object[] {
				cpSpecificationOptionModelImpl.getExternalReferenceCode(),
				cpSpecificationOptionModelImpl.getCompanyId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_C, args, cpSpecificationOptionModelImpl);
		}
	}

	/**
	 * Creates a new cp specification option with the primary key. Does not add the cp specification option to the database.
	 *
	 * @param CPSpecificationOptionId the primary key for the new cp specification option
	 * @return the new cp specification option
	 */
	@Override
	public CPSpecificationOption create(long CPSpecificationOptionId) {
		CPSpecificationOption cpSpecificationOption =
			new CPSpecificationOptionImpl();

		cpSpecificationOption.setNew(true);
		cpSpecificationOption.setPrimaryKey(CPSpecificationOptionId);

		String uuid = PortalUUIDUtil.generate();

		cpSpecificationOption.setUuid(uuid);

		cpSpecificationOption.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpSpecificationOption;
	}

	/**
	 * Removes the cp specification option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option that was removed
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption remove(long CPSpecificationOptionId)
		throws NoSuchCPSpecificationOptionException {

		return remove((Serializable)CPSpecificationOptionId);
	}

	/**
	 * Removes the cp specification option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cp specification option
	 * @return the cp specification option that was removed
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption remove(Serializable primaryKey)
		throws NoSuchCPSpecificationOptionException {

		Session session = null;

		try {
			session = openSession();

			CPSpecificationOption cpSpecificationOption =
				(CPSpecificationOption)session.get(
					CPSpecificationOptionImpl.class, primaryKey);

			if (cpSpecificationOption == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCPSpecificationOptionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(cpSpecificationOption);
		}
		catch (NoSuchCPSpecificationOptionException noSuchEntityException) {
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
	protected CPSpecificationOption removeImpl(
		CPSpecificationOption cpSpecificationOption) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpSpecificationOption)) {
				cpSpecificationOption = (CPSpecificationOption)session.get(
					CPSpecificationOptionImpl.class,
					cpSpecificationOption.getPrimaryKeyObj());
			}

			if ((cpSpecificationOption != null) &&
				ctPersistenceHelper.isRemove(cpSpecificationOption)) {

				session.delete(cpSpecificationOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpSpecificationOption != null) {
			clearCache(cpSpecificationOption);
		}

		return cpSpecificationOption;
	}

	@Override
	public CPSpecificationOption updateImpl(
		CPSpecificationOption cpSpecificationOption) {

		boolean isNew = cpSpecificationOption.isNew();

		if (!(cpSpecificationOption instanceof
				CPSpecificationOptionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpSpecificationOption.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpSpecificationOption);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpSpecificationOption proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPSpecificationOption implementation " +
					cpSpecificationOption.getClass());
		}

		CPSpecificationOptionModelImpl cpSpecificationOptionModelImpl =
			(CPSpecificationOptionModelImpl)cpSpecificationOption;

		if (Validator.isNull(cpSpecificationOption.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpSpecificationOption.setUuid(uuid);
		}

		if (Validator.isNull(
				cpSpecificationOption.getExternalReferenceCode())) {

			cpSpecificationOption.setExternalReferenceCode(
				cpSpecificationOption.getUuid());
		}
		else {
			if (!Objects.equals(
					cpSpecificationOptionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpSpecificationOption.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpSpecificationOption.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = cpSpecificationOption.getPrimaryKey();
					}

					try {
						cpSpecificationOption.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPSpecificationOption.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpSpecificationOption.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPSpecificationOption ercCPSpecificationOption = fetchByERC_C(
				cpSpecificationOption.getExternalReferenceCode(),
				cpSpecificationOption.getCompanyId());

			if (isNew) {
				if (ercCPSpecificationOption != null) {
					throw new DuplicateCPSpecificationOptionExternalReferenceCodeException(
						"Duplicate cp specification option with external reference code " +
							cpSpecificationOption.getExternalReferenceCode() +
								" and company " +
									cpSpecificationOption.getCompanyId());
				}
			}
			else {
				if ((ercCPSpecificationOption != null) &&
					(cpSpecificationOption.getCPSpecificationOptionId() !=
						ercCPSpecificationOption.
							getCPSpecificationOptionId())) {

					throw new DuplicateCPSpecificationOptionExternalReferenceCodeException(
						"Duplicate cp specification option with external reference code " +
							cpSpecificationOption.getExternalReferenceCode() +
								" and company " +
									cpSpecificationOption.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpSpecificationOption.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpSpecificationOption.setCreateDate(date);
			}
			else {
				cpSpecificationOption.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpSpecificationOptionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpSpecificationOption.setModifiedDate(date);
			}
			else {
				cpSpecificationOption.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpSpecificationOption)) {
				if (!isNew) {
					session.evict(
						CPSpecificationOptionImpl.class,
						cpSpecificationOption.getPrimaryKeyObj());
				}

				session.save(cpSpecificationOption);
			}
			else {
				cpSpecificationOption = (CPSpecificationOption)session.merge(
					cpSpecificationOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CPSpecificationOptionImpl.class, cpSpecificationOptionModelImpl,
			false, true);

		cacheUniqueFindersCache(cpSpecificationOptionModelImpl);

		if (isNew) {
			cpSpecificationOption.setNew(false);
		}

		cpSpecificationOption.resetOriginalValues();

		return cpSpecificationOption;
	}

	/**
	 * Returns the cp specification option with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp specification option
	 * @return the cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCPSpecificationOptionException {

		CPSpecificationOption cpSpecificationOption = fetchByPrimaryKey(
			primaryKey);

		if (cpSpecificationOption == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCPSpecificationOptionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return cpSpecificationOption;
	}

	/**
	 * Returns the cp specification option with the primary key or throws a <code>NoSuchCPSpecificationOptionException</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option
	 * @throws NoSuchCPSpecificationOptionException if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption findByPrimaryKey(long CPSpecificationOptionId)
		throws NoSuchCPSpecificationOptionException {

		return findByPrimaryKey((Serializable)CPSpecificationOptionId);
	}

	/**
	 * Returns the cp specification option with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp specification option
	 * @return the cp specification option, or <code>null</code> if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				CPSpecificationOption.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CPSpecificationOption cpSpecificationOption =
			(CPSpecificationOption)entityCache.getResult(
				CPSpecificationOptionImpl.class, primaryKey);

		if (cpSpecificationOption != null) {
			return cpSpecificationOption;
		}

		Session session = null;

		try {
			session = openSession();

			cpSpecificationOption = (CPSpecificationOption)session.get(
				CPSpecificationOptionImpl.class, primaryKey);

			if (cpSpecificationOption != null) {
				cacheResult(cpSpecificationOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return cpSpecificationOption;
	}

	/**
	 * Returns the cp specification option with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option, or <code>null</code> if a cp specification option with the primary key could not be found
	 */
	@Override
	public CPSpecificationOption fetchByPrimaryKey(
		long CPSpecificationOptionId) {

		return fetchByPrimaryKey((Serializable)CPSpecificationOptionId);
	}

	@Override
	public Map<Serializable, CPSpecificationOption> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CPSpecificationOption.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CPSpecificationOption> map =
			new HashMap<Serializable, CPSpecificationOption>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CPSpecificationOption cpSpecificationOption = fetchByPrimaryKey(
				primaryKey);

			if (cpSpecificationOption != null) {
				map.put(primaryKey, cpSpecificationOption);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CPSpecificationOption.class, primaryKey)) {

				CPSpecificationOption cpSpecificationOption =
					(CPSpecificationOption)entityCache.getResult(
						CPSpecificationOptionImpl.class, primaryKey);

				if (cpSpecificationOption == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, cpSpecificationOption);
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

			for (CPSpecificationOption cpSpecificationOption :
					(List<CPSpecificationOption>)query.list()) {

				map.put(
					cpSpecificationOption.getPrimaryKeyObj(),
					cpSpecificationOption);

				cacheResult(cpSpecificationOption);
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
	 * Returns all the cp specification options.
	 *
	 * @return the cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp specification options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp specification options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findAll(
		int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp specification options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cp specification options
	 */
	@Override
	public List<CPSpecificationOption> findAll(
		int start, int end,
		OrderByComparator<CPSpecificationOption> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

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

			List<CPSpecificationOption> list = null;

			if (useFinderCache) {
				list = (List<CPSpecificationOption>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CPSPECIFICATIONOPTION);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CPSPECIFICATIONOPTION;

					sql = sql.concat(
						CPSpecificationOptionModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CPSpecificationOption>)QueryUtil.list(
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
	 * Removes all the cp specification options from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CPSpecificationOption cpSpecificationOption : findAll()) {
			remove(cpSpecificationOption);
		}
	}

	/**
	 * Returns the number of cp specification options.
	 *
	 * @return the number of cp specification options
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPSpecificationOption.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_CPSPECIFICATIONOPTION);

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
		return "CPSpecificationOptionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPSPECIFICATIONOPTION;
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
		return CPSpecificationOptionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPSpecificationOption";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPOptionCategoryId");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("facetable");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("visible");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPSpecificationOptionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"companyId", "key_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp specification option persistence.
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

		_finderPathWithPaginationFindByCPOptionCategoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPOptionCategoryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPOptionCategoryId"}, true);

		_finderPathWithoutPaginationFindByCPOptionCategoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByCPOptionCategoryId", new String[] {Long.class.getName()},
			new String[] {"CPOptionCategoryId"}, true);

		_finderPathCountByCPOptionCategoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByCPOptionCategoryId", new String[] {Long.class.getName()},
			new String[] {"CPOptionCategoryId"}, false);

		_finderPathFetchByC_K = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "key_"}, true);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CPSpecificationOptionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPSpecificationOptionUtil.setPersistence(null);

		entityCache.removeCache(CPSpecificationOptionImpl.class.getName());
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

	private static final String _SQL_SELECT_CPSPECIFICATIONOPTION =
		"SELECT cpSpecificationOption FROM CPSpecificationOption cpSpecificationOption";

	private static final String _SQL_SELECT_CPSPECIFICATIONOPTION_WHERE =
		"SELECT cpSpecificationOption FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final String _SQL_COUNT_CPSPECIFICATIONOPTION =
		"SELECT COUNT(cpSpecificationOption) FROM CPSpecificationOption cpSpecificationOption";

	private static final String _SQL_COUNT_CPSPECIFICATIONOPTION_WHERE =
		"SELECT COUNT(cpSpecificationOption) FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"cpSpecificationOption.CPSpecificationOptionId";

	private static final String _FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_WHERE =
		"SELECT DISTINCT {cpSpecificationOption.*} FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CPSpecificationOption.*} FROM (SELECT DISTINCT cpSpecificationOption.CPSpecificationOptionId FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CPSPECIFICATIONOPTION_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CPSpecificationOption ON TEMP_TABLE.CPSpecificationOptionId = CPSpecificationOption.CPSpecificationOptionId";

	private static final String _FILTER_SQL_COUNT_CPSPECIFICATIONOPTION_WHERE =
		"SELECT COUNT(DISTINCT cpSpecificationOption.CPSpecificationOptionId) AS COUNT_VALUE FROM CPSpecificationOption cpSpecificationOption WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "cpSpecificationOption";

	private static final String _FILTER_ENTITY_TABLE = "CPSpecificationOption";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"cpSpecificationOption.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"CPSpecificationOption.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CPSpecificationOption exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPSpecificationOption exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}