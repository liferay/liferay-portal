/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.NoSuchDiscountOrderTypeRelException;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRelTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountOrderTypeRelPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountOrderTypeRelUtil;
import com.liferay.commerce.discount.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce discount order type rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountOrderTypeRelPersistence.class)
public class CommerceDiscountOrderTypeRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceDiscountOrderTypeRel, NoSuchDiscountOrderTypeRelException>
	implements CommerceDiscountOrderTypeRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountOrderTypeRelUtil</code> to access the commerce discount order type rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountOrderTypeRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceDiscountOrderTypeRel, NoSuchDiscountOrderTypeRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce discount order type rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discount order type rels
	 * @param end the upper bound of the range of commerce discount order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount order type rels
	 */
	@Override
	public List<CommerceDiscountOrderTypeRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel
	 * @throws NoSuchDiscountOrderTypeRelException if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel findByUuid_First(
			String uuid,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws NoSuchDiscountOrderTypeRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount order type rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce discount order type rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce discount order type rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountOrderTypeRel, NoSuchDiscountOrderTypeRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce discount order type rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discount order type rels
	 * @param end the upper bound of the range of commerce discount order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount order type rels
	 */
	@Override
	public List<CommerceDiscountOrderTypeRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel
	 * @throws NoSuchDiscountOrderTypeRelException if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws NoSuchDiscountOrderTypeRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount order type rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce discount order type rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce discount order type rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountOrderTypeRel, NoSuchDiscountOrderTypeRelException>
			_collectionPersistenceFinderByCommerceDiscountId;

	/**
	 * Returns an ordered range of all the commerce discount order type rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount order type rels
	 * @param end the upper bound of the range of commerce discount order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount order type rels
	 */
	@Override
	public List<CommerceDiscountOrderTypeRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceDiscountId.find(
			finderCache, new Object[] {commerceDiscountId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel
	 * @throws NoSuchDiscountOrderTypeRelException if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws NoSuchDiscountOrderTypeRelException {

		return _collectionPersistenceFinderByCommerceDiscountId.findFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceDiscountId.fetchFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount order type rels where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	@Override
	public void removeByCommerceDiscountId(long commerceDiscountId) {
		_collectionPersistenceFinderByCommerceDiscountId.remove(
			finderCache, new Object[] {commerceDiscountId});
	}

	/**
	 * Returns the number of commerce discount order type rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount order type rels
	 */
	@Override
	public int countByCommerceDiscountId(long commerceDiscountId) {
		return _collectionPersistenceFinderByCommerceDiscountId.count(
			finderCache, new Object[] {commerceDiscountId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountOrderTypeRel, NoSuchDiscountOrderTypeRelException>
			_collectionPersistenceFinderByCommerceOrderTypeId;

	/**
	 * Returns an ordered range of all the commerce discount order type rels where commerceOrderTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param start the lower bound of the range of commerce discount order type rels
	 * @param end the upper bound of the range of commerce discount order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount order type rels
	 */
	@Override
	public List<CommerceDiscountOrderTypeRel> findByCommerceOrderTypeId(
		long commerceOrderTypeId, int start, int end,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderTypeId.find(
			finderCache, new Object[] {commerceOrderTypeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where commerceOrderTypeId = &#63;.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel
	 * @throws NoSuchDiscountOrderTypeRelException if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel findByCommerceOrderTypeId_First(
			long commerceOrderTypeId,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws NoSuchDiscountOrderTypeRelException {

		return _collectionPersistenceFinderByCommerceOrderTypeId.findFirst(
			finderCache, new Object[] {commerceOrderTypeId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount order type rel in the ordered set where commerceOrderTypeId = &#63;.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel fetchByCommerceOrderTypeId_First(
		long commerceOrderTypeId,
		OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderTypeId.fetchFirst(
			finderCache, new Object[] {commerceOrderTypeId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount order type rels where commerceOrderTypeId = &#63; from the database.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 */
	@Override
	public void removeByCommerceOrderTypeId(long commerceOrderTypeId) {
		_collectionPersistenceFinderByCommerceOrderTypeId.remove(
			finderCache, new Object[] {commerceOrderTypeId});
	}

	/**
	 * Returns the number of commerce discount order type rels where commerceOrderTypeId = &#63;.
	 *
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the number of matching commerce discount order type rels
	 */
	@Override
	public int countByCommerceOrderTypeId(long commerceOrderTypeId) {
		return _collectionPersistenceFinderByCommerceOrderTypeId.count(
			finderCache, new Object[] {commerceOrderTypeId});
	}

	private UniquePersistenceFinder
		<CommerceDiscountOrderTypeRel, NoSuchDiscountOrderTypeRelException>
			_uniquePersistenceFinderByCDI_COTI;

	/**
	 * Returns the commerce discount order type rel where commerceDiscountId = &#63; and commerceOrderTypeId = &#63; or throws a <code>NoSuchDiscountOrderTypeRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the matching commerce discount order type rel
	 * @throws NoSuchDiscountOrderTypeRelException if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel findByCDI_COTI(
			long commerceDiscountId, long commerceOrderTypeId)
		throws NoSuchDiscountOrderTypeRelException {

		return _uniquePersistenceFinderByCDI_COTI.find(
			finderCache,
			new Object[] {commerceDiscountId, commerceOrderTypeId});
	}

	/**
	 * Returns the commerce discount order type rel where commerceDiscountId = &#63; and commerceOrderTypeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel fetchByCDI_COTI(
		long commerceDiscountId, long commerceOrderTypeId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCDI_COTI.fetch(
			finderCache, new Object[] {commerceDiscountId, commerceOrderTypeId},
			useFinderCache);
	}

	/**
	 * Removes the commerce discount order type rel where commerceDiscountId = &#63; and commerceOrderTypeId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the commerce discount order type rel that was removed
	 */
	@Override
	public CommerceDiscountOrderTypeRel removeByCDI_COTI(
			long commerceDiscountId, long commerceOrderTypeId)
		throws NoSuchDiscountOrderTypeRelException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			findByCDI_COTI(commerceDiscountId, commerceOrderTypeId);

		return remove(commerceDiscountOrderTypeRel);
	}

	/**
	 * Returns the number of commerce discount order type rels where commerceDiscountId = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the number of matching commerce discount order type rels
	 */
	@Override
	public int countByCDI_COTI(
		long commerceDiscountId, long commerceOrderTypeId) {

		return _uniquePersistenceFinderByCDI_COTI.count(
			finderCache,
			new Object[] {commerceDiscountId, commerceOrderTypeId});
	}

	public CommerceDiscountOrderTypeRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceDiscountOrderTypeRel.class);

		setModelImplClass(CommerceDiscountOrderTypeRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountOrderTypeRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce discount order type rel with the primary key. Does not add the commerce discount order type rel to the database.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key for the new commerce discount order type rel
	 * @return the new commerce discount order type rel
	 */
	@Override
	public CommerceDiscountOrderTypeRel create(
		long commerceDiscountOrderTypeRelId) {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			new CommerceDiscountOrderTypeRelImpl();

		commerceDiscountOrderTypeRel.setNew(true);
		commerceDiscountOrderTypeRel.setPrimaryKey(
			commerceDiscountOrderTypeRelId);

		String uuid = PortalUUIDUtil.generate();

		commerceDiscountOrderTypeRel.setUuid(uuid);

		commerceDiscountOrderTypeRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceDiscountOrderTypeRel;
	}

	/**
	 * Removes the commerce discount order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel that was removed
	 * @throws NoSuchDiscountOrderTypeRelException if a commerce discount order type rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel remove(
			long commerceDiscountOrderTypeRelId)
		throws NoSuchDiscountOrderTypeRelException {

		return remove((Serializable)commerceDiscountOrderTypeRelId);
	}

	@Override
	protected CommerceDiscountOrderTypeRel removeImpl(
		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscountOrderTypeRel)) {
				commerceDiscountOrderTypeRel =
					(CommerceDiscountOrderTypeRel)session.get(
						CommerceDiscountOrderTypeRelImpl.class,
						commerceDiscountOrderTypeRel.getPrimaryKeyObj());
			}

			if (commerceDiscountOrderTypeRel != null) {
				session.delete(commerceDiscountOrderTypeRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscountOrderTypeRel != null) {
			clearCache(commerceDiscountOrderTypeRel);
		}

		return commerceDiscountOrderTypeRel;
	}

	@Override
	public CommerceDiscountOrderTypeRel updateImpl(
		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel) {

		boolean isNew = commerceDiscountOrderTypeRel.isNew();

		if (!(commerceDiscountOrderTypeRel instanceof
				CommerceDiscountOrderTypeRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceDiscountOrderTypeRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscountOrderTypeRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscountOrderTypeRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscountOrderTypeRel implementation " +
					commerceDiscountOrderTypeRel.getClass());
		}

		CommerceDiscountOrderTypeRelModelImpl
			commerceDiscountOrderTypeRelModelImpl =
				(CommerceDiscountOrderTypeRelModelImpl)
					commerceDiscountOrderTypeRel;

		if (Validator.isNull(commerceDiscountOrderTypeRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceDiscountOrderTypeRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceDiscountOrderTypeRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceDiscountOrderTypeRel.setCreateDate(date);
			}
			else {
				commerceDiscountOrderTypeRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountOrderTypeRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceDiscountOrderTypeRel.setModifiedDate(date);
			}
			else {
				commerceDiscountOrderTypeRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscountOrderTypeRel);
			}
			else {
				commerceDiscountOrderTypeRel =
					(CommerceDiscountOrderTypeRel)session.merge(
						commerceDiscountOrderTypeRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceDiscountOrderTypeRel, false);

		if (isNew) {
			commerceDiscountOrderTypeRel.setNew(false);
		}

		commerceDiscountOrderTypeRel.resetOriginalValues();

		return commerceDiscountOrderTypeRel;
	}

	/**
	 * Returns the commerce discount order type rel with the primary key or throws a <code>NoSuchDiscountOrderTypeRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel
	 * @throws NoSuchDiscountOrderTypeRelException if a commerce discount order type rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel findByPrimaryKey(
			long commerceDiscountOrderTypeRelId)
		throws NoSuchDiscountOrderTypeRelException {

		return findByPrimaryKey((Serializable)commerceDiscountOrderTypeRelId);
	}

	/**
	 * Returns the commerce discount order type rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel, or <code>null</code> if a commerce discount order type rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountOrderTypeRel fetchByPrimaryKey(
		long commerceDiscountOrderTypeRelId) {

		return fetchByPrimaryKey((Serializable)commerceDiscountOrderTypeRelId);
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
		return "commerceDiscountOrderTypeRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountOrderTypeRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount order type rel persistence.
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
			_SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
			_SQL_COUNT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
			CommerceDiscountOrderTypeRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"commerceDiscountOrderTypeRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceDiscountOrderTypeRel::getUuid));

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
				_SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
				CommerceDiscountOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountOrderTypeRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceDiscountOrderTypeRel::getUuid),
				new FinderColumn<>(
					"commerceDiscountOrderTypeRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountOrderTypeRel::getCompanyId));

		_collectionPersistenceFinderByCommerceDiscountId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
				CommerceDiscountOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountOrderTypeRel.", "commerceDiscountId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountOrderTypeRel::getCommerceDiscountId));

		_collectionPersistenceFinderByCommerceOrderTypeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceOrderTypeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceOrderTypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceOrderTypeId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderTypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceOrderTypeId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderTypeId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTORDERTYPEREL_WHERE,
				CommerceDiscountOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountOrderTypeRel.", "commerceOrderTypeId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountOrderTypeRel::getCommerceOrderTypeId));

		_uniquePersistenceFinderByCDI_COTI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCDI_COTI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceDiscountId", "commerceOrderTypeId"}, 0,
				0, false, CommerceDiscountOrderTypeRel::getCommerceDiscountId,
				CommerceDiscountOrderTypeRel::getCommerceOrderTypeId),
			_SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL_WHERE, "",
			new FinderColumn<>(
				"commerceDiscountOrderTypeRel.", "commerceDiscountId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceDiscountOrderTypeRel::getCommerceDiscountId),
			new FinderColumn<>(
				"commerceDiscountOrderTypeRel.", "commerceOrderTypeId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceDiscountOrderTypeRel::getCommerceOrderTypeId));

		CommerceDiscountOrderTypeRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountOrderTypeRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceDiscountOrderTypeRelImpl.class.getName());
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
		CommerceDiscountOrderTypeRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL =
		"SELECT commerceDiscountOrderTypeRel FROM CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTORDERTYPEREL_WHERE =
		"SELECT commerceDiscountOrderTypeRel FROM CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEDISCOUNTORDERTYPEREL_WHERE =
		"SELECT COUNT(commerceDiscountOrderTypeRel) FROM CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1136793845