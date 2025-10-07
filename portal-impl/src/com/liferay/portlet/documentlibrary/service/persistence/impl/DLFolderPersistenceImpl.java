/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFolderExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypePersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
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
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;

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
 * The persistence implementation for the document library folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFolderPersistenceImpl
	extends BasePersistenceImpl<DLFolder> implements DLFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFolderUtil</code> to access the document library folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFolderImpl.class.getName();

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
	 * Returns all the document library folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if (!uuid.equals(dlFolder.getUuid())) {
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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUuid_First(
			String uuid, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByUuid_First(uuid, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUuid_First(
		String uuid, OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUuid_Last(
			String uuid, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByUuid_Last(uuid, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUuid_Last(
		String uuid, OrderByComparator<DLFolder> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where uuid = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByUuid_PrevAndNext(
			long folderId, String uuid,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, dlFolder, uuid, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByUuid_PrevAndNext(
				session, dlFolder, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByUuid_PrevAndNext(
		Session session, DLFolder dlFolder, String uuid,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DLFolder dlFolder :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

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
		"dlFolder.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(dlFolder.uuid IS NULL OR dlFolder.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByUUID_G(uuid, groupId);

		if (dlFolder == null) {
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

		return dlFolder;
	}

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the document library folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			if (result instanceof DLFolder) {
				DLFolder dlFolder = (DLFolder)result;

				if (!Objects.equals(uuid, dlFolder.getUuid()) ||
					(groupId != dlFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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

					List<DLFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DLFolder dlFolder = list.get(0);

						result = dlFolder;

						cacheResult(dlFolder);
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
				return (DLFolder)result;
			}
		}
	}

	/**
	 * Removes the document library folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByUUID_G(uuid, groupId);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DLFolder dlFolder = fetchByUUID_G(uuid, groupId);

		if (dlFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"dlFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(dlFolder.uuid IS NULL OR dlFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"dlFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if (!uuid.equals(dlFolder.getUuid()) ||
							(companyId != dlFolder.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the first document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the last document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByUuid_C_PrevAndNext(
			long folderId, String uuid, long companyId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, dlFolder, uuid, companyId, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByUuid_C_PrevAndNext(
				session, dlFolder, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByUuid_C_PrevAndNext(
		Session session, DLFolder dlFolder, String uuid, long companyId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DLFolder dlFolder :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

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
		"dlFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(dlFolder.uuid IS NULL OR dlFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"dlFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the document library folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if (groupId != dlFolder.getGroupId()) {
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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGroupId_First(
			long groupId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByGroupId_First(groupId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGroupId_First(
		long groupId, OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGroupId_Last(
			long groupId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByGroupId_Last(groupId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGroupId_Last(
		long groupId, OrderByComparator<DLFolder> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByGroupId_PrevAndNext(
			long folderId, long groupId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, dlFolder, groupId, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByGroupId_PrevAndNext(
				session, dlFolder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByGroupId_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByGroupId_PrevAndNext(
			long folderId, long groupId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				folderId, groupId, orderByComparator);
		}

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, dlFolder, groupId, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, dlFolder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder filterGetByGroupId_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DLFolder dlFolder :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByGroupId(groupId);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
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
		"dlFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the document library folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if (companyId != dlFolder.getCompanyId()) {
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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByCompanyId_First(
			long companyId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByCompanyId_Last(
			long companyId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByCompanyId_Last(companyId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByCompanyId_Last(
		long companyId, OrderByComparator<DLFolder> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where companyId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByCompanyId_PrevAndNext(
			long folderId, long companyId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, dlFolder, companyId, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByCompanyId_PrevAndNext(
				session, dlFolder, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByCompanyId_PrevAndNext(
		Session session, DLFolder dlFolder, long companyId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (DLFolder dlFolder :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"dlFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByRepositoryId;
	private FinderPath _finderPathWithoutPaginationFindByRepositoryId;
	private FinderPath _finderPathCountByRepositoryId;

	/**
	 * Returns all the document library folders where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByRepositoryId(long repositoryId) {
		return findByRepositoryId(
			repositoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end) {

		return findByRepositoryId(repositoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByRepositoryId(
			repositoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByRepositoryId;
					finderArgs = new Object[] {repositoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByRepositoryId;
				finderArgs = new Object[] {
					repositoryId, start, end, orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if (repositoryId != dlFolder.getRepositoryId()) {
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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByRepositoryId_First(
			long repositoryId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByRepositoryId_First(
			repositoryId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByRepositoryId_First(
		long repositoryId, OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByRepositoryId(
			repositoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByRepositoryId_Last(
			long repositoryId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByRepositoryId_Last(
			repositoryId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByRepositoryId_Last(
		long repositoryId, OrderByComparator<DLFolder> orderByComparator) {

		int count = countByRepositoryId(repositoryId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByRepositoryId(
			repositoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where repositoryId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByRepositoryId_PrevAndNext(
			long folderId, long repositoryId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByRepositoryId_PrevAndNext(
				session, dlFolder, repositoryId, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByRepositoryId_PrevAndNext(
				session, dlFolder, repositoryId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByRepositoryId_PrevAndNext(
		Session session, DLFolder dlFolder, long repositoryId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(repositoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	@Override
	public void removeByRepositoryId(long repositoryId) {
		for (DLFolder dlFolder :
				findByRepositoryId(
					repositoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByRepositoryId(long repositoryId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByRepositoryId;

			Object[] finderArgs = new Object[] {repositoryId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

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

	private static final String _FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2 =
		"dlFolder.repositoryId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P(long groupId, long parentFolderId) {
		return findByG_P(
			groupId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return findByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByG_P(
			groupId, parentFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(parentFolderId != dlFolder.getParentFolderId())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_First(
			long groupId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_P_First(
			groupId, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_First(
		long groupId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_P(
			groupId, parentFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_Last(
			long groupId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_P_Last(
			groupId, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the last document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_Last(
		long groupId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_P(groupId, parentFolderId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_P(
			groupId, parentFolderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_P_PrevAndNext(
			long folderId, long groupId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, orderByComparator,
				true);

			array[1] = dlFolder;

			array[2] = getByG_P_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, orderByComparator,
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

	protected DLFolder getByG_P_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P(long groupId, long parentFolderId) {
		return filterFindByG_P(
			groupId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return filterFindByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_P_PrevAndNext(
			long folderId, long groupId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				folderId, groupId, parentFolderId, orderByComparator);
		}

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, orderByComparator,
				true);

			array[1] = dlFolder;

			array[2] = filterGetByG_P_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, orderByComparator,
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

	protected DLFolder filterGetByG_P_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentFolderId) {
		for (DLFolder dlFolder :
				findByG_P(
					groupId, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_P(long groupId, long parentFolderId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentFolderId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentFolderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentFolderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_P(groupId, parentFolderId);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
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
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((companyId != dlFolder.getCompanyId()) ||
							(status == dlFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the first document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the last document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByC_NotS_PrevAndNext(
			long folderId, long companyId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, dlFolder, companyId, status, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByC_NotS_PrevAndNext(
				session, dlFolder, companyId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByC_NotS_PrevAndNext(
		Session session, DLFolder dlFolder, long companyId, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (DLFolder dlFolder :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

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

	private static final String _FINDER_COLUMN_C_NOTS_COMPANYID_2 =
		"dlFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"dlFolder.status != ?";

	private FinderPath _finderPathFetchByR_M;

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByR_M(long repositoryId, boolean mountPoint)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByR_M(repositoryId, mountPoint);

		if (dlFolder == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("repositoryId=");
			sb.append(repositoryId);

			sb.append(", mountPoint=");
			sb.append(mountPoint);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFolderException(sb.toString());
		}

		return dlFolder;
	}

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByR_M(long repositoryId, boolean mountPoint) {
		return fetchByR_M(repositoryId, mountPoint, true);
	}

	/**
	 * Returns the document library folder where repositoryId = &#63; and mountPoint = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByR_M(
		long repositoryId, boolean mountPoint, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {repositoryId, mountPoint};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByR_M, finderArgs, this);
			}

			if (result instanceof DLFolder) {
				DLFolder dlFolder = (DLFolder)result;

				if ((repositoryId != dlFolder.getRepositoryId()) ||
					(mountPoint != dlFolder.isMountPoint())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_R_M_REPOSITORYID_2);

				sb.append(_FINDER_COLUMN_R_M_MOUNTPOINT_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					queryPos.add(mountPoint);

					List<DLFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByR_M, finderArgs, list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {
										repositoryId, mountPoint
									};
								}

								_log.warn(
									"DLFolderPersistenceImpl.fetchByR_M(long, boolean, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						DLFolder dlFolder = list.get(0);

						result = dlFolder;

						cacheResult(dlFolder);
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
				return (DLFolder)result;
			}
		}
	}

	/**
	 * Removes the document library folder where repositoryId = &#63; and mountPoint = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByR_M(long repositoryId, boolean mountPoint)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByR_M(repositoryId, mountPoint);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where repositoryId = &#63; and mountPoint = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param mountPoint the mount point
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByR_M(long repositoryId, boolean mountPoint) {
		DLFolder dlFolder = fetchByR_M(repositoryId, mountPoint);

		if (dlFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_R_M_REPOSITORYID_2 =
		"dlFolder.repositoryId = ? AND ";

	private static final String _FINDER_COLUMN_R_M_MOUNTPOINT_2 =
		"dlFolder.mountPoint = ?";

	private FinderPath _finderPathWithPaginationFindByR_P;
	private FinderPath _finderPathWithoutPaginationFindByR_P;
	private FinderPath _finderPathCountByR_P;

	/**
	 * Returns all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByR_P(long repositoryId, long parentFolderId) {
		return findByR_P(
			repositoryId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end) {

		return findByR_P(repositoryId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByR_P(
			repositoryId, parentFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByR_P(
		long repositoryId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_P;
					finderArgs = new Object[] {repositoryId, parentFolderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_P;
				finderArgs = new Object[] {
					repositoryId, parentFolderId, start, end, orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((repositoryId != dlFolder.getRepositoryId()) ||
							(parentFolderId != dlFolder.getParentFolderId())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_R_P_REPOSITORYID_2);

				sb.append(_FINDER_COLUMN_R_P_PARENTFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					queryPos.add(parentFolderId);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByR_P_First(
			long repositoryId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByR_P_First(
			repositoryId, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByR_P_First(
		long repositoryId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByR_P(
			repositoryId, parentFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByR_P_Last(
			long repositoryId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByR_P_Last(
			repositoryId, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByR_P_Last(
		long repositoryId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByR_P(repositoryId, parentFolderId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByR_P(
			repositoryId, parentFolderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByR_P_PrevAndNext(
			long folderId, long repositoryId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByR_P_PrevAndNext(
				session, dlFolder, repositoryId, parentFolderId,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByR_P_PrevAndNext(
				session, dlFolder, repositoryId, parentFolderId,
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

	protected DLFolder getByR_P_PrevAndNext(
		Session session, DLFolder dlFolder, long repositoryId,
		long parentFolderId, OrderByComparator<DLFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_R_P_REPOSITORYID_2);

		sb.append(_FINDER_COLUMN_R_P_PARENTFOLDERID_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(repositoryId);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where repositoryId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByR_P(long repositoryId, long parentFolderId) {
		for (DLFolder dlFolder :
				findByR_P(
					repositoryId, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where repositoryId = &#63; and parentFolderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByR_P(long repositoryId, long parentFolderId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByR_P;

			Object[] finderArgs = new Object[] {repositoryId, parentFolderId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_R_P_REPOSITORYID_2);

				sb.append(_FINDER_COLUMN_R_P_PARENTFOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_R_P_REPOSITORYID_2 =
		"dlFolder.repositoryId = ? AND ";

	private static final String _FINDER_COLUMN_R_P_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ?";

	private FinderPath _finderPathWithPaginationFindByP_N;
	private FinderPath _finderPathWithoutPaginationFindByP_N;
	private FinderPath _finderPathCountByP_N;

	/**
	 * Returns all the document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByP_N(long parentFolderId, String name) {
		return findByP_N(
			parentFolderId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByP_N(
		long parentFolderId, String name, int start, int end) {

		return findByP_N(parentFolderId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByP_N(
		long parentFolderId, String name, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByP_N(
			parentFolderId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByP_N(
		long parentFolderId, String name, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByP_N;
					finderArgs = new Object[] {parentFolderId, name};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByP_N;
				finderArgs = new Object[] {
					parentFolderId, name, start, end, orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((parentFolderId != dlFolder.getParentFolderId()) ||
							!name.equals(dlFolder.getName())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_P_N_PARENTFOLDERID_2);

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
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentFolderId);

					if (bindName) {
						queryPos.add(name);
					}

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByP_N_First(
			long parentFolderId, String name,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByP_N_First(
			parentFolderId, name, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByP_N_First(
		long parentFolderId, String name,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByP_N(
			parentFolderId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByP_N_Last(
			long parentFolderId, String name,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByP_N_Last(
			parentFolderId, name, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByP_N_Last(
		long parentFolderId, String name,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByP_N(parentFolderId, name);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByP_N(
			parentFolderId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByP_N_PrevAndNext(
			long folderId, long parentFolderId, String name,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		name = Objects.toString(name, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByP_N_PrevAndNext(
				session, dlFolder, parentFolderId, name, orderByComparator,
				true);

			array[1] = dlFolder;

			array[2] = getByP_N_PrevAndNext(
				session, dlFolder, parentFolderId, name, orderByComparator,
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

	protected DLFolder getByP_N_PrevAndNext(
		Session session, DLFolder dlFolder, long parentFolderId, String name,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_P_N_PARENTFOLDERID_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentFolderId);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where parentFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 */
	@Override
	public void removeByP_N(long parentFolderId, String name) {
		for (DLFolder dlFolder :
				findByP_N(
					parentFolderId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where parentFolderId = &#63; and name = &#63;.
	 *
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByP_N(long parentFolderId, String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByP_N;

			Object[] finderArgs = new Object[] {parentFolderId, name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_P_N_PARENTFOLDERID_2);

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

					queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_P_N_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_P_N_NAME_2 = "dlFolder.name = ?";

	private static final String _FINDER_COLUMN_P_N_NAME_3 =
		"(dlFolder.name IS NULL OR dlFolder.name = '')";

	private FinderPath _finderPathWithPaginationFindByGtF_C_P;
	private FinderPath _finderPathWithPaginationCountByGtF_C_P;

	/**
	 * Returns all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId) {

		return findByGtF_C_P(
			folderId, companyId, parentFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start,
		int end) {

		return findByGtF_C_P(
			folderId, companyId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByGtF_C_P(
			folderId, companyId, parentFolderId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P(
		long folderId, long companyId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtF_C_P;
			finderArgs = new Object[] {
				folderId, companyId, parentFolderId, start, end,
				orderByComparator
			};

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((folderId >= dlFolder.getFolderId()) ||
							(companyId != dlFolder.getCompanyId()) ||
							(parentFolderId != dlFolder.getParentFolderId())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_PARENTFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGtF_C_P_First(
			long folderId, long companyId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByGtF_C_P_First(
			folderId, companyId, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId>");
		sb.append(folderId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGtF_C_P_First(
		long folderId, long companyId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByGtF_C_P(
			folderId, companyId, parentFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGtF_C_P_Last(
			long folderId, long companyId, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByGtF_C_P_Last(
			folderId, companyId, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId>");
		sb.append(folderId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGtF_C_P_Last(
		long folderId, long companyId, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByGtF_C_P(folderId, companyId, parentFolderId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByGtF_C_P(
			folderId, companyId, parentFolderId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByGtF_C_P(
		long folderId, long companyId, long parentFolderId) {

		for (DLFolder dlFolder :
				findByGtF_C_P(
					folderId, companyId, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByGtF_C_P(
		long folderId, long companyId, long parentFolderId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByGtF_C_P;

			Object[] finderArgs = new Object[] {
				folderId, companyId, parentFolderId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_PARENTFOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					queryPos.add(companyId);

					queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_GTF_C_P_FOLDERID_2 =
		"dlFolder.folderId > ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_COMPANYID_2 =
		"dlFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ?";

	private FinderPath _finderPathWithPaginationFindByG_M_P;
	private FinderPath _finderPathWithoutPaginationFindByG_M_P;
	private FinderPath _finderPathCountByG_M_P;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		return findByG_M_P(
			groupId, mountPoint, parentFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end) {

		return findByG_M_P(
			groupId, mountPoint, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_P(
			groupId, mountPoint, parentFolderId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_M_P;
					finderArgs = new Object[] {
						groupId, mountPoint, parentFolderId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_M_P;
				finderArgs = new Object[] {
					groupId, mountPoint, parentFolderId, start, end,
					orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(mountPoint != dlFolder.isMountPoint()) ||
							(parentFolderId != dlFolder.getParentFolderId())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_P_MOUNTPOINT_2);

				sb.append(_FINDER_COLUMN_G_M_P_PARENTFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					queryPos.add(parentFolderId);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_First(
			long groupId, boolean mountPoint, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_P_First(
			groupId, mountPoint, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_First(
		long groupId, boolean mountPoint, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_M_P(
			groupId, mountPoint, parentFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_Last(
			long groupId, boolean mountPoint, long parentFolderId,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_P_Last(
			groupId, mountPoint, parentFolderId, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_Last(
		long groupId, boolean mountPoint, long parentFolderId,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_M_P(groupId, mountPoint, parentFolderId);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_M_P(
			groupId, mountPoint, parentFolderId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_M_P_PrevAndNext(
			long folderId, long groupId, boolean mountPoint,
			long parentFolderId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_M_P_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByG_M_P_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId,
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

	protected DLFolder getByG_M_P_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		long parentFolderId, OrderByComparator<DLFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_PARENTFOLDERID_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		return filterFindByG_M_P(
			groupId, mountPoint, parentFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end) {

		return filterFindByG_M_P(
			groupId, mountPoint, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId, int start,
		int end, OrderByComparator<DLFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_P(
				groupId, mountPoint, parentFolderId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_M_P(
					groupId, mountPoint, parentFolderId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			queryPos.add(parentFolderId);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_M_P_PrevAndNext(
			long folderId, long groupId, boolean mountPoint,
			long parentFolderId, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_P_PrevAndNext(
				folderId, groupId, mountPoint, parentFolderId,
				orderByComparator);
		}

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_M_P_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByG_M_P_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId,
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

	protected DLFolder filterGetByG_M_P_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		long parentFolderId, OrderByComparator<DLFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		for (DLFolder dlFolder :
				findByG_M_P(
					groupId, mountPoint, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_M_P;

			Object[] finderArgs = new Object[] {
				groupId, mountPoint, parentFolderId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_P_MOUNTPOINT_2);

				sb.append(_FINDER_COLUMN_G_M_P_PARENTFOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					queryPos.add(parentFolderId);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_P(
		long groupId, boolean mountPoint, long parentFolderId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_M_P(groupId, mountPoint, parentFolderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_M_P(
				groupId, mountPoint, parentFolderId);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_PARENTFOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

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

	private static final String _FINDER_COLUMN_G_M_P_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_MOUNTPOINT_2 =
		"dlFolder.mountPoint = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ?";

	private FinderPath _finderPathFetchByG_P_N;

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_N(long groupId, long parentFolderId, String name)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_P_N(groupId, parentFolderId, name);

		if (dlFolder == null) {
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

		return dlFolder;
	}

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name) {

		return fetchByG_P_N(groupId, parentFolderId, name, true);
	}

	/**
	 * Returns the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_N(
		long groupId, long parentFolderId, String name,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, parentFolderId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_P_N, finderArgs, this);
			}

			if (result instanceof DLFolder) {
				DLFolder dlFolder = (DLFolder)result;

				if ((groupId != dlFolder.getGroupId()) ||
					(parentFolderId != dlFolder.getParentFolderId()) ||
					!Objects.equals(name, dlFolder.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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

					List<DLFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_P_N, finderArgs, list);
						}
					}
					else {
						DLFolder dlFolder = list.get(0);

						result = dlFolder;

						cacheResult(dlFolder);
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
				return (DLFolder)result;
			}
		}
	}

	/**
	 * Removes the document library folder where groupId = &#63; and parentFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByG_P_N(
			long groupId, long parentFolderId, String name)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByG_P_N(groupId, parentFolderId, name);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param name the name
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_P_N(long groupId, long parentFolderId, String name) {
		DLFolder dlFolder = fetchByG_P_N(groupId, parentFolderId, name);

		if (dlFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_N_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_NAME_2 =
		"dlFolder.name = ?";

	private static final String _FINDER_COLUMN_G_P_N_NAME_3 =
		"(dlFolder.name IS NULL OR dlFolder.name = '')";

	private FinderPath _finderPathWithPaginationFindByGtF_C_P_NotS;
	private FinderPath _finderPathWithPaginationCountByGtF_C_P_NotS;

	/**
	 * Returns all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtF_C_P_NotS;
			finderArgs = new Object[] {
				folderId, companyId, parentFolderId, status, start, end,
				orderByComparator
			};

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((folderId >= dlFolder.getFolderId()) ||
							(companyId != dlFolder.getCompanyId()) ||
							(parentFolderId != dlFolder.getParentFolderId()) ||
							(status == dlFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGtF_C_P_NotS_First(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByGtF_C_P_NotS_First(
			folderId, companyId, parentFolderId, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the first document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGtF_C_P_NotS_First(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByGtF_C_P_NotS_Last(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByGtF_C_P_NotS_Last(
			folderId, companyId, parentFolderId, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
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
	 * Returns the last document library folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByGtF_C_P_NotS_Last(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		for (DLFolder dlFolder :
				findByGtF_C_P_NotS(
					folderId, companyId, parentFolderId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath =
				_finderPathWithPaginationCountByGtF_C_P_NotS;

			Object[] finderArgs = new Object[] {
				folderId, companyId, parentFolderId, status
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

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

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2 =
		"dlFolder.folderId > ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2 =
		"dlFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2 =
		"dlFolder.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_M_P_H;
	private FinderPath _finderPathWithoutPaginationFindByG_M_P_H;
	private FinderPath _finderPathCountByG_M_P_H;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		return findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end) {

		return findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_M_P_H;
					finderArgs = new Object[] {
						groupId, mountPoint, parentFolderId, hidden
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_M_P_H;
				finderArgs = new Object[] {
					groupId, mountPoint, parentFolderId, hidden, start, end,
					orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(mountPoint != dlFolder.isMountPoint()) ||
							(parentFolderId != dlFolder.getParentFolderId()) ||
							(hidden != dlFolder.isHidden())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_P_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_HIDDEN_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					queryPos.add(parentFolderId);

					queryPos.add(hidden);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_H_First(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_P_H_First(
			groupId, mountPoint, parentFolderId, hidden, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_H_First(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_H_Last(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_P_H_Last(
			groupId, mountPoint, parentFolderId, hidden, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_H_Last(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_M_P_H(groupId, mountPoint, parentFolderId, hidden);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_M_P_H_PrevAndNext(
			long folderId, long groupId, boolean mountPoint,
			long parentFolderId, boolean hidden,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_M_P_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByG_M_P_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
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

	protected DLFolder getByG_M_P_H_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		long parentFolderId, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_HIDDEN_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		queryPos.add(parentFolderId);

		queryPos.add(hidden);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		return filterFindByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end) {

		return filterFindByG_M_P_H(
			groupId, mountPoint, parentFolderId, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_P_H(
				groupId, mountPoint, parentFolderId, hidden, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_M_P_H(
					groupId, mountPoint, parentFolderId, hidden,
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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			queryPos.add(parentFolderId);

			queryPos.add(hidden);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_M_P_H_PrevAndNext(
			long folderId, long groupId, boolean mountPoint,
			long parentFolderId, boolean hidden,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_P_H_PrevAndNext(
				folderId, groupId, mountPoint, parentFolderId, hidden,
				orderByComparator);
		}

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_M_P_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByG_M_P_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
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

	protected DLFolder filterGetByG_M_P_H_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		long parentFolderId, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		queryPos.add(parentFolderId);

		queryPos.add(hidden);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 */
	@Override
	public void removeByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		for (DLFolder dlFolder :
				findByG_M_P_H(
					groupId, mountPoint, parentFolderId, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_M_P_H;

			Object[] finderArgs = new Object[] {
				groupId, mountPoint, parentFolderId, hidden
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_P_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_HIDDEN_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					queryPos.add(parentFolderId);

					queryPos.add(hidden);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_P_H(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_M_P_H(groupId, mountPoint, parentFolderId, hidden);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_M_P_H(
				groupId, mountPoint, parentFolderId, hidden);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_HIDDEN_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			queryPos.add(parentFolderId);

			queryPos.add(hidden);

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

	private static final String _FINDER_COLUMN_G_M_P_H_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_MOUNTPOINT_2 =
		"dlFolder.mountPoint = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_HIDDEN_2 =
		"dlFolder.hidden = ?";

	private static final String _FINDER_COLUMN_G_M_P_H_HIDDEN_2_SQL =
		"dlFolder.hidden_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_M_LikeT_H;
	private FinderPath _finderPathWithPaginationCountByG_M_LikeT_H;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end) {

		return findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_M_LikeT_H;
			finderArgs = new Object[] {
				groupId, mountPoint, treePath, hidden, start, end,
				orderByComparator
			};

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(mountPoint != dlFolder.isMountPoint()) ||
							!StringUtil.wildcardMatches(
								dlFolder.getTreePath(), treePath, '_', '%',
								'\\', true) ||
							(hidden != dlFolder.isHidden())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(hidden);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_LikeT_H_First(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_LikeT_H_First(
			groupId, mountPoint, treePath, hidden, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_LikeT_H_First(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_LikeT_H_Last(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_LikeT_H_Last(
			groupId, mountPoint, treePath, hidden, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_LikeT_H_Last(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_M_LikeT_H(groupId, mountPoint, treePath, hidden);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_M_LikeT_H_PrevAndNext(
			long folderId, long groupId, boolean mountPoint, String treePath,
			boolean hidden, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		treePath = Objects.toString(treePath, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_M_LikeT_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByG_M_LikeT_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
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

	protected DLFolder getByG_M_LikeT_H_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		String treePath, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(hidden);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return filterFindByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end) {

		return filterFindByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_LikeT_H(
				groupId, mountPoint, treePath, hidden, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_M_LikeT_H(
					groupId, mountPoint, treePath, hidden, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			queryPos.add(hidden);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_M_LikeT_H_PrevAndNext(
			long folderId, long groupId, boolean mountPoint, String treePath,
			boolean hidden, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_LikeT_H_PrevAndNext(
				folderId, groupId, mountPoint, treePath, hidden,
				orderByComparator);
		}

		treePath = Objects.toString(treePath, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_M_LikeT_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByG_M_LikeT_H_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
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

	protected DLFolder filterGetByG_M_LikeT_H_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		String treePath, boolean hidden,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(hidden);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 */
	@Override
	public void removeByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		for (DLFolder dlFolder :
				findByG_M_LikeT_H(
					groupId, mountPoint, treePath, hidden, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_M_LikeT_H;

			Object[] finderArgs = new Object[] {
				groupId, mountPoint, treePath, hidden
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(hidden);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_LikeT_H(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_M_LikeT_H(groupId, mountPoint, treePath, hidden);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_M_LikeT_H(
				groupId, mountPoint, treePath, hidden);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			queryPos.add(hidden);

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

	private static final String _FINDER_COLUMN_G_M_LIKET_H_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_MOUNTPOINT_2 =
		"dlFolder.mountPoint = ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_TREEPATH_2 =
		"dlFolder.treePath LIKE ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_TREEPATH_3 =
		"(dlFolder.treePath IS NULL OR dlFolder.treePath LIKE '') AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2 =
		"dlFolder.hidden = ?";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_HIDDEN_2_SQL =
		"dlFolder.hidden_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_H_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_H_S;
	private FinderPath _finderPathCountByG_P_H_S;

	/**
	 * Returns all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		return findByG_P_H_S(
			groupId, parentFolderId, hidden, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end) {

		return findByG_P_H_S(
			groupId, parentFolderId, hidden, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findByG_P_H_S(
			groupId, parentFolderId, hidden, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_H_S;
					finderArgs = new Object[] {
						groupId, parentFolderId, hidden, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_H_S;
				finderArgs = new Object[] {
					groupId, parentFolderId, hidden, status, start, end,
					orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(parentFolderId != dlFolder.getParentFolderId()) ||
							(hidden != dlFolder.isHidden()) ||
							(status != dlFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_H_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_H_S_HIDDEN_2);

				sb.append(_FINDER_COLUMN_G_P_H_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					queryPos.add(hidden);

					queryPos.add(status);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_H_S_First(
			long groupId, long parentFolderId, boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_P_H_S_First(
			groupId, parentFolderId, hidden, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_H_S_First(
		long groupId, long parentFolderId, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_P_H_S(
			groupId, parentFolderId, hidden, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_P_H_S_Last(
			long groupId, long parentFolderId, boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_P_H_S_Last(
			groupId, parentFolderId, hidden, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_P_H_S_Last(
		long groupId, long parentFolderId, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_P_H_S(groupId, parentFolderId, hidden, status);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_P_H_S(
			groupId, parentFolderId, hidden, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_P_H_S_PrevAndNext(
			long folderId, long groupId, long parentFolderId, boolean hidden,
			int status, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_P_H_S_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, hidden, status,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByG_P_H_S_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, hidden, status,
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

	protected DLFolder getByG_P_H_S_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, long parentFolderId,
		boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_HIDDEN_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_STATUS_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(hidden);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		return filterFindByG_P_H_S(
			groupId, parentFolderId, hidden, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end) {

		return filterFindByG_P_H_S(
			groupId, parentFolderId, hidden, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_H_S(
				groupId, parentFolderId, hidden, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_H_S(
					groupId, parentFolderId, hidden, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_H_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			queryPos.add(hidden);

			queryPos.add(status);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_P_H_S_PrevAndNext(
			long folderId, long groupId, long parentFolderId, boolean hidden,
			int status, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_H_S_PrevAndNext(
				folderId, groupId, parentFolderId, hidden, status,
				orderByComparator);
		}

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_P_H_S_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, hidden, status,
				orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByG_P_H_S_PrevAndNext(
				session, dlFolder, groupId, parentFolderId, hidden, status,
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

	protected DLFolder filterGetByG_P_H_S_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, long parentFolderId,
		boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_H_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(hidden);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 */
	@Override
	public void removeByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		for (DLFolder dlFolder :
				findByG_P_H_S(
					groupId, parentFolderId, hidden, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P_H_S;

			Object[] finderArgs = new Object[] {
				groupId, parentFolderId, hidden, status
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_H_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_H_S_HIDDEN_2);

				sb.append(_FINDER_COLUMN_G_P_H_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					queryPos.add(hidden);

					queryPos.add(status);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_H_S(
		long groupId, long parentFolderId, boolean hidden, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_H_S(groupId, parentFolderId, hidden, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_P_H_S(
				groupId, parentFolderId, hidden, status);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_H_S_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_H_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
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

			queryPos.add(hidden);

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

	private static final String _FINDER_COLUMN_G_P_H_S_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_H_S_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_H_S_HIDDEN_2 =
		"dlFolder.hidden = ? AND ";

	private static final String _FINDER_COLUMN_G_P_H_S_HIDDEN_2_SQL =
		"dlFolder.hidden_ = ? AND ";

	private static final String _FINDER_COLUMN_G_P_H_S_STATUS_2 =
		"dlFolder.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_M_P_H_S;
	private FinderPath _finderPathWithoutPaginationFindByG_M_P_H_S;
	private FinderPath _finderPathCountByG_M_P_H_S;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		return findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end) {

		return findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_M_P_H_S;
					finderArgs = new Object[] {
						groupId, mountPoint, parentFolderId, hidden, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_M_P_H_S;
				finderArgs = new Object[] {
					groupId, mountPoint, parentFolderId, hidden, status, start,
					end, orderByComparator
				};
			}

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(mountPoint != dlFolder.isMountPoint()) ||
							(parentFolderId != dlFolder.getParentFolderId()) ||
							(hidden != dlFolder.isHidden()) ||
							(status != dlFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_HIDDEN_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					queryPos.add(parentFolderId);

					queryPos.add(hidden);

					queryPos.add(status);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_H_S_First(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_P_H_S_First(
			groupId, mountPoint, parentFolderId, hidden, status,
			orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_H_S_First(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_P_H_S_Last(
			long groupId, boolean mountPoint, long parentFolderId,
			boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_P_H_S_Last(
			groupId, mountPoint, parentFolderId, hidden, status,
			orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_P_H_S_Last(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_M_P_H_S_PrevAndNext(
			long folderId, long groupId, boolean mountPoint,
			long parentFolderId, boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_M_P_H_S_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
				status, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByG_M_P_H_S_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByG_M_P_H_S_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		long parentFolderId, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_HIDDEN_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_STATUS_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		queryPos.add(parentFolderId);

		queryPos.add(hidden);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		return filterFindByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end) {

		return filterFindByG_M_P_H_S(
			groupId, mountPoint, parentFolderId, hidden, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_P_H_S(
				groupId, mountPoint, parentFolderId, hidden, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_M_P_H_S(
					groupId, mountPoint, parentFolderId, hidden, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			queryPos.add(parentFolderId);

			queryPos.add(hidden);

			queryPos.add(status);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_M_P_H_S_PrevAndNext(
			long folderId, long groupId, boolean mountPoint,
			long parentFolderId, boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_P_H_S_PrevAndNext(
				folderId, groupId, mountPoint, parentFolderId, hidden, status,
				orderByComparator);
		}

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_M_P_H_S_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
				status, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByG_M_P_H_S_PrevAndNext(
				session, dlFolder, groupId, mountPoint, parentFolderId, hidden,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder filterGetByG_M_P_H_S_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		long parentFolderId, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		queryPos.add(parentFolderId);

		queryPos.add(hidden);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 */
	@Override
	public void removeByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		for (DLFolder dlFolder :
				findByG_M_P_H_S(
					groupId, mountPoint, parentFolderId, hidden, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_M_P_H_S;

			Object[] finderArgs = new Object[] {
				groupId, mountPoint, parentFolderId, hidden, status
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_HIDDEN_2);

				sb.append(_FINDER_COLUMN_G_M_P_H_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					queryPos.add(parentFolderId);

					queryPos.add(hidden);

					queryPos.add(status);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and parentFolderId = &#63; and hidden = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param parentFolderId the parent folder ID
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_P_H_S(
		long groupId, boolean mountPoint, long parentFolderId, boolean hidden,
		int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_M_P_H_S(
				groupId, mountPoint, parentFolderId, hidden, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_M_P_H_S(
				groupId, mountPoint, parentFolderId, hidden, status);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_M_P_H_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			queryPos.add(parentFolderId);

			queryPos.add(hidden);

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

	private static final String _FINDER_COLUMN_G_M_P_H_S_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_S_MOUNTPOINT_2 =
		"dlFolder.mountPoint = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_S_PARENTFOLDERID_2 =
		"dlFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_S_HIDDEN_2 =
		"dlFolder.hidden = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_S_HIDDEN_2_SQL =
		"dlFolder.hidden_ = ? AND ";

	private static final String _FINDER_COLUMN_G_M_P_H_S_STATUS_2 =
		"dlFolder.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_M_LikeT_H_NotS;
	private FinderPath _finderPathWithPaginationCountByG_M_LikeT_H_NotS;

	/**
	 * Returns all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		return findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end) {

		return findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library folders
	 */
	@Override
	public List<DLFolder> findByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_M_LikeT_H_NotS;
			finderArgs = new Object[] {
				groupId, mountPoint, treePath, hidden, status, start, end,
				orderByComparator
			};

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFolder dlFolder : list) {
						if ((groupId != dlFolder.getGroupId()) ||
							(mountPoint != dlFolder.isMountPoint()) ||
							!StringUtil.wildcardMatches(
								dlFolder.getTreePath(), treePath, '_', '%',
								'\\', true) ||
							(hidden != dlFolder.isHidden()) ||
							(status == dlFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(hidden);

					queryPos.add(status);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_LikeT_H_NotS_First(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			int status, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_LikeT_H_NotS_First(
			groupId, mountPoint, treePath, hidden, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_LikeT_H_NotS_First(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, OrderByComparator<DLFolder> orderByComparator) {

		List<DLFolder> list = findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByG_M_LikeT_H_NotS_Last(
			long groupId, boolean mountPoint, String treePath, boolean hidden,
			int status, OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByG_M_LikeT_H_NotS_Last(
			groupId, mountPoint, treePath, hidden, status, orderByComparator);

		if (dlFolder != null) {
			return dlFolder;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", mountPoint=");
		sb.append(mountPoint);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByG_M_LikeT_H_NotS_Last(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, OrderByComparator<DLFolder> orderByComparator) {

		int count = countByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status);

		if (count == 0) {
			return null;
		}

		List<DLFolder> list = findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library folders before and after the current document library folder in the ordered set where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] findByG_M_LikeT_H_NotS_PrevAndNext(
			long folderId, long groupId, boolean mountPoint, String treePath,
			boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		treePath = Objects.toString(treePath, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = getByG_M_LikeT_H_NotS_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
				status, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = getByG_M_LikeT_H_NotS_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder getByG_M_LikeT_H_NotS_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		String treePath, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		sb.append(_SQL_SELECT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2);

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
			sb.append(DLFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(hidden);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		return filterFindByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end) {

		return filterFindByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders that the user has permissions to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library folders that the user has permission to view
	 */
	@Override
	public List<DLFolder> filterFindByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_LikeT_H_NotS(
				groupId, mountPoint, treePath, hidden, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_M_LikeT_H_NotS(
					groupId, mountPoint, treePath, hidden, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			queryPos.add(hidden);

			queryPos.add(status);

			return (List<DLFolder>)QueryUtil.list(
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
	 * Returns the document library folders before and after the current document library folder in the ordered set of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current document library folder
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder[] filterFindByG_M_LikeT_H_NotS_PrevAndNext(
			long folderId, long groupId, boolean mountPoint, String treePath,
			boolean hidden, int status,
			OrderByComparator<DLFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_M_LikeT_H_NotS_PrevAndNext(
				folderId, groupId, mountPoint, treePath, hidden, status,
				orderByComparator);
		}

		treePath = Objects.toString(treePath, "");

		DLFolder dlFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			DLFolder[] array = new DLFolderImpl[3];

			array[0] = filterGetByG_M_LikeT_H_NotS_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
				status, orderByComparator, true);

			array[1] = dlFolder;

			array[2] = filterGetByG_M_LikeT_H_NotS_PrevAndNext(
				session, dlFolder, groupId, mountPoint, treePath, hidden,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFolder filterGetByG_M_LikeT_H_NotS_PrevAndNext(
		Session session, DLFolder dlFolder, long groupId, boolean mountPoint,
		String treePath, boolean hidden, int status,
		OrderByComparator<DLFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(mountPoint);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(hidden);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 */
	@Override
	public void removeByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		for (DLFolder dlFolder :
				findByG_M_LikeT_H_NotS(
					groupId, mountPoint, treePath, hidden, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath =
				_finderPathWithPaginationCountByG_M_LikeT_H_NotS;

			Object[] finderArgs = new Object[] {
				groupId, mountPoint, treePath, hidden, status
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_COUNT_DLFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2);

				sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(mountPoint);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(hidden);

					queryPos.add(status);

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
	 * Returns the number of document library folders that the user has permission to view where groupId = &#63; and mountPoint = &#63; and treePath LIKE &#63; and hidden = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param mountPoint the mount point
	 * @param treePath the tree path
	 * @param hidden the hidden
	 * @param status the status
	 * @return the number of matching document library folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_M_LikeT_H_NotS(
		long groupId, boolean mountPoint, String treePath, boolean hidden,
		int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_M_LikeT_H_NotS(
				groupId, mountPoint, treePath, hidden, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFolder> dlFolders = findByG_M_LikeT_H_NotS(
				groupId, mountPoint, treePath, hidden, status);

			dlFolders = InlineSQLHelperUtil.filter(dlFolders, groupId);

			return dlFolders.size();
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = new StringBundler(6);

		sb.append(_FILTER_SQL_COUNT_DLFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2_SQL);

		sb.append(_FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(mountPoint);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			queryPos.add(hidden);

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

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_GROUPID_2 =
		"dlFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_MOUNTPOINT_2 =
		"dlFolder.mountPoint = ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_2 =
		"dlFolder.treePath LIKE ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_TREEPATH_3 =
		"(dlFolder.treePath IS NULL OR dlFolder.treePath LIKE '') AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2 =
		"dlFolder.hidden = ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_HIDDEN_2_SQL =
		"dlFolder.hidden_ = ? AND ";

	private static final String _FINDER_COLUMN_G_M_LIKET_H_NOTS_STATUS_2 =
		"dlFolder.status != ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library folder
	 * @throws NoSuchFolderException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByERC_G(externalReferenceCode, groupId);

		if (dlFolder == null) {
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

		return dlFolder;
	}

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByERC_G(String externalReferenceCode, long groupId) {
		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the document library folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			if (result instanceof DLFolder) {
				DLFolder dlFolder = (DLFolder)result;

				if (!Objects.equals(
						externalReferenceCode,
						dlFolder.getExternalReferenceCode()) ||
					(groupId != dlFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFOLDER_WHERE);

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

					List<DLFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						DLFolder dlFolder = list.get(0);

						result = dlFolder;

						cacheResult(dlFolder);
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
				return (DLFolder)result;
			}
		}
	}

	/**
	 * Removes the document library folder where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		DLFolder dlFolder = findByERC_G(externalReferenceCode, groupId);

		return remove(dlFolder);
	}

	/**
	 * Returns the number of document library folders where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library folders
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		DLFolder dlFolder = fetchByERC_G(externalReferenceCode, groupId);

		if (dlFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"dlFolder.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(dlFolder.externalReferenceCode IS NULL OR dlFolder.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"dlFolder.groupId = ?";

	public DLFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("hidden", "hidden_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFolder.class);

		setModelImplClass(DLFolderImpl.class);
		setModelPKClass(long.class);

		setTable(DLFolderTable.INSTANCE);
	}

	/**
	 * Caches the document library folder in the entity cache if it is enabled.
	 *
	 * @param dlFolder the document library folder
	 */
	@Override
	public void cacheResult(DLFolder dlFolder) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFolder.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				DLFolderImpl.class, dlFolder.getPrimaryKey(), dlFolder);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {dlFolder.getUuid(), dlFolder.getGroupId()},
				dlFolder);

			FinderCacheUtil.putResult(
				_finderPathFetchByR_M,
				new Object[] {
					dlFolder.getRepositoryId(), dlFolder.isMountPoint()
				},
				dlFolder);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_P_N,
				new Object[] {
					dlFolder.getGroupId(), dlFolder.getParentFolderId(),
					dlFolder.getName()
				},
				dlFolder);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					dlFolder.getExternalReferenceCode(), dlFolder.getGroupId()
				},
				dlFolder);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the document library folders in the entity cache if it is enabled.
	 *
	 * @param dlFolders the document library folders
	 */
	@Override
	public void cacheResult(List<DLFolder> dlFolders) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (dlFolders.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DLFolder dlFolder : dlFolders) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						dlFolder.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						DLFolderImpl.class, dlFolder.getPrimaryKey()) == null) {

					cacheResult(dlFolder);
				}
			}
		}
	}

	/**
	 * Clears the cache for all document library folders.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(DLFolderImpl.class);

		FinderCacheUtil.clearCache(DLFolderImpl.class);
	}

	/**
	 * Clears the cache for the document library folder.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DLFolder dlFolder) {
		EntityCacheUtil.removeResult(DLFolderImpl.class, dlFolder);
	}

	@Override
	public void clearCache(List<DLFolder> dlFolders) {
		for (DLFolder dlFolder : dlFolders) {
			EntityCacheUtil.removeResult(DLFolderImpl.class, dlFolder);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(DLFolderImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(DLFolderImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DLFolderModelImpl dlFolderModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFolderModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				dlFolderModelImpl.getUuid(), dlFolderModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args, dlFolderModelImpl);

			args = new Object[] {
				dlFolderModelImpl.getRepositoryId(),
				dlFolderModelImpl.isMountPoint()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByR_M, args, dlFolderModelImpl);

			args = new Object[] {
				dlFolderModelImpl.getGroupId(),
				dlFolderModelImpl.getParentFolderId(),
				dlFolderModelImpl.getName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_P_N, args, dlFolderModelImpl);

			args = new Object[] {
				dlFolderModelImpl.getExternalReferenceCode(),
				dlFolderModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, dlFolderModelImpl);
		}
	}

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	@Override
	public DLFolder create(long folderId) {
		DLFolder dlFolder = new DLFolderImpl();

		dlFolder.setNew(true);
		dlFolder.setPrimaryKey(folderId);

		String uuid = PortalUUIDUtil.generate();

		dlFolder.setUuid(uuid);

		dlFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFolder;
	}

	/**
	 * Removes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder remove(long folderId) throws NoSuchFolderException {
		return remove((Serializable)folderId);
	}

	/**
	 * Removes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder remove(Serializable primaryKey)
		throws NoSuchFolderException {

		Session session = null;

		try {
			session = openSession();

			DLFolder dlFolder = (DLFolder)session.get(
				DLFolderImpl.class, primaryKey);

			if (dlFolder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFolderException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(dlFolder);
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
	protected DLFolder removeImpl(DLFolder dlFolder) {
		dlFolderToDLFileEntryTypeTableMapper.deleteLeftPrimaryKeyTableMappings(
			dlFolder.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFolder)) {
				dlFolder = (DLFolder)session.get(
					DLFolderImpl.class, dlFolder.getPrimaryKeyObj());
			}

			if ((dlFolder != null) &&
				CTPersistenceHelperUtil.isRemove(dlFolder)) {

				session.delete(dlFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFolder != null) {
			clearCache(dlFolder);
		}

		return dlFolder;
	}

	@Override
	public DLFolder updateImpl(DLFolder dlFolder) {
		boolean isNew = dlFolder.isNew();

		if (!(dlFolder instanceof DLFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dlFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFolder implementation " +
					dlFolder.getClass());
		}

		DLFolderModelImpl dlFolderModelImpl = (DLFolderModelImpl)dlFolder;

		if (Validator.isNull(dlFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFolder.setUuid(uuid);
		}

		if (Validator.isNull(dlFolder.getExternalReferenceCode())) {
			dlFolder.setExternalReferenceCode(dlFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFolder.getCompanyId();

					long groupId = dlFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFolder.getPrimaryKey();
					}

					try {
						dlFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFolder.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFolder ercDLFolder = fetchByERC_G(
				dlFolder.getExternalReferenceCode(), dlFolder.getGroupId());

			if (isNew) {
				if (ercDLFolder != null) {
					throw new DuplicateDLFolderExternalReferenceCodeException(
						"Duplicate document library folder with external reference code " +
							dlFolder.getExternalReferenceCode() +
								" and group " + dlFolder.getGroupId());
				}
			}
			else {
				if ((ercDLFolder != null) &&
					(dlFolder.getFolderId() != ercDLFolder.getFolderId())) {

					throw new DuplicateDLFolderExternalReferenceCodeException(
						"Duplicate document library folder with external reference code " +
							dlFolder.getExternalReferenceCode() +
								" and group " + dlFolder.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFolder.setCreateDate(date);
			}
			else {
				dlFolder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!dlFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFolder.setModifiedDate(date);
			}
			else {
				dlFolder.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFolder)) {
				if (!isNew) {
					session.evict(
						DLFolderImpl.class, dlFolder.getPrimaryKeyObj());
				}

				session.save(dlFolder);
			}
			else {
				dlFolder = (DLFolder)session.merge(dlFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			DLFolderImpl.class, dlFolderModelImpl, false, true);

		cacheUniqueFindersCache(dlFolderModelImpl);

		if (isNew) {
			dlFolder.setNew(false);
		}

		dlFolder.resetOriginalValues();

		return dlFolder;
	}

	/**
	 * Returns the document library folder with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library folder
	 * @return the document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFolderException {

		DLFolder dlFolder = fetchByPrimaryKey(primaryKey);

		if (dlFolder == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFolderException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return dlFolder;
	}

	/**
	 * Returns the document library folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws NoSuchFolderException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder findByPrimaryKey(long folderId)
		throws NoSuchFolderException {

		return findByPrimaryKey((Serializable)folderId);
	}

	/**
	 * Returns the document library folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library folder
	 * @return the document library folder, or <code>null</code> if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				DLFolder.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DLFolder dlFolder = (DLFolder)EntityCacheUtil.getResult(
			DLFolderImpl.class, primaryKey);

		if (dlFolder != null) {
			return dlFolder;
		}

		Session session = null;

		try {
			session = openSession();

			dlFolder = (DLFolder)session.get(DLFolderImpl.class, primaryKey);

			if (dlFolder != null) {
				cacheResult(dlFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return dlFolder;
	}

	/**
	 * Returns the document library folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder, or <code>null</code> if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder fetchByPrimaryKey(long folderId) {
		return fetchByPrimaryKey((Serializable)folderId);
	}

	@Override
	public Map<Serializable, DLFolder> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(DLFolder.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DLFolder> map = new HashMap<Serializable, DLFolder>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DLFolder dlFolder = fetchByPrimaryKey(primaryKey);

			if (dlFolder != null) {
				map.put(primaryKey, dlFolder);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						DLFolder.class, primaryKey)) {

				DLFolder dlFolder = (DLFolder)EntityCacheUtil.getResult(
					DLFolderImpl.class, primaryKey);

				if (dlFolder == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, dlFolder);
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

			for (DLFolder dlFolder : (List<DLFolder>)query.list()) {
				map.put(dlFolder.getPrimaryKeyObj(), dlFolder);

				cacheResult(dlFolder);
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
	 * Returns all the document library folders.
	 *
	 * @return the document library folders
	 */
	@Override
	public List<DLFolder> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library folders
	 */
	@Override
	public List<DLFolder> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library folders
	 */
	@Override
	public List<DLFolder> findAll(
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of document library folders
	 */
	@Override
	public List<DLFolder> findAll(
		int start, int end, OrderByComparator<DLFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

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

			List<DLFolder> list = null;

			if (useFinderCache) {
				list = (List<DLFolder>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DLFOLDER);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DLFOLDER;

					sql = sql.concat(DLFolderModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DLFolder>)QueryUtil.list(
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
	 * Removes all the document library folders from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DLFolder dlFolder : findAll()) {
			remove(dlFolder);
		}
	}

	/**
	 * Returns the number of document library folders.
	 *
	 * @return the number of document library folders
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFolder.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_DLFOLDER);

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

	/**
	 * Returns the primaryKeys of document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return long[] of the primaryKeys of document library file entry types associated with the document library folder
	 */
	@Override
	public long[] getDLFileEntryTypePrimaryKeys(long pk) {
		long[] pks = dlFolderToDLFileEntryTypeTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return the document library file entry types associated with the document library folder
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFileEntryType>
		getDLFileEntryTypes(long pk) {

		return getDLFileEntryTypes(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the document library file entry types associated with the document library folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library folder
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library file entry types associated with the document library folder
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFileEntryType>
		getDLFileEntryTypes(long pk, int start, int end) {

		return getDLFileEntryTypes(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entry types associated with the document library folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the document library folder
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library file entry types associated with the document library folder
	 */
	@Override
	public List<com.liferay.document.library.kernel.model.DLFileEntryType>
		getDLFileEntryTypes(
			long pk, int start, int end,
			OrderByComparator
				<com.liferay.document.library.kernel.model.DLFileEntryType>
					orderByComparator) {

		return dlFolderToDLFileEntryTypeTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file entry types associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @return the number of document library file entry types associated with the document library folder
	 */
	@Override
	public int getDLFileEntryTypesSize(long pk) {
		long[] pks = dlFolderToDLFileEntryTypeTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the document library file entry type is associated with the document library folder.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 * @return <code>true</code> if the document library file entry type is associated with the document library folder; <code>false</code> otherwise
	 */
	@Override
	public boolean containsDLFileEntryType(long pk, long dlFileEntryTypePK) {
		return dlFolderToDLFileEntryTypeTableMapper.containsTableMapping(
			pk, dlFileEntryTypePK);
	}

	/**
	 * Returns <code>true</code> if the document library folder has any document library file entry types associated with it.
	 *
	 * @param pk the primary key of the document library folder to check for associations with document library file entry types
	 * @return <code>true</code> if the document library folder has any document library file entry types associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsDLFileEntryTypes(long pk) {
		if (getDLFileEntryTypesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 * @return <code>true</code> if an association between the document library folder and the document library file entry type was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addDLFileEntryType(long pk, long dlFileEntryTypePK) {
		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, dlFileEntryTypePK);
		}
		else {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				dlFolder.getCompanyId(), pk, dlFileEntryTypePK);
		}
	}

	/**
	 * Adds an association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryType the document library file entry type
	 * @return <code>true</code> if an association between the document library folder and the document library file entry type was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addDLFileEntryType(
		long pk,
		com.liferay.document.library.kernel.model.DLFileEntryType
			dlFileEntryType) {

		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				dlFileEntryType.getPrimaryKey());
		}
		else {
			return dlFolderToDLFileEntryTypeTableMapper.addTableMapping(
				dlFolder.getCompanyId(), pk, dlFileEntryType.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types
	 * @return <code>true</code> if at least one association between the document library folder and the document library file entry types was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs) {
		long companyId = 0;

		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = dlFolder.getCompanyId();
		}

		long[] addedKeys =
			dlFolderToDLFileEntryTypeTableMapper.addTableMappings(
				companyId, pk, dlFileEntryTypePKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types
	 * @return <code>true</code> if at least one association between the document library folder and the document library file entry types was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addDLFileEntryTypes(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFileEntryTypes) {

		return addDLFileEntryTypes(
			pk,
			ListUtil.toLongArray(
				dlFileEntryTypes,
				com.liferay.document.library.kernel.model.DLFileEntryType.
					FILE_ENTRY_TYPE_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the document library folder and its document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder to clear the associated document library file entry types from
	 */
	@Override
	public void clearDLFileEntryTypes(long pk) {
		dlFolderToDLFileEntryTypeTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePK the primary key of the document library file entry type
	 */
	@Override
	public void removeDLFileEntryType(long pk, long dlFileEntryTypePK) {
		dlFolderToDLFileEntryTypeTableMapper.deleteTableMapping(
			pk, dlFileEntryTypePK);
	}

	/**
	 * Removes the association between the document library folder and the document library file entry type. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryType the document library file entry type
	 */
	@Override
	public void removeDLFileEntryType(
		long pk,
		com.liferay.document.library.kernel.model.DLFileEntryType
			dlFileEntryType) {

		dlFolderToDLFileEntryTypeTableMapper.deleteTableMapping(
			pk, dlFileEntryType.getPrimaryKey());
	}

	/**
	 * Removes the association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types
	 */
	@Override
	public void removeDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs) {
		dlFolderToDLFileEntryTypeTableMapper.deleteTableMappings(
			pk, dlFileEntryTypePKs);
	}

	/**
	 * Removes the association between the document library folder and the document library file entry types. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types
	 */
	@Override
	public void removeDLFileEntryTypes(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFileEntryTypes) {

		removeDLFileEntryTypes(
			pk,
			ListUtil.toLongArray(
				dlFileEntryTypes,
				com.liferay.document.library.kernel.model.DLFileEntryType.
					FILE_ENTRY_TYPE_ID_ACCESSOR));
	}

	/**
	 * Sets the document library file entry types associated with the document library folder, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypePKs the primary keys of the document library file entry types to be associated with the document library folder
	 */
	@Override
	public void setDLFileEntryTypes(long pk, long[] dlFileEntryTypePKs) {
		Set<Long> newDLFileEntryTypePKsSet = SetUtil.fromArray(
			dlFileEntryTypePKs);
		Set<Long> oldDLFileEntryTypePKsSet = SetUtil.fromArray(
			dlFolderToDLFileEntryTypeTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeDLFileEntryTypePKsSet = new HashSet<Long>(
			oldDLFileEntryTypePKsSet);

		removeDLFileEntryTypePKsSet.removeAll(newDLFileEntryTypePKsSet);

		dlFolderToDLFileEntryTypeTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeDLFileEntryTypePKsSet));

		newDLFileEntryTypePKsSet.removeAll(oldDLFileEntryTypePKsSet);

		long companyId = 0;

		DLFolder dlFolder = fetchByPrimaryKey(pk);

		if (dlFolder == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = dlFolder.getCompanyId();
		}

		dlFolderToDLFileEntryTypeTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newDLFileEntryTypePKsSet));
	}

	/**
	 * Sets the document library file entry types associated with the document library folder, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the document library folder
	 * @param dlFileEntryTypes the document library file entry types to be associated with the document library folder
	 */
	@Override
	public void setDLFileEntryTypes(
		long pk,
		List<com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFileEntryTypes) {

		try {
			long[] dlFileEntryTypePKs = new long[dlFileEntryTypes.size()];

			for (int i = 0; i < dlFileEntryTypes.size(); i++) {
				com.liferay.document.library.kernel.model.DLFileEntryType
					dlFileEntryType = dlFileEntryTypes.get(i);

				dlFileEntryTypePKs[i] = dlFileEntryType.getPrimaryKey();
			}

			setDLFileEntryTypes(pk, dlFileEntryTypePKs);
		}
		catch (Exception exception) {
			throw processException(exception);
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
		return "folderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFOLDER;
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
		return DLFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFolder";
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
		Set<String> ctMaxColumnNames = new HashSet<String>();
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
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("mountPoint");
		ctMergeColumnNames.add("parentFolderId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMaxColumnNames.add("lastPostDate");
		ctMergeColumnNames.add("defaultFileEntryTypeId");
		ctMergeColumnNames.add("hidden_");
		ctMergeColumnNames.add("restrictionType");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");
		ctMergeColumnNames.add("fileEntryTypes");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MAX, ctMaxColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("folderId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("DLFileEntryTypes_DLFolders");

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "parentFolderId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library folder persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		dlFolderToDLFileEntryTypeTableMapper =
			TableMapperFactory.getTableMapper(
				"DLFileEntryTypes_DLFolders", "companyId", "folderId",
				"fileEntryTypeId", this, dlFileEntryTypePersistence);

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

		_finderPathWithPaginationFindByRepositoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRepositoryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"repositoryId"}, true);

		_finderPathWithoutPaginationFindByRepositoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRepositoryId",
			new String[] {Long.class.getName()}, new String[] {"repositoryId"},
			true);

		_finderPathCountByRepositoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRepositoryId",
			new String[] {Long.class.getName()}, new String[] {"repositoryId"},
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

		_finderPathFetchByR_M = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByR_M",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"repositoryId", "mountPoint"}, true);

		_finderPathWithPaginationFindByR_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"repositoryId", "parentFolderId"}, true);

		_finderPathWithoutPaginationFindByR_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"repositoryId", "parentFolderId"}, true);

		_finderPathCountByR_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"repositoryId", "parentFolderId"}, false);

		_finderPathWithPaginationFindByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"parentFolderId", "name"}, true);

		_finderPathWithoutPaginationFindByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"parentFolderId", "name"}, true);

		_finderPathCountByP_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"parentFolderId", "name"}, false);

		_finderPathWithPaginationFindByGtF_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtF_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"folderId", "companyId", "parentFolderId"}, true);

		_finderPathWithPaginationCountByGtF_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtF_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"folderId", "companyId", "parentFolderId"}, false);

		_finderPathWithPaginationFindByG_M_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "mountPoint", "parentFolderId"}, true);

		_finderPathWithoutPaginationFindByG_M_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "mountPoint", "parentFolderId"}, true);

		_finderPathCountByG_M_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_M_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "mountPoint", "parentFolderId"}, false);

		_finderPathFetchByG_P_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "name"}, true);

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

		_finderPathWithPaginationFindByG_M_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "mountPoint", "parentFolderId", "hidden_"},
			true);

		_finderPathWithoutPaginationFindByG_M_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "mountPoint", "parentFolderId", "hidden_"},
			true);

		_finderPathCountByG_M_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_M_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "mountPoint", "parentFolderId", "hidden_"},
			false);

		_finderPathWithPaginationFindByG_M_LikeT_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_LikeT_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "mountPoint", "treePath", "hidden_"},
			true);

		_finderPathWithPaginationCountByG_M_LikeT_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_M_LikeT_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "mountPoint", "treePath", "hidden_"},
			false);

		_finderPathWithPaginationFindByG_P_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_H_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "hidden_", "status"},
			true);

		_finderPathWithoutPaginationFindByG_P_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_H_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "hidden_", "status"},
			true);

		_finderPathCountByG_P_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_H_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "hidden_", "status"},
			false);

		_finderPathWithPaginationFindByG_M_P_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_P_H_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "mountPoint", "parentFolderId", "hidden_", "status"
			},
			true);

		_finderPathWithoutPaginationFindByG_M_P_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_M_P_H_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"groupId", "mountPoint", "parentFolderId", "hidden_", "status"
			},
			true);

		_finderPathCountByG_M_P_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_M_P_H_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"groupId", "mountPoint", "parentFolderId", "hidden_", "status"
			},
			false);

		_finderPathWithPaginationFindByG_M_LikeT_H_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_M_LikeT_H_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "mountPoint", "treePath", "hidden_", "status"
			},
			true);

		_finderPathWithPaginationCountByG_M_LikeT_H_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_M_LikeT_H_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"groupId", "mountPoint", "treePath", "hidden_", "status"
			},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		DLFolderUtil.setPersistence(this);
	}

	public void destroy() {
		DLFolderUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFolderImpl.class.getName());

		TableMapperFactory.removeTableMapper("DLFileEntryTypes_DLFolders");
	}

	@BeanReference(type = DLFileEntryTypePersistence.class)
	protected DLFileEntryTypePersistence dlFileEntryTypePersistence;

	protected TableMapper
		<DLFolder, com.liferay.document.library.kernel.model.DLFileEntryType>
			dlFolderToDLFileEntryTypeTableMapper;

	private static final String _SQL_SELECT_DLFOLDER =
		"SELECT dlFolder FROM DLFolder dlFolder";

	private static final String _SQL_SELECT_DLFOLDER_WHERE =
		"SELECT dlFolder FROM DLFolder dlFolder WHERE ";

	private static final String _SQL_COUNT_DLFOLDER =
		"SELECT COUNT(dlFolder) FROM DLFolder dlFolder";

	private static final String _SQL_COUNT_DLFOLDER_WHERE =
		"SELECT COUNT(dlFolder) FROM DLFolder dlFolder WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"dlFolder.folderId";

	private static final String _FILTER_SQL_SELECT_DLFOLDER_WHERE =
		"SELECT DISTINCT {dlFolder.*} FROM DLFolder dlFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DLFolder.*} FROM (SELECT DISTINCT dlFolder.folderId FROM DLFolder dlFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DLFOLDER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DLFolder ON TEMP_TABLE.folderId = DLFolder.folderId";

	private static final String _FILTER_SQL_COUNT_DLFOLDER_WHERE =
		"SELECT COUNT(DISTINCT dlFolder.folderId) AS COUNT_VALUE FROM DLFolder dlFolder WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "dlFolder";

	private static final String _FILTER_ENTITY_TABLE = "DLFolder";

	private static final String _ORDER_BY_ENTITY_ALIAS = "dlFolder.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DLFolder.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DLFolder exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFolder exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "hidden"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}