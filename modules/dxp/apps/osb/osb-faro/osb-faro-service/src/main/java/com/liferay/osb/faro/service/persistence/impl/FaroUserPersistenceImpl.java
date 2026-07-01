/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroUserException;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.model.FaroUserTable;
import com.liferay.osb.faro.model.impl.FaroUserImpl;
import com.liferay.osb.faro.model.impl.FaroUserModelImpl;
import com.liferay.osb.faro.service.persistence.FaroUserPersistence;
import com.liferay.osb.faro.service.persistence.FaroUserUtil;
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
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the faro user service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroUserPersistence.class)
public class FaroUserPersistenceImpl
	extends BasePersistenceImpl<FaroUser, NoSuchFaroUserException>
	implements FaroUserPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroUserUtil</code> to access the faro user persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroUserImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<FaroUser, NoSuchFaroUserException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	@Override
	public List<FaroUser> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByGroupId_First(
			long groupId, OrderByComparator<FaroUser> orderByComparator)
		throws NoSuchFaroUserException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByGroupId_First(
		long groupId, OrderByComparator<FaroUser> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the faro users where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of faro users where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro users
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<FaroUser, NoSuchFaroUserException>
		_collectionPersistenceFinderByLiveUserId;

	/**
	 * Returns an ordered range of all the faro users where liveUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	@Override
	public List<FaroUser> findByLiveUserId(
		long liveUserId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByLiveUserId.find(
			finderCache, new Object[] {liveUserId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByLiveUserId_First(
			long liveUserId, OrderByComparator<FaroUser> orderByComparator)
		throws NoSuchFaroUserException {

		return _collectionPersistenceFinderByLiveUserId.findFirst(
			finderCache, new Object[] {liveUserId}, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByLiveUserId_First(
		long liveUserId, OrderByComparator<FaroUser> orderByComparator) {

		return _collectionPersistenceFinderByLiveUserId.fetchFirst(
			finderCache, new Object[] {liveUserId}, orderByComparator);
	}

	/**
	 * Removes all the faro users where liveUserId = &#63; from the database.
	 *
	 * @param liveUserId the live user ID
	 */
	@Override
	public void removeByLiveUserId(long liveUserId) {
		_collectionPersistenceFinderByLiveUserId.remove(
			finderCache, new Object[] {liveUserId});
	}

	/**
	 * Returns the number of faro users where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @return the number of matching faro users
	 */
	@Override
	public int countByLiveUserId(long liveUserId) {
		return _collectionPersistenceFinderByLiveUserId.count(
			finderCache, new Object[] {liveUserId});
	}

	private UniquePersistenceFinder<FaroUser, NoSuchFaroUserException>
		_uniquePersistenceFinderByKey;

	/**
	 * Returns the faro user where key = &#63; or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param key the key
	 * @return the matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByKey(String key) throws NoSuchFaroUserException {
		return _uniquePersistenceFinderByKey.find(
			finderCache, new Object[] {key});
	}

	/**
	 * Returns the faro user where key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByKey(String key, boolean useFinderCache) {
		return _uniquePersistenceFinderByKey.fetch(
			finderCache, new Object[] {key}, useFinderCache);
	}

	/**
	 * Removes the faro user where key = &#63; from the database.
	 *
	 * @param key the key
	 * @return the faro user that was removed
	 */
	@Override
	public FaroUser removeByKey(String key) throws NoSuchFaroUserException {
		FaroUser faroUser = findByKey(key);

		return remove(faroUser);
	}

	/**
	 * Returns the number of faro users where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching faro users
	 */
	@Override
	public int countByKey(String key) {
		return _uniquePersistenceFinderByKey.count(
			finderCache, new Object[] {key});
	}

	private UniquePersistenceFinder<FaroUser, NoSuchFaroUserException>
		_uniquePersistenceFinderByG_L;

	/**
	 * Returns the faro user where groupId = &#63; and liveUserId = &#63; or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByG_L(long groupId, long liveUserId)
		throws NoSuchFaroUserException {

		return _uniquePersistenceFinderByG_L.find(
			finderCache, new Object[] {groupId, liveUserId});
	}

	/**
	 * Returns the faro user where groupId = &#63; and liveUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByG_L(
		long groupId, long liveUserId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_L.fetch(
			finderCache, new Object[] {groupId, liveUserId}, useFinderCache);
	}

	/**
	 * Removes the faro user where groupId = &#63; and liveUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the faro user that was removed
	 */
	@Override
	public FaroUser removeByG_L(long groupId, long liveUserId)
		throws NoSuchFaroUserException {

		FaroUser faroUser = findByG_L(groupId, liveUserId);

		return remove(faroUser);
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and liveUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the number of matching faro users
	 */
	@Override
	public int countByG_L(long groupId, long liveUserId) {
		return _uniquePersistenceFinderByG_L.count(
			finderCache, new Object[] {groupId, liveUserId});
	}

	private CollectionPersistenceFinder<FaroUser, NoSuchFaroUserException>
		_collectionPersistenceFinderByG_R;

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	@Override
	public List<FaroUser> findByG_R(
		long groupId, long roleId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_R.find(
			finderCache, new Object[] {groupId, roleId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByG_R_First(
			long groupId, long roleId,
			OrderByComparator<FaroUser> orderByComparator)
		throws NoSuchFaroUserException {

		return _collectionPersistenceFinderByG_R.findFirst(
			finderCache, new Object[] {groupId, roleId}, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByG_R_First(
		long groupId, long roleId,
		OrderByComparator<FaroUser> orderByComparator) {

		return _collectionPersistenceFinderByG_R.fetchFirst(
			finderCache, new Object[] {groupId, roleId}, orderByComparator);
	}

	/**
	 * Removes all the faro users where groupId = &#63; and roleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 */
	@Override
	public void removeByG_R(long groupId, long roleId) {
		_collectionPersistenceFinderByG_R.remove(
			finderCache, new Object[] {groupId, roleId});
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the number of matching faro users
	 */
	@Override
	public int countByG_R(long groupId, long roleId) {
		return _collectionPersistenceFinderByG_R.count(
			finderCache, new Object[] {groupId, roleId});
	}

	private UniquePersistenceFinder<FaroUser, NoSuchFaroUserException>
		_uniquePersistenceFinderByG_E;

	/**
	 * Returns the faro user where groupId = &#63; and emailAddress = &#63; or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByG_E(long groupId, String emailAddress)
		throws NoSuchFaroUserException {

		return _uniquePersistenceFinderByG_E.find(
			finderCache, new Object[] {groupId, emailAddress});
	}

	/**
	 * Returns the faro user where groupId = &#63; and emailAddress = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByG_E(
		long groupId, String emailAddress, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_E.fetch(
			finderCache, new Object[] {groupId, emailAddress}, useFinderCache);
	}

	/**
	 * Removes the faro user where groupId = &#63; and emailAddress = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the faro user that was removed
	 */
	@Override
	public FaroUser removeByG_E(long groupId, String emailAddress)
		throws NoSuchFaroUserException {

		FaroUser faroUser = findByG_E(groupId, emailAddress);

		return remove(faroUser);
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and emailAddress = &#63;.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the number of matching faro users
	 */
	@Override
	public int countByG_E(long groupId, String emailAddress) {
		return _uniquePersistenceFinderByG_E.count(
			finderCache, new Object[] {groupId, emailAddress});
	}

	private CollectionPersistenceFinder<FaroUser, NoSuchFaroUserException>
		_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	@Override
	public List<FaroUser> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByG_S_First(
			long groupId, int status,
			OrderByComparator<FaroUser> orderByComparator)
		throws NoSuchFaroUserException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<FaroUser> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Removes all the faro users where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching faro users
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	private CollectionPersistenceFinder<FaroUser, NoSuchFaroUserException>
		_collectionPersistenceFinderByL_S;

	/**
	 * Returns an ordered range of all the faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	@Override
	public List<FaroUser> findByL_S(
		long liveUserId, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByL_S.find(
			finderCache, new Object[] {liveUserId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByL_S_First(
			long liveUserId, int status,
			OrderByComparator<FaroUser> orderByComparator)
		throws NoSuchFaroUserException {

		return _collectionPersistenceFinderByL_S.findFirst(
			finderCache, new Object[] {liveUserId, status}, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByL_S_First(
		long liveUserId, int status,
		OrderByComparator<FaroUser> orderByComparator) {

		return _collectionPersistenceFinderByL_S.fetchFirst(
			finderCache, new Object[] {liveUserId, status}, orderByComparator);
	}

	/**
	 * Removes all the faro users where liveUserId = &#63; and status = &#63; from the database.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 */
	@Override
	public void removeByL_S(long liveUserId, int status) {
		_collectionPersistenceFinderByL_S.remove(
			finderCache, new Object[] {liveUserId, status});
	}

	/**
	 * Returns the number of faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @return the number of matching faro users
	 */
	@Override
	public int countByL_S(long liveUserId, int status) {
		return _collectionPersistenceFinderByL_S.count(
			finderCache, new Object[] {liveUserId, status});
	}

	private CollectionPersistenceFinder<FaroUser, NoSuchFaroUserException>
		_collectionPersistenceFinderByE_S;

	/**
	 * Returns an ordered range of all the faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	@Override
	public List<FaroUser> findByE_S(
		String emailAddress, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByE_S.find(
			finderCache, new Object[] {emailAddress, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	@Override
	public FaroUser findByE_S_First(
			String emailAddress, int status,
			OrderByComparator<FaroUser> orderByComparator)
		throws NoSuchFaroUserException {

		return _collectionPersistenceFinderByE_S.findFirst(
			finderCache, new Object[] {emailAddress, status},
			orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	@Override
	public FaroUser fetchByE_S_First(
		String emailAddress, int status,
		OrderByComparator<FaroUser> orderByComparator) {

		return _collectionPersistenceFinderByE_S.fetchFirst(
			finderCache, new Object[] {emailAddress, status},
			orderByComparator);
	}

	/**
	 * Removes all the faro users where emailAddress = &#63; and status = &#63; from the database.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 */
	@Override
	public void removeByE_S(String emailAddress, int status) {
		_collectionPersistenceFinderByE_S.remove(
			finderCache, new Object[] {emailAddress, status});
	}

	/**
	 * Returns the number of faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @return the number of matching faro users
	 */
	@Override
	public int countByE_S(String emailAddress, int status) {
		return _collectionPersistenceFinderByE_S.count(
			finderCache, new Object[] {emailAddress, status});
	}

	public FaroUserPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FaroUser.class);

		setModelImplClass(FaroUserImpl.class);
		setModelPKClass(long.class);

		setTable(FaroUserTable.INSTANCE);
	}

	/**
	 * Creates a new faro user with the primary key. Does not add the faro user to the database.
	 *
	 * @param faroUserId the primary key for the new faro user
	 * @return the new faro user
	 */
	@Override
	public FaroUser create(long faroUserId) {
		FaroUser faroUser = new FaroUserImpl();

		faroUser.setNew(true);
		faroUser.setPrimaryKey(faroUserId);

		faroUser.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroUser;
	}

	/**
	 * Removes the faro user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroUserId the primary key of the faro user
	 * @return the faro user that was removed
	 * @throws NoSuchFaroUserException if a faro user with the primary key could not be found
	 */
	@Override
	public FaroUser remove(long faroUserId) throws NoSuchFaroUserException {
		return remove((Serializable)faroUserId);
	}

	@Override
	protected FaroUser removeImpl(FaroUser faroUser) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroUser)) {
				faroUser = (FaroUser)session.get(
					FaroUserImpl.class, faroUser.getPrimaryKeyObj());
			}

			if (faroUser != null) {
				session.delete(faroUser);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroUser != null) {
			clearCache(faroUser);
		}

		return faroUser;
	}

	@Override
	public FaroUser updateImpl(FaroUser faroUser) {
		boolean isNew = faroUser.isNew();

		if (!(faroUser instanceof FaroUserModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroUser.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(faroUser);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroUser proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroUser implementation " +
					faroUser.getClass());
		}

		FaroUserModelImpl faroUserModelImpl = (FaroUserModelImpl)faroUser;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroUser);
			}
			else {
				faroUser = (FaroUser)session.merge(faroUser);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(faroUser, false);

		if (isNew) {
			faroUser.setNew(false);
		}

		faroUser.resetOriginalValues();

		return faroUser;
	}

	/**
	 * Returns the faro user with the primary key or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param faroUserId the primary key of the faro user
	 * @return the faro user
	 * @throws NoSuchFaroUserException if a faro user with the primary key could not be found
	 */
	@Override
	public FaroUser findByPrimaryKey(long faroUserId)
		throws NoSuchFaroUserException {

		return findByPrimaryKey((Serializable)faroUserId);
	}

	/**
	 * Returns the faro user with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroUserId the primary key of the faro user
	 * @return the faro user, or <code>null</code> if a faro user with the primary key could not be found
	 */
	@Override
	public FaroUser fetchByPrimaryKey(long faroUserId) {
		return fetchByPrimaryKey((Serializable)faroUserId);
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
		return "faroUserId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FAROUSER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroUserModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro user persistence.
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
				_SQL_SELECT_FAROUSER_WHERE, _SQL_COUNT_FAROUSER_WHERE,
				FaroUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"faroUser.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, FaroUser::getGroupId));

		_collectionPersistenceFinderByLiveUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLiveUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"liveUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLiveUserId", new String[] {Long.class.getName()},
					new String[] {"liveUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLiveUserId", new String[] {Long.class.getName()},
					new String[] {"liveUserId"}, false),
				_SQL_SELECT_FAROUSER_WHERE, _SQL_COUNT_FAROUSER_WHERE,
				FaroUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"faroUser.", "liveUserId", FinderColumn.Type.LONG, "=",
					true, true, FaroUser::getLiveUserId));

		_uniquePersistenceFinderByKey = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKey",
				new String[] {String.class.getName()}, new String[] {"key_"}, 0,
				1, false, convertNullFunction(FaroUser::getKey)),
			_SQL_SELECT_FAROUSER_WHERE, "",
			new FinderColumn<>(
				"faroUser.", "key", "key_", FinderColumn.Type.STRING, "=", true,
				true, FaroUser::getKey));

		_uniquePersistenceFinderByG_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "liveUserId"}, 0, 0, false,
				FaroUser::getGroupId, FaroUser::getLiveUserId),
			_SQL_SELECT_FAROUSER_WHERE, "",
			new FinderColumn<>(
				"faroUser.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				FaroUser::getGroupId),
			new FinderColumn<>(
				"faroUser.", "liveUserId", FinderColumn.Type.LONG, "=", true,
				true, FaroUser::getLiveUserId));

		_collectionPersistenceFinderByG_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "roleId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "roleId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "roleId"}, false),
			_SQL_SELECT_FAROUSER_WHERE, _SQL_COUNT_FAROUSER_WHERE,
			FaroUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"faroUser.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				FaroUser::getGroupId),
			new FinderColumn<>(
				"faroUser.", "roleId", FinderColumn.Type.LONG, "=", true, true,
				FaroUser::getRoleId));

		_uniquePersistenceFinderByG_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_E",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "emailAddress"}, 0, 2, false,
				FaroUser::getGroupId,
				convertNullFunction(FaroUser::getEmailAddress)),
			_SQL_SELECT_FAROUSER_WHERE, "",
			new FinderColumn<>(
				"faroUser.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				FaroUser::getGroupId),
			new FinderColumn<>(
				"faroUser.", "emailAddress", FinderColumn.Type.STRING, "=",
				true, true, FaroUser::getEmailAddress));

		_collectionPersistenceFinderByG_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, false),
			_SQL_SELECT_FAROUSER_WHERE, _SQL_COUNT_FAROUSER_WHERE,
			FaroUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"faroUser.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				FaroUser::getGroupId),
			new FinderColumn<>(
				"faroUser.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, FaroUser::getStatus));

		_collectionPersistenceFinderByL_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"liveUserId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"liveUserId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"liveUserId", "status"}, false),
			_SQL_SELECT_FAROUSER_WHERE, _SQL_COUNT_FAROUSER_WHERE,
			FaroUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"faroUser.", "liveUserId", FinderColumn.Type.LONG, "=", true,
				true, FaroUser::getLiveUserId),
			new FinderColumn<>(
				"faroUser.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, FaroUser::getStatus));

		_collectionPersistenceFinderByE_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByE_S",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"emailAddress", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByE_S",
				new String[] {String.class.getName(), Integer.class.getName()},
				new String[] {"emailAddress", "status"}, 0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByE_S",
				new String[] {String.class.getName(), Integer.class.getName()},
				new String[] {"emailAddress", "status"}, 0, 1, false, null),
			_SQL_SELECT_FAROUSER_WHERE, _SQL_COUNT_FAROUSER_WHERE,
			FaroUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"faroUser.", "emailAddress", FinderColumn.Type.STRING, "=",
				true, true, FaroUser::getEmailAddress),
			new FinderColumn<>(
				"faroUser.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, FaroUser::getStatus));

		FaroUserUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroUserUtil.setPersistence(null);

		entityCache.removeCache(FaroUserImpl.class.getName());
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
		FaroUserModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FAROUSER =
		"SELECT faroUser FROM FaroUser faroUser";

	private static final String _SQL_SELECT_FAROUSER_WHERE =
		"SELECT faroUser FROM FaroUser faroUser WHERE ";

	private static final String _SQL_COUNT_FAROUSER_WHERE =
		"SELECT COUNT(faroUser) FROM FaroUser faroUser WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FaroUser exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FaroUserPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1435248490