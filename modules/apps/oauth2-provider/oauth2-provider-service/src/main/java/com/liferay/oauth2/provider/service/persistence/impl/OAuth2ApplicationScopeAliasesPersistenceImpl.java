/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.persistence.impl;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationScopeAliasesException;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliasesTable;
import com.liferay.oauth2.provider.model.impl.OAuth2ApplicationScopeAliasesImpl;
import com.liferay.oauth2.provider.model.impl.OAuth2ApplicationScopeAliasesModelImpl;
import com.liferay.oauth2.provider.service.persistence.OAuth2ApplicationScopeAliasesPersistence;
import com.liferay.oauth2.provider.service.persistence.OAuth2ApplicationScopeAliasesUtil;
import com.liferay.oauth2.provider.service.persistence.impl.constants.OAuthTwoPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
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
 * The persistence implementation for the o auth2 application scope aliases service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuth2ApplicationScopeAliasesPersistence.class)
public class OAuth2ApplicationScopeAliasesPersistenceImpl
	extends BasePersistenceImpl
		<OAuth2ApplicationScopeAliases,
		 NoSuchOAuth2ApplicationScopeAliasesException>
	implements OAuth2ApplicationScopeAliasesPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuth2ApplicationScopeAliasesUtil</code> to access the o auth2 application scope aliases persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuth2ApplicationScopeAliasesImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<OAuth2ApplicationScopeAliases,
		 NoSuchOAuth2ApplicationScopeAliasesException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the o auth2 application scope aliaseses where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2ApplicationScopeAliasesModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth2 application scope aliaseses
	 * @param end the upper bound of the range of o auth2 application scope aliaseses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 application scope aliaseses
	 */
	@Override
	public List<OAuth2ApplicationScopeAliases> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuth2ApplicationScopeAliases> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 application scope aliases in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 application scope aliases
	 * @throws NoSuchOAuth2ApplicationScopeAliasesException if a matching o auth2 application scope aliases could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuth2ApplicationScopeAliases> orderByComparator)
		throws NoSuchOAuth2ApplicationScopeAliasesException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first o auth2 application scope aliases in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 application scope aliases, or <code>null</code> if a matching o auth2 application scope aliases could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases fetchByCompanyId_First(
		long companyId,
		OrderByComparator<OAuth2ApplicationScopeAliases> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the o auth2 application scope aliaseses where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of o auth2 application scope aliaseses where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth2 application scope aliaseses
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<OAuth2ApplicationScopeAliases,
		 NoSuchOAuth2ApplicationScopeAliasesException>
			_collectionPersistenceFinderByOAuth2ApplicationId;

	/**
	 * Returns an ordered range of all the o auth2 application scope aliaseses where oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuth2ApplicationScopeAliasesModelImpl</code>.
	 * </p>
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 application scope aliaseses
	 * @param end the upper bound of the range of o auth2 application scope aliaseses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 application scope aliaseses
	 */
	@Override
	public List<OAuth2ApplicationScopeAliases> findByOAuth2ApplicationId(
		long oAuth2ApplicationId, int start, int end,
		OrderByComparator<OAuth2ApplicationScopeAliases> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOAuth2ApplicationId.find(
			finderCache, new Object[] {oAuth2ApplicationId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth2 application scope aliases in the ordered set where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 application scope aliases
	 * @throws NoSuchOAuth2ApplicationScopeAliasesException if a matching o auth2 application scope aliases could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases findByOAuth2ApplicationId_First(
			long oAuth2ApplicationId,
			OrderByComparator<OAuth2ApplicationScopeAliases> orderByComparator)
		throws NoSuchOAuth2ApplicationScopeAliasesException {

		return _collectionPersistenceFinderByOAuth2ApplicationId.findFirst(
			finderCache, new Object[] {oAuth2ApplicationId}, orderByComparator);
	}

	/**
	 * Returns the first o auth2 application scope aliases in the ordered set where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 application scope aliases, or <code>null</code> if a matching o auth2 application scope aliases could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases fetchByOAuth2ApplicationId_First(
		long oAuth2ApplicationId,
		OrderByComparator<OAuth2ApplicationScopeAliases> orderByComparator) {

		return _collectionPersistenceFinderByOAuth2ApplicationId.fetchFirst(
			finderCache, new Object[] {oAuth2ApplicationId}, orderByComparator);
	}

	/**
	 * Removes all the o auth2 application scope aliaseses where oAuth2ApplicationId = &#63; from the database.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 */
	@Override
	public void removeByOAuth2ApplicationId(long oAuth2ApplicationId) {
		_collectionPersistenceFinderByOAuth2ApplicationId.remove(
			finderCache, new Object[] {oAuth2ApplicationId});
	}

	/**
	 * Returns the number of o auth2 application scope aliaseses where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @return the number of matching o auth2 application scope aliaseses
	 */
	@Override
	public int countByOAuth2ApplicationId(long oAuth2ApplicationId) {
		return _collectionPersistenceFinderByOAuth2ApplicationId.count(
			finderCache, new Object[] {oAuth2ApplicationId});
	}

	public OAuth2ApplicationScopeAliasesPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"oAuth2ApplicationScopeAliasesId", "oA2AScopeAliasesId");

		setDBColumnNames(dbColumnNames);

		setModelClass(OAuth2ApplicationScopeAliases.class);

		setModelImplClass(OAuth2ApplicationScopeAliasesImpl.class);
		setModelPKClass(long.class);

		setTable(OAuth2ApplicationScopeAliasesTable.INSTANCE);
	}

	/**
	 * Creates a new o auth2 application scope aliases with the primary key. Does not add the o auth2 application scope aliases to the database.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key for the new o auth2 application scope aliases
	 * @return the new o auth2 application scope aliases
	 */
	@Override
	public OAuth2ApplicationScopeAliases create(
		long oAuth2ApplicationScopeAliasesId) {

		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
			new OAuth2ApplicationScopeAliasesImpl();

		oAuth2ApplicationScopeAliases.setNew(true);
		oAuth2ApplicationScopeAliases.setPrimaryKey(
			oAuth2ApplicationScopeAliasesId);

		oAuth2ApplicationScopeAliases.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return oAuth2ApplicationScopeAliases;
	}

	/**
	 * Removes the o auth2 application scope aliases with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key of the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases that was removed
	 * @throws NoSuchOAuth2ApplicationScopeAliasesException if a o auth2 application scope aliases with the primary key could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases remove(
			long oAuth2ApplicationScopeAliasesId)
		throws NoSuchOAuth2ApplicationScopeAliasesException {

		return remove((Serializable)oAuth2ApplicationScopeAliasesId);
	}

	@Override
	protected OAuth2ApplicationScopeAliases removeImpl(
		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuth2ApplicationScopeAliases)) {
				oAuth2ApplicationScopeAliases =
					(OAuth2ApplicationScopeAliases)session.get(
						OAuth2ApplicationScopeAliasesImpl.class,
						oAuth2ApplicationScopeAliases.getPrimaryKeyObj());
			}

			if (oAuth2ApplicationScopeAliases != null) {
				session.delete(oAuth2ApplicationScopeAliases);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuth2ApplicationScopeAliases != null) {
			clearCache(oAuth2ApplicationScopeAliases);
		}

		return oAuth2ApplicationScopeAliases;
	}

	@Override
	public OAuth2ApplicationScopeAliases updateImpl(
		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases) {

		boolean isNew = oAuth2ApplicationScopeAliases.isNew();

		if (!(oAuth2ApplicationScopeAliases instanceof
				OAuth2ApplicationScopeAliasesModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					oAuth2ApplicationScopeAliases.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuth2ApplicationScopeAliases);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuth2ApplicationScopeAliases proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuth2ApplicationScopeAliases implementation " +
					oAuth2ApplicationScopeAliases.getClass());
		}

		OAuth2ApplicationScopeAliasesModelImpl
			oAuth2ApplicationScopeAliasesModelImpl =
				(OAuth2ApplicationScopeAliasesModelImpl)
					oAuth2ApplicationScopeAliases;

		if (isNew && (oAuth2ApplicationScopeAliases.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				oAuth2ApplicationScopeAliases.setCreateDate(date);
			}
			else {
				oAuth2ApplicationScopeAliases.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuth2ApplicationScopeAliases);
			}
			else {
				oAuth2ApplicationScopeAliases =
					(OAuth2ApplicationScopeAliases)session.merge(
						oAuth2ApplicationScopeAliases);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(oAuth2ApplicationScopeAliases, false);

		if (isNew) {
			oAuth2ApplicationScopeAliases.setNew(false);
		}

		oAuth2ApplicationScopeAliases.resetOriginalValues();

		return oAuth2ApplicationScopeAliases;
	}

	/**
	 * Returns the o auth2 application scope aliases with the primary key or throws a <code>NoSuchOAuth2ApplicationScopeAliasesException</code> if it could not be found.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key of the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases
	 * @throws NoSuchOAuth2ApplicationScopeAliasesException if a o auth2 application scope aliases with the primary key could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases findByPrimaryKey(
			long oAuth2ApplicationScopeAliasesId)
		throws NoSuchOAuth2ApplicationScopeAliasesException {

		return findByPrimaryKey((Serializable)oAuth2ApplicationScopeAliasesId);
	}

	/**
	 * Returns the o auth2 application scope aliases with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key of the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases, or <code>null</code> if a o auth2 application scope aliases with the primary key could not be found
	 */
	@Override
	public OAuth2ApplicationScopeAliases fetchByPrimaryKey(
		long oAuth2ApplicationScopeAliasesId) {

		return fetchByPrimaryKey((Serializable)oAuth2ApplicationScopeAliasesId);
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
		return "oA2AScopeAliasesId";
	}

	@Override
	protected String getPKFieldName() {
		return "oAuth2ApplicationScopeAliasesId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTH2APPLICATIONSCOPEALIASES;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuth2ApplicationScopeAliasesModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth2 application scope aliases persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_OAUTH2APPLICATIONSCOPEALIASES_WHERE,
				_SQL_COUNT_OAUTH2APPLICATIONSCOPEALIASES_WHERE,
				OAuth2ApplicationScopeAliasesModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"oAuth2ApplicationScopeAliases.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuth2ApplicationScopeAliases::getCompanyId));

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
				_SQL_SELECT_OAUTH2APPLICATIONSCOPEALIASES_WHERE,
				_SQL_COUNT_OAUTH2APPLICATIONSCOPEALIASES_WHERE,
				OAuth2ApplicationScopeAliasesModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"oAuth2ApplicationScopeAliases.", "oAuth2ApplicationId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuth2ApplicationScopeAliases::getOAuth2ApplicationId));

		OAuth2ApplicationScopeAliasesUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuth2ApplicationScopeAliasesUtil.setPersistence(null);

		entityCache.removeCache(
			OAuth2ApplicationScopeAliasesImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		OAuth2ApplicationScopeAliasesModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OAUTH2APPLICATIONSCOPEALIASES =
		"SELECT oAuth2ApplicationScopeAliases FROM OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases";

	private static final String
		_SQL_SELECT_OAUTH2APPLICATIONSCOPEALIASES_WHERE =
			"SELECT oAuth2ApplicationScopeAliases FROM OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases WHERE ";

	private static final String _SQL_COUNT_OAUTH2APPLICATIONSCOPEALIASES_WHERE =
		"SELECT COUNT(oAuth2ApplicationScopeAliases) FROM OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OAuth2ApplicationScopeAliases exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"oAuth2ApplicationScopeAliasesId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:85985528