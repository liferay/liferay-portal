/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.NoSuchCPDAvailabilityEstimateException;
import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.model.CPDAvailabilityEstimateTable;
import com.liferay.commerce.model.impl.CPDAvailabilityEstimateImpl;
import com.liferay.commerce.model.impl.CPDAvailabilityEstimateModelImpl;
import com.liferay.commerce.service.persistence.CPDAvailabilityEstimatePersistence;
import com.liferay.commerce.service.persistence.CPDAvailabilityEstimateUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

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
 * The persistence implementation for the cpd availability estimate service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CPDAvailabilityEstimatePersistence.class)
public class CPDAvailabilityEstimatePersistenceImpl
	extends BasePersistenceImpl
		<CPDAvailabilityEstimate, NoSuchCPDAvailabilityEstimateException>
	implements CPDAvailabilityEstimatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDAvailabilityEstimateUtil</code> to access the cpd availability estimate persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDAvailabilityEstimateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDAvailabilityEstimate, NoSuchCPDAvailabilityEstimateException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cpd availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd availability estimates
	 * @param end the upper bound of the range of cpd availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd availability estimates
	 */
	@Override
	public List<CPDAvailabilityEstimate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDAvailabilityEstimate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cpd availability estimate in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd availability estimate
	 * @throws NoSuchCPDAvailabilityEstimateException if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate findByUuid_First(
			String uuid,
			OrderByComparator<CPDAvailabilityEstimate> orderByComparator)
		throws NoSuchCPDAvailabilityEstimateException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cpd availability estimate in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd availability estimate, or <code>null</code> if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDAvailabilityEstimate> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cpd availability estimates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cpd availability estimates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cpd availability estimates
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CPDAvailabilityEstimate, NoSuchCPDAvailabilityEstimateException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cpd availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd availability estimates
	 * @param end the upper bound of the range of cpd availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd availability estimates
	 */
	@Override
	public List<CPDAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDAvailabilityEstimate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd availability estimate in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd availability estimate
	 * @throws NoSuchCPDAvailabilityEstimateException if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDAvailabilityEstimate> orderByComparator)
		throws NoSuchCPDAvailabilityEstimateException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cpd availability estimate in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd availability estimate, or <code>null</code> if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDAvailabilityEstimate> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cpd availability estimates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of cpd availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cpd availability estimates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDAvailabilityEstimate, NoSuchCPDAvailabilityEstimateException>
			_collectionPersistenceFinderByCommerceAvailabilityEstimateId;

	/**
	 * Returns an ordered range of all the cpd availability estimates where commerceAvailabilityEstimateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID
	 * @param start the lower bound of the range of cpd availability estimates
	 * @param end the upper bound of the range of cpd availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd availability estimates
	 */
	@Override
	public List<CPDAvailabilityEstimate> findByCommerceAvailabilityEstimateId(
		long commerceAvailabilityEstimateId, int start, int end,
		OrderByComparator<CPDAvailabilityEstimate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceAvailabilityEstimateId.
			find(
				finderCache, new Object[] {commerceAvailabilityEstimateId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd availability estimate in the ordered set where commerceAvailabilityEstimateId = &#63;.
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd availability estimate
	 * @throws NoSuchCPDAvailabilityEstimateException if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate findByCommerceAvailabilityEstimateId_First(
			long commerceAvailabilityEstimateId,
			OrderByComparator<CPDAvailabilityEstimate> orderByComparator)
		throws NoSuchCPDAvailabilityEstimateException {

		return _collectionPersistenceFinderByCommerceAvailabilityEstimateId.
			findFirst(
				finderCache, new Object[] {commerceAvailabilityEstimateId},
				orderByComparator);
	}

	/**
	 * Returns the first cpd availability estimate in the ordered set where commerceAvailabilityEstimateId = &#63;.
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd availability estimate, or <code>null</code> if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate fetchByCommerceAvailabilityEstimateId_First(
		long commerceAvailabilityEstimateId,
		OrderByComparator<CPDAvailabilityEstimate> orderByComparator) {

		return _collectionPersistenceFinderByCommerceAvailabilityEstimateId.
			fetchFirst(
				finderCache, new Object[] {commerceAvailabilityEstimateId},
				orderByComparator);
	}

	/**
	 * Removes all the cpd availability estimates where commerceAvailabilityEstimateId = &#63; from the database.
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID
	 */
	@Override
	public void removeByCommerceAvailabilityEstimateId(
		long commerceAvailabilityEstimateId) {

		_collectionPersistenceFinderByCommerceAvailabilityEstimateId.remove(
			finderCache, new Object[] {commerceAvailabilityEstimateId});
	}

	/**
	 * Returns the number of cpd availability estimates where commerceAvailabilityEstimateId = &#63;.
	 *
	 * @param commerceAvailabilityEstimateId the commerce availability estimate ID
	 * @return the number of matching cpd availability estimates
	 */
	@Override
	public int countByCommerceAvailabilityEstimateId(
		long commerceAvailabilityEstimateId) {

		return _collectionPersistenceFinderByCommerceAvailabilityEstimateId.
			count(finderCache, new Object[] {commerceAvailabilityEstimateId});
	}

	private UniquePersistenceFinder
		<CPDAvailabilityEstimate, NoSuchCPDAvailabilityEstimateException>
			_uniquePersistenceFinderByCProductId;

	/**
	 * Returns the cpd availability estimate where CProductId = &#63; or throws a <code>NoSuchCPDAvailabilityEstimateException</code> if it could not be found.
	 *
	 * @param CProductId the c product ID
	 * @return the matching cpd availability estimate
	 * @throws NoSuchCPDAvailabilityEstimateException if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate findByCProductId(long CProductId)
		throws NoSuchCPDAvailabilityEstimateException {

		return _uniquePersistenceFinderByCProductId.find(
			finderCache, new Object[] {CProductId});
	}

	/**
	 * Returns the cpd availability estimate where CProductId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CProductId the c product ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cpd availability estimate, or <code>null</code> if a matching cpd availability estimate could not be found
	 */
	@Override
	public CPDAvailabilityEstimate fetchByCProductId(
		long CProductId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCProductId.fetch(
			finderCache, new Object[] {CProductId}, useFinderCache);
	}

	/**
	 * Removes the cpd availability estimate where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @return the cpd availability estimate that was removed
	 */
	@Override
	public CPDAvailabilityEstimate removeByCProductId(long CProductId)
		throws NoSuchCPDAvailabilityEstimateException {

		CPDAvailabilityEstimate cpdAvailabilityEstimate = findByCProductId(
			CProductId);

		return remove(cpdAvailabilityEstimate);
	}

	/**
	 * Returns the number of cpd availability estimates where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cpd availability estimates
	 */
	@Override
	public int countByCProductId(long CProductId) {
		return _uniquePersistenceFinderByCProductId.count(
			finderCache, new Object[] {CProductId});
	}

	public CPDAvailabilityEstimatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDAvailabilityEstimate.class);

		setModelImplClass(CPDAvailabilityEstimateImpl.class);
		setModelPKClass(long.class);

		setTable(CPDAvailabilityEstimateTable.INSTANCE);
	}

	/**
	 * Creates a new cpd availability estimate with the primary key. Does not add the cpd availability estimate to the database.
	 *
	 * @param CPDAvailabilityEstimateId the primary key for the new cpd availability estimate
	 * @return the new cpd availability estimate
	 */
	@Override
	public CPDAvailabilityEstimate create(long CPDAvailabilityEstimateId) {
		CPDAvailabilityEstimate cpdAvailabilityEstimate =
			new CPDAvailabilityEstimateImpl();

		cpdAvailabilityEstimate.setNew(true);
		cpdAvailabilityEstimate.setPrimaryKey(CPDAvailabilityEstimateId);

		String uuid = PortalUUIDUtil.generate();

		cpdAvailabilityEstimate.setUuid(uuid);

		cpdAvailabilityEstimate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpdAvailabilityEstimate;
	}

	/**
	 * Removes the cpd availability estimate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDAvailabilityEstimateId the primary key of the cpd availability estimate
	 * @return the cpd availability estimate that was removed
	 * @throws NoSuchCPDAvailabilityEstimateException if a cpd availability estimate with the primary key could not be found
	 */
	@Override
	public CPDAvailabilityEstimate remove(long CPDAvailabilityEstimateId)
		throws NoSuchCPDAvailabilityEstimateException {

		return remove((Serializable)CPDAvailabilityEstimateId);
	}

	@Override
	protected CPDAvailabilityEstimate removeImpl(
		CPDAvailabilityEstimate cpdAvailabilityEstimate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpdAvailabilityEstimate)) {
				cpdAvailabilityEstimate = (CPDAvailabilityEstimate)session.get(
					CPDAvailabilityEstimateImpl.class,
					cpdAvailabilityEstimate.getPrimaryKeyObj());
			}

			if (cpdAvailabilityEstimate != null) {
				session.delete(cpdAvailabilityEstimate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpdAvailabilityEstimate != null) {
			clearCache(cpdAvailabilityEstimate);
		}

		return cpdAvailabilityEstimate;
	}

	@Override
	public CPDAvailabilityEstimate updateImpl(
		CPDAvailabilityEstimate cpdAvailabilityEstimate) {

		boolean isNew = cpdAvailabilityEstimate.isNew();

		if (!(cpdAvailabilityEstimate instanceof
				CPDAvailabilityEstimateModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpdAvailabilityEstimate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpdAvailabilityEstimate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpdAvailabilityEstimate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDAvailabilityEstimate implementation " +
					cpdAvailabilityEstimate.getClass());
		}

		CPDAvailabilityEstimateModelImpl cpdAvailabilityEstimateModelImpl =
			(CPDAvailabilityEstimateModelImpl)cpdAvailabilityEstimate;

		if (Validator.isNull(cpdAvailabilityEstimate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpdAvailabilityEstimate.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpdAvailabilityEstimate.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpdAvailabilityEstimate.setCreateDate(date);
			}
			else {
				cpdAvailabilityEstimate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpdAvailabilityEstimateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpdAvailabilityEstimate.setModifiedDate(date);
			}
			else {
				cpdAvailabilityEstimate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cpdAvailabilityEstimate);
			}
			else {
				cpdAvailabilityEstimate =
					(CPDAvailabilityEstimate)session.merge(
						cpdAvailabilityEstimate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpdAvailabilityEstimate, false);

		if (isNew) {
			cpdAvailabilityEstimate.setNew(false);
		}

		cpdAvailabilityEstimate.resetOriginalValues();

		return cpdAvailabilityEstimate;
	}

	/**
	 * Returns the cpd availability estimate with the primary key or throws a <code>NoSuchCPDAvailabilityEstimateException</code> if it could not be found.
	 *
	 * @param CPDAvailabilityEstimateId the primary key of the cpd availability estimate
	 * @return the cpd availability estimate
	 * @throws NoSuchCPDAvailabilityEstimateException if a cpd availability estimate with the primary key could not be found
	 */
	@Override
	public CPDAvailabilityEstimate findByPrimaryKey(
			long CPDAvailabilityEstimateId)
		throws NoSuchCPDAvailabilityEstimateException {

		return findByPrimaryKey((Serializable)CPDAvailabilityEstimateId);
	}

	/**
	 * Returns the cpd availability estimate with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDAvailabilityEstimateId the primary key of the cpd availability estimate
	 * @return the cpd availability estimate, or <code>null</code> if a cpd availability estimate with the primary key could not be found
	 */
	@Override
	public CPDAvailabilityEstimate fetchByPrimaryKey(
		long CPDAvailabilityEstimateId) {

		return fetchByPrimaryKey((Serializable)CPDAvailabilityEstimateId);
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
		return "CPDAvailabilityEstimateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDAVAILABILITYESTIMATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CPDAvailabilityEstimateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cpd availability estimate persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_CPDAVAILABILITYESTIMATE_WHERE,
			_SQL_COUNT_CPDAVAILABILITYESTIMATE_WHERE,
			CPDAvailabilityEstimateModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpdAvailabilityEstimate.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDAvailabilityEstimate::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_CPDAVAILABILITYESTIMATE_WHERE,
				_SQL_COUNT_CPDAVAILABILITYESTIMATE_WHERE,
				CPDAvailabilityEstimateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpdAvailabilityEstimate.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDAvailabilityEstimate::getUuid),
				new FinderColumn<>(
					"cpdAvailabilityEstimate.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDAvailabilityEstimate::getCompanyId));

		_collectionPersistenceFinderByCommerceAvailabilityEstimateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceAvailabilityEstimateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceAvailabilityEstimateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceAvailabilityEstimateId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAvailabilityEstimateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceAvailabilityEstimateId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAvailabilityEstimateId"}, false),
				_SQL_SELECT_CPDAVAILABILITYESTIMATE_WHERE,
				_SQL_COUNT_CPDAVAILABILITYESTIMATE_WHERE,
				CPDAvailabilityEstimateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpdAvailabilityEstimate.",
					"commerceAvailabilityEstimateId", FinderColumn.Type.LONG,
					"=", true, true,
					CPDAvailabilityEstimate::
						getCommerceAvailabilityEstimateId));

		_uniquePersistenceFinderByCProductId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCProductId",
				new String[] {Long.class.getName()},
				new String[] {"CProductId"}, 0, 0, false,
				CPDAvailabilityEstimate::getCProductId),
			_SQL_SELECT_CPDAVAILABILITYESTIMATE_WHERE, "",
			new FinderColumn<>(
				"cpdAvailabilityEstimate.", "CProductId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDAvailabilityEstimate::getCProductId));

		CPDAvailabilityEstimateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDAvailabilityEstimateUtil.setPersistence(null);

		entityCache.removeCache(CPDAvailabilityEstimateImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CPDAvailabilityEstimateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDAVAILABILITYESTIMATE =
		"SELECT cpdAvailabilityEstimate FROM CPDAvailabilityEstimate cpdAvailabilityEstimate";

	private static final String _SQL_SELECT_CPDAVAILABILITYESTIMATE_WHERE =
		"SELECT cpdAvailabilityEstimate FROM CPDAvailabilityEstimate cpdAvailabilityEstimate WHERE ";

	private static final String _SQL_COUNT_CPDAVAILABILITYESTIMATE_WHERE =
		"SELECT COUNT(cpdAvailabilityEstimate) FROM CPDAvailabilityEstimate cpdAvailabilityEstimate WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDAvailabilityEstimate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDAvailabilityEstimatePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-382216688