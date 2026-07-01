/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

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
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.saml.persistence.exception.NoSuchIdpSpConnectionException;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlIdpSpConnectionTable;
import com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionImpl;
import com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpConnectionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpConnectionUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

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
 * The persistence implementation for the saml idp sp connection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlIdpSpConnectionPersistence.class)
public class SamlIdpSpConnectionPersistenceImpl
	extends BasePersistenceImpl
		<SamlIdpSpConnection, NoSuchIdpSpConnectionException>
	implements SamlIdpSpConnectionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlIdpSpConnectionUtil</code> to access the saml idp sp connection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlIdpSpConnectionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SamlIdpSpConnection, NoSuchIdpSpConnectionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the saml idp sp connections where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSpConnectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of saml idp sp connections
	 * @param end the upper bound of the range of saml idp sp connections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml idp sp connections
	 */
	@Override
	public List<SamlIdpSpConnection> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SamlIdpSpConnection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml idp sp connection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sp connection
	 * @throws NoSuchIdpSpConnectionException if a matching saml idp sp connection could not be found
	 */
	@Override
	public SamlIdpSpConnection findByCompanyId_First(
			long companyId,
			OrderByComparator<SamlIdpSpConnection> orderByComparator)
		throws NoSuchIdpSpConnectionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first saml idp sp connection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sp connection, or <code>null</code> if a matching saml idp sp connection could not be found
	 */
	@Override
	public SamlIdpSpConnection fetchByCompanyId_First(
		long companyId,
		OrderByComparator<SamlIdpSpConnection> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the saml idp sp connections where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of saml idp sp connections where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching saml idp sp connections
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder
		<SamlIdpSpConnection, NoSuchIdpSpConnectionException>
			_uniquePersistenceFinderByC_SSEI;

	/**
	 * Returns the saml idp sp connection where companyId = &#63; and samlSpEntityId = &#63; or throws a <code>NoSuchIdpSpConnectionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param samlSpEntityId the saml sp entity ID
	 * @return the matching saml idp sp connection
	 * @throws NoSuchIdpSpConnectionException if a matching saml idp sp connection could not be found
	 */
	@Override
	public SamlIdpSpConnection findByC_SSEI(
			long companyId, String samlSpEntityId)
		throws NoSuchIdpSpConnectionException {

		return _uniquePersistenceFinderByC_SSEI.find(
			finderCache, new Object[] {companyId, samlSpEntityId});
	}

	/**
	 * Returns the saml idp sp connection where companyId = &#63; and samlSpEntityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param samlSpEntityId the saml sp entity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml idp sp connection, or <code>null</code> if a matching saml idp sp connection could not be found
	 */
	@Override
	public SamlIdpSpConnection fetchByC_SSEI(
		long companyId, String samlSpEntityId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_SSEI.fetch(
			finderCache, new Object[] {companyId, samlSpEntityId},
			useFinderCache);
	}

	/**
	 * Removes the saml idp sp connection where companyId = &#63; and samlSpEntityId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param samlSpEntityId the saml sp entity ID
	 * @return the saml idp sp connection that was removed
	 */
	@Override
	public SamlIdpSpConnection removeByC_SSEI(
			long companyId, String samlSpEntityId)
		throws NoSuchIdpSpConnectionException {

		SamlIdpSpConnection samlIdpSpConnection = findByC_SSEI(
			companyId, samlSpEntityId);

		return remove(samlIdpSpConnection);
	}

	/**
	 * Returns the number of saml idp sp connections where companyId = &#63; and samlSpEntityId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param samlSpEntityId the saml sp entity ID
	 * @return the number of matching saml idp sp connections
	 */
	@Override
	public int countByC_SSEI(long companyId, String samlSpEntityId) {
		return _uniquePersistenceFinderByC_SSEI.count(
			finderCache, new Object[] {companyId, samlSpEntityId});
	}

	public SamlIdpSpConnectionPersistenceImpl() {
		setModelClass(SamlIdpSpConnection.class);

		setModelImplClass(SamlIdpSpConnectionImpl.class);
		setModelPKClass(long.class);

		setTable(SamlIdpSpConnectionTable.INSTANCE);
	}

	/**
	 * Creates a new saml idp sp connection with the primary key. Does not add the saml idp sp connection to the database.
	 *
	 * @param samlIdpSpConnectionId the primary key for the new saml idp sp connection
	 * @return the new saml idp sp connection
	 */
	@Override
	public SamlIdpSpConnection create(long samlIdpSpConnectionId) {
		SamlIdpSpConnection samlIdpSpConnection = new SamlIdpSpConnectionImpl();

		samlIdpSpConnection.setNew(true);
		samlIdpSpConnection.setPrimaryKey(samlIdpSpConnectionId);

		samlIdpSpConnection.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlIdpSpConnection;
	}

	/**
	 * Removes the saml idp sp connection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection that was removed
	 * @throws NoSuchIdpSpConnectionException if a saml idp sp connection with the primary key could not be found
	 */
	@Override
	public SamlIdpSpConnection remove(long samlIdpSpConnectionId)
		throws NoSuchIdpSpConnectionException {

		return remove((Serializable)samlIdpSpConnectionId);
	}

	@Override
	protected SamlIdpSpConnection removeImpl(
		SamlIdpSpConnection samlIdpSpConnection) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlIdpSpConnection)) {
				samlIdpSpConnection = (SamlIdpSpConnection)session.get(
					SamlIdpSpConnectionImpl.class,
					samlIdpSpConnection.getPrimaryKeyObj());
			}

			if (samlIdpSpConnection != null) {
				session.delete(samlIdpSpConnection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlIdpSpConnection != null) {
			clearCache(samlIdpSpConnection);
		}

		return samlIdpSpConnection;
	}

	@Override
	public SamlIdpSpConnection updateImpl(
		SamlIdpSpConnection samlIdpSpConnection) {

		boolean isNew = samlIdpSpConnection.isNew();

		if (!(samlIdpSpConnection instanceof SamlIdpSpConnectionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlIdpSpConnection.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlIdpSpConnection);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlIdpSpConnection proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlIdpSpConnection implementation " +
					samlIdpSpConnection.getClass());
		}

		SamlIdpSpConnectionModelImpl samlIdpSpConnectionModelImpl =
			(SamlIdpSpConnectionModelImpl)samlIdpSpConnection;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (samlIdpSpConnection.getCreateDate() == null)) {
			if (serviceContext == null) {
				samlIdpSpConnection.setCreateDate(date);
			}
			else {
				samlIdpSpConnection.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!samlIdpSpConnectionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				samlIdpSpConnection.setModifiedDate(date);
			}
			else {
				samlIdpSpConnection.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlIdpSpConnection);
			}
			else {
				samlIdpSpConnection = (SamlIdpSpConnection)session.merge(
					samlIdpSpConnection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlIdpSpConnection, false);

		if (isNew) {
			samlIdpSpConnection.setNew(false);
		}

		samlIdpSpConnection.resetOriginalValues();

		return samlIdpSpConnection;
	}

	/**
	 * Returns the saml idp sp connection with the primary key or throws a <code>NoSuchIdpSpConnectionException</code> if it could not be found.
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection
	 * @throws NoSuchIdpSpConnectionException if a saml idp sp connection with the primary key could not be found
	 */
	@Override
	public SamlIdpSpConnection findByPrimaryKey(long samlIdpSpConnectionId)
		throws NoSuchIdpSpConnectionException {

		return findByPrimaryKey((Serializable)samlIdpSpConnectionId);
	}

	/**
	 * Returns the saml idp sp connection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection, or <code>null</code> if a saml idp sp connection with the primary key could not be found
	 */
	@Override
	public SamlIdpSpConnection fetchByPrimaryKey(long samlIdpSpConnectionId) {
		return fetchByPrimaryKey((Serializable)samlIdpSpConnectionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlIdpSpConnectionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLIDPSPCONNECTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlIdpSpConnectionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml idp sp connection persistence.
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
				_SQL_SELECT_SAMLIDPSPCONNECTION_WHERE,
				_SQL_COUNT_SAMLIDPSPCONNECTION_WHERE,
				SamlIdpSpConnectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"samlIdpSpConnection.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SamlIdpSpConnection::getCompanyId));

		_uniquePersistenceFinderByC_SSEI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_SSEI",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "samlSpEntityId"}, 0, 2, false,
				SamlIdpSpConnection::getCompanyId,
				convertNullFunction(SamlIdpSpConnection::getSamlSpEntityId)),
			_SQL_SELECT_SAMLIDPSPCONNECTION_WHERE, "",
			new FinderColumn<>(
				"samlIdpSpConnection.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, SamlIdpSpConnection::getCompanyId),
			new FinderColumn<>(
				"samlIdpSpConnection.", "samlSpEntityId",
				FinderColumn.Type.STRING, "=", true, true,
				SamlIdpSpConnection::getSamlSpEntityId));

		SamlIdpSpConnectionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlIdpSpConnectionUtil.setPersistence(null);

		entityCache.removeCache(SamlIdpSpConnectionImpl.class.getName());
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SamlIdpSpConnectionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLIDPSPCONNECTION =
		"SELECT samlIdpSpConnection FROM SamlIdpSpConnection samlIdpSpConnection";

	private static final String _SQL_SELECT_SAMLIDPSPCONNECTION_WHERE =
		"SELECT samlIdpSpConnection FROM SamlIdpSpConnection samlIdpSpConnection WHERE ";

	private static final String _SQL_COUNT_SAMLIDPSPCONNECTION_WHERE =
		"SELECT COUNT(samlIdpSpConnection) FROM SamlIdpSpConnection samlIdpSpConnection WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1627314166