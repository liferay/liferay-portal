/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service.persistence.impl;

import com.liferay.invitation.invite.members.exception.NoSuchMemberRequestException;
import com.liferay.invitation.invite.members.model.MemberRequest;
import com.liferay.invitation.invite.members.model.MemberRequestTable;
import com.liferay.invitation.invite.members.model.impl.MemberRequestImpl;
import com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl;
import com.liferay.invitation.invite.members.service.persistence.MemberRequestPersistence;
import com.liferay.invitation.invite.members.service.persistence.MemberRequestUtil;
import com.liferay.invitation.invite.members.service.persistence.impl.constants.IMPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the member request service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MemberRequestPersistence.class)
public class MemberRequestPersistenceImpl
	extends BasePersistenceImpl<MemberRequest, NoSuchMemberRequestException>
	implements MemberRequestPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MemberRequestUtil</code> to access the member request persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MemberRequestImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<MemberRequest, NoSuchMemberRequestException>
		_uniquePersistenceFinderByKey;

	/**
	 * Returns the member request where key = &#63; or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param key the key
	 * @return the matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	@Override
	public MemberRequest findByKey(String key)
		throws NoSuchMemberRequestException {

		return _uniquePersistenceFinderByKey.find(
			finderCache, new Object[] {key});
	}

	/**
	 * Returns the member request where key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	@Override
	public MemberRequest fetchByKey(String key, boolean useFinderCache) {
		return _uniquePersistenceFinderByKey.fetch(
			finderCache, new Object[] {key}, useFinderCache);
	}

	/**
	 * Removes the member request where key = &#63; from the database.
	 *
	 * @param key the key
	 * @return the member request that was removed
	 */
	@Override
	public MemberRequest removeByKey(String key)
		throws NoSuchMemberRequestException {

		MemberRequest memberRequest = findByKey(key);

		return remove(memberRequest);
	}

	/**
	 * Returns the number of member requests where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching member requests
	 */
	@Override
	public int countByKey(String key) {
		return _uniquePersistenceFinderByKey.count(
			finderCache, new Object[] {key});
	}

	private CollectionPersistenceFinder
		<MemberRequest, NoSuchMemberRequestException>
			_collectionPersistenceFinderByReceiverUserId;

	/**
	 * Returns an ordered range of all the member requests where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MemberRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of member requests
	 * @param end the upper bound of the range of member requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching member requests
	 */
	@Override
	public List<MemberRequest> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<MemberRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByReceiverUserId.find(
			finderCache, new Object[] {receiverUserId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	@Override
	public MemberRequest findByReceiverUserId_First(
			long receiverUserId,
			OrderByComparator<MemberRequest> orderByComparator)
		throws NoSuchMemberRequestException {

		return _collectionPersistenceFinderByReceiverUserId.findFirst(
			finderCache, new Object[] {receiverUserId}, orderByComparator);
	}

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request, or <code>null</code> if a matching member request could not be found
	 */
	@Override
	public MemberRequest fetchByReceiverUserId_First(
		long receiverUserId,
		OrderByComparator<MemberRequest> orderByComparator) {

		return _collectionPersistenceFinderByReceiverUserId.fetchFirst(
			finderCache, new Object[] {receiverUserId}, orderByComparator);
	}

	/**
	 * Removes all the member requests where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	@Override
	public void removeByReceiverUserId(long receiverUserId) {
		_collectionPersistenceFinderByReceiverUserId.remove(
			finderCache, new Object[] {receiverUserId});
	}

	/**
	 * Returns the number of member requests where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching member requests
	 */
	@Override
	public int countByReceiverUserId(long receiverUserId) {
		return _collectionPersistenceFinderByReceiverUserId.count(
			finderCache, new Object[] {receiverUserId});
	}

	private CollectionPersistenceFinder
		<MemberRequest, NoSuchMemberRequestException>
			_collectionPersistenceFinderByR_S;

	/**
	 * Returns an ordered range of all the member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MemberRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of member requests
	 * @param end the upper bound of the range of member requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching member requests
	 */
	@Override
	public List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		OrderByComparator<MemberRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_S.find(
			finderCache, new Object[] {receiverUserId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	@Override
	public MemberRequest findByR_S_First(
			long receiverUserId, int status,
			OrderByComparator<MemberRequest> orderByComparator)
		throws NoSuchMemberRequestException {

		return _collectionPersistenceFinderByR_S.findFirst(
			finderCache, new Object[] {receiverUserId, status},
			orderByComparator);
	}

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request, or <code>null</code> if a matching member request could not be found
	 */
	@Override
	public MemberRequest fetchByR_S_First(
		long receiverUserId, int status,
		OrderByComparator<MemberRequest> orderByComparator) {

		return _collectionPersistenceFinderByR_S.fetchFirst(
			finderCache, new Object[] {receiverUserId, status},
			orderByComparator);
	}

	/**
	 * Removes all the member requests where receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	@Override
	public void removeByR_S(long receiverUserId, int status) {
		_collectionPersistenceFinderByR_S.remove(
			finderCache, new Object[] {receiverUserId, status});
	}

	/**
	 * Returns the number of member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching member requests
	 */
	@Override
	public int countByR_S(long receiverUserId, int status) {
		return _collectionPersistenceFinderByR_S.count(
			finderCache, new Object[] {receiverUserId, status});
	}

	private UniquePersistenceFinder<MemberRequest, NoSuchMemberRequestException>
		_uniquePersistenceFinderByG_R_S;

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	@Override
	public MemberRequest findByG_R_S(
			long groupId, long receiverUserId, int status)
		throws NoSuchMemberRequestException {

		return _uniquePersistenceFinderByG_R_S.find(
			finderCache, new Object[] {groupId, receiverUserId, status});
	}

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	@Override
	public MemberRequest fetchByG_R_S(
		long groupId, long receiverUserId, int status, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_R_S.fetch(
			finderCache, new Object[] {groupId, receiverUserId, status},
			useFinderCache);
	}

	/**
	 * Removes the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the member request that was removed
	 */
	@Override
	public MemberRequest removeByG_R_S(
			long groupId, long receiverUserId, int status)
		throws NoSuchMemberRequestException {

		MemberRequest memberRequest = findByG_R_S(
			groupId, receiverUserId, status);

		return remove(memberRequest);
	}

	/**
	 * Returns the number of member requests where groupId = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching member requests
	 */
	@Override
	public int countByG_R_S(long groupId, long receiverUserId, int status) {
		return _uniquePersistenceFinderByG_R_S.count(
			finderCache, new Object[] {groupId, receiverUserId, status});
	}

	public MemberRequestPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MemberRequest.class);

		setModelImplClass(MemberRequestImpl.class);
		setModelPKClass(long.class);

		setTable(MemberRequestTable.INSTANCE);
	}

	/**
	 * Creates a new member request with the primary key. Does not add the member request to the database.
	 *
	 * @param memberRequestId the primary key for the new member request
	 * @return the new member request
	 */
	@Override
	public MemberRequest create(long memberRequestId) {
		MemberRequest memberRequest = new MemberRequestImpl();

		memberRequest.setNew(true);
		memberRequest.setPrimaryKey(memberRequestId);

		memberRequest.setCompanyId(CompanyThreadLocal.getCompanyId());

		return memberRequest;
	}

	/**
	 * Removes the member request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request that was removed
	 * @throws NoSuchMemberRequestException if a member request with the primary key could not be found
	 */
	@Override
	public MemberRequest remove(long memberRequestId)
		throws NoSuchMemberRequestException {

		return remove((Serializable)memberRequestId);
	}

	@Override
	protected MemberRequest removeImpl(MemberRequest memberRequest) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(memberRequest)) {
				memberRequest = (MemberRequest)session.get(
					MemberRequestImpl.class, memberRequest.getPrimaryKeyObj());
			}

			if (memberRequest != null) {
				session.delete(memberRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (memberRequest != null) {
			clearCache(memberRequest);
		}

		return memberRequest;
	}

	@Override
	public MemberRequest updateImpl(MemberRequest memberRequest) {
		boolean isNew = memberRequest.isNew();

		if (!(memberRequest instanceof MemberRequestModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(memberRequest.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					memberRequest);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in memberRequest proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MemberRequest implementation " +
					memberRequest.getClass());
		}

		MemberRequestModelImpl memberRequestModelImpl =
			(MemberRequestModelImpl)memberRequest;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (memberRequest.getCreateDate() == null)) {
			if (serviceContext == null) {
				memberRequest.setCreateDate(date);
			}
			else {
				memberRequest.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!memberRequestModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				memberRequest.setModifiedDate(date);
			}
			else {
				memberRequest.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(memberRequest);
			}
			else {
				memberRequest = (MemberRequest)session.merge(memberRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(memberRequest, false);

		if (isNew) {
			memberRequest.setNew(false);
		}

		memberRequest.resetOriginalValues();

		return memberRequest;
	}

	/**
	 * Returns the member request with the primary key or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request
	 * @throws NoSuchMemberRequestException if a member request with the primary key could not be found
	 */
	@Override
	public MemberRequest findByPrimaryKey(long memberRequestId)
		throws NoSuchMemberRequestException {

		return findByPrimaryKey((Serializable)memberRequestId);
	}

	/**
	 * Returns the member request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request, or <code>null</code> if a member request with the primary key could not be found
	 */
	@Override
	public MemberRequest fetchByPrimaryKey(long memberRequestId) {
		return fetchByPrimaryKey((Serializable)memberRequestId);
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
		return "memberRequestId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MEMBERREQUEST;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MemberRequestModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the member request persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByKey = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKey",
				new String[] {String.class.getName()}, new String[] {"key_"}, 0,
				1, false, convertNullFunction(MemberRequest::getKey)),
			_SQL_SELECT_MEMBERREQUEST_WHERE, "",
			new FinderColumn<>(
				"memberRequest.", "key", "key_", FinderColumn.Type.STRING, "=",
				true, true, MemberRequest::getKey));

		_collectionPersistenceFinderByReceiverUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByReceiverUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"receiverUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByReceiverUserId", new String[] {Long.class.getName()},
					new String[] {"receiverUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByReceiverUserId",
					new String[] {Long.class.getName()},
					new String[] {"receiverUserId"}, false),
				_SQL_SELECT_MEMBERREQUEST_WHERE, _SQL_COUNT_MEMBERREQUEST_WHERE,
				MemberRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"memberRequest.", "receiverUserId", FinderColumn.Type.LONG,
					"=", true, true, MemberRequest::getReceiverUserId));

		_collectionPersistenceFinderByR_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"receiverUserId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"receiverUserId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"receiverUserId", "status"}, false),
			_SQL_SELECT_MEMBERREQUEST_WHERE, _SQL_COUNT_MEMBERREQUEST_WHERE,
			MemberRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"memberRequest.", "receiverUserId", FinderColumn.Type.LONG, "=",
				true, true, MemberRequest::getReceiverUserId),
			new FinderColumn<>(
				"memberRequest.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, MemberRequest::getStatus));

		_uniquePersistenceFinderByG_R_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_R_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "receiverUserId", "status"}, 0, 0,
				false, MemberRequest::getGroupId,
				MemberRequest::getReceiverUserId, MemberRequest::getStatus),
			_SQL_SELECT_MEMBERREQUEST_WHERE, "",
			new FinderColumn<>(
				"memberRequest.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, MemberRequest::getGroupId),
			new FinderColumn<>(
				"memberRequest.", "receiverUserId", FinderColumn.Type.LONG, "=",
				true, true, MemberRequest::getReceiverUserId),
			new FinderColumn<>(
				"memberRequest.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, MemberRequest::getStatus));

		MemberRequestUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MemberRequestUtil.setPersistence(null);

		entityCache.removeCache(MemberRequestImpl.class.getName());
	}

	@Override
	@Reference(
		target = IMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = IMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = IMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		MemberRequestModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MEMBERREQUEST =
		"SELECT memberRequest FROM MemberRequest memberRequest";

	private static final String _SQL_SELECT_MEMBERREQUEST_WHERE =
		"SELECT memberRequest FROM MemberRequest memberRequest WHERE ";

	private static final String _SQL_COUNT_MEMBERREQUEST_WHERE =
		"SELECT COUNT(memberRequest) FROM MemberRequest memberRequest WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MemberRequest exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MemberRequestPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:664587651