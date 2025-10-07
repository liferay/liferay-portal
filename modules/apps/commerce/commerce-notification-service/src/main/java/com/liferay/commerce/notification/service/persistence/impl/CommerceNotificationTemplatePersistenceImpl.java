/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.persistence.impl;

import com.liferay.commerce.notification.exception.NoSuchNotificationTemplateException;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.model.CommerceNotificationTemplateTable;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateImpl;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplatePersistence;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplateUtil;
import com.liferay.commerce.notification.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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

import java.util.Date;
import java.util.HashMap;
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
 * The persistence implementation for the commerce notification template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @deprecated
 * @generated
 */
@Component(service = CommerceNotificationTemplatePersistence.class)
@Deprecated
public class CommerceNotificationTemplatePersistenceImpl
	extends BasePersistenceImpl<CommerceNotificationTemplate>
	implements CommerceNotificationTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceNotificationTemplateUtil</code> to access the commerce notification template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceNotificationTemplateImpl.class.getName();

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
	 * Returns all the commerce notification templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

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

		List<CommerceNotificationTemplate> list = null;

		if (useFinderCache) {
			list = (List<CommerceNotificationTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceNotificationTemplate commerceNotificationTemplate :
						list) {

					if (!uuid.equals(commerceNotificationTemplate.getUuid())) {
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

			sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceNotificationTemplate>)QueryUtil.list(
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

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUuid_First(
			String uuid,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByUuid_First(uuid, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		List<CommerceNotificationTemplate> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUuid_Last(
			String uuid,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByUuid_Last(uuid, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the last commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUuid_Last(
		String uuid,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CommerceNotificationTemplate> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] findByUuid_PrevAndNext(
			long commerceNotificationTemplateId, String uuid,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		uuid = Objects.toString(uuid, "");

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, commerceNotificationTemplate, uuid, orderByComparator,
				true);

			array[1] = commerceNotificationTemplate;

			array[2] = getByUuid_PrevAndNext(
				session, commerceNotificationTemplate, uuid, orderByComparator,
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

	protected CommerceNotificationTemplate getByUuid_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, String uuid,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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
			sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce notification templates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceNotificationTemplate);
		}
	}

	/**
	 * Returns the number of commerce notification templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"commerceNotificationTemplate.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(commerceNotificationTemplate.uuid IS NULL OR commerceNotificationTemplate.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the commerce notification template where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUUID_G(String uuid, long groupId)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByUUID_G(uuid, groupId);

		if (commerceNotificationTemplate == null) {
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

			throw new NoSuchNotificationTemplateException(sb.toString());
		}

		return commerceNotificationTemplate;
	}

	/**
	 * Returns the commerce notification template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the commerce notification template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

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

		if (result instanceof CommerceNotificationTemplate) {
			CommerceNotificationTemplate commerceNotificationTemplate =
				(CommerceNotificationTemplate)result;

			if (!Objects.equals(uuid, commerceNotificationTemplate.getUuid()) ||
				(groupId != commerceNotificationTemplate.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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

				List<CommerceNotificationTemplate> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					CommerceNotificationTemplate commerceNotificationTemplate =
						list.get(0);

					result = commerceNotificationTemplate;

					cacheResult(commerceNotificationTemplate);
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
			return (CommerceNotificationTemplate)result;
		}
	}

	/**
	 * Removes the commerce notification template where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce notification template that was removed
	 */
	@Override
	public CommerceNotificationTemplate removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByUUID_G(uuid, groupId);

		return remove(commerceNotificationTemplate);
	}

	/**
	 * Returns the number of commerce notification templates where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByUUID_G(uuid, groupId);

		if (commerceNotificationTemplate == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"commerceNotificationTemplate.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(commerceNotificationTemplate.uuid IS NULL OR commerceNotificationTemplate.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"commerceNotificationTemplate.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

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

		List<CommerceNotificationTemplate> list = null;

		if (useFinderCache) {
			list = (List<CommerceNotificationTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceNotificationTemplate commerceNotificationTemplate :
						list) {

					if (!uuid.equals(commerceNotificationTemplate.getUuid()) ||
						(companyId !=
							commerceNotificationTemplate.getCompanyId())) {

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

			sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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

				list = (List<CommerceNotificationTemplate>)QueryUtil.list(
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

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		List<CommerceNotificationTemplate> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the last commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceNotificationTemplate> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] findByUuid_C_PrevAndNext(
			long commerceNotificationTemplateId, String uuid, long companyId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		uuid = Objects.toString(uuid, "");

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, commerceNotificationTemplate, uuid, companyId,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = getByUuid_C_PrevAndNext(
				session, commerceNotificationTemplate, uuid, companyId,
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

	protected CommerceNotificationTemplate getByUuid_C_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, String uuid,
		long companyId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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
			sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce notification templates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceNotificationTemplate);
		}
	}

	/**
	 * Returns the number of commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"commerceNotificationTemplate.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(commerceNotificationTemplate.uuid IS NULL OR commerceNotificationTemplate.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"commerceNotificationTemplate.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the commerce notification templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

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
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<CommerceNotificationTemplate> list = null;

		if (useFinderCache) {
			list = (List<CommerceNotificationTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceNotificationTemplate commerceNotificationTemplate :
						list) {

					if (groupId != commerceNotificationTemplate.getGroupId()) {
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

			sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<CommerceNotificationTemplate>)QueryUtil.list(
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

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByGroupId_First(groupId, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		List<CommerceNotificationTemplate> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByGroupId_Last(
			long groupId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByGroupId_Last(groupId, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the last commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByGroupId_Last(
		long groupId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<CommerceNotificationTemplate> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] findByGroupId_PrevAndNext(
			long commerceNotificationTemplateId, long groupId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, commerceNotificationTemplate, groupId,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = getByGroupId_PrevAndNext(
				session, commerceNotificationTemplate, groupId,
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

	protected CommerceNotificationTemplate getByGroupId_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, long groupId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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
			sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce notification templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceNotificationTemplateModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					CommerceNotificationTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					CommerceNotificationTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<CommerceNotificationTemplate>)QueryUtil.list(
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
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set of commerce notification templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] filterFindByGroupId_PrevAndNext(
			long commerceNotificationTemplateId, long groupId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				commerceNotificationTemplateId, groupId, orderByComparator);
		}

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, commerceNotificationTemplate, groupId,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, commerceNotificationTemplate, groupId,
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

	protected CommerceNotificationTemplate filterGetByGroupId_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, long groupId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceNotificationTemplateModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceNotificationTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceNotificationTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce notification templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceNotificationTemplate);
		}
	}

	/**
	 * Returns the number of commerce notification templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

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

	/**
	 * Returns the number of commerce notification templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceNotificationTemplate> commerceNotificationTemplates =
				findByGroupId(groupId);

			commerceNotificationTemplates = InlineSQLHelperUtil.filter(
				commerceNotificationTemplates, groupId);

			return commerceNotificationTemplates.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
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
		"commerceNotificationTemplate.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByG_E;
	private FinderPath _finderPathWithoutPaginationFindByG_E;
	private FinderPath _finderPathCountByG_E;

	/**
	 * Returns all the commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @return the matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_E(
		long groupId, boolean enabled) {

		return findByG_E(
			groupId, enabled, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_E(
		long groupId, boolean enabled, int start, int end) {

		return findByG_E(groupId, enabled, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_E(
		long groupId, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return findByG_E(groupId, enabled, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_E(
		long groupId, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_E;
				finderArgs = new Object[] {groupId, enabled};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_E;
			finderArgs = new Object[] {
				groupId, enabled, start, end, orderByComparator
			};
		}

		List<CommerceNotificationTemplate> list = null;

		if (useFinderCache) {
			list = (List<CommerceNotificationTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceNotificationTemplate commerceNotificationTemplate :
						list) {

					if ((groupId !=
							commerceNotificationTemplate.getGroupId()) ||
						(enabled != commerceNotificationTemplate.isEnabled())) {

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

			sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_G_E_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_E_ENABLED_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(enabled);

				list = (List<CommerceNotificationTemplate>)QueryUtil.list(
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

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByG_E_First(
			long groupId, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByG_E_First(groupId, enabled, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", enabled=");
		sb.append(enabled);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByG_E_First(
		long groupId, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		List<CommerceNotificationTemplate> list = findByG_E(
			groupId, enabled, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByG_E_Last(
			long groupId, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByG_E_Last(groupId, enabled, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", enabled=");
		sb.append(enabled);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the last commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByG_E_Last(
		long groupId, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		int count = countByG_E(groupId, enabled);

		if (count == 0) {
			return null;
		}

		List<CommerceNotificationTemplate> list = findByG_E(
			groupId, enabled, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] findByG_E_PrevAndNext(
			long commerceNotificationTemplateId, long groupId, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = getByG_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, enabled,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = getByG_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, enabled,
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

	protected CommerceNotificationTemplate getByG_E_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, long groupId,
		boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_E_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_E_ENABLED_2);

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
			sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(enabled);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce notification templates that the user has permission to view where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @return the matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_E(
		long groupId, boolean enabled) {

		return filterFindByG_E(
			groupId, enabled, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates that the user has permission to view where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_E(
		long groupId, boolean enabled, int start, int end) {

		return filterFindByG_E(groupId, enabled, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates that the user has permissions to view where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_E(
		long groupId, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_E(groupId, enabled, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_E(
					groupId, enabled, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_E_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_E_ENABLED_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceNotificationTemplateModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					CommerceNotificationTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					CommerceNotificationTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(enabled);

			return (List<CommerceNotificationTemplate>)QueryUtil.list(
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
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set of commerce notification templates that the user has permission to view where groupId = &#63; and enabled = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] filterFindByG_E_PrevAndNext(
			long commerceNotificationTemplateId, long groupId, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_E_PrevAndNext(
				commerceNotificationTemplateId, groupId, enabled,
				orderByComparator);
		}

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = filterGetByG_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, enabled,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = filterGetByG_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, enabled,
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

	protected CommerceNotificationTemplate filterGetByG_E_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, long groupId,
		boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_E_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_E_ENABLED_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceNotificationTemplateModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceNotificationTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceNotificationTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(enabled);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce notification templates where groupId = &#63; and enabled = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 */
	@Override
	public void removeByG_E(long groupId, boolean enabled) {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				findByG_E(
					groupId, enabled, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceNotificationTemplate);
		}
	}

	/**
	 * Returns the number of commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByG_E(long groupId, boolean enabled) {
		FinderPath finderPath = _finderPathCountByG_E;

		Object[] finderArgs = new Object[] {groupId, enabled};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_G_E_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_E_ENABLED_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(enabled);

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

	/**
	 * Returns the number of commerce notification templates that the user has permission to view where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_E(long groupId, boolean enabled) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_E(groupId, enabled);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceNotificationTemplate> commerceNotificationTemplates =
				findByG_E(groupId, enabled);

			commerceNotificationTemplates = InlineSQLHelperUtil.filter(
				commerceNotificationTemplates, groupId);

			return commerceNotificationTemplates.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_E_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_E_ENABLED_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(enabled);

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

	private static final String _FINDER_COLUMN_G_E_GROUPID_2 =
		"commerceNotificationTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_E_ENABLED_2 =
		"commerceNotificationTemplate.enabled = ?";

	private FinderPath _finderPathWithPaginationFindByG_T_E;
	private FinderPath _finderPathWithoutPaginationFindByG_T_E;
	private FinderPath _finderPathCountByG_T_E;

	/**
	 * Returns all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @return the matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_T_E(
		long groupId, String type, boolean enabled) {

		return findByG_T_E(
			groupId, type, enabled, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_T_E(
		long groupId, String type, boolean enabled, int start, int end) {

		return findByG_T_E(groupId, type, enabled, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_T_E(
		long groupId, String type, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return findByG_T_E(
			groupId, type, enabled, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_T_E(
		long groupId, String type, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_T_E;
				finderArgs = new Object[] {groupId, type, enabled};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_T_E;
			finderArgs = new Object[] {
				groupId, type, enabled, start, end, orderByComparator
			};
		}

		List<CommerceNotificationTemplate> list = null;

		if (useFinderCache) {
			list = (List<CommerceNotificationTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceNotificationTemplate commerceNotificationTemplate :
						list) {

					if ((groupId !=
							commerceNotificationTemplate.getGroupId()) ||
						!type.equals(commerceNotificationTemplate.getType()) ||
						(enabled != commerceNotificationTemplate.isEnabled())) {

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

			sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_G_T_E_GROUPID_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_T_E_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_G_T_E_TYPE_2);
			}

			sb.append(_FINDER_COLUMN_G_T_E_ENABLED_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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

				queryPos.add(enabled);

				list = (List<CommerceNotificationTemplate>)QueryUtil.list(
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

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByG_T_E_First(
			long groupId, String type, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByG_T_E_First(groupId, type, enabled, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append(", enabled=");
		sb.append(enabled);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByG_T_E_First(
		long groupId, String type, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		List<CommerceNotificationTemplate> list = findByG_T_E(
			groupId, type, enabled, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByG_T_E_Last(
			long groupId, String type, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByG_T_E_Last(groupId, type, enabled, orderByComparator);

		if (commerceNotificationTemplate != null) {
			return commerceNotificationTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append(", enabled=");
		sb.append(enabled);

		sb.append("}");

		throw new NoSuchNotificationTemplateException(sb.toString());
	}

	/**
	 * Returns the last commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByG_T_E_Last(
		long groupId, String type, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		int count = countByG_T_E(groupId, type, enabled);

		if (count == 0) {
			return null;
		}

		List<CommerceNotificationTemplate> list = findByG_T_E(
			groupId, type, enabled, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] findByG_T_E_PrevAndNext(
			long commerceNotificationTemplateId, long groupId, String type,
			boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		type = Objects.toString(type, "");

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = getByG_T_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, type, enabled,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = getByG_T_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, type, enabled,
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

	protected CommerceNotificationTemplate getByG_T_E_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, long groupId,
		String type, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_T_E_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_E_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_E_TYPE_2);
		}

		sb.append(_FINDER_COLUMN_G_T_E_ENABLED_2);

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
			sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
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

		queryPos.add(enabled);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce notification templates that the user has permission to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @return the matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_T_E(
		long groupId, String type, boolean enabled) {

		return filterFindByG_T_E(
			groupId, type, enabled, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates that the user has permission to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_T_E(
		long groupId, String type, boolean enabled, int start, int end) {

		return filterFindByG_T_E(groupId, type, enabled, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates that the user has permissions to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_T_E(
		long groupId, String type, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T_E(
				groupId, type, enabled, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_T_E(
					groupId, type, enabled, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_E_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_E_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_E_TYPE_2_SQL);
		}

		sb.append(_FINDER_COLUMN_G_T_E_ENABLED_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceNotificationTemplateModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS,
					CommerceNotificationTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE,
					CommerceNotificationTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindType) {
				queryPos.add(type);
			}

			queryPos.add(enabled);

			return (List<CommerceNotificationTemplate>)QueryUtil.list(
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
	 * Returns the commerce notification templates before and after the current commerce notification template in the ordered set of commerce notification templates that the user has permission to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param commerceNotificationTemplateId the primary key of the current commerce notification template
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate[] filterFindByG_T_E_PrevAndNext(
			long commerceNotificationTemplateId, long groupId, String type,
			boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T_E_PrevAndNext(
				commerceNotificationTemplateId, groupId, type, enabled,
				orderByComparator);
		}

		type = Objects.toString(type, "");

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByPrimaryKey(commerceNotificationTemplateId);

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate[] array =
				new CommerceNotificationTemplateImpl[3];

			array[0] = filterGetByG_T_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, type, enabled,
				orderByComparator, true);

			array[1] = commerceNotificationTemplate;

			array[2] = filterGetByG_T_E_PrevAndNext(
				session, commerceNotificationTemplate, groupId, type, enabled,
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

	protected CommerceNotificationTemplate filterGetByG_T_E_PrevAndNext(
		Session session,
		CommerceNotificationTemplate commerceNotificationTemplate, long groupId,
		String type, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_E_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_E_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_E_TYPE_2_SQL);
		}

		sb.append(_FINDER_COLUMN_G_T_E_ENABLED_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceNotificationTemplateModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceNotificationTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceNotificationTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceNotificationTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindType) {
			queryPos.add(type);
		}

		queryPos.add(enabled);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceNotificationTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceNotificationTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 */
	@Override
	public void removeByG_T_E(long groupId, String type, boolean enabled) {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				findByG_T_E(
					groupId, type, enabled, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceNotificationTemplate);
		}
	}

	/**
	 * Returns the number of commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByG_T_E(long groupId, String type, boolean enabled) {
		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByG_T_E;

		Object[] finderArgs = new Object[] {groupId, type, enabled};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

			sb.append(_FINDER_COLUMN_G_T_E_GROUPID_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_T_E_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_G_T_E_TYPE_2);
			}

			sb.append(_FINDER_COLUMN_G_T_E_ENABLED_2);

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

				queryPos.add(enabled);

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

	/**
	 * Returns the number of commerce notification templates that the user has permission to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_E(long groupId, String type, boolean enabled) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_T_E(groupId, type, enabled);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceNotificationTemplate> commerceNotificationTemplates =
				findByG_T_E(groupId, type, enabled);

			commerceNotificationTemplates = InlineSQLHelperUtil.filter(
				commerceNotificationTemplates, groupId);

			return commerceNotificationTemplates.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_T_E_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_E_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_E_TYPE_2_SQL);
		}

		sb.append(_FINDER_COLUMN_G_T_E_ENABLED_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceNotificationTemplate.class.getName(),
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

			queryPos.add(enabled);

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

	private static final String _FINDER_COLUMN_G_T_E_GROUPID_2 =
		"commerceNotificationTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_T_E_TYPE_2 =
		"commerceNotificationTemplate.type = ? AND ";

	private static final String _FINDER_COLUMN_G_T_E_TYPE_3 =
		"(commerceNotificationTemplate.type IS NULL OR commerceNotificationTemplate.type = '') AND ";

	private static final String _FINDER_COLUMN_G_T_E_TYPE_2_SQL =
		"commerceNotificationTemplate.type_ = ? AND ";

	private static final String _FINDER_COLUMN_G_T_E_TYPE_3_SQL =
		"(commerceNotificationTemplate.type_ IS NULL OR commerceNotificationTemplate.type_ = '') AND ";

	private static final String _FINDER_COLUMN_G_T_E_ENABLED_2 =
		"commerceNotificationTemplate.enabled = ?";

	public CommerceNotificationTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("from", "from_");
		dbColumnNames.put("to", "to_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceNotificationTemplate.class);

		setModelImplClass(CommerceNotificationTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceNotificationTemplateTable.INSTANCE);
	}

	/**
	 * Caches the commerce notification template in the entity cache if it is enabled.
	 *
	 * @param commerceNotificationTemplate the commerce notification template
	 */
	@Override
	public void cacheResult(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		entityCache.putResult(
			CommerceNotificationTemplateImpl.class,
			commerceNotificationTemplate.getPrimaryKey(),
			commerceNotificationTemplate);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {
				commerceNotificationTemplate.getUuid(),
				commerceNotificationTemplate.getGroupId()
			},
			commerceNotificationTemplate);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce notification templates in the entity cache if it is enabled.
	 *
	 * @param commerceNotificationTemplates the commerce notification templates
	 */
	@Override
	public void cacheResult(
		List<CommerceNotificationTemplate> commerceNotificationTemplates) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commerceNotificationTemplates.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommerceNotificationTemplate commerceNotificationTemplate :
				commerceNotificationTemplates) {

			if (entityCache.getResult(
					CommerceNotificationTemplateImpl.class,
					commerceNotificationTemplate.getPrimaryKey()) == null) {

				cacheResult(commerceNotificationTemplate);
			}
		}
	}

	/**
	 * Clears the cache for all commerce notification templates.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommerceNotificationTemplateImpl.class);

		finderCache.clearCache(CommerceNotificationTemplateImpl.class);
	}

	/**
	 * Clears the cache for the commerce notification template.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		entityCache.removeResult(
			CommerceNotificationTemplateImpl.class,
			commerceNotificationTemplate);
	}

	@Override
	public void clearCache(
		List<CommerceNotificationTemplate> commerceNotificationTemplates) {

		for (CommerceNotificationTemplate commerceNotificationTemplate :
				commerceNotificationTemplates) {

			entityCache.removeResult(
				CommerceNotificationTemplateImpl.class,
				commerceNotificationTemplate);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommerceNotificationTemplateImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CommerceNotificationTemplateImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommerceNotificationTemplateModelImpl
			commerceNotificationTemplateModelImpl) {

		Object[] args = new Object[] {
			commerceNotificationTemplateModelImpl.getUuid(),
			commerceNotificationTemplateModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathFetchByUUID_G, args,
			commerceNotificationTemplateModelImpl);
	}

	/**
	 * Creates a new commerce notification template with the primary key. Does not add the commerce notification template to the database.
	 *
	 * @param commerceNotificationTemplateId the primary key for the new commerce notification template
	 * @return the new commerce notification template
	 */
	@Override
	public CommerceNotificationTemplate create(
		long commerceNotificationTemplateId) {

		CommerceNotificationTemplate commerceNotificationTemplate =
			new CommerceNotificationTemplateImpl();

		commerceNotificationTemplate.setNew(true);
		commerceNotificationTemplate.setPrimaryKey(
			commerceNotificationTemplateId);

		String uuid = PortalUUIDUtil.generate();

		commerceNotificationTemplate.setUuid(uuid);

		commerceNotificationTemplate.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceNotificationTemplate;
	}

	/**
	 * Removes the commerce notification template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template that was removed
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate remove(
			long commerceNotificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return remove((Serializable)commerceNotificationTemplateId);
	}

	/**
	 * Removes the commerce notification template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce notification template
	 * @return the commerce notification template that was removed
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate remove(Serializable primaryKey)
		throws NoSuchNotificationTemplateException {

		Session session = null;

		try {
			session = openSession();

			CommerceNotificationTemplate commerceNotificationTemplate =
				(CommerceNotificationTemplate)session.get(
					CommerceNotificationTemplateImpl.class, primaryKey);

			if (commerceNotificationTemplate == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchNotificationTemplateException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commerceNotificationTemplate);
		}
		catch (NoSuchNotificationTemplateException noSuchEntityException) {
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
	protected CommerceNotificationTemplate removeImpl(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceNotificationTemplate)) {
				commerceNotificationTemplate =
					(CommerceNotificationTemplate)session.get(
						CommerceNotificationTemplateImpl.class,
						commerceNotificationTemplate.getPrimaryKeyObj());
			}

			if (commerceNotificationTemplate != null) {
				session.delete(commerceNotificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceNotificationTemplate != null) {
			clearCache(commerceNotificationTemplate);
		}

		return commerceNotificationTemplate;
	}

	@Override
	public CommerceNotificationTemplate updateImpl(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		boolean isNew = commerceNotificationTemplate.isNew();

		if (!(commerceNotificationTemplate instanceof
				CommerceNotificationTemplateModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceNotificationTemplate.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceNotificationTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceNotificationTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceNotificationTemplate implementation " +
					commerceNotificationTemplate.getClass());
		}

		CommerceNotificationTemplateModelImpl
			commerceNotificationTemplateModelImpl =
				(CommerceNotificationTemplateModelImpl)
					commerceNotificationTemplate;

		if (Validator.isNull(commerceNotificationTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceNotificationTemplate.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceNotificationTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceNotificationTemplate.setCreateDate(date);
			}
			else {
				commerceNotificationTemplate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceNotificationTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceNotificationTemplate.setModifiedDate(date);
			}
			else {
				commerceNotificationTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceNotificationTemplate);
			}
			else {
				commerceNotificationTemplate =
					(CommerceNotificationTemplate)session.merge(
						commerceNotificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommerceNotificationTemplateImpl.class,
			commerceNotificationTemplateModelImpl, false, true);

		cacheUniqueFindersCache(commerceNotificationTemplateModelImpl);

		if (isNew) {
			commerceNotificationTemplate.setNew(false);
		}

		commerceNotificationTemplate.resetOriginalValues();

		return commerceNotificationTemplate;
	}

	/**
	 * Returns the commerce notification template with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce notification template
	 * @return the commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByPrimaryKey(
			Serializable primaryKey)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			fetchByPrimaryKey(primaryKey);

		if (commerceNotificationTemplate == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchNotificationTemplateException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commerceNotificationTemplate;
	}

	/**
	 * Returns the commerce notification template with the primary key or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByPrimaryKey(
			long commerceNotificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return findByPrimaryKey((Serializable)commerceNotificationTemplateId);
	}

	/**
	 * Returns the commerce notification template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template, or <code>null</code> if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByPrimaryKey(
		long commerceNotificationTemplateId) {

		return fetchByPrimaryKey((Serializable)commerceNotificationTemplateId);
	}

	/**
	 * Returns all the commerce notification templates.
	 *
	 * @return the commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findAll(
		int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findAll(
		int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
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

		List<CommerceNotificationTemplate> list = null;

		if (useFinderCache) {
			list = (List<CommerceNotificationTemplate>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE;

				sql = sql.concat(
					CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommerceNotificationTemplate>)QueryUtil.list(
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

	/**
	 * Removes all the commerce notification templates from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				findAll()) {

			remove(commerceNotificationTemplate);
		}
	}

	/**
	 * Returns the number of commerce notification templates.
	 *
	 * @return the number of commerce notification templates
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE);

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
		return "commerceNotificationTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceNotificationTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce notification template persistence.
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

		_finderPathWithPaginationFindByG_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_E",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "enabled"}, true);

		_finderPathWithoutPaginationFindByG_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "enabled"}, true);

		_finderPathCountByG_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "enabled"}, false);

		_finderPathWithPaginationFindByG_T_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_E",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "type_", "enabled"}, true);

		_finderPathWithoutPaginationFindByG_T_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_E",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "type_", "enabled"}, true);

		_finderPathCountByG_T_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T_E",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "type_", "enabled"}, false);

		CommerceNotificationTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceNotificationTemplateUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceNotificationTemplateImpl.class.getName());
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
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE =
		"SELECT commerceNotificationTemplate FROM CommerceNotificationTemplate commerceNotificationTemplate";

	private static final String _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE =
		"SELECT commerceNotificationTemplate FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String _SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE =
		"SELECT COUNT(commerceNotificationTemplate) FROM CommerceNotificationTemplate commerceNotificationTemplate";

	private static final String _SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE =
		"SELECT COUNT(commerceNotificationTemplate) FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commerceNotificationTemplate.commerceNotificationTemplateId";

	private static final String
		_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE =
			"SELECT DISTINCT {commerceNotificationTemplate.*} FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommerceNotificationTemplate.*} FROM (SELECT DISTINCT commerceNotificationTemplate.commerceNotificationTemplateId FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommerceNotificationTemplate ON TEMP_TABLE.commerceNotificationTemplateId = CommerceNotificationTemplate.commerceNotificationTemplateId";

	private static final String
		_FILTER_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE =
			"SELECT COUNT(DISTINCT commerceNotificationTemplate.commerceNotificationTemplateId) AS COUNT_VALUE FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"commerceNotificationTemplate";

	private static final String _FILTER_ENTITY_TABLE =
		"CommerceNotificationTemplate";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"commerceNotificationTemplate.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"CommerceNotificationTemplate.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommerceNotificationTemplate exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceNotificationTemplate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationTemplatePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "from", "to", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}