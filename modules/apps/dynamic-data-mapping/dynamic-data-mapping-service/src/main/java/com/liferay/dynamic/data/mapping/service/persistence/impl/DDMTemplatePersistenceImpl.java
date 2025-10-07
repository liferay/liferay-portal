/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.DuplicateDDMTemplateExternalReferenceCodeException;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplatePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
 * The persistence implementation for the ddm template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMTemplatePersistence.class)
public class DDMTemplatePersistenceImpl
	extends BasePersistenceImpl<DDMTemplate> implements DDMTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMTemplateUtil</code> to access the ddm template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMTemplateImpl.class.getName();

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
	 * Returns all the ddm templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

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

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!uuid.equals(ddmTemplate.getUuid())) {
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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUuid_First(
			String uuid, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByUuid_First(uuid, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUuid_First(
		String uuid, OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUuid_Last(
			String uuid, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByUuid_Last(uuid, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUuid_Last(
		String uuid, OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByUuid_PrevAndNext(
			long templateId, String uuid,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		uuid = Objects.toString(uuid, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ddmTemplate, uuid, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByUuid_PrevAndNext(
				session, ddmTemplate, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate getByUuid_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, String uuid,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DDMTemplate ddmTemplate :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

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
		"ddmTemplate.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ddmTemplate.uuid IS NULL OR ddmTemplate.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the ddm template where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUUID_G(String uuid, long groupId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByUUID_G(uuid, groupId);

		if (ddmTemplate == null) {
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

			throw new NoSuchTemplateException(sb.toString());
		}

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the ddm template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

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

			if (result instanceof DDMTemplate) {
				DDMTemplate ddmTemplate = (DDMTemplate)result;

				if (!Objects.equals(uuid, ddmTemplate.getUuid()) ||
					(groupId != ddmTemplate.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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

					List<DDMTemplate> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DDMTemplate ddmTemplate = list.get(0);

						result = ddmTemplate;

						cacheResult(ddmTemplate);
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
				return (DDMTemplate)result;
			}
		}
	}

	/**
	 * Removes the ddm template where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeByUUID_G(String uuid, long groupId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByUUID_G(uuid, groupId);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DDMTemplate ddmTemplate = fetchByUUID_G(uuid, groupId);

		if (ddmTemplate == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"ddmTemplate.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(ddmTemplate.uuid IS NULL OR ddmTemplate.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"ddmTemplate.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

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

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!uuid.equals(ddmTemplate.getUuid()) ||
							(companyId != ddmTemplate.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByUuid_C_PrevAndNext(
			long templateId, String uuid, long companyId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		uuid = Objects.toString(uuid, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ddmTemplate, uuid, companyId, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByUuid_C_PrevAndNext(
				session, ddmTemplate, uuid, companyId, orderByComparator,
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

	protected DDMTemplate getByUuid_C_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, String uuid, long companyId,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DDMTemplate ddmTemplate :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

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
		"ddmTemplate.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ddmTemplate.uuid IS NULL OR ddmTemplate.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ddmTemplate.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the ddm templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

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

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (groupId != ddmTemplate.getGroupId()) {
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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByGroupId_First(
			long groupId, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByGroupId_First(
			groupId, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByGroupId_First(
		long groupId, OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByGroupId_Last(
			long groupId, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByGroupId_Last(
		long groupId, OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByGroupId_PrevAndNext(
			long templateId, long groupId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, ddmTemplate, groupId, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByGroupId_PrevAndNext(
				session, ddmTemplate, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate getByGroupId_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the ddm templates before and after the current ddm template in the ordered set of ddm templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] filterFindByGroupId_PrevAndNext(
			long templateId, long groupId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				templateId, groupId, orderByComparator);
		}

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, ddmTemplate, groupId, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, ddmTemplate, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate filterGetByGroupId_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DDMTemplate ddmTemplate :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

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
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = findByGroupId(groupId);

			ddmTemplates = InlineSQLHelperUtil.filter(ddmTemplates, groupId);

			return ddmTemplates.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
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
		"ddmTemplate.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByClassPK;
	private FinderPath _finderPathWithoutPaginationFindByClassPK;
	private FinderPath _finderPathCountByClassPK;

	/**
	 * Returns all the ddm templates where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByClassPK(long classPK) {
		return findByClassPK(
			classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByClassPK(long classPK, int start, int end) {
		return findByClassPK(classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByClassPK(
		long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByClassPK(classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByClassPK(
		long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByClassPK;
					finderArgs = new Object[] {classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByClassPK;
				finderArgs = new Object[] {
					classPK, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (classPK != ddmTemplate.getClassPK()) {
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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_CLASSPK_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classPK);

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByClassPK_First(
			long classPK, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByClassPK_First(
			classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByClassPK_First(
		long classPK, OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByClassPK(
			classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByClassPK_Last(
			long classPK, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByClassPK_Last(
			classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByClassPK_Last(
		long classPK, OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByClassPK(classPK);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByClassPK(
			classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByClassPK_PrevAndNext(
			long templateId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByClassPK_PrevAndNext(
				session, ddmTemplate, classPK, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByClassPK_PrevAndNext(
				session, ddmTemplate, classPK, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate getByClassPK_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_CLASSPK_CLASSPK_2);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where classPK = &#63; from the database.
	 *
	 * @param classPK the class pk
	 */
	@Override
	public void removeByClassPK(long classPK) {
		for (DDMTemplate ddmTemplate :
				findByClassPK(
					classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByClassPK(long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = _finderPathCountByClassPK;

			Object[] finderArgs = new Object[] {classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_CLASSPK_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_CLASSPK_CLASSPK_2 =
		"ddmTemplate.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByTemplateKey;
	private FinderPath _finderPathWithoutPaginationFindByTemplateKey;
	private FinderPath _finderPathCountByTemplateKey;

	/**
	 * Returns all the ddm templates where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByTemplateKey(String templateKey) {
		return findByTemplateKey(
			templateKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where templateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param templateKey the template key
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByTemplateKey(
		String templateKey, int start, int end) {

		return findByTemplateKey(templateKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where templateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param templateKey the template key
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByTemplateKey(
		String templateKey, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByTemplateKey(
			templateKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where templateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param templateKey the template key
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByTemplateKey(
		String templateKey, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			templateKey = Objects.toString(templateKey, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByTemplateKey;
					finderArgs = new Object[] {templateKey};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByTemplateKey;
				finderArgs = new Object[] {
					templateKey, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!templateKey.equals(ddmTemplate.getTemplateKey())) {
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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				boolean bindTemplateKey = false;

				if (templateKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_3);
				}
				else {
					bindTemplateKey = true;

					sb.append(_FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindTemplateKey) {
						queryPos.add(templateKey);
					}

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByTemplateKey_First(
			String templateKey,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByTemplateKey_First(
			templateKey, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("templateKey=");
		sb.append(templateKey);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByTemplateKey_First(
		String templateKey, OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByTemplateKey(
			templateKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByTemplateKey_Last(
			String templateKey,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByTemplateKey_Last(
			templateKey, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("templateKey=");
		sb.append(templateKey);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByTemplateKey_Last(
		String templateKey, OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByTemplateKey(templateKey);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByTemplateKey(
			templateKey, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByTemplateKey_PrevAndNext(
			long templateId, String templateKey,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		templateKey = Objects.toString(templateKey, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByTemplateKey_PrevAndNext(
				session, ddmTemplate, templateKey, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByTemplateKey_PrevAndNext(
				session, ddmTemplate, templateKey, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate getByTemplateKey_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, String templateKey,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		boolean bindTemplateKey = false;

		if (templateKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_3);
		}
		else {
			bindTemplateKey = true;

			sb.append(_FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_2);
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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindTemplateKey) {
			queryPos.add(templateKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where templateKey = &#63; from the database.
	 *
	 * @param templateKey the template key
	 */
	@Override
	public void removeByTemplateKey(String templateKey) {
		for (DDMTemplate ddmTemplate :
				findByTemplateKey(
					templateKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByTemplateKey(String templateKey) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			templateKey = Objects.toString(templateKey, "");

			FinderPath finderPath = _finderPathCountByTemplateKey;

			Object[] finderArgs = new Object[] {templateKey};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				boolean bindTemplateKey = false;

				if (templateKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_3);
				}
				else {
					bindTemplateKey = true;

					sb.append(_FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindTemplateKey) {
						queryPos.add(templateKey);
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

	private static final String _FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_2 =
		"ddmTemplate.templateKey = ?";

	private static final String _FINDER_COLUMN_TEMPLATEKEY_TEMPLATEKEY_3 =
		"(ddmTemplate.templateKey IS NULL OR ddmTemplate.templateKey = '')";

	private FinderPath _finderPathWithPaginationFindByType;
	private FinderPath _finderPathWithoutPaginationFindByType;
	private FinderPath _finderPathCountByType;

	/**
	 * Returns all the ddm templates where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByType(String type) {
		return findByType(type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByType(String type, int start, int end) {
		return findByType(type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByType(
		String type, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByType(type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByType(
		String type, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByType;
					finderArgs = new Object[] {type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByType;
				finderArgs = new Object[] {type, start, end, orderByComparator};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!type.equals(ddmTemplate.getType())) {
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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_TYPE_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_TYPE_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByType_First(
			String type, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByType_First(type, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByType_First(
		String type, OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByType(type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByType_Last(
			String type, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByType_Last(type, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByType_Last(
		String type, OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByType(type);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByType(
			type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where type = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByType_PrevAndNext(
			long templateId, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		type = Objects.toString(type, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByType_PrevAndNext(
				session, ddmTemplate, type, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByType_PrevAndNext(
				session, ddmTemplate, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate getByType_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, String type,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_TYPE_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_TYPE_TYPE_2);
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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(String type) {
		for (DDMTemplate ddmTemplate :
				findByType(type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByType(String type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByType;

			Object[] finderArgs = new Object[] {type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_TYPE_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_TYPE_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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

	private static final String _FINDER_COLUMN_TYPE_TYPE_2 =
		"ddmTemplate.type = ?";

	private static final String _FINDER_COLUMN_TYPE_TYPE_3 =
		"(ddmTemplate.type IS NULL OR ddmTemplate.type = '')";

	private FinderPath _finderPathWithPaginationFindByLanguage;
	private FinderPath _finderPathWithoutPaginationFindByLanguage;
	private FinderPath _finderPathCountByLanguage;

	/**
	 * Returns all the ddm templates where language = &#63;.
	 *
	 * @param language the language
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByLanguage(String language) {
		return findByLanguage(
			language, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where language = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param language the language
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByLanguage(
		String language, int start, int end) {

		return findByLanguage(language, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where language = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param language the language
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByLanguage(
		String language, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByLanguage(language, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where language = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param language the language
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByLanguage(
		String language, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			language = Objects.toString(language, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByLanguage;
					finderArgs = new Object[] {language};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByLanguage;
				finderArgs = new Object[] {
					language, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!language.equals(ddmTemplate.getLanguage())) {
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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				boolean bindLanguage = false;

				if (language.isEmpty()) {
					sb.append(_FINDER_COLUMN_LANGUAGE_LANGUAGE_3);
				}
				else {
					bindLanguage = true;

					sb.append(_FINDER_COLUMN_LANGUAGE_LANGUAGE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindLanguage) {
						queryPos.add(language);
					}

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where language = &#63;.
	 *
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByLanguage_First(
			String language, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByLanguage_First(
			language, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("language=");
		sb.append(language);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where language = &#63;.
	 *
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByLanguage_First(
		String language, OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByLanguage(
			language, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where language = &#63;.
	 *
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByLanguage_Last(
			String language, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByLanguage_Last(
			language, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("language=");
		sb.append(language);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where language = &#63;.
	 *
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByLanguage_Last(
		String language, OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByLanguage(language);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByLanguage(
			language, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where language = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByLanguage_PrevAndNext(
			long templateId, String language,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		language = Objects.toString(language, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByLanguage_PrevAndNext(
				session, ddmTemplate, language, orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByLanguage_PrevAndNext(
				session, ddmTemplate, language, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMTemplate getByLanguage_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, String language,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		boolean bindLanguage = false;

		if (language.isEmpty()) {
			sb.append(_FINDER_COLUMN_LANGUAGE_LANGUAGE_3);
		}
		else {
			bindLanguage = true;

			sb.append(_FINDER_COLUMN_LANGUAGE_LANGUAGE_2);
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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindLanguage) {
			queryPos.add(language);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where language = &#63; from the database.
	 *
	 * @param language the language
	 */
	@Override
	public void removeByLanguage(String language) {
		for (DDMTemplate ddmTemplate :
				findByLanguage(
					language, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where language = &#63;.
	 *
	 * @param language the language
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByLanguage(String language) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			language = Objects.toString(language, "");

			FinderPath finderPath = _finderPathCountByLanguage;

			Object[] finderArgs = new Object[] {language};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				boolean bindLanguage = false;

				if (language.isEmpty()) {
					sb.append(_FINDER_COLUMN_LANGUAGE_LANGUAGE_3);
				}
				else {
					bindLanguage = true;

					sb.append(_FINDER_COLUMN_LANGUAGE_LANGUAGE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindLanguage) {
						queryPos.add(language);
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

	private static final String _FINDER_COLUMN_LANGUAGE_LANGUAGE_2 =
		"ddmTemplate.language = ?";

	private static final String _FINDER_COLUMN_LANGUAGE_LANGUAGE_3 =
		"(ddmTemplate.language IS NULL OR ddmTemplate.language = '')";

	private FinderPath _finderPathFetchBySmallImageId;

	/**
	 * Returns the ddm template where smallImageId = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findBySmallImageId(long smallImageId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchBySmallImageId(smallImageId);

		if (ddmTemplate == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("smallImageId=");
			sb.append(smallImageId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTemplateException(sb.toString());
		}

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template where smallImageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchBySmallImageId(long smallImageId) {
		return fetchBySmallImageId(smallImageId, true);
	}

	/**
	 * Returns the ddm template where smallImageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param smallImageId the small image ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchBySmallImageId(
		long smallImageId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {smallImageId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchBySmallImageId, finderArgs, this);
			}

			if (result instanceof DDMTemplate) {
				DDMTemplate ddmTemplate = (DDMTemplate)result;

				if (smallImageId != ddmTemplate.getSmallImageId()) {
					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_SMALLIMAGEID_SMALLIMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(smallImageId);

					List<DDMTemplate> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchBySmallImageId, finderArgs,
								list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {smallImageId};
								}

								_log.warn(
									"DDMTemplatePersistenceImpl.fetchBySmallImageId(long, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						DDMTemplate ddmTemplate = list.get(0);

						result = ddmTemplate;

						cacheResult(ddmTemplate);
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
				return (DDMTemplate)result;
			}
		}
	}

	/**
	 * Removes the ddm template where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeBySmallImageId(long smallImageId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findBySmallImageId(smallImageId);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countBySmallImageId(long smallImageId) {
		DDMTemplate ddmTemplate = fetchBySmallImageId(smallImageId);

		if (ddmTemplate == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_SMALLIMAGEID_SMALLIMAGEID_2 =
		"ddmTemplate.smallImageId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C;
	private FinderPath _finderPathCountByG_C;

	/**
	 * Returns all the ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C(long groupId, long classNameId) {
		return findByG_C(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C(
		long groupId, long classNameId, int start, int end) {

		return findByG_C(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_C(
			groupId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C;
					finderArgs = new Object[] {groupId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C;
				finderArgs = new Object[] {
					groupId, classNameId, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if ((groupId != ddmTemplate.getGroupId()) ||
							(classNameId != ddmTemplate.getClassNameId())) {

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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_First(
			groupId, classNameId, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByG_C(
			groupId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_Last(
			long groupId, long classNameId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_Last(
			groupId, classNameId, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_Last(
		long groupId, long classNameId,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByG_C(groupId, classNameId);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByG_C(
			groupId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByG_C_PrevAndNext(
			long templateId, long groupId, long classNameId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByG_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, orderByComparator,
				true);

			array[1] = ddmTemplate;

			array[2] = getByG_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, orderByComparator,
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

	protected DDMTemplate getByG_C_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, OrderByComparator<DDMTemplate> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C(long groupId, long classNameId) {
		return filterFindByG_C(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C(
		long groupId, long classNameId, int start, int end) {

		return filterFindByG_C(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C(
				groupId, classNameId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the ddm templates before and after the current ddm template in the ordered set of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] filterFindByG_C_PrevAndNext(
			long templateId, long groupId, long classNameId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_PrevAndNext(
				templateId, groupId, classNameId, orderByComparator);
		}

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = filterGetByG_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, orderByComparator,
				true);

			array[1] = ddmTemplate;

			array[2] = filterGetByG_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, orderByComparator,
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

	protected DDMTemplate filterGetByG_C_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, OrderByComparator<DDMTemplate> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		for (DDMTemplate ddmTemplate :
				findByG_C(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = _finderPathCountByG_C;

			Object[] finderArgs = new Object[] {groupId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

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
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long classNameId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C(groupId, classNameId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = findByG_C(groupId, classNameId);

			ddmTemplates = InlineSQLHelperUtil.filter(ddmTemplates, groupId);

			return ddmTemplates.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_G_C_GROUPID_2 =
		"ddmTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_CLASSNAMEID_2 =
		"ddmTemplate.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByG_CPK;
	private FinderPath _finderPathWithoutPaginationFindByG_CPK;
	private FinderPath _finderPathCountByG_CPK;
	private FinderPath _finderPathWithPaginationCountByG_CPK;

	/**
	 * Returns all the ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(long groupId, long classPK) {
		return findByG_CPK(
			groupId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long groupId, long classPK, int start, int end) {

		return findByG_CPK(groupId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long groupId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_CPK(
			groupId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long groupId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_CPK;
					finderArgs = new Object[] {groupId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_CPK;
				finderArgs = new Object[] {
					groupId, classPK, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if ((groupId != ddmTemplate.getGroupId()) ||
							(classPK != ddmTemplate.getClassPK())) {

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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_CPK_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classPK);

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_CPK_First(
			long groupId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_CPK_First(
			groupId, classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_CPK_First(
		long groupId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByG_CPK(
			groupId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_CPK_Last(
			long groupId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_CPK_Last(
			groupId, classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_CPK_Last(
		long groupId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByG_CPK(groupId, classPK);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByG_CPK(
			groupId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByG_CPK_PrevAndNext(
			long templateId, long groupId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByG_CPK_PrevAndNext(
				session, ddmTemplate, groupId, classPK, orderByComparator,
				true);

			array[1] = ddmTemplate;

			array[2] = getByG_CPK_PrevAndNext(
				session, ddmTemplate, groupId, classPK, orderByComparator,
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

	protected DDMTemplate getByG_CPK_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_CPK_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(long groupId, long classPK) {
		return filterFindByG_CPK(
			groupId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(
		long groupId, long classPK, int start, int end) {

		return filterFindByG_CPK(groupId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(
		long groupId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_CPK(groupId, classPK, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_CPK(
					groupId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_CPK_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classPK);

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the ddm templates before and after the current ddm template in the ordered set of ddm templates that the user has permission to view where groupId = &#63; and classPK = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] filterFindByG_CPK_PrevAndNext(
			long templateId, long groupId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_CPK_PrevAndNext(
				templateId, groupId, classPK, orderByComparator);
		}

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = filterGetByG_CPK_PrevAndNext(
				session, ddmTemplate, groupId, classPK, orderByComparator,
				true);

			array[1] = ddmTemplate;

			array[2] = filterGetByG_CPK_PrevAndNext(
				session, ddmTemplate, groupId, classPK, orderByComparator,
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

	protected DDMTemplate filterGetByG_CPK_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_CPK_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = any &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(long[] groupIds, long classPK) {
		return filterFindByG_CPK(
			groupIds, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = any &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(
		long[] groupIds, long classPK, int start, int end) {

		return filterFindByG_CPK(groupIds, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permission to view where groupId = any &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(
		long[] groupIds, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_CPK(
				groupIds, classPK, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_CPK(
					groupIds, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_CPK_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classPK);

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns all the ddm templates where groupId = any &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(long[] groupIds, long classPK) {
		return findByG_CPK(
			groupIds, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = any &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long[] groupIds, long classPK, int start, int end) {

		return findByG_CPK(groupIds, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = any &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long[] groupIds, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_CPK(
			groupIds, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long[] groupIds, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByG_CPK(
				groupIds[0], classPK, start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds), classPK
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), classPK, start, end,
					orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					_finderPathWithPaginationFindByG_CPK, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!ArrayUtil.contains(
								groupIds, ddmTemplate.getGroupId()) ||
							(classPK != ddmTemplate.getClassPK())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_CPK_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classPK);

					list = (List<DDMTemplate>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_CPK, finderArgs,
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
	 * Removes all the ddm templates where groupId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_CPK(long groupId, long classPK) {
		for (DDMTemplate ddmTemplate :
				findByG_CPK(
					groupId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_CPK(long groupId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = _finderPathCountByG_CPK;

			Object[] finderArgs = new Object[] {groupId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_CPK_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classPK);

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
	 * Returns the number of ddm templates where groupId = any &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_CPK(long[] groupIds, long classPK) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), classPK
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_CPK, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_CPK_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classPK);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_CPK, finderArgs,
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
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_CPK(long groupId, long classPK) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_CPK(groupId, classPK);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = findByG_CPK(groupId, classPK);

			ddmTemplates = InlineSQLHelperUtil.filter(ddmTemplates, groupId);

			return ddmTemplates.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_CPK_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classPK);

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
	 * Returns the number of ddm templates that the user has permission to view where groupId = any &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_CPK(long[] groupIds, long classPK) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_CPK(groupIds, classPK);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = InlineSQLHelperUtil.filter(
				findByG_CPK(groupIds, classPK), groupIds);

			return ddmTemplates.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_CPK_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_CPK_CLASSPK_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_G_CPK_GROUPID_2 =
		"ddmTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_CPK_GROUPID_7 =
		"ddmTemplate.groupId IN (";

	private static final String _FINDER_COLUMN_G_CPK_CLASSPK_2 =
		"ddmTemplate.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C;
	private FinderPath _finderPathCountByG_C_C;
	private FinderPath _finderPathWithPaginationCountByG_C_C;

	/**
	 * Returns all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long groupId, long classNameId, long classPK) {

		return findByG_C_C(
			groupId, classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return findByG_C_C(groupId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C;
					finderArgs = new Object[] {groupId, classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C;
				finderArgs = new Object[] {
					groupId, classNameId, classPK, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if ((groupId != ddmTemplate.getGroupId()) ||
							(classNameId != ddmTemplate.getClassNameId()) ||
							(classPK != ddmTemplate.getClassPK())) {

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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByG_C_C(
			groupId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_Last(
			long groupId, long classNameId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_Last(
			groupId, classNameId, classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_Last(
		long groupId, long classNameId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByG_C_C(groupId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByG_C_C(
			groupId, classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByG_C_C_PrevAndNext(
			long templateId, long groupId, long classNameId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByG_C_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByG_C_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK,
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

	protected DDMTemplate getByG_C_C_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long groupId, long classNameId, long classPK) {

		return filterFindByG_C_C(
			groupId, classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return filterFindByG_C_C(
			groupId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C(
				groupId, classNameId, classPK, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_C(
					groupId, classNameId, classPK, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the ddm templates before and after the current ddm template in the ordered set of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] filterFindByG_C_C_PrevAndNext(
			long templateId, long groupId, long classNameId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C_PrevAndNext(
				templateId, groupId, classNameId, classPK, orderByComparator);
		}

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = filterGetByG_C_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = filterGetByG_C_C_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK,
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

	protected DDMTemplate filterGetByG_C_C_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long[] groupIds, long classNameId, long classPK) {

		return filterFindByG_C_C(
			groupIds, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end) {

		return filterFindByG_C_C(
			groupIds, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_C_C(
				groupIds, classNameId, classPK, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_C(
					groupIds, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_C_C_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns all the ddm templates where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long[] groupIds, long classNameId, long classPK) {

		return findByG_C_C(
			groupIds, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end) {

		return findByG_C_C(groupIds, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_C_C(
			groupIds, classNameId, classPK, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByG_C_C(
				groupIds[0], classNameId, classPK, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds), classNameId, classPK
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), classNameId, classPK, start,
					end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					_finderPathWithPaginationFindByG_C_C, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if (!ArrayUtil.contains(
								groupIds, ddmTemplate.getGroupId()) ||
							(classNameId != ddmTemplate.getClassNameId()) ||
							(classPK != ddmTemplate.getClassPK())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_C_C_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<DDMTemplate>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_C_C, finderArgs,
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
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C(long groupId, long classNameId, long classPK) {
		for (DDMTemplate ddmTemplate :
				findByG_C_C(
					groupId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			FinderPath finderPath = _finderPathCountByG_C_C;

			Object[] finderArgs = new Object[] {groupId, classNameId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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
	 * Returns the number of ddm templates where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C(long[] groupIds, long classNameId, long classPK) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), classNameId, classPK
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_C_C, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_C_C_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_C_C, finderArgs,
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
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C(
		long groupId, long classNameId, long classPK) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_C(groupId, classNameId, classPK);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = findByG_C_C(
				groupId, classNameId, classPK);

			ddmTemplates = InlineSQLHelperUtil.filter(ddmTemplates, groupId);

			return ddmTemplates.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

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
	 * Returns the number of ddm templates that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C(
		long[] groupIds, long classNameId, long classPK) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_C_C(groupIds, classNameId, classPK);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = InlineSQLHelperUtil.filter(
				findByG_C_C(groupIds, classNameId, classPK), groupIds);

			return ddmTemplates.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_C_C_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);

			queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_G_C_C_GROUPID_2 =
		"ddmTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_GROUPID_7 =
		"ddmTemplate.groupId IN (";

	private static final String _FINDER_COLUMN_G_C_C_CLASSNAMEID_2 =
		"ddmTemplate.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_CLASSPK_2 =
		"ddmTemplate.classPK = ?";

	private FinderPath _finderPathFetchByG_C_T;

	/**
	 * Returns the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_T(
			long groupId, long classNameId, String templateKey)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_T(
			groupId, classNameId, templateKey);

		if (ddmTemplate == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", templateKey=");
			sb.append(templateKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTemplateException(sb.toString());
		}

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_T(
		long groupId, long classNameId, String templateKey) {

		return fetchByG_C_T(groupId, classNameId, templateKey, true);
	}

	/**
	 * Returns the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_T(
		long groupId, long classNameId, String templateKey,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			templateKey = Objects.toString(templateKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, classNameId, templateKey};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_C_T, finderArgs, this);
			}

			if (result instanceof DDMTemplate) {
				DDMTemplate ddmTemplate = (DDMTemplate)result;

				if ((groupId != ddmTemplate.getGroupId()) ||
					(classNameId != ddmTemplate.getClassNameId()) ||
					!Objects.equals(
						templateKey, ddmTemplate.getTemplateKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_CLASSNAMEID_2);

				boolean bindTemplateKey = false;

				if (templateKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_T_TEMPLATEKEY_3);
				}
				else {
					bindTemplateKey = true;

					sb.append(_FINDER_COLUMN_G_C_T_TEMPLATEKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindTemplateKey) {
						queryPos.add(templateKey);
					}

					List<DDMTemplate> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_C_T, finderArgs, list);
						}
					}
					else {
						DDMTemplate ddmTemplate = list.get(0);

						result = ddmTemplate;

						cacheResult(ddmTemplate);
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
				return (DDMTemplate)result;
			}
		}
	}

	/**
	 * Removes the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeByG_C_T(
			long groupId, long classNameId, String templateKey)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByG_C_T(
			groupId, classNameId, templateKey);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and templateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_T(
		long groupId, long classNameId, String templateKey) {

		DDMTemplate ddmTemplate = fetchByG_C_T(
			groupId, classNameId, templateKey);

		if (ddmTemplate == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_C_T_GROUPID_2 =
		"ddmTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_CLASSNAMEID_2 =
		"ddmTemplate.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_TEMPLATEKEY_2 =
		"ddmTemplate.templateKey = ?";

	private static final String _FINDER_COLUMN_G_C_T_TEMPLATEKEY_3 =
		"(ddmTemplate.templateKey IS NULL OR ddmTemplate.templateKey = '')";

	private FinderPath _finderPathWithPaginationFindByC_C_T;
	private FinderPath _finderPathWithoutPaginationFindByC_C_T;
	private FinderPath _finderPathCountByC_C_T;

	/**
	 * Returns all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByC_C_T(
		long classNameId, long classPK, String type) {

		return findByC_C_T(
			classNameId, classPK, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByC_C_T(
		long classNameId, long classPK, String type, int start, int end) {

		return findByC_C_T(classNameId, classPK, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByC_C_T(
		long classNameId, long classPK, String type, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return findByC_C_T(
			classNameId, classPK, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByC_C_T(
		long classNameId, long classPK, String type, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C_T;
					finderArgs = new Object[] {classNameId, classPK, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C_T;
				finderArgs = new Object[] {
					classNameId, classPK, type, start, end, orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if ((classNameId != ddmTemplate.getClassNameId()) ||
							(classPK != ddmTemplate.getClassPK()) ||
							!type.equals(ddmTemplate.getType())) {

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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_T_CLASSPK_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_C_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_C_C_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByC_C_T_First(
			long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByC_C_T_First(
			classNameId, classPK, type, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByC_C_T_First(
		long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByC_C_T(
			classNameId, classPK, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByC_C_T_Last(
			long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByC_C_T_Last(
			classNameId, classPK, type, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByC_C_T_Last(
		long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByC_C_T(classNameId, classPK, type);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByC_C_T(
			classNameId, classPK, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByC_C_T_PrevAndNext(
			long templateId, long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		type = Objects.toString(type, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByC_C_T_PrevAndNext(
				session, ddmTemplate, classNameId, classPK, type,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByC_C_T_PrevAndNext(
				session, ddmTemplate, classNameId, classPK, type,
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

	protected DDMTemplate getByC_C_T_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long classNameId,
		long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_T_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_C_T_TYPE_2);
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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_T(long classNameId, long classPK, String type) {
		for (DDMTemplate ddmTemplate :
				findByC_C_T(
					classNameId, classPK, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByC_C_T(long classNameId, long classPK, String type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByC_C_T;

			Object[] finderArgs = new Object[] {classNameId, classPK, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_T_CLASSPK_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_C_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_C_C_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_C_C_T_CLASSNAMEID_2 =
		"ddmTemplate.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_T_CLASSPK_2 =
		"ddmTemplate.classPK = ? AND ";

	private static final String _FINDER_COLUMN_C_C_T_TYPE_2 =
		"ddmTemplate.type = ?";

	private static final String _FINDER_COLUMN_C_C_T_TYPE_3 =
		"(ddmTemplate.type IS NULL OR ddmTemplate.type = '')";

	private FinderPath _finderPathWithPaginationFindByG_C_C_T;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C_T;
	private FinderPath _finderPathCountByG_C_C_T;

	/**
	 * Returns all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		return findByG_C_C_T(
			groupId, classNameId, classPK, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end) {

		return findByG_C_C_T(
			groupId, classNameId, classPK, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_C_C_T(
			groupId, classNameId, classPK, type, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end, OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C_T;
					finderArgs = new Object[] {
						groupId, classNameId, classPK, type
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C_T;
				finderArgs = new Object[] {
					groupId, classNameId, classPK, type, start, end,
					orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if ((groupId != ddmTemplate.getGroupId()) ||
							(classNameId != ddmTemplate.getClassNameId()) ||
							(classPK != ddmTemplate.getClassPK()) ||
							!type.equals(ddmTemplate.getType())) {

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

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_CLASSPK_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_T_First(
			long groupId, long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_T_First(
			groupId, classNameId, classPK, type, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_T_First(
		long groupId, long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByG_C_C_T(
			groupId, classNameId, classPK, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_T_Last(
			long groupId, long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_T_Last(
			groupId, classNameId, classPK, type, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_T_Last(
		long groupId, long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByG_C_C_T(groupId, classNameId, classPK, type);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByG_C_C_T(
			groupId, classNameId, classPK, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByG_C_C_T_PrevAndNext(
			long templateId, long groupId, long classNameId, long classPK,
			String type, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		type = Objects.toString(type, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByG_C_C_T_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByG_C_C_T_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type,
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

	protected DDMTemplate getByG_C_C_T_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_2);
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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		return filterFindByG_C_C_T(
			groupId, classNameId, classPK, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end) {

		return filterFindByG_C_C_T(
			groupId, classNameId, classPK, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end, OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C_T(
				groupId, classNameId, classPK, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_C_T(
					groupId, classNameId, classPK, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the ddm templates before and after the current ddm template in the ordered set of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] filterFindByG_C_C_T_PrevAndNext(
			long templateId, long groupId, long classNameId, long classPK,
			String type, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C_T_PrevAndNext(
				templateId, groupId, classNameId, classPK, type,
				orderByComparator);
		}

		type = Objects.toString(type, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = filterGetByG_C_C_T_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = filterGetByG_C_C_T_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type,
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

	protected DDMTemplate filterGetByG_C_C_T_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		for (DDMTemplate ddmTemplate :
				findByG_C_C_T(
					groupId, classNameId, classPK, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByG_C_C_T;

			Object[] finderArgs = new Object[] {
				groupId, classNameId, classPK, type
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_CLASSPK_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_C_T(groupId, classNameId, classPK, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = findByG_C_C_T(
				groupId, classNameId, classPK, type);

			ddmTemplates = InlineSQLHelperUtil.filter(ddmTemplates, groupId);

			return ddmTemplates.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_G_C_C_T_GROUPID_2 =
		"ddmTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_CLASSNAMEID_2 =
		"ddmTemplate.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_CLASSPK_2 =
		"ddmTemplate.classPK = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_TYPE_2 =
		"ddmTemplate.type = ?";

	private static final String _FINDER_COLUMN_G_C_C_T_TYPE_3 =
		"(ddmTemplate.type IS NULL OR ddmTemplate.type = '')";

	private static final String _FINDER_COLUMN_G_C_C_T_TYPE_2_SQL =
		"ddmTemplate.type_ = ?";

	private static final String _FINDER_COLUMN_G_C_C_T_TYPE_3_SQL =
		"(ddmTemplate.type_ IS NULL OR ddmTemplate.type_ = '')";

	private FinderPath _finderPathWithPaginationFindByG_C_C_T_M;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C_T_M;
	private FinderPath _finderPathCountByG_C_C_T_M;

	/**
	 * Returns all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @return the matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		return findByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end) {

		return findByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return findByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");
			mode = Objects.toString(mode, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C_T_M;
					finderArgs = new Object[] {
						groupId, classNameId, classPK, type, mode
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C_T_M;
				finderArgs = new Object[] {
					groupId, classNameId, classPK, type, mode, start, end,
					orderByComparator
				};
			}

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMTemplate ddmTemplate : list) {
						if ((groupId != ddmTemplate.getGroupId()) ||
							(classNameId != ddmTemplate.getClassNameId()) ||
							(classPK != ddmTemplate.getClassPK()) ||
							!type.equals(ddmTemplate.getType()) ||
							!mode.equals(ddmTemplate.getMode())) {

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
						7 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(7);
				}

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_T_M_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSPK_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_2);
				}

				boolean bindMode = false;

				if (mode.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_3);
				}
				else {
					bindMode = true;

					sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindType) {
						queryPos.add(type);
					}

					if (bindMode) {
						queryPos.add(mode);
					}

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_T_M_First(
			long groupId, long classNameId, long classPK, String type,
			String mode, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_T_M_First(
			groupId, classNameId, classPK, type, mode, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append(", mode=");
		sb.append(mode);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_T_M_First(
		long groupId, long classNameId, long classPK, String type, String mode,
		OrderByComparator<DDMTemplate> orderByComparator) {

		List<DDMTemplate> list = findByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_T_M_Last(
			long groupId, long classNameId, long classPK, String type,
			String mode, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_T_M_Last(
			groupId, classNameId, classPK, type, mode, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append(", mode=");
		sb.append(mode);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the last ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_T_M_Last(
		long groupId, long classNameId, long classPK, String type, String mode,
		OrderByComparator<DDMTemplate> orderByComparator) {

		int count = countByG_C_C_T_M(groupId, classNameId, classPK, type, mode);

		if (count == 0) {
			return null;
		}

		List<DDMTemplate> list = findByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm templates before and after the current ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] findByG_C_C_T_M_PrevAndNext(
			long templateId, long groupId, long classNameId, long classPK,
			String type, String mode,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		type = Objects.toString(type, "");
		mode = Objects.toString(mode, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = getByG_C_C_T_M_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type, mode,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = getByG_C_C_T_M_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type, mode,
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

	protected DDMTemplate getByG_C_C_T_M_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, long classPK, String type, String mode,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_2);
		}

		boolean bindMode = false;

		if (mode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_3);
		}
		else {
			bindMode = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_2);
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
			sb.append(DDMTemplateModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (bindType) {
			queryPos.add(type);
		}

		if (bindMode) {
			queryPos.add(mode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @return the matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		return filterFindByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end) {

		return filterFindByG_C_C_T_M(
			groupId, classNameId, classPK, type, mode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C_T_M(
				groupId, classNameId, classPK, type, mode, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_C_T_M(
					groupId, classNameId, classPK, type, mode,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		type = Objects.toString(type, "");
		mode = Objects.toString(mode, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_T_M_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_2_SQL);
		}

		boolean bindMode = false;

		if (mode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_3_SQL);
		}
		else {
			bindMode = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			if (bindType) {
				queryPos.add(type);
			}

			if (bindMode) {
				queryPos.add(mode);
			}

			return (List<DDMTemplate>)QueryUtil.list(
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
	 * Returns the ddm templates before and after the current ddm template in the ordered set of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param templateId the primary key of the current ddm template
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate[] filterFindByG_C_C_T_M_PrevAndNext(
			long templateId, long groupId, long classNameId, long classPK,
			String type, String mode,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C_T_M_PrevAndNext(
				templateId, groupId, classNameId, classPK, type, mode,
				orderByComparator);
		}

		type = Objects.toString(type, "");
		mode = Objects.toString(mode, "");

		DDMTemplate ddmTemplate = findByPrimaryKey(templateId);

		Session session = null;

		try {
			session = openSession();

			DDMTemplate[] array = new DDMTemplateImpl[3];

			array[0] = filterGetByG_C_C_T_M_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type, mode,
				orderByComparator, true);

			array[1] = ddmTemplate;

			array[2] = filterGetByG_C_C_T_M_PrevAndNext(
				session, ddmTemplate, groupId, classNameId, classPK, type, mode,
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

	protected DDMTemplate filterGetByG_C_C_T_M_PrevAndNext(
		Session session, DDMTemplate ddmTemplate, long groupId,
		long classNameId, long classPK, String type, String mode,
		OrderByComparator<DDMTemplate> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				9 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMTEMPLATE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_T_M_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_2_SQL);
		}

		boolean bindMode = false;

		if (mode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_3_SQL);
		}
		else {
			bindMode = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMTemplateModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMTemplateImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMTemplateImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (bindType) {
			queryPos.add(type);
		}

		if (bindMode) {
			queryPos.add(mode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmTemplate)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMTemplate> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 */
	@Override
	public void removeByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		for (DDMTemplate ddmTemplate :
				findByG_C_C_T_M(
					groupId, classNameId, classPK, type, mode,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			type = Objects.toString(type, "");
			mode = Objects.toString(mode, "");

			FinderPath finderPath = _finderPathCountByG_C_C_T_M;

			Object[] finderArgs = new Object[] {
				groupId, classNameId, classPK, type, mode
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_COUNT_DDMTEMPLATE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_T_M_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSPK_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_2);
				}

				boolean bindMode = false;

				if (mode.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_3);
				}
				else {
					bindMode = true;

					sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindType) {
						queryPos.add(type);
					}

					if (bindMode) {
						queryPos.add(mode);
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
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_C_T_M(groupId, classNameId, classPK, type, mode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMTemplate> ddmTemplates = findByG_C_C_T_M(
				groupId, classNameId, classPK, type, mode);

			ddmTemplates = InlineSQLHelperUtil.filter(ddmTemplates, groupId);

			return ddmTemplates.size();
		}

		type = Objects.toString(type, "");
		mode = Objects.toString(mode, "");

		StringBundler sb = new StringBundler(6);

		sb.append(_FILTER_SQL_COUNT_DDMTEMPLATE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_T_M_CLASSPK_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_TYPE_2_SQL);
		}

		boolean bindMode = false;

		if (mode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_3_SQL);
		}
		else {
			bindMode = true;

			sb.append(_FINDER_COLUMN_G_C_C_T_M_MODE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMTemplate.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			if (bindType) {
				queryPos.add(type);
			}

			if (bindMode) {
				queryPos.add(mode);
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

	private static final String _FINDER_COLUMN_G_C_C_T_M_GROUPID_2 =
		"ddmTemplate.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_CLASSNAMEID_2 =
		"ddmTemplate.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_CLASSPK_2 =
		"ddmTemplate.classPK = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_TYPE_2 =
		"ddmTemplate.type = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_TYPE_3 =
		"(ddmTemplate.type IS NULL OR ddmTemplate.type = '') AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_TYPE_2_SQL =
		"ddmTemplate.type_ = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_TYPE_3_SQL =
		"(ddmTemplate.type_ IS NULL OR ddmTemplate.type_ = '') AND ";

	private static final String _FINDER_COLUMN_G_C_C_T_M_MODE_2 =
		"ddmTemplate.mode = ?";

	private static final String _FINDER_COLUMN_G_C_C_T_M_MODE_3 =
		"(ddmTemplate.mode IS NULL OR ddmTemplate.mode = '')";

	private static final String _FINDER_COLUMN_G_C_C_T_M_MODE_2_SQL =
		"ddmTemplate.mode_ = ?";

	private static final String _FINDER_COLUMN_G_C_C_T_M_MODE_3_SQL =
		"(ddmTemplate.mode_ IS NULL OR ddmTemplate.mode_ = '')";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the ddm template where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByERC_G(externalReferenceCode, groupId);

		if (ddmTemplate == null) {
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

			throw new NoSuchTemplateException(sb.toString());
		}

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the ddm template where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

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

			if (result instanceof DDMTemplate) {
				DDMTemplate ddmTemplate = (DDMTemplate)result;

				if (!Objects.equals(
						externalReferenceCode,
						ddmTemplate.getExternalReferenceCode()) ||
					(groupId != ddmTemplate.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DDMTEMPLATE_WHERE);

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

					List<DDMTemplate> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						DDMTemplate ddmTemplate = list.get(0);

						result = ddmTemplate;

						cacheResult(ddmTemplate);
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
				return (DDMTemplate)result;
			}
		}
	}

	/**
	 * Removes the ddm template where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByERC_G(externalReferenceCode, groupId);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		DDMTemplate ddmTemplate = fetchByERC_G(externalReferenceCode, groupId);

		if (ddmTemplate == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"ddmTemplate.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(ddmTemplate.externalReferenceCode IS NULL OR ddmTemplate.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"ddmTemplate.groupId = ?";

	public DDMTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("mode", "mode_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMTemplate.class);

		setModelImplClass(DDMTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(DDMTemplateTable.INSTANCE);
	}

	/**
	 * Caches the ddm template in the entity cache if it is enabled.
	 *
	 * @param ddmTemplate the ddm template
	 */
	@Override
	public void cacheResult(DDMTemplate ddmTemplate) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddmTemplate.getCtCollectionId())) {

			entityCache.putResult(
				DDMTemplateImpl.class, ddmTemplate.getPrimaryKey(),
				ddmTemplate);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {ddmTemplate.getUuid(), ddmTemplate.getGroupId()},
				ddmTemplate);

			finderCache.putResult(
				_finderPathFetchBySmallImageId,
				new Object[] {ddmTemplate.getSmallImageId()}, ddmTemplate);

			finderCache.putResult(
				_finderPathFetchByG_C_T,
				new Object[] {
					ddmTemplate.getGroupId(), ddmTemplate.getClassNameId(),
					ddmTemplate.getTemplateKey()
				},
				ddmTemplate);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					ddmTemplate.getExternalReferenceCode(),
					ddmTemplate.getGroupId()
				},
				ddmTemplate);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the ddm templates in the entity cache if it is enabled.
	 *
	 * @param ddmTemplates the ddm templates
	 */
	@Override
	public void cacheResult(List<DDMTemplate> ddmTemplates) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ddmTemplates.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DDMTemplate ddmTemplate : ddmTemplates) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ddmTemplate.getCtCollectionId())) {

				DDMTemplate cachedDDMTemplate =
					(DDMTemplate)entityCache.getResult(
						DDMTemplateImpl.class, ddmTemplate.getPrimaryKey());

				if (cachedDDMTemplate == null) {
					cacheResult(ddmTemplate);
				}
				else {
					DDMTemplateModelImpl ddmTemplateModelImpl =
						(DDMTemplateModelImpl)ddmTemplate;
					DDMTemplateModelImpl cachedDDMTemplateModelImpl =
						(DDMTemplateModelImpl)cachedDDMTemplate;

					ddmTemplateModelImpl.setResourceClassName(
						cachedDDMTemplateModelImpl.getResourceClassName());
				}
			}
		}
	}

	/**
	 * Clears the cache for all ddm templates.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DDMTemplateImpl.class);

		finderCache.clearCache(DDMTemplateImpl.class);
	}

	/**
	 * Clears the cache for the ddm template.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DDMTemplate ddmTemplate) {
		entityCache.removeResult(DDMTemplateImpl.class, ddmTemplate);
	}

	@Override
	public void clearCache(List<DDMTemplate> ddmTemplates) {
		for (DDMTemplate ddmTemplate : ddmTemplates) {
			entityCache.removeResult(DDMTemplateImpl.class, ddmTemplate);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(DDMTemplateImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DDMTemplateImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DDMTemplateModelImpl ddmTemplateModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddmTemplateModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				ddmTemplateModelImpl.getUuid(),
				ddmTemplateModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, ddmTemplateModelImpl);

			args = new Object[] {ddmTemplateModelImpl.getSmallImageId()};

			finderCache.putResult(
				_finderPathFetchBySmallImageId, args, ddmTemplateModelImpl);

			args = new Object[] {
				ddmTemplateModelImpl.getGroupId(),
				ddmTemplateModelImpl.getClassNameId(),
				ddmTemplateModelImpl.getTemplateKey()
			};

			finderCache.putResult(
				_finderPathFetchByG_C_T, args, ddmTemplateModelImpl);

			args = new Object[] {
				ddmTemplateModelImpl.getExternalReferenceCode(),
				ddmTemplateModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, ddmTemplateModelImpl);
		}
	}

	/**
	 * Creates a new ddm template with the primary key. Does not add the ddm template to the database.
	 *
	 * @param templateId the primary key for the new ddm template
	 * @return the new ddm template
	 */
	@Override
	public DDMTemplate create(long templateId) {
		DDMTemplate ddmTemplate = new DDMTemplateImpl();

		ddmTemplate.setNew(true);
		ddmTemplate.setPrimaryKey(templateId);

		String uuid = PortalUUIDUtil.generate();

		ddmTemplate.setUuid(uuid);

		ddmTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmTemplate;
	}

	/**
	 * Removes the ddm template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template that was removed
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate remove(long templateId) throws NoSuchTemplateException {
		return remove((Serializable)templateId);
	}

	/**
	 * Removes the ddm template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ddm template
	 * @return the ddm template that was removed
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate remove(Serializable primaryKey)
		throws NoSuchTemplateException {

		Session session = null;

		try {
			session = openSession();

			DDMTemplate ddmTemplate = (DDMTemplate)session.get(
				DDMTemplateImpl.class, primaryKey);

			if (ddmTemplate == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTemplateException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ddmTemplate);
		}
		catch (NoSuchTemplateException noSuchEntityException) {
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
	protected DDMTemplate removeImpl(DDMTemplate ddmTemplate) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmTemplate)) {
				ddmTemplate = (DDMTemplate)session.get(
					DDMTemplateImpl.class, ddmTemplate.getPrimaryKeyObj());
			}

			if ((ddmTemplate != null) &&
				ctPersistenceHelper.isRemove(ddmTemplate)) {

				session.delete(ddmTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmTemplate != null) {
			clearCache(ddmTemplate);
		}

		return ddmTemplate;
	}

	@Override
	public DDMTemplate updateImpl(DDMTemplate ddmTemplate) {
		boolean isNew = ddmTemplate.isNew();

		if (!(ddmTemplate instanceof DDMTemplateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ddmTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMTemplate implementation " +
					ddmTemplate.getClass());
		}

		DDMTemplateModelImpl ddmTemplateModelImpl =
			(DDMTemplateModelImpl)ddmTemplate;

		if (Validator.isNull(ddmTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmTemplate.setUuid(uuid);
		}

		if (Validator.isNull(ddmTemplate.getExternalReferenceCode())) {
			ddmTemplate.setExternalReferenceCode(ddmTemplate.getUuid());
		}
		else {
			if (!Objects.equals(
					ddmTemplateModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ddmTemplate.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ddmTemplate.getCompanyId();

					long groupId = ddmTemplate.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ddmTemplate.getPrimaryKey();
					}

					try {
						ddmTemplate.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DDMTemplate.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ddmTemplate.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DDMTemplate ercDDMTemplate = fetchByERC_G(
				ddmTemplate.getExternalReferenceCode(),
				ddmTemplate.getGroupId());

			if (isNew) {
				if (ercDDMTemplate != null) {
					throw new DuplicateDDMTemplateExternalReferenceCodeException(
						"Duplicate ddm template with external reference code " +
							ddmTemplate.getExternalReferenceCode() +
								" and group " + ddmTemplate.getGroupId());
				}
			}
			else {
				if ((ercDDMTemplate != null) &&
					(ddmTemplate.getTemplateId() !=
						ercDDMTemplate.getTemplateId())) {

					throw new DuplicateDDMTemplateExternalReferenceCodeException(
						"Duplicate ddm template with external reference code " +
							ddmTemplate.getExternalReferenceCode() +
								" and group " + ddmTemplate.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmTemplate.setCreateDate(date);
			}
			else {
				ddmTemplate.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ddmTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmTemplate.setModifiedDate(date);
			}
			else {
				ddmTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmTemplate)) {
				if (!isNew) {
					session.evict(
						DDMTemplateImpl.class, ddmTemplate.getPrimaryKeyObj());
				}

				session.save(ddmTemplate);
			}
			else {
				ddmTemplate = (DDMTemplate)session.merge(ddmTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DDMTemplateImpl.class, ddmTemplateModelImpl, false, true);

		cacheUniqueFindersCache(ddmTemplateModelImpl);

		if (isNew) {
			ddmTemplate.setNew(false);
		}

		ddmTemplate.resetOriginalValues();

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm template
	 * @return the ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByPrimaryKey(primaryKey);

		if (ddmTemplate == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTemplateException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template with the primary key or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate findByPrimaryKey(long templateId)
		throws NoSuchTemplateException {

		return findByPrimaryKey((Serializable)templateId);
	}

	/**
	 * Returns the ddm template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm template
	 * @return the ddm template, or <code>null</code> if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				DDMTemplate.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DDMTemplate ddmTemplate = (DDMTemplate)entityCache.getResult(
			DDMTemplateImpl.class, primaryKey);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		Session session = null;

		try {
			session = openSession();

			ddmTemplate = (DDMTemplate)session.get(
				DDMTemplateImpl.class, primaryKey);

			if (ddmTemplate != null) {
				cacheResult(ddmTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template, or <code>null</code> if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate fetchByPrimaryKey(long templateId) {
		return fetchByPrimaryKey((Serializable)templateId);
	}

	@Override
	public Map<Serializable, DDMTemplate> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(DDMTemplate.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DDMTemplate> map =
			new HashMap<Serializable, DDMTemplate>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DDMTemplate ddmTemplate = fetchByPrimaryKey(primaryKey);

			if (ddmTemplate != null) {
				map.put(primaryKey, ddmTemplate);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						DDMTemplate.class, primaryKey)) {

				DDMTemplate ddmTemplate = (DDMTemplate)entityCache.getResult(
					DDMTemplateImpl.class, primaryKey);

				if (ddmTemplate == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, ddmTemplate);
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

			for (DDMTemplate ddmTemplate : (List<DDMTemplate>)query.list()) {
				map.put(ddmTemplate.getPrimaryKeyObj(), ddmTemplate);

				cacheResult(ddmTemplate);
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
	 * Returns all the ddm templates.
	 *
	 * @return the ddm templates
	 */
	@Override
	public List<DDMTemplate> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of ddm templates
	 */
	@Override
	public List<DDMTemplate> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddm templates
	 */
	@Override
	public List<DDMTemplate> findAll(
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddm templates
	 */
	@Override
	public List<DDMTemplate> findAll(
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

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

			List<DDMTemplate> list = null;

			if (useFinderCache) {
				list = (List<DDMTemplate>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DDMTEMPLATE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DDMTEMPLATE;

					sql = sql.concat(DDMTemplateModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDMTemplate>)QueryUtil.list(
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
	 * Removes all the ddm templates from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DDMTemplate ddmTemplate : findAll()) {
			remove(ddmTemplate);
		}
	}

	/**
	 * Returns the number of ddm templates.
	 *
	 * @return the number of ddm templates
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMTemplate.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_DDMTEMPLATE);

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
		return "templateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMTEMPLATE;
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
		return DDMTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMTemplate";
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
		ctMergeColumnNames.add("versionUserId");
		ctMergeColumnNames.add("versionUserName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("resourceClassNameId");
		ctMergeColumnNames.add("templateKey");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("mode_");
		ctMergeColumnNames.add("language");
		ctMergeColumnNames.add("script");
		ctMergeColumnNames.add("cacheable");
		ctMergeColumnNames.add("smallImage");
		ctMergeColumnNames.add("smallImageId");
		ctMergeColumnNames.add("smallImageURL");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("templateId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "templateKey"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the ddm template persistence.
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

		_finderPathWithPaginationFindByClassPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByClassPK",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"classPK"}, true);

		_finderPathWithoutPaginationFindByClassPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByClassPK",
			new String[] {Long.class.getName()}, new String[] {"classPK"},
			true);

		_finderPathCountByClassPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByClassPK",
			new String[] {Long.class.getName()}, new String[] {"classPK"},
			false);

		_finderPathWithPaginationFindByTemplateKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTemplateKey",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"templateKey"}, true);

		_finderPathWithoutPaginationFindByTemplateKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTemplateKey",
			new String[] {String.class.getName()}, new String[] {"templateKey"},
			true);

		_finderPathCountByTemplateKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTemplateKey",
			new String[] {String.class.getName()}, new String[] {"templateKey"},
			false);

		_finderPathWithPaginationFindByType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"type_"}, true);

		_finderPathWithoutPaginationFindByType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
			new String[] {String.class.getName()}, new String[] {"type_"},
			true);

		_finderPathCountByType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
			new String[] {String.class.getName()}, new String[] {"type_"},
			false);

		_finderPathWithPaginationFindByLanguage = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLanguage",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"language"}, true);

		_finderPathWithoutPaginationFindByLanguage = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLanguage",
			new String[] {String.class.getName()}, new String[] {"language"},
			true);

		_finderPathCountByLanguage = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByLanguage",
			new String[] {String.class.getName()}, new String[] {"language"},
			false);

		_finderPathFetchBySmallImageId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchBySmallImageId",
			new String[] {Long.class.getName()}, new String[] {"smallImageId"},
			true);

		_finderPathWithPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, false);

		_finderPathWithPaginationFindByG_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CPK",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classPK"}, true);

		_finderPathWithoutPaginationFindByG_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classPK"}, true);

		_finderPathCountByG_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classPK"}, false);

		_finderPathWithPaginationCountByG_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classPK"}, false);

		_finderPathWithPaginationFindByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, true);

		_finderPathCountByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, false);

		_finderPathWithPaginationCountByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, false);

		_finderPathFetchByG_C_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "classNameId", "templateKey"}, true);

		_finderPathWithPaginationFindByC_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK", "type_"}, true);

		_finderPathWithoutPaginationFindByC_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"classNameId", "classPK", "type_"}, true);

		_finderPathCountByC_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"classNameId", "classPK", "type_"}, false);

		_finderPathWithPaginationFindByG_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK", "type_"}, true);

		_finderPathWithoutPaginationFindByG_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK", "type_"}, true);

		_finderPathCountByG_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK", "type_"}, false);

		_finderPathWithPaginationFindByG_C_C_T_M = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_T_M",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "classNameId", "classPK", "type_", "mode_"
			},
			true);

		_finderPathWithoutPaginationFindByG_C_C_T_M = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_T_M",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {
				"groupId", "classNameId", "classPK", "type_", "mode_"
			},
			true);

		_finderPathCountByG_C_C_T_M = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_T_M",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {
				"groupId", "classNameId", "classPK", "type_", "mode_"
			},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		DDMTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMTemplateUtil.setPersistence(null);

		entityCache.removeCache(DDMTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_DDMTEMPLATE =
		"SELECT ddmTemplate FROM DDMTemplate ddmTemplate";

	private static final String _SQL_SELECT_DDMTEMPLATE_WHERE =
		"SELECT ddmTemplate FROM DDMTemplate ddmTemplate WHERE ";

	private static final String _SQL_COUNT_DDMTEMPLATE =
		"SELECT COUNT(ddmTemplate) FROM DDMTemplate ddmTemplate";

	private static final String _SQL_COUNT_DDMTEMPLATE_WHERE =
		"SELECT COUNT(ddmTemplate) FROM DDMTemplate ddmTemplate WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"ddmTemplate.templateId";

	private static final String _FILTER_SQL_SELECT_DDMTEMPLATE_WHERE =
		"SELECT DISTINCT {ddmTemplate.*} FROM DDMTemplate ddmTemplate WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DDMTemplate.*} FROM (SELECT DISTINCT ddmTemplate.templateId FROM DDMTemplate ddmTemplate WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDMTEMPLATE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DDMTemplate ON TEMP_TABLE.templateId = DDMTemplate.templateId";

	private static final String _FILTER_SQL_COUNT_DDMTEMPLATE_WHERE =
		"SELECT COUNT(DISTINCT ddmTemplate.templateId) AS COUNT_VALUE FROM DDMTemplate ddmTemplate WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "ddmTemplate";

	private static final String _FILTER_ENTITY_TABLE = "DDMTemplate";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ddmTemplate.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DDMTemplate.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DDMTemplate exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMTemplate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTemplatePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "mode"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}