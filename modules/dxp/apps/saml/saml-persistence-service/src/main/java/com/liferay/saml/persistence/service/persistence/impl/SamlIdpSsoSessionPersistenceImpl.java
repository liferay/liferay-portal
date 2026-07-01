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
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.saml.persistence.exception.NoSuchIdpSsoSessionException;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.model.SamlIdpSsoSessionTable;
import com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionImpl;
import com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlIdpSsoSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSsoSessionUtil;
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
 * The persistence implementation for the saml idp sso session service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlIdpSsoSessionPersistence.class)
public class SamlIdpSsoSessionPersistenceImpl
	extends BasePersistenceImpl<SamlIdpSsoSession, NoSuchIdpSsoSessionException>
	implements SamlIdpSsoSessionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlIdpSsoSessionUtil</code> to access the saml idp sso session persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlIdpSsoSessionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<SamlIdpSsoSession, NoSuchIdpSsoSessionException>
			_uniquePersistenceFinderByUserId;

	/**
	 * Returns the saml idp sso session where userId = &#63; or throws a <code>NoSuchIdpSsoSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findByUserId(long userId)
		throws NoSuchIdpSsoSessionException {

		return _uniquePersistenceFinderByUserId.find(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the saml idp sso session where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByUserId(
		long userId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUserId.fetch(
			finderCache, new Object[] {userId}, useFinderCache);
	}

	/**
	 * Removes the saml idp sso session where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the saml idp sso session that was removed
	 */
	@Override
	public SamlIdpSsoSession removeByUserId(long userId)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = findByUserId(userId);

		return remove(samlIdpSsoSession);
	}

	/**
	 * Returns the number of saml idp sso sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching saml idp sso sessions
	 */
	@Override
	public int countByUserId(long userId) {
		return _uniquePersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<SamlIdpSsoSession, NoSuchIdpSsoSessionException>
			_collectionPersistenceFinderByLtCreateDate;

	/**
	 * Returns all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(Date createDate) {
		return findByLtCreateDate(
			createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @return the range of matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(
		Date createDate, int start, int end) {

		return findByLtCreateDate(createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlIdpSsoSession> orderByComparator) {

		return findByLtCreateDate(
			createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlIdpSsoSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtCreateDate.find(
			finderCache, new Object[] {createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findByLtCreateDate_First(
			Date createDate,
			OrderByComparator<SamlIdpSsoSession> orderByComparator)
		throws NoSuchIdpSsoSessionException {

		return _collectionPersistenceFinderByLtCreateDate.findFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Returns the first saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByLtCreateDate_First(
		Date createDate,
		OrderByComparator<SamlIdpSsoSession> orderByComparator) {

		return _collectionPersistenceFinderByLtCreateDate.fetchFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Removes all the saml idp sso sessions where createDate &lt; &#63; from the database.
	 *
	 * @param createDate the create date
	 */
	@Override
	public void removeByLtCreateDate(Date createDate) {
		_collectionPersistenceFinderByLtCreateDate.remove(
			finderCache, new Object[] {createDate});
	}

	/**
	 * Returns the number of saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the number of matching saml idp sso sessions
	 */
	@Override
	public int countByLtCreateDate(Date createDate) {
		return _collectionPersistenceFinderByLtCreateDate.count(
			finderCache, new Object[] {createDate});
	}

	private UniquePersistenceFinder
		<SamlIdpSsoSession, NoSuchIdpSsoSessionException>
			_uniquePersistenceFinderBySamlIdpSsoSessionKey;

	/**
	 * Returns the saml idp sso session where samlIdpSsoSessionKey = &#63; or throws a <code>NoSuchIdpSsoSessionException</code> if it could not be found.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findBySamlIdpSsoSessionKey(
			String samlIdpSsoSessionKey)
		throws NoSuchIdpSsoSessionException {

		return _uniquePersistenceFinderBySamlIdpSsoSessionKey.find(
			finderCache, new Object[] {samlIdpSsoSessionKey});
	}

	/**
	 * Returns the saml idp sso session where samlIdpSsoSessionKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchBySamlIdpSsoSessionKey(
		String samlIdpSsoSessionKey, boolean useFinderCache) {

		return _uniquePersistenceFinderBySamlIdpSsoSessionKey.fetch(
			finderCache, new Object[] {samlIdpSsoSessionKey}, useFinderCache);
	}

	/**
	 * Removes the saml idp sso session where samlIdpSsoSessionKey = &#63; from the database.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the saml idp sso session that was removed
	 */
	@Override
	public SamlIdpSsoSession removeBySamlIdpSsoSessionKey(
			String samlIdpSsoSessionKey)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = findBySamlIdpSsoSessionKey(
			samlIdpSsoSessionKey);

		return remove(samlIdpSsoSession);
	}

	/**
	 * Returns the number of saml idp sso sessions where samlIdpSsoSessionKey = &#63;.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the number of matching saml idp sso sessions
	 */
	@Override
	public int countBySamlIdpSsoSessionKey(String samlIdpSsoSessionKey) {
		return _uniquePersistenceFinderBySamlIdpSsoSessionKey.count(
			finderCache, new Object[] {samlIdpSsoSessionKey});
	}

	public SamlIdpSsoSessionPersistenceImpl() {
		setModelClass(SamlIdpSsoSession.class);

		setModelImplClass(SamlIdpSsoSessionImpl.class);
		setModelPKClass(long.class);

		setTable(SamlIdpSsoSessionTable.INSTANCE);
	}

	/**
	 * Creates a new saml idp sso session with the primary key. Does not add the saml idp sso session to the database.
	 *
	 * @param samlIdpSsoSessionId the primary key for the new saml idp sso session
	 * @return the new saml idp sso session
	 */
	@Override
	public SamlIdpSsoSession create(long samlIdpSsoSessionId) {
		SamlIdpSsoSession samlIdpSsoSession = new SamlIdpSsoSessionImpl();

		samlIdpSsoSession.setNew(true);
		samlIdpSsoSession.setPrimaryKey(samlIdpSsoSessionId);

		samlIdpSsoSession.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlIdpSsoSession;
	}

	/**
	 * Removes the saml idp sso session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session that was removed
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession remove(long samlIdpSsoSessionId)
		throws NoSuchIdpSsoSessionException {

		return remove((Serializable)samlIdpSsoSessionId);
	}

	@Override
	protected SamlIdpSsoSession removeImpl(
		SamlIdpSsoSession samlIdpSsoSession) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlIdpSsoSession)) {
				samlIdpSsoSession = (SamlIdpSsoSession)session.get(
					SamlIdpSsoSessionImpl.class,
					samlIdpSsoSession.getPrimaryKeyObj());
			}

			if (samlIdpSsoSession != null) {
				session.delete(samlIdpSsoSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlIdpSsoSession != null) {
			clearCache(samlIdpSsoSession);
		}

		return samlIdpSsoSession;
	}

	@Override
	public SamlIdpSsoSession updateImpl(SamlIdpSsoSession samlIdpSsoSession) {
		boolean isNew = samlIdpSsoSession.isNew();

		if (!(samlIdpSsoSession instanceof SamlIdpSsoSessionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlIdpSsoSession.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlIdpSsoSession);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlIdpSsoSession proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlIdpSsoSession implementation " +
					samlIdpSsoSession.getClass());
		}

		SamlIdpSsoSessionModelImpl samlIdpSsoSessionModelImpl =
			(SamlIdpSsoSessionModelImpl)samlIdpSsoSession;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (samlIdpSsoSession.getCreateDate() == null)) {
			if (serviceContext == null) {
				samlIdpSsoSession.setCreateDate(date);
			}
			else {
				samlIdpSsoSession.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!samlIdpSsoSessionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				samlIdpSsoSession.setModifiedDate(date);
			}
			else {
				samlIdpSsoSession.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlIdpSsoSession);
			}
			else {
				samlIdpSsoSession = (SamlIdpSsoSession)session.merge(
					samlIdpSsoSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlIdpSsoSession, false);

		if (isNew) {
			samlIdpSsoSession.setNew(false);
		}

		samlIdpSsoSession.resetOriginalValues();

		return samlIdpSsoSession;
	}

	/**
	 * Returns the saml idp sso session with the primary key or throws a <code>NoSuchIdpSsoSessionException</code> if it could not be found.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession findByPrimaryKey(long samlIdpSsoSessionId)
		throws NoSuchIdpSsoSessionException {

		return findByPrimaryKey((Serializable)samlIdpSsoSessionId);
	}

	/**
	 * Returns the saml idp sso session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session, or <code>null</code> if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByPrimaryKey(long samlIdpSsoSessionId) {
		return fetchByPrimaryKey((Serializable)samlIdpSsoSessionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlIdpSsoSessionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLIDPSSOSESSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlIdpSsoSessionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml idp sso session persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByUserId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] {Long.class.getName()}, new String[] {"userId"}, 0,
				0, false, SamlIdpSsoSession::getUserId),
			_SQL_SELECT_SAMLIDPSSOSESSION_WHERE, "",
			new FinderColumn<>(
				"samlIdpSsoSession.", "userId", FinderColumn.Type.LONG, "=",
				true, true, SamlIdpSsoSession::getUserId));

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
				_SQL_SELECT_SAMLIDPSSOSESSION_WHERE,
				_SQL_COUNT_SAMLIDPSSOSESSION_WHERE,
				SamlIdpSsoSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"samlIdpSsoSession.", "createDate", FinderColumn.Type.DATE,
					"<", true, true, SamlIdpSsoSession::getCreateDate));

		_uniquePersistenceFinderBySamlIdpSsoSessionKey =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchBySamlIdpSsoSessionKey",
					new String[] {String.class.getName()},
					new String[] {"samlIdpSsoSessionKey"}, 0, 1, false,
					convertNullFunction(
						SamlIdpSsoSession::getSamlIdpSsoSessionKey)),
				_SQL_SELECT_SAMLIDPSSOSESSION_WHERE, "",
				new FinderColumn<>(
					"samlIdpSsoSession.", "samlIdpSsoSessionKey",
					FinderColumn.Type.STRING, "=", true, true,
					SamlIdpSsoSession::getSamlIdpSsoSessionKey));

		SamlIdpSsoSessionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlIdpSsoSessionUtil.setPersistence(null);

		entityCache.removeCache(SamlIdpSsoSessionImpl.class.getName());
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
		SamlIdpSsoSessionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLIDPSSOSESSION =
		"SELECT samlIdpSsoSession FROM SamlIdpSsoSession samlIdpSsoSession";

	private static final String _SQL_SELECT_SAMLIDPSSOSESSION_WHERE =
		"SELECT samlIdpSsoSession FROM SamlIdpSsoSession samlIdpSsoSession WHERE ";

	private static final String _SQL_COUNT_SAMLIDPSSOSESSION_WHERE =
		"SELECT COUNT(samlIdpSsoSession) FROM SamlIdpSsoSession samlIdpSsoSession WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:638400187