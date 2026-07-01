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
import com.liferay.saml.persistence.exception.NoSuchSpIdpConnectionException;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnectionTable;
import com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionImpl;
import com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlSpIdpConnectionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpIdpConnectionUtil;
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
 * The persistence implementation for the saml sp idp connection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlSpIdpConnectionPersistence.class)
public class SamlSpIdpConnectionPersistenceImpl
	extends BasePersistenceImpl
		<SamlSpIdpConnection, NoSuchSpIdpConnectionException>
	implements SamlSpIdpConnectionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlSpIdpConnectionUtil</code> to access the saml sp idp connection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlSpIdpConnectionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SamlSpIdpConnection, NoSuchSpIdpConnectionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the saml sp idp connections where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpIdpConnectionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of saml sp idp connections
	 * @param end the upper bound of the range of saml sp idp connections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml sp idp connections
	 */
	@Override
	public List<SamlSpIdpConnection> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SamlSpIdpConnection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml sp idp connection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp idp connection
	 * @throws NoSuchSpIdpConnectionException if a matching saml sp idp connection could not be found
	 */
	@Override
	public SamlSpIdpConnection findByCompanyId_First(
			long companyId,
			OrderByComparator<SamlSpIdpConnection> orderByComparator)
		throws NoSuchSpIdpConnectionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first saml sp idp connection in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp idp connection, or <code>null</code> if a matching saml sp idp connection could not be found
	 */
	@Override
	public SamlSpIdpConnection fetchByCompanyId_First(
		long companyId,
		OrderByComparator<SamlSpIdpConnection> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the saml sp idp connections where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of saml sp idp connections where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching saml sp idp connections
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder
		<SamlSpIdpConnection, NoSuchSpIdpConnectionException>
			_uniquePersistenceFinderByC_SIEI;

	/**
	 * Returns the saml sp idp connection where companyId = &#63; and samlIdpEntityId = &#63; or throws a <code>NoSuchSpIdpConnectionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param samlIdpEntityId the saml idp entity ID
	 * @return the matching saml sp idp connection
	 * @throws NoSuchSpIdpConnectionException if a matching saml sp idp connection could not be found
	 */
	@Override
	public SamlSpIdpConnection findByC_SIEI(
			long companyId, String samlIdpEntityId)
		throws NoSuchSpIdpConnectionException {

		return _uniquePersistenceFinderByC_SIEI.find(
			finderCache, new Object[] {companyId, samlIdpEntityId});
	}

	/**
	 * Returns the saml sp idp connection where companyId = &#63; and samlIdpEntityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml sp idp connection, or <code>null</code> if a matching saml sp idp connection could not be found
	 */
	@Override
	public SamlSpIdpConnection fetchByC_SIEI(
		long companyId, String samlIdpEntityId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_SIEI.fetch(
			finderCache, new Object[] {companyId, samlIdpEntityId},
			useFinderCache);
	}

	/**
	 * Removes the saml sp idp connection where companyId = &#63; and samlIdpEntityId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param samlIdpEntityId the saml idp entity ID
	 * @return the saml sp idp connection that was removed
	 */
	@Override
	public SamlSpIdpConnection removeByC_SIEI(
			long companyId, String samlIdpEntityId)
		throws NoSuchSpIdpConnectionException {

		SamlSpIdpConnection samlSpIdpConnection = findByC_SIEI(
			companyId, samlIdpEntityId);

		return remove(samlSpIdpConnection);
	}

	/**
	 * Returns the number of saml sp idp connections where companyId = &#63; and samlIdpEntityId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param samlIdpEntityId the saml idp entity ID
	 * @return the number of matching saml sp idp connections
	 */
	@Override
	public int countByC_SIEI(long companyId, String samlIdpEntityId) {
		return _uniquePersistenceFinderByC_SIEI.count(
			finderCache, new Object[] {companyId, samlIdpEntityId});
	}

	public SamlSpIdpConnectionPersistenceImpl() {
		setModelClass(SamlSpIdpConnection.class);

		setModelImplClass(SamlSpIdpConnectionImpl.class);
		setModelPKClass(long.class);

		setTable(SamlSpIdpConnectionTable.INSTANCE);
	}

	/**
	 * Creates a new saml sp idp connection with the primary key. Does not add the saml sp idp connection to the database.
	 *
	 * @param samlSpIdpConnectionId the primary key for the new saml sp idp connection
	 * @return the new saml sp idp connection
	 */
	@Override
	public SamlSpIdpConnection create(long samlSpIdpConnectionId) {
		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setNew(true);
		samlSpIdpConnection.setPrimaryKey(samlSpIdpConnectionId);

		samlSpIdpConnection.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlSpIdpConnection;
	}

	/**
	 * Removes the saml sp idp connection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlSpIdpConnectionId the primary key of the saml sp idp connection
	 * @return the saml sp idp connection that was removed
	 * @throws NoSuchSpIdpConnectionException if a saml sp idp connection with the primary key could not be found
	 */
	@Override
	public SamlSpIdpConnection remove(long samlSpIdpConnectionId)
		throws NoSuchSpIdpConnectionException {

		return remove((Serializable)samlSpIdpConnectionId);
	}

	@Override
	protected SamlSpIdpConnection removeImpl(
		SamlSpIdpConnection samlSpIdpConnection) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlSpIdpConnection)) {
				samlSpIdpConnection = (SamlSpIdpConnection)session.get(
					SamlSpIdpConnectionImpl.class,
					samlSpIdpConnection.getPrimaryKeyObj());
			}

			if (samlSpIdpConnection != null) {
				session.delete(samlSpIdpConnection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlSpIdpConnection != null) {
			clearCache(samlSpIdpConnection);
		}

		return samlSpIdpConnection;
	}

	@Override
	public SamlSpIdpConnection updateImpl(
		SamlSpIdpConnection samlSpIdpConnection) {

		boolean isNew = samlSpIdpConnection.isNew();

		if (!(samlSpIdpConnection instanceof SamlSpIdpConnectionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlSpIdpConnection.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlSpIdpConnection);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlSpIdpConnection proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlSpIdpConnection implementation " +
					samlSpIdpConnection.getClass());
		}

		SamlSpIdpConnectionModelImpl samlSpIdpConnectionModelImpl =
			(SamlSpIdpConnectionModelImpl)samlSpIdpConnection;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (samlSpIdpConnection.getCreateDate() == null)) {
			if (serviceContext == null) {
				samlSpIdpConnection.setCreateDate(date);
			}
			else {
				samlSpIdpConnection.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!samlSpIdpConnectionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				samlSpIdpConnection.setModifiedDate(date);
			}
			else {
				samlSpIdpConnection.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlSpIdpConnection);
			}
			else {
				samlSpIdpConnection = (SamlSpIdpConnection)session.merge(
					samlSpIdpConnection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlSpIdpConnection, false);

		if (isNew) {
			samlSpIdpConnection.setNew(false);
		}

		samlSpIdpConnection.resetOriginalValues();

		return samlSpIdpConnection;
	}

	/**
	 * Returns the saml sp idp connection with the primary key or throws a <code>NoSuchSpIdpConnectionException</code> if it could not be found.
	 *
	 * @param samlSpIdpConnectionId the primary key of the saml sp idp connection
	 * @return the saml sp idp connection
	 * @throws NoSuchSpIdpConnectionException if a saml sp idp connection with the primary key could not be found
	 */
	@Override
	public SamlSpIdpConnection findByPrimaryKey(long samlSpIdpConnectionId)
		throws NoSuchSpIdpConnectionException {

		return findByPrimaryKey((Serializable)samlSpIdpConnectionId);
	}

	/**
	 * Returns the saml sp idp connection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlSpIdpConnectionId the primary key of the saml sp idp connection
	 * @return the saml sp idp connection, or <code>null</code> if a saml sp idp connection with the primary key could not be found
	 */
	@Override
	public SamlSpIdpConnection fetchByPrimaryKey(long samlSpIdpConnectionId) {
		return fetchByPrimaryKey((Serializable)samlSpIdpConnectionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlSpIdpConnectionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLSPIDPCONNECTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlSpIdpConnectionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml sp idp connection persistence.
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
				_SQL_SELECT_SAMLSPIDPCONNECTION_WHERE,
				_SQL_COUNT_SAMLSPIDPCONNECTION_WHERE,
				SamlSpIdpConnectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"samlSpIdpConnection.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SamlSpIdpConnection::getCompanyId));

		_uniquePersistenceFinderByC_SIEI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_SIEI",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "samlIdpEntityId"}, 0, 2, false,
				SamlSpIdpConnection::getCompanyId,
				convertNullFunction(SamlSpIdpConnection::getSamlIdpEntityId)),
			_SQL_SELECT_SAMLSPIDPCONNECTION_WHERE, "",
			new FinderColumn<>(
				"samlSpIdpConnection.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, SamlSpIdpConnection::getCompanyId),
			new FinderColumn<>(
				"samlSpIdpConnection.", "samlIdpEntityId",
				FinderColumn.Type.STRING, "=", true, true,
				SamlSpIdpConnection::getSamlIdpEntityId));

		SamlSpIdpConnectionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlSpIdpConnectionUtil.setPersistence(null);

		entityCache.removeCache(SamlSpIdpConnectionImpl.class.getName());
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
		SamlSpIdpConnectionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLSPIDPCONNECTION =
		"SELECT samlSpIdpConnection FROM SamlSpIdpConnection samlSpIdpConnection";

	private static final String _SQL_SELECT_SAMLSPIDPCONNECTION_WHERE =
		"SELECT samlSpIdpConnection FROM SamlSpIdpConnection samlSpIdpConnection WHERE ";

	private static final String _SQL_COUNT_SAMLSPIDPCONNECTION_WHERE =
		"SELECT COUNT(samlSpIdpConnection) FROM SamlSpIdpConnection samlSpIdpConnection WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlSpIdpConnection exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlSpIdpConnectionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1402828092