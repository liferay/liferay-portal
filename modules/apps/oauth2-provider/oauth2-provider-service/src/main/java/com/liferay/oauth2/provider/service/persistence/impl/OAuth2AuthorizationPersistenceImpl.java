/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.persistence.impl;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2AuthorizationException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2AuthorizationTable;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationImpl;
import com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl;
import com.liferay.oauth2.provider.service.persistence.OAuth2AuthorizationPersistence;
import com.liferay.oauth2.provider.service.persistence.OAuth2AuthorizationUtil;
import com.liferay.oauth2.provider.service.persistence.impl.constants.OAuthTwoPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
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
 * The persistence implementation for the o auth2 authorization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuth2AuthorizationPersistence.class)
public class OAuth2AuthorizationPersistenceImpl
	extends BasePersistenceImpl
		<OAuth2Authorization, NoSuchOAuth2AuthorizationException>
	implements OAuth2AuthorizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuth2AuthorizationUtil</code> to access the o auth2 authorization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuth2AuthorizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<OAuth2Authorization, NoSuchOAuth2AuthorizationException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	@Override
	public List<OAuth2Authorization> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization findByUserId_First(
			long userId,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization fetchByUserId_First(
		long userId, OrderByComparator<OAuth2Authorization> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the o auth2 authorizations where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of o auth2 authorizations where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth2 authorizations
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<OAuth2Authorization, NoSuchOAuth2AuthorizationException>
			_collectionPersistenceFinderByOAuth2ApplicationId;

	/**
	 * Returns an ordered range of all the o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	@Override
	public List<OAuth2Authorization> findByOAuth2ApplicationId(
		long oAuth2ApplicationId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOAuth2ApplicationId.find(
			finderCache, new Object[] {oAuth2ApplicationId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization findByOAuth2ApplicationId_First(
			long oAuth2ApplicationId,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException {

		return _collectionPersistenceFinderByOAuth2ApplicationId.findFirst(
			finderCache, new Object[] {oAuth2ApplicationId}, orderByComparator);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization fetchByOAuth2ApplicationId_First(
		long oAuth2ApplicationId,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return _collectionPersistenceFinderByOAuth2ApplicationId.fetchFirst(
			finderCache, new Object[] {oAuth2ApplicationId}, orderByComparator);
	}

	/**
	 * Removes all the o auth2 authorizations where oAuth2ApplicationId = &#63; from the database.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 */
	@Override
	public void removeByOAuth2ApplicationId(long oAuth2ApplicationId) {
		_collectionPersistenceFinderByOAuth2ApplicationId.remove(
			finderCache, new Object[] {oAuth2ApplicationId});
	}

	/**
	 * Returns the number of o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @return the number of matching o auth2 authorizations
	 */
	@Override
	public int countByOAuth2ApplicationId(long oAuth2ApplicationId) {
		return _collectionPersistenceFinderByOAuth2ApplicationId.count(
			finderCache, new Object[] {oAuth2ApplicationId});
	}

	private CollectionPersistenceFinder
		<OAuth2Authorization, NoSuchOAuth2AuthorizationException>
			_collectionPersistenceFinderByC_ATCH;

	/**
	 * Returns an ordered range of all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	@Override
	public List<OAuth2Authorization> findByC_ATCH(
		long companyId, long accessTokenContentHash, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_ATCH.find(
			finderCache, new Object[] {companyId, accessTokenContentHash},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization findByC_ATCH_First(
			long companyId, long accessTokenContentHash,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException {

		return _collectionPersistenceFinderByC_ATCH.findFirst(
			finderCache, new Object[] {companyId, accessTokenContentHash},
			orderByComparator);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization fetchByC_ATCH_First(
		long companyId, long accessTokenContentHash,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return _collectionPersistenceFinderByC_ATCH.fetchFirst(
			finderCache, new Object[] {companyId, accessTokenContentHash},
			orderByComparator);
	}

	/**
	 * Removes all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 */
	@Override
	public void removeByC_ATCH(long companyId, long accessTokenContentHash) {
		_collectionPersistenceFinderByC_ATCH.remove(
			finderCache, new Object[] {companyId, accessTokenContentHash});
	}

	/**
	 * Returns the number of o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @return the number of matching o auth2 authorizations
	 */
	@Override
	public int countByC_ATCH(long companyId, long accessTokenContentHash) {
		return _collectionPersistenceFinderByC_ATCH.count(
			finderCache, new Object[] {companyId, accessTokenContentHash});
	}

	private CollectionPersistenceFinder
		<OAuth2Authorization, NoSuchOAuth2AuthorizationException>
			_collectionPersistenceFinderByC_RTCH;

	/**
	 * Returns an ordered range of all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	@Override
	public List<OAuth2Authorization> findByC_RTCH(
		long companyId, long refreshTokenContentHash, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_RTCH.find(
			finderCache, new Object[] {companyId, refreshTokenContentHash},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization findByC_RTCH_First(
			long companyId, long refreshTokenContentHash,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException {

		return _collectionPersistenceFinderByC_RTCH.findFirst(
			finderCache, new Object[] {companyId, refreshTokenContentHash},
			orderByComparator);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization fetchByC_RTCH_First(
		long companyId, long refreshTokenContentHash,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return _collectionPersistenceFinderByC_RTCH.fetchFirst(
			finderCache, new Object[] {companyId, refreshTokenContentHash},
			orderByComparator);
	}

	/**
	 * Removes all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 */
	@Override
	public void removeByC_RTCH(long companyId, long refreshTokenContentHash) {
		_collectionPersistenceFinderByC_RTCH.remove(
			finderCache, new Object[] {companyId, refreshTokenContentHash});
	}

	/**
	 * Returns the number of o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @return the number of matching o auth2 authorizations
	 */
	@Override
	public int countByC_RTCH(long companyId, long refreshTokenContentHash) {
		return _collectionPersistenceFinderByC_RTCH.count(
			finderCache, new Object[] {companyId, refreshTokenContentHash});
	}

	private CollectionPersistenceFinder
		<OAuth2Authorization, NoSuchOAuth2AuthorizationException>
			_collectionPersistenceFinderByU_O_R;

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	@Override
	public List<OAuth2Authorization> findByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent,
		int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_O_R.find(
			finderCache,
			new Object[] {userId, oAuth2ApplicationId, rememberDeviceContent},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization findByU_O_R_First(
			long userId, long oAuth2ApplicationId, String rememberDeviceContent,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException {

		return _collectionPersistenceFinderByU_O_R.findFirst(
			finderCache,
			new Object[] {userId, oAuth2ApplicationId, rememberDeviceContent},
			orderByComparator);
	}

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	@Override
	public OAuth2Authorization fetchByU_O_R_First(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return _collectionPersistenceFinderByU_O_R.fetchFirst(
			finderCache,
			new Object[] {userId, oAuth2ApplicationId, rememberDeviceContent},
			orderByComparator);
	}

	/**
	 * Removes all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 */
	@Override
	public void removeByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent) {

		_collectionPersistenceFinderByU_O_R.remove(
			finderCache,
			new Object[] {userId, oAuth2ApplicationId, rememberDeviceContent});
	}

	/**
	 * Returns the number of o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @return the number of matching o auth2 authorizations
	 */
	@Override
	public int countByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent) {

		return _collectionPersistenceFinderByU_O_R.count(
			finderCache,
			new Object[] {userId, oAuth2ApplicationId, rememberDeviceContent});
	}

	public OAuth2AuthorizationPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"oAuth2ApplicationScopeAliasesId", "oA2AScopeAliasesId");

		setDBColumnNames(dbColumnNames);

		setModelClass(OAuth2Authorization.class);

		setModelImplClass(OAuth2AuthorizationImpl.class);
		setModelPKClass(long.class);

		setTable(OAuth2AuthorizationTable.INSTANCE);
	}

	/**
	 * Creates a new o auth2 authorization with the primary key. Does not add the o auth2 authorization to the database.
	 *
	 * @param oAuth2AuthorizationId the primary key for the new o auth2 authorization
	 * @return the new o auth2 authorization
	 */
	@Override
	public OAuth2Authorization create(long oAuth2AuthorizationId) {
		OAuth2Authorization oAuth2Authorization = new OAuth2AuthorizationImpl();

		oAuth2Authorization.setNew(true);
		oAuth2Authorization.setPrimaryKey(oAuth2AuthorizationId);

		oAuth2Authorization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return oAuth2Authorization;
	}

	/**
	 * Removes the o auth2 authorization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization that was removed
	 * @throws NoSuchOAuth2AuthorizationException if a o auth2 authorization with the primary key could not be found
	 */
	@Override
	public OAuth2Authorization remove(long oAuth2AuthorizationId)
		throws NoSuchOAuth2AuthorizationException {

		return remove((Serializable)oAuth2AuthorizationId);
	}

	@Override
	protected OAuth2Authorization removeImpl(
		OAuth2Authorization oAuth2Authorization) {

		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
			deleteLeftPrimaryKeyTableMappings(
				oAuth2Authorization.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuth2Authorization)) {
				oAuth2Authorization = (OAuth2Authorization)session.get(
					OAuth2AuthorizationImpl.class,
					oAuth2Authorization.getPrimaryKeyObj());
			}

			if (oAuth2Authorization != null) {
				session.delete(oAuth2Authorization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuth2Authorization != null) {
			clearCache(oAuth2Authorization);
		}

		return oAuth2Authorization;
	}

	@Override
	public OAuth2Authorization updateImpl(
		OAuth2Authorization oAuth2Authorization) {

		boolean isNew = oAuth2Authorization.isNew();

		if (!(oAuth2Authorization instanceof OAuth2AuthorizationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuth2Authorization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuth2Authorization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuth2Authorization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuth2Authorization implementation " +
					oAuth2Authorization.getClass());
		}

		OAuth2AuthorizationModelImpl oAuth2AuthorizationModelImpl =
			(OAuth2AuthorizationModelImpl)oAuth2Authorization;

		if (isNew && (oAuth2Authorization.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				oAuth2Authorization.setCreateDate(date);
			}
			else {
				oAuth2Authorization.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuth2Authorization);
			}
			else {
				oAuth2Authorization = (OAuth2Authorization)session.merge(
					oAuth2Authorization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(oAuth2Authorization, false);

		if (isNew) {
			oAuth2Authorization.setNew(false);
		}

		oAuth2Authorization.resetOriginalValues();

		return oAuth2Authorization;
	}

	/**
	 * Returns the o auth2 authorization with the primary key or throws a <code>NoSuchOAuth2AuthorizationException</code> if it could not be found.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a o auth2 authorization with the primary key could not be found
	 */
	@Override
	public OAuth2Authorization findByPrimaryKey(long oAuth2AuthorizationId)
		throws NoSuchOAuth2AuthorizationException {

		return findByPrimaryKey((Serializable)oAuth2AuthorizationId);
	}

	/**
	 * Returns the o auth2 authorization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization, or <code>null</code> if a o auth2 authorization with the primary key could not be found
	 */
	@Override
	public OAuth2Authorization fetchByPrimaryKey(long oAuth2AuthorizationId) {
		return fetchByPrimaryKey((Serializable)oAuth2AuthorizationId);
	}

	/**
	 * Returns the primaryKeys of o auth2 scope grants associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @return long[] of the primaryKeys of o auth2 scope grants associated with the o auth2 authorization
	 */
	@Override
	public long[] getOAuth2ScopeGrantPrimaryKeys(long pk) {
		long[] pks =
			oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the o auth2 authorization associated with the o auth2 scope grant.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @return the o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Override
	public List<OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long pk) {

		return getOAuth2ScopeGrantOAuth2Authorizations(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the o auth2 authorization associated with the o auth2 scope grant.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param start the lower bound of the range of o auth2 scope grants
	 * @param end the upper bound of the range of o auth2 scope grants (not inclusive)
	 * @return the range of o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Override
	public List<OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long pk, int start, int end) {

		return getOAuth2ScopeGrantOAuth2Authorizations(pk, start, end, null);
	}

	/**
	 * Returns all the o auth2 authorization associated with the o auth2 scope grant.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param start the lower bound of the range of o auth2 scope grants
	 * @param end the upper bound of the range of o auth2 scope grants (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Override
	public List<OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long pk, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
			getLeftBaseModels(pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of o auth2 scope grants associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @return the number of o auth2 scope grants associated with the o auth2 authorization
	 */
	@Override
	public int getOAuth2ScopeGrantsSize(long pk) {
		long[] pks =
			oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the o auth2 scope grant is associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPK the primary key of the o auth2 scope grant
	 * @return <code>true</code> if the o auth2 scope grant is associated with the o auth2 authorization; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOAuth2ScopeGrant(long pk, long oAuth2ScopeGrantPK) {
		return oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
			containsTableMapping(pk, oAuth2ScopeGrantPK);
	}

	/**
	 * Returns <code>true</code> if the o auth2 authorization has any o auth2 scope grants associated with it.
	 *
	 * @param pk the primary key of the o auth2 authorization to check for associations with o auth2 scope grants
	 * @return <code>true</code> if the o auth2 authorization has any o auth2 scope grants associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOAuth2ScopeGrants(long pk) {
		if (getOAuth2ScopeGrantsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPK the primary key of the o auth2 scope grant
	 * @return <code>true</code> if an association between the o auth2 authorization and the o auth2 scope grant was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOAuth2ScopeGrant(long pk, long oAuth2ScopeGrantPK) {
		OAuth2Authorization oAuth2Authorization = fetchByPrimaryKey(pk);

		if (oAuth2Authorization == null) {
			return oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				addTableMapping(
					CompanyThreadLocal.getCompanyId(), pk, oAuth2ScopeGrantPK);
		}
		else {
			return oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				addTableMapping(
					oAuth2Authorization.getCompanyId(), pk, oAuth2ScopeGrantPK);
		}
	}

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 * @return <code>true</code> if an association between the o auth2 authorization and the o auth2 scope grant was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOAuth2ScopeGrant(
		long pk, OAuth2ScopeGrant oAuth2ScopeGrant) {

		OAuth2Authorization oAuth2Authorization = fetchByPrimaryKey(pk);

		if (oAuth2Authorization == null) {
			return oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				addTableMapping(
					CompanyThreadLocal.getCompanyId(), pk,
					oAuth2ScopeGrant.getPrimaryKey());
		}
		else {
			return oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				addTableMapping(
					oAuth2Authorization.getCompanyId(), pk,
					oAuth2ScopeGrant.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPKs the primary keys of the o auth2 scope grants
	 * @return <code>true</code> if at least one association between the o auth2 authorization and the o auth2 scope grants was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOAuth2ScopeGrants(long pk, long[] oAuth2ScopeGrantPKs) {
		long companyId = 0;

		OAuth2Authorization oAuth2Authorization = fetchByPrimaryKey(pk);

		if (oAuth2Authorization == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = oAuth2Authorization.getCompanyId();
		}

		long[] addedKeys =
			oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.addTableMappings(
				companyId, pk, oAuth2ScopeGrantPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrants the o auth2 scope grants
	 * @return <code>true</code> if at least one association between the o auth2 authorization and the o auth2 scope grants was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOAuth2ScopeGrants(
		long pk, List<OAuth2ScopeGrant> oAuth2ScopeGrants) {

		return addOAuth2ScopeGrants(
			pk,
			ListUtil.toLongArray(
				oAuth2ScopeGrants,
				OAuth2ScopeGrant.O_AUTH2_SCOPE_GRANT_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the o auth2 authorization and its o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization to clear the associated o auth2 scope grants from
	 */
	@Override
	public void clearOAuth2ScopeGrants(long pk) {
		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPK the primary key of the o auth2 scope grant
	 */
	@Override
	public void removeOAuth2ScopeGrant(long pk, long oAuth2ScopeGrantPK) {
		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.deleteTableMapping(
			pk, oAuth2ScopeGrantPK);
	}

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 */
	@Override
	public void removeOAuth2ScopeGrant(
		long pk, OAuth2ScopeGrant oAuth2ScopeGrant) {

		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.deleteTableMapping(
			pk, oAuth2ScopeGrant.getPrimaryKey());
	}

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPKs the primary keys of the o auth2 scope grants
	 */
	@Override
	public void removeOAuth2ScopeGrants(long pk, long[] oAuth2ScopeGrantPKs) {
		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.deleteTableMappings(
			pk, oAuth2ScopeGrantPKs);
	}

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrants the o auth2 scope grants
	 */
	@Override
	public void removeOAuth2ScopeGrants(
		long pk, List<OAuth2ScopeGrant> oAuth2ScopeGrants) {

		removeOAuth2ScopeGrants(
			pk,
			ListUtil.toLongArray(
				oAuth2ScopeGrants,
				OAuth2ScopeGrant.O_AUTH2_SCOPE_GRANT_ID_ACCESSOR));
	}

	/**
	 * Sets the o auth2 scope grants associated with the o auth2 authorization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPKs the primary keys of the o auth2 scope grants to be associated with the o auth2 authorization
	 */
	@Override
	public void setOAuth2ScopeGrants(long pk, long[] oAuth2ScopeGrantPKs) {
		Set<Long> newOAuth2ScopeGrantPKsSet = SetUtil.fromArray(
			oAuth2ScopeGrantPKs);
		Set<Long> oldOAuth2ScopeGrantPKsSet = SetUtil.fromArray(
			oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.
				getRightPrimaryKeys(pk));

		Set<Long> removeOAuth2ScopeGrantPKsSet = new HashSet<Long>(
			oldOAuth2ScopeGrantPKsSet);

		removeOAuth2ScopeGrantPKsSet.removeAll(newOAuth2ScopeGrantPKsSet);

		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeOAuth2ScopeGrantPKsSet));

		newOAuth2ScopeGrantPKsSet.removeAll(oldOAuth2ScopeGrantPKsSet);

		long companyId = 0;

		OAuth2Authorization oAuth2Authorization = fetchByPrimaryKey(pk);

		if (oAuth2Authorization == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = oAuth2Authorization.getCompanyId();
		}

		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newOAuth2ScopeGrantPKsSet));
	}

	/**
	 * Sets the o auth2 scope grants associated with the o auth2 authorization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrants the o auth2 scope grants to be associated with the o auth2 authorization
	 */
	@Override
	public void setOAuth2ScopeGrants(
		long pk, List<OAuth2ScopeGrant> oAuth2ScopeGrants) {

		try {
			long[] oAuth2ScopeGrantPKs = new long[oAuth2ScopeGrants.size()];

			for (int i = 0; i < oAuth2ScopeGrants.size(); i++) {
				OAuth2ScopeGrant oAuth2ScopeGrant = oAuth2ScopeGrants.get(i);

				oAuth2ScopeGrantPKs[i] = oAuth2ScopeGrant.getPrimaryKey();
			}

			setOAuth2ScopeGrants(pk, oAuth2ScopeGrantPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
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
		return "oAuth2AuthorizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTH2AUTHORIZATION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuth2AuthorizationModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth2 authorization persistence.
	 */
	@Activate
	public void activate() {
		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper =
			TableMapperFactory.getTableMapper(
				"OA2Auths_OA2ScopeGrants#oAuth2AuthorizationId",
				"OA2Auths_OA2ScopeGrants", "companyId", "oAuth2AuthorizationId",
				"oAuth2ScopeGrantId", this, OAuth2ScopeGrant.class);

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
				_SQL_SELECT_OAUTH2AUTHORIZATION_WHERE,
				_SQL_COUNT_OAUTH2AUTHORIZATION_WHERE,
				OAuth2AuthorizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuth2Authorization.", "userId", FinderColumn.Type.LONG,
					"=", true, true, OAuth2Authorization::getUserId));

		_collectionPersistenceFinderByOAuth2ApplicationId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByOAuth2ApplicationId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"oAuth2ApplicationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByOAuth2ApplicationId",
					new String[] {Long.class.getName()},
					new String[] {"oAuth2ApplicationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByOAuth2ApplicationId",
					new String[] {Long.class.getName()},
					new String[] {"oAuth2ApplicationId"}, false),
				_SQL_SELECT_OAUTH2AUTHORIZATION_WHERE,
				_SQL_COUNT_OAUTH2AUTHORIZATION_WHERE,
				OAuth2AuthorizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuth2Authorization.", "oAuth2ApplicationId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuth2Authorization::getOAuth2ApplicationId));

		_collectionPersistenceFinderByC_ATCH =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_ATCH",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "accessTokenContentHash"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_ATCH",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "accessTokenContentHash"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_ATCH",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "accessTokenContentHash"},
					false),
				_SQL_SELECT_OAUTH2AUTHORIZATION_WHERE,
				_SQL_COUNT_OAUTH2AUTHORIZATION_WHERE,
				OAuth2AuthorizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuth2Authorization.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, OAuth2Authorization::getCompanyId),
				new FinderColumn<>(
					"oAuth2Authorization.", "accessTokenContentHash",
					FinderColumn.Type.LONG, "=", true, true,
					OAuth2Authorization::getAccessTokenContentHash));

		_collectionPersistenceFinderByC_RTCH =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_RTCH",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "refreshTokenContentHash"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_RTCH",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "refreshTokenContentHash"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_RTCH",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "refreshTokenContentHash"},
					false),
				_SQL_SELECT_OAUTH2AUTHORIZATION_WHERE,
				_SQL_COUNT_OAUTH2AUTHORIZATION_WHERE,
				OAuth2AuthorizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuth2Authorization.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, OAuth2Authorization::getCompanyId),
				new FinderColumn<>(
					"oAuth2Authorization.", "refreshTokenContentHash",
					FinderColumn.Type.LONG, "=", true, true,
					OAuth2Authorization::getRefreshTokenContentHash));

		_collectionPersistenceFinderByU_O_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_O_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"userId", "oAuth2ApplicationId", "rememberDeviceContent"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_O_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {
					"userId", "oAuth2ApplicationId", "rememberDeviceContent"
				},
				0, 4, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_O_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {
					"userId", "oAuth2ApplicationId", "rememberDeviceContent"
				},
				0, 4, false, null),
			_SQL_SELECT_OAUTH2AUTHORIZATION_WHERE,
			_SQL_COUNT_OAUTH2AUTHORIZATION_WHERE,
			OAuth2AuthorizationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"oAuth2Authorization.", "userId", FinderColumn.Type.LONG, "=",
				true, true, OAuth2Authorization::getUserId),
			new FinderColumn<>(
				"oAuth2Authorization.", "oAuth2ApplicationId",
				FinderColumn.Type.LONG, "=", true, true,
				OAuth2Authorization::getOAuth2ApplicationId),
			new FinderColumn<>(
				"oAuth2Authorization.", "rememberDeviceContent",
				FinderColumn.Type.STRING, "=", true, true,
				OAuth2Authorization::getRememberDeviceContent));

		OAuth2AuthorizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuth2AuthorizationUtil.setPersistence(null);

		entityCache.removeCache(OAuth2AuthorizationImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OA2Auths_OA2ScopeGrants#oAuth2AuthorizationId");
	}

	@Override
	@Reference(
		target = OAuthTwoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OAuthTwoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OAuthTwoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	protected TableMapper<OAuth2Authorization, OAuth2ScopeGrant>
		oAuth2AuthorizationToOAuth2ScopeGrantTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		OAuth2AuthorizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OAUTH2AUTHORIZATION =
		"SELECT oAuth2Authorization FROM OAuth2Authorization oAuth2Authorization";

	private static final String _SQL_SELECT_OAUTH2AUTHORIZATION_WHERE =
		"SELECT oAuth2Authorization FROM OAuth2Authorization oAuth2Authorization WHERE ";

	private static final String _SQL_COUNT_OAUTH2AUTHORIZATION_WHERE =
		"SELECT COUNT(oAuth2Authorization) FROM OAuth2Authorization oAuth2Authorization WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"oAuth2ApplicationScopeAliasesId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-693941488