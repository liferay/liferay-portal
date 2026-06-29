/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.NoSuchInventoryBookedQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantityTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryBookedQuantityPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryBookedQuantityUtil;
import com.liferay.commerce.inventory.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce inventory booked quantity service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryBookedQuantityPersistence.class)
public class CommerceInventoryBookedQuantityPersistenceImpl
	extends BasePersistenceImpl
		<CommerceInventoryBookedQuantity,
		 NoSuchInventoryBookedQuantityException>
	implements CommerceInventoryBookedQuantityPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryBookedQuantityUtil</code> to access the commerce inventory booked quantity persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryBookedQuantityImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceInventoryBookedQuantity,
		 NoSuchInventoryBookedQuantityException>
			_collectionPersistenceFinderByLtExpirationDate;

	/**
	 * Returns all the commerce inventory booked quantities where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching commerce inventory booked quantities
	 */
	@Override
	public List<CommerceInventoryBookedQuantity> findByLtExpirationDate(
		Date expirationDate) {

		return findByLtExpirationDate(
			expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce inventory booked quantities where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @return the range of matching commerce inventory booked quantities
	 */
	@Override
	public List<CommerceInventoryBookedQuantity> findByLtExpirationDate(
		Date expirationDate, int start, int end) {

		return findByLtExpirationDate(expirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce inventory booked quantities where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory booked quantities
	 */
	@Override
	public List<CommerceInventoryBookedQuantity> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator) {

		return findByLtExpirationDate(
			expirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory booked quantities where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory booked quantities
	 */
	@Override
	public List<CommerceInventoryBookedQuantity> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtExpirationDate.find(
			finderCache, new Object[] {expirationDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory booked quantity in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory booked quantity
	 * @throws NoSuchInventoryBookedQuantityException if a matching commerce inventory booked quantity could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity findByLtExpirationDate_First(
			Date expirationDate,
			OrderByComparator<CommerceInventoryBookedQuantity>
				orderByComparator)
		throws NoSuchInventoryBookedQuantityException {

		return _collectionPersistenceFinderByLtExpirationDate.findFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory booked quantity in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory booked quantity, or <code>null</code> if a matching commerce inventory booked quantity could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity fetchByLtExpirationDate_First(
		Date expirationDate,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator) {

		return _collectionPersistenceFinderByLtExpirationDate.fetchFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory booked quantities where expirationDate &lt; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByLtExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByLtExpirationDate.remove(
			finderCache, new Object[] {expirationDate});
	}

	/**
	 * Returns the number of commerce inventory booked quantities where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching commerce inventory booked quantities
	 */
	@Override
	public int countByLtExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByLtExpirationDate.count(
			finderCache, new Object[] {expirationDate});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryBookedQuantity,
		 NoSuchInventoryBookedQuantityException>
			_collectionPersistenceFinderBySku;

	/**
	 * Returns an ordered range of all the commerce inventory booked quantities where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory booked quantities
	 */
	@Override
	public List<CommerceInventoryBookedQuantity> findBySku(
		String sku, int start, int end,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySku.find(
			finderCache, new Object[] {sku}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory booked quantity in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory booked quantity
	 * @throws NoSuchInventoryBookedQuantityException if a matching commerce inventory booked quantity could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity findBySku_First(
			String sku,
			OrderByComparator<CommerceInventoryBookedQuantity>
				orderByComparator)
		throws NoSuchInventoryBookedQuantityException {

		return _collectionPersistenceFinderBySku.findFirst(
			finderCache, new Object[] {sku}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory booked quantity in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory booked quantity, or <code>null</code> if a matching commerce inventory booked quantity could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity fetchBySku_First(
		String sku,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator) {

		return _collectionPersistenceFinderBySku.fetchFirst(
			finderCache, new Object[] {sku}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory booked quantities where sku = &#63; from the database.
	 *
	 * @param sku the sku
	 */
	@Override
	public void removeBySku(String sku) {
		_collectionPersistenceFinderBySku.remove(
			finderCache, new Object[] {sku});
	}

	/**
	 * Returns the number of commerce inventory booked quantities where sku = &#63;.
	 *
	 * @param sku the sku
	 * @return the number of matching commerce inventory booked quantities
	 */
	@Override
	public int countBySku(String sku) {
		return _collectionPersistenceFinderBySku.count(
			finderCache, new Object[] {sku});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryBookedQuantity,
		 NoSuchInventoryBookedQuantityException>
			_collectionPersistenceFinderByC_S_U;

	/**
	 * Returns an ordered range of all the commerce inventory booked quantities where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory booked quantities
	 */
	@Override
	public List<CommerceInventoryBookedQuantity> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S_U.find(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory booked quantity in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory booked quantity
	 * @throws NoSuchInventoryBookedQuantityException if a matching commerce inventory booked quantity could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity findByC_S_U_First(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryBookedQuantity>
				orderByComparator)
		throws NoSuchInventoryBookedQuantityException {

		return _collectionPersistenceFinderByC_S_U.findFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory booked quantity in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory booked quantity, or <code>null</code> if a matching commerce inventory booked quantity could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity fetchByC_S_U_First(
		long companyId, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryBookedQuantity> orderByComparator) {

		return _collectionPersistenceFinderByC_S_U.fetchFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Removes all the commerce inventory booked quantities where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	@Override
	public void removeByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		_collectionPersistenceFinderByC_S_U.remove(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	/**
	 * Returns the number of commerce inventory booked quantities where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory booked quantities
	 */
	@Override
	public int countByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		return _collectionPersistenceFinderByC_S_U.count(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	public CommerceInventoryBookedQuantityPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceInventoryBookedQuantityId", "CIBookedQuantityId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryBookedQuantity.class);

		setModelImplClass(CommerceInventoryBookedQuantityImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryBookedQuantityTable.INSTANCE);
	}

	/**
	 * Creates a new commerce inventory booked quantity with the primary key. Does not add the commerce inventory booked quantity to the database.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key for the new commerce inventory booked quantity
	 * @return the new commerce inventory booked quantity
	 */
	@Override
	public CommerceInventoryBookedQuantity create(
		long commerceInventoryBookedQuantityId) {

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			new CommerceInventoryBookedQuantityImpl();

		commerceInventoryBookedQuantity.setNew(true);
		commerceInventoryBookedQuantity.setPrimaryKey(
			commerceInventoryBookedQuantityId);

		commerceInventoryBookedQuantity.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceInventoryBookedQuantity;
	}

	/**
	 * Removes the commerce inventory booked quantity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 * @throws NoSuchInventoryBookedQuantityException if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity remove(
			long commerceInventoryBookedQuantityId)
		throws NoSuchInventoryBookedQuantityException {

		return remove((Serializable)commerceInventoryBookedQuantityId);
	}

	@Override
	protected CommerceInventoryBookedQuantity removeImpl(
		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryBookedQuantity)) {
				commerceInventoryBookedQuantity =
					(CommerceInventoryBookedQuantity)session.get(
						CommerceInventoryBookedQuantityImpl.class,
						commerceInventoryBookedQuantity.getPrimaryKeyObj());
			}

			if (commerceInventoryBookedQuantity != null) {
				session.delete(commerceInventoryBookedQuantity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryBookedQuantity != null) {
			clearCache(commerceInventoryBookedQuantity);
		}

		return commerceInventoryBookedQuantity;
	}

	@Override
	public CommerceInventoryBookedQuantity updateImpl(
		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity) {

		boolean isNew = commerceInventoryBookedQuantity.isNew();

		if (!(commerceInventoryBookedQuantity instanceof
				CommerceInventoryBookedQuantityModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceInventoryBookedQuantity.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryBookedQuantity);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryBookedQuantity proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryBookedQuantity implementation " +
					commerceInventoryBookedQuantity.getClass());
		}

		CommerceInventoryBookedQuantityModelImpl
			commerceInventoryBookedQuantityModelImpl =
				(CommerceInventoryBookedQuantityModelImpl)
					commerceInventoryBookedQuantity;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceInventoryBookedQuantity.getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceInventoryBookedQuantity.setCreateDate(date);
			}
			else {
				commerceInventoryBookedQuantity.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryBookedQuantityModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryBookedQuantity.setModifiedDate(date);
			}
			else {
				commerceInventoryBookedQuantity.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryBookedQuantity);
			}
			else {
				commerceInventoryBookedQuantity =
					(CommerceInventoryBookedQuantity)session.merge(
						commerceInventoryBookedQuantity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceInventoryBookedQuantity, false);

		if (isNew) {
			commerceInventoryBookedQuantity.setNew(false);
		}

		commerceInventoryBookedQuantity.resetOriginalValues();

		return commerceInventoryBookedQuantity;
	}

	/**
	 * Returns the commerce inventory booked quantity with the primary key or throws a <code>NoSuchInventoryBookedQuantityException</code> if it could not be found.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity
	 * @throws NoSuchInventoryBookedQuantityException if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity findByPrimaryKey(
			long commerceInventoryBookedQuantityId)
		throws NoSuchInventoryBookedQuantityException {

		return findByPrimaryKey(
			(Serializable)commerceInventoryBookedQuantityId);
	}

	/**
	 * Returns the commerce inventory booked quantity with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity, or <code>null</code> if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Override
	public CommerceInventoryBookedQuantity fetchByPrimaryKey(
		long commerceInventoryBookedQuantityId) {

		return fetchByPrimaryKey(
			(Serializable)commerceInventoryBookedQuantityId);
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
		return "CIBookedQuantityId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceInventoryBookedQuantityId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYBOOKEDQUANTITY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryBookedQuantityModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory booked quantity persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLtExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE,
				CommerceInventoryBookedQuantityModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryBookedQuantity.", "expirationDate",
					FinderColumn.Type.DATE, "<", true, true,
					CommerceInventoryBookedQuantity::getExpirationDate));

		_collectionPersistenceFinderBySku = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySku",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"sku"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySku",
				new String[] {String.class.getName()}, new String[] {"sku"}, 0,
				1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySku",
				new String[] {String.class.getName()}, new String[] {"sku"}, 0,
				1, false, null),
			_SQL_SELECT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE,
			CommerceInventoryBookedQuantityModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryBookedQuantity.", "sku",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryBookedQuantity::getSku));

		_collectionPersistenceFinderByC_S_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, 0, 6,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, 0, 6,
				false, null),
			_SQL_SELECT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE,
			CommerceInventoryBookedQuantityModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryBookedQuantity.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryBookedQuantity::getCompanyId),
			new FinderColumn<>(
				"commerceInventoryBookedQuantity.", "sku",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryBookedQuantity::getSku),
			new FinderColumn<>(
				"commerceInventoryBookedQuantity.", "unitOfMeasureKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryBookedQuantity::getUnitOfMeasureKey));

		CommerceInventoryBookedQuantityUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryBookedQuantityUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceInventoryBookedQuantityImpl.class.getName());
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
		CommerceInventoryBookedQuantityModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEINVENTORYBOOKEDQUANTITY =
		"SELECT commerceInventoryBookedQuantity FROM CommerceInventoryBookedQuantity commerceInventoryBookedQuantity";

	private static final String
		_SQL_SELECT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE =
			"SELECT commerceInventoryBookedQuantity FROM CommerceInventoryBookedQuantity commerceInventoryBookedQuantity WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEINVENTORYBOOKEDQUANTITY_WHERE =
			"SELECT COUNT(commerceInventoryBookedQuantity) FROM CommerceInventoryBookedQuantity commerceInventoryBookedQuantity WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryBookedQuantity exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceInventoryBookedQuantityId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1171608330