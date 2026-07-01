/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchRememberMeTokenException;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.model.RememberMeTokenTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.RememberMeTokenPersistence;
import com.liferay.portal.kernel.service.persistence.RememberMeTokenUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.RememberMeTokenImpl;
import com.liferay.portal.model.impl.RememberMeTokenModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the remember me token service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RememberMeTokenPersistenceImpl
	extends BasePersistenceImpl<RememberMeToken, NoSuchRememberMeTokenException>
	implements RememberMeTokenPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RememberMeTokenUtil</code> to access the remember me token persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RememberMeTokenImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RememberMeToken, NoSuchRememberMeTokenException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the remember me tokens where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching remember me tokens
	 */
	@Override
	public List<RememberMeToken> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first remember me token in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token
	 * @throws NoSuchRememberMeTokenException if a matching remember me token could not be found
	 */
	@Override
	public RememberMeToken findByUserId_First(
			long userId, OrderByComparator<RememberMeToken> orderByComparator)
		throws NoSuchRememberMeTokenException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first remember me token in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token, or <code>null</code> if a matching remember me token could not be found
	 */
	@Override
	public RememberMeToken fetchByUserId_First(
		long userId, OrderByComparator<RememberMeToken> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the remember me tokens where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of remember me tokens where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching remember me tokens
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<RememberMeToken, NoSuchRememberMeTokenException>
			_collectionPersistenceFinderByLteExpirationDate;

	/**
	 * Returns all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching remember me tokens
	 */
	@Override
	public List<RememberMeToken> findByLteExpirationDate(Date expirationDate) {
		return findByLteExpirationDate(
			expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @return the range of matching remember me tokens
	 */
	@Override
	public List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate, int start, int end) {

		return findByLteExpirationDate(expirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching remember me tokens
	 */
	@Override
	public List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator) {

		return findByLteExpirationDate(
			expirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching remember me tokens
	 */
	@Override
	public List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLteExpirationDate.find(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first remember me token in the ordered set where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token
	 * @throws NoSuchRememberMeTokenException if a matching remember me token could not be found
	 */
	@Override
	public RememberMeToken findByLteExpirationDate_First(
			Date expirationDate,
			OrderByComparator<RememberMeToken> orderByComparator)
		throws NoSuchRememberMeTokenException {

		return _collectionPersistenceFinderByLteExpirationDate.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate},
			orderByComparator);
	}

	/**
	 * Returns the first remember me token in the ordered set where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token, or <code>null</code> if a matching remember me token could not be found
	 */
	@Override
	public RememberMeToken fetchByLteExpirationDate_First(
		Date expirationDate,
		OrderByComparator<RememberMeToken> orderByComparator) {

		return _collectionPersistenceFinderByLteExpirationDate.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate},
			orderByComparator);
	}

	/**
	 * Removes all the remember me tokens where expirationDate &le; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByLteExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByLteExpirationDate.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate});
	}

	/**
	 * Returns the number of remember me tokens where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching remember me tokens
	 */
	@Override
	public int countByLteExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByLteExpirationDate.count(
			FinderCacheUtil.getFinderCache(), new Object[] {expirationDate});
	}

	public RememberMeTokenPersistenceImpl() {
		setModelClass(RememberMeToken.class);

		setModelImplClass(RememberMeTokenImpl.class);
		setModelPKClass(long.class);

		setTable(RememberMeTokenTable.INSTANCE);
	}

	/**
	 * Creates a new remember me token with the primary key. Does not add the remember me token to the database.
	 *
	 * @param rememberMeTokenId the primary key for the new remember me token
	 * @return the new remember me token
	 */
	@Override
	public RememberMeToken create(long rememberMeTokenId) {
		RememberMeToken rememberMeToken = new RememberMeTokenImpl();

		rememberMeToken.setNew(true);
		rememberMeToken.setPrimaryKey(rememberMeTokenId);

		rememberMeToken.setCompanyId(CompanyThreadLocal.getCompanyId());

		return rememberMeToken;
	}

	/**
	 * Removes the remember me token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token that was removed
	 * @throws NoSuchRememberMeTokenException if a remember me token with the primary key could not be found
	 */
	@Override
	public RememberMeToken remove(long rememberMeTokenId)
		throws NoSuchRememberMeTokenException {

		return remove((Serializable)rememberMeTokenId);
	}

	@Override
	protected RememberMeToken removeImpl(RememberMeToken rememberMeToken) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(rememberMeToken)) {
				rememberMeToken = (RememberMeToken)session.get(
					RememberMeTokenImpl.class,
					rememberMeToken.getPrimaryKeyObj());
			}

			if (rememberMeToken != null) {
				session.delete(rememberMeToken);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (rememberMeToken != null) {
			clearCache(rememberMeToken);
		}

		return rememberMeToken;
	}

	@Override
	public RememberMeToken updateImpl(RememberMeToken rememberMeToken) {
		boolean isNew = rememberMeToken.isNew();

		if (!(rememberMeToken instanceof RememberMeTokenModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(rememberMeToken.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					rememberMeToken);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in rememberMeToken proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RememberMeToken implementation " +
					rememberMeToken.getClass());
		}

		RememberMeTokenModelImpl rememberMeTokenModelImpl =
			(RememberMeTokenModelImpl)rememberMeToken;

		if (isNew && (rememberMeToken.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				rememberMeToken.setCreateDate(date);
			}
			else {
				rememberMeToken.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(rememberMeToken);
			}
			else {
				rememberMeToken = (RememberMeToken)session.merge(
					rememberMeToken);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(rememberMeToken, false);

		if (isNew) {
			rememberMeToken.setNew(false);
		}

		rememberMeToken.resetOriginalValues();

		return rememberMeToken;
	}

	/**
	 * Returns the remember me token with the primary key or throws a <code>NoSuchRememberMeTokenException</code> if it could not be found.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token
	 * @throws NoSuchRememberMeTokenException if a remember me token with the primary key could not be found
	 */
	@Override
	public RememberMeToken findByPrimaryKey(long rememberMeTokenId)
		throws NoSuchRememberMeTokenException {

		return findByPrimaryKey((Serializable)rememberMeTokenId);
	}

	/**
	 * Returns the remember me token with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token, or <code>null</code> if a remember me token with the primary key could not be found
	 */
	@Override
	public RememberMeToken fetchByPrimaryKey(long rememberMeTokenId) {
		return fetchByPrimaryKey((Serializable)rememberMeTokenId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "rememberMeTokenId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REMEMBERMETOKEN;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RememberMeTokenModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the remember me token persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_REMEMBERMETOKEN_WHERE,
				_SQL_COUNT_REMEMBERMETOKEN_WHERE,
				RememberMeTokenModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"rememberMeToken.", "userId", FinderColumn.Type.LONG, "=",
					true, true, RememberMeToken::getUserId));

		_collectionPersistenceFinderByLteExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLteExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLteExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_REMEMBERMETOKEN_WHERE,
				_SQL_COUNT_REMEMBERMETOKEN_WHERE,
				RememberMeTokenModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"rememberMeToken.", "expirationDate",
					FinderColumn.Type.DATE, "<=", true, true,
					RememberMeToken::getExpirationDate));

		RememberMeTokenUtil.setPersistence(this);
	}

	public void destroy() {
		RememberMeTokenUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RememberMeTokenImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RememberMeTokenModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REMEMBERMETOKEN =
		"SELECT rememberMeToken FROM RememberMeToken rememberMeToken";

	private static final String _SQL_SELECT_REMEMBERMETOKEN_WHERE =
		"SELECT rememberMeToken FROM RememberMeToken rememberMeToken WHERE ";

	private static final String _SQL_COUNT_REMEMBERMETOKEN_WHERE =
		"SELECT COUNT(rememberMeToken) FROM RememberMeToken rememberMeToken WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1561254256