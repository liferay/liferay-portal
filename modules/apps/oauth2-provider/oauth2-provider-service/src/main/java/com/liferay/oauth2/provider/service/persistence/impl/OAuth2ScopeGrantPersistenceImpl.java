/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.persistence.impl;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2ScopeGrantException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrantTable;
import com.liferay.oauth2.provider.model.impl.OAuth2ScopeGrantImpl;
import com.liferay.oauth2.provider.model.impl.OAuth2ScopeGrantModelImpl;
import com.liferay.oauth2.provider.service.persistence.OAuth2ScopeGrantPersistence;
import com.liferay.oauth2.provider.service.persistence.OAuth2ScopeGrantUtil;
import com.liferay.oauth2.provider.service.persistence.impl.constants.OAuthTwoPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the o auth2 scope grant service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuth2ScopeGrantPersistence.class)
public class OAuth2ScopeGrantPersistenceImpl
	extends BasePersistenceImpl
		<OAuth2ScopeGrant, NoSuchOAuth2ScopeGrantException>
	implements OAuth2ScopeGrantPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuth2ScopeGrantUtil</code> to access the o auth2 scope grant persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuth2ScopeGrantImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<OAuth2ScopeGrant, NoSuchOAuth2ScopeGrantException>
			_collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId;

	/**
	 * Returns an ordered range of all the o auth2 scope grants where oAuth2ApplicationScopeAliasesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2ScopeGrantModelImpl</code>.
	 * </p>
	 *
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param start the lower bound of the range of o auth2 scope grants
	 * @param end the upper bound of the range of o auth2 scope grants (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 scope grants
	 */
	@Override
	public List<OAuth2ScopeGrant> findByOAuth2ApplicationScopeAliasesId(
		long oAuth2ApplicationScopeAliasesId, int start, int end,
		OrderByComparator<OAuth2ScopeGrant> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId.
			find(
				finderCache, new Object[] {oAuth2ApplicationScopeAliasesId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 scope grant in the ordered set where oAuth2ApplicationScopeAliasesId = &#63;.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 scope grant
	 * @throws NoSuchOAuth2ScopeGrantException if a matching o auth2 scope grant could not be found
	 */
	@Override
	public OAuth2ScopeGrant findByOAuth2ApplicationScopeAliasesId_First(
			long oAuth2ApplicationScopeAliasesId,
			OrderByComparator<OAuth2ScopeGrant> orderByComparator)
		throws NoSuchOAuth2ScopeGrantException {

		return _collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId.
			findFirst(
				finderCache, new Object[] {oAuth2ApplicationScopeAliasesId},
				orderByComparator);
	}

	/**
	 * Returns the first o auth2 scope grant in the ordered set where oAuth2ApplicationScopeAliasesId = &#63;.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 scope grant, or <code>null</code> if a matching o auth2 scope grant could not be found
	 */
	@Override
	public OAuth2ScopeGrant fetchByOAuth2ApplicationScopeAliasesId_First(
		long oAuth2ApplicationScopeAliasesId,
		OrderByComparator<OAuth2ScopeGrant> orderByComparator) {

		return _collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId.
			fetchFirst(
				finderCache, new Object[] {oAuth2ApplicationScopeAliasesId},
				orderByComparator);
	}

	/**
	 * Removes all the o auth2 scope grants where oAuth2ApplicationScopeAliasesId = &#63; from the database.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 */
	@Override
	public void removeByOAuth2ApplicationScopeAliasesId(
		long oAuth2ApplicationScopeAliasesId) {

		_collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId.remove(
			finderCache, new Object[] {oAuth2ApplicationScopeAliasesId});
	}

	/**
	 * Returns the number of o auth2 scope grants where oAuth2ApplicationScopeAliasesId = &#63;.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @return the number of matching o auth2 scope grants
	 */
	@Override
	public int countByOAuth2ApplicationScopeAliasesId(
		long oAuth2ApplicationScopeAliasesId) {

		return _collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId.
			count(finderCache, new Object[] {oAuth2ApplicationScopeAliasesId});
	}

	private UniquePersistenceFinder
		<OAuth2ScopeGrant, NoSuchOAuth2ScopeGrantException>
			_uniquePersistenceFinderByC_O_A_B_S;

	/**
	 * Returns the o auth2 scope grant where companyId = &#63; and oAuth2ApplicationScopeAliasesId = &#63; and applicationName = &#63; and bundleSymbolicName = &#63; and scope = &#63; or throws a <code>NoSuchOAuth2ScopeGrantException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param applicationName the application name
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param scope the scope
	 * @return the matching o auth2 scope grant
	 * @throws NoSuchOAuth2ScopeGrantException if a matching o auth2 scope grant could not be found
	 */
	@Override
	public OAuth2ScopeGrant findByC_O_A_B_S(
			long companyId, long oAuth2ApplicationScopeAliasesId,
			String applicationName, String bundleSymbolicName, String scope)
		throws NoSuchOAuth2ScopeGrantException {

		return _uniquePersistenceFinderByC_O_A_B_S.find(
			finderCache,
			new Object[] {
				companyId, oAuth2ApplicationScopeAliasesId, applicationName,
				bundleSymbolicName, scope
			});
	}

	/**
	 * Returns the o auth2 scope grant where companyId = &#63; and oAuth2ApplicationScopeAliasesId = &#63; and applicationName = &#63; and bundleSymbolicName = &#63; and scope = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param applicationName the application name
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param scope the scope
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth2 scope grant, or <code>null</code> if a matching o auth2 scope grant could not be found
	 */
	@Override
	public OAuth2ScopeGrant fetchByC_O_A_B_S(
		long companyId, long oAuth2ApplicationScopeAliasesId,
		String applicationName, String bundleSymbolicName, String scope,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_O_A_B_S.fetch(
			finderCache,
			new Object[] {
				companyId, oAuth2ApplicationScopeAliasesId, applicationName,
				bundleSymbolicName, scope
			},
			useFinderCache);
	}

	/**
	 * Removes the o auth2 scope grant where companyId = &#63; and oAuth2ApplicationScopeAliasesId = &#63; and applicationName = &#63; and bundleSymbolicName = &#63; and scope = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param applicationName the application name
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param scope the scope
	 * @return the o auth2 scope grant that was removed
	 */
	@Override
	public OAuth2ScopeGrant removeByC_O_A_B_S(
			long companyId, long oAuth2ApplicationScopeAliasesId,
			String applicationName, String bundleSymbolicName, String scope)
		throws NoSuchOAuth2ScopeGrantException {

		OAuth2ScopeGrant oAuth2ScopeGrant = findByC_O_A_B_S(
			companyId, oAuth2ApplicationScopeAliasesId, applicationName,
			bundleSymbolicName, scope);

		return remove(oAuth2ScopeGrant);
	}

	/**
	 * Returns the number of o auth2 scope grants where companyId = &#63; and oAuth2ApplicationScopeAliasesId = &#63; and applicationName = &#63; and bundleSymbolicName = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param oAuth2ApplicationScopeAliasesId the o auth2 application scope aliases ID
	 * @param applicationName the application name
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param scope the scope
	 * @return the number of matching o auth2 scope grants
	 */
	@Override
	public int countByC_O_A_B_S(
		long companyId, long oAuth2ApplicationScopeAliasesId,
		String applicationName, String bundleSymbolicName, String scope) {

		return _uniquePersistenceFinderByC_O_A_B_S.count(
			finderCache,
			new Object[] {
				companyId, oAuth2ApplicationScopeAliasesId, applicationName,
				bundleSymbolicName, scope
			});
	}

	public OAuth2ScopeGrantPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"oAuth2ApplicationScopeAliasesId", "oA2AScopeAliasesId");

		setDBColumnNames(dbColumnNames);

		setModelClass(OAuth2ScopeGrant.class);

		setModelImplClass(OAuth2ScopeGrantImpl.class);
		setModelPKClass(long.class);

		setTable(OAuth2ScopeGrantTable.INSTANCE);
	}

	/**
	 * Creates a new o auth2 scope grant with the primary key. Does not add the o auth2 scope grant to the database.
	 *
	 * @param oAuth2ScopeGrantId the primary key for the new o auth2 scope grant
	 * @return the new o auth2 scope grant
	 */
	@Override
	public OAuth2ScopeGrant create(long oAuth2ScopeGrantId) {
		OAuth2ScopeGrant oAuth2ScopeGrant = new OAuth2ScopeGrantImpl();

		oAuth2ScopeGrant.setNew(true);
		oAuth2ScopeGrant.setPrimaryKey(oAuth2ScopeGrantId);

		oAuth2ScopeGrant.setCompanyId(CompanyThreadLocal.getCompanyId());

		return oAuth2ScopeGrant;
	}

	/**
	 * Removes the o auth2 scope grant with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuth2ScopeGrantId the primary key of the o auth2 scope grant
	 * @return the o auth2 scope grant that was removed
	 * @throws NoSuchOAuth2ScopeGrantException if a o auth2 scope grant with the primary key could not be found
	 */
	@Override
	public OAuth2ScopeGrant remove(long oAuth2ScopeGrantId)
		throws NoSuchOAuth2ScopeGrantException {

		return remove((Serializable)oAuth2ScopeGrantId);
	}

	@Override
	protected OAuth2ScopeGrant removeImpl(OAuth2ScopeGrant oAuth2ScopeGrant) {
		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
			deleteLeftPrimaryKeyTableMappings(oAuth2ScopeGrant.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuth2ScopeGrant)) {
				oAuth2ScopeGrant = (OAuth2ScopeGrant)session.get(
					OAuth2ScopeGrantImpl.class,
					oAuth2ScopeGrant.getPrimaryKeyObj());
			}

			if (oAuth2ScopeGrant != null) {
				session.delete(oAuth2ScopeGrant);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuth2ScopeGrant != null) {
			clearCache(oAuth2ScopeGrant);
		}

		return oAuth2ScopeGrant;
	}

	@Override
	public OAuth2ScopeGrant updateImpl(OAuth2ScopeGrant oAuth2ScopeGrant) {
		boolean isNew = oAuth2ScopeGrant.isNew();

		if (!(oAuth2ScopeGrant instanceof OAuth2ScopeGrantModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuth2ScopeGrant.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuth2ScopeGrant);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuth2ScopeGrant proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuth2ScopeGrant implementation " +
					oAuth2ScopeGrant.getClass());
		}

		OAuth2ScopeGrantModelImpl oAuth2ScopeGrantModelImpl =
			(OAuth2ScopeGrantModelImpl)oAuth2ScopeGrant;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuth2ScopeGrant);
			}
			else {
				oAuth2ScopeGrant = (OAuth2ScopeGrant)session.merge(
					oAuth2ScopeGrant);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(oAuth2ScopeGrant, false);

		if (isNew) {
			oAuth2ScopeGrant.setNew(false);
		}

		oAuth2ScopeGrant.resetOriginalValues();

		return oAuth2ScopeGrant;
	}

	/**
	 * Returns the o auth2 scope grant with the primary key or throws a <code>NoSuchOAuth2ScopeGrantException</code> if it could not be found.
	 *
	 * @param oAuth2ScopeGrantId the primary key of the o auth2 scope grant
	 * @return the o auth2 scope grant
	 * @throws NoSuchOAuth2ScopeGrantException if a o auth2 scope grant with the primary key could not be found
	 */
	@Override
	public OAuth2ScopeGrant findByPrimaryKey(long oAuth2ScopeGrantId)
		throws NoSuchOAuth2ScopeGrantException {

		return findByPrimaryKey((Serializable)oAuth2ScopeGrantId);
	}

	/**
	 * Returns the o auth2 scope grant with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuth2ScopeGrantId the primary key of the o auth2 scope grant
	 * @return the o auth2 scope grant, or <code>null</code> if a o auth2 scope grant with the primary key could not be found
	 */
	@Override
	public OAuth2ScopeGrant fetchByPrimaryKey(long oAuth2ScopeGrantId) {
		return fetchByPrimaryKey((Serializable)oAuth2ScopeGrantId);
	}

	/**
	 * Returns the primaryKeys of o auth2 authorizations associated with the o auth2 scope grant.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @return long[] of the primaryKeys of o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Override
	public long[] getOAuth2AuthorizationPrimaryKeys(long pk) {
		long[] pks =
			oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the o auth2 scope grant associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @return the o auth2 scope grants associated with the o auth2 authorization
	 */
	@Override
	public List<OAuth2ScopeGrant> getOAuth2AuthorizationOAuth2ScopeGrants(
		long pk) {

		return getOAuth2AuthorizationOAuth2ScopeGrants(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the o auth2 scope grant associated with the o auth2 authorization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2ScopeGrantModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of o auth2 scope grants associated with the o auth2 authorization
	 */
	@Override
	public List<OAuth2ScopeGrant> getOAuth2AuthorizationOAuth2ScopeGrants(
		long pk, int start, int end) {

		return getOAuth2AuthorizationOAuth2ScopeGrants(pk, start, end, null);
	}

	/**
	 * Returns all the o auth2 scope grant associated with the o auth2 authorization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2ScopeGrantModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of o auth2 scope grants associated with the o auth2 authorization
	 */
	@Override
	public List<OAuth2ScopeGrant> getOAuth2AuthorizationOAuth2ScopeGrants(
		long pk, int start, int end,
		OrderByComparator<OAuth2ScopeGrant> orderByComparator) {

		return oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
			getLeftBaseModels(pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of o auth2 authorizations associated with the o auth2 scope grant.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @return the number of o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Override
	public int getOAuth2AuthorizationsSize(long pk) {
		long[] pks =
			oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the o auth2 authorization is associated with the o auth2 scope grant.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2AuthorizationPK the primary key of the o auth2 authorization
	 * @return <code>true</code> if the o auth2 authorization is associated with the o auth2 scope grant; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOAuth2Authorization(
		long pk, long oAuth2AuthorizationPK) {

		return oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
			containsTableMapping(pk, oAuth2AuthorizationPK);
	}

	/**
	 * Returns <code>true</code> if the o auth2 scope grant has any o auth2 authorizations associated with it.
	 *
	 * @param pk the primary key of the o auth2 scope grant to check for associations with o auth2 authorizations
	 * @return <code>true</code> if the o auth2 scope grant has any o auth2 authorizations associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOAuth2Authorizations(long pk) {
		if (getOAuth2AuthorizationsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the o auth2 scope grant and the o auth2 authorization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2AuthorizationPK the primary key of the o auth2 authorization
	 * @return <code>true</code> if an association between the o auth2 scope grant and the o auth2 authorization was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOAuth2Authorization(long pk, long oAuth2AuthorizationPK) {
		OAuth2ScopeGrant oAuth2ScopeGrant = fetchByPrimaryKey(pk);

		if (oAuth2ScopeGrant == null) {
			return oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				addTableMapping(
					CompanyThreadLocal.getCompanyId(), pk,
					oAuth2AuthorizationPK);
		}
		else {
			return oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				addTableMapping(
					oAuth2ScopeGrant.getCompanyId(), pk, oAuth2AuthorizationPK);
		}
	}

	/**
	 * Adds an association between the o auth2 scope grant and the o auth2 authorization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return <code>true</code> if an association between the o auth2 scope grant and the o auth2 authorization was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOAuth2Authorization(
		long pk, OAuth2Authorization oAuth2Authorization) {

		OAuth2ScopeGrant oAuth2ScopeGrant = fetchByPrimaryKey(pk);

		if (oAuth2ScopeGrant == null) {
			return oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				addTableMapping(
					CompanyThreadLocal.getCompanyId(), pk,
					oAuth2Authorization.getPrimaryKey());
		}
		else {
			return oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				addTableMapping(
					oAuth2ScopeGrant.getCompanyId(), pk,
					oAuth2Authorization.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the o auth2 scope grant and the o auth2 authorizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2AuthorizationPKs the primary keys of the o auth2 authorizations
	 * @return <code>true</code> if at least one association between the o auth2 scope grant and the o auth2 authorizations was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOAuth2Authorizations(
		long pk, long[] oAuth2AuthorizationPKs) {

		long companyId = 0;

		OAuth2ScopeGrant oAuth2ScopeGrant = fetchByPrimaryKey(pk);

		if (oAuth2ScopeGrant == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = oAuth2ScopeGrant.getCompanyId();
		}

		long[] addedKeys =
			oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.addTableMappings(
				companyId, pk, oAuth2AuthorizationPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the o auth2 scope grant and the o auth2 authorizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2Authorizations the o auth2 authorizations
	 * @return <code>true</code> if at least one association between the o auth2 scope grant and the o auth2 authorizations was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOAuth2Authorizations(
		long pk, List<OAuth2Authorization> oAuth2Authorizations) {

		return addOAuth2Authorizations(
			pk,
			ListUtil.toLongArray(
				oAuth2Authorizations,
				OAuth2Authorization.O_AUTH2_AUTHORIZATION_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the o auth2 scope grant and its o auth2 authorizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant to clear the associated o auth2 authorizations from
	 */
	@Override
	public void clearOAuth2Authorizations(long pk) {
		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the o auth2 scope grant and the o auth2 authorization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2AuthorizationPK the primary key of the o auth2 authorization
	 */
	@Override
	public void removeOAuth2Authorization(long pk, long oAuth2AuthorizationPK) {
		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.deleteTableMapping(
			pk, oAuth2AuthorizationPK);
	}

	/**
	 * Removes the association between the o auth2 scope grant and the o auth2 authorization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2Authorization the o auth2 authorization
	 */
	@Override
	public void removeOAuth2Authorization(
		long pk, OAuth2Authorization oAuth2Authorization) {

		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.deleteTableMapping(
			pk, oAuth2Authorization.getPrimaryKey());
	}

	/**
	 * Removes the association between the o auth2 scope grant and the o auth2 authorizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2AuthorizationPKs the primary keys of the o auth2 authorizations
	 */
	@Override
	public void removeOAuth2Authorizations(
		long pk, long[] oAuth2AuthorizationPKs) {

		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.deleteTableMappings(
			pk, oAuth2AuthorizationPKs);
	}

	/**
	 * Removes the association between the o auth2 scope grant and the o auth2 authorizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2Authorizations the o auth2 authorizations
	 */
	@Override
	public void removeOAuth2Authorizations(
		long pk, List<OAuth2Authorization> oAuth2Authorizations) {

		removeOAuth2Authorizations(
			pk,
			ListUtil.toLongArray(
				oAuth2Authorizations,
				OAuth2Authorization.O_AUTH2_AUTHORIZATION_ID_ACCESSOR));
	}

	/**
	 * Sets the o auth2 authorizations associated with the o auth2 scope grant, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2AuthorizationPKs the primary keys of the o auth2 authorizations to be associated with the o auth2 scope grant
	 */
	@Override
	public void setOAuth2Authorizations(
		long pk, long[] oAuth2AuthorizationPKs) {

		Set<Long> newOAuth2AuthorizationPKsSet = SetUtil.fromArray(
			oAuth2AuthorizationPKs);
		Set<Long> oldOAuth2AuthorizationPKsSet = SetUtil.fromArray(
			oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.
				getRightPrimaryKeys(pk));

		Set<Long> removeOAuth2AuthorizationPKsSet = new HashSet<Long>(
			oldOAuth2AuthorizationPKsSet);

		removeOAuth2AuthorizationPKsSet.removeAll(newOAuth2AuthorizationPKsSet);

		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeOAuth2AuthorizationPKsSet));

		newOAuth2AuthorizationPKsSet.removeAll(oldOAuth2AuthorizationPKsSet);

		long companyId = 0;

		OAuth2ScopeGrant oAuth2ScopeGrant = fetchByPrimaryKey(pk);

		if (oAuth2ScopeGrant == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = oAuth2ScopeGrant.getCompanyId();
		}

		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newOAuth2AuthorizationPKsSet));
	}

	/**
	 * Sets the o auth2 authorizations associated with the o auth2 scope grant, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param oAuth2Authorizations the o auth2 authorizations to be associated with the o auth2 scope grant
	 */
	@Override
	public void setOAuth2Authorizations(
		long pk, List<OAuth2Authorization> oAuth2Authorizations) {

		try {
			long[] oAuth2AuthorizationPKs =
				new long[oAuth2Authorizations.size()];

			for (int i = 0; i < oAuth2Authorizations.size(); i++) {
				OAuth2Authorization oAuth2Authorization =
					oAuth2Authorizations.get(i);

				oAuth2AuthorizationPKs[i] = oAuth2Authorization.getPrimaryKey();
			}

			setOAuth2Authorizations(pk, oAuth2AuthorizationPKs);
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
		return "oAuth2ScopeGrantId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTH2SCOPEGRANT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuth2ScopeGrantModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth2 scope grant persistence.
	 */
	@Activate
	public void activate() {
		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper =
			TableMapperFactory.getTableMapper(
				"OA2Auths_OA2ScopeGrants#oAuth2ScopeGrantId",
				"OA2Auths_OA2ScopeGrants", "companyId", "oAuth2ScopeGrantId",
				"oAuth2AuthorizationId", this, OAuth2Authorization.class);

		_collectionPersistenceFinderByOAuth2ApplicationScopeAliasesId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByOAuth2ApplicationScopeAliasesId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"oA2AScopeAliasesId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByOAuth2ApplicationScopeAliasesId",
					new String[] {Long.class.getName()},
					new String[] {"oA2AScopeAliasesId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByOAuth2ApplicationScopeAliasesId",
					new String[] {Long.class.getName()},
					new String[] {"oA2AScopeAliasesId"}, false),
				_SQL_SELECT_OAUTH2SCOPEGRANT_WHERE,
				_SQL_COUNT_OAUTH2SCOPEGRANT_WHERE,
				OAuth2ScopeGrantModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"oAuth2ScopeGrant.", "oAuth2ApplicationScopeAliasesId",
					"oA2AScopeAliasesId", FinderColumn.Type.LONG, "=", true,
					true,
					OAuth2ScopeGrant::getOAuth2ApplicationScopeAliasesId));

		_uniquePersistenceFinderByC_O_A_B_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_O_A_B_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"companyId", "oA2AScopeAliasesId", "applicationName",
					"bundleSymbolicName", "scope"
				},
				0, 28, false, OAuth2ScopeGrant::getCompanyId,
				OAuth2ScopeGrant::getOAuth2ApplicationScopeAliasesId,
				convertNullFunction(OAuth2ScopeGrant::getApplicationName),
				convertNullFunction(OAuth2ScopeGrant::getBundleSymbolicName),
				convertNullFunction(OAuth2ScopeGrant::getScope)),
			_SQL_SELECT_OAUTH2SCOPEGRANT_WHERE, "",
			new FinderColumn<>(
				"oAuth2ScopeGrant.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, OAuth2ScopeGrant::getCompanyId),
			new FinderColumn<>(
				"oAuth2ScopeGrant.", "oAuth2ApplicationScopeAliasesId",
				"oA2AScopeAliasesId", FinderColumn.Type.LONG, "=", true, true,
				OAuth2ScopeGrant::getOAuth2ApplicationScopeAliasesId),
			new FinderColumn<>(
				"oAuth2ScopeGrant.", "applicationName",
				FinderColumn.Type.STRING, "=", true, true,
				OAuth2ScopeGrant::getApplicationName),
			new FinderColumn<>(
				"oAuth2ScopeGrant.", "bundleSymbolicName",
				FinderColumn.Type.STRING, "=", true, true,
				OAuth2ScopeGrant::getBundleSymbolicName),
			new FinderColumn<>(
				"oAuth2ScopeGrant.", "scope", FinderColumn.Type.STRING, "=",
				true, true, OAuth2ScopeGrant::getScope));

		OAuth2ScopeGrantUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuth2ScopeGrantUtil.setPersistence(null);

		entityCache.removeCache(OAuth2ScopeGrantImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OA2Auths_OA2ScopeGrants#oAuth2ScopeGrantId");
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

	protected TableMapper<OAuth2ScopeGrant, OAuth2Authorization>
		oAuth2ScopeGrantToOAuth2AuthorizationTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		OAuth2ScopeGrantModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OAUTH2SCOPEGRANT =
		"SELECT oAuth2ScopeGrant FROM OAuth2ScopeGrant oAuth2ScopeGrant";

	private static final String _SQL_SELECT_OAUTH2SCOPEGRANT_WHERE =
		"SELECT oAuth2ScopeGrant FROM OAuth2ScopeGrant oAuth2ScopeGrant WHERE ";

	private static final String _SQL_COUNT_OAUTH2SCOPEGRANT_WHERE =
		"SELECT COUNT(oAuth2ScopeGrant) FROM OAuth2ScopeGrant oAuth2ScopeGrant WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"oAuth2ApplicationScopeAliasesId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:606474620