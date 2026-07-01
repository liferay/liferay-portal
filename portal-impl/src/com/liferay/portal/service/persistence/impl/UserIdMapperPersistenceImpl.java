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
import com.liferay.portal.kernel.exception.NoSuchUserIdMapperException;
import com.liferay.portal.kernel.model.UserIdMapper;
import com.liferay.portal.kernel.model.UserIdMapperTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserIdMapperPersistence;
import com.liferay.portal.kernel.service.persistence.UserIdMapperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.UserIdMapperImpl;
import com.liferay.portal.model.impl.UserIdMapperModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the user ID mapper service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserIdMapperPersistenceImpl
	extends BasePersistenceImpl<UserIdMapper, NoSuchUserIdMapperException>
	implements UserIdMapperPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserIdMapperUtil</code> to access the user ID mapper persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserIdMapperImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<UserIdMapper, NoSuchUserIdMapperException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the user ID mappers where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserIdMapperModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user ID mappers
	 * @param end the upper bound of the range of user ID mappers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user ID mappers
	 */
	@Override
	public List<UserIdMapper> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserIdMapper> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user ID mapper in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user ID mapper
	 * @throws NoSuchUserIdMapperException if a matching user ID mapper could not be found
	 */
	@Override
	public UserIdMapper findByUserId_First(
			long userId, OrderByComparator<UserIdMapper> orderByComparator)
		throws NoSuchUserIdMapperException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first user ID mapper in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user ID mapper, or <code>null</code> if a matching user ID mapper could not be found
	 */
	@Override
	public UserIdMapper fetchByUserId_First(
		long userId, OrderByComparator<UserIdMapper> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the user ID mappers where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of user ID mappers where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user ID mappers
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private UniquePersistenceFinder<UserIdMapper, NoSuchUserIdMapperException>
		_uniquePersistenceFinderByU_T;

	/**
	 * Returns the user ID mapper where userId = &#63; and type = &#63; or throws a <code>NoSuchUserIdMapperException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the matching user ID mapper
	 * @throws NoSuchUserIdMapperException if a matching user ID mapper could not be found
	 */
	@Override
	public UserIdMapper findByU_T(long userId, String type)
		throws NoSuchUserIdMapperException {

		return _uniquePersistenceFinderByU_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, type});
	}

	/**
	 * Returns the user ID mapper where userId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user ID mapper, or <code>null</code> if a matching user ID mapper could not be found
	 */
	@Override
	public UserIdMapper fetchByU_T(
		long userId, String type, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_T.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, type},
			useFinderCache);
	}

	/**
	 * Removes the user ID mapper where userId = &#63; and type = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the user ID mapper that was removed
	 */
	@Override
	public UserIdMapper removeByU_T(long userId, String type)
		throws NoSuchUserIdMapperException {

		UserIdMapper userIdMapper = findByU_T(userId, type);

		return remove(userIdMapper);
	}

	/**
	 * Returns the number of user ID mappers where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching user ID mappers
	 */
	@Override
	public int countByU_T(long userId, String type) {
		return _uniquePersistenceFinderByU_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, type});
	}

	private UniquePersistenceFinder<UserIdMapper, NoSuchUserIdMapperException>
		_uniquePersistenceFinderByT_E;

	/**
	 * Returns the user ID mapper where type = &#63; and externalUserId = &#63; or throws a <code>NoSuchUserIdMapperException</code> if it could not be found.
	 *
	 * @param type the type
	 * @param externalUserId the external user ID
	 * @return the matching user ID mapper
	 * @throws NoSuchUserIdMapperException if a matching user ID mapper could not be found
	 */
	@Override
	public UserIdMapper findByT_E(String type, String externalUserId)
		throws NoSuchUserIdMapperException {

		return _uniquePersistenceFinderByT_E.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {type, externalUserId});
	}

	/**
	 * Returns the user ID mapper where type = &#63; and externalUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param type the type
	 * @param externalUserId the external user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user ID mapper, or <code>null</code> if a matching user ID mapper could not be found
	 */
	@Override
	public UserIdMapper fetchByT_E(
		String type, String externalUserId, boolean useFinderCache) {

		return _uniquePersistenceFinderByT_E.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {type, externalUserId}, useFinderCache);
	}

	/**
	 * Removes the user ID mapper where type = &#63; and externalUserId = &#63; from the database.
	 *
	 * @param type the type
	 * @param externalUserId the external user ID
	 * @return the user ID mapper that was removed
	 */
	@Override
	public UserIdMapper removeByT_E(String type, String externalUserId)
		throws NoSuchUserIdMapperException {

		UserIdMapper userIdMapper = findByT_E(type, externalUserId);

		return remove(userIdMapper);
	}

	/**
	 * Returns the number of user ID mappers where type = &#63; and externalUserId = &#63;.
	 *
	 * @param type the type
	 * @param externalUserId the external user ID
	 * @return the number of matching user ID mappers
	 */
	@Override
	public int countByT_E(String type, String externalUserId) {
		return _uniquePersistenceFinderByT_E.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {type, externalUserId});
	}

	public UserIdMapperPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(UserIdMapper.class);

		setModelImplClass(UserIdMapperImpl.class);
		setModelPKClass(long.class);

		setTable(UserIdMapperTable.INSTANCE);
	}

	/**
	 * Creates a new user ID mapper with the primary key. Does not add the user ID mapper to the database.
	 *
	 * @param userIdMapperId the primary key for the new user ID mapper
	 * @return the new user ID mapper
	 */
	@Override
	public UserIdMapper create(long userIdMapperId) {
		UserIdMapper userIdMapper = new UserIdMapperImpl();

		userIdMapper.setNew(true);
		userIdMapper.setPrimaryKey(userIdMapperId);

		userIdMapper.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userIdMapper;
	}

	/**
	 * Removes the user ID mapper with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userIdMapperId the primary key of the user ID mapper
	 * @return the user ID mapper that was removed
	 * @throws NoSuchUserIdMapperException if a user ID mapper with the primary key could not be found
	 */
	@Override
	public UserIdMapper remove(long userIdMapperId)
		throws NoSuchUserIdMapperException {

		return remove((Serializable)userIdMapperId);
	}

	@Override
	protected UserIdMapper removeImpl(UserIdMapper userIdMapper) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userIdMapper)) {
				userIdMapper = (UserIdMapper)session.get(
					UserIdMapperImpl.class, userIdMapper.getPrimaryKeyObj());
			}

			if (userIdMapper != null) {
				session.delete(userIdMapper);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userIdMapper != null) {
			clearCache(userIdMapper);
		}

		return userIdMapper;
	}

	@Override
	public UserIdMapper updateImpl(UserIdMapper userIdMapper) {
		boolean isNew = userIdMapper.isNew();

		if (!(userIdMapper instanceof UserIdMapperModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userIdMapper.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					userIdMapper);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userIdMapper proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserIdMapper implementation " +
					userIdMapper.getClass());
		}

		UserIdMapperModelImpl userIdMapperModelImpl =
			(UserIdMapperModelImpl)userIdMapper;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(userIdMapper);
			}
			else {
				userIdMapper = (UserIdMapper)session.merge(userIdMapper);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userIdMapper, false);

		if (isNew) {
			userIdMapper.setNew(false);
		}

		userIdMapper.resetOriginalValues();

		return userIdMapper;
	}

	/**
	 * Returns the user ID mapper with the primary key or throws a <code>NoSuchUserIdMapperException</code> if it could not be found.
	 *
	 * @param userIdMapperId the primary key of the user ID mapper
	 * @return the user ID mapper
	 * @throws NoSuchUserIdMapperException if a user ID mapper with the primary key could not be found
	 */
	@Override
	public UserIdMapper findByPrimaryKey(long userIdMapperId)
		throws NoSuchUserIdMapperException {

		return findByPrimaryKey((Serializable)userIdMapperId);
	}

	/**
	 * Returns the user ID mapper with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userIdMapperId the primary key of the user ID mapper
	 * @return the user ID mapper, or <code>null</code> if a user ID mapper with the primary key could not be found
	 */
	@Override
	public UserIdMapper fetchByPrimaryKey(long userIdMapperId) {
		return fetchByPrimaryKey((Serializable)userIdMapperId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "userIdMapperId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERIDMAPPER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UserIdMapperModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the user ID mapper persistence.
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
				_SQL_SELECT_USERIDMAPPER_WHERE, _SQL_COUNT_USERIDMAPPER_WHERE,
				UserIdMapperModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"userIdMapper.", "userId", FinderColumn.Type.LONG, "=",
					true, true, UserIdMapper::getUserId));

		_uniquePersistenceFinderByU_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "type_"}, 0, 2, false,
				UserIdMapper::getUserId,
				convertNullFunction(UserIdMapper::getType)),
			_SQL_SELECT_USERIDMAPPER_WHERE, "",
			new FinderColumn<>(
				"userIdMapper.", "userId", FinderColumn.Type.LONG, "=", true,
				true, UserIdMapper::getUserId),
			new FinderColumn<>(
				"userIdMapper.", "type", "type_", FinderColumn.Type.STRING, "=",
				true, true, UserIdMapper::getType));

		_uniquePersistenceFinderByT_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByT_E",
				new String[] {String.class.getName(), String.class.getName()},
				new String[] {"type_", "externalUserId"}, 0, 3, false,
				convertNullFunction(UserIdMapper::getType),
				convertNullFunction(UserIdMapper::getExternalUserId)),
			_SQL_SELECT_USERIDMAPPER_WHERE, "",
			new FinderColumn<>(
				"userIdMapper.", "type", "type_", FinderColumn.Type.STRING, "=",
				true, true, UserIdMapper::getType),
			new FinderColumn<>(
				"userIdMapper.", "externalUserId", FinderColumn.Type.STRING,
				"=", true, true, UserIdMapper::getExternalUserId));

		UserIdMapperUtil.setPersistence(this);
	}

	public void destroy() {
		UserIdMapperUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserIdMapperImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserIdMapperModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERIDMAPPER =
		"SELECT userIdMapper FROM UserIdMapper userIdMapper";

	private static final String _SQL_SELECT_USERIDMAPPER_WHERE =
		"SELECT userIdMapper FROM UserIdMapper userIdMapper WHERE ";

	private static final String _SQL_COUNT_USERIDMAPPER_WHERE =
		"SELECT COUNT(userIdMapper) FROM UserIdMapper userIdMapper WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:919534693