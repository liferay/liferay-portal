/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.NoSuchThreadFlagException;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.message.boards.model.MBThreadFlagTable;
import com.liferay.message.boards.model.impl.MBThreadFlagImpl;
import com.liferay.message.boards.model.impl.MBThreadFlagModelImpl;
import com.liferay.message.boards.service.persistence.MBThreadFlagPersistence;
import com.liferay.message.boards.service.persistence.MBThreadFlagUtil;
import com.liferay.message.boards.service.persistence.impl.constants.MBPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * The persistence implementation for the message boards thread flag service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBThreadFlagPersistence.class)
public class MBThreadFlagPersistenceImpl
	extends BasePersistenceImpl<MBThreadFlag, NoSuchThreadFlagException>
	implements MBThreadFlagPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBThreadFlagUtil</code> to access the message boards thread flag persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBThreadFlagImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<MBThreadFlag, NoSuchThreadFlagException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the message boards thread flags where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards thread flags
	 * @param end the upper bound of the range of message boards thread flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards thread flags
	 */
	@Override
	public List<MBThreadFlag> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBThreadFlag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag
	 * @throws NoSuchThreadFlagException if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag findByUuid_First(
			String uuid, OrderByComparator<MBThreadFlag> orderByComparator)
		throws NoSuchThreadFlagException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag fetchByUuid_First(
		String uuid, OrderByComparator<MBThreadFlag> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the message boards thread flags where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of message boards thread flags where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards thread flags
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<MBThreadFlag, NoSuchThreadFlagException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the message boards thread flag where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchThreadFlagException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards thread flag
	 * @throws NoSuchThreadFlagException if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag findByUUID_G(String uuid, long groupId)
		throws NoSuchThreadFlagException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the message boards thread flag where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the message boards thread flag where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards thread flag that was removed
	 */
	@Override
	public MBThreadFlag removeByUUID_G(String uuid, long groupId)
		throws NoSuchThreadFlagException {

		MBThreadFlag mbThreadFlag = findByUUID_G(uuid, groupId);

		return remove(mbThreadFlag);
	}

	/**
	 * Returns the number of message boards thread flags where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards thread flags
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<MBThreadFlag, NoSuchThreadFlagException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the message boards thread flags where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards thread flags
	 * @param end the upper bound of the range of message boards thread flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards thread flags
	 */
	@Override
	public List<MBThreadFlag> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBThreadFlag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag
	 * @throws NoSuchThreadFlagException if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBThreadFlag> orderByComparator)
		throws NoSuchThreadFlagException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBThreadFlag> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the message boards thread flags where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of message boards thread flags where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards thread flags
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<MBThreadFlag, NoSuchThreadFlagException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the message boards thread flags where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message boards thread flags
	 * @param end the upper bound of the range of message boards thread flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards thread flags
	 */
	@Override
	public List<MBThreadFlag> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MBThreadFlag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag
	 * @throws NoSuchThreadFlagException if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag findByUserId_First(
			long userId, OrderByComparator<MBThreadFlag> orderByComparator)
		throws NoSuchThreadFlagException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag fetchByUserId_First(
		long userId, OrderByComparator<MBThreadFlag> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the message boards thread flags where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of message boards thread flags where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching message boards thread flags
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder<MBThreadFlag, NoSuchThreadFlagException>
		_collectionPersistenceFinderByThreadId;

	/**
	 * Returns an ordered range of all the message boards thread flags where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBThreadFlagModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message boards thread flags
	 * @param end the upper bound of the range of message boards thread flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards thread flags
	 */
	@Override
	public List<MBThreadFlag> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBThreadFlag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByThreadId.find(
			finderCache, new Object[] {threadId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag
	 * @throws NoSuchThreadFlagException if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag findByThreadId_First(
			long threadId, OrderByComparator<MBThreadFlag> orderByComparator)
		throws NoSuchThreadFlagException {

		return _collectionPersistenceFinderByThreadId.findFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Returns the first message boards thread flag in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag fetchByThreadId_First(
		long threadId, OrderByComparator<MBThreadFlag> orderByComparator) {

		return _collectionPersistenceFinderByThreadId.fetchFirst(
			finderCache, new Object[] {threadId}, orderByComparator);
	}

	/**
	 * Removes all the message boards thread flags where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	@Override
	public void removeByThreadId(long threadId) {
		_collectionPersistenceFinderByThreadId.remove(
			finderCache, new Object[] {threadId});
	}

	/**
	 * Returns the number of message boards thread flags where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message boards thread flags
	 */
	@Override
	public int countByThreadId(long threadId) {
		return _collectionPersistenceFinderByThreadId.count(
			finderCache, new Object[] {threadId});
	}

	private UniquePersistenceFinder<MBThreadFlag, NoSuchThreadFlagException>
		_uniquePersistenceFinderByU_T;

	/**
	 * Returns the message boards thread flag where userId = &#63; and threadId = &#63; or throws a <code>NoSuchThreadFlagException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @return the matching message boards thread flag
	 * @throws NoSuchThreadFlagException if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag findByU_T(long userId, long threadId)
		throws NoSuchThreadFlagException {

		return _uniquePersistenceFinderByU_T.find(
			finderCache, new Object[] {userId, threadId});
	}

	/**
	 * Returns the message boards thread flag where userId = &#63; and threadId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards thread flag, or <code>null</code> if a matching message boards thread flag could not be found
	 */
	@Override
	public MBThreadFlag fetchByU_T(
		long userId, long threadId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_T.fetch(
			finderCache, new Object[] {userId, threadId}, useFinderCache);
	}

	/**
	 * Removes the message boards thread flag where userId = &#63; and threadId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @return the message boards thread flag that was removed
	 */
	@Override
	public MBThreadFlag removeByU_T(long userId, long threadId)
		throws NoSuchThreadFlagException {

		MBThreadFlag mbThreadFlag = findByU_T(userId, threadId);

		return remove(mbThreadFlag);
	}

	/**
	 * Returns the number of message boards thread flags where userId = &#63; and threadId = &#63;.
	 *
	 * @param userId the user ID
	 * @param threadId the thread ID
	 * @return the number of matching message boards thread flags
	 */
	@Override
	public int countByU_T(long userId, long threadId) {
		return _uniquePersistenceFinderByU_T.count(
			finderCache, new Object[] {userId, threadId});
	}

	public MBThreadFlagPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBThreadFlag.class);

		setModelImplClass(MBThreadFlagImpl.class);
		setModelPKClass(long.class);

		setTable(MBThreadFlagTable.INSTANCE);
	}

	/**
	 * Creates a new message boards thread flag with the primary key. Does not add the message boards thread flag to the database.
	 *
	 * @param threadFlagId the primary key for the new message boards thread flag
	 * @return the new message boards thread flag
	 */
	@Override
	public MBThreadFlag create(long threadFlagId) {
		MBThreadFlag mbThreadFlag = new MBThreadFlagImpl();

		mbThreadFlag.setNew(true);
		mbThreadFlag.setPrimaryKey(threadFlagId);

		String uuid = PortalUUIDUtil.generate();

		mbThreadFlag.setUuid(uuid);

		mbThreadFlag.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbThreadFlag;
	}

	/**
	 * Removes the message boards thread flag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param threadFlagId the primary key of the message boards thread flag
	 * @return the message boards thread flag that was removed
	 * @throws NoSuchThreadFlagException if a message boards thread flag with the primary key could not be found
	 */
	@Override
	public MBThreadFlag remove(long threadFlagId)
		throws NoSuchThreadFlagException {

		return remove((Serializable)threadFlagId);
	}

	@Override
	protected MBThreadFlag removeImpl(MBThreadFlag mbThreadFlag) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbThreadFlag)) {
				mbThreadFlag = (MBThreadFlag)session.get(
					MBThreadFlagImpl.class, mbThreadFlag.getPrimaryKeyObj());
			}

			if ((mbThreadFlag != null) &&
				ctPersistenceHelper.isRemove(mbThreadFlag)) {

				session.delete(mbThreadFlag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbThreadFlag != null) {
			clearCache(mbThreadFlag);
		}

		return mbThreadFlag;
	}

	@Override
	public MBThreadFlag updateImpl(MBThreadFlag mbThreadFlag) {
		boolean isNew = mbThreadFlag.isNew();

		if (!(mbThreadFlag instanceof MBThreadFlagModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbThreadFlag.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mbThreadFlag);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbThreadFlag proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBThreadFlag implementation " +
					mbThreadFlag.getClass());
		}

		MBThreadFlagModelImpl mbThreadFlagModelImpl =
			(MBThreadFlagModelImpl)mbThreadFlag;

		if (Validator.isNull(mbThreadFlag.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbThreadFlag.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbThreadFlag.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbThreadFlag.setCreateDate(date);
			}
			else {
				mbThreadFlag.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbThreadFlagModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbThreadFlag.setModifiedDate(date);
			}
			else {
				mbThreadFlag.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbThreadFlag)) {
				if (!isNew) {
					session.evict(
						MBThreadFlagImpl.class,
						mbThreadFlag.getPrimaryKeyObj());
				}

				session.save(mbThreadFlag);
			}
			else {
				mbThreadFlag = (MBThreadFlag)session.merge(mbThreadFlag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mbThreadFlag, false);

		if (isNew) {
			mbThreadFlag.setNew(false);
		}

		mbThreadFlag.resetOriginalValues();

		return mbThreadFlag;
	}

	/**
	 * Returns the message boards thread flag with the primary key or throws a <code>NoSuchThreadFlagException</code> if it could not be found.
	 *
	 * @param threadFlagId the primary key of the message boards thread flag
	 * @return the message boards thread flag
	 * @throws NoSuchThreadFlagException if a message boards thread flag with the primary key could not be found
	 */
	@Override
	public MBThreadFlag findByPrimaryKey(long threadFlagId)
		throws NoSuchThreadFlagException {

		return findByPrimaryKey((Serializable)threadFlagId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the message boards thread flag with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param threadFlagId the primary key of the message boards thread flag
	 * @return the message boards thread flag, or <code>null</code> if a message boards thread flag with the primary key could not be found
	 */
	@Override
	public MBThreadFlag fetchByPrimaryKey(long threadFlagId) {
		return fetchByPrimaryKey((Serializable)threadFlagId);
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
		return "threadFlagId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBTHREADFLAG;
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
		return MBThreadFlagModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBThreadFlag";
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
		ctMergeColumnNames.add("threadId");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("threadFlagId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"userId", "threadId"});
	}

	/**
	 * Initializes the message boards thread flag persistence.
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
			_SQL_SELECT_MBTHREADFLAG_WHERE, _SQL_COUNT_MBTHREADFLAG_WHERE,
			MBThreadFlagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbThreadFlag.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBThreadFlag::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(MBThreadFlag::getUuid),
				MBThreadFlag::getGroupId),
			_SQL_SELECT_MBTHREADFLAG_WHERE, "",
			new FinderColumn<>(
				"mbThreadFlag.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBThreadFlag::getUuid),
			new FinderColumn<>(
				"mbThreadFlag.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBThreadFlag::getGroupId));

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
				_SQL_SELECT_MBTHREADFLAG_WHERE, _SQL_COUNT_MBTHREADFLAG_WHERE,
				MBThreadFlagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"mbThreadFlag.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, MBThreadFlag::getUuid),
				new FinderColumn<>(
					"mbThreadFlag.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBThreadFlag::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_MBTHREADFLAG_WHERE, _SQL_COUNT_MBTHREADFLAG_WHERE,
				MBThreadFlagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"mbThreadFlag.", "userId", FinderColumn.Type.LONG, "=",
					true, true, MBThreadFlag::getUserId));

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
				_SQL_SELECT_MBTHREADFLAG_WHERE, _SQL_COUNT_MBTHREADFLAG_WHERE,
				MBThreadFlagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"mbThreadFlag.", "threadId", FinderColumn.Type.LONG, "=",
					true, true, MBThreadFlag::getThreadId));

		_uniquePersistenceFinderByU_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_T",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "threadId"}, 0, 0, false,
				MBThreadFlag::getUserId, MBThreadFlag::getThreadId),
			_SQL_SELECT_MBTHREADFLAG_WHERE, "",
			new FinderColumn<>(
				"mbThreadFlag.", "userId", FinderColumn.Type.LONG, "=", true,
				true, MBThreadFlag::getUserId),
			new FinderColumn<>(
				"mbThreadFlag.", "threadId", FinderColumn.Type.LONG, "=", true,
				true, MBThreadFlag::getThreadId));

		MBThreadFlagUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBThreadFlagUtil.setPersistence(null);

		entityCache.removeCache(MBThreadFlagImpl.class.getName());
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
		MBThreadFlagModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MBTHREADFLAG =
		"SELECT mbThreadFlag FROM MBThreadFlag mbThreadFlag";

	private static final String _SQL_SELECT_MBTHREADFLAG_WHERE =
		"SELECT mbThreadFlag FROM MBThreadFlag mbThreadFlag WHERE ";

	private static final String _SQL_COUNT_MBTHREADFLAG_WHERE =
		"SELECT COUNT(mbThreadFlag) FROM MBThreadFlag mbThreadFlag WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBThreadFlag exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBThreadFlagPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1879562163