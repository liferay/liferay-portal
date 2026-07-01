/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.NoSuchSuspiciousActivityException;
import com.liferay.message.boards.model.MBSuspiciousActivity;
import com.liferay.message.boards.model.MBSuspiciousActivityTable;
import com.liferay.message.boards.model.impl.MBSuspiciousActivityImpl;
import com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl;
import com.liferay.message.boards.service.persistence.MBSuspiciousActivityPersistence;
import com.liferay.message.boards.service.persistence.MBSuspiciousActivityUtil;
import com.liferay.message.boards.service.persistence.impl.constants.MBPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the message boards suspicious activity service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBSuspiciousActivityPersistence.class)
public class MBSuspiciousActivityPersistenceImpl
	extends BasePersistenceImpl
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
	implements MBSuspiciousActivityPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBSuspiciousActivityUtil</code> to access the message boards suspicious activity persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBSuspiciousActivityImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the message boards suspicious activities where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBSuspiciousActivityModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards suspicious activities
	 */
	@Override
	public List<MBSuspiciousActivity> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBSuspiciousActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByUuid_First(
			String uuid,
			OrderByComparator<MBSuspiciousActivity> orderByComparator)
		throws NoSuchSuspiciousActivityException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByUuid_First(
		String uuid,
		OrderByComparator<MBSuspiciousActivity> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the message boards suspicious activities where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of message boards suspicious activities where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the message boards suspicious activity where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchSuspiciousActivityException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByUUID_G(String uuid, long groupId)
		throws NoSuchSuspiciousActivityException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the message boards suspicious activity where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the message boards suspicious activity where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards suspicious activity that was removed
	 */
	@Override
	public MBSuspiciousActivity removeByUUID_G(String uuid, long groupId)
		throws NoSuchSuspiciousActivityException {

		MBSuspiciousActivity mbSuspiciousActivity = findByUUID_G(uuid, groupId);

		return remove(mbSuspiciousActivity);
	}

	/**
	 * Returns the number of message boards suspicious activities where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the message boards suspicious activities where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBSuspiciousActivityModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards suspicious activities
	 */
	@Override
	public List<MBSuspiciousActivity> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBSuspiciousActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBSuspiciousActivity> orderByComparator)
		throws NoSuchSuspiciousActivityException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBSuspiciousActivity> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the message boards suspicious activities where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of message boards suspicious activities where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_collectionPersistenceFinderByMessageId;

	/**
	 * Returns an ordered range of all the message boards suspicious activities where messageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBSuspiciousActivityModelImpl</code>.
	 * </p>
	 *
	 * @param messageId the message ID
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards suspicious activities
	 */
	@Override
	public List<MBSuspiciousActivity> findByMessageId(
		long messageId, int start, int end,
		OrderByComparator<MBSuspiciousActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByMessageId.find(
			finderCache, new Object[] {messageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where messageId = &#63;.
	 *
	 * @param messageId the message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByMessageId_First(
			long messageId,
			OrderByComparator<MBSuspiciousActivity> orderByComparator)
		throws NoSuchSuspiciousActivityException {

		return _collectionPersistenceFinderByMessageId.findFirst(
			finderCache, new Object[] {messageId}, orderByComparator);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where messageId = &#63;.
	 *
	 * @param messageId the message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByMessageId_First(
		long messageId,
		OrderByComparator<MBSuspiciousActivity> orderByComparator) {

		return _collectionPersistenceFinderByMessageId.fetchFirst(
			finderCache, new Object[] {messageId}, orderByComparator);
	}

	/**
	 * Removes all the message boards suspicious activities where messageId = &#63; from the database.
	 *
	 * @param messageId the message ID
	 */
	@Override
	public void removeByMessageId(long messageId) {
		_collectionPersistenceFinderByMessageId.remove(
			finderCache, new Object[] {messageId});
	}

	/**
	 * Returns the number of message boards suspicious activities where messageId = &#63;.
	 *
	 * @param messageId the message ID
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByMessageId(long messageId) {
		return _collectionPersistenceFinderByMessageId.count(
			finderCache, new Object[] {messageId});
	}

	private CollectionPersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_collectionPersistenceFinderByThreadId;

	/**
	 * Returns an ordered range of all the message boards suspicious activities where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBSuspiciousActivityModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards suspicious activities
	 */
	@Override
	public List<MBSuspiciousActivity> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBSuspiciousActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByThreadId.find(
			finderCache, new Object[] {threadId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByThreadId_First(
			long threadId,
			OrderByComparator<MBSuspiciousActivity> orderByComparator)
		throws NoSuchSuspiciousActivityException {

		return _collectionPersistenceFinderByThreadId.findFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Returns the first message boards suspicious activity in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByThreadId_First(
		long threadId,
		OrderByComparator<MBSuspiciousActivity> orderByComparator) {

		return _collectionPersistenceFinderByThreadId.fetchFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Removes all the message boards suspicious activities where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByThreadId(long threadId) {
		_collectionPersistenceFinderByThreadId.remove(
			finderCache, new Object[] {threadId});
	}

	/**
	 * Returns the number of message boards suspicious activities where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByThreadId(long threadId) {
		return _collectionPersistenceFinderByThreadId.count(
			finderCache, new Object[] {threadId});
	}

	private UniquePersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_uniquePersistenceFinderByU_M;

	/**
	 * Returns the message boards suspicious activity where userId = &#63; and messageId = &#63; or throws a <code>NoSuchSuspiciousActivityException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param messageId the message ID
	 * @return the matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByU_M(long userId, long messageId)
		throws NoSuchSuspiciousActivityException {

		return _uniquePersistenceFinderByU_M.find(
			finderCache, new Object[] {userId, messageId});
	}

	/**
	 * Returns the message boards suspicious activity where userId = &#63; and messageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param messageId the message ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByU_M(
		long userId, long messageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_M.fetch(
			finderCache, new Object[] {userId, messageId}, useFinderCache);
	}

	/**
	 * Removes the message boards suspicious activity where userId = &#63; and messageId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param messageId the message ID
	 * @return the message boards suspicious activity that was removed
	 */
	@Override
	public MBSuspiciousActivity removeByU_M(long userId, long messageId)
		throws NoSuchSuspiciousActivityException {

		MBSuspiciousActivity mbSuspiciousActivity = findByU_M(
			userId, messageId);

		return remove(mbSuspiciousActivity);
	}

	/**
	 * Returns the number of message boards suspicious activities where userId = &#63; and messageId = &#63;.
	 *
	 * @param userId the user ID
	 * @param messageId the message ID
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByU_M(long userId, long messageId) {
		return _uniquePersistenceFinderByU_M.count(
			finderCache, new Object[] {userId, messageId});
	}

	private UniquePersistenceFinder
		<MBSuspiciousActivity, NoSuchSuspiciousActivityException>
			_uniquePersistenceFinderByU_T;

	/**
	 * Returns the message boards suspicious activity where userId = &#63; and threadId = &#63; or throws a <code>NoSuchSuspiciousActivityException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @return the matching message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity findByU_T(long userId, long threadId)
		throws NoSuchSuspiciousActivityException {

		return _uniquePersistenceFinderByU_T.find(
			finderCache, new Object[] {userId, threadId});
	}

	/**
	 * Returns the message boards suspicious activity where userId = &#63; and threadId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByU_T(
		long userId, long threadId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_T.fetch(
			finderCache, new Object[] {userId, threadId}, useFinderCache);
	}

	/**
	 * Removes the message boards suspicious activity where userId = &#63; and threadId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @return the message boards suspicious activity that was removed
	 */
	@Override
	public MBSuspiciousActivity removeByU_T(long userId, long threadId)
		throws NoSuchSuspiciousActivityException {

		MBSuspiciousActivity mbSuspiciousActivity = findByU_T(userId, threadId);

		return remove(mbSuspiciousActivity);
	}

	/**
	 * Returns the number of message boards suspicious activities where userId = &#63; and threadId = &#63;.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @return the number of matching message boards suspicious activities
	 */
	@Override
	public int countByU_T(long userId, long threadId) {
		return _uniquePersistenceFinderByU_T.count(
			finderCache, new Object[] {userId, threadId});
	}

	public MBSuspiciousActivityPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBSuspiciousActivity.class);

		setModelImplClass(MBSuspiciousActivityImpl.class);
		setModelPKClass(long.class);

		setTable(MBSuspiciousActivityTable.INSTANCE);
	}

	/**
	 * Creates a new message boards suspicious activity with the primary key. Does not add the message boards suspicious activity to the database.
	 *
	 * @param suspiciousActivityId the primary key for the new message boards suspicious activity
	 * @return the new message boards suspicious activity
	 */
	@Override
	public MBSuspiciousActivity create(long suspiciousActivityId) {
		MBSuspiciousActivity mbSuspiciousActivity =
			new MBSuspiciousActivityImpl();

		mbSuspiciousActivity.setNew(true);
		mbSuspiciousActivity.setPrimaryKey(suspiciousActivityId);

		String uuid = PortalUUIDUtil.generate();

		mbSuspiciousActivity.setUuid(uuid);

		mbSuspiciousActivity.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbSuspiciousActivity;
	}

	/**
	 * Removes the message boards suspicious activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity that was removed
	 * @throws NoSuchSuspiciousActivityException if a message boards suspicious activity with the primary key could not be found
	 */
	@Override
	public MBSuspiciousActivity remove(long suspiciousActivityId)
		throws NoSuchSuspiciousActivityException {

		return remove((Serializable)suspiciousActivityId);
	}

	@Override
	protected MBSuspiciousActivity removeImpl(
		MBSuspiciousActivity mbSuspiciousActivity) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbSuspiciousActivity)) {
				mbSuspiciousActivity = (MBSuspiciousActivity)session.get(
					MBSuspiciousActivityImpl.class,
					mbSuspiciousActivity.getPrimaryKeyObj());
			}

			if ((mbSuspiciousActivity != null) &&
				ctPersistenceHelper.isRemove(mbSuspiciousActivity)) {

				session.delete(mbSuspiciousActivity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbSuspiciousActivity != null) {
			clearCache(mbSuspiciousActivity);
		}

		return mbSuspiciousActivity;
	}

	@Override
	public MBSuspiciousActivity updateImpl(
		MBSuspiciousActivity mbSuspiciousActivity) {

		boolean isNew = mbSuspiciousActivity.isNew();

		if (!(mbSuspiciousActivity instanceof MBSuspiciousActivityModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbSuspiciousActivity.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mbSuspiciousActivity);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbSuspiciousActivity proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBSuspiciousActivity implementation " +
					mbSuspiciousActivity.getClass());
		}

		MBSuspiciousActivityModelImpl mbSuspiciousActivityModelImpl =
			(MBSuspiciousActivityModelImpl)mbSuspiciousActivity;

		if (Validator.isNull(mbSuspiciousActivity.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbSuspiciousActivity.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbSuspiciousActivity.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbSuspiciousActivity.setCreateDate(date);
			}
			else {
				mbSuspiciousActivity.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!mbSuspiciousActivityModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbSuspiciousActivity.setModifiedDate(date);
			}
			else {
				mbSuspiciousActivity.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbSuspiciousActivity)) {
				if (!isNew) {
					session.evict(
						MBSuspiciousActivityImpl.class,
						mbSuspiciousActivity.getPrimaryKeyObj());
				}

				session.save(mbSuspiciousActivity);
			}
			else {
				mbSuspiciousActivity = (MBSuspiciousActivity)session.merge(
					mbSuspiciousActivity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mbSuspiciousActivity, false);

		if (isNew) {
			mbSuspiciousActivity.setNew(false);
		}

		mbSuspiciousActivity.resetOriginalValues();

		return mbSuspiciousActivity;
	}

	/**
	 * Returns the message boards suspicious activity with the primary key or throws a <code>NoSuchSuspiciousActivityException</code> if it could not be found.
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity
	 * @throws NoSuchSuspiciousActivityException if a message boards suspicious activity with the primary key could not be found
	 */
	@Override
	public MBSuspiciousActivity findByPrimaryKey(long suspiciousActivityId)
		throws NoSuchSuspiciousActivityException {

		return findByPrimaryKey((Serializable)suspiciousActivityId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the message boards suspicious activity with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity, or <code>null</code> if a message boards suspicious activity with the primary key could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchByPrimaryKey(long suspiciousActivityId) {
		return fetchByPrimaryKey((Serializable)suspiciousActivityId);
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
		return "suspiciousActivityId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBSUSPICIOUSACTIVITY;
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
		return MBSuspiciousActivityModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBSuspiciousActivity";
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
		ctMergeColumnNames.add("messageId");
		ctMergeColumnNames.add("threadId");
		ctMergeColumnNames.add("reason");
		ctMergeColumnNames.add("validated");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("suspiciousActivityId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the message boards suspicious activity persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE,
			_SQL_COUNT_MBSUSPICIOUSACTIVITY_WHERE,
			MBSuspiciousActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"mbSuspiciousActivity.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				MBSuspiciousActivity::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(MBSuspiciousActivity::getUuid),
				MBSuspiciousActivity::getGroupId),
			_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE, "",
			new FinderColumn<>(
				"mbSuspiciousActivity.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				MBSuspiciousActivity::getUuid),
			new FinderColumn<>(
				"mbSuspiciousActivity.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, MBSuspiciousActivity::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE,
				_SQL_COUNT_MBSUSPICIOUSACTIVITY_WHERE,
				MBSuspiciousActivityModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"mbSuspiciousActivity.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					MBSuspiciousActivity::getUuid),
				new FinderColumn<>(
					"mbSuspiciousActivity.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					MBSuspiciousActivity::getCompanyId));

		_collectionPersistenceFinderByMessageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByMessageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"messageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByMessageId", new String[] {Long.class.getName()},
					new String[] {"messageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByMessageId", new String[] {Long.class.getName()},
					new String[] {"messageId"}, false),
				_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE,
				_SQL_COUNT_MBSUSPICIOUSACTIVITY_WHERE,
				MBSuspiciousActivityModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"mbSuspiciousActivity.", "messageId",
					FinderColumn.Type.LONG, "=", true, true,
					MBSuspiciousActivity::getMessageId));

		_collectionPersistenceFinderByThreadId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByThreadId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByThreadId",
					new String[] {Long.class.getName()},
					new String[] {"threadId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByThreadId", new String[] {Long.class.getName()},
					new String[] {"threadId"}, false),
				_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE,
				_SQL_COUNT_MBSUSPICIOUSACTIVITY_WHERE,
				MBSuspiciousActivityModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"mbSuspiciousActivity.", "threadId", FinderColumn.Type.LONG,
					"=", true, true, MBSuspiciousActivity::getThreadId));

		_uniquePersistenceFinderByU_M = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_M",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "messageId"}, 0, 0, false,
				MBSuspiciousActivity::getUserId,
				MBSuspiciousActivity::getMessageId),
			_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE, "",
			new FinderColumn<>(
				"mbSuspiciousActivity.", "userId", FinderColumn.Type.LONG, "=",
				true, true, MBSuspiciousActivity::getUserId),
			new FinderColumn<>(
				"mbSuspiciousActivity.", "messageId", FinderColumn.Type.LONG,
				"=", true, true, MBSuspiciousActivity::getMessageId));

		_uniquePersistenceFinderByU_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_T",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "threadId"}, 0, 0, false,
				MBSuspiciousActivity::getUserId,
				MBSuspiciousActivity::getThreadId),
			_SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE, "",
			new FinderColumn<>(
				"mbSuspiciousActivity.", "userId", FinderColumn.Type.LONG, "=",
				true, true, MBSuspiciousActivity::getUserId),
			new FinderColumn<>(
				"mbSuspiciousActivity.", "threadId", FinderColumn.Type.LONG,
				"=", true, true, MBSuspiciousActivity::getThreadId));

		MBSuspiciousActivityUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBSuspiciousActivityUtil.setPersistence(null);

		entityCache.removeCache(MBSuspiciousActivityImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		MBSuspiciousActivityModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MBSUSPICIOUSACTIVITY =
		"SELECT mbSuspiciousActivity FROM MBSuspiciousActivity mbSuspiciousActivity";

	private static final String _SQL_SELECT_MBSUSPICIOUSACTIVITY_WHERE =
		"SELECT mbSuspiciousActivity FROM MBSuspiciousActivity mbSuspiciousActivity WHERE ";

	private static final String _SQL_COUNT_MBSUSPICIOUSACTIVITY_WHERE =
		"SELECT COUNT(mbSuspiciousActivity) FROM MBSuspiciousActivity mbSuspiciousActivity WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-517529512