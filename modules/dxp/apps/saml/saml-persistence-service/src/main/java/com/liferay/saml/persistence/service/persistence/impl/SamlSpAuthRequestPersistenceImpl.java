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
import com.liferay.saml.persistence.exception.NoSuchSpAuthRequestException;
import com.liferay.saml.persistence.model.SamlSpAuthRequest;
import com.liferay.saml.persistence.model.SamlSpAuthRequestTable;
import com.liferay.saml.persistence.model.impl.SamlSpAuthRequestImpl;
import com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlSpAuthRequestPersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpAuthRequestUtil;
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
 * The persistence implementation for the saml sp auth request service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlSpAuthRequestPersistence.class)
public class SamlSpAuthRequestPersistenceImpl
	extends BasePersistenceImpl<SamlSpAuthRequest, NoSuchSpAuthRequestException>
	implements SamlSpAuthRequestPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlSpAuthRequestUtil</code> to access the saml sp auth request persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlSpAuthRequestImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SamlSpAuthRequest, NoSuchSpAuthRequestException>
			_collectionPersistenceFinderByLtCreateDate;

	/**
	 * Returns all the saml sp auth requests where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the matching saml sp auth requests
	 */
	@Override
	public List<SamlSpAuthRequest> findByLtCreateDate(Date createDate) {
		return findByLtCreateDate(
			createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml sp auth requests where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpAuthRequestModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml sp auth requests
	 * @param end the upper bound of the range of saml sp auth requests (not inclusive)
	 * @return the range of matching saml sp auth requests
	 */
	@Override
	public List<SamlSpAuthRequest> findByLtCreateDate(
		Date createDate, int start, int end) {

		return findByLtCreateDate(createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml sp auth requests where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpAuthRequestModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml sp auth requests
	 * @param end the upper bound of the range of saml sp auth requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml sp auth requests
	 */
	@Override
	public List<SamlSpAuthRequest> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlSpAuthRequest> orderByComparator) {

		return findByLtCreateDate(
			createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml sp auth requests where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlSpAuthRequestModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml sp auth requests
	 * @param end the upper bound of the range of saml sp auth requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml sp auth requests
	 */
	@Override
	public List<SamlSpAuthRequest> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlSpAuthRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtCreateDate.find(
			finderCache, new Object[] {createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml sp auth request in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp auth request
	 * @throws NoSuchSpAuthRequestException if a matching saml sp auth request could not be found
	 */
	@Override
	public SamlSpAuthRequest findByLtCreateDate_First(
			Date createDate,
			OrderByComparator<SamlSpAuthRequest> orderByComparator)
		throws NoSuchSpAuthRequestException {

		return _collectionPersistenceFinderByLtCreateDate.findFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Returns the first saml sp auth request in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml sp auth request, or <code>null</code> if a matching saml sp auth request could not be found
	 */
	@Override
	public SamlSpAuthRequest fetchByLtCreateDate_First(
		Date createDate,
		OrderByComparator<SamlSpAuthRequest> orderByComparator) {

		return _collectionPersistenceFinderByLtCreateDate.fetchFirst(
			finderCache, new Object[] {createDate}, orderByComparator);
	}

	/**
	 * Removes all the saml sp auth requests where createDate &lt; &#63; from the database.
	 *
	 * @param createDate the create date
	 */
	@Override
	public void removeByLtCreateDate(Date createDate) {
		_collectionPersistenceFinderByLtCreateDate.remove(
			finderCache, new Object[] {createDate});
	}

	/**
	 * Returns the number of saml sp auth requests where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the number of matching saml sp auth requests
	 */
	@Override
	public int countByLtCreateDate(Date createDate) {
		return _collectionPersistenceFinderByLtCreateDate.count(
			finderCache, new Object[] {createDate});
	}

	private UniquePersistenceFinder
		<SamlSpAuthRequest, NoSuchSpAuthRequestException>
			_uniquePersistenceFinderBySIEI_SSARK;

	/**
	 * Returns the saml sp auth request where samlIdpEntityId = &#63; and samlSpAuthRequestKey = &#63; or throws a <code>NoSuchSpAuthRequestException</code> if it could not be found.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlSpAuthRequestKey the saml sp auth request key
	 * @return the matching saml sp auth request
	 * @throws NoSuchSpAuthRequestException if a matching saml sp auth request could not be found
	 */
	@Override
	public SamlSpAuthRequest findBySIEI_SSARK(
			String samlIdpEntityId, String samlSpAuthRequestKey)
		throws NoSuchSpAuthRequestException {

		return _uniquePersistenceFinderBySIEI_SSARK.find(
			finderCache, new Object[] {samlIdpEntityId, samlSpAuthRequestKey});
	}

	/**
	 * Returns the saml sp auth request where samlIdpEntityId = &#63; and samlSpAuthRequestKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlSpAuthRequestKey the saml sp auth request key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml sp auth request, or <code>null</code> if a matching saml sp auth request could not be found
	 */
	@Override
	public SamlSpAuthRequest fetchBySIEI_SSARK(
		String samlIdpEntityId, String samlSpAuthRequestKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderBySIEI_SSARK.fetch(
			finderCache, new Object[] {samlIdpEntityId, samlSpAuthRequestKey},
			useFinderCache);
	}

	/**
	 * Removes the saml sp auth request where samlIdpEntityId = &#63; and samlSpAuthRequestKey = &#63; from the database.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlSpAuthRequestKey the saml sp auth request key
	 * @return the saml sp auth request that was removed
	 */
	@Override
	public SamlSpAuthRequest removeBySIEI_SSARK(
			String samlIdpEntityId, String samlSpAuthRequestKey)
		throws NoSuchSpAuthRequestException {

		SamlSpAuthRequest samlSpAuthRequest = findBySIEI_SSARK(
			samlIdpEntityId, samlSpAuthRequestKey);

		return remove(samlSpAuthRequest);
	}

	/**
	 * Returns the number of saml sp auth requests where samlIdpEntityId = &#63; and samlSpAuthRequestKey = &#63;.
	 *
	 * @param samlIdpEntityId the saml idp entity ID
	 * @param samlSpAuthRequestKey the saml sp auth request key
	 * @return the number of matching saml sp auth requests
	 */
	@Override
	public int countBySIEI_SSARK(
		String samlIdpEntityId, String samlSpAuthRequestKey) {

		return _uniquePersistenceFinderBySIEI_SSARK.count(
			finderCache, new Object[] {samlIdpEntityId, samlSpAuthRequestKey});
	}

	public SamlSpAuthRequestPersistenceImpl() {
		setModelClass(SamlSpAuthRequest.class);

		setModelImplClass(SamlSpAuthRequestImpl.class);
		setModelPKClass(long.class);

		setTable(SamlSpAuthRequestTable.INSTANCE);
	}

	/**
	 * Creates a new saml sp auth request with the primary key. Does not add the saml sp auth request to the database.
	 *
	 * @param samlSpAuthnRequestId the primary key for the new saml sp auth request
	 * @return the new saml sp auth request
	 */
	@Override
	public SamlSpAuthRequest create(long samlSpAuthnRequestId) {
		SamlSpAuthRequest samlSpAuthRequest = new SamlSpAuthRequestImpl();

		samlSpAuthRequest.setNew(true);
		samlSpAuthRequest.setPrimaryKey(samlSpAuthnRequestId);

		samlSpAuthRequest.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlSpAuthRequest;
	}

	/**
	 * Removes the saml sp auth request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request that was removed
	 * @throws NoSuchSpAuthRequestException if a saml sp auth request with the primary key could not be found
	 */
	@Override
	public SamlSpAuthRequest remove(long samlSpAuthnRequestId)
		throws NoSuchSpAuthRequestException {

		return remove((Serializable)samlSpAuthnRequestId);
	}

	@Override
	protected SamlSpAuthRequest removeImpl(
		SamlSpAuthRequest samlSpAuthRequest) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlSpAuthRequest)) {
				samlSpAuthRequest = (SamlSpAuthRequest)session.get(
					SamlSpAuthRequestImpl.class,
					samlSpAuthRequest.getPrimaryKeyObj());
			}

			if (samlSpAuthRequest != null) {
				session.delete(samlSpAuthRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlSpAuthRequest != null) {
			clearCache(samlSpAuthRequest);
		}

		return samlSpAuthRequest;
	}

	@Override
	public SamlSpAuthRequest updateImpl(SamlSpAuthRequest samlSpAuthRequest) {
		boolean isNew = samlSpAuthRequest.isNew();

		if (!(samlSpAuthRequest instanceof SamlSpAuthRequestModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlSpAuthRequest.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlSpAuthRequest);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlSpAuthRequest proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlSpAuthRequest implementation " +
					samlSpAuthRequest.getClass());
		}

		SamlSpAuthRequestModelImpl samlSpAuthRequestModelImpl =
			(SamlSpAuthRequestModelImpl)samlSpAuthRequest;

		if (isNew && (samlSpAuthRequest.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				samlSpAuthRequest.setCreateDate(date);
			}
			else {
				samlSpAuthRequest.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlSpAuthRequest);
			}
			else {
				samlSpAuthRequest = (SamlSpAuthRequest)session.merge(
					samlSpAuthRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlSpAuthRequest, false);

		if (isNew) {
			samlSpAuthRequest.setNew(false);
		}

		samlSpAuthRequest.resetOriginalValues();

		return samlSpAuthRequest;
	}

	/**
	 * Returns the saml sp auth request with the primary key or throws a <code>NoSuchSpAuthRequestException</code> if it could not be found.
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request
	 * @throws NoSuchSpAuthRequestException if a saml sp auth request with the primary key could not be found
	 */
	@Override
	public SamlSpAuthRequest findByPrimaryKey(long samlSpAuthnRequestId)
		throws NoSuchSpAuthRequestException {

		return findByPrimaryKey((Serializable)samlSpAuthnRequestId);
	}

	/**
	 * Returns the saml sp auth request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request, or <code>null</code> if a saml sp auth request with the primary key could not be found
	 */
	@Override
	public SamlSpAuthRequest fetchByPrimaryKey(long samlSpAuthnRequestId) {
		return fetchByPrimaryKey((Serializable)samlSpAuthnRequestId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlSpAuthnRequestId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLSPAUTHREQUEST;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlSpAuthRequestModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml sp auth request persistence.
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
				_SQL_SELECT_SAMLSPAUTHREQUEST_WHERE,
				_SQL_COUNT_SAMLSPAUTHREQUEST_WHERE,
				SamlSpAuthRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"samlSpAuthRequest.", "createDate", FinderColumn.Type.DATE,
					"<", true, true, SamlSpAuthRequest::getCreateDate));

		_uniquePersistenceFinderBySIEI_SSARK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchBySIEI_SSARK",
				new String[] {String.class.getName(), String.class.getName()},
				new String[] {"samlIdpEntityId", "samlSpAuthRequestKey"}, 0, 3,
				false,
				convertNullFunction(SamlSpAuthRequest::getSamlIdpEntityId),
				convertNullFunction(
					SamlSpAuthRequest::getSamlSpAuthRequestKey)),
			_SQL_SELECT_SAMLSPAUTHREQUEST_WHERE, "",
			new FinderColumn<>(
				"samlSpAuthRequest.", "samlIdpEntityId",
				FinderColumn.Type.STRING, "=", true, true,
				SamlSpAuthRequest::getSamlIdpEntityId),
			new FinderColumn<>(
				"samlSpAuthRequest.", "samlSpAuthRequestKey",
				FinderColumn.Type.STRING, "=", true, true,
				SamlSpAuthRequest::getSamlSpAuthRequestKey));

		SamlSpAuthRequestUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlSpAuthRequestUtil.setPersistence(null);

		entityCache.removeCache(SamlSpAuthRequestImpl.class.getName());
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
		SamlSpAuthRequestModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLSPAUTHREQUEST =
		"SELECT samlSpAuthRequest FROM SamlSpAuthRequest samlSpAuthRequest";

	private static final String _SQL_SELECT_SAMLSPAUTHREQUEST_WHERE =
		"SELECT samlSpAuthRequest FROM SamlSpAuthRequest samlSpAuthRequest WHERE ";

	private static final String _SQL_COUNT_SAMLSPAUTHREQUEST_WHERE =
		"SELECT COUNT(samlSpAuthRequest) FROM SamlSpAuthRequest samlSpAuthRequest WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlSpAuthRequest exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlSpAuthRequestPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1534247722