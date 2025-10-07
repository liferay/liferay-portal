/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.impl;

import com.liferay.knowledge.base.exception.DuplicateKBFolderExternalReferenceCodeException;
import com.liferay.knowledge.base.exception.NoSuchFolderException;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBFolderTable;
import com.liferay.knowledge.base.model.impl.KBFolderImpl;
import com.liferay.knowledge.base.model.impl.KBFolderModelImpl;
import com.liferay.knowledge.base.service.persistence.KBFolderPersistence;
import com.liferay.knowledge.base.service.persistence.KBFolderUtil;
import com.liferay.knowledge.base.service.persistence.impl.constants.KBPersistenceConstants;
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
 * The persistence implementation for the kb folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KBFolderPersistence.class)
public class KBFolderPersistenceImpl
	extends BasePersistenceImpl<KBFolder> implements KBFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KBFolderUtil</code> to access the kb folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KBFolderImpl.class.getName();

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
	 * Returns all the kb folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

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

			List<KBFolder> list = null;

			if (useFinderCache) {
				list = (List<KBFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (KBFolder kbFolder : list) {
						if (!uuid.equals(kbFolder.getUuid())) {
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

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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
					sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<KBFolder>)QueryUtil.list(
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
	 * Returns the first kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUuid_First(
			String uuid, OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByUuid_First(uuid, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUuid_First(
		String uuid, OrderByComparator<KBFolder> orderByComparator) {

		List<KBFolder> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUuid_Last(
			String uuid, OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByUuid_Last(uuid, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUuid_Last(
		String uuid, OrderByComparator<KBFolder> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<KBFolder> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kb folders before and after the current kb folder in the ordered set where uuid = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] findByUuid_PrevAndNext(
			long kbFolderId, String uuid,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, kbFolder, uuid, orderByComparator, true);

			array[1] = kbFolder;

			array[2] = getByUuid_PrevAndNext(
				session, kbFolder, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KBFolder getByUuid_PrevAndNext(
		Session session, KBFolder kbFolder, String uuid,
		OrderByComparator<KBFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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
			sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kb folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (KBFolder kbFolder :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(kbFolder);
		}
	}

	/**
	 * Returns the number of kb folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_KBFOLDER_WHERE);

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
		"kbFolder.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(kbFolder.uuid IS NULL OR kbFolder.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the kb folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByUUID_G(uuid, groupId);

		if (kbFolder == null) {
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

		return kbFolder;
	}

	/**
	 * Returns the kb folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the kb folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

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

			if (result instanceof KBFolder) {
				KBFolder kbFolder = (KBFolder)result;

				if (!Objects.equals(uuid, kbFolder.getUuid()) ||
					(groupId != kbFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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

					List<KBFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						KBFolder kbFolder = list.get(0);

						result = kbFolder;

						cacheResult(kbFolder);
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
				return (KBFolder)result;
			}
		}
	}

	/**
	 * Removes the kb folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByUUID_G(uuid, groupId);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		KBFolder kbFolder = fetchByUUID_G(uuid, groupId);

		if (kbFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"kbFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(kbFolder.uuid IS NULL OR kbFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"kbFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

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

			List<KBFolder> list = null;

			if (useFinderCache) {
				list = (List<KBFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (KBFolder kbFolder : list) {
						if (!uuid.equals(kbFolder.getUuid()) ||
							(companyId != kbFolder.getCompanyId())) {

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

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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
					sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<KBFolder>)QueryUtil.list(
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
	 * Returns the first kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
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
	 * Returns the first kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KBFolder> orderByComparator) {

		List<KBFolder> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
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
	 * Returns the last kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<KBFolder> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<KBFolder> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kb folders before and after the current kb folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] findByUuid_C_PrevAndNext(
			long kbFolderId, String uuid, long companyId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, kbFolder, uuid, companyId, orderByComparator, true);

			array[1] = kbFolder;

			array[2] = getByUuid_C_PrevAndNext(
				session, kbFolder, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KBFolder getByUuid_C_PrevAndNext(
		Session session, KBFolder kbFolder, String uuid, long companyId,
		OrderByComparator<KBFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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
			sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kb folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (KBFolder kbFolder :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(kbFolder);
		}
	}

	/**
	 * Returns the number of kb folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_KBFOLDER_WHERE);

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
		"kbFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(kbFolder.uuid IS NULL OR kbFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"kbFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the kb folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching kb folders
	 */
	@Override
	public List<KBFolder> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

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

			List<KBFolder> list = null;

			if (useFinderCache) {
				list = (List<KBFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (KBFolder kbFolder : list) {
						if (companyId != kbFolder.getCompanyId()) {
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

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<KBFolder>)QueryUtil.list(
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
	 * Returns the first kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByCompanyId_First(
			long companyId, OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<KBFolder> orderByComparator) {

		List<KBFolder> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByCompanyId_Last(
			long companyId, OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByCompanyId_Last(companyId, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByCompanyId_Last(
		long companyId, OrderByComparator<KBFolder> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<KBFolder> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kb folders before and after the current kb folder in the ordered set where companyId = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] findByCompanyId_PrevAndNext(
			long kbFolderId, long companyId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, kbFolder, companyId, orderByComparator, true);

			array[1] = kbFolder;

			array[2] = getByCompanyId_PrevAndNext(
				session, kbFolder, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KBFolder getByCompanyId_PrevAndNext(
		Session session, KBFolder kbFolder, long companyId,
		OrderByComparator<KBFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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
			sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kb folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (KBFolder kbFolder :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(kbFolder);
		}
	}

	/**
	 * Returns the number of kb folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_KBFOLDER_WHERE);

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
		"kbFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @return the matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P(long groupId, long parentKBFolderId) {
		return findByG_P(
			groupId, parentKBFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P(
		long groupId, long parentKBFolderId, int start, int end) {

		return findByG_P(groupId, parentKBFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P(
		long groupId, long parentKBFolderId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return findByG_P(
			groupId, parentKBFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P(
		long groupId, long parentKBFolderId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, parentKBFolderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, parentKBFolderId, start, end, orderByComparator
				};
			}

			List<KBFolder> list = null;

			if (useFinderCache) {
				list = (List<KBFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (KBFolder kbFolder : list) {
						if ((groupId != kbFolder.getGroupId()) ||
							(parentKBFolderId !=
								kbFolder.getParentKBFolderId())) {

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

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTKBFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentKBFolderId);

					list = (List<KBFolder>)QueryUtil.list(
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
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_First(
			long groupId, long parentKBFolderId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByG_P_First(
			groupId, parentKBFolderId, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentKBFolderId=");
		sb.append(parentKBFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_First(
		long groupId, long parentKBFolderId,
		OrderByComparator<KBFolder> orderByComparator) {

		List<KBFolder> list = findByG_P(
			groupId, parentKBFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_Last(
			long groupId, long parentKBFolderId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByG_P_Last(
			groupId, parentKBFolderId, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentKBFolderId=");
		sb.append(parentKBFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_Last(
		long groupId, long parentKBFolderId,
		OrderByComparator<KBFolder> orderByComparator) {

		int count = countByG_P(groupId, parentKBFolderId);

		if (count == 0) {
			return null;
		}

		List<KBFolder> list = findByG_P(
			groupId, parentKBFolderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kb folders before and after the current kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] findByG_P_PrevAndNext(
			long kbFolderId, long groupId, long parentKBFolderId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, orderByComparator,
				true);

			array[1] = kbFolder;

			array[2] = getByG_P_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, orderByComparator,
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

	protected KBFolder getByG_P_PrevAndNext(
		Session session, KBFolder kbFolder, long groupId, long parentKBFolderId,
		OrderByComparator<KBFolder> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_KBFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTKBFOLDERID_2);

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
			sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentKBFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @return the matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P(long groupId, long parentKBFolderId) {
		return filterFindByG_P(
			groupId, parentKBFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P(
		long groupId, long parentKBFolderId, int start, int end) {

		return filterFindByG_P(groupId, parentKBFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders that the user has permissions to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P(
		long groupId, long parentKBFolderId, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentKBFolderId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentKBFolderId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTKBFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(KBFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(KBFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KBFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, KBFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, KBFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentKBFolderId);

			return (List<KBFolder>)QueryUtil.list(
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
	 * Returns the kb folders before and after the current kb folder in the ordered set of kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] filterFindByG_P_PrevAndNext(
			long kbFolderId, long groupId, long parentKBFolderId,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				kbFolderId, groupId, parentKBFolderId, orderByComparator);
		}

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, orderByComparator,
				true);

			array[1] = kbFolder;

			array[2] = filterGetByG_P_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, orderByComparator,
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

	protected KBFolder filterGetByG_P_PrevAndNext(
		Session session, KBFolder kbFolder, long groupId, long parentKBFolderId,
		OrderByComparator<KBFolder> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTKBFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(KBFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(KBFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KBFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, KBFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, KBFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentKBFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kb folders where groupId = &#63; and parentKBFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentKBFolderId) {
		for (KBFolder kbFolder :
				findByG_P(
					groupId, parentKBFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(kbFolder);
		}
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P(long groupId, long parentKBFolderId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentKBFolderId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTKBFOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentKBFolderId);

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
	 * Returns the number of kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @return the number of matching kb folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentKBFolderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentKBFolderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<KBFolder> kbFolders = findByG_P(groupId, parentKBFolderId);

			kbFolders = InlineSQLHelperUtil.filter(kbFolders, groupId);

			return kbFolders.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_KBFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTKBFOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KBFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentKBFolderId);

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
		"kbFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTKBFOLDERID_2 =
		"kbFolder.parentKBFolderId = ?";

	private FinderPath _finderPathFetchByG_P_N;

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_N(
			long groupId, long parentKBFolderId, String name)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByG_P_N(groupId, parentKBFolderId, name);

		if (kbFolder == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", parentKBFolderId=");
			sb.append(parentKBFolderId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFolderException(sb.toString());
		}

		return kbFolder;
	}

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_N(
		long groupId, long parentKBFolderId, String name) {

		return fetchByG_P_N(groupId, parentKBFolderId, name, true);
	}

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_N(
		long groupId, long parentKBFolderId, String name,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, parentKBFolderId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_P_N, finderArgs, this);
			}

			if (result instanceof KBFolder) {
				KBFolder kbFolder = (KBFolder)result;

				if ((groupId != kbFolder.getGroupId()) ||
					(parentKBFolderId != kbFolder.getParentKBFolderId()) ||
					!Objects.equals(name, kbFolder.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_N_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_N_PARENTKBFOLDERID_2);

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

					queryPos.add(parentKBFolderId);

					if (bindName) {
						queryPos.add(name);
					}

					List<KBFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_P_N, finderArgs, list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {
										groupId, parentKBFolderId, name
									};
								}

								_log.warn(
									"KBFolderPersistenceImpl.fetchByG_P_N(long, long, String, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						KBFolder kbFolder = list.get(0);

						result = kbFolder;

						cacheResult(kbFolder);
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
				return (KBFolder)result;
			}
		}
	}

	/**
	 * Removes the kb folder where groupId = &#63; and parentKBFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByG_P_N(
			long groupId, long parentKBFolderId, String name)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByG_P_N(groupId, parentKBFolderId, name);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param name the name
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P_N(long groupId, long parentKBFolderId, String name) {
		KBFolder kbFolder = fetchByG_P_N(groupId, parentKBFolderId, name);

		if (kbFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_N_GROUPID_2 =
		"kbFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_PARENTKBFOLDERID_2 =
		"kbFolder.parentKBFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_N_NAME_2 =
		"kbFolder.name = ?";

	private static final String _FINDER_COLUMN_G_P_N_NAME_3 =
		"(kbFolder.name IS NULL OR kbFolder.name = '')";

	private FinderPath _finderPathFetchByG_P_UT;

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_UT(
			long groupId, long parentKBFolderId, String urlTitle)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByG_P_UT(groupId, parentKBFolderId, urlTitle);

		if (kbFolder == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", parentKBFolderId=");
			sb.append(parentKBFolderId);

			sb.append(", urlTitle=");
			sb.append(urlTitle);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFolderException(sb.toString());
		}

		return kbFolder;
	}

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_UT(
		long groupId, long parentKBFolderId, String urlTitle) {

		return fetchByG_P_UT(groupId, parentKBFolderId, urlTitle, true);
	}

	/**
	 * Returns the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_UT(
		long groupId, long parentKBFolderId, String urlTitle,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			urlTitle = Objects.toString(urlTitle, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, parentKBFolderId, urlTitle};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_P_UT, finderArgs, this);
			}

			if (result instanceof KBFolder) {
				KBFolder kbFolder = (KBFolder)result;

				if ((groupId != kbFolder.getGroupId()) ||
					(parentKBFolderId != kbFolder.getParentKBFolderId()) ||
					!Objects.equals(urlTitle, kbFolder.getUrlTitle())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_UT_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_UT_PARENTKBFOLDERID_2);

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_UT_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_P_UT_URLTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentKBFolderId);

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					List<KBFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_P_UT, finderArgs, list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {
										groupId, parentKBFolderId, urlTitle
									};
								}

								_log.warn(
									"KBFolderPersistenceImpl.fetchByG_P_UT(long, long, String, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						KBFolder kbFolder = list.get(0);

						result = kbFolder;

						cacheResult(kbFolder);
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
				return (KBFolder)result;
			}
		}
	}

	/**
	 * Removes the kb folder where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByG_P_UT(
			long groupId, long parentKBFolderId, String urlTitle)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByG_P_UT(groupId, parentKBFolderId, urlTitle);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P_UT(
		long groupId, long parentKBFolderId, String urlTitle) {

		KBFolder kbFolder = fetchByG_P_UT(groupId, parentKBFolderId, urlTitle);

		if (kbFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_UT_GROUPID_2 =
		"kbFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_UT_PARENTKBFOLDERID_2 =
		"kbFolder.parentKBFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_UT_URLTITLE_2 =
		"kbFolder.urlTitle = ?";

	private static final String _FINDER_COLUMN_G_P_UT_URLTITLE_3 =
		"(kbFolder.urlTitle IS NULL OR kbFolder.urlTitle = '')";

	private FinderPath _finderPathWithPaginationFindByG_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_S;
	private FinderPath _finderPathCountByG_P_S;

	/**
	 * Returns all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @return the matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P_S(
		long groupId, long parentKBFolderId, int status) {

		return findByG_P_S(
			groupId, parentKBFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end) {

		return findByG_P_S(groupId, parentKBFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		return findByG_P_S(
			groupId, parentKBFolderId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb folders
	 */
	@Override
	public List<KBFolder> findByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end,
		OrderByComparator<KBFolder> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_S;
					finderArgs = new Object[] {
						groupId, parentKBFolderId, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_S;
				finderArgs = new Object[] {
					groupId, parentKBFolderId, status, start, end,
					orderByComparator
				};
			}

			List<KBFolder> list = null;

			if (useFinderCache) {
				list = (List<KBFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (KBFolder kbFolder : list) {
						if ((groupId != kbFolder.getGroupId()) ||
							(parentKBFolderId !=
								kbFolder.getParentKBFolderId()) ||
							(status != kbFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentKBFolderId);

					queryPos.add(status);

					list = (List<KBFolder>)QueryUtil.list(
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
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_S_First(
			long groupId, long parentKBFolderId, int status,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByG_P_S_First(
			groupId, parentKBFolderId, status, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentKBFolderId=");
		sb.append(parentKBFolderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_S_First(
		long groupId, long parentKBFolderId, int status,
		OrderByComparator<KBFolder> orderByComparator) {

		List<KBFolder> list = findByG_P_S(
			groupId, parentKBFolderId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByG_P_S_Last(
			long groupId, long parentKBFolderId, int status,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByG_P_S_Last(
			groupId, parentKBFolderId, status, orderByComparator);

		if (kbFolder != null) {
			return kbFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentKBFolderId=");
		sb.append(parentKBFolderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByG_P_S_Last(
		long groupId, long parentKBFolderId, int status,
		OrderByComparator<KBFolder> orderByComparator) {

		int count = countByG_P_S(groupId, parentKBFolderId, status);

		if (count == 0) {
			return null;
		}

		List<KBFolder> list = findByG_P_S(
			groupId, parentKBFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kb folders before and after the current kb folder in the ordered set where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] findByG_P_S_PrevAndNext(
			long kbFolderId, long groupId, long parentKBFolderId, int status,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = getByG_P_S_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, status,
				orderByComparator, true);

			array[1] = kbFolder;

			array[2] = getByG_P_S_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, status,
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

	protected KBFolder getByG_P_S_PrevAndNext(
		Session session, KBFolder kbFolder, long groupId, long parentKBFolderId,
		int status, OrderByComparator<KBFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_KBFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2);

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
			sb.append(KBFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentKBFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @return the matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P_S(
		long groupId, long parentKBFolderId, int status) {

		return filterFindByG_P_S(
			groupId, parentKBFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end) {

		return filterFindByG_P_S(
			groupId, parentKBFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders that the user has permissions to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb folders that the user has permission to view
	 */
	@Override
	public List<KBFolder> filterFindByG_P_S(
		long groupId, long parentKBFolderId, int status, int start, int end,
		OrderByComparator<KBFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S(
				groupId, parentKBFolderId, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_S(
					groupId, parentKBFolderId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(KBFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(KBFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KBFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, KBFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, KBFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentKBFolderId);

			queryPos.add(status);

			return (List<KBFolder>)QueryUtil.list(
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
	 * Returns the kb folders before and after the current kb folder in the ordered set of kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param kbFolderId the primary key of the current kb folder
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder[] filterFindByG_P_S_PrevAndNext(
			long kbFolderId, long groupId, long parentKBFolderId, int status,
			OrderByComparator<KBFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S_PrevAndNext(
				kbFolderId, groupId, parentKBFolderId, status,
				orderByComparator);
		}

		KBFolder kbFolder = findByPrimaryKey(kbFolderId);

		Session session = null;

		try {
			session = openSession();

			KBFolder[] array = new KBFolderImpl[3];

			array[0] = filterGetByG_P_S_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, status,
				orderByComparator, true);

			array[1] = kbFolder;

			array[2] = filterGetByG_P_S_PrevAndNext(
				session, kbFolder, groupId, parentKBFolderId, status,
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

	protected KBFolder filterGetByG_P_S_PrevAndNext(
		Session session, KBFolder kbFolder, long groupId, long parentKBFolderId,
		int status, OrderByComparator<KBFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(KBFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(KBFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KBFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, KBFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, KBFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentKBFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kbFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KBFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(long groupId, long parentKBFolderId, int status) {
		for (KBFolder kbFolder :
				findByG_P_S(
					groupId, parentKBFolderId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(kbFolder);
		}
	}

	/**
	 * Returns the number of kb folders where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByG_P_S(long groupId, long parentKBFolderId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P_S;

			Object[] finderArgs = new Object[] {
				groupId, parentKBFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_KBFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentKBFolderId);

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
	 * Returns the number of kb folders that the user has permission to view where groupId = &#63; and parentKBFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentKBFolderId the parent kb folder ID
	 * @param status the status
	 * @return the number of matching kb folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentKBFolderId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_S(groupId, parentKBFolderId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<KBFolder> kbFolders = findByG_P_S(
				groupId, parentKBFolderId, status);

			kbFolders = InlineSQLHelperUtil.filter(kbFolders, groupId);

			return kbFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_KBFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KBFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentKBFolderId);

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
		"kbFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_PARENTKBFOLDERID_2 =
		"kbFolder.parentKBFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_STATUS_2 =
		"kbFolder.status = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the kb folder where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching kb folder
	 * @throws NoSuchFolderException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByERC_G(externalReferenceCode, groupId);

		if (kbFolder == null) {
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

		return kbFolder;
	}

	/**
	 * Returns the kb folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByERC_G(String externalReferenceCode, long groupId) {
		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the kb folder where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

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

			if (result instanceof KBFolder) {
				KBFolder kbFolder = (KBFolder)result;

				if (!Objects.equals(
						externalReferenceCode,
						kbFolder.getExternalReferenceCode()) ||
					(groupId != kbFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_KBFOLDER_WHERE);

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

					List<KBFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						KBFolder kbFolder = list.get(0);

						result = kbFolder;

						cacheResult(kbFolder);
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
				return (KBFolder)result;
			}
		}
	}

	/**
	 * Removes the kb folder where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the kb folder that was removed
	 */
	@Override
	public KBFolder removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFolderException {

		KBFolder kbFolder = findByERC_G(externalReferenceCode, groupId);

		return remove(kbFolder);
	}

	/**
	 * Returns the number of kb folders where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching kb folders
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		KBFolder kbFolder = fetchByERC_G(externalReferenceCode, groupId);

		if (kbFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"kbFolder.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(kbFolder.externalReferenceCode IS NULL OR kbFolder.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"kbFolder.groupId = ?";

	public KBFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KBFolder.class);

		setModelImplClass(KBFolderImpl.class);
		setModelPKClass(long.class);

		setTable(KBFolderTable.INSTANCE);
	}

	/**
	 * Caches the kb folder in the entity cache if it is enabled.
	 *
	 * @param kbFolder the kb folder
	 */
	@Override
	public void cacheResult(KBFolder kbFolder) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					kbFolder.getCtCollectionId())) {

			entityCache.putResult(
				KBFolderImpl.class, kbFolder.getPrimaryKey(), kbFolder);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {kbFolder.getUuid(), kbFolder.getGroupId()},
				kbFolder);

			finderCache.putResult(
				_finderPathFetchByG_P_N,
				new Object[] {
					kbFolder.getGroupId(), kbFolder.getParentKBFolderId(),
					kbFolder.getName()
				},
				kbFolder);

			finderCache.putResult(
				_finderPathFetchByG_P_UT,
				new Object[] {
					kbFolder.getGroupId(), kbFolder.getParentKBFolderId(),
					kbFolder.getUrlTitle()
				},
				kbFolder);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					kbFolder.getExternalReferenceCode(), kbFolder.getGroupId()
				},
				kbFolder);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the kb folders in the entity cache if it is enabled.
	 *
	 * @param kbFolders the kb folders
	 */
	@Override
	public void cacheResult(List<KBFolder> kbFolders) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (kbFolders.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (KBFolder kbFolder : kbFolders) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						kbFolder.getCtCollectionId())) {

				if (entityCache.getResult(
						KBFolderImpl.class, kbFolder.getPrimaryKey()) == null) {

					cacheResult(kbFolder);
				}
			}
		}
	}

	/**
	 * Clears the cache for all kb folders.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(KBFolderImpl.class);

		finderCache.clearCache(KBFolderImpl.class);
	}

	/**
	 * Clears the cache for the kb folder.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(KBFolder kbFolder) {
		entityCache.removeResult(KBFolderImpl.class, kbFolder);
	}

	@Override
	public void clearCache(List<KBFolder> kbFolders) {
		for (KBFolder kbFolder : kbFolders) {
			entityCache.removeResult(KBFolderImpl.class, kbFolder);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(KBFolderImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(KBFolderImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		KBFolderModelImpl kbFolderModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					kbFolderModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				kbFolderModelImpl.getUuid(), kbFolderModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, kbFolderModelImpl);

			args = new Object[] {
				kbFolderModelImpl.getGroupId(),
				kbFolderModelImpl.getParentKBFolderId(),
				kbFolderModelImpl.getName()
			};

			finderCache.putResult(
				_finderPathFetchByG_P_N, args, kbFolderModelImpl);

			args = new Object[] {
				kbFolderModelImpl.getGroupId(),
				kbFolderModelImpl.getParentKBFolderId(),
				kbFolderModelImpl.getUrlTitle()
			};

			finderCache.putResult(
				_finderPathFetchByG_P_UT, args, kbFolderModelImpl);

			args = new Object[] {
				kbFolderModelImpl.getExternalReferenceCode(),
				kbFolderModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, kbFolderModelImpl);
		}
	}

	/**
	 * Creates a new kb folder with the primary key. Does not add the kb folder to the database.
	 *
	 * @param kbFolderId the primary key for the new kb folder
	 * @return the new kb folder
	 */
	@Override
	public KBFolder create(long kbFolderId) {
		KBFolder kbFolder = new KBFolderImpl();

		kbFolder.setNew(true);
		kbFolder.setPrimaryKey(kbFolderId);

		String uuid = PortalUUIDUtil.generate();

		kbFolder.setUuid(uuid);

		kbFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kbFolder;
	}

	/**
	 * Removes the kb folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder that was removed
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder remove(long kbFolderId) throws NoSuchFolderException {
		return remove((Serializable)kbFolderId);
	}

	/**
	 * Removes the kb folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the kb folder
	 * @return the kb folder that was removed
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder remove(Serializable primaryKey)
		throws NoSuchFolderException {

		Session session = null;

		try {
			session = openSession();

			KBFolder kbFolder = (KBFolder)session.get(
				KBFolderImpl.class, primaryKey);

			if (kbFolder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFolderException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(kbFolder);
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
	protected KBFolder removeImpl(KBFolder kbFolder) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kbFolder)) {
				kbFolder = (KBFolder)session.get(
					KBFolderImpl.class, kbFolder.getPrimaryKeyObj());
			}

			if ((kbFolder != null) && ctPersistenceHelper.isRemove(kbFolder)) {
				session.delete(kbFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kbFolder != null) {
			clearCache(kbFolder);
		}

		return kbFolder;
	}

	@Override
	public KBFolder updateImpl(KBFolder kbFolder) {
		boolean isNew = kbFolder.isNew();

		if (!(kbFolder instanceof KBFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kbFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kbFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kbFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KBFolder implementation " +
					kbFolder.getClass());
		}

		KBFolderModelImpl kbFolderModelImpl = (KBFolderModelImpl)kbFolder;

		if (Validator.isNull(kbFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kbFolder.setUuid(uuid);
		}

		if (Validator.isNull(kbFolder.getExternalReferenceCode())) {
			kbFolder.setExternalReferenceCode(kbFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					kbFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					kbFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = kbFolder.getCompanyId();

					long groupId = kbFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = kbFolder.getPrimaryKey();
					}

					try {
						kbFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								KBFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								kbFolder.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			KBFolder ercKBFolder = fetchByERC_G(
				kbFolder.getExternalReferenceCode(), kbFolder.getGroupId());

			if (isNew) {
				if (ercKBFolder != null) {
					throw new DuplicateKBFolderExternalReferenceCodeException(
						"Duplicate kb folder with external reference code " +
							kbFolder.getExternalReferenceCode() +
								" and group " + kbFolder.getGroupId());
				}
			}
			else {
				if ((ercKBFolder != null) &&
					(kbFolder.getKbFolderId() != ercKBFolder.getKbFolderId())) {

					throw new DuplicateKBFolderExternalReferenceCodeException(
						"Duplicate kb folder with external reference code " +
							kbFolder.getExternalReferenceCode() +
								" and group " + kbFolder.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kbFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				kbFolder.setCreateDate(date);
			}
			else {
				kbFolder.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kbFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kbFolder.setModifiedDate(date);
			}
			else {
				kbFolder.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kbFolder)) {
				if (!isNew) {
					session.evict(
						KBFolderImpl.class, kbFolder.getPrimaryKeyObj());
				}

				session.save(kbFolder);
			}
			else {
				kbFolder = (KBFolder)session.merge(kbFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			KBFolderImpl.class, kbFolderModelImpl, false, true);

		cacheUniqueFindersCache(kbFolderModelImpl);

		if (isNew) {
			kbFolder.setNew(false);
		}

		kbFolder.resetOriginalValues();

		return kbFolder;
	}

	/**
	 * Returns the kb folder with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the kb folder
	 * @return the kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFolderException {

		KBFolder kbFolder = fetchByPrimaryKey(primaryKey);

		if (kbFolder == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFolderException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return kbFolder;
	}

	/**
	 * Returns the kb folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder
	 * @throws NoSuchFolderException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder findByPrimaryKey(long kbFolderId)
		throws NoSuchFolderException {

		return findByPrimaryKey((Serializable)kbFolderId);
	}

	/**
	 * Returns the kb folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the kb folder
	 * @return the kb folder, or <code>null</code> if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(KBFolder.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		KBFolder kbFolder = (KBFolder)entityCache.getResult(
			KBFolderImpl.class, primaryKey);

		if (kbFolder != null) {
			return kbFolder;
		}

		Session session = null;

		try {
			session = openSession();

			kbFolder = (KBFolder)session.get(KBFolderImpl.class, primaryKey);

			if (kbFolder != null) {
				cacheResult(kbFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return kbFolder;
	}

	/**
	 * Returns the kb folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder, or <code>null</code> if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder fetchByPrimaryKey(long kbFolderId) {
		return fetchByPrimaryKey((Serializable)kbFolderId);
	}

	@Override
	public Map<Serializable, KBFolder> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(KBFolder.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, KBFolder> map = new HashMap<Serializable, KBFolder>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			KBFolder kbFolder = fetchByPrimaryKey(primaryKey);

			if (kbFolder != null) {
				map.put(primaryKey, kbFolder);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						KBFolder.class, primaryKey)) {

				KBFolder kbFolder = (KBFolder)entityCache.getResult(
					KBFolderImpl.class, primaryKey);

				if (kbFolder == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, kbFolder);
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

			for (KBFolder kbFolder : (List<KBFolder>)query.list()) {
				map.put(kbFolder.getPrimaryKeyObj(), kbFolder);

				cacheResult(kbFolder);
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
	 * Returns all the kb folders.
	 *
	 * @return the kb folders
	 */
	@Override
	public List<KBFolder> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kb folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of kb folders
	 */
	@Override
	public List<KBFolder> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the kb folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of kb folders
	 */
	@Override
	public List<KBFolder> findAll(
		int start, int end, OrderByComparator<KBFolder> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kb folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of kb folders
	 */
	@Override
	public List<KBFolder> findAll(
		int start, int end, OrderByComparator<KBFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

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

			List<KBFolder> list = null;

			if (useFinderCache) {
				list = (List<KBFolder>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_KBFOLDER);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_KBFOLDER;

					sql = sql.concat(KBFolderModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<KBFolder>)QueryUtil.list(
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
	 * Removes all the kb folders from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (KBFolder kbFolder : findAll()) {
			remove(kbFolder);
		}
	}

	/**
	 * Returns the number of kb folders.
	 *
	 * @return the number of kb folders
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					KBFolder.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_KBFOLDER);

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
		return "kbFolderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KBFOLDER;
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
		return KBFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KBFolder";
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
		ctMergeColumnNames.add("parentKBFolderId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("urlTitle");
		ctMergeColumnNames.add("description");
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
			CTColumnResolutionType.PK, Collections.singleton("kbFolderId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the kb folder persistence.
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
			new String[] {"groupId", "parentKBFolderId"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentKBFolderId"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentKBFolderId"}, false);

		_finderPathFetchByG_P_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "parentKBFolderId", "name"}, true);

		_finderPathFetchByG_P_UT = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_UT",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "parentKBFolderId", "urlTitle"}, true);

		_finderPathWithPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentKBFolderId", "status"}, true);

		_finderPathWithoutPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentKBFolderId", "status"}, true);

		_finderPathCountByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentKBFolderId", "status"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		KBFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KBFolderUtil.setPersistence(null);

		entityCache.removeCache(KBFolderImpl.class.getName());
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KBPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_KBFOLDER =
		"SELECT kbFolder FROM KBFolder kbFolder";

	private static final String _SQL_SELECT_KBFOLDER_WHERE =
		"SELECT kbFolder FROM KBFolder kbFolder WHERE ";

	private static final String _SQL_COUNT_KBFOLDER =
		"SELECT COUNT(kbFolder) FROM KBFolder kbFolder";

	private static final String _SQL_COUNT_KBFOLDER_WHERE =
		"SELECT COUNT(kbFolder) FROM KBFolder kbFolder WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"kbFolder.kbFolderId";

	private static final String _FILTER_SQL_SELECT_KBFOLDER_WHERE =
		"SELECT DISTINCT {kbFolder.*} FROM KBFolder kbFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {KBFolder.*} FROM (SELECT DISTINCT kbFolder.kbFolderId FROM KBFolder kbFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_KBFOLDER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN KBFolder ON TEMP_TABLE.kbFolderId = KBFolder.kbFolderId";

	private static final String _FILTER_SQL_COUNT_KBFOLDER_WHERE =
		"SELECT COUNT(DISTINCT kbFolder.kbFolderId) AS COUNT_VALUE FROM KBFolder kbFolder WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "kbFolder";

	private static final String _FILTER_ENTITY_TABLE = "KBFolder";

	private static final String _ORDER_BY_ENTITY_ALIAS = "kbFolder.";

	private static final String _ORDER_BY_ENTITY_TABLE = "KBFolder.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No KBFolder exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KBFolder exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KBFolderPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}