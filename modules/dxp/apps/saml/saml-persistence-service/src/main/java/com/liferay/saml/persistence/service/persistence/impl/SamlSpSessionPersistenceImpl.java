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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.saml.persistence.exception.NoSuchSpSessionException;
import com.liferay.saml.persistence.model.SamlSpSession;
import com.liferay.saml.persistence.model.SamlSpSessionTable;
import com.liferay.saml.persistence.model.impl.SamlSpSessionImpl;
import com.liferay.saml.persistence.model.impl.SamlSpSessionModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlSpSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpSessionUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

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
 * The persistence implementation for the saml sp session service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlSpSessionPersistence.class)
public class SamlSpSessionPersistenceImpl
	extends BasePersistenceImpl<SamlSpSession, NoSuchSpSessionException>
	implements SamlSpSessionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlSpSessionUtil</code> to access the saml sp session persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlSpSessionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SamlSpSession, NoSuchSpSessionException>
		_collectionPersistenceFinderBySamlPeerBindingId;

	/**
	 * Returns an ordered range of all the saml sp sessions where samlPeerBindingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param samlPeerBindingId the saml peer binding ID
	 * @param start the lower bound of the range of saml sp sessions
	 * @param end the upper bound of the range of saml sp sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml sp sessions
	 */
	@Override
	public List<SamlSpSession> findBySamlPeerBindingId(
		long samlPeerBindingId, int start, int end,
		OrderByComparator<SamlSpSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySamlPeerBindingId.find(
			finderCache, new Object[] {samlPeerBindingId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml sp session in the ordered set where samlPeerBindingId = &#63;.
	 *
	 * @param samlPeerBindingId the saml peer binding ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp session
	 * @throws NoSuchSpSessionException if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession findBySamlPeerBindingId_First(
			long samlPeerBindingId,
			OrderByComparator<SamlSpSession> orderByComparator)
		throws NoSuchSpSessionException {

		return _collectionPersistenceFinderBySamlPeerBindingId.findFirst(
			finderCache, new Object[] {samlPeerBindingId}, orderByComparator);
	}

	/**
	 * Returns the first saml sp session in the ordered set where samlPeerBindingId = &#63;.
	 *
	 * @param samlPeerBindingId the saml peer binding ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp session, or <code>null</code> if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession fetchBySamlPeerBindingId_First(
		long samlPeerBindingId,
		OrderByComparator<SamlSpSession> orderByComparator) {

		return _collectionPersistenceFinderBySamlPeerBindingId.fetchFirst(
			finderCache, new Object[] {samlPeerBindingId}, orderByComparator);
	}

	/**
	 * Removes all the saml sp sessions where samlPeerBindingId = &#63; from the database.
	 *
	 * @param samlPeerBindingId the saml peer binding ID
	 */
	@Override
	public void removeBySamlPeerBindingId(long samlPeerBindingId) {
		_collectionPersistenceFinderBySamlPeerBindingId.remove(
			finderCache, new Object[] {samlPeerBindingId});
	}

	/**
	 * Returns the number of saml sp sessions where samlPeerBindingId = &#63;.
	 *
	 * @param samlPeerBindingId the saml peer binding ID
	 * @return the number of matching saml sp sessions
	 */
	@Override
	public int countBySamlPeerBindingId(long samlPeerBindingId) {
		return _collectionPersistenceFinderBySamlPeerBindingId.count(
			finderCache, new Object[] {samlPeerBindingId});
	}

	private UniquePersistenceFinder<SamlSpSession, NoSuchSpSessionException>
		_uniquePersistenceFinderByJSessionId;

	/**
	 * Returns the saml sp session where jSessionId = &#63; or throws a <code>NoSuchSpSessionException</code> if it could not be found.
	 *
	 * @param jSessionId the j session ID
	 * @return the matching saml sp session
	 * @throws NoSuchSpSessionException if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession findByJSessionId(String jSessionId)
		throws NoSuchSpSessionException {

		return _uniquePersistenceFinderByJSessionId.find(
			finderCache, new Object[] {jSessionId});
	}

	/**
	 * Returns the saml sp session where jSessionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param jSessionId the j session ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml sp session, or <code>null</code> if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession fetchByJSessionId(
		String jSessionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByJSessionId.fetch(
			finderCache, new Object[] {jSessionId}, useFinderCache);
	}

	/**
	 * Removes the saml sp session where jSessionId = &#63; from the database.
	 *
	 * @param jSessionId the j session ID
	 * @return the saml sp session that was removed
	 */
	@Override
	public SamlSpSession removeByJSessionId(String jSessionId)
		throws NoSuchSpSessionException {

		SamlSpSession samlSpSession = findByJSessionId(jSessionId);

		return remove(samlSpSession);
	}

	/**
	 * Returns the number of saml sp sessions where jSessionId = &#63;.
	 *
	 * @param jSessionId the j session ID
	 * @return the number of matching saml sp sessions
	 */
	@Override
	public int countByJSessionId(String jSessionId) {
		return _uniquePersistenceFinderByJSessionId.count(
			finderCache, new Object[] {jSessionId});
	}

	private UniquePersistenceFinder<SamlSpSession, NoSuchSpSessionException>
		_uniquePersistenceFinderBySamlSpSessionKey;

	/**
	 * Returns the saml sp session where samlSpSessionKey = &#63; or throws a <code>NoSuchSpSessionException</code> if it could not be found.
	 *
	 * @param samlSpSessionKey the saml sp session key
	 * @return the matching saml sp session
	 * @throws NoSuchSpSessionException if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession findBySamlSpSessionKey(String samlSpSessionKey)
		throws NoSuchSpSessionException {

		return _uniquePersistenceFinderBySamlSpSessionKey.find(
			finderCache, new Object[] {samlSpSessionKey});
	}

	/**
	 * Returns the saml sp session where samlSpSessionKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlSpSessionKey the saml sp session key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml sp session, or <code>null</code> if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession fetchBySamlSpSessionKey(
		String samlSpSessionKey, boolean useFinderCache) {

		return _uniquePersistenceFinderBySamlSpSessionKey.fetch(
			finderCache, new Object[] {samlSpSessionKey}, useFinderCache);
	}

	/**
	 * Removes the saml sp session where samlSpSessionKey = &#63; from the database.
	 *
	 * @param samlSpSessionKey the saml sp session key
	 * @return the saml sp session that was removed
	 */
	@Override
	public SamlSpSession removeBySamlSpSessionKey(String samlSpSessionKey)
		throws NoSuchSpSessionException {

		SamlSpSession samlSpSession = findBySamlSpSessionKey(samlSpSessionKey);

		return remove(samlSpSession);
	}

	/**
	 * Returns the number of saml sp sessions where samlSpSessionKey = &#63;.
	 *
	 * @param samlSpSessionKey the saml sp session key
	 * @return the number of matching saml sp sessions
	 */
	@Override
	public int countBySamlSpSessionKey(String samlSpSessionKey) {
		return _uniquePersistenceFinderBySamlSpSessionKey.count(
			finderCache, new Object[] {samlSpSessionKey});
	}

	private CollectionPersistenceFinder<SamlSpSession, NoSuchSpSessionException>
		_collectionPersistenceFinderByC_SI;

	/**
	 * Returns an ordered range of all the saml sp sessions where companyId = &#63; and sessionIndex = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sessionIndex the session index
	 * @param start the lower bound of the range of saml sp sessions
	 * @param end the upper bound of the range of saml sp sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml sp sessions
	 */
	@Override
	public List<SamlSpSession> findByC_SI(
		long companyId, String sessionIndex, int start, int end,
		OrderByComparator<SamlSpSession> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_SI.find(
			finderCache, new Object[] {companyId, sessionIndex}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml sp session in the ordered set where companyId = &#63; and sessionIndex = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sessionIndex the session index
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp session
	 * @throws NoSuchSpSessionException if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession findByC_SI_First(
			long companyId, String sessionIndex,
			OrderByComparator<SamlSpSession> orderByComparator)
		throws NoSuchSpSessionException {

		return _collectionPersistenceFinderByC_SI.findFirst(
			finderCache, new Object[] {companyId, sessionIndex},
			orderByComparator);
	}

	/**
	 * Returns the first saml sp session in the ordered set where companyId = &#63; and sessionIndex = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sessionIndex the session index
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp session, or <code>null</code> if a matching saml sp session could not be found
	 */
	@Override
	public SamlSpSession fetchByC_SI_First(
		long companyId, String sessionIndex,
		OrderByComparator<SamlSpSession> orderByComparator) {

		return _collectionPersistenceFinderByC_SI.fetchFirst(
			finderCache, new Object[] {companyId, sessionIndex},
			orderByComparator);
	}

	/**
	 * Removes all the saml sp sessions where companyId = &#63; and sessionIndex = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sessionIndex the session index
	 */
	@Override
	public void removeByC_SI(long companyId, String sessionIndex) {
		_collectionPersistenceFinderByC_SI.remove(
			finderCache, new Object[] {companyId, sessionIndex});
	}

	/**
	 * Returns the number of saml sp sessions where companyId = &#63; and sessionIndex = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sessionIndex the session index
	 * @return the number of matching saml sp sessions
	 */
	@Override
	public int countByC_SI(long companyId, String sessionIndex) {
		return _collectionPersistenceFinderByC_SI.count(
			finderCache, new Object[] {companyId, sessionIndex});
	}

	public SamlSpSessionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("terminated", "terminated_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SamlSpSession.class);

		setModelImplClass(SamlSpSessionImpl.class);
		setModelPKClass(long.class);

		setTable(SamlSpSessionTable.INSTANCE);
	}

	/**
	 * Creates a new saml sp session with the primary key. Does not add the saml sp session to the database.
	 *
	 * @param samlSpSessionId the primary key for the new saml sp session
	 * @return the new saml sp session
	 */
	@Override
	public SamlSpSession create(long samlSpSessionId) {
		SamlSpSession samlSpSession = new SamlSpSessionImpl();

		samlSpSession.setNew(true);
		samlSpSession.setPrimaryKey(samlSpSessionId);

		samlSpSession.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlSpSession;
	}

	/**
	 * Removes the saml sp session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlSpSessionId the primary key of the saml sp session
	 * @return the saml sp session that was removed
	 * @throws NoSuchSpSessionException if a saml sp session with the primary key could not be found
	 */
	@Override
	public SamlSpSession remove(long samlSpSessionId)
		throws NoSuchSpSessionException {

		return remove((Serializable)samlSpSessionId);
	}

	@Override
	protected SamlSpSession removeImpl(SamlSpSession samlSpSession) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlSpSession)) {
				samlSpSession = (SamlSpSession)session.get(
					SamlSpSessionImpl.class, samlSpSession.getPrimaryKeyObj());
			}

			if (samlSpSession != null) {
				session.delete(samlSpSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlSpSession != null) {
			clearCache(samlSpSession);
		}

		return samlSpSession;
	}

	@Override
	public SamlSpSession updateImpl(SamlSpSession samlSpSession) {
		boolean isNew = samlSpSession.isNew();

		if (!(samlSpSession instanceof SamlSpSessionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlSpSession.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlSpSession);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlSpSession proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlSpSession implementation " +
					samlSpSession.getClass());
		}

		SamlSpSessionModelImpl samlSpSessionModelImpl =
			(SamlSpSessionModelImpl)samlSpSession;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (samlSpSession.getCreateDate() == null)) {
			if (serviceContext == null) {
				samlSpSession.setCreateDate(date);
			}
			else {
				samlSpSession.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!samlSpSessionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				samlSpSession.setModifiedDate(date);
			}
			else {
				samlSpSession.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlSpSession);
			}
			else {
				samlSpSession = (SamlSpSession)session.merge(samlSpSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlSpSession, false);

		if (isNew) {
			samlSpSession.setNew(false);
		}

		samlSpSession.resetOriginalValues();

		return samlSpSession;
	}

	/**
	 * Returns the saml sp session with the primary key or throws a <code>NoSuchSpSessionException</code> if it could not be found.
	 *
	 * @param samlSpSessionId the primary key of the saml sp session
	 * @return the saml sp session
	 * @throws NoSuchSpSessionException if a saml sp session with the primary key could not be found
	 */
	@Override
	public SamlSpSession findByPrimaryKey(long samlSpSessionId)
		throws NoSuchSpSessionException {

		return findByPrimaryKey((Serializable)samlSpSessionId);
	}

	/**
	 * Returns the saml sp session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlSpSessionId the primary key of the saml sp session
	 * @return the saml sp session, or <code>null</code> if a saml sp session with the primary key could not be found
	 */
	@Override
	public SamlSpSession fetchByPrimaryKey(long samlSpSessionId) {
		return fetchByPrimaryKey((Serializable)samlSpSessionId);
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
		return "samlSpSessionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLSPSESSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlSpSessionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml sp session persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderBySamlPeerBindingId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySamlPeerBindingId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"samlPeerBindingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySamlPeerBindingId",
					new String[] {Long.class.getName()},
					new String[] {"samlPeerBindingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySamlPeerBindingId",
					new String[] {Long.class.getName()},
					new String[] {"samlPeerBindingId"}, false),
				_SQL_SELECT_SAMLSPSESSION_WHERE, _SQL_COUNT_SAMLSPSESSION_WHERE,
				SamlSpSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"samlSpSession.", "samlPeerBindingId",
					FinderColumn.Type.LONG, "=", true, true,
					SamlSpSession::getSamlPeerBindingId));

		_uniquePersistenceFinderByJSessionId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByJSessionId",
				new String[] {String.class.getName()},
				new String[] {"jSessionId"}, 0, 1, false,
				convertNullFunction(SamlSpSession::getJSessionId)),
			_SQL_SELECT_SAMLSPSESSION_WHERE, "",
			new FinderColumn<>(
				"samlSpSession.", "jSessionId", FinderColumn.Type.STRING, "=",
				true, true, SamlSpSession::getJSessionId));

		_uniquePersistenceFinderBySamlSpSessionKey =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchBySamlSpSessionKey",
					new String[] {String.class.getName()},
					new String[] {"samlSpSessionKey"}, 0, 1, false,
					convertNullFunction(SamlSpSession::getSamlSpSessionKey)),
				_SQL_SELECT_SAMLSPSESSION_WHERE, "",
				new FinderColumn<>(
					"samlSpSession.", "samlSpSessionKey",
					FinderColumn.Type.STRING, "=", true, true,
					SamlSpSession::getSamlSpSessionKey));

		_collectionPersistenceFinderByC_SI = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_SI",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "sessionIndex"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_SI",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "sessionIndex"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_SI",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "sessionIndex"}, 0, 2, false, null),
			_SQL_SELECT_SAMLSPSESSION_WHERE, _SQL_COUNT_SAMLSPSESSION_WHERE,
			SamlSpSessionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"samlSpSession.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SamlSpSession::getCompanyId),
			new FinderColumn<>(
				"samlSpSession.", "sessionIndex", FinderColumn.Type.STRING, "=",
				true, true, SamlSpSession::getSessionIndex));

		SamlSpSessionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlSpSessionUtil.setPersistence(null);

		entityCache.removeCache(SamlSpSessionImpl.class.getName());
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
		SamlSpSessionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLSPSESSION =
		"SELECT samlSpSession FROM SamlSpSession samlSpSession";

	private static final String _SQL_SELECT_SAMLSPSESSION_WHERE =
		"SELECT samlSpSession FROM SamlSpSession samlSpSession WHERE ";

	private static final String _SQL_COUNT_SAMLSPSESSION_WHERE =
		"SELECT COUNT(samlSpSession) FROM SamlSpSession samlSpSession WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlSpSession exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlSpSessionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"terminated"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:449104256