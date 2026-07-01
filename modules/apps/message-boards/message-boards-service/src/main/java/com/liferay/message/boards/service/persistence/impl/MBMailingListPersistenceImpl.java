/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.NoSuchMailingListException;
import com.liferay.message.boards.model.MBMailingList;
import com.liferay.message.boards.model.MBMailingListTable;
import com.liferay.message.boards.model.impl.MBMailingListImpl;
import com.liferay.message.boards.model.impl.MBMailingListModelImpl;
import com.liferay.message.boards.service.persistence.MBMailingListPersistence;
import com.liferay.message.boards.service.persistence.MBMailingListUtil;
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
 * The persistence implementation for the message boards mailing list service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBMailingListPersistence.class)
public class MBMailingListPersistenceImpl
	extends BasePersistenceImpl<MBMailingList, NoSuchMailingListException>
	implements MBMailingListPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBMailingListUtil</code> to access the message boards mailing list persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBMailingListImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<MBMailingList, NoSuchMailingListException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the message boards mailing lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards mailing lists
	 */
	@Override
	public List<MBMailingList> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList findByUuid_First(
			String uuid, OrderByComparator<MBMailingList> orderByComparator)
		throws NoSuchMailingListException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList fetchByUuid_First(
		String uuid, OrderByComparator<MBMailingList> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the message boards mailing lists where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of message boards mailing lists where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards mailing lists
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<MBMailingList, NoSuchMailingListException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the message boards mailing list where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMailingListException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList findByUUID_G(String uuid, long groupId)
		throws NoSuchMailingListException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the message boards mailing list where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the message boards mailing list where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards mailing list that was removed
	 */
	@Override
	public MBMailingList removeByUUID_G(String uuid, long groupId)
		throws NoSuchMailingListException {

		MBMailingList mbMailingList = findByUUID_G(uuid, groupId);

		return remove(mbMailingList);
	}

	/**
	 * Returns the number of message boards mailing lists where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards mailing lists
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<MBMailingList, NoSuchMailingListException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards mailing lists
	 */
	@Override
	public List<MBMailingList> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBMailingList> orderByComparator)
		throws NoSuchMailingListException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBMailingList> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the message boards mailing lists where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards mailing lists
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<MBMailingList, NoSuchMailingListException>
			_collectionPersistenceFinderByActive;

	/**
	 * Returns an ordered range of all the message boards mailing lists where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards mailing lists
	 */
	@Override
	public List<MBMailingList> findByActive(
		boolean active, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByActive.find(
			finderCache, new Object[] {active}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList findByActive_First(
			boolean active, OrderByComparator<MBMailingList> orderByComparator)
		throws NoSuchMailingListException {

		return _collectionPersistenceFinderByActive.findFirst(
			finderCache, new Object[] {active}, orderByComparator);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList fetchByActive_First(
		boolean active, OrderByComparator<MBMailingList> orderByComparator) {

		return _collectionPersistenceFinderByActive.fetchFirst(
			finderCache, new Object[] {active}, orderByComparator);
	}

	/**
	 * Removes all the message boards mailing lists where active = &#63; from the database.
	 *
	 * @param active the active
	 */
	@Override
	public void removeByActive(boolean active) {
		_collectionPersistenceFinderByActive.remove(
			finderCache, new Object[] {active});
	}

	/**
	 * Returns the number of message boards mailing lists where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching message boards mailing lists
	 */
	@Override
	public int countByActive(boolean active) {
		return _collectionPersistenceFinderByActive.count(
			finderCache, new Object[] {active});
	}

	private UniquePersistenceFinder<MBMailingList, NoSuchMailingListException>
		_uniquePersistenceFinderByG_C;

	/**
	 * Returns the message boards mailing list where groupId = &#63; and categoryId = &#63; or throws a <code>NoSuchMailingListException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList findByG_C(long groupId, long categoryId)
		throws NoSuchMailingListException {

		return _uniquePersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, categoryId});
	}

	/**
	 * Returns the message boards mailing list where groupId = &#63; and categoryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList fetchByG_C(
		long groupId, long categoryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C.fetch(
			finderCache, new Object[] {groupId, categoryId}, useFinderCache);
	}

	/**
	 * Removes the message boards mailing list where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the message boards mailing list that was removed
	 */
	@Override
	public MBMailingList removeByG_C(long groupId, long categoryId)
		throws NoSuchMailingListException {

		MBMailingList mbMailingList = findByG_C(groupId, categoryId);

		return remove(mbMailingList);
	}

	/**
	 * Returns the number of message boards mailing lists where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message boards mailing lists
	 */
	@Override
	public int countByG_C(long groupId, long categoryId) {
		return _uniquePersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, categoryId});
	}

	public MBMailingListPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBMailingList.class);

		setModelImplClass(MBMailingListImpl.class);
		setModelPKClass(long.class);

		setTable(MBMailingListTable.INSTANCE);
	}

	/**
	 * Creates a new message boards mailing list with the primary key. Does not add the message boards mailing list to the database.
	 *
	 * @param mailingListId the primary key for the new message boards mailing list
	 * @return the new message boards mailing list
	 */
	@Override
	public MBMailingList create(long mailingListId) {
		MBMailingList mbMailingList = new MBMailingListImpl();

		mbMailingList.setNew(true);
		mbMailingList.setPrimaryKey(mailingListId);

		String uuid = PortalUUIDUtil.generate();

		mbMailingList.setUuid(uuid);

		mbMailingList.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbMailingList;
	}

	/**
	 * Removes the message boards mailing list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list that was removed
	 * @throws NoSuchMailingListException if a message boards mailing list with the primary key could not be found
	 */
	@Override
	public MBMailingList remove(long mailingListId)
		throws NoSuchMailingListException {

		return remove((Serializable)mailingListId);
	}

	@Override
	protected MBMailingList removeImpl(MBMailingList mbMailingList) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbMailingList)) {
				mbMailingList = (MBMailingList)session.get(
					MBMailingListImpl.class, mbMailingList.getPrimaryKeyObj());
			}

			if ((mbMailingList != null) &&
				ctPersistenceHelper.isRemove(mbMailingList)) {

				session.delete(mbMailingList);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbMailingList != null) {
			clearCache(mbMailingList);
		}

		return mbMailingList;
	}

	@Override
	public MBMailingList updateImpl(MBMailingList mbMailingList) {
		boolean isNew = mbMailingList.isNew();

		if (!(mbMailingList instanceof MBMailingListModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbMailingList.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mbMailingList);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbMailingList proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBMailingList implementation " +
					mbMailingList.getClass());
		}

		MBMailingListModelImpl mbMailingListModelImpl =
			(MBMailingListModelImpl)mbMailingList;

		if (Validator.isNull(mbMailingList.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbMailingList.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbMailingList.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbMailingList.setCreateDate(date);
			}
			else {
				mbMailingList.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbMailingListModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbMailingList.setModifiedDate(date);
			}
			else {
				mbMailingList.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbMailingList)) {
				if (!isNew) {
					session.evict(
						MBMailingListImpl.class,
						mbMailingList.getPrimaryKeyObj());
				}

				session.save(mbMailingList);
			}
			else {
				mbMailingList = (MBMailingList)session.merge(mbMailingList);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mbMailingList, false);

		if (isNew) {
			mbMailingList.setNew(false);
		}

		mbMailingList.resetOriginalValues();

		return mbMailingList;
	}

	/**
	 * Returns the message boards mailing list with the primary key or throws a <code>NoSuchMailingListException</code> if it could not be found.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list
	 * @throws NoSuchMailingListException if a message boards mailing list with the primary key could not be found
	 */
	@Override
	public MBMailingList findByPrimaryKey(long mailingListId)
		throws NoSuchMailingListException {

		return findByPrimaryKey((Serializable)mailingListId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the message boards mailing list with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list, or <code>null</code> if a message boards mailing list with the primary key could not be found
	 */
	@Override
	public MBMailingList fetchByPrimaryKey(long mailingListId) {
		return fetchByPrimaryKey((Serializable)mailingListId);
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
		return "mailingListId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBMAILINGLIST;
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
		return MBMailingListModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBMailingList";
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
		ctMergeColumnNames.add("categoryId");
		ctMergeColumnNames.add("emailAddress");
		ctMergeColumnNames.add("inProtocol");
		ctMergeColumnNames.add("inServerName");
		ctMergeColumnNames.add("inServerPort");
		ctMergeColumnNames.add("inUseSSL");
		ctMergeColumnNames.add("inUserName");
		ctMergeColumnNames.add("inPassword");
		ctMergeColumnNames.add("inReadInterval");
		ctMergeColumnNames.add("outEmailAddress");
		ctMergeColumnNames.add("outCustom");
		ctMergeColumnNames.add("outServerName");
		ctMergeColumnNames.add("outServerPort");
		ctMergeColumnNames.add("outUseSSL");
		ctMergeColumnNames.add("outUserName");
		ctMergeColumnNames.add("outPassword");
		ctMergeColumnNames.add("allowAnonymous");
		ctMergeColumnNames.add("active_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("mailingListId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "categoryId"});
	}

	/**
	 * Initializes the message boards mailing list persistence.
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
			_SQL_SELECT_MBMAILINGLIST_WHERE, _SQL_COUNT_MBMAILINGLIST_WHERE,
			MBMailingListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbMailingList.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, MBMailingList::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(MBMailingList::getUuid),
				MBMailingList::getGroupId),
			_SQL_SELECT_MBMAILINGLIST_WHERE, "",
			new FinderColumn<>(
				"mbMailingList.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, MBMailingList::getUuid),
			new FinderColumn<>(
				"mbMailingList.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBMailingList::getGroupId));

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
				_SQL_SELECT_MBMAILINGLIST_WHERE, _SQL_COUNT_MBMAILINGLIST_WHERE,
				MBMailingListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"mbMailingList.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, MBMailingList::getUuid),
				new FinderColumn<>(
					"mbMailingList.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBMailingList::getCompanyId));

		_collectionPersistenceFinderByActive =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByActive",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, false),
				_SQL_SELECT_MBMAILINGLIST_WHERE, _SQL_COUNT_MBMAILINGLIST_WHERE,
				MBMailingListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"mbMailingList.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					MBMailingList::isActive));

		_uniquePersistenceFinderByG_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "categoryId"}, 0, 0, false,
				MBMailingList::getGroupId, MBMailingList::getCategoryId),
			_SQL_SELECT_MBMAILINGLIST_WHERE, "",
			new FinderColumn<>(
				"mbMailingList.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBMailingList::getGroupId),
			new FinderColumn<>(
				"mbMailingList.", "categoryId", FinderColumn.Type.LONG, "=",
				true, true, MBMailingList::getCategoryId));

		MBMailingListUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBMailingListUtil.setPersistence(null);

		entityCache.removeCache(MBMailingListImpl.class.getName());
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
		MBMailingListModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MBMAILINGLIST =
		"SELECT mbMailingList FROM MBMailingList mbMailingList";

	private static final String _SQL_SELECT_MBMAILINGLIST_WHERE =
		"SELECT mbMailingList FROM MBMailingList mbMailingList WHERE ";

	private static final String _SQL_COUNT_MBMAILINGLIST_WHERE =
		"SELECT COUNT(mbMailingList) FROM MBMailingList mbMailingList WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBMailingList exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBMailingListPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1905382965