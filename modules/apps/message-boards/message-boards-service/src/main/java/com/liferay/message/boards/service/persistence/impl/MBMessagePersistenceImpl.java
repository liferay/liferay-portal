/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.DuplicateMBMessageExternalReferenceCodeException;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageTable;
import com.liferay.message.boards.model.impl.MBMessageImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.MBMessageUtil;
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
 * The persistence implementation for the message-boards message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBMessagePersistence.class)
public class MBMessagePersistenceImpl
	extends BasePersistenceImpl<MBMessage> implements MBMessagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBMessageUtil</code> to access the message-boards message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBMessageImpl.class.getName();

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
	 * Returns all the message-boards messages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (!uuid.equals(mbMessage.getUuid())) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
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

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUuid_First(
			String uuid, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUuid_First(uuid, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUuid_First(
		String uuid, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUuid_Last(
			String uuid, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUuid_Last(uuid, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUuid_Last(
		String uuid, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByUuid_PrevAndNext(
			long messageId, String uuid,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		uuid = Objects.toString(uuid, "");

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, mbMessage, uuid, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByUuid_PrevAndNext(
				session, mbMessage, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByUuid_PrevAndNext(
		Session session, MBMessage mbMessage, String uuid,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (MBMessage mbMessage :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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
		"mbMessage.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(mbMessage.uuid IS NULL OR mbMessage.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUUID_G(String uuid, long groupId)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUUID_G(uuid, groupId);

		if (mbMessage == null) {
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

			throw new NoSuchMessageException(sb.toString());
		}

		return mbMessage;
	}

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			if (result instanceof MBMessage) {
				MBMessage mbMessage = (MBMessage)result;

				if (!Objects.equals(uuid, mbMessage.getUuid()) ||
					(groupId != mbMessage.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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

					List<MBMessage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						MBMessage mbMessage = list.get(0);

						result = mbMessage;

						cacheResult(mbMessage);
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
				return (MBMessage)result;
			}
		}
	}

	/**
	 * Removes the message-boards message where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message-boards message that was removed
	 */
	@Override
	public MBMessage removeByUUID_G(String uuid, long groupId)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByUUID_G(uuid, groupId);

		return remove(mbMessage);
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		MBMessage mbMessage = fetchByUUID_G(uuid, groupId);

		if (mbMessage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"mbMessage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(mbMessage.uuid IS NULL OR mbMessage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"mbMessage.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (!uuid.equals(mbMessage.getUuid()) ||
							(companyId != mbMessage.getCompanyId())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
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

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByUuid_C_PrevAndNext(
			long messageId, String uuid, long companyId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		uuid = Objects.toString(uuid, "");

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, mbMessage, uuid, companyId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByUuid_C_PrevAndNext(
				session, mbMessage, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByUuid_C_PrevAndNext(
		Session session, MBMessage mbMessage, String uuid, long companyId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (MBMessage mbMessage :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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
		"mbMessage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(mbMessage.uuid IS NULL OR mbMessage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"mbMessage.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the message-boards messages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (groupId != mbMessage.getGroupId()) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByGroupId_First(
			long groupId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByGroupId_First(groupId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByGroupId_First(
		long groupId, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByGroupId_Last(
			long groupId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByGroupId_Last(groupId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByGroupId_Last(
		long groupId, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByGroupId_PrevAndNext(
			long messageId, long groupId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, mbMessage, groupId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByGroupId_PrevAndNext(
				session, mbMessage, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByGroupId_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByGroupId_PrevAndNext(
			long messageId, long groupId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				messageId, groupId, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, mbMessage, groupId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, mbMessage, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage filterGetByGroupId_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (MBMessage mbMessage :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByGroupId(groupId);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
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
		"mbMessage.groupId = ? AND mbMessage.categoryId != -1";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the message-boards messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (companyId != mbMessage.getCompanyId()) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByCompanyId_First(
			long companyId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByCompanyId_First(
		long companyId, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByCompanyId_Last(
			long companyId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByCompanyId_Last(
		long companyId, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByCompanyId_PrevAndNext(
			long messageId, long companyId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, mbMessage, companyId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByCompanyId_PrevAndNext(
				session, mbMessage, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByCompanyId_PrevAndNext(
		Session session, MBMessage mbMessage, long companyId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (MBMessage mbMessage :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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
		"mbMessage.companyId = ? AND mbMessage.categoryId != -1";

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the message-boards messages where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUserId(long userId, int start, int end) {
		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUserId;
					finderArgs = new Object[] {userId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUserId;
				finderArgs = new Object[] {
					userId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (userId != mbMessage.getUserId()) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_USERID_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUserId_First(
			long userId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUserId_First(userId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUserId_First(
		long userId, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByUserId(userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByUserId_Last(
			long userId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByUserId_Last(userId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByUserId_Last(
		long userId, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByUserId_PrevAndNext(
			long messageId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, mbMessage, userId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByUserId_PrevAndNext(
				session, mbMessage, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByUserId_PrevAndNext(
		Session session, MBMessage mbMessage, long userId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (MBMessage mbMessage :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByUserId(long userId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByUserId;

			Object[] finderArgs = new Object[] {userId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_USERID_USERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"mbMessage.userId = ? AND mbMessage.categoryId != -1";

	private FinderPath _finderPathWithPaginationFindByThreadId;
	private FinderPath _finderPathWithoutPaginationFindByThreadId;
	private FinderPath _finderPathCountByThreadId;

	/**
	 * Returns all the message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadId(long threadId) {
		return findByThreadId(
			threadId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadId(long threadId, int start, int end) {
		return findByThreadId(threadId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByThreadId(threadId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByThreadId;
					finderArgs = new Object[] {threadId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByThreadId;
				finderArgs = new Object[] {
					threadId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (threadId != mbMessage.getThreadId()) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_THREADID_THREADID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByThreadId_First(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByThreadId_First(
			threadId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByThreadId_First(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByThreadId(
			threadId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByThreadId_Last(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByThreadId_Last(threadId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByThreadId_Last(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByThreadId(threadId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByThreadId(
			threadId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByThreadId_PrevAndNext(
			long messageId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByThreadId_PrevAndNext(
				session, mbMessage, threadId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByThreadId_PrevAndNext(
				session, mbMessage, threadId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByThreadId_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_THREADID_THREADID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByThreadId(long threadId) {
		for (MBMessage mbMessage :
				findByThreadId(
					threadId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByThreadId(long threadId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByThreadId;

			Object[] finderArgs = new Object[] {threadId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_THREADID_THREADID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_THREADID_THREADID_2 =
		"mbMessage.threadId = ?";

	private FinderPath _finderPathWithPaginationFindByThreadIdReplies;
	private FinderPath _finderPathWithoutPaginationFindByThreadIdReplies;
	private FinderPath _finderPathCountByThreadIdReplies;

	/**
	 * Returns all the message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadIdReplies(long threadId) {
		return findByThreadIdReplies(
			threadId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end) {

		return findByThreadIdReplies(threadId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByThreadIdReplies(
			threadId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByThreadIdReplies;
					finderArgs = new Object[] {threadId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByThreadIdReplies;
				finderArgs = new Object[] {
					threadId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (threadId != mbMessage.getThreadId()) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_THREADIDREPLIES_THREADID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByThreadIdReplies_First(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByThreadIdReplies_First(
			threadId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByThreadIdReplies_First(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByThreadIdReplies(
			threadId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByThreadIdReplies_Last(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByThreadIdReplies_Last(
			threadId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByThreadIdReplies_Last(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByThreadIdReplies(threadId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByThreadIdReplies(
			threadId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByThreadIdReplies_PrevAndNext(
			long messageId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByThreadIdReplies_PrevAndNext(
				session, mbMessage, threadId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByThreadIdReplies_PrevAndNext(
				session, mbMessage, threadId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByThreadIdReplies_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_THREADIDREPLIES_THREADID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByThreadIdReplies(long threadId) {
		for (MBMessage mbMessage :
				findByThreadIdReplies(
					threadId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByThreadIdReplies(long threadId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByThreadIdReplies;

			Object[] finderArgs = new Object[] {threadId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_THREADIDREPLIES_THREADID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_THREADIDREPLIES_THREADID_2 =
		"mbMessage.threadId = ? AND mbMessage.parentMessageId != 0";

	private FinderPath _finderPathWithPaginationFindByParentMessageId;
	private FinderPath _finderPathWithoutPaginationFindByParentMessageId;
	private FinderPath _finderPathCountByParentMessageId;

	/**
	 * Returns all the message-boards messages where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByParentMessageId(long parentMessageId) {
		return findByParentMessageId(
			parentMessageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end) {

		return findByParentMessageId(parentMessageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByParentMessageId(
			parentMessageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByParentMessageId;
					finderArgs = new Object[] {parentMessageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByParentMessageId;
				finderArgs = new Object[] {
					parentMessageId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if (parentMessageId != mbMessage.getParentMessageId()) {
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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PARENTMESSAGEID_PARENTMESSAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentMessageId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByParentMessageId_First(
			long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByParentMessageId_First(
			parentMessageId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentMessageId=");
		sb.append(parentMessageId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByParentMessageId_First(
		long parentMessageId, OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByParentMessageId(
			parentMessageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByParentMessageId_Last(
			long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByParentMessageId_Last(
			parentMessageId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentMessageId=");
		sb.append(parentMessageId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByParentMessageId_Last(
		long parentMessageId, OrderByComparator<MBMessage> orderByComparator) {

		int count = countByParentMessageId(parentMessageId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByParentMessageId(
			parentMessageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByParentMessageId_PrevAndNext(
			long messageId, long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByParentMessageId_PrevAndNext(
				session, mbMessage, parentMessageId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByParentMessageId_PrevAndNext(
				session, mbMessage, parentMessageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByParentMessageId_PrevAndNext(
		Session session, MBMessage mbMessage, long parentMessageId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_PARENTMESSAGEID_PARENTMESSAGEID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentMessageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where parentMessageId = &#63; from the database.
	 *
	 * @param parentMessageId the parent message ID
	 */
	@Override
	public void removeByParentMessageId(long parentMessageId) {
		for (MBMessage mbMessage :
				findByParentMessageId(
					parentMessageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByParentMessageId(long parentMessageId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByParentMessageId;

			Object[] finderArgs = new Object[] {parentMessageId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PARENTMESSAGEID_PARENTMESSAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentMessageId);

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
		_FINDER_COLUMN_PARENTMESSAGEID_PARENTMESSAGEID_2 =
			"mbMessage.parentMessageId = ?";

	private FinderPath _finderPathWithPaginationFindByG_U;
	private FinderPath _finderPathWithoutPaginationFindByG_U;
	private FinderPath _finderPathCountByG_U;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U(long groupId, long userId) {
		return findByG_U(
			groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end) {

		return findByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByG_U(groupId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U;
					finderArgs = new Object[] {groupId, userId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U;
				finderArgs = new Object[] {
					groupId, userId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(userId != mbMessage.getUserId())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_U_First(
			long groupId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_U_First(
			groupId, userId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_U(
			groupId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_U_Last(
			long groupId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_U_Last(
			groupId, userId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_U_Last(
		long groupId, long userId,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_U(groupId, userId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_U(
			groupId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_U_PrevAndNext(
			long messageId, long groupId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_U_PrevAndNext(
				session, mbMessage, groupId, userId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByG_U_PrevAndNext(
				session, mbMessage, groupId, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByG_U_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long userId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U(long groupId, long userId) {
		return filterFindByG_U(
			groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U(
		long groupId, long userId, int start, int end) {

		return filterFindByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U(groupId, userId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U(
					groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_U_PrevAndNext(
			long messageId, long groupId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_PrevAndNext(
				messageId, groupId, userId, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_U_PrevAndNext(
				session, mbMessage, groupId, userId, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByG_U_PrevAndNext(
				session, mbMessage, groupId, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage filterGetByG_U_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long userId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		for (MBMessage mbMessage :
				findByG_U(
					groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_U;

			Object[] finderArgs = new Object[] {groupId, userId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_USERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U(long groupId, long userId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U(groupId, userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_U(groupId, userId);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_G_U_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_USERID_2 =
		"mbMessage.userId = ? AND (mbMessage.categoryId != -1) AND (mbMessage.anonymous = [$FALSE$])";

	private FinderPath _finderPathWithPaginationFindByG_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C;
	private FinderPath _finderPathCountByG_C;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C(long groupId, long categoryId) {
		return findByG_C(
			groupId, categoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end) {

		return findByG_C(groupId, categoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByG_C(
			groupId, categoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C;
					finderArgs = new Object[] {groupId, categoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C;
				finderArgs = new Object[] {
					groupId, categoryId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(categoryId != mbMessage.getCategoryId())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CATEGORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_First(
			long groupId, long categoryId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_First(
			groupId, categoryId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_First(
		long groupId, long categoryId,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_C(
			groupId, categoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_Last(
			long groupId, long categoryId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_Last(
			groupId, categoryId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_Last(
		long groupId, long categoryId,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_C(groupId, categoryId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_C(
			groupId, categoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_C_PrevAndNext(
			long messageId, long groupId, long categoryId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_C_PrevAndNext(
				session, mbMessage, groupId, categoryId, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = getByG_C_PrevAndNext(
				session, mbMessage, groupId, categoryId, orderByComparator,
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

	protected MBMessage getByG_C_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CATEGORYID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C(long groupId, long categoryId) {
		return filterFindByG_C(
			groupId, categoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C(
		long groupId, long categoryId, int start, int end) {

		return filterFindByG_C(groupId, categoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C(
				groupId, categoryId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C(
					groupId, categoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_C_PrevAndNext(
			long messageId, long groupId, long categoryId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_PrevAndNext(
				messageId, groupId, categoryId, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_C_PrevAndNext(
				session, mbMessage, groupId, categoryId, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = filterGetByG_C_PrevAndNext(
				session, mbMessage, groupId, categoryId, orderByComparator,
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

	protected MBMessage filterGetByG_C_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CATEGORYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 */
	@Override
	public void removeByG_C(long groupId, long categoryId) {
		for (MBMessage mbMessage :
				findByG_C(
					groupId, categoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C(long groupId, long categoryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_C;

			Object[] finderArgs = new Object[] {groupId, categoryId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CATEGORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long categoryId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C(groupId, categoryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_C(groupId, categoryId);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CATEGORYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

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
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_CATEGORYID_2 =
		"mbMessage.categoryId = ?";

	private FinderPath _finderPathFetchByG_US;

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_US(long groupId, String urlSubject)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_US(groupId, urlSubject);

		if (mbMessage == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", urlSubject=");
			sb.append(urlSubject);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchMessageException(sb.toString());
		}

		return mbMessage;
	}

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_US(long groupId, String urlSubject) {
		return fetchByG_US(groupId, urlSubject, true);
	}

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_US(
		long groupId, String urlSubject, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			urlSubject = Objects.toString(urlSubject, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, urlSubject};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_US, finderArgs, this);
			}

			if (result instanceof MBMessage) {
				MBMessage mbMessage = (MBMessage)result;

				if ((groupId != mbMessage.getGroupId()) ||
					!Objects.equals(urlSubject, mbMessage.getUrlSubject())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_US_GROUPID_2);

				boolean bindUrlSubject = false;

				if (urlSubject.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_US_URLSUBJECT_3);
				}
				else {
					bindUrlSubject = true;

					sb.append(_FINDER_COLUMN_G_US_URLSUBJECT_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindUrlSubject) {
						queryPos.add(urlSubject);
					}

					List<MBMessage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_US, finderArgs, list);
						}
					}
					else {
						MBMessage mbMessage = list.get(0);

						result = mbMessage;

						cacheResult(mbMessage);
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
				return (MBMessage)result;
			}
		}
	}

	/**
	 * Removes the message-boards message where groupId = &#63; and urlSubject = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the message-boards message that was removed
	 */
	@Override
	public MBMessage removeByG_US(long groupId, String urlSubject)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByG_US(groupId, urlSubject);

		return remove(mbMessage);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and urlSubject = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_US(long groupId, String urlSubject) {
		MBMessage mbMessage = fetchByG_US(groupId, urlSubject);

		if (mbMessage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_US_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_US_URLSUBJECT_2 =
		"mbMessage.urlSubject = ?";

	private static final String _FINDER_COLUMN_G_US_URLSUBJECT_3 =
		"(mbMessage.urlSubject IS NULL OR mbMessage.urlSubject = '')";

	private FinderPath _finderPathWithPaginationFindByG_S;
	private FinderPath _finderPathWithoutPaginationFindByG_S;
	private FinderPath _finderPathCountByG_S;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_S(long groupId, int status) {
		return findByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_S(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_S_Last(
			long groupId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_S_Last(
			groupId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_S(groupId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_S(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_S_PrevAndNext(
			long messageId, long groupId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_S_PrevAndNext(
				session, mbMessage, groupId, status, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByG_S_PrevAndNext(
				session, mbMessage, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByG_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_S(long groupId, int status) {
		return filterFindByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_S_PrevAndNext(
			long messageId, long groupId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_PrevAndNext(
				messageId, groupId, status, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_S_PrevAndNext(
				session, mbMessage, groupId, status, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByG_S_PrevAndNext(
				session, mbMessage, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage filterGetByG_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		for (MBMessage mbMessage :
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_S;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S(groupId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_S(groupId, status);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
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
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_STATUS_2 =
		"mbMessage.status = ? AND mbMessage.categoryId != -1";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_S(long companyId, int status) {
		return findByC_S(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_S(
		long companyId, int status, int start, int end) {

		return findByC_S(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByC_S(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((companyId != mbMessage.getCompanyId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_S_First(
			long companyId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByC_S_First(
			companyId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByC_S(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_S_Last(
			long companyId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByC_S_Last(
			companyId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByC_S(companyId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByC_S(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByC_S_PrevAndNext(
			long messageId, long companyId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, mbMessage, companyId, status, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByC_S_PrevAndNext(
				session, mbMessage, companyId, status, orderByComparator,
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

	protected MBMessage getByC_S_PrevAndNext(
		Session session, MBMessage mbMessage, long companyId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		for (MBMessage mbMessage :
				findByC_S(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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
		"mbMessage.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"mbMessage.status = ? AND mbMessage.categoryId != -1";

	private FinderPath _finderPathWithPaginationFindByU_C;
	private FinderPath _finderPathWithoutPaginationFindByU_C;
	private FinderPath _finderPathCountByU_C;
	private FinderPath _finderPathWithPaginationCountByU_C;

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(long userId, long classNameId) {
		return findByU_C(
			userId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long classNameId, int start, int end) {

		return findByU_C(userId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByU_C(
			userId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByU_C;
					finderArgs = new Object[] {userId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByU_C;
				finderArgs = new Object[] {
					userId, classNameId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((userId != mbMessage.getUserId()) ||
							(classNameId != mbMessage.getClassNameId())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_First(
			long userId, long classNameId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_First(
			userId, classNameId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_First(
		long userId, long classNameId,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByU_C(
			userId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_Last(
			long userId, long classNameId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_Last(
			userId, classNameId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_Last(
		long userId, long classNameId,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByU_C(userId, classNameId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByU_C(
			userId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByU_C_PrevAndNext(
			long messageId, long userId, long classNameId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByU_C_PrevAndNext(
				session, mbMessage, userId, classNameId, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = getByU_C_PrevAndNext(
				session, mbMessage, userId, classNameId, orderByComparator,
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

	protected MBMessage getByU_C_PrevAndNext(
		Session session, MBMessage mbMessage, long userId, long classNameId,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_U_C_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(long userId, long[] classNameIds) {
		return findByU_C(
			userId, classNameIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long[] classNameIds, int start, int end) {

		return findByU_C(userId, classNameIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long[] classNameIds, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByU_C(
			userId, classNameIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C(
		long userId, long[] classNameIds, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		if (classNameIds == null) {
			classNameIds = new long[0];
		}
		else if (classNameIds.length > 1) {
			classNameIds = ArrayUtil.sortedUnique(classNameIds);
		}

		if (classNameIds.length == 1) {
			return findByU_C(
				userId, classNameIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						userId, StringUtil.merge(classNameIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					userId, StringUtil.merge(classNameIds), start, end,
					orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					_finderPathWithPaginationFindByU_C, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((userId != mbMessage.getUserId()) ||
							!ArrayUtil.contains(
								classNameIds, mbMessage.getClassNameId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_USERID_2);

				if (classNameIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_7);

					sb.append(StringUtil.merge(classNameIds));

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
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					list = (List<MBMessage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByU_C, finderArgs,
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
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByU_C(long userId, long classNameId) {
		for (MBMessage mbMessage :
				findByU_C(
					userId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C(long userId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByU_C;

			Object[] finderArgs = new Object[] {userId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

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
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = any &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C(long userId, long[] classNameIds) {
		if (classNameIds == null) {
			classNameIds = new long[0];
		}
		else if (classNameIds.length > 1) {
			classNameIds = ArrayUtil.sortedUnique(classNameIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			Object[] finderArgs = new Object[] {
				userId, StringUtil.merge(classNameIds)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByU_C, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_USERID_2);

				if (classNameIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_7);

					sb.append(StringUtil.merge(classNameIds));

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

					queryPos.add(userId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByU_C, finderArgs, count);
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

	private static final String _FINDER_COLUMN_U_C_USERID_2 =
		"mbMessage.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_CLASSNAMEID_2 =
		"mbMessage.classNameId = ?";

	private static final String _FINDER_COLUMN_U_C_CLASSNAMEID_7 =
		"mbMessage.classNameId IN (";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C(long classNameId, long classPK) {
		return findByC_C(
			classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return findByC_C(classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByC_C(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C;
					finderArgs = new Object[] {classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C;
				finderArgs = new Object[] {
					classNameId, classPK, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((classNameId != mbMessage.getClassNameId()) ||
							(classPK != mbMessage.getClassPK())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByC_C_First(
			classNameId, classPK, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByC_C(
			classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_C_Last(
			long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByC_C_Last(
			classNameId, classPK, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_C_Last(
		long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByC_C(classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByC_C(
			classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByC_C_PrevAndNext(
			long messageId, long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, mbMessage, classNameId, classPK, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = getByC_C_PrevAndNext(
				session, mbMessage, classNameId, classPK, orderByComparator,
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

	protected MBMessage getByC_C_PrevAndNext(
		Session session, MBMessage mbMessage, long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		for (MBMessage mbMessage :
				findByC_C(
					classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {classNameId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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

	private static final String _FINDER_COLUMN_C_C_CLASSNAMEID_2 =
		"mbMessage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CLASSPK_2 =
		"mbMessage.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByT_P;
	private FinderPath _finderPathWithoutPaginationFindByT_P;
	private FinderPath _finderPathCountByT_P;

	/**
	 * Returns all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_P(long threadId, long parentMessageId) {
		return findByT_P(
			threadId, parentMessageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end) {

		return findByT_P(threadId, parentMessageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByT_P(
			threadId, parentMessageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByT_P;
					finderArgs = new Object[] {threadId, parentMessageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByT_P;
				finderArgs = new Object[] {
					threadId, parentMessageId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((threadId != mbMessage.getThreadId()) ||
							(parentMessageId !=
								mbMessage.getParentMessageId())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_P_THREADID_2);

				sb.append(_FINDER_COLUMN_T_P_PARENTMESSAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(parentMessageId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_P_First(
			long threadId, long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_P_First(
			threadId, parentMessageId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", parentMessageId=");
		sb.append(parentMessageId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_P_First(
		long threadId, long parentMessageId,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByT_P(
			threadId, parentMessageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_P_Last(
			long threadId, long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_P_Last(
			threadId, parentMessageId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", parentMessageId=");
		sb.append(parentMessageId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_P_Last(
		long threadId, long parentMessageId,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByT_P(threadId, parentMessageId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByT_P(
			threadId, parentMessageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByT_P_PrevAndNext(
			long messageId, long threadId, long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByT_P_PrevAndNext(
				session, mbMessage, threadId, parentMessageId,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByT_P_PrevAndNext(
				session, mbMessage, threadId, parentMessageId,
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

	protected MBMessage getByT_P_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId,
		long parentMessageId, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_T_P_THREADID_2);

		sb.append(_FINDER_COLUMN_T_P_PARENTMESSAGEID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		queryPos.add(parentMessageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and parentMessageId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 */
	@Override
	public void removeByT_P(long threadId, long parentMessageId) {
		for (MBMessage mbMessage :
				findByT_P(
					threadId, parentMessageId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_P(long threadId, long parentMessageId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByT_P;

			Object[] finderArgs = new Object[] {threadId, parentMessageId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_P_THREADID_2);

				sb.append(_FINDER_COLUMN_T_P_PARENTMESSAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(parentMessageId);

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

	private static final String _FINDER_COLUMN_T_P_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_T_P_PARENTMESSAGEID_2 =
		"mbMessage.parentMessageId = ?";

	private FinderPath _finderPathWithPaginationFindByT_A;
	private FinderPath _finderPathWithoutPaginationFindByT_A;
	private FinderPath _finderPathCountByT_A;

	/**
	 * Returns all the message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_A(long threadId, boolean answer) {
		return findByT_A(
			threadId, answer, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_A(
		long threadId, boolean answer, int start, int end) {

		return findByT_A(threadId, answer, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_A(
		long threadId, boolean answer, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByT_A(threadId, answer, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_A(
		long threadId, boolean answer, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByT_A;
					finderArgs = new Object[] {threadId, answer};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByT_A;
				finderArgs = new Object[] {
					threadId, answer, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((threadId != mbMessage.getThreadId()) ||
							(answer != mbMessage.isAnswer())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_A_THREADID_2);

				sb.append(_FINDER_COLUMN_T_A_ANSWER_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(answer);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_A_First(
			long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_A_First(
			threadId, answer, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", answer=");
		sb.append(answer);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_A_First(
		long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByT_A(
			threadId, answer, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_A_Last(
			long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_A_Last(
			threadId, answer, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", answer=");
		sb.append(answer);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_A_Last(
		long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByT_A(threadId, answer);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByT_A(
			threadId, answer, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63; and answer = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByT_A_PrevAndNext(
			long messageId, long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByT_A_PrevAndNext(
				session, mbMessage, threadId, answer, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByT_A_PrevAndNext(
				session, mbMessage, threadId, answer, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByT_A_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_T_A_THREADID_2);

		sb.append(_FINDER_COLUMN_T_A_ANSWER_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		queryPos.add(answer);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and answer = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 */
	@Override
	public void removeByT_A(long threadId, boolean answer) {
		for (MBMessage mbMessage :
				findByT_A(
					threadId, answer, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and answer = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_A(long threadId, boolean answer) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByT_A;

			Object[] finderArgs = new Object[] {threadId, answer};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_A_THREADID_2);

				sb.append(_FINDER_COLUMN_T_A_ANSWER_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(answer);

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

	private static final String _FINDER_COLUMN_T_A_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_T_A_ANSWER_2 =
		"mbMessage.answer = ?";

	private FinderPath _finderPathWithPaginationFindByT_S;
	private FinderPath _finderPathWithoutPaginationFindByT_S;
	private FinderPath _finderPathCountByT_S;

	/**
	 * Returns all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_S(long threadId, int status) {
		return findByT_S(
			threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_S(
		long threadId, int status, int start, int end) {

		return findByT_S(threadId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByT_S(threadId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByT_S;
					finderArgs = new Object[] {threadId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByT_S;
				finderArgs = new Object[] {
					threadId, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((threadId != mbMessage.getThreadId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_S_THREADID_2);

				sb.append(_FINDER_COLUMN_T_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_S_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_S_First(
			threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_S_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByT_S(
			threadId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_S_Last(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_S_Last(
			threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_S_Last(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByT_S(threadId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByT_S(
			threadId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByT_S_PrevAndNext(
			long messageId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByT_S_PrevAndNext(
				session, mbMessage, threadId, status, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByT_S_PrevAndNext(
				session, mbMessage, threadId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByT_S_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_T_S_THREADID_2);

		sb.append(_FINDER_COLUMN_T_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByT_S(long threadId, int status) {
		for (MBMessage mbMessage :
				findByT_S(
					threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_S(long threadId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByT_S;

			Object[] finderArgs = new Object[] {threadId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_S_THREADID_2);

				sb.append(_FINDER_COLUMN_T_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_T_S_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_T_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathWithPaginationFindByT_NotS;
	private FinderPath _finderPathWithPaginationCountByT_NotS;

	/**
	 * Returns all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(long threadId, int status) {
		return findByT_NotS(
			threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end) {

		return findByT_NotS(threadId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByT_NotS(
			threadId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByT_NotS;
			finderArgs = new Object[] {
				threadId, status, start, end, orderByComparator
			};

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((threadId != mbMessage.getThreadId()) ||
							(status == mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_NOTS_THREADID_2);

				sb.append(_FINDER_COLUMN_T_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_NotS_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_NotS_First(
			threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_NotS_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByT_NotS(
			threadId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByT_NotS_Last(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByT_NotS_Last(
			threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByT_NotS_Last(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByT_NotS(threadId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByT_NotS(
			threadId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByT_NotS_PrevAndNext(
			long messageId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByT_NotS_PrevAndNext(
				session, mbMessage, threadId, status, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByT_NotS_PrevAndNext(
				session, mbMessage, threadId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByT_NotS_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_T_NOTS_THREADID_2);

		sb.append(_FINDER_COLUMN_T_NOTS_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByT_NotS(long threadId, int status) {
		for (MBMessage mbMessage :
				findByT_NotS(
					threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByT_NotS(long threadId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByT_NotS;

			Object[] finderArgs = new Object[] {threadId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_T_NOTS_THREADID_2);

				sb.append(_FINDER_COLUMN_T_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_T_NOTS_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_T_NOTS_STATUS_2 =
		"mbMessage.status != ?";

	private FinderPath _finderPathWithPaginationFindByTR_S;
	private FinderPath _finderPathWithoutPaginationFindByTR_S;
	private FinderPath _finderPathCountByTR_S;

	/**
	 * Returns all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByTR_S(long threadId, int status) {
		return findByTR_S(
			threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByTR_S(
		long threadId, int status, int start, int end) {

		return findByTR_S(threadId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByTR_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByTR_S(
			threadId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByTR_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByTR_S;
					finderArgs = new Object[] {threadId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByTR_S;
				finderArgs = new Object[] {
					threadId, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((threadId != mbMessage.getThreadId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_TR_S_THREADID_2);

				sb.append(_FINDER_COLUMN_TR_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByTR_S_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByTR_S_First(
			threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByTR_S_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByTR_S(
			threadId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByTR_S_Last(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByTR_S_Last(
			threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("threadId=");
		sb.append(threadId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByTR_S_Last(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByTR_S(threadId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByTR_S(
			threadId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByTR_S_PrevAndNext(
			long messageId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByTR_S_PrevAndNext(
				session, mbMessage, threadId, status, orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByTR_S_PrevAndNext(
				session, mbMessage, threadId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MBMessage getByTR_S_PrevAndNext(
		Session session, MBMessage mbMessage, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_TR_S_THREADID_2);

		sb.append(_FINDER_COLUMN_TR_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(threadId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByTR_S(long threadId, int status) {
		for (MBMessage mbMessage :
				findByTR_S(
					threadId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByTR_S(long threadId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByTR_S;

			Object[] finderArgs = new Object[] {threadId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_TR_S_THREADID_2);

				sb.append(_FINDER_COLUMN_TR_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_TR_S_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_TR_S_STATUS_2 =
		"mbMessage.status = ? AND mbMessage.parentMessageId != 0";

	private FinderPath _finderPathWithPaginationFindByP_S;
	private FinderPath _finderPathWithoutPaginationFindByP_S;
	private FinderPath _finderPathCountByP_S;

	/**
	 * Returns all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByP_S(long parentMessageId, int status) {
		return findByP_S(
			parentMessageId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end) {

		return findByP_S(parentMessageId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByP_S(
			parentMessageId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByP_S;
					finderArgs = new Object[] {parentMessageId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByP_S;
				finderArgs = new Object[] {
					parentMessageId, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((parentMessageId !=
								mbMessage.getParentMessageId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_P_S_PARENTMESSAGEID_2);

				sb.append(_FINDER_COLUMN_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentMessageId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByP_S_First(
			long parentMessageId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByP_S_First(
			parentMessageId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentMessageId=");
		sb.append(parentMessageId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByP_S_First(
		long parentMessageId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByP_S(
			parentMessageId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByP_S_Last(
			long parentMessageId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByP_S_Last(
			parentMessageId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentMessageId=");
		sb.append(parentMessageId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByP_S_Last(
		long parentMessageId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByP_S(parentMessageId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByP_S(
			parentMessageId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByP_S_PrevAndNext(
			long messageId, long parentMessageId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByP_S_PrevAndNext(
				session, mbMessage, parentMessageId, status, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = getByP_S_PrevAndNext(
				session, mbMessage, parentMessageId, status, orderByComparator,
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

	protected MBMessage getByP_S_PrevAndNext(
		Session session, MBMessage mbMessage, long parentMessageId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_P_S_PARENTMESSAGEID_2);

		sb.append(_FINDER_COLUMN_P_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentMessageId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where parentMessageId = &#63; and status = &#63; from the database.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 */
	@Override
	public void removeByP_S(long parentMessageId, int status) {
		for (MBMessage mbMessage :
				findByP_S(
					parentMessageId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByP_S(long parentMessageId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByP_S;

			Object[] finderArgs = new Object[] {parentMessageId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_P_S_PARENTMESSAGEID_2);

				sb.append(_FINDER_COLUMN_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentMessageId);

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

	private static final String _FINDER_COLUMN_P_S_PARENTMESSAGEID_2 =
		"mbMessage.parentMessageId = ? AND ";

	private static final String _FINDER_COLUMN_P_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_U_S;
	private FinderPath _finderPathWithoutPaginationFindByG_U_S;
	private FinderPath _finderPathCountByG_U_S;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U_S(long groupId, long userId, int status) {
		return findByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByG_U_S(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U_S;
					finderArgs = new Object[] {groupId, userId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U_S;
				finderArgs = new Object[] {
					groupId, userId, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(userId != mbMessage.getUserId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_U_S(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_U_S_Last(
			long groupId, long userId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_U_S_Last(
			groupId, userId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_U_S_Last(
		long groupId, long userId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_U_S(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_U_S(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_U_S_PrevAndNext(
			long messageId, long groupId, long userId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_U_S_PrevAndNext(
				session, mbMessage, groupId, userId, status, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = getByG_U_S_PrevAndNext(
				session, mbMessage, groupId, userId, status, orderByComparator,
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

	protected MBMessage getByG_U_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long userId,
		int status, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return filterFindByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S(
				groupId, userId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_U_S_PrevAndNext(
			long messageId, long groupId, long userId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S_PrevAndNext(
				messageId, groupId, userId, status, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_U_S_PrevAndNext(
				session, mbMessage, groupId, userId, status, orderByComparator,
				true);

			array[1] = mbMessage;

			array[2] = filterGetByG_U_S_PrevAndNext(
				session, mbMessage, groupId, userId, status, orderByComparator,
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

	protected MBMessage filterGetByG_U_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long userId,
		int status, OrderByComparator<MBMessage> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		for (MBMessage mbMessage :
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_U_S;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_U_S(groupId, userId, status);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_G_U_S_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_USERID_2 =
		"mbMessage.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_2 =
		"mbMessage.status = ? AND (mbMessage.categoryId != -1) AND (mbMessage.anonymous = [$FALSE$])";

	private FinderPath _finderPathWithPaginationFindByG_C_T;
	private FinderPath _finderPathWithoutPaginationFindByG_C_T;
	private FinderPath _finderPathCountByG_C_T;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId) {

		return findByG_C_T(
			groupId, categoryId, threadId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end) {

		return findByG_C_T(groupId, categoryId, threadId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByG_C_T(
			groupId, categoryId, threadId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_T;
					finderArgs = new Object[] {groupId, categoryId, threadId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_T;
				finderArgs = new Object[] {
					groupId, categoryId, threadId, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(categoryId != mbMessage.getCategoryId()) ||
							(threadId != mbMessage.getThreadId())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_T_THREADID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(threadId);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_First(
			long groupId, long categoryId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_T_First(
			groupId, categoryId, threadId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", threadId=");
		sb.append(threadId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_First(
		long groupId, long categoryId, long threadId,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_C_T(
			groupId, categoryId, threadId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_Last(
			long groupId, long categoryId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_T_Last(
			groupId, categoryId, threadId, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", threadId=");
		sb.append(threadId);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_Last(
		long groupId, long categoryId, long threadId,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_C_T(groupId, categoryId, threadId);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_C_T(
			groupId, categoryId, threadId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_C_T_PrevAndNext(
			long messageId, long groupId, long categoryId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_C_T_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByG_C_T_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId,
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

	protected MBMessage getByG_C_T_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		long threadId, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_THREADID_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(threadId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId) {

		return filterFindByG_C_T(
			groupId, categoryId, threadId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end) {

		return filterFindByG_C_T(
			groupId, categoryId, threadId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_T(
				groupId, categoryId, threadId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_T(
					groupId, categoryId, threadId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_THREADID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(threadId);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_C_T_PrevAndNext(
			long messageId, long groupId, long categoryId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_T_PrevAndNext(
				messageId, groupId, categoryId, threadId, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_C_T_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByG_C_T_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId,
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

	protected MBMessage filterGetByG_C_T_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		long threadId, OrderByComparator<MBMessage> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_THREADID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(threadId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByG_C_T(long groupId, long categoryId, long threadId) {
		for (MBMessage mbMessage :
				findByG_C_T(
					groupId, categoryId, threadId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_T(long groupId, long categoryId, long threadId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_C_T;

			Object[] finderArgs = new Object[] {groupId, categoryId, threadId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_T_THREADID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(threadId);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T(
		long groupId, long categoryId, long threadId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_T(groupId, categoryId, threadId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_C_T(
				groupId, categoryId, threadId);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_THREADID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_G_C_T_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_CATEGORYID_2 =
		"mbMessage.categoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_THREADID_2 =
		"mbMessage.threadId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_S;
	private FinderPath _finderPathWithoutPaginationFindByG_C_S;
	private FinderPath _finderPathCountByG_C_S;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status) {

		return findByG_C_S(
			groupId, categoryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return findByG_C_S(groupId, categoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_S;
					finderArgs = new Object[] {groupId, categoryId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_S;
				finderArgs = new Object[] {
					groupId, categoryId, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(categoryId != mbMessage.getCategoryId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_S_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_S_First(
			long groupId, long categoryId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_S_First(
			groupId, categoryId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_S_First(
		long groupId, long categoryId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_C_S(
			groupId, categoryId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_S_Last(
			long groupId, long categoryId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_S_Last(
			groupId, categoryId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_S_Last(
		long groupId, long categoryId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_C_S(groupId, categoryId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_C_S(
			groupId, categoryId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_C_S_PrevAndNext(
			long messageId, long groupId, long categoryId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_C_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByG_C_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, status,
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

	protected MBMessage getByG_C_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		int status, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status) {

		return filterFindByG_C_S(
			groupId, categoryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return filterFindByG_C_S(groupId, categoryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_S(
				groupId, categoryId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_S(
					groupId, categoryId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(status);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_C_S_PrevAndNext(
			long messageId, long groupId, long categoryId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_S_PrevAndNext(
				messageId, groupId, categoryId, status, orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_C_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByG_C_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, status,
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

	protected MBMessage filterGetByG_C_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		int status, OrderByComparator<MBMessage> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 */
	@Override
	public void removeByG_C_S(long groupId, long categoryId, int status) {
		for (MBMessage mbMessage :
				findByG_C_S(
					groupId, categoryId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_S(long groupId, long categoryId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_C_S;

			Object[] finderArgs = new Object[] {groupId, categoryId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_S_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_S(long groupId, long categoryId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_S(groupId, categoryId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_C_S(
				groupId, categoryId, status);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

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

	private static final String _FINDER_COLUMN_G_C_S_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_S_CATEGORYID_2 =
		"mbMessage.categoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathWithPaginationFindByU_C_C;
	private FinderPath _finderPathWithoutPaginationFindByU_C_C;
	private FinderPath _finderPathCountByU_C_C;

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK) {

		return findByU_C_C(
			userId, classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end) {

		return findByU_C_C(userId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByU_C_C(
			userId, classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByU_C_C;
					finderArgs = new Object[] {userId, classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByU_C_C;
				finderArgs = new Object[] {
					userId, classNameId, classPK, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((userId != mbMessage.getUserId()) ||
							(classNameId != mbMessage.getClassNameId()) ||
							(classPK != mbMessage.getClassPK())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_C_First(
			long userId, long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_C_First(
			userId, classNameId, classPK, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_C_First(
		long userId, long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByU_C_C(
			userId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_C_Last(
			long userId, long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_C_Last(
			userId, classNameId, classPK, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_C_Last(
		long userId, long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByU_C_C(userId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByU_C_C(
			userId, classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByU_C_C_PrevAndNext(
			long messageId, long userId, long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByU_C_C_PrevAndNext(
				session, mbMessage, userId, classNameId, classPK,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByU_C_C_PrevAndNext(
				session, mbMessage, userId, classNameId, classPK,
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

	protected MBMessage getByU_C_C_PrevAndNext(
		Session session, MBMessage mbMessage, long userId, long classNameId,
		long classPK, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_U_C_C_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_U_C_C_CLASSPK_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByU_C_C(long userId, long classNameId, long classPK) {
		for (MBMessage mbMessage :
				findByU_C_C(
					userId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_C(long userId, long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByU_C_C;

			Object[] finderArgs = new Object[] {userId, classNameId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

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

	private static final String _FINDER_COLUMN_U_C_C_USERID_2 =
		"mbMessage.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_CLASSNAMEID_2 =
		"mbMessage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_CLASSPK_2 =
		"mbMessage.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByU_C_S;
	private FinderPath _finderPathWithoutPaginationFindByU_C_S;
	private FinderPath _finderPathCountByU_C_S;
	private FinderPath _finderPathWithPaginationCountByU_C_S;

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long classNameId, int status) {

		return findByU_C_S(
			userId, classNameId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long classNameId, int status, int start, int end) {

		return findByU_C_S(userId, classNameId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long classNameId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByU_C_S(
			userId, classNameId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long classNameId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByU_C_S;
					finderArgs = new Object[] {userId, classNameId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByU_C_S;
				finderArgs = new Object[] {
					userId, classNameId, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((userId != mbMessage.getUserId()) ||
							(classNameId != mbMessage.getClassNameId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_S_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_S_First(
			long userId, long classNameId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_S_First(
			userId, classNameId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_S_First(
		long userId, long classNameId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByU_C_S(
			userId, classNameId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_S_Last(
			long userId, long classNameId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_S_Last(
			userId, classNameId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_S_Last(
		long userId, long classNameId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByU_C_S(userId, classNameId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByU_C_S(
			userId, classNameId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByU_C_S_PrevAndNext(
			long messageId, long userId, long classNameId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByU_C_S_PrevAndNext(
				session, mbMessage, userId, classNameId, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByU_C_S_PrevAndNext(
				session, mbMessage, userId, classNameId, status,
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

	protected MBMessage getByU_C_S_PrevAndNext(
		Session session, MBMessage mbMessage, long userId, long classNameId,
		int status, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_U_C_S_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_S_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_U_C_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(classNameId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long[] classNameIds, int status) {

		return findByU_C_S(
			userId, classNameIds, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long[] classNameIds, int status, int start, int end) {

		return findByU_C_S(userId, classNameIds, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long[] classNameIds, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByU_C_S(
			userId, classNameIds, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_S(
		long userId, long[] classNameIds, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		if (classNameIds == null) {
			classNameIds = new long[0];
		}
		else if (classNameIds.length > 1) {
			classNameIds = ArrayUtil.sortedUnique(classNameIds);
		}

		if (classNameIds.length == 1) {
			return findByU_C_S(
				userId, classNameIds[0], status, start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						userId, StringUtil.merge(classNameIds), status
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					userId, StringUtil.merge(classNameIds), status, start, end,
					orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					_finderPathWithPaginationFindByU_C_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((userId != mbMessage.getUserId()) ||
							!ArrayUtil.contains(
								classNameIds, mbMessage.getClassNameId()) ||
							(status != mbMessage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_S_USERID_2);

				if (classNameIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_U_C_S_CLASSNAMEID_7);

					sb.append(StringUtil.merge(classNameIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_U_C_S_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByU_C_S, finderArgs,
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
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 */
	@Override
	public void removeByU_C_S(long userId, long classNameId, int status) {
		for (MBMessage mbMessage :
				findByU_C_S(
					userId, classNameId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_S(long userId, long classNameId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByU_C_S;

			Object[] finderArgs = new Object[] {userId, classNameId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_S_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

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
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = any &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameIds the class name IDs
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_S(long userId, long[] classNameIds, int status) {
		if (classNameIds == null) {
			classNameIds = new long[0];
		}
		else if (classNameIds.length > 1) {
			classNameIds = ArrayUtil.sortedUnique(classNameIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			Object[] finderArgs = new Object[] {
				userId, StringUtil.merge(classNameIds), status
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByU_C_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_S_USERID_2);

				if (classNameIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_U_C_S_CLASSNAMEID_7);

					sb.append(StringUtil.merge(classNameIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_U_C_S_STATUS_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByU_C_S, finderArgs,
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

	private static final String _FINDER_COLUMN_U_C_S_USERID_2 =
		"mbMessage.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_S_CLASSNAMEID_2 =
		"mbMessage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_S_CLASSNAMEID_7 =
		"mbMessage.classNameId IN (";

	private static final String _FINDER_COLUMN_U_C_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_S;
	private FinderPath _finderPathWithoutPaginationFindByC_C_S;
	private FinderPath _finderPathCountByC_C_S;

	/**
	 * Returns all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status) {

		return findByC_C_S(
			classNameId, classPK, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end) {

		return findByC_C_S(classNameId, classPK, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return findByC_C_S(
			classNameId, classPK, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C_S;
					finderArgs = new Object[] {classNameId, classPK, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C_S;
				finderArgs = new Object[] {
					classNameId, classPK, status, start, end, orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((classNameId != mbMessage.getClassNameId()) ||
							(classPK != mbMessage.getClassPK()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_S_CLASSPK_2);

				sb.append(_FINDER_COLUMN_C_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_C_S_First(
			long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByC_C_S_First(
			classNameId, classPK, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_C_S_First(
		long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByC_C_S(
			classNameId, classPK, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByC_C_S_Last(
			long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByC_C_S_Last(
			classNameId, classPK, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByC_C_S_Last(
		long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByC_C_S(classNameId, classPK, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByC_C_S(
			classNameId, classPK, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByC_C_S_PrevAndNext(
			long messageId, long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByC_C_S_PrevAndNext(
				session, mbMessage, classNameId, classPK, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByC_C_S_PrevAndNext(
				session, mbMessage, classNameId, classPK, status,
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

	protected MBMessage getByC_C_S_PrevAndNext(
		Session session, MBMessage mbMessage, long classNameId, long classPK,
		int status, OrderByComparator<MBMessage> orderByComparator,
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

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_C_C_S_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_S_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	@Override
	public void removeByC_C_S(long classNameId, long classPK, int status) {
		for (MBMessage mbMessage :
				findByC_C_S(
					classNameId, classPK, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByC_C_S(long classNameId, long classPK, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByC_C_S;

			Object[] finderArgs = new Object[] {classNameId, classPK, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_S_CLASSPK_2);

				sb.append(_FINDER_COLUMN_C_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_C_C_S_CLASSNAMEID_2 =
		"mbMessage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_S_CLASSPK_2 =
		"mbMessage.classPK = ? AND ";

	private static final String _FINDER_COLUMN_C_C_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_T_A;
	private FinderPath _finderPathWithoutPaginationFindByG_C_T_A;
	private FinderPath _finderPathCountByG_C_T_A;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return findByG_C_T_A(
			groupId, categoryId, threadId, answer, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end) {

		return findByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return findByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_T_A;
					finderArgs = new Object[] {
						groupId, categoryId, threadId, answer
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_T_A;
				finderArgs = new Object[] {
					groupId, categoryId, threadId, answer, start, end,
					orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(categoryId != mbMessage.getCategoryId()) ||
							(threadId != mbMessage.getThreadId()) ||
							(answer != mbMessage.isAnswer())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_A_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_T_A_THREADID_2);

				sb.append(_FINDER_COLUMN_G_C_T_A_ANSWER_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(threadId);

					queryPos.add(answer);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_A_First(
			long groupId, long categoryId, long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_T_A_First(
			groupId, categoryId, threadId, answer, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", threadId=");
		sb.append(threadId);

		sb.append(", answer=");
		sb.append(answer);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_A_First(
		long groupId, long categoryId, long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_C_T_A(
			groupId, categoryId, threadId, answer, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_A_Last(
			long groupId, long categoryId, long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_T_A_Last(
			groupId, categoryId, threadId, answer, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", threadId=");
		sb.append(threadId);

		sb.append(", answer=");
		sb.append(answer);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_A_Last(
		long groupId, long categoryId, long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_C_T_A(groupId, categoryId, threadId, answer);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_C_T_A(
			groupId, categoryId, threadId, answer, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_C_T_A_PrevAndNext(
			long messageId, long groupId, long categoryId, long threadId,
			boolean answer, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_C_T_A_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, answer,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByG_C_T_A_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, answer,
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

	protected MBMessage getByG_C_T_A_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_T_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_ANSWER_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(threadId);

		queryPos.add(answer);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return filterFindByG_C_T_A(
			groupId, categoryId, threadId, answer, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end) {

		return filterFindByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_T_A(
				groupId, categoryId, threadId, answer, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_T_A(
					groupId, categoryId, threadId, answer, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_T_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_ANSWER_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(threadId);

			queryPos.add(answer);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_C_T_A_PrevAndNext(
			long messageId, long groupId, long categoryId, long threadId,
			boolean answer, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_T_A_PrevAndNext(
				messageId, groupId, categoryId, threadId, answer,
				orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_C_T_A_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, answer,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByG_C_T_A_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, answer,
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

	protected MBMessage filterGetByG_C_T_A_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_T_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_ANSWER_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(threadId);

		queryPos.add(answer);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 */
	@Override
	public void removeByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		for (MBMessage mbMessage :
				findByG_C_T_A(
					groupId, categoryId, threadId, answer, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_C_T_A;

			Object[] finderArgs = new Object[] {
				groupId, categoryId, threadId, answer
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_A_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_T_A_THREADID_2);

				sb.append(_FINDER_COLUMN_G_C_T_A_ANSWER_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(threadId);

					queryPos.add(answer);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_T_A(groupId, categoryId, threadId, answer);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_C_T_A(
				groupId, categoryId, threadId, answer);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_T_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_A_ANSWER_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(threadId);

			queryPos.add(answer);

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

	private static final String _FINDER_COLUMN_G_C_T_A_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_A_CATEGORYID_2 =
		"mbMessage.categoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_A_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_A_ANSWER_2 =
		"mbMessage.answer = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_T_S;
	private FinderPath _finderPathWithoutPaginationFindByG_C_T_S;
	private FinderPath _finderPathCountByG_C_T_S;

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return findByG_C_T_S(
			groupId, categoryId, threadId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end) {

		return findByG_C_T_S(
			groupId, categoryId, threadId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return findByG_C_T_S(
			groupId, categoryId, threadId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_T_S;
					finderArgs = new Object[] {
						groupId, categoryId, threadId, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_T_S;
				finderArgs = new Object[] {
					groupId, categoryId, threadId, status, start, end,
					orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((groupId != mbMessage.getGroupId()) ||
							(categoryId != mbMessage.getCategoryId()) ||
							(threadId != mbMessage.getThreadId()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_S_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_T_S_THREADID_2);

				sb.append(_FINDER_COLUMN_G_C_T_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(threadId);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_S_First(
			long groupId, long categoryId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_T_S_First(
			groupId, categoryId, threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", threadId=");
		sb.append(threadId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_S_First(
		long groupId, long categoryId, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByG_C_T_S(
			groupId, categoryId, threadId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByG_C_T_S_Last(
			long groupId, long categoryId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByG_C_T_S_Last(
			groupId, categoryId, threadId, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", categoryId=");
		sb.append(categoryId);

		sb.append(", threadId=");
		sb.append(threadId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByG_C_T_S_Last(
		long groupId, long categoryId, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByG_C_T_S(groupId, categoryId, threadId, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByG_C_T_S(
			groupId, categoryId, threadId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByG_C_T_S_PrevAndNext(
			long messageId, long groupId, long categoryId, long threadId,
			int status, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByG_C_T_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByG_C_T_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, status,
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

	protected MBMessage getByG_C_T_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_T_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(threadId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return filterFindByG_C_T_S(
			groupId, categoryId, threadId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end) {

		return filterFindByG_C_T_S(
			groupId, categoryId, threadId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	@Override
	public List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_T_S(
				groupId, categoryId, threadId, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_T_S(
					groupId, categoryId, threadId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_T_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(threadId);

			queryPos.add(status);

			return (List<MBMessage>)QueryUtil.list(
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
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] filterFindByG_C_T_S_PrevAndNext(
			long messageId, long groupId, long categoryId, long threadId,
			int status, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_T_S_PrevAndNext(
				messageId, groupId, categoryId, threadId, status,
				orderByComparator);
		}

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = filterGetByG_C_T_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = filterGetByG_C_T_S_PrevAndNext(
				session, mbMessage, groupId, categoryId, threadId, status,
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

	protected MBMessage filterGetByG_C_T_S_PrevAndNext(
		Session session, MBMessage mbMessage, long groupId, long categoryId,
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_T_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(MBMessageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(MBMessageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, MBMessageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, MBMessageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(categoryId);

		queryPos.add(threadId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 */
	@Override
	public void removeByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		for (MBMessage mbMessage :
				findByG_C_T_S(
					groupId, categoryId, threadId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByG_C_T_S;

			Object[] finderArgs = new Object[] {
				groupId, categoryId, threadId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_T_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_T_S_CATEGORYID_2);

				sb.append(_FINDER_COLUMN_G_C_T_S_THREADID_2);

				sb.append(_FINDER_COLUMN_G_C_T_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(categoryId);

					queryPos.add(threadId);

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
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_T_S(groupId, categoryId, threadId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<MBMessage> mbMessages = findByG_C_T_S(
				groupId, categoryId, threadId, status);

			mbMessages = InlineSQLHelperUtil.filter(mbMessages, groupId);

			return mbMessages.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_T_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_CATEGORYID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_THREADID_2);

		sb.append(_FINDER_COLUMN_G_C_T_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), MBMessage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(categoryId);

			queryPos.add(threadId);

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

	private static final String _FINDER_COLUMN_G_C_T_S_GROUPID_2 =
		"mbMessage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_S_CATEGORYID_2 =
		"mbMessage.categoryId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_S_THREADID_2 =
		"mbMessage.threadId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_T_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathWithPaginationFindByU_C_C_S;
	private FinderPath _finderPathWithoutPaginationFindByU_C_C_S;
	private FinderPath _finderPathCountByU_C_C_S;

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		return findByU_C_C_S(
			userId, classNameId, classPK, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end) {

		return findByU_C_C_S(
			userId, classNameId, classPK, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return findByU_C_C_S(
			userId, classNameId, classPK, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	@Override
	public List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByU_C_C_S;
					finderArgs = new Object[] {
						userId, classNameId, classPK, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByU_C_C_S;
				finderArgs = new Object[] {
					userId, classNameId, classPK, status, start, end,
					orderByComparator
				};
			}

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (MBMessage mbMessage : list) {
						if ((userId != mbMessage.getUserId()) ||
							(classNameId != mbMessage.getClassNameId()) ||
							(classPK != mbMessage.getClassPK()) ||
							(status != mbMessage.getStatus())) {

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

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_C_S_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_C_S_CLASSPK_2);

				sb.append(_FINDER_COLUMN_U_C_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					queryPos.add(status);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_C_S_First(
			long userId, long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_C_S_First(
			userId, classNameId, classPK, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_C_S_First(
		long userId, long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		List<MBMessage> list = findByU_C_C_S(
			userId, classNameId, classPK, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByU_C_C_S_Last(
			long userId, long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByU_C_C_S_Last(
			userId, classNameId, classPK, status, orderByComparator);

		if (mbMessage != null) {
			return mbMessage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchMessageException(sb.toString());
	}

	/**
	 * Returns the last message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByU_C_C_S_Last(
		long userId, long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		int count = countByU_C_C_S(userId, classNameId, classPK, status);

		if (count == 0) {
			return null;
		}

		List<MBMessage> list = findByU_C_C_S(
			userId, classNameId, classPK, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the message-boards messages before and after the current message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param messageId the primary key of the current message-boards message
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage[] findByU_C_C_S_PrevAndNext(
			long messageId, long userId, long classNameId, long classPK,
			int status, OrderByComparator<MBMessage> orderByComparator)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByPrimaryKey(messageId);

		Session session = null;

		try {
			session = openSession();

			MBMessage[] array = new MBMessageImpl[3];

			array[0] = getByU_C_C_S_PrevAndNext(
				session, mbMessage, userId, classNameId, classPK, status,
				orderByComparator, true);

			array[1] = mbMessage;

			array[2] = getByU_C_C_S_PrevAndNext(
				session, mbMessage, userId, classNameId, classPK, status,
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

	protected MBMessage getByU_C_C_S_PrevAndNext(
		Session session, MBMessage mbMessage, long userId, long classNameId,
		long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

		sb.append(_FINDER_COLUMN_U_C_C_S_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_C_S_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_U_C_C_S_CLASSPK_2);

		sb.append(_FINDER_COLUMN_U_C_C_S_STATUS_2);

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
			sb.append(MBMessageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mbMessage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MBMessage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	@Override
	public void removeByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		for (MBMessage mbMessage :
				findByU_C_C_S(
					userId, classNameId, classPK, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			FinderPath finderPath = _finderPathCountByU_C_C_S;

			Object[] finderArgs = new Object[] {
				userId, classNameId, classPK, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_MBMESSAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_C_C_S_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_U_C_C_S_CLASSPK_2);

				sb.append(_FINDER_COLUMN_U_C_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_U_C_C_S_USERID_2 =
		"mbMessage.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_S_CLASSNAMEID_2 =
		"mbMessage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_S_CLASSPK_2 =
		"mbMessage.classPK = ? AND ";

	private static final String _FINDER_COLUMN_U_C_C_S_STATUS_2 =
		"mbMessage.status = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByERC_G(externalReferenceCode, groupId);

		if (mbMessage == null) {
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

			throw new NoSuchMessageException(sb.toString());
		}

		return mbMessage;
	}

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByERC_G(String externalReferenceCode, long groupId) {
		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			if (result instanceof MBMessage) {
				MBMessage mbMessage = (MBMessage)result;

				if (!Objects.equals(
						externalReferenceCode,
						mbMessage.getExternalReferenceCode()) ||
					(groupId != mbMessage.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_MBMESSAGE_WHERE);

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

					List<MBMessage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						MBMessage mbMessage = list.get(0);

						result = mbMessage;

						cacheResult(mbMessage);
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
				return (MBMessage)result;
			}
		}
	}

	/**
	 * Removes the message-boards message where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the message-boards message that was removed
	 */
	@Override
	public MBMessage removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchMessageException {

		MBMessage mbMessage = findByERC_G(externalReferenceCode, groupId);

		return remove(mbMessage);
	}

	/**
	 * Returns the number of message-boards messages where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		MBMessage mbMessage = fetchByERC_G(externalReferenceCode, groupId);

		if (mbMessage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"mbMessage.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(mbMessage.externalReferenceCode IS NULL OR mbMessage.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"mbMessage.groupId = ?";

	public MBMessagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBMessage.class);

		setModelImplClass(MBMessageImpl.class);
		setModelPKClass(long.class);

		setTable(MBMessageTable.INSTANCE);
	}

	/**
	 * Caches the message-boards message in the entity cache if it is enabled.
	 *
	 * @param mbMessage the message-boards message
	 */
	@Override
	public void cacheResult(MBMessage mbMessage) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					mbMessage.getCtCollectionId())) {

			entityCache.putResult(
				MBMessageImpl.class, mbMessage.getPrimaryKey(), mbMessage);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {mbMessage.getUuid(), mbMessage.getGroupId()},
				mbMessage);

			finderCache.putResult(
				_finderPathFetchByG_US,
				new Object[] {
					mbMessage.getGroupId(), mbMessage.getUrlSubject()
				},
				mbMessage);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					mbMessage.getExternalReferenceCode(), mbMessage.getGroupId()
				},
				mbMessage);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the message-boards messages in the entity cache if it is enabled.
	 *
	 * @param mbMessages the message-boards messages
	 */
	@Override
	public void cacheResult(List<MBMessage> mbMessages) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (mbMessages.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (MBMessage mbMessage : mbMessages) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						mbMessage.getCtCollectionId())) {

				if (entityCache.getResult(
						MBMessageImpl.class, mbMessage.getPrimaryKey()) ==
							null) {

					cacheResult(mbMessage);
				}
			}
		}
	}

	/**
	 * Clears the cache for all message-boards messages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(MBMessageImpl.class);

		finderCache.clearCache(MBMessageImpl.class);
	}

	/**
	 * Clears the cache for the message-boards message.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(MBMessage mbMessage) {
		entityCache.removeResult(MBMessageImpl.class, mbMessage);
	}

	@Override
	public void clearCache(List<MBMessage> mbMessages) {
		for (MBMessage mbMessage : mbMessages) {
			entityCache.removeResult(MBMessageImpl.class, mbMessage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(MBMessageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(MBMessageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		MBMessageModelImpl mbMessageModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					mbMessageModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				mbMessageModelImpl.getUuid(), mbMessageModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, mbMessageModelImpl);

			args = new Object[] {
				mbMessageModelImpl.getGroupId(),
				mbMessageModelImpl.getUrlSubject()
			};

			finderCache.putResult(
				_finderPathFetchByG_US, args, mbMessageModelImpl);

			args = new Object[] {
				mbMessageModelImpl.getExternalReferenceCode(),
				mbMessageModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, mbMessageModelImpl);
		}
	}

	/**
	 * Creates a new message-boards message with the primary key. Does not add the message-boards message to the database.
	 *
	 * @param messageId the primary key for the new message-boards message
	 * @return the new message-boards message
	 */
	@Override
	public MBMessage create(long messageId) {
		MBMessage mbMessage = new MBMessageImpl();

		mbMessage.setNew(true);
		mbMessage.setPrimaryKey(messageId);

		String uuid = PortalUUIDUtil.generate();

		mbMessage.setUuid(uuid);

		mbMessage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbMessage;
	}

	/**
	 * Removes the message-boards message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message that was removed
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage remove(long messageId) throws NoSuchMessageException {
		return remove((Serializable)messageId);
	}

	/**
	 * Removes the message-boards message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the message-boards message
	 * @return the message-boards message that was removed
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage remove(Serializable primaryKey)
		throws NoSuchMessageException {

		Session session = null;

		try {
			session = openSession();

			MBMessage mbMessage = (MBMessage)session.get(
				MBMessageImpl.class, primaryKey);

			if (mbMessage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchMessageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(mbMessage);
		}
		catch (NoSuchMessageException noSuchEntityException) {
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
	protected MBMessage removeImpl(MBMessage mbMessage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbMessage)) {
				mbMessage = (MBMessage)session.get(
					MBMessageImpl.class, mbMessage.getPrimaryKeyObj());
			}

			if ((mbMessage != null) &&
				ctPersistenceHelper.isRemove(mbMessage)) {

				session.delete(mbMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbMessage != null) {
			clearCache(mbMessage);
		}

		return mbMessage;
	}

	@Override
	public MBMessage updateImpl(MBMessage mbMessage) {
		boolean isNew = mbMessage.isNew();

		if (!(mbMessage instanceof MBMessageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbMessage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(mbMessage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbMessage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBMessage implementation " +
					mbMessage.getClass());
		}

		MBMessageModelImpl mbMessageModelImpl = (MBMessageModelImpl)mbMessage;

		if (Validator.isNull(mbMessage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbMessage.setUuid(uuid);
		}

		if (Validator.isNull(mbMessage.getExternalReferenceCode())) {
			mbMessage.setExternalReferenceCode(mbMessage.getUuid());
		}
		else {
			if (!Objects.equals(
					mbMessageModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					mbMessage.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = mbMessage.getCompanyId();

					long groupId = mbMessage.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = mbMessage.getPrimaryKey();
					}

					try {
						mbMessage.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								MBMessage.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								mbMessage.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			MBMessage ercMBMessage = fetchByERC_G(
				mbMessage.getExternalReferenceCode(), mbMessage.getGroupId());

			if (isNew) {
				if (ercMBMessage != null) {
					throw new DuplicateMBMessageExternalReferenceCodeException(
						"Duplicate message-boards message with external reference code " +
							mbMessage.getExternalReferenceCode() +
								" and group " + mbMessage.getGroupId());
				}
			}
			else {
				if ((ercMBMessage != null) &&
					(mbMessage.getMessageId() != ercMBMessage.getMessageId())) {

					throw new DuplicateMBMessageExternalReferenceCodeException(
						"Duplicate message-boards message with external reference code " +
							mbMessage.getExternalReferenceCode() +
								" and group " + mbMessage.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbMessage.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbMessage.setCreateDate(date);
			}
			else {
				mbMessage.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbMessageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbMessage.setModifiedDate(date);
			}
			else {
				mbMessage.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = mbMessage.getCompanyId();

			long groupId = mbMessage.getGroupId();

			long messageId = 0;

			if (!isNew) {
				messageId = mbMessage.getPrimaryKey();
			}

			try {
				mbMessage.setSubject(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, MBMessage.class.getName(),
						messageId, ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						mbMessage.getSubject(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbMessage)) {
				if (!isNew) {
					session.evict(
						MBMessageImpl.class, mbMessage.getPrimaryKeyObj());
				}

				session.save(mbMessage);
			}
			else {
				mbMessage = (MBMessage)session.merge(mbMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			MBMessageImpl.class, mbMessageModelImpl, false, true);

		cacheUniqueFindersCache(mbMessageModelImpl);

		if (isNew) {
			mbMessage.setNew(false);
		}

		mbMessage.resetOriginalValues();

		return mbMessage;
	}

	/**
	 * Returns the message-boards message with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchMessageException {

		MBMessage mbMessage = fetchByPrimaryKey(primaryKey);

		if (mbMessage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchMessageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return mbMessage;
	}

	/**
	 * Returns the message-boards message with the primary key or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage findByPrimaryKey(long messageId)
		throws NoSuchMessageException {

		return findByPrimaryKey((Serializable)messageId);
	}

	/**
	 * Returns the message-boards message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the message-boards message
	 * @return the message-boards message, or <code>null</code> if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(MBMessage.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		MBMessage mbMessage = (MBMessage)entityCache.getResult(
			MBMessageImpl.class, primaryKey);

		if (mbMessage != null) {
			return mbMessage;
		}

		Session session = null;

		try {
			session = openSession();

			mbMessage = (MBMessage)session.get(MBMessageImpl.class, primaryKey);

			if (mbMessage != null) {
				cacheResult(mbMessage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return mbMessage;
	}

	/**
	 * Returns the message-boards message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message, or <code>null</code> if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage fetchByPrimaryKey(long messageId) {
		return fetchByPrimaryKey((Serializable)messageId);
	}

	@Override
	public Map<Serializable, MBMessage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(MBMessage.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, MBMessage> map =
			new HashMap<Serializable, MBMessage>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			MBMessage mbMessage = fetchByPrimaryKey(primaryKey);

			if (mbMessage != null) {
				map.put(primaryKey, mbMessage);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						MBMessage.class, primaryKey)) {

				MBMessage mbMessage = (MBMessage)entityCache.getResult(
					MBMessageImpl.class, primaryKey);

				if (mbMessage == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, mbMessage);
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

			for (MBMessage mbMessage : (List<MBMessage>)query.list()) {
				map.put(mbMessage.getPrimaryKeyObj(), mbMessage);

				cacheResult(mbMessage);
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
	 * Returns all the message-boards messages.
	 *
	 * @return the message-boards messages
	 */
	@Override
	public List<MBMessage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the message-boards messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of message-boards messages
	 */
	@Override
	public List<MBMessage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the message-boards messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of message-boards messages
	 */
	@Override
	public List<MBMessage> findAll(
		int start, int end, OrderByComparator<MBMessage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the message-boards messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of message-boards messages
	 */
	@Override
	public List<MBMessage> findAll(
		int start, int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

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

			List<MBMessage> list = null;

			if (useFinderCache) {
				list = (List<MBMessage>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_MBMESSAGE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_MBMESSAGE;

					sql = sql.concat(MBMessageModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<MBMessage>)QueryUtil.list(
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
	 * Removes all the message-boards messages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (MBMessage mbMessage : findAll()) {
			remove(mbMessage);
		}
	}

	/**
	 * Returns the number of message-boards messages.
	 *
	 * @return the number of message-boards messages
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					MBMessage.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_MBMESSAGE);

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
		return "messageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBMESSAGE;
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
		return MBMessageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBMessage";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("categoryId");
		ctMergeColumnNames.add("threadId");
		ctMergeColumnNames.add("rootMessageId");
		ctMergeColumnNames.add("parentMessageId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("subject");
		ctMergeColumnNames.add("urlSubject");
		ctMergeColumnNames.add("body");
		ctMergeColumnNames.add("format");
		ctMergeColumnNames.add("anonymous");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("allowPingbacks");
		ctMergeColumnNames.add("answer");
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
			CTColumnResolutionType.PK, Collections.singleton("messageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "urlSubject"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the message-boards message persistence.
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

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

		_finderPathWithPaginationFindByThreadId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByThreadId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"threadId"}, true);

		_finderPathWithoutPaginationFindByThreadId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByThreadId",
			new String[] {Long.class.getName()}, new String[] {"threadId"},
			true);

		_finderPathCountByThreadId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByThreadId",
			new String[] {Long.class.getName()}, new String[] {"threadId"},
			false);

		_finderPathWithPaginationFindByThreadIdReplies = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByThreadIdReplies",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"threadId"}, true);

		_finderPathWithoutPaginationFindByThreadIdReplies = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByThreadIdReplies",
			new String[] {Long.class.getName()}, new String[] {"threadId"},
			true);

		_finderPathCountByThreadIdReplies = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByThreadIdReplies",
			new String[] {Long.class.getName()}, new String[] {"threadId"},
			false);

		_finderPathWithPaginationFindByParentMessageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByParentMessageId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parentMessageId"}, true);

		_finderPathWithoutPaginationFindByParentMessageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByParentMessageId",
			new String[] {Long.class.getName()},
			new String[] {"parentMessageId"}, true);

		_finderPathCountByParentMessageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByParentMessageId",
			new String[] {Long.class.getName()},
			new String[] {"parentMessageId"}, false);

		_finderPathWithPaginationFindByG_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId"}, true);

		_finderPathWithoutPaginationFindByG_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "userId"}, true);

		_finderPathCountByG_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "userId"}, false);

		_finderPathWithPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "categoryId"}, true);

		_finderPathWithoutPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "categoryId"}, true);

		_finderPathCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "categoryId"}, false);

		_finderPathFetchByG_US = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_US",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "urlSubject"}, true);

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

		_finderPathWithPaginationFindByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"userId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "classNameId"}, true);

		_finderPathCountByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "classNameId"}, false);

		_finderPathWithPaginationCountByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "classNameId"}, false);

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, false);

		_finderPathWithPaginationFindByT_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"threadId", "parentMessageId"}, true);

		_finderPathWithoutPaginationFindByT_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"threadId", "parentMessageId"}, true);

		_finderPathCountByT_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"threadId", "parentMessageId"}, false);

		_finderPathWithPaginationFindByT_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"threadId", "answer"}, true);

		_finderPathWithoutPaginationFindByT_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"threadId", "answer"}, true);

		_finderPathCountByT_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"threadId", "answer"}, false);

		_finderPathWithPaginationFindByT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"threadId", "status"}, true);

		_finderPathWithoutPaginationFindByT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"threadId", "status"}, true);

		_finderPathCountByT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"threadId", "status"}, false);

		_finderPathWithPaginationFindByT_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"threadId", "status"}, true);

		_finderPathWithPaginationCountByT_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByT_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"threadId", "status"}, false);

		_finderPathWithPaginationFindByTR_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTR_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"threadId", "status"}, true);

		_finderPathWithoutPaginationFindByTR_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTR_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"threadId", "status"}, true);

		_finderPathCountByTR_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTR_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"threadId", "status"}, false);

		_finderPathWithPaginationFindByP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"parentMessageId", "status"}, true);

		_finderPathWithoutPaginationFindByP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"parentMessageId", "status"}, true);

		_finderPathCountByP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"parentMessageId", "status"}, false);

		_finderPathWithPaginationFindByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathWithoutPaginationFindByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathCountByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationFindByG_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId"}, true);

		_finderPathWithoutPaginationFindByG_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId"}, true);

		_finderPathCountByG_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId"}, false);

		_finderPathWithPaginationFindByG_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "categoryId", "status"}, true);

		_finderPathWithoutPaginationFindByG_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "categoryId", "status"}, true);

		_finderPathCountByG_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "categoryId", "status"}, false);

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

		_finderPathCountByU_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK"}, false);

		_finderPathWithPaginationFindByU_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId", "classNameId", "status"}, true);

		_finderPathWithoutPaginationFindByU_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"userId", "classNameId", "status"}, true);

		_finderPathCountByU_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"userId", "classNameId", "status"}, false);

		_finderPathWithPaginationCountByU_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"userId", "classNameId", "status"}, false);

		_finderPathWithPaginationFindByC_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK", "status"}, true);

		_finderPathWithoutPaginationFindByC_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"classNameId", "classPK", "status"}, true);

		_finderPathCountByC_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"classNameId", "classPK", "status"}, false);

		_finderPathWithPaginationFindByG_C_T_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId", "answer"}, true);

		_finderPathWithoutPaginationFindByG_C_T_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId", "answer"}, true);

		_finderPathCountByG_C_T_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId", "answer"},
			false);

		_finderPathWithPaginationFindByG_C_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_T_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId", "status"}, true);

		_finderPathWithoutPaginationFindByG_C_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_T_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId", "status"}, true);

		_finderPathCountByG_C_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_T_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "categoryId", "threadId", "status"},
			false);

		_finderPathWithPaginationFindByU_C_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK", "status"}, true);

		_finderPathWithoutPaginationFindByU_C_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK", "status"}, true);

		_finderPathCountByU_C_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"userId", "classNameId", "classPK", "status"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		MBMessageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBMessageUtil.setPersistence(null);

		entityCache.removeCache(MBMessageImpl.class.getName());
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

	private static final String _SQL_SELECT_MBMESSAGE =
		"SELECT mbMessage FROM MBMessage mbMessage";

	private static final String _SQL_SELECT_MBMESSAGE_WHERE =
		"SELECT mbMessage FROM MBMessage mbMessage WHERE ";

	private static final String _SQL_COUNT_MBMESSAGE =
		"SELECT COUNT(mbMessage) FROM MBMessage mbMessage";

	private static final String _SQL_COUNT_MBMESSAGE_WHERE =
		"SELECT COUNT(mbMessage) FROM MBMessage mbMessage WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"mbMessage.rootMessageId";

	private static final String _FILTER_SQL_SELECT_MBMESSAGE_WHERE =
		"SELECT DISTINCT {mbMessage.*} FROM MBMessage mbMessage WHERE ";

	private static final String
		_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {MBMessage.*} FROM (SELECT DISTINCT mbMessage.messageId FROM MBMessage mbMessage WHERE ";

	private static final String
		_FILTER_SQL_SELECT_MBMESSAGE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN MBMessage ON TEMP_TABLE.messageId = MBMessage.messageId";

	private static final String _FILTER_SQL_COUNT_MBMESSAGE_WHERE =
		"SELECT COUNT(DISTINCT mbMessage.messageId) AS COUNT_VALUE FROM MBMessage mbMessage WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "mbMessage";

	private static final String _FILTER_ENTITY_TABLE = "MBMessage";

	private static final String _ORDER_BY_ENTITY_ALIAS = "mbMessage.";

	private static final String _ORDER_BY_ENTITY_TABLE = "MBMessage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No MBMessage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBMessage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBMessagePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}