/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.exception.DuplicateJournalFolderExternalReferenceCodeException;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderTable;
import com.liferay.journal.model.impl.JournalFolderImpl;
import com.liferay.journal.model.impl.JournalFolderModelImpl;
import com.liferay.journal.service.persistence.JournalFolderPersistence;
import com.liferay.journal.service.persistence.JournalFolderUtil;
import com.liferay.journal.service.persistence.impl.constants.JournalPersistenceConstants;
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
 * The persistence implementation for the journal folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = JournalFolderPersistence.class)
public class JournalFolderPersistenceImpl
	extends BasePersistenceImpl<JournalFolder>
	implements JournalFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JournalFolderUtil</code> to access the journal folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JournalFolderImpl.class.getName();

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
	 * Returns all the journal folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if (!uuid.equals(journalFolder.getUuid())) {
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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByUuid_First(
			String uuid, OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByUuid_First(
			uuid, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByUuid_First(
		String uuid, OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByUuid_Last(
			String uuid, OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByUuid_Last(uuid, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByUuid_Last(
		String uuid, OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where uuid = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByUuid_PrevAndNext(
			long folderId, String uuid,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, journalFolder, uuid, orderByComparator, true);

			array[1] = journalFolder;

			array[2] = getByUuid_PrevAndNext(
				session, journalFolder, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected JournalFolder getByUuid_PrevAndNext(
		Session session, JournalFolder journalFolder, String uuid,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (JournalFolder journalFolder :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

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
		"journalFolder.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(journalFolder.uuid IS NULL OR journalFolder.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the journal folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByUUID_G(uuid, groupId);

		if (journalFolder == null) {
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

			throw new NoSuchFolderException(sb.toString());
		}

		return journalFolder;
	}

	/**
	 * Returns the journal folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the journal folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			if (result instanceof JournalFolder) {
				JournalFolder journalFolder = (JournalFolder)result;

				if (!Objects.equals(uuid, journalFolder.getUuid()) ||
					(groupId != journalFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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

					List<JournalFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						JournalFolder journalFolder = list.get(0);

						result = journalFolder;

						cacheResult(journalFolder);
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
				return (JournalFolder)result;
			}
		}
	}

	/**
	 * Removes the journal folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the journal folder that was removed
	 */
	@Override
	public JournalFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByUUID_G(uuid, groupId);

		return remove(journalFolder);
	}

	/**
	 * Returns the number of journal folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		JournalFolder journalFolder = fetchByUUID_G(uuid, groupId);

		if (journalFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"journalFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(journalFolder.uuid IS NULL OR journalFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"journalFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the journal folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if (!uuid.equals(journalFolder.getUuid()) ||
							(companyId != journalFolder.getCompanyId())) {

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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByUuid_C_PrevAndNext(
			long folderId, String uuid, long companyId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, journalFolder, uuid, companyId, orderByComparator,
				true);

			array[1] = journalFolder;

			array[2] = getByUuid_C_PrevAndNext(
				session, journalFolder, uuid, companyId, orderByComparator,
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

	protected JournalFolder getByUuid_C_PrevAndNext(
		Session session, JournalFolder journalFolder, String uuid,
		long companyId, OrderByComparator<JournalFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (JournalFolder journalFolder :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

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
		"journalFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(journalFolder.uuid IS NULL OR journalFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"journalFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the journal folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if (groupId != journalFolder.getGroupId()) {
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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByGroupId_First(
			long groupId, OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByGroupId_First(
			groupId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByGroupId_First(
		long groupId, OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByGroupId_Last(
			long groupId, OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByGroupId_Last(
		long groupId, OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where groupId = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByGroupId_PrevAndNext(
			long folderId, long groupId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, journalFolder, groupId, orderByComparator, true);

			array[1] = journalFolder;

			array[2] = getByGroupId_PrevAndNext(
				session, journalFolder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected JournalFolder getByGroupId_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the journal folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the journal folders before and after the current journal folder in the ordered set of journal folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] filterFindByGroupId_PrevAndNext(
			long folderId, long groupId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				folderId, groupId, orderByComparator);
		}

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, journalFolder, groupId, orderByComparator, true);

			array[1] = journalFolder;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, journalFolder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected JournalFolder filterGetByGroupId_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (JournalFolder journalFolder :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

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
	 * Returns the number of journal folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal folders that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<JournalFolder> journalFolders = findByGroupId(groupId);

			journalFolders = InlineSQLHelperUtil.filter(
				journalFolders, groupId);

			return journalFolders.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
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
		"journalFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the journal folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if (companyId != journalFolder.getCompanyId()) {
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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByCompanyId_First(
			long companyId, OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByCompanyId_Last(
			long companyId, OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByCompanyId_Last(
		long companyId, OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where companyId = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByCompanyId_PrevAndNext(
			long folderId, long companyId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, journalFolder, companyId, orderByComparator, true);

			array[1] = journalFolder;

			array[2] = getByCompanyId_PrevAndNext(
				session, journalFolder, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected JournalFolder getByCompanyId_PrevAndNext(
		Session session, JournalFolder journalFolder, long companyId,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (JournalFolder journalFolder :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

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
		"journalFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the journal folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P(long groupId, long parentFolderId) {
		return findByG_P(
			groupId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the journal folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return findByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByG_P(
			groupId, parentFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, parentFolderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, parentFolderId, start, end, orderByComparator
				};
			}

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if ((groupId != journalFolder.getGroupId()) ||
							(parentFolderId !=
								journalFolder.getParentFolderId())) {

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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_First(
			long groupId, long parentFolderId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_First(
			groupId, parentFolderId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_First(
		long groupId, long parentFolderId,
		OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByG_P(
			groupId, parentFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_Last(
			long groupId, long parentFolderId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_Last(
			groupId, parentFolderId, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_Last(
		long groupId, long parentFolderId,
		OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByG_P(groupId, parentFolderId);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByG_P(
			groupId, parentFolderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByG_P_PrevAndNext(
			long folderId, long groupId, long parentFolderId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, journalFolder, groupId, parentFolderId,
				orderByComparator, true);

			array[1] = journalFolder;

			array[2] = getByG_P_PrevAndNext(
				session, journalFolder, groupId, parentFolderId,
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

	protected JournalFolder getByG_P_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		long parentFolderId, OrderByComparator<JournalFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P(
		long groupId, long parentFolderId) {

		return filterFindByG_P(
			groupId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return filterFindByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentFolderId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentFolderId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			return (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the journal folders before and after the current journal folder in the ordered set of journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] filterFindByG_P_PrevAndNext(
			long folderId, long groupId, long parentFolderId,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				folderId, groupId, parentFolderId, orderByComparator);
		}

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, journalFolder, groupId, parentFolderId,
				orderByComparator, true);

			array[1] = journalFolder;

			array[2] = filterGetByG_P_PrevAndNext(
				session, journalFolder, groupId, parentFolderId,
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

	protected JournalFolder filterGetByG_P_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		long parentFolderId, OrderByComparator<JournalFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where groupId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentFolderId) {
		for (JournalFolder journalFolder :
				findByG_P(
					groupId, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByG_P(long groupId, long parentFolderId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentFolderId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

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
	 * Returns the number of journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching journal folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentFolderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentFolderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<JournalFolder> journalFolders = findByG_P(
				groupId, parentFolderId);

			journalFolders = InlineSQLHelperUtil.filter(
				journalFolders, groupId);

			return journalFolders.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

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
		"journalFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTFOLDERID_2 =
		"journalFolder.parentFolderId = ?";

	private FinderPath _finderPathFetchByG_N;

	/**
	 * Returns the journal folder where groupId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_N(long groupId, String name)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_N(groupId, name);

		if (journalFolder == null) {
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

			throw new NoSuchFolderException(sb.toString());
		}

		return journalFolder;
	}

	/**
	 * Returns the journal folder where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_N(long groupId, String name) {
		return fetchByG_N(groupId, name, true);
	}

	/**
	 * Returns the journal folder where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_N(
		long groupId, String name, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_N, finderArgs, this);
			}

			if (result instanceof JournalFolder) {
				JournalFolder journalFolder = (JournalFolder)result;

				if ((groupId != journalFolder.getGroupId()) ||
					!Objects.equals(name, journalFolder.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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
						queryPos.add(name);
					}

					List<JournalFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_N, finderArgs, list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {groupId, name};
								}

								_log.warn(
									"JournalFolderPersistenceImpl.fetchByG_N(long, String, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						JournalFolder journalFolder = list.get(0);

						result = journalFolder;

						cacheResult(journalFolder);
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
				return (JournalFolder)result;
			}
		}
	}

	/**
	 * Removes the journal folder where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the journal folder that was removed
	 */
	@Override
	public JournalFolder removeByG_N(long groupId, String name)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByG_N(groupId, name);

		return remove(journalFolder);
	}

	/**
	 * Returns the number of journal folders where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		JournalFolder journalFolder = fetchByG_N(groupId, name);

		if (journalFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_N_GROUPID_2 =
		"journalFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_NAME_2 =
		"journalFolder.name = ?";

	private static final String _FINDER_COLUMN_G_N_NAME_3 =
		"(journalFolder.name IS NULL OR journalFolder.name = '')";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the journal folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if ((companyId != journalFolder.getCompanyId()) ||
							(status == journalFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByC_NotS_PrevAndNext(
			long folderId, long companyId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, journalFolder, companyId, status, orderByComparator,
				true);

			array[1] = journalFolder;

			array[2] = getByC_NotS_PrevAndNext(
				session, journalFolder, companyId, status, orderByComparator,
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

	protected JournalFolder getByC_NotS_PrevAndNext(
		Session session, JournalFolder journalFolder, long companyId,
		int status, OrderByComparator<JournalFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (JournalFolder journalFolder :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

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

	private static final String _FINDER_COLUMN_C_NOTS_COMPANYID_2 =
		"journalFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"journalFolder.status != ?";

	private FinderPath _finderPathFetchByG_P_N;

	/**
	 * Returns the journal folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_N(
			long groupId, long parentFolderId, String name)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_N(
			groupId, parentFolderId, name);

		if (journalFolder == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", parentFolderId=");
			sb.append(parentFolderId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFolderException(sb.toString());
		}

		return journalFolder;
	}

	/**
	 * Returns the journal folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name) {

		return fetchByG_P_N(groupId, parentFolderId, name, true);
	}

	/**
	 * Returns the journal folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, parentFolderId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_P_N, finderArgs, this);
			}

			if (result instanceof JournalFolder) {
				JournalFolder journalFolder = (JournalFolder)result;

				if ((groupId != journalFolder.getGroupId()) ||
					(parentFolderId != journalFolder.getParentFolderId()) ||
					!Objects.equals(name, journalFolder.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_N_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_N_PARENTFOLDERID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_P_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					if (bindName) {
						queryPos.add(name);
					}

					List<JournalFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_P_N, finderArgs, list);
						}
					}
					else {
						JournalFolder journalFolder = list.get(0);

						result = journalFolder;

						cacheResult(journalFolder);
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
				return (JournalFolder)result;
			}
		}
	}

	/**
	 * Removes the journal folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the journal folder that was removed
	 */
	@Override
	public JournalFolder removeByG_P_N(
			long groupId, long parentFolderId, String name)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByG_P_N(
			groupId, parentFolderId, name);

		return remove(journalFolder);
	}

	/**
	 * Returns the number of journal folders where groupId = &#63; and parentFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByG_P_N(long groupId, long parentFolderId, String name) {
		JournalFolder journalFolder = fetchByG_P_N(
			groupId, parentFolderId, name);

		if (journalFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_N_GROUPID_2 =
		"journalFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_PARENTFOLDERID_2 =
		"journalFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_NAME_2 =
		"journalFolder.name = ?";

	private static final String _FINDER_COLUMN_G_P_N_NAME_3 =
		"(journalFolder.name IS NULL OR journalFolder.name = '')";

	private FinderPath _finderPathWithPaginationFindByG_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_S;
	private FinderPath _finderPathCountByG_P_S;

	/**
	 * Returns all the journal folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_S(
		long groupId, long parentFolderId, int status) {

		return findByG_P_S(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end) {

		return findByG_P_S(groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByG_P_S(
			groupId, parentFolderId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_S;
					finderArgs = new Object[] {groupId, parentFolderId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_S;
				finderArgs = new Object[] {
					groupId, parentFolderId, status, start, end,
					orderByComparator
				};
			}

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if ((groupId != journalFolder.getGroupId()) ||
							(parentFolderId !=
								journalFolder.getParentFolderId()) ||
							(status != journalFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					queryPos.add(status);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_S_First(
			long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_S_First(
			groupId, parentFolderId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_S_First(
		long groupId, long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByG_P_S(
			groupId, parentFolderId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_S_Last(
			long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_S_Last(
			groupId, parentFolderId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_S_Last(
		long groupId, long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByG_P_S(groupId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByG_P_S(
			groupId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByG_P_S_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByG_P_S_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = journalFolder;

			array[2] = getByG_P_S_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
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

	protected JournalFolder getByG_P_S_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P_S(
		long groupId, long parentFolderId, int status) {

		return filterFindByG_P_S(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end) {

		return filterFindByG_P_S(
			groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S(
				groupId, parentFolderId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_S(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			queryPos.add(status);

			return (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the journal folders before and after the current journal folder in the ordered set of journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] filterFindByG_P_S_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S_PrevAndNext(
				folderId, groupId, parentFolderId, status, orderByComparator);
		}

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = filterGetByG_P_S_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = journalFolder;

			array[2] = filterGetByG_P_S_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
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

	protected JournalFolder filterGetByG_P_S_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where groupId = &#63; and parentFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(long groupId, long parentFolderId, int status) {
		for (JournalFolder journalFolder :
				findByG_P_S(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByG_P_S(long groupId, long parentFolderId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P_S;

			Object[] finderArgs = new Object[] {
				groupId, parentFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

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
	 * Returns the number of journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching journal folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentFolderId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_S(groupId, parentFolderId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<JournalFolder> journalFolders = findByG_P_S(
				groupId, parentFolderId, status);

			journalFolders = InlineSQLHelperUtil.filter(
				journalFolders, groupId);

			return journalFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

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
		"journalFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_PARENTFOLDERID_2 =
		"journalFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_STATUS_2 =
		"journalFolder.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_NotS;
	private FinderPath _finderPathWithPaginationCountByG_P_NotS;

	/**
	 * Returns all the journal folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		return findByG_P_NotS(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end) {

		return findByG_P_NotS(
			groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByG_P_NotS(
			groupId, parentFolderId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the journal folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_P_NotS;
			finderArgs = new Object[] {
				groupId, parentFolderId, status, start, end, orderByComparator
			};

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if ((groupId != journalFolder.getGroupId()) ||
							(parentFolderId !=
								journalFolder.getParentFolderId()) ||
							(status == journalFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					queryPos.add(status);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_NotS_First(
			long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_NotS_First(
			groupId, parentFolderId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_NotS_First(
		long groupId, long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByG_P_NotS(
			groupId, parentFolderId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByG_P_NotS_Last(
			long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByG_P_NotS_Last(
			groupId, parentFolderId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByG_P_NotS_Last(
		long groupId, long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByG_P_NotS(groupId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByG_P_NotS(
			groupId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the journal folders before and after the current journal folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] findByG_P_NotS_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = getByG_P_NotS_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = journalFolder;

			array[2] = getByG_P_NotS_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
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

	protected JournalFolder getByG_P_NotS_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

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
			sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		return filterFindByG_P_NotS(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end) {

		return filterFindByG_P_NotS(
			groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders that the user has permission to view
	 */
	@Override
	public List<JournalFolder> filterFindByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS(
				groupId, parentFolderId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_NotS(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			queryPos.add(status);

			return (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the journal folders before and after the current journal folder in the ordered set of journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current journal folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder[] filterFindByG_P_NotS_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS_PrevAndNext(
				folderId, groupId, parentFolderId, status, orderByComparator);
		}

		JournalFolder journalFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			JournalFolder[] array = new JournalFolderImpl[3];

			array[0] = filterGetByG_P_NotS_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = journalFolder;

			array[2] = filterGetByG_P_NotS_PrevAndNext(
				session, journalFolder, groupId, parentFolderId, status,
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

	protected JournalFolder filterGetByG_P_NotS_PrevAndNext(
		Session session, JournalFolder journalFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_JOURNALFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(JournalFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, JournalFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, JournalFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						journalFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<JournalFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the journal folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		for (JournalFolder journalFolder :
				findByG_P_NotS(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByG_P_NotS(long groupId, long parentFolderId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_P_NotS;

			Object[] finderArgs = new Object[] {
				groupId, parentFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

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
	 * Returns the number of journal folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching journal folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_NotS(groupId, parentFolderId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<JournalFolder> journalFolders = findByG_P_NotS(
				groupId, parentFolderId, status);

			journalFolders = InlineSQLHelperUtil.filter(
				journalFolders, groupId);

			return journalFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_JOURNALFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), JournalFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

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
		"journalFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2 =
		"journalFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_NOTS_STATUS_2 =
		"journalFolder.status != ?";

	private FinderPath _finderPathWithPaginationFindByGtF_C_P_NotS;
	private FinderPath _finderPathWithPaginationCountByGtF_C_P_NotS;

	/**
	 * Returns all the journal folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal folders
	 */
	@Override
	public List<JournalFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end, OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtF_C_P_NotS;
			finderArgs = new Object[] {
				folderId, companyId, parentFolderId, status, start, end,
				orderByComparator
			};

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (JournalFolder journalFolder : list) {
						if ((folderId >= journalFolder.getFolderId()) ||
							(companyId != journalFolder.getCompanyId()) ||
							(parentFolderId !=
								journalFolder.getParentFolderId()) ||
							(status == journalFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					queryPos.add(companyId);

					queryPos.add(parentFolderId);

					queryPos.add(status);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Returns the first journal folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByGtF_C_P_NotS_First(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByGtF_C_P_NotS_First(
			folderId, companyId, parentFolderId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId>");
		sb.append(folderId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first journal folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByGtF_C_P_NotS_First(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		List<JournalFolder> list = findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last journal folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByGtF_C_P_NotS_Last(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<JournalFolder> orderByComparator)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByGtF_C_P_NotS_Last(
			folderId, companyId, parentFolderId, status, orderByComparator);

		if (journalFolder != null) {
			return journalFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId>");
		sb.append(folderId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last journal folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByGtF_C_P_NotS_Last(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<JournalFolder> orderByComparator) {

		int count = countByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<JournalFolder> list = findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the journal folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		for (JournalFolder journalFolder :
				findByGtF_C_P_NotS(
					folderId, companyId, parentFolderId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			FinderPath finderPath =
				_finderPathWithPaginationCountByGtF_C_P_NotS;

			Object[] finderArgs = new Object[] {
				folderId, companyId, parentFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_JOURNALFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					queryPos.add(companyId);

					queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2 =
		"journalFolder.folderId > ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2 =
		"journalFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2 =
		"journalFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2 =
		"journalFolder.status != ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the journal folder where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching journal folder
	 * @throws NoSuchFolderException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByERC_G(
			externalReferenceCode, groupId);

		if (journalFolder == null) {
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

			throw new NoSuchFolderException(sb.toString());
		}

		return journalFolder;
	}

	/**
	 * Returns the journal folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the journal folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			if (result instanceof JournalFolder) {
				JournalFolder journalFolder = (JournalFolder)result;

				if (!Objects.equals(
						externalReferenceCode,
						journalFolder.getExternalReferenceCode()) ||
					(groupId != journalFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_JOURNALFOLDER_WHERE);

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

					List<JournalFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						JournalFolder journalFolder = list.get(0);

						result = journalFolder;

						cacheResult(journalFolder);
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
				return (JournalFolder)result;
			}
		}
	}

	/**
	 * Removes the journal folder where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the journal folder that was removed
	 */
	@Override
	public JournalFolder removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		JournalFolder journalFolder = findByERC_G(
			externalReferenceCode, groupId);

		return remove(journalFolder);
	}

	/**
	 * Returns the number of journal folders where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching journal folders
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		JournalFolder journalFolder = fetchByERC_G(
			externalReferenceCode, groupId);

		if (journalFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"journalFolder.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(journalFolder.externalReferenceCode IS NULL OR journalFolder.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"journalFolder.groupId = ?";

	public JournalFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(JournalFolder.class);

		setModelImplClass(JournalFolderImpl.class);
		setModelPKClass(long.class);

		setTable(JournalFolderTable.INSTANCE);
	}

	/**
	 * Caches the journal folder in the entity cache if it is enabled.
	 *
	 * @param journalFolder the journal folder
	 */
	@Override
	public void cacheResult(JournalFolder journalFolder) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					journalFolder.getCtCollectionId())) {

			entityCache.putResult(
				JournalFolderImpl.class, journalFolder.getPrimaryKey(),
				journalFolder);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					journalFolder.getUuid(), journalFolder.getGroupId()
				},
				journalFolder);

			finderCache.putResult(
				_finderPathFetchByG_N,
				new Object[] {
					journalFolder.getGroupId(), journalFolder.getName()
				},
				journalFolder);

			finderCache.putResult(
				_finderPathFetchByG_P_N,
				new Object[] {
					journalFolder.getGroupId(),
					journalFolder.getParentFolderId(), journalFolder.getName()
				},
				journalFolder);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					journalFolder.getExternalReferenceCode(),
					journalFolder.getGroupId()
				},
				journalFolder);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the journal folders in the entity cache if it is enabled.
	 *
	 * @param journalFolders the journal folders
	 */
	@Override
	public void cacheResult(List<JournalFolder> journalFolders) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (journalFolders.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (JournalFolder journalFolder : journalFolders) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						journalFolder.getCtCollectionId())) {

				if (entityCache.getResult(
						JournalFolderImpl.class,
						journalFolder.getPrimaryKey()) == null) {

					cacheResult(journalFolder);
				}
			}
		}
	}

	/**
	 * Clears the cache for all journal folders.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(JournalFolderImpl.class);

		finderCache.clearCache(JournalFolderImpl.class);
	}

	/**
	 * Clears the cache for the journal folder.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(JournalFolder journalFolder) {
		entityCache.removeResult(JournalFolderImpl.class, journalFolder);
	}

	@Override
	public void clearCache(List<JournalFolder> journalFolders) {
		for (JournalFolder journalFolder : journalFolders) {
			entityCache.removeResult(JournalFolderImpl.class, journalFolder);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(JournalFolderImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(JournalFolderImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		JournalFolderModelImpl journalFolderModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					journalFolderModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				journalFolderModelImpl.getUuid(),
				journalFolderModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, journalFolderModelImpl);

			args = new Object[] {
				journalFolderModelImpl.getGroupId(),
				journalFolderModelImpl.getName()
			};

			finderCache.putResult(
				_finderPathFetchByG_N, args, journalFolderModelImpl);

			args = new Object[] {
				journalFolderModelImpl.getGroupId(),
				journalFolderModelImpl.getParentFolderId(),
				journalFolderModelImpl.getName()
			};

			finderCache.putResult(
				_finderPathFetchByG_P_N, args, journalFolderModelImpl);

			args = new Object[] {
				journalFolderModelImpl.getExternalReferenceCode(),
				journalFolderModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, journalFolderModelImpl);
		}
	}

	/**
	 * Creates a new journal folder with the primary key. Does not add the journal folder to the database.
	 *
	 * @param folderId the primary key for the new journal folder
	 * @return the new journal folder
	 */
	@Override
	public JournalFolder create(long folderId) {
		JournalFolder journalFolder = new JournalFolderImpl();

		journalFolder.setNew(true);
		journalFolder.setPrimaryKey(folderId);

		String uuid = PortalUUIDUtil.generate();

		journalFolder.setUuid(uuid);

		journalFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return journalFolder;
	}

	/**
	 * Removes the journal folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder that was removed
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder remove(long folderId) throws NoSuchFolderException {
		return remove((Serializable)folderId);
	}

	/**
	 * Removes the journal folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the journal folder
	 * @return the journal folder that was removed
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder remove(Serializable primaryKey)
		throws NoSuchFolderException {

		Session session = null;

		try {
			session = openSession();

			JournalFolder journalFolder = (JournalFolder)session.get(
				JournalFolderImpl.class, primaryKey);

			if (journalFolder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFolderException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(journalFolder);
		}
		catch (NoSuchFolderException noSuchEntityException) {
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
	protected JournalFolder removeImpl(JournalFolder journalFolder) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(journalFolder)) {
				journalFolder = (JournalFolder)session.get(
					JournalFolderImpl.class, journalFolder.getPrimaryKeyObj());
			}

			if ((journalFolder != null) &&
				ctPersistenceHelper.isRemove(journalFolder)) {

				session.delete(journalFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (journalFolder != null) {
			clearCache(journalFolder);
		}

		return journalFolder;
	}

	@Override
	public JournalFolder updateImpl(JournalFolder journalFolder) {
		boolean isNew = journalFolder.isNew();

		if (!(journalFolder instanceof JournalFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(journalFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					journalFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in journalFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JournalFolder implementation " +
					journalFolder.getClass());
		}

		JournalFolderModelImpl journalFolderModelImpl =
			(JournalFolderModelImpl)journalFolder;

		if (Validator.isNull(journalFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			journalFolder.setUuid(uuid);
		}

		if (Validator.isNull(journalFolder.getExternalReferenceCode())) {
			journalFolder.setExternalReferenceCode(journalFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					journalFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					journalFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = journalFolder.getCompanyId();

					long groupId = journalFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = journalFolder.getPrimaryKey();
					}

					try {
						journalFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								JournalFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								journalFolder.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			JournalFolder ercJournalFolder = fetchByERC_G(
				journalFolder.getExternalReferenceCode(),
				journalFolder.getGroupId());

			if (isNew) {
				if (ercJournalFolder != null) {
					throw new DuplicateJournalFolderExternalReferenceCodeException(
						"Duplicate journal folder with external reference code " +
							journalFolder.getExternalReferenceCode() +
								" and group " + journalFolder.getGroupId());
				}
			}
			else {
				if ((ercJournalFolder != null) &&
					(journalFolder.getFolderId() !=
						ercJournalFolder.getFolderId())) {

					throw new DuplicateJournalFolderExternalReferenceCodeException(
						"Duplicate journal folder with external reference code " +
							journalFolder.getExternalReferenceCode() +
								" and group " + journalFolder.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (journalFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				journalFolder.setCreateDate(date);
			}
			else {
				journalFolder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!journalFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				journalFolder.setModifiedDate(date);
			}
			else {
				journalFolder.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(journalFolder)) {
				if (!isNew) {
					session.evict(
						JournalFolderImpl.class,
						journalFolder.getPrimaryKeyObj());
				}

				session.save(journalFolder);
			}
			else {
				journalFolder = (JournalFolder)session.merge(journalFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			JournalFolderImpl.class, journalFolderModelImpl, false, true);

		cacheUniqueFindersCache(journalFolderModelImpl);

		if (isNew) {
			journalFolder.setNew(false);
		}

		journalFolder.resetOriginalValues();

		return journalFolder;
	}

	/**
	 * Returns the journal folder with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the journal folder
	 * @return the journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFolderException {

		JournalFolder journalFolder = fetchByPrimaryKey(primaryKey);

		if (journalFolder == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFolderException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return journalFolder;
	}

	/**
	 * Returns the journal folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder
	 * @throws NoSuchFolderException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder findByPrimaryKey(long folderId)
		throws NoSuchFolderException {

		return findByPrimaryKey((Serializable)folderId);
	}

	/**
	 * Returns the journal folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the journal folder
	 * @return the journal folder, or <code>null</code> if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				JournalFolder.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		JournalFolder journalFolder = (JournalFolder)entityCache.getResult(
			JournalFolderImpl.class, primaryKey);

		if (journalFolder != null) {
			return journalFolder;
		}

		Session session = null;

		try {
			session = openSession();

			journalFolder = (JournalFolder)session.get(
				JournalFolderImpl.class, primaryKey);

			if (journalFolder != null) {
				cacheResult(journalFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return journalFolder;
	}

	/**
	 * Returns the journal folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder, or <code>null</code> if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder fetchByPrimaryKey(long folderId) {
		return fetchByPrimaryKey((Serializable)folderId);
	}

	@Override
	public Map<Serializable, JournalFolder> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(JournalFolder.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, JournalFolder> map =
			new HashMap<Serializable, JournalFolder>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			JournalFolder journalFolder = fetchByPrimaryKey(primaryKey);

			if (journalFolder != null) {
				map.put(primaryKey, journalFolder);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						JournalFolder.class, primaryKey)) {

				JournalFolder journalFolder =
					(JournalFolder)entityCache.getResult(
						JournalFolderImpl.class, primaryKey);

				if (journalFolder == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, journalFolder);
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

			for (JournalFolder journalFolder :
					(List<JournalFolder>)query.list()) {

				map.put(journalFolder.getPrimaryKeyObj(), journalFolder);

				cacheResult(journalFolder);
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
	 * Returns all the journal folders.
	 *
	 * @return the journal folders
	 */
	@Override
	public List<JournalFolder> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the journal folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of journal folders
	 */
	@Override
	public List<JournalFolder> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the journal folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of journal folders
	 */
	@Override
	public List<JournalFolder> findAll(
		int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the journal folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of journal folders
	 */
	@Override
	public List<JournalFolder> findAll(
		int start, int end, OrderByComparator<JournalFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

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

			List<JournalFolder> list = null;

			if (useFinderCache) {
				list = (List<JournalFolder>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_JOURNALFOLDER);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_JOURNALFOLDER;

					sql = sql.concat(JournalFolderModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<JournalFolder>)QueryUtil.list(
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
	 * Removes all the journal folders from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (JournalFolder journalFolder : findAll()) {
			remove(journalFolder);
		}
	}

	/**
	 * Returns the number of journal folders.
	 *
	 * @return the number of journal folders
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					JournalFolder.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_JOURNALFOLDER);

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
		return "folderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_JOURNALFOLDER;
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
		return JournalFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JournalFolder";
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
		ctMergeColumnNames.add("parentFolderId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("restrictionType");
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
			CTColumnResolutionType.PK, Collections.singleton("folderId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "parentFolderId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the journal folder persistence.
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
			new String[] {"groupId", "parentFolderId"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentFolderId"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentFolderId"}, false);

		_finderPathFetchByG_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "name"}, true);

		_finderPathWithPaginationFindByC_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "status"}, true);

		_finderPathWithPaginationCountByC_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, false);

		_finderPathFetchByG_P_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "name"}, true);

		_finderPathWithPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, true);

		_finderPathWithoutPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, true);

		_finderPathCountByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, false);

		_finderPathWithPaginationFindByG_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, true);

		_finderPathWithPaginationCountByG_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, false);

		_finderPathWithPaginationFindByGtF_C_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtF_C_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"folderId", "companyId", "parentFolderId", "status"},
			true);

		_finderPathWithPaginationCountByGtF_C_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtF_C_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"folderId", "companyId", "parentFolderId", "status"},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		JournalFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JournalFolderUtil.setPersistence(null);

		entityCache.removeCache(JournalFolderImpl.class.getName());
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = JournalPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_JOURNALFOLDER =
		"SELECT journalFolder FROM JournalFolder journalFolder";

	private static final String _SQL_SELECT_JOURNALFOLDER_WHERE =
		"SELECT journalFolder FROM JournalFolder journalFolder WHERE ";

	private static final String _SQL_COUNT_JOURNALFOLDER =
		"SELECT COUNT(journalFolder) FROM JournalFolder journalFolder";

	private static final String _SQL_COUNT_JOURNALFOLDER_WHERE =
		"SELECT COUNT(journalFolder) FROM JournalFolder journalFolder WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"journalFolder.folderId";

	private static final String _FILTER_SQL_SELECT_JOURNALFOLDER_WHERE =
		"SELECT DISTINCT {journalFolder.*} FROM JournalFolder journalFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {JournalFolder.*} FROM (SELECT DISTINCT journalFolder.folderId FROM JournalFolder journalFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_JOURNALFOLDER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN JournalFolder ON TEMP_TABLE.folderId = JournalFolder.folderId";

	private static final String _FILTER_SQL_COUNT_JOURNALFOLDER_WHERE =
		"SELECT COUNT(DISTINCT journalFolder.folderId) AS COUNT_VALUE FROM JournalFolder journalFolder WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "journalFolder";

	private static final String _FILTER_ENTITY_TABLE = "JournalFolder";

	private static final String _ORDER_BY_ENTITY_ALIAS = "journalFolder.";

	private static final String _ORDER_BY_ENTITY_TABLE = "JournalFolder.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No JournalFolder exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No JournalFolder exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFolderPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}