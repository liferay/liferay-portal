/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroChannelException;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroChannelTable;
import com.liferay.osb.faro.model.impl.FaroChannelImpl;
import com.liferay.osb.faro.model.impl.FaroChannelModelImpl;
import com.liferay.osb.faro.service.persistence.FaroChannelPersistence;
import com.liferay.osb.faro.service.persistence.FaroChannelUtil;
import com.liferay.osb.faro.service.persistence.impl.constants.OSBFaroPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the faro channel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroChannelPersistence.class)
public class FaroChannelPersistenceImpl
	extends BasePersistenceImpl<FaroChannel, NoSuchFaroChannelException>
	implements FaroChannelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroChannelUtil</code> to access the faro channel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroChannelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<FaroChannel, NoSuchFaroChannelException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the faro channels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroChannelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro channels
	 * @param end the upper bound of the range of faro channels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro channels
	 */
	@Override
	public List<FaroChannel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroChannel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro channel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro channel
	 * @throws NoSuchFaroChannelException if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel findByGroupId_First(
			long groupId, OrderByComparator<FaroChannel> orderByComparator)
		throws NoSuchFaroChannelException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first faro channel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro channel, or <code>null</code> if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel fetchByGroupId_First(
		long groupId, OrderByComparator<FaroChannel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the faro channels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of faro channels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro channels
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<FaroChannel, NoSuchFaroChannelException>
		_collectionPersistenceFinderByWorkspaceGroupId;

	/**
	 * Returns an ordered range of all the faro channels where workspaceGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroChannelModelImpl</code>.
	 * </p>
	 *
	 * @param workspaceGroupId the workspace group ID
	 * @param start the lower bound of the range of faro channels
	 * @param end the upper bound of the range of faro channels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro channels
	 */
	@Override
	public List<FaroChannel> findByWorkspaceGroupId(
		long workspaceGroupId, int start, int end,
		OrderByComparator<FaroChannel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByWorkspaceGroupId.find(
			finderCache, new Object[] {workspaceGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro channel in the ordered set where workspaceGroupId = &#63;.
	 *
	 * @param workspaceGroupId the workspace group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro channel
	 * @throws NoSuchFaroChannelException if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel findByWorkspaceGroupId_First(
			long workspaceGroupId,
			OrderByComparator<FaroChannel> orderByComparator)
		throws NoSuchFaroChannelException {

		return _collectionPersistenceFinderByWorkspaceGroupId.findFirst(
			finderCache, new Object[] {workspaceGroupId}, orderByComparator);
	}

	/**
	 * Returns the first faro channel in the ordered set where workspaceGroupId = &#63;.
	 *
	 * @param workspaceGroupId the workspace group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro channel, or <code>null</code> if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel fetchByWorkspaceGroupId_First(
		long workspaceGroupId,
		OrderByComparator<FaroChannel> orderByComparator) {

		return _collectionPersistenceFinderByWorkspaceGroupId.fetchFirst(
			finderCache, new Object[] {workspaceGroupId}, orderByComparator);
	}

	/**
	 * Removes all the faro channels where workspaceGroupId = &#63; from the database.
	 *
	 * @param workspaceGroupId the workspace group ID
	 */
	@Override
	public void removeByWorkspaceGroupId(long workspaceGroupId) {
		_collectionPersistenceFinderByWorkspaceGroupId.remove(
			finderCache, new Object[] {workspaceGroupId});
	}

	/**
	 * Returns the number of faro channels where workspaceGroupId = &#63;.
	 *
	 * @param workspaceGroupId the workspace group ID
	 * @return the number of matching faro channels
	 */
	@Override
	public int countByWorkspaceGroupId(long workspaceGroupId) {
		return _collectionPersistenceFinderByWorkspaceGroupId.count(
			finderCache, new Object[] {workspaceGroupId});
	}

	private CollectionPersistenceFinder<FaroChannel, NoSuchFaroChannelException>
		_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the faro channels where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroChannelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of faro channels
	 * @param end the upper bound of the range of faro channels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro channels
	 */
	@Override
	public List<FaroChannel> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<FaroChannel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			finderCache, new Object[] {groupId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro channel in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro channel
	 * @throws NoSuchFaroChannelException if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel findByG_U_First(
			long groupId, long userId,
			OrderByComparator<FaroChannel> orderByComparator)
		throws NoSuchFaroChannelException {

		return _collectionPersistenceFinderByG_U.findFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Returns the first faro channel in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro channel, or <code>null</code> if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<FaroChannel> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Removes all the faro channels where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			finderCache, new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of faro channels where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching faro channels
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			finderCache, new Object[] {groupId, userId});
	}

	private UniquePersistenceFinder<FaroChannel, NoSuchFaroChannelException>
		_uniquePersistenceFinderByC_W;

	/**
	 * Returns the faro channel where channelId = &#63; and workspaceGroupId = &#63; or throws a <code>NoSuchFaroChannelException</code> if it could not be found.
	 *
	 * @param channelId the channel ID
	 * @param workspaceGroupId the workspace group ID
	 * @return the matching faro channel
	 * @throws NoSuchFaroChannelException if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel findByC_W(String channelId, long workspaceGroupId)
		throws NoSuchFaroChannelException {

		return _uniquePersistenceFinderByC_W.find(
			finderCache, new Object[] {channelId, workspaceGroupId});
	}

	/**
	 * Returns the faro channel where channelId = &#63; and workspaceGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param channelId the channel ID
	 * @param workspaceGroupId the workspace group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro channel, or <code>null</code> if a matching faro channel could not be found
	 */
	@Override
	public FaroChannel fetchByC_W(
		String channelId, long workspaceGroupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_W.fetch(
			finderCache, new Object[] {channelId, workspaceGroupId},
			useFinderCache);
	}

	/**
	 * Removes the faro channel where channelId = &#63; and workspaceGroupId = &#63; from the database.
	 *
	 * @param channelId the channel ID
	 * @param workspaceGroupId the workspace group ID
	 * @return the faro channel that was removed
	 */
	@Override
	public FaroChannel removeByC_W(String channelId, long workspaceGroupId)
		throws NoSuchFaroChannelException {

		FaroChannel faroChannel = findByC_W(channelId, workspaceGroupId);

		return remove(faroChannel);
	}

	/**
	 * Returns the number of faro channels where channelId = &#63; and workspaceGroupId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param workspaceGroupId the workspace group ID
	 * @return the number of matching faro channels
	 */
	@Override
	public int countByC_W(String channelId, long workspaceGroupId) {
		return _uniquePersistenceFinderByC_W.count(
			finderCache, new Object[] {channelId, workspaceGroupId});
	}

	public FaroChannelPersistenceImpl() {
		setModelClass(FaroChannel.class);

		setModelImplClass(FaroChannelImpl.class);
		setModelPKClass(long.class);

		setTable(FaroChannelTable.INSTANCE);
	}

	/**
	 * Creates a new faro channel with the primary key. Does not add the faro channel to the database.
	 *
	 * @param faroChannelId the primary key for the new faro channel
	 * @return the new faro channel
	 */
	@Override
	public FaroChannel create(long faroChannelId) {
		FaroChannel faroChannel = new FaroChannelImpl();

		faroChannel.setNew(true);
		faroChannel.setPrimaryKey(faroChannelId);

		faroChannel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroChannel;
	}

	/**
	 * Removes the faro channel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroChannelId the primary key of the faro channel
	 * @return the faro channel that was removed
	 * @throws NoSuchFaroChannelException if a faro channel with the primary key could not be found
	 */
	@Override
	public FaroChannel remove(long faroChannelId)
		throws NoSuchFaroChannelException {

		return remove((Serializable)faroChannelId);
	}

	@Override
	protected FaroChannel removeImpl(FaroChannel faroChannel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroChannel)) {
				faroChannel = (FaroChannel)session.get(
					FaroChannelImpl.class, faroChannel.getPrimaryKeyObj());
			}

			if (faroChannel != null) {
				session.delete(faroChannel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroChannel != null) {
			clearCache(faroChannel);
		}

		return faroChannel;
	}

	@Override
	public FaroChannel updateImpl(FaroChannel faroChannel) {
		boolean isNew = faroChannel.isNew();

		if (!(faroChannel instanceof FaroChannelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroChannel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(faroChannel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroChannel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroChannel implementation " +
					faroChannel.getClass());
		}

		FaroChannelModelImpl faroChannelModelImpl =
			(FaroChannelModelImpl)faroChannel;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroChannel);
			}
			else {
				faroChannel = (FaroChannel)session.merge(faroChannel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(faroChannel, false);

		if (isNew) {
			faroChannel.setNew(false);
		}

		faroChannel.resetOriginalValues();

		return faroChannel;
	}

	/**
	 * Returns the faro channel with the primary key or throws a <code>NoSuchFaroChannelException</code> if it could not be found.
	 *
	 * @param faroChannelId the primary key of the faro channel
	 * @return the faro channel
	 * @throws NoSuchFaroChannelException if a faro channel with the primary key could not be found
	 */
	@Override
	public FaroChannel findByPrimaryKey(long faroChannelId)
		throws NoSuchFaroChannelException {

		return findByPrimaryKey((Serializable)faroChannelId);
	}

	/**
	 * Returns the faro channel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroChannelId the primary key of the faro channel
	 * @return the faro channel, or <code>null</code> if a faro channel with the primary key could not be found
	 */
	@Override
	public FaroChannel fetchByPrimaryKey(long faroChannelId) {
		return fetchByPrimaryKey((Serializable)faroChannelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "faroChannelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FAROCHANNEL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroChannelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro channel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_FAROCHANNEL_WHERE, _SQL_COUNT_FAROCHANNEL_WHERE,
				FaroChannelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"faroChannel.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, FaroChannel::getGroupId));

		_collectionPersistenceFinderByWorkspaceGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByWorkspaceGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"workspaceGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByWorkspaceGroupId",
					new String[] {Long.class.getName()},
					new String[] {"workspaceGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByWorkspaceGroupId",
					new String[] {Long.class.getName()},
					new String[] {"workspaceGroupId"}, false),
				_SQL_SELECT_FAROCHANNEL_WHERE, _SQL_COUNT_FAROCHANNEL_WHERE,
				FaroChannelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"faroChannel.", "workspaceGroupId", FinderColumn.Type.LONG,
					"=", true, true, FaroChannel::getWorkspaceGroupId));

		_collectionPersistenceFinderByG_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, false),
			_SQL_SELECT_FAROCHANNEL_WHERE, _SQL_COUNT_FAROCHANNEL_WHERE,
			FaroChannelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"faroChannel.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, FaroChannel::getGroupId),
			new FinderColumn<>(
				"faroChannel.", "userId", FinderColumn.Type.LONG, "=", true,
				true, FaroChannel::getUserId));

		_uniquePersistenceFinderByC_W = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_W",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"channelId", "workspaceGroupId"}, 0, 1, false,
				convertNullFunction(FaroChannel::getChannelId),
				FaroChannel::getWorkspaceGroupId),
			_SQL_SELECT_FAROCHANNEL_WHERE, "",
			new FinderColumn<>(
				"faroChannel.", "channelId", FinderColumn.Type.STRING, "=",
				true, true, FaroChannel::getChannelId),
			new FinderColumn<>(
				"faroChannel.", "workspaceGroupId", FinderColumn.Type.LONG, "=",
				true, true, FaroChannel::getWorkspaceGroupId));

		FaroChannelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroChannelUtil.setPersistence(null);

		entityCache.removeCache(FaroChannelImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		FaroChannelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FAROCHANNEL =
		"SELECT faroChannel FROM FaroChannel faroChannel";

	private static final String _SQL_SELECT_FAROCHANNEL_WHERE =
		"SELECT faroChannel FROM FaroChannel faroChannel WHERE ";

	private static final String _SQL_COUNT_FAROCHANNEL_WHERE =
		"SELECT COUNT(faroChannel) FROM FaroChannel faroChannel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FaroChannel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FaroChannelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:135673806