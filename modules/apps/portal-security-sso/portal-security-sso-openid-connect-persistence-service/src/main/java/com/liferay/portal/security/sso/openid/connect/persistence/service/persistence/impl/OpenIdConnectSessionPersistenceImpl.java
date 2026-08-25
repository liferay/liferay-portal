/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchSessionException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSessionTable;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionModelImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectSessionPersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectSessionUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl.constants.OpenIdConnectPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the open ID connect session service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = OpenIdConnectSessionPersistence.class)
public class OpenIdConnectSessionPersistenceImpl
	extends BasePersistenceImpl<OpenIdConnectSession, NoSuchSessionException>
	implements OpenIdConnectSessionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OpenIdConnectSessionUtil</code> to access the open ID connect session persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OpenIdConnectSessionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<OpenIdConnectSession, NoSuchSessionException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByUserId_First(
			long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByUserId_First(
		long userId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<OpenIdConnectSession, NoSuchSessionException>
			_collectionPersistenceFinderByLtAccessTokenExpirationDate;

	/**
	 * Returns all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @return the matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		return findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end) {

		return findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtAccessTokenExpirationDate.find(
			finderCache, new Object[] {accessTokenExpirationDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByLtAccessTokenExpirationDate_First(
			Date accessTokenExpirationDate,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		return _collectionPersistenceFinderByLtAccessTokenExpirationDate.
			findFirst(
				finderCache, new Object[] {accessTokenExpirationDate},
				orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByLtAccessTokenExpirationDate_First(
		Date accessTokenExpirationDate,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return _collectionPersistenceFinderByLtAccessTokenExpirationDate.
			fetchFirst(
				finderCache, new Object[] {accessTokenExpirationDate},
				orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where accessTokenExpirationDate &lt; &#63; from the database.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 */
	@Override
	public void removeByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		_collectionPersistenceFinderByLtAccessTokenExpirationDate.remove(
			finderCache, new Object[] {accessTokenExpirationDate});
	}

	/**
	 * Returns the number of open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		return _collectionPersistenceFinderByLtAccessTokenExpirationDate.count(
			finderCache, new Object[] {accessTokenExpirationDate});
	}

	private UniquePersistenceFinder
		<OpenIdConnectSession, NoSuchSessionException>
			_uniquePersistenceFinderByU_I;

	/**
	 * Returns the open ID connect session where userId = &#63; and issuer = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByU_I(long userId, String issuer)
		throws NoSuchSessionException {

		return _uniquePersistenceFinderByU_I.find(
			finderCache, new Object[] {userId, issuer});
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and issuer = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByU_I(
		long userId, String issuer, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_I.fetch(
			finderCache, new Object[] {userId, issuer}, useFinderCache);
	}

	/**
	 * Removes the open ID connect session where userId = &#63; and issuer = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the open ID connect session that was removed
	 */
	@Override
	public OpenIdConnectSession removeByU_I(long userId, String issuer)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByU_I(userId, issuer);

		return remove(openIdConnectSession);
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63; and issuer = &#63;.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByU_I(long userId, String issuer) {
		return _uniquePersistenceFinderByU_I.count(
			finderCache, new Object[] {userId, issuer});
	}

	private CollectionPersistenceFinder
		<OpenIdConnectSession, NoSuchSessionException>
			_collectionPersistenceFinderByC_A_C;

	/**
	 * Returns an ordered range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_C.find(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByC_A_C_First(
			long companyId, String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		return _collectionPersistenceFinderByC_A_C.findFirst(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId},
			orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByC_A_C_First(
		long companyId, String authServerWellKnownURI, String clientId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return _collectionPersistenceFinderByC_A_C.fetchFirst(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId},
			orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 */
	@Override
	public void removeByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		_collectionPersistenceFinderByC_A_C.remove(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId});
	}

	/**
	 * Returns the number of open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		return _collectionPersistenceFinderByC_A_C.count(
			finderCache,
			new Object[] {companyId, authServerWellKnownURI, clientId});
	}

	private CollectionPersistenceFinder
		<OpenIdConnectSession, NoSuchSessionException>
			_collectionPersistenceFinderByC_I_S;

	/**
	 * Returns an ordered range of all the open ID connect sessions where companyId = &#63; and issuer = &#63; and sessionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByC_I_S(
		long companyId, String issuer, String sessionId, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_I_S.find(
			finderCache, new Object[] {companyId, issuer, sessionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and issuer = &#63; and sessionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByC_I_S_First(
			long companyId, String issuer, String sessionId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		return _collectionPersistenceFinderByC_I_S.findFirst(
			finderCache, new Object[] {companyId, issuer, sessionId},
			orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and issuer = &#63; and sessionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByC_I_S_First(
		long companyId, String issuer, String sessionId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return _collectionPersistenceFinderByC_I_S.fetchFirst(
			finderCache, new Object[] {companyId, issuer, sessionId},
			orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where companyId = &#63; and issuer = &#63; and sessionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 */
	@Override
	public void removeByC_I_S(long companyId, String issuer, String sessionId) {
		_collectionPersistenceFinderByC_I_S.remove(
			finderCache, new Object[] {companyId, issuer, sessionId});
	}

	/**
	 * Returns the number of open ID connect sessions where companyId = &#63; and issuer = &#63; and sessionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByC_I_S(long companyId, String issuer, String sessionId) {
		return _collectionPersistenceFinderByC_I_S.count(
			finderCache, new Object[] {companyId, issuer, sessionId});
	}

	private UniquePersistenceFinder
		<OpenIdConnectSession, NoSuchSessionException>
			_uniquePersistenceFinderByU_A_C;

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByU_A_C(
			long userId, String authServerWellKnownURI, String clientId)
		throws NoSuchSessionException {

		return _uniquePersistenceFinderByU_A_C.find(
			finderCache,
			new Object[] {userId, authServerWellKnownURI, clientId});
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByU_A_C(
		long userId, String authServerWellKnownURI, String clientId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByU_A_C.fetch(
			finderCache,
			new Object[] {userId, authServerWellKnownURI, clientId},
			useFinderCache);
	}

	/**
	 * Removes the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the open ID connect session that was removed
	 */
	@Override
	public OpenIdConnectSession removeByU_A_C(
			long userId, String authServerWellKnownURI, String clientId)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByU_A_C(
			userId, authServerWellKnownURI, clientId);

		return remove(openIdConnectSession);
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByU_A_C(
		long userId, String authServerWellKnownURI, String clientId) {

		return _uniquePersistenceFinderByU_A_C.count(
			finderCache,
			new Object[] {userId, authServerWellKnownURI, clientId});
	}

	public OpenIdConnectSessionPersistenceImpl() {
		setModelClass(OpenIdConnectSession.class);

		setModelImplClass(OpenIdConnectSessionImpl.class);
		setModelPKClass(long.class);

		setTable(OpenIdConnectSessionTable.INSTANCE);
	}

	/**
	 * Creates a new open ID connect session with the primary key. Does not add the open ID connect session to the database.
	 *
	 * @param openIdConnectSessionId the primary key for the new open ID connect session
	 * @return the new open ID connect session
	 */
	@Override
	public OpenIdConnectSession create(long openIdConnectSessionId) {
		OpenIdConnectSession openIdConnectSession =
			new OpenIdConnectSessionImpl();

		openIdConnectSession.setNew(true);
		openIdConnectSession.setPrimaryKey(openIdConnectSessionId);

		openIdConnectSession.setCompanyId(CompanyThreadLocal.getCompanyId());

		return openIdConnectSession;
	}

	/**
	 * Removes the open ID connect session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session that was removed
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession remove(long openIdConnectSessionId)
		throws NoSuchSessionException {

		return remove((Serializable)openIdConnectSessionId);
	}

	@Override
	protected OpenIdConnectSession removeImpl(
		OpenIdConnectSession openIdConnectSession) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(openIdConnectSession)) {
				openIdConnectSession = (OpenIdConnectSession)session.get(
					OpenIdConnectSessionImpl.class,
					openIdConnectSession.getPrimaryKeyObj());
			}

			if (openIdConnectSession != null) {
				session.delete(openIdConnectSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (openIdConnectSession != null) {
			clearCache(openIdConnectSession);
		}

		return openIdConnectSession;
	}

	@Override
	public OpenIdConnectSession updateImpl(
		OpenIdConnectSession openIdConnectSession) {

		boolean isNew = openIdConnectSession.isNew();

		if (!(openIdConnectSession instanceof OpenIdConnectSessionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(openIdConnectSession.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					openIdConnectSession);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in openIdConnectSession proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OpenIdConnectSession implementation " +
					openIdConnectSession.getClass());
		}

		OpenIdConnectSessionModelImpl openIdConnectSessionModelImpl =
			(OpenIdConnectSessionModelImpl)openIdConnectSession;

		if (!openIdConnectSessionModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				openIdConnectSession.setModifiedDate(date);
			}
			else {
				openIdConnectSession.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(openIdConnectSession);
			}
			else {
				openIdConnectSession = (OpenIdConnectSession)session.merge(
					openIdConnectSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(openIdConnectSession, false);

		if (isNew) {
			openIdConnectSession.setNew(false);
		}

		openIdConnectSession.resetOriginalValues();

		return openIdConnectSession;
	}

	/**
	 * Returns the open ID connect session with the primary key or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession findByPrimaryKey(long openIdConnectSessionId)
		throws NoSuchSessionException {

		return findByPrimaryKey((Serializable)openIdConnectSessionId);
	}

	/**
	 * Returns the open ID connect session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session, or <code>null</code> if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByPrimaryKey(long openIdConnectSessionId) {
		return fetchByPrimaryKey((Serializable)openIdConnectSessionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "openIdConnectSessionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OPENIDCONNECTSESSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OpenIdConnectSessionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the open ID connect session persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_OPENIDCONNECTSESSION_WHERE,
				_SQL_COUNT_OPENIDCONNECTSESSION_WHERE,
				OpenIdConnectSessionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"openIdConnectSession.", "userId", FinderColumn.Type.LONG,
					"=", true, true, OpenIdConnectSession::getUserId));

		_collectionPersistenceFinderByLtAccessTokenExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtAccessTokenExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"accessTokenExpirationDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtAccessTokenExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"accessTokenExpirationDate"}, false),
				_SQL_SELECT_OPENIDCONNECTSESSION_WHERE,
				_SQL_COUNT_OPENIDCONNECTSESSION_WHERE,
				OpenIdConnectSessionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"openIdConnectSession.", "accessTokenExpirationDate",
					FinderColumn.Type.DATE, "<", true, true,
					OpenIdConnectSession::getAccessTokenExpirationDate));

		_uniquePersistenceFinderByU_I = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_I",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "issuer"}, 0, 2, false,
				OpenIdConnectSession::getUserId,
				convertNullFunction(OpenIdConnectSession::getIssuer)),
			_SQL_SELECT_OPENIDCONNECTSESSION_WHERE, "",
			new FinderColumn<>(
				"openIdConnectSession.", "userId", FinderColumn.Type.LONG, "=",
				true, true, OpenIdConnectSession::getUserId),
			new FinderColumn<>(
				"openIdConnectSession.", "issuer", FinderColumn.Type.STRING,
				"=", true, true, OpenIdConnectSession::getIssuer));

		_collectionPersistenceFinderByC_A_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"companyId", "authServerWellKnownURI", "clientId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"companyId", "authServerWellKnownURI", "clientId"
				},
				0, 6, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"companyId", "authServerWellKnownURI", "clientId"
				},
				0, 6, false, null),
			_SQL_SELECT_OPENIDCONNECTSESSION_WHERE,
			_SQL_COUNT_OPENIDCONNECTSESSION_WHERE,
			OpenIdConnectSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"openIdConnectSession.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, OpenIdConnectSession::getCompanyId),
			new FinderColumn<>(
				"openIdConnectSession.", "authServerWellKnownURI",
				FinderColumn.Type.STRING, "=", true, true,
				OpenIdConnectSession::getAuthServerWellKnownURI),
			new FinderColumn<>(
				"openIdConnectSession.", "clientId", FinderColumn.Type.STRING,
				"=", true, true, OpenIdConnectSession::getClientId));

		_collectionPersistenceFinderByC_I_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_I_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "issuer", "sessionId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_I_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "issuer", "sessionId"}, 0, 6, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_I_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "issuer", "sessionId"}, 0, 6, false,
				null),
			_SQL_SELECT_OPENIDCONNECTSESSION_WHERE,
			_SQL_COUNT_OPENIDCONNECTSESSION_WHERE,
			OpenIdConnectSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"openIdConnectSession.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, OpenIdConnectSession::getCompanyId),
			new FinderColumn<>(
				"openIdConnectSession.", "issuer", FinderColumn.Type.STRING,
				"=", true, true, OpenIdConnectSession::getIssuer),
			new FinderColumn<>(
				"openIdConnectSession.", "sessionId", FinderColumn.Type.STRING,
				"=", true, true, OpenIdConnectSession::getSessionId));

		_uniquePersistenceFinderByU_A_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_A_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"userId", "authServerWellKnownURI", "clientId"},
				0, 6, false, OpenIdConnectSession::getUserId,
				convertNullFunction(
					OpenIdConnectSession::getAuthServerWellKnownURI),
				convertNullFunction(OpenIdConnectSession::getClientId)),
			_SQL_SELECT_OPENIDCONNECTSESSION_WHERE, "",
			new FinderColumn<>(
				"openIdConnectSession.", "userId", FinderColumn.Type.LONG, "=",
				true, true, OpenIdConnectSession::getUserId),
			new FinderColumn<>(
				"openIdConnectSession.", "authServerWellKnownURI",
				FinderColumn.Type.STRING, "=", true, true,
				OpenIdConnectSession::getAuthServerWellKnownURI),
			new FinderColumn<>(
				"openIdConnectSession.", "clientId", FinderColumn.Type.STRING,
				"=", true, true, OpenIdConnectSession::getClientId));

		OpenIdConnectSessionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OpenIdConnectSessionUtil.setPersistence(null);

		entityCache.removeCache(OpenIdConnectSessionImpl.class.getName());
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		OpenIdConnectSessionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OPENIDCONNECTSESSION =
		"SELECT openIdConnectSession FROM OpenIdConnectSession openIdConnectSession";

	private static final String _SQL_SELECT_OPENIDCONNECTSESSION_WHERE =
		"SELECT openIdConnectSession FROM OpenIdConnectSession openIdConnectSession WHERE ";

	private static final String _SQL_COUNT_OPENIDCONNECTSESSION_WHERE =
		"SELECT COUNT(openIdConnectSession) FROM OpenIdConnectSession openIdConnectSession WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1432312722