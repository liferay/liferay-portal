/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseItemExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseItemException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItemTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseItemPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseItemUtil;
import com.liferay.commerce.inventory.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce inventory warehouse item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryWarehouseItemPersistence.class)
public class CommerceInventoryWarehouseItemPersistenceImpl
	extends BasePersistenceImpl
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
	implements CommerceInventoryWarehouseItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryWarehouseItemUtil</code> to access the commerce inventory warehouse item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryWarehouseItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse items
	 */
	@Override
	public List<CommerceInventoryWarehouseItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByUuid_First(
			String uuid,
			OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator)
		throws NoSuchInventoryWarehouseItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce inventory warehouse items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse items
	 */
	@Override
	public List<CommerceInventoryWarehouseItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator)
		throws NoSuchInventoryWarehouseItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse items where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce inventory warehouse items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse items where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse items
	 */
	@Override
	public List<CommerceInventoryWarehouseItem> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator)
		throws NoSuchInventoryWarehouseItemException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse items where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce inventory warehouse items where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_collectionPersistenceFinderByCommerceInventoryWarehouseId;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse items
	 */
	@Override
	public List<CommerceInventoryWarehouseItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.find(
			finderCache, new Object[] {commerceInventoryWarehouseId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem
			findByCommerceInventoryWarehouseId_First(
				long commerceInventoryWarehouseId,
				OrderByComparator<CommerceInventoryWarehouseItem>
					orderByComparator)
		throws NoSuchInventoryWarehouseItemException {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.
			findFirst(
				finderCache, new Object[] {commerceInventoryWarehouseId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem
		fetchByCommerceInventoryWarehouseId_First(
			long commerceInventoryWarehouseId,
			OrderByComparator<CommerceInventoryWarehouseItem>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.
			fetchFirst(
				finderCache, new Object[] {commerceInventoryWarehouseId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse items where commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 */
	@Override
	public void removeByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		_collectionPersistenceFinderByCommerceInventoryWarehouseId.remove(
			finderCache, new Object[] {commerceInventoryWarehouseId});
	}

	/**
	 * Returns the number of commerce inventory warehouse items where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.count(
			finderCache, new Object[] {commerceInventoryWarehouseId});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_collectionPersistenceFinderByC_S_U;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse items
	 */
	@Override
	public List<CommerceInventoryWarehouseItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S_U.find(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByC_S_U_First(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator)
		throws NoSuchInventoryWarehouseItemException {

		return _collectionPersistenceFinderByC_S_U.findFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByC_S_U_First(
		long companyId, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryWarehouseItem> orderByComparator) {

		return _collectionPersistenceFinderByC_S_U.fetchFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
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
	 * Returns the number of commerce inventory warehouse items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		return _collectionPersistenceFinderByC_S_U.count(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	private UniquePersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_uniquePersistenceFinderByCIWI_S_U;

	/**
	 * Returns the commerce inventory warehouse item where commerceInventoryWarehouseId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; or throws a <code>NoSuchInventoryWarehouseItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByCIWI_S_U(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey)
		throws NoSuchInventoryWarehouseItemException {

		return _uniquePersistenceFinderByCIWI_S_U.find(
			finderCache,
			new Object[] {commerceInventoryWarehouseId, sku, unitOfMeasureKey});
	}

	/**
	 * Returns the commerce inventory warehouse item where commerceInventoryWarehouseId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByCIWI_S_U(
		long commerceInventoryWarehouseId, String sku, String unitOfMeasureKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCIWI_S_U.fetch(
			finderCache,
			new Object[] {commerceInventoryWarehouseId, sku, unitOfMeasureKey},
			useFinderCache);
	}

	/**
	 * Removes the commerce inventory warehouse item where commerceInventoryWarehouseId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the commerce inventory warehouse item that was removed
	 */
	@Override
	public CommerceInventoryWarehouseItem removeByCIWI_S_U(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey)
		throws NoSuchInventoryWarehouseItemException {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			findByCIWI_S_U(commerceInventoryWarehouseId, sku, unitOfMeasureKey);

		return remove(commerceInventoryWarehouseItem);
	}

	/**
	 * Returns the number of commerce inventory warehouse items where commerceInventoryWarehouseId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByCIWI_S_U(
		long commerceInventoryWarehouseId, String sku,
		String unitOfMeasureKey) {

		return _uniquePersistenceFinderByCIWI_S_U.count(
			finderCache,
			new Object[] {commerceInventoryWarehouseId, sku, unitOfMeasureKey});
	}

	private UniquePersistenceFinder
		<CommerceInventoryWarehouseItem, NoSuchInventoryWarehouseItemException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce inventory warehouse item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchInventoryWarehouseItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryWarehouseItemException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce inventory warehouse item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce inventory warehouse item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce inventory warehouse item that was removed
	 */
	@Override
	public CommerceInventoryWarehouseItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryWarehouseItemException {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			findByERC_C(externalReferenceCode, companyId);

		return remove(commerceInventoryWarehouseItem);
	}

	/**
	 * Returns the number of commerce inventory warehouse items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouse items
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceInventoryWarehouseItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commerceInventoryWarehouseItemId", "CIWarehouseItemId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryWarehouseItem.class);

		setModelImplClass(CommerceInventoryWarehouseItemImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryWarehouseItemTable.INSTANCE);
	}

	/**
	 * Creates a new commerce inventory warehouse item with the primary key. Does not add the commerce inventory warehouse item to the database.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key for the new commerce inventory warehouse item
	 * @return the new commerce inventory warehouse item
	 */
	@Override
	public CommerceInventoryWarehouseItem create(
		long commerceInventoryWarehouseItemId) {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			new CommerceInventoryWarehouseItemImpl();

		commerceInventoryWarehouseItem.setNew(true);
		commerceInventoryWarehouseItem.setPrimaryKey(
			commerceInventoryWarehouseItemId);

		String uuid = PortalUUIDUtil.generate();

		commerceInventoryWarehouseItem.setUuid(uuid);

		commerceInventoryWarehouseItem.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceInventoryWarehouseItem;
	}

	/**
	 * Removes the commerce inventory warehouse item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was removed
	 * @throws NoSuchInventoryWarehouseItemException if a commerce inventory warehouse item with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem remove(
			long commerceInventoryWarehouseItemId)
		throws NoSuchInventoryWarehouseItemException {

		return remove((Serializable)commerceInventoryWarehouseItemId);
	}

	@Override
	protected CommerceInventoryWarehouseItem removeImpl(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryWarehouseItem)) {
				commerceInventoryWarehouseItem =
					(CommerceInventoryWarehouseItem)session.get(
						CommerceInventoryWarehouseItemImpl.class,
						commerceInventoryWarehouseItem.getPrimaryKeyObj());
			}

			if (commerceInventoryWarehouseItem != null) {
				session.delete(commerceInventoryWarehouseItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryWarehouseItem != null) {
			clearCache(commerceInventoryWarehouseItem);
		}

		return commerceInventoryWarehouseItem;
	}

	@Override
	public CommerceInventoryWarehouseItem updateImpl(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		boolean isNew = commerceInventoryWarehouseItem.isNew();

		if (!(commerceInventoryWarehouseItem instanceof
				CommerceInventoryWarehouseItemModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceInventoryWarehouseItem.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryWarehouseItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryWarehouseItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryWarehouseItem implementation " +
					commerceInventoryWarehouseItem.getClass());
		}

		CommerceInventoryWarehouseItemModelImpl
			commerceInventoryWarehouseItemModelImpl =
				(CommerceInventoryWarehouseItemModelImpl)
					commerceInventoryWarehouseItem;

		if (Validator.isNull(commerceInventoryWarehouseItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceInventoryWarehouseItem.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceInventoryWarehouseItem.getExternalReferenceCode())) {

			commerceInventoryWarehouseItem.setExternalReferenceCode(
				commerceInventoryWarehouseItem.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceInventoryWarehouseItemModelImpl.
						getColumnOriginalValue("externalReferenceCode"),
					commerceInventoryWarehouseItem.
						getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId =
						commerceInventoryWarehouseItem.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK =
							commerceInventoryWarehouseItem.getPrimaryKey();
					}

					try {
						commerceInventoryWarehouseItem.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceInventoryWarehouseItem.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								commerceInventoryWarehouseItem.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceInventoryWarehouseItem ercCommerceInventoryWarehouseItem =
				fetchByERC_C(
					commerceInventoryWarehouseItem.getExternalReferenceCode(),
					commerceInventoryWarehouseItem.getCompanyId());

			if (isNew) {
				if (ercCommerceInventoryWarehouseItem != null) {
					throw new DuplicateCommerceInventoryWarehouseItemExternalReferenceCodeException(
						"Duplicate commerce inventory warehouse item with external reference code " +
							commerceInventoryWarehouseItem.
								getExternalReferenceCode() + " and company " +
									commerceInventoryWarehouseItem.
										getCompanyId());
				}
			}
			else {
				if ((ercCommerceInventoryWarehouseItem != null) &&
					(commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId() !=
							ercCommerceInventoryWarehouseItem.
								getCommerceInventoryWarehouseItemId())) {

					throw new DuplicateCommerceInventoryWarehouseItemExternalReferenceCodeException(
						"Duplicate commerce inventory warehouse item with external reference code " +
							commerceInventoryWarehouseItem.
								getExternalReferenceCode() + " and company " +
									commerceInventoryWarehouseItem.
										getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceInventoryWarehouseItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceInventoryWarehouseItem.setCreateDate(date);
			}
			else {
				commerceInventoryWarehouseItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryWarehouseItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryWarehouseItem.setModifiedDate(date);
			}
			else {
				commerceInventoryWarehouseItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryWarehouseItem);
			}
			else {
				commerceInventoryWarehouseItem =
					(CommerceInventoryWarehouseItem)session.merge(
						commerceInventoryWarehouseItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceInventoryWarehouseItem, false);

		if (isNew) {
			commerceInventoryWarehouseItem.setNew(false);
		}

		commerceInventoryWarehouseItem.resetOriginalValues();

		return commerceInventoryWarehouseItem;
	}

	/**
	 * Returns the commerce inventory warehouse item with the primary key or throws a <code>NoSuchInventoryWarehouseItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item
	 * @throws NoSuchInventoryWarehouseItemException if a commerce inventory warehouse item with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem findByPrimaryKey(
			long commerceInventoryWarehouseItemId)
		throws NoSuchInventoryWarehouseItemException {

		return findByPrimaryKey((Serializable)commerceInventoryWarehouseItemId);
	}

	/**
	 * Returns the commerce inventory warehouse item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item, or <code>null</code> if a commerce inventory warehouse item with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouseItem fetchByPrimaryKey(
		long commerceInventoryWarehouseItemId) {

		return fetchByPrimaryKey(
			(Serializable)commerceInventoryWarehouseItemId);
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
		return "CIWarehouseItemId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceInventoryWarehouseItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryWarehouseItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory warehouse item persistence.
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
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
			CommerceInventoryWarehouseItemModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouseItem::getUuid));

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
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
				CommerceInventoryWarehouseItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouseItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryWarehouseItem::getUuid),
				new FinderColumn<>(
					"commerceInventoryWarehouseItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouseItem::getCompanyId));

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
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
				CommerceInventoryWarehouseItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouseItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouseItem::getCompanyId));

		_collectionPersistenceFinderByCommerceInventoryWarehouseId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceInventoryWarehouseId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceInventoryWarehouseId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceInventoryWarehouseId",
					new String[] {Long.class.getName()},
					new String[] {"commerceInventoryWarehouseId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceInventoryWarehouseId",
					new String[] {Long.class.getName()},
					new String[] {"commerceInventoryWarehouseId"}, false),
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
				CommerceInventoryWarehouseItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouseItem.",
					"commerceInventoryWarehouseId", FinderColumn.Type.LONG, "=",
					true, true,
					CommerceInventoryWarehouseItem::
						getCommerceInventoryWarehouseId));

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
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE,
			CommerceInventoryWarehouseItemModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseItem::getCompanyId),
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "sku",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouseItem::getSku),
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "unitOfMeasureKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouseItem::getUnitOfMeasureKey));

		_uniquePersistenceFinderByCIWI_S_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCIWI_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"commerceInventoryWarehouseId", "sku", "unitOfMeasureKey"
				},
				0, 6, false,
				CommerceInventoryWarehouseItem::getCommerceInventoryWarehouseId,
				convertNullFunction(CommerceInventoryWarehouseItem::getSku),
				convertNullFunction(
					CommerceInventoryWarehouseItem::getUnitOfMeasureKey)),
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE, "",
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.",
				"commerceInventoryWarehouseId", FinderColumn.Type.LONG, "=",
				true, true,
				CommerceInventoryWarehouseItem::
					getCommerceInventoryWarehouseId),
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "sku",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouseItem::getSku),
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "unitOfMeasureKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouseItem::getUnitOfMeasureKey));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceInventoryWarehouseItem::getExternalReferenceCode),
				CommerceInventoryWarehouseItem::getCompanyId),
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE, "",
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouseItem::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceInventoryWarehouseItem.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseItem::getCompanyId));

		CommerceInventoryWarehouseItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryWarehouseItemUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceInventoryWarehouseItemImpl.class.getName());
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
		CommerceInventoryWarehouseItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM =
		"SELECT commerceInventoryWarehouseItem FROM CommerceInventoryWarehouseItem commerceInventoryWarehouseItem";

	private static final String
		_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE =
			"SELECT commerceInventoryWarehouseItem FROM CommerceInventoryWarehouseItem commerceInventoryWarehouseItem WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEITEM_WHERE =
			"SELECT COUNT(commerceInventoryWarehouseItem) FROM CommerceInventoryWarehouseItem commerceInventoryWarehouseItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryWarehouseItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryWarehouseItemPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "commerceInventoryWarehouseItemId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:505072741