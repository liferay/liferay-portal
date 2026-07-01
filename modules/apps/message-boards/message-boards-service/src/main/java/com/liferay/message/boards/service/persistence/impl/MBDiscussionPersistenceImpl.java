/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.exception.NoSuchDiscussionException;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBDiscussionTable;
import com.liferay.message.boards.model.impl.MBDiscussionImpl;
import com.liferay.message.boards.model.impl.MBDiscussionModelImpl;
import com.liferay.message.boards.service.persistence.MBDiscussionPersistence;
import com.liferay.message.boards.service.persistence.MBDiscussionUtil;
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
 * The persistence implementation for the message boards discussion service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MBDiscussionPersistence.class)
public class MBDiscussionPersistenceImpl
	extends BasePersistenceImpl<MBDiscussion, NoSuchDiscussionException>
	implements MBDiscussionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MBDiscussionUtil</code> to access the message boards discussion persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MBDiscussionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<MBDiscussion, NoSuchDiscussionException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the message boards discussions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBDiscussionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards discussions
	 * @param end the upper bound of the range of message boards discussions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards discussions
	 */
	@Override
	public List<MBDiscussion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBDiscussion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards discussion in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards discussion
	 * @throws NoSuchDiscussionException if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion findByUuid_First(
			String uuid, OrderByComparator<MBDiscussion> orderByComparator)
		throws NoSuchDiscussionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first message boards discussion in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion fetchByUuid_First(
		String uuid, OrderByComparator<MBDiscussion> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the message boards discussions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of message boards discussions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards discussions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<MBDiscussion, NoSuchDiscussionException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the message boards discussion where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDiscussionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards discussion
	 * @throws NoSuchDiscussionException if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion findByUUID_G(String uuid, long groupId)
		throws NoSuchDiscussionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the message boards discussion where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the message boards discussion where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards discussion that was removed
	 */
	@Override
	public MBDiscussion removeByUUID_G(String uuid, long groupId)
		throws NoSuchDiscussionException {

		MBDiscussion mbDiscussion = findByUUID_G(uuid, groupId);

		return remove(mbDiscussion);
	}

	/**
	 * Returns the number of message boards discussions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards discussions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<MBDiscussion, NoSuchDiscussionException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the message boards discussions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MBDiscussionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards discussions
	 * @param end the upper bound of the range of message boards discussions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards discussions
	 */
	@Override
	public List<MBDiscussion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBDiscussion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards discussion in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards discussion
	 * @throws NoSuchDiscussionException if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBDiscussion> orderByComparator)
		throws NoSuchDiscussionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first message boards discussion in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBDiscussion> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the message boards discussions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of message boards discussions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards discussions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<MBDiscussion, NoSuchDiscussionException>
		_uniquePersistenceFinderByThreadId;

	/**
	 * Returns the message boards discussion where threadId = &#63; or throws a <code>NoSuchDiscussionException</code> if it could not be found.
	 *
	 * @param threadId the thread ID
	 * @return the matching message boards discussion
	 * @throws NoSuchDiscussionException if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion findByThreadId(long threadId)
		throws NoSuchDiscussionException {

		return _uniquePersistenceFinderByThreadId.find(
			finderCache, new Object[] {threadId});
	}

	/**
	 * Returns the message boards discussion where threadId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param threadId the thread ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion fetchByThreadId(long threadId, boolean useFinderCache) {
		return _uniquePersistenceFinderByThreadId.fetch(
			finderCache, new Object[] {threadId}, useFinderCache);
	}

	/**
	 * Removes the message boards discussion where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @return the message boards discussion that was removed
	 */
	@Override
	public MBDiscussion removeByThreadId(long threadId)
		throws NoSuchDiscussionException {

		MBDiscussion mbDiscussion = findByThreadId(threadId);

		return remove(mbDiscussion);
	}

	/**
	 * Returns the number of message boards discussions where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message boards discussions
	 */
	@Override
	public int countByThreadId(long threadId) {
		return _uniquePersistenceFinderByThreadId.count(
			finderCache, new Object[] {threadId});
	}

	private UniquePersistenceFinder<MBDiscussion, NoSuchDiscussionException>
		_uniquePersistenceFinderByC_C;

	/**
	 * Returns the message boards discussion where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchDiscussionException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching message boards discussion
	 * @throws NoSuchDiscussionException if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion findByC_C(long classNameId, long classPK)
		throws NoSuchDiscussionException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the message boards discussion where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the message boards discussion where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the message boards discussion that was removed
	 */
	@Override
	public MBDiscussion removeByC_C(long classNameId, long classPK)
		throws NoSuchDiscussionException {

		MBDiscussion mbDiscussion = findByC_C(classNameId, classPK);

		return remove(mbDiscussion);
	}

	/**
	 * Returns the number of message boards discussions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message boards discussions
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	public MBDiscussionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MBDiscussion.class);

		setModelImplClass(MBDiscussionImpl.class);
		setModelPKClass(long.class);

		setTable(MBDiscussionTable.INSTANCE);
	}

	/**
	 * Creates a new message boards discussion with the primary key. Does not add the message boards discussion to the database.
	 *
	 * @param discussionId the primary key for the new message boards discussion
	 * @return the new message boards discussion
	 */
	@Override
	public MBDiscussion create(long discussionId) {
		MBDiscussion mbDiscussion = new MBDiscussionImpl();

		mbDiscussion.setNew(true);
		mbDiscussion.setPrimaryKey(discussionId);

		String uuid = PortalUUIDUtil.generate();

		mbDiscussion.setUuid(uuid);

		mbDiscussion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mbDiscussion;
	}

	/**
	 * Removes the message boards discussion with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion that was removed
	 * @throws NoSuchDiscussionException if a message boards discussion with the primary key could not be found
	 */
	@Override
	public MBDiscussion remove(long discussionId)
		throws NoSuchDiscussionException {

		return remove((Serializable)discussionId);
	}

	@Override
	protected MBDiscussion removeImpl(MBDiscussion mbDiscussion) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mbDiscussion)) {
				mbDiscussion = (MBDiscussion)session.get(
					MBDiscussionImpl.class, mbDiscussion.getPrimaryKeyObj());
			}

			if ((mbDiscussion != null) &&
				ctPersistenceHelper.isRemove(mbDiscussion)) {

				session.delete(mbDiscussion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mbDiscussion != null) {
			clearCache(mbDiscussion);
		}

		return mbDiscussion;
	}

	@Override
	public MBDiscussion updateImpl(MBDiscussion mbDiscussion) {
		boolean isNew = mbDiscussion.isNew();

		if (!(mbDiscussion instanceof MBDiscussionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mbDiscussion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mbDiscussion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mbDiscussion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MBDiscussion implementation " +
					mbDiscussion.getClass());
		}

		MBDiscussionModelImpl mbDiscussionModelImpl =
			(MBDiscussionModelImpl)mbDiscussion;

		if (Validator.isNull(mbDiscussion.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			mbDiscussion.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mbDiscussion.getCreateDate() == null)) {
			if (serviceContext == null) {
				mbDiscussion.setCreateDate(date);
			}
			else {
				mbDiscussion.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!mbDiscussionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mbDiscussion.setModifiedDate(date);
			}
			else {
				mbDiscussion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(mbDiscussion)) {
				if (!isNew) {
					session.evict(
						MBDiscussionImpl.class,
						mbDiscussion.getPrimaryKeyObj());
				}

				session.save(mbDiscussion);
			}
			else {
				mbDiscussion = (MBDiscussion)session.merge(mbDiscussion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mbDiscussion, false);

		if (isNew) {
			mbDiscussion.setNew(false);
		}

		mbDiscussion.resetOriginalValues();

		return mbDiscussion;
	}

	/**
	 * Returns the message boards discussion with the primary key or throws a <code>NoSuchDiscussionException</code> if it could not be found.
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion
	 * @throws NoSuchDiscussionException if a message boards discussion with the primary key could not be found
	 */
	@Override
	public MBDiscussion findByPrimaryKey(long discussionId)
		throws NoSuchDiscussionException {

		return findByPrimaryKey((Serializable)discussionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the message boards discussion with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion, or <code>null</code> if a message boards discussion with the primary key could not be found
	 */
	@Override
	public MBDiscussion fetchByPrimaryKey(long discussionId) {
		return fetchByPrimaryKey((Serializable)discussionId);
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
		return "discussionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MBDISCUSSION;
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
		return MBDiscussionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "MBDiscussion";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("threadId");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("discussionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"threadId"});

		_uniqueIndexColumnNames.add(new String[] {"classNameId", "classPK"});
	}

	/**
	 * Initializes the message boards discussion persistence.
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
			_SQL_SELECT_MBDISCUSSION_WHERE, _SQL_COUNT_MBDISCUSSION_WHERE,
			MBDiscussionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"mbDiscussion.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBDiscussion::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(MBDiscussion::getUuid),
				MBDiscussion::getGroupId),
			_SQL_SELECT_MBDISCUSSION_WHERE, "",
			new FinderColumn<>(
				"mbDiscussion.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, MBDiscussion::getUuid),
			new FinderColumn<>(
				"mbDiscussion.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MBDiscussion::getGroupId));

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
				_SQL_SELECT_MBDISCUSSION_WHERE, _SQL_COUNT_MBDISCUSSION_WHERE,
				MBDiscussionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"mbDiscussion.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, MBDiscussion::getUuid),
				new FinderColumn<>(
					"mbDiscussion.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, MBDiscussion::getCompanyId));

		_uniquePersistenceFinderByThreadId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByThreadId",
				new String[] {Long.class.getName()}, new String[] {"threadId"},
				0, 0, false, MBDiscussion::getThreadId),
			_SQL_SELECT_MBDISCUSSION_WHERE, "",
			new FinderColumn<>(
				"mbDiscussion.", "threadId", FinderColumn.Type.LONG, "=", true,
				true, MBDiscussion::getThreadId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				MBDiscussion::getClassNameId, MBDiscussion::getClassPK),
			_SQL_SELECT_MBDISCUSSION_WHERE, "",
			new FinderColumn<>(
				"mbDiscussion.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, MBDiscussion::getClassNameId),
			new FinderColumn<>(
				"mbDiscussion.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, MBDiscussion::getClassPK));

		MBDiscussionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MBDiscussionUtil.setPersistence(null);

		entityCache.removeCache(MBDiscussionImpl.class.getName());
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
		MBDiscussionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MBDISCUSSION =
		"SELECT mbDiscussion FROM MBDiscussion mbDiscussion";

	private static final String _SQL_SELECT_MBDISCUSSION_WHERE =
		"SELECT mbDiscussion FROM MBDiscussion mbDiscussion WHERE ";

	private static final String _SQL_COUNT_MBDISCUSSION_WHERE =
		"SELECT COUNT(mbDiscussion) FROM MBDiscussion mbDiscussion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MBDiscussion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MBDiscussionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:734664986