/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.saml.persistence.exception.NoSuchIdpSpSessionException;
import com.liferay.saml.persistence.model.SamlIdpSpSession;
import com.liferay.saml.persistence.model.SamlIdpSpSessionTable;
import com.liferay.saml.persistence.model.impl.SamlIdpSpSessionImpl;
import com.liferay.saml.persistence.model.impl.SamlIdpSpSessionModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpSessionUtil;
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
 * The persistence implementation for the saml idp sp session service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlIdpSpSessionPersistence.class)
public class SamlIdpSpSessionPersistenceImpl
	extends BasePersistenceImpl<SamlIdpSpSession, NoSuchIdpSpSessionException>
	implements SamlIdpSpSessionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlIdpSpSessionUtil</code> to access the saml idp sp session persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlIdpSpSessionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SamlIdpSpSession, NoSuchIdpSpSessionException>
			_collectionPersistenceFinderByLtCreateDate;

	/**
	 * Returns all the saml idp sp sessions where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the matching saml idp sp sessions
	 */
	@Override
	public List<SamlIdpSpSession> findByLtCreateDate(Date createDate) {
		return findByLtCreateDate(
			createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml idp sp sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sp sessions
	 * @param end the upper bound of the range of saml idp sp sessions (not inclusive)
	 * @return the range of matching saml idp sp sessions
	 */
	@Override
	public List<SamlIdpSpSession> findByLtCreateDate(
		Date createDate, int start, int end) {

		return findByLtCreateDate(createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml idp sp sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sp sessions
	 * @param end the upper bound of the range of saml idp sp sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml idp sp sessions
	 */
	@Override
	public List<SamlIdpSpSession> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlIdpSpSession> orderByComparator) {

		return findByLtCreateDate(
			createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml idp sp sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sp sessions
	 * @param end the upper bound of the range of saml idp sp sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml idp sp sessions
	 */
	@Override
	public List<SamlIdpSpSession> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlIdpSpSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtCreateDate.find(
			finderCache, new Object[] {createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml idp sp session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sp session
	 * @throws NoSuchIdpSpSessionException if a matching saml idp sp session could not be found
	 */
	@Override
	public SamlIdpSpSession findByLtCreateDate_First(
			Date createDate,
			OrderByComparator<SamlIdpSpSession> orderByComparator)
		throws NoSuchIdpSpSessionException {

		return _collectionPersistenceFinderByLtCreateDate.findFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Returns the first saml idp sp session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sp session, or <code>null</code> if a matching saml idp sp session could not be found
	 */
	@Override
	public SamlIdpSpSession fetchByLtCreateDate_First(
		Date createDate,
		OrderByComparator<SamlIdpSpSession> orderByComparator) {

		return _collectionPersistenceFinderByLtCreateDate.fetchFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Removes all the saml idp sp sessions where createDate &lt; &#63; from the database.
	 *
	 * @param createDate the create date
	 */
	@Override
	public void removeByLtCreateDate(Date createDate) {
		_collectionPersistenceFinderByLtCreateDate.remove(
			finderCache, new Object[] {createDate});
	}

	/**
	 * Returns the number of saml idp sp sessions where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the number of matching saml idp sp sessions
	 */
	@Override
	public int countByLtCreateDate(Date createDate) {
		return _collectionPersistenceFinderByLtCreateDate.count(
			finderCache, new Object[] {createDate});
	}

	private CollectionPersistenceFinder
		<SamlIdpSpSession, NoSuchIdpSpSessionException>
			_collectionPersistenceFinderBySamlIdpSsoSessionId;

	/**
	 * Returns an ordered range of all the saml idp sp sessions where samlIdpSsoSessionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param samlIdpSsoSessionId the saml idp sso session ID
	 * @param start the lower bound of the range of saml idp sp sessions
	 * @param end the upper bound of the range of saml idp sp sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml idp sp sessions
	 */
	@Override
	public List<SamlIdpSpSession> findBySamlIdpSsoSessionId(
		long samlIdpSsoSessionId, int start, int end,
		OrderByComparator<SamlIdpSpSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySamlIdpSsoSessionId.find(
			finderCache, new Object[] {samlIdpSsoSessionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml idp sp session in the ordered set where samlIdpSsoSessionId = &#63;.
	 *
	 * @param samlIdpSsoSessionId the saml idp sso session ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sp session
	 * @throws NoSuchIdpSpSessionException if a matching saml idp sp session could not be found
	 */
	@Override
	public SamlIdpSpSession findBySamlIdpSsoSessionId_First(
			long samlIdpSsoSessionId,
			OrderByComparator<SamlIdpSpSession> orderByComparator)
		throws NoSuchIdpSpSessionException {

		return _collectionPersistenceFinderBySamlIdpSsoSessionId.findFirst(
			finderCache, new Object[] {samlIdpSsoSessionId}, orderByComparator);
	}

	/**
	 * Returns the first saml idp sp session in the ordered set where samlIdpSsoSessionId = &#63;.
	 *
	 * @param samlIdpSsoSessionId the saml idp sso session ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sp session, or <code>null</code> if a matching saml idp sp session could not be found
	 */
	@Override
	public SamlIdpSpSession fetchBySamlIdpSsoSessionId_First(
		long samlIdpSsoSessionId,
		OrderByComparator<SamlIdpSpSession> orderByComparator) {

		return _collectionPersistenceFinderBySamlIdpSsoSessionId.fetchFirst(
			finderCache, new Object[] {samlIdpSsoSessionId}, orderByComparator);
	}

	/**
	 * Removes all the saml idp sp sessions where samlIdpSsoSessionId = &#63; from the database.
	 *
	 * @param samlIdpSsoSessionId the saml idp sso session ID
	 */
	@Override
	public void removeBySamlIdpSsoSessionId(long samlIdpSsoSessionId) {
		_collectionPersistenceFinderBySamlIdpSsoSessionId.remove(
			finderCache, new Object[] {samlIdpSsoSessionId});
	}

	/**
	 * Returns the number of saml idp sp sessions where samlIdpSsoSessionId = &#63;.
	 *
	 * @param samlIdpSsoSessionId the saml idp sso session ID
	 * @return the number of matching saml idp sp sessions
	 */
	@Override
	public int countBySamlIdpSsoSessionId(long samlIdpSsoSessionId) {
		return _collectionPersistenceFinderBySamlIdpSsoSessionId.count(
			finderCache, new Object[] {samlIdpSsoSessionId});
	}

	public SamlIdpSpSessionPersistenceImpl() {
		setModelClass(SamlIdpSpSession.class);

		setModelImplClass(SamlIdpSpSessionImpl.class);
		setModelPKClass(long.class);

		setTable(SamlIdpSpSessionTable.INSTANCE);
	}

	/**
	 * Creates a new saml idp sp session with the primary key. Does not add the saml idp sp session to the database.
	 *
	 * @param samlIdpSpSessionId the primary key for the new saml idp sp session
	 * @return the new saml idp sp session
	 */
	@Override
	public SamlIdpSpSession create(long samlIdpSpSessionId) {
		SamlIdpSpSession samlIdpSpSession = new SamlIdpSpSessionImpl();

		samlIdpSpSession.setNew(true);
		samlIdpSpSession.setPrimaryKey(samlIdpSpSessionId);

		samlIdpSpSession.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlIdpSpSession;
	}

	/**
	 * Removes the saml idp sp session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIdpSpSessionId the primary key of the saml idp sp session
	 * @return the saml idp sp session that was removed
	 * @throws NoSuchIdpSpSessionException if a saml idp sp session with the primary key could not be found
	 */
	@Override
	public SamlIdpSpSession remove(long samlIdpSpSessionId)
		throws NoSuchIdpSpSessionException {

		return remove((Serializable)samlIdpSpSessionId);
	}

	@Override
	protected SamlIdpSpSession removeImpl(SamlIdpSpSession samlIdpSpSession) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlIdpSpSession)) {
				samlIdpSpSession = (SamlIdpSpSession)session.get(
					SamlIdpSpSessionImpl.class,
					samlIdpSpSession.getPrimaryKeyObj());
			}

			if (samlIdpSpSession != null) {
				session.delete(samlIdpSpSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlIdpSpSession != null) {
			clearCache(samlIdpSpSession);
		}

		return samlIdpSpSession;
	}

	@Override
	public SamlIdpSpSession updateImpl(SamlIdpSpSession samlIdpSpSession) {
		boolean isNew = samlIdpSpSession.isNew();

		if (!(samlIdpSpSession instanceof SamlIdpSpSessionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlIdpSpSession.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlIdpSpSession);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlIdpSpSession proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlIdpSpSession implementation " +
					samlIdpSpSession.getClass());
		}

		SamlIdpSpSessionModelImpl samlIdpSpSessionModelImpl =
			(SamlIdpSpSessionModelImpl)samlIdpSpSession;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (samlIdpSpSession.getCreateDate() == null)) {
			if (serviceContext == null) {
				samlIdpSpSession.setCreateDate(date);
			}
			else {
				samlIdpSpSession.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!samlIdpSpSessionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				samlIdpSpSession.setModifiedDate(date);
			}
			else {
				samlIdpSpSession.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlIdpSpSession);
			}
			else {
				samlIdpSpSession = (SamlIdpSpSession)session.merge(
					samlIdpSpSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlIdpSpSession, false);

		if (isNew) {
			samlIdpSpSession.setNew(false);
		}

		samlIdpSpSession.resetOriginalValues();

		return samlIdpSpSession;
	}

	/**
	 * Returns the saml idp sp session with the primary key or throws a <code>NoSuchIdpSpSessionException</code> if it could not be found.
	 *
	 * @param samlIdpSpSessionId the primary key of the saml idp sp session
	 * @return the saml idp sp session
	 * @throws NoSuchIdpSpSessionException if a saml idp sp session with the primary key could not be found
	 */
	@Override
	public SamlIdpSpSession findByPrimaryKey(long samlIdpSpSessionId)
		throws NoSuchIdpSpSessionException {

		return findByPrimaryKey((Serializable)samlIdpSpSessionId);
	}

	/**
	 * Returns the saml idp sp session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIdpSpSessionId the primary key of the saml idp sp session
	 * @return the saml idp sp session, or <code>null</code> if a saml idp sp session with the primary key could not be found
	 */
	@Override
	public SamlIdpSpSession fetchByPrimaryKey(long samlIdpSpSessionId) {
		return fetchByPrimaryKey((Serializable)samlIdpSpSessionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlIdpSpSessionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLIDPSPSESSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlIdpSpSessionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml idp sp session persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLtCreateDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtCreateDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"createDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtCreateDate", new String[] {Date.class.getName()},
					new String[] {"createDate"}, false),
				_SQL_SELECT_SAMLIDPSPSESSION_WHERE,
				_SQL_COUNT_SAMLIDPSPSESSION_WHERE,
				SamlIdpSpSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"samlIdpSpSession.", "createDate", FinderColumn.Type.DATE,
					"<", true, true, SamlIdpSpSession::getCreateDate));

		_collectionPersistenceFinderBySamlIdpSsoSessionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySamlIdpSsoSessionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"samlIdpSsoSessionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySamlIdpSsoSessionId",
					new String[] {Long.class.getName()},
					new String[] {"samlIdpSsoSessionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySamlIdpSsoSessionId",
					new String[] {Long.class.getName()},
					new String[] {"samlIdpSsoSessionId"}, false),
				_SQL_SELECT_SAMLIDPSPSESSION_WHERE,
				_SQL_COUNT_SAMLIDPSPSESSION_WHERE,
				SamlIdpSpSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"samlIdpSpSession.", "samlIdpSsoSessionId",
					FinderColumn.Type.LONG, "=", true, true,
					SamlIdpSpSession::getSamlIdpSsoSessionId));

		SamlIdpSpSessionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlIdpSpSessionUtil.setPersistence(null);

		entityCache.removeCache(SamlIdpSpSessionImpl.class.getName());
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
		SamlIdpSpSessionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLIDPSPSESSION =
		"SELECT samlIdpSpSession FROM SamlIdpSpSession samlIdpSpSession";

	private static final String _SQL_SELECT_SAMLIDPSPSESSION_WHERE =
		"SELECT samlIdpSpSession FROM SamlIdpSpSession samlIdpSpSession WHERE ";

	private static final String _SQL_COUNT_SAMLIDPSPSESSION_WHERE =
		"SELECT COUNT(samlIdpSpSession) FROM SamlIdpSpSession samlIdpSpSession WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlIdpSpSession exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1261930590