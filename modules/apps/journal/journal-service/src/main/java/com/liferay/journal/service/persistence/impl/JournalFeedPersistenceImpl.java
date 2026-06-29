/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.exception.NoSuchFeedException;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.model.JournalFeedTable;
import com.liferay.journal.model.impl.JournalFeedImpl;
import com.liferay.journal.model.impl.JournalFeedModelImpl;
import com.liferay.journal.service.persistence.JournalFeedPersistence;
import com.liferay.journal.service.persistence.JournalFeedUtil;
import com.liferay.journal.service.persistence.impl.constants.JournalPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the journal feed service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = JournalFeedPersistence.class)
public class JournalFeedPersistenceImpl
	extends BasePersistenceImpl<JournalFeed, NoSuchFeedException>
	implements JournalFeedPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JournalFeedUtil</code> to access the journal feed persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JournalFeedImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<JournalFeed, NoSuchFeedException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the journal feeds where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFeedModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal feeds
	 */
	@Override
	public List<JournalFeed> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal feed in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal feed
	 * @throws NoSuchFeedException if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed findByUuid_First(
			String uuid, OrderByComparator<JournalFeed> orderByComparator)
		throws NoSuchFeedException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first journal feed in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed fetchByUuid_First(
		String uuid, OrderByComparator<JournalFeed> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the journal feeds where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of journal feeds where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching journal feeds
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<JournalFeed, NoSuchFeedException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the journal feed where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFeedException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal feed
	 * @throws NoSuchFeedException if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed findByUUID_G(String uuid, long groupId)
		throws NoSuchFeedException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the journal feed where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the journal feed where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the journal feed that was removed
	 */
	@Override
	public JournalFeed removeByUUID_G(String uuid, long groupId)
		throws NoSuchFeedException {

		JournalFeed journalFeed = findByUUID_G(uuid, groupId);

		return remove(journalFeed);
	}

	/**
	 * Returns the number of journal feeds where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching journal feeds
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<JournalFeed, NoSuchFeedException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the journal feeds where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFeedModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal feeds
	 */
	@Override
	public List<JournalFeed> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal feed in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal feed
	 * @throws NoSuchFeedException if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<JournalFeed> orderByComparator)
		throws NoSuchFeedException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first journal feed in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<JournalFeed> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the journal feeds where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of journal feeds where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching journal feeds
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<JournalFeed, NoSuchFeedException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the journal feeds where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFeedModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal feeds
	 */
	@Override
	public List<JournalFeed> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal feed in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal feed
	 * @throws NoSuchFeedException if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed findByGroupId_First(
			long groupId, OrderByComparator<JournalFeed> orderByComparator)
		throws NoSuchFeedException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first journal feed in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed fetchByGroupId_First(
		long groupId, OrderByComparator<JournalFeed> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal feeds that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JournalFeedModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal feeds that the user has permission to view
	 */
	@Override
	public List<JournalFeed> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the journal feeds where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of journal feeds where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal feeds
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of journal feeds that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal feeds that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private UniquePersistenceFinder<JournalFeed, NoSuchFeedException>
		_uniquePersistenceFinderByG_F;

	/**
	 * Returns the journal feed where groupId = &#63; and feedId = &#63; or throws a <code>NoSuchFeedException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param feedId the feed ID
	 * @return the matching journal feed
	 * @throws NoSuchFeedException if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed findByG_F(long groupId, String feedId)
		throws NoSuchFeedException {

		return _uniquePersistenceFinderByG_F.find(
			finderCache, new Object[] {groupId, feedId});
	}

	/**
	 * Returns the journal feed where groupId = &#63; and feedId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param feedId the feed ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Override
	public JournalFeed fetchByG_F(
		long groupId, String feedId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_F.fetch(
			finderCache, new Object[] {groupId, feedId}, useFinderCache);
	}

	/**
	 * Removes the journal feed where groupId = &#63; and feedId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param feedId the feed ID
	 * @return the journal feed that was removed
	 */
	@Override
	public JournalFeed removeByG_F(long groupId, String feedId)
		throws NoSuchFeedException {

		JournalFeed journalFeed = findByG_F(groupId, feedId);

		return remove(journalFeed);
	}

	/**
	 * Returns the number of journal feeds where groupId = &#63; and feedId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param feedId the feed ID
	 * @return the number of matching journal feeds
	 */
	@Override
	public int countByG_F(long groupId, String feedId) {
		return _uniquePersistenceFinderByG_F.count(
			finderCache, new Object[] {groupId, feedId});
	}

	public JournalFeedPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("id", "id_");

		setDBColumnNames(dbColumnNames);

		setModelClass(JournalFeed.class);

		setModelImplClass(JournalFeedImpl.class);
		setModelPKClass(long.class);

		setTable(JournalFeedTable.INSTANCE);
	}

	/**
	 * Creates a new journal feed with the primary key. Does not add the journal feed to the database.
	 *
	 * @param id the primary key for the new journal feed
	 * @return the new journal feed
	 */
	@Override
	public JournalFeed create(long id) {
		JournalFeed journalFeed = new JournalFeedImpl();

		journalFeed.setNew(true);
		journalFeed.setPrimaryKey(id);

		String uuid = PortalUUIDUtil.generate();

		journalFeed.setUuid(uuid);

		journalFeed.setCompanyId(CompanyThreadLocal.getCompanyId());

		return journalFeed;
	}

	/**
	 * Removes the journal feed with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed that was removed
	 * @throws NoSuchFeedException if a journal feed with the primary key could not be found
	 */
	@Override
	public JournalFeed remove(long id) throws NoSuchFeedException {
		return remove((Serializable)id);
	}

	@Override
	protected JournalFeed removeImpl(JournalFeed journalFeed) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(journalFeed)) {
				journalFeed = (JournalFeed)session.get(
					JournalFeedImpl.class, journalFeed.getPrimaryKeyObj());
			}

			if ((journalFeed != null) &&
				ctPersistenceHelper.isRemove(journalFeed)) {

				session.delete(journalFeed);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (journalFeed != null) {
			clearCache(journalFeed);
		}

		return journalFeed;
	}

	@Override
	public JournalFeed updateImpl(JournalFeed journalFeed) {
		boolean isNew = journalFeed.isNew();

		if (!(journalFeed instanceof JournalFeedModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(journalFeed.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(journalFeed);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in journalFeed proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JournalFeed implementation " +
					journalFeed.getClass());
		}

		JournalFeedModelImpl journalFeedModelImpl =
			(JournalFeedModelImpl)journalFeed;

		if (Validator.isNull(journalFeed.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			journalFeed.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (journalFeed.getCreateDate() == null)) {
			if (serviceContext == null) {
				journalFeed.setCreateDate(date);
			}
			else {
				journalFeed.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!journalFeedModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				journalFeed.setModifiedDate(date);
			}
			else {
				journalFeed.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(journalFeed)) {
				if (!isNew) {
					session.evict(
						JournalFeedImpl.class, journalFeed.getPrimaryKeyObj());
				}

				session.save(journalFeed);
			}
			else {
				journalFeed = (JournalFeed)session.merge(journalFeed);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(journalFeed, false);

		if (isNew) {
			journalFeed.setNew(false);
		}

		journalFeed.resetOriginalValues();

		return journalFeed;
	}

	/**
	 * Returns the journal feed with the primary key or throws a <code>NoSuchFeedException</code> if it could not be found.
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed
	 * @throws NoSuchFeedException if a journal feed with the primary key could not be found
	 */
	@Override
	public JournalFeed findByPrimaryKey(long id) throws NoSuchFeedException {
		return findByPrimaryKey((Serializable)id);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the journal feed with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed, or <code>null</code> if a journal feed with the primary key could not be found
	 */
	@Override
	public JournalFeed fetchByPrimaryKey(long id) {
		return fetchByPrimaryKey((Serializable)id);
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
		return "id_";
	}

	@Override
	protected String getPKFieldName() {
		return "id";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_JOURNALFEED;
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
		return JournalFeedModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JournalFeed";
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
		ctMergeColumnNames.add("feedId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("DDMStructureId");
		ctMergeColumnNames.add("DDMTemplateKey");
		ctMergeColumnNames.add("DDMRendererTemplateKey");
		ctMergeColumnNames.add("delta");
		ctMergeColumnNames.add("orderByCol");
		ctMergeColumnNames.add("orderByType");
		ctMergeColumnNames.add("targetLayoutFriendlyUrl");
		ctMergeColumnNames.add("targetPortletId");
		ctMergeColumnNames.add("contentField");
		ctMergeColumnNames.add("feedFormat");
		ctMergeColumnNames.add("feedVersion");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("id_"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "feedId"});
	}

	/**
	 * Initializes the journal feed persistence.
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
			_SQL_SELECT_JOURNALFEED_WHERE, _SQL_COUNT_JOURNALFEED_WHERE,
			JournalFeedModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"journalFeed.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, JournalFeed::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(JournalFeed::getUuid),
				JournalFeed::getGroupId),
			_SQL_SELECT_JOURNALFEED_WHERE, "",
			new FinderColumn<>(
				"journalFeed.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, JournalFeed::getUuid),
			new FinderColumn<>(
				"journalFeed.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, JournalFeed::getGroupId));

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
				_SQL_SELECT_JOURNALFEED_WHERE, _SQL_COUNT_JOURNALFEED_WHERE,
				JournalFeedModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalFeed.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, JournalFeed::getUuid),
				new FinderColumn<>(
					"journalFeed.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, JournalFeed::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_JOURNALFEED_WHERE, _SQL_COUNT_JOURNALFEED_WHERE,
				JournalFeedModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"journalFeed.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, JournalFeed::getGroupId));

		_uniquePersistenceFinderByG_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "feedId"}, 0, 2, false,
				JournalFeed::getGroupId,
				convertNullFunction(JournalFeed::getFeedId)),
			_SQL_SELECT_JOURNALFEED_WHERE, "",
			new FinderColumn<>(
				"journalFeed.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, JournalFeed::getGroupId),
			new FinderColumn<>(
				"journalFeed.", "feedId", FinderColumn.Type.STRING, "=", true,
				true, JournalFeed::getFeedId));

		JournalFeedUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JournalFeedUtil.setPersistence(null);

		entityCache.removeCache(JournalFeedImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		JournalFeedModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_JOURNALFEED =
		"SELECT journalFeed FROM JournalFeed journalFeed";

	private static final String _SQL_SELECT_JOURNALFEED_WHERE =
		"SELECT journalFeed FROM JournalFeed journalFeed WHERE ";

	private static final String _SQL_COUNT_JOURNALFEED_WHERE =
		"SELECT COUNT(journalFeed) FROM JournalFeed journalFeed WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No JournalFeed exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFeedPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "id"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1526971217