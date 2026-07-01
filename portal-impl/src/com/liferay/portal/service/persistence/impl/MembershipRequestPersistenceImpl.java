/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchMembershipRequestException;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.model.MembershipRequestTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.MembershipRequestPersistence;
import com.liferay.portal.kernel.service.persistence.MembershipRequestUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.MembershipRequestImpl;
import com.liferay.portal.model.impl.MembershipRequestModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the membership request service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class MembershipRequestPersistenceImpl
	extends BasePersistenceImpl
		<MembershipRequest, NoSuchMembershipRequestException>
	implements MembershipRequestPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MembershipRequestUtil</code> to access the membership request persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MembershipRequestImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<MembershipRequest, NoSuchMembershipRequestException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the membership requests where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MembershipRequestModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of membership requests
	 * @param end the upper bound of the range of membership requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching membership requests
	 */
	@Override
	public List<MembershipRequest> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MembershipRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first membership request in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request
	 * @throws NoSuchMembershipRequestException if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest findByGroupId_First(
			long groupId,
			OrderByComparator<MembershipRequest> orderByComparator)
		throws NoSuchMembershipRequestException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first membership request in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request, or <code>null</code> if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest fetchByGroupId_First(
		long groupId, OrderByComparator<MembershipRequest> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the membership requests where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of membership requests where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching membership requests
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<MembershipRequest, NoSuchMembershipRequestException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the membership requests where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MembershipRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of membership requests
	 * @param end the upper bound of the range of membership requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching membership requests
	 */
	@Override
	public List<MembershipRequest> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MembershipRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first membership request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request
	 * @throws NoSuchMembershipRequestException if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest findByUserId_First(
			long userId, OrderByComparator<MembershipRequest> orderByComparator)
		throws NoSuchMembershipRequestException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first membership request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request, or <code>null</code> if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest fetchByUserId_First(
		long userId, OrderByComparator<MembershipRequest> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the membership requests where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of membership requests where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching membership requests
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<MembershipRequest, NoSuchMembershipRequestException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the membership requests where groupId = &#63; and statusId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MembershipRequestModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param statusId the status ID
	 * @param start the lower bound of the range of membership requests
	 * @param end the upper bound of the range of membership requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching membership requests
	 */
	@Override
	public List<MembershipRequest> findByG_S(
		long groupId, long statusId, int start, int end,
		OrderByComparator<MembershipRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, statusId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first membership request in the ordered set where groupId = &#63; and statusId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param statusId the status ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request
	 * @throws NoSuchMembershipRequestException if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest findByG_S_First(
			long groupId, long statusId,
			OrderByComparator<MembershipRequest> orderByComparator)
		throws NoSuchMembershipRequestException {

		return _collectionPersistenceFinderByG_S.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, statusId},
			orderByComparator);
	}

	/**
	 * Returns the first membership request in the ordered set where groupId = &#63; and statusId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param statusId the status ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request, or <code>null</code> if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest fetchByG_S_First(
		long groupId, long statusId,
		OrderByComparator<MembershipRequest> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, statusId},
			orderByComparator);
	}

	/**
	 * Removes all the membership requests where groupId = &#63; and statusId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param statusId the status ID
	 */
	@Override
	public void removeByG_S(long groupId, long statusId) {
		_collectionPersistenceFinderByG_S.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, statusId});
	}

	/**
	 * Returns the number of membership requests where groupId = &#63; and statusId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param statusId the status ID
	 * @return the number of matching membership requests
	 */
	@Override
	public int countByG_S(long groupId, long statusId) {
		return _collectionPersistenceFinderByG_S.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, statusId});
	}

	private CollectionPersistenceFinder
		<MembershipRequest, NoSuchMembershipRequestException>
			_collectionPersistenceFinderByG_U_S;

	/**
	 * Returns an ordered range of all the membership requests where groupId = &#63; and userId = &#63; and statusId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MembershipRequestModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statusId the status ID
	 * @param start the lower bound of the range of membership requests
	 * @param end the upper bound of the range of membership requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching membership requests
	 */
	@Override
	public List<MembershipRequest> findByG_U_S(
		long groupId, long userId, long statusId, int start, int end,
		OrderByComparator<MembershipRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, statusId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first membership request in the ordered set where groupId = &#63; and userId = &#63; and statusId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statusId the status ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request
	 * @throws NoSuchMembershipRequestException if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest findByG_U_S_First(
			long groupId, long userId, long statusId,
			OrderByComparator<MembershipRequest> orderByComparator)
		throws NoSuchMembershipRequestException {

		return _collectionPersistenceFinderByG_U_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, statusId}, orderByComparator);
	}

	/**
	 * Returns the first membership request in the ordered set where groupId = &#63; and userId = &#63; and statusId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statusId the status ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching membership request, or <code>null</code> if a matching membership request could not be found
	 */
	@Override
	public MembershipRequest fetchByG_U_S_First(
		long groupId, long userId, long statusId,
		OrderByComparator<MembershipRequest> orderByComparator) {

		return _collectionPersistenceFinderByG_U_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, statusId}, orderByComparator);
	}

	/**
	 * Removes all the membership requests where groupId = &#63; and userId = &#63; and statusId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statusId the status ID
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, long statusId) {
		_collectionPersistenceFinderByG_U_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, statusId});
	}

	/**
	 * Returns the number of membership requests where groupId = &#63; and userId = &#63; and statusId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statusId the status ID
	 * @return the number of matching membership requests
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, long statusId) {
		return _collectionPersistenceFinderByG_U_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, statusId});
	}

	public MembershipRequestPersistenceImpl() {
		setModelClass(MembershipRequest.class);

		setModelImplClass(MembershipRequestImpl.class);
		setModelPKClass(long.class);

		setTable(MembershipRequestTable.INSTANCE);
	}

	/**
	 * Creates a new membership request with the primary key. Does not add the membership request to the database.
	 *
	 * @param membershipRequestId the primary key for the new membership request
	 * @return the new membership request
	 */
	@Override
	public MembershipRequest create(long membershipRequestId) {
		MembershipRequest membershipRequest = new MembershipRequestImpl();

		membershipRequest.setNew(true);
		membershipRequest.setPrimaryKey(membershipRequestId);

		membershipRequest.setCompanyId(CompanyThreadLocal.getCompanyId());

		return membershipRequest;
	}

	/**
	 * Removes the membership request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param membershipRequestId the primary key of the membership request
	 * @return the membership request that was removed
	 * @throws NoSuchMembershipRequestException if a membership request with the primary key could not be found
	 */
	@Override
	public MembershipRequest remove(long membershipRequestId)
		throws NoSuchMembershipRequestException {

		return remove((Serializable)membershipRequestId);
	}

	@Override
	protected MembershipRequest removeImpl(
		MembershipRequest membershipRequest) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(membershipRequest)) {
				membershipRequest = (MembershipRequest)session.get(
					MembershipRequestImpl.class,
					membershipRequest.getPrimaryKeyObj());
			}

			if (membershipRequest != null) {
				session.delete(membershipRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (membershipRequest != null) {
			clearCache(membershipRequest);
		}

		return membershipRequest;
	}

	@Override
	public MembershipRequest updateImpl(MembershipRequest membershipRequest) {
		boolean isNew = membershipRequest.isNew();

		if (!(membershipRequest instanceof MembershipRequestModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(membershipRequest.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					membershipRequest);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in membershipRequest proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MembershipRequest implementation " +
					membershipRequest.getClass());
		}

		MembershipRequestModelImpl membershipRequestModelImpl =
			(MembershipRequestModelImpl)membershipRequest;

		if (isNew && (membershipRequest.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				membershipRequest.setCreateDate(date);
			}
			else {
				membershipRequest.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(membershipRequest);
			}
			else {
				membershipRequest = (MembershipRequest)session.merge(
					membershipRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(membershipRequest, false);

		if (isNew) {
			membershipRequest.setNew(false);
		}

		membershipRequest.resetOriginalValues();

		return membershipRequest;
	}

	/**
	 * Returns the membership request with the primary key or throws a <code>NoSuchMembershipRequestException</code> if it could not be found.
	 *
	 * @param membershipRequestId the primary key of the membership request
	 * @return the membership request
	 * @throws NoSuchMembershipRequestException if a membership request with the primary key could not be found
	 */
	@Override
	public MembershipRequest findByPrimaryKey(long membershipRequestId)
		throws NoSuchMembershipRequestException {

		return findByPrimaryKey((Serializable)membershipRequestId);
	}

	/**
	 * Returns the membership request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param membershipRequestId the primary key of the membership request
	 * @return the membership request, or <code>null</code> if a membership request with the primary key could not be found
	 */
	@Override
	public MembershipRequest fetchByPrimaryKey(long membershipRequestId) {
		return fetchByPrimaryKey((Serializable)membershipRequestId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "membershipRequestId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MEMBERSHIPREQUEST;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MembershipRequestModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the membership request persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_MEMBERSHIPREQUEST_WHERE,
				_SQL_COUNT_MEMBERSHIPREQUEST_WHERE,
				MembershipRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"membershipRequest.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, MembershipRequest::getGroupId));

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
				_SQL_SELECT_MEMBERSHIPREQUEST_WHERE,
				_SQL_COUNT_MEMBERSHIPREQUEST_WHERE,
				MembershipRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"membershipRequest.", "userId", FinderColumn.Type.LONG, "=",
					true, true, MembershipRequest::getUserId));

		_collectionPersistenceFinderByG_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "statusId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "statusId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "statusId"}, false),
			_SQL_SELECT_MEMBERSHIPREQUEST_WHERE,
			_SQL_COUNT_MEMBERSHIPREQUEST_WHERE,
			MembershipRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"membershipRequest.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, MembershipRequest::getGroupId),
			new FinderColumn<>(
				"membershipRequest.", "statusId", FinderColumn.Type.LONG, "=",
				true, true, MembershipRequest::getStatusId));

		_collectionPersistenceFinderByG_U_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId", "statusId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "userId", "statusId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "userId", "statusId"}, false),
			_SQL_SELECT_MEMBERSHIPREQUEST_WHERE,
			_SQL_COUNT_MEMBERSHIPREQUEST_WHERE,
			MembershipRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"membershipRequest.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, MembershipRequest::getGroupId),
			new FinderColumn<>(
				"membershipRequest.", "userId", FinderColumn.Type.LONG, "=",
				true, true, MembershipRequest::getUserId),
			new FinderColumn<>(
				"membershipRequest.", "statusId", FinderColumn.Type.LONG, "=",
				true, true, MembershipRequest::getStatusId));

		MembershipRequestUtil.setPersistence(this);
	}

	public void destroy() {
		MembershipRequestUtil.setPersistence(null);

		EntityCacheUtil.removeCache(MembershipRequestImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		MembershipRequestModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MEMBERSHIPREQUEST =
		"SELECT membershipRequest FROM MembershipRequest membershipRequest";

	private static final String _SQL_SELECT_MEMBERSHIPREQUEST_WHERE =
		"SELECT membershipRequest FROM MembershipRequest membershipRequest WHERE ";

	private static final String _SQL_COUNT_MEMBERSHIPREQUEST_WHERE =
		"SELECT COUNT(membershipRequest) FROM MembershipRequest membershipRequest WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2121508116