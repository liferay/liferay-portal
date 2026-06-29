/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehousePersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the commerce inventory warehouse service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryWarehousePersistence.class)
public class CommerceInventoryWarehousePersistenceImpl
	extends BasePersistenceImpl
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
	implements CommerceInventoryWarehousePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryWarehouseUtil</code> to access the commerce inventory warehouse persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryWarehouseImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByUuid_First(
			String uuid,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouses where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce inventory warehouses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce inventory warehouses where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce inventory warehouses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, active});
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, active});
	}

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.filterCount(
			finderCache, new Object[] {companyId, active}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_C_First(
			long companyId, String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_C_First(
		long companyId, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_C(
		long companyId, String countryTwoLettersISOCode, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByC_C.filterFind(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 */
	@Override
	public void removeByC_C(long companyId, String countryTwoLettersISOCode) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode});
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByC_C(long companyId, String countryTwoLettersISOCode) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode});
	}

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByC_C(
		long companyId, String countryTwoLettersISOCode) {

		return _collectionPersistenceFinderByC_C.filterCount(
			finderCache, new Object[] {companyId, countryTwoLettersISOCode},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_collectionPersistenceFinderByC_A_C;

	/**
	 * Returns an ordered range of all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouses
	 */
	@Override
	public List<CommerceInventoryWarehouse> findByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_C.find(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByC_A_C_First(
			long companyId, boolean active, String countryTwoLettersISOCode,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws NoSuchInventoryWarehouseException {

		return _collectionPersistenceFinderByC_A_C.findFirst(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse in the ordered set where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByC_A_C_First(
		long companyId, boolean active, String countryTwoLettersISOCode,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByC_A_C.fetchFirst(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce inventory warehouses that the user has permissions to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public List<CommerceInventoryWarehouse> filterFindByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode,
		int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator) {

		return _collectionPersistenceFinderByC_A_C.filterFind(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 */
	@Override
	public void removeByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		_collectionPersistenceFinderByC_A_C.remove(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode});
	}

	/**
	 * Returns the number of commerce inventory warehouses where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		return _collectionPersistenceFinderByC_A_C.count(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode});
	}

	/**
	 * Returns the number of commerce inventory warehouses that the user has permission to view where companyId = &#63; and active = &#63; and countryTwoLettersISOCode = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param countryTwoLettersISOCode the country two letters iso code
	 * @return the number of matching commerce inventory warehouses that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_C(
		long companyId, boolean active, String countryTwoLettersISOCode) {

		return _collectionPersistenceFinderByC_A_C.filterCount(
			finderCache,
			new Object[] {companyId, active, countryTwoLettersISOCode},
			companyId, 0);
	}

	private UniquePersistenceFinder
		<CommerceInventoryWarehouse, NoSuchInventoryWarehouseException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchInventoryWarehouseException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryWarehouseException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce inventory warehouse where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce inventory warehouse that was removed
	 */
	@Override
	public CommerceInventoryWarehouse removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryWarehouseException {

		CommerceInventoryWarehouse commerceInventoryWarehouse = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceInventoryWarehouse);
	}

	/**
	 * Returns the number of commerce inventory warehouses where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory warehouses
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceInventoryWarehousePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("commerceInventoryWarehouseId", "CIWarehouseId");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryWarehouse.class);

		setModelImplClass(CommerceInventoryWarehouseImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryWarehouseTable.INSTANCE);
	}

	/**
	 * Creates a new commerce inventory warehouse with the primary key. Does not add the commerce inventory warehouse to the database.
	 *
	 * @param commerceInventoryWarehouseId the primary key for the new commerce inventory warehouse
	 * @return the new commerce inventory warehouse
	 */
	@Override
	public CommerceInventoryWarehouse create(
		long commerceInventoryWarehouseId) {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			new CommerceInventoryWarehouseImpl();

		commerceInventoryWarehouse.setNew(true);
		commerceInventoryWarehouse.setPrimaryKey(commerceInventoryWarehouseId);

		String uuid = PortalUUIDUtil.generate();

		commerceInventoryWarehouse.setUuid(uuid);

		commerceInventoryWarehouse.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceInventoryWarehouse;
	}

	/**
	 * Removes the commerce inventory warehouse with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse remove(long commerceInventoryWarehouseId)
		throws NoSuchInventoryWarehouseException {

		return remove((Serializable)commerceInventoryWarehouseId);
	}

	@Override
	protected CommerceInventoryWarehouse removeImpl(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryWarehouse)) {
				commerceInventoryWarehouse =
					(CommerceInventoryWarehouse)session.get(
						CommerceInventoryWarehouseImpl.class,
						commerceInventoryWarehouse.getPrimaryKeyObj());
			}

			if (commerceInventoryWarehouse != null) {
				session.delete(commerceInventoryWarehouse);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryWarehouse != null) {
			clearCache(commerceInventoryWarehouse);
		}

		return commerceInventoryWarehouse;
	}

	@Override
	public CommerceInventoryWarehouse updateImpl(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		boolean isNew = commerceInventoryWarehouse.isNew();

		if (!(commerceInventoryWarehouse instanceof
				CommerceInventoryWarehouseModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceInventoryWarehouse.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryWarehouse);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryWarehouse proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryWarehouse implementation " +
					commerceInventoryWarehouse.getClass());
		}

		CommerceInventoryWarehouseModelImpl
			commerceInventoryWarehouseModelImpl =
				(CommerceInventoryWarehouseModelImpl)commerceInventoryWarehouse;

		if (Validator.isNull(commerceInventoryWarehouse.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceInventoryWarehouse.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceInventoryWarehouse.getExternalReferenceCode())) {

			commerceInventoryWarehouse.setExternalReferenceCode(
				commerceInventoryWarehouse.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceInventoryWarehouseModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceInventoryWarehouse.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceInventoryWarehouse.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceInventoryWarehouse.getPrimaryKey();
					}

					try {
						commerceInventoryWarehouse.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceInventoryWarehouse.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								commerceInventoryWarehouse.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceInventoryWarehouse ercCommerceInventoryWarehouse =
				fetchByERC_C(
					commerceInventoryWarehouse.getExternalReferenceCode(),
					commerceInventoryWarehouse.getCompanyId());

			if (isNew) {
				if (ercCommerceInventoryWarehouse != null) {
					throw new DuplicateCommerceInventoryWarehouseExternalReferenceCodeException(
						"Duplicate commerce inventory warehouse with external reference code " +
							commerceInventoryWarehouse.
								getExternalReferenceCode() + " and company " +
									commerceInventoryWarehouse.getCompanyId());
				}
			}
			else {
				if ((ercCommerceInventoryWarehouse != null) &&
					(commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId() !=
							ercCommerceInventoryWarehouse.
								getCommerceInventoryWarehouseId())) {

					throw new DuplicateCommerceInventoryWarehouseExternalReferenceCodeException(
						"Duplicate commerce inventory warehouse with external reference code " +
							commerceInventoryWarehouse.
								getExternalReferenceCode() + " and company " +
									commerceInventoryWarehouse.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceInventoryWarehouse.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceInventoryWarehouse.setCreateDate(date);
			}
			else {
				commerceInventoryWarehouse.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryWarehouseModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryWarehouse.setModifiedDate(date);
			}
			else {
				commerceInventoryWarehouse.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryWarehouse);
			}
			else {
				commerceInventoryWarehouse =
					(CommerceInventoryWarehouse)session.merge(
						commerceInventoryWarehouse);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceInventoryWarehouse, false);

		if (isNew) {
			commerceInventoryWarehouse.setNew(false);
		}

		commerceInventoryWarehouse.resetOriginalValues();

		return commerceInventoryWarehouse;
	}

	/**
	 * Returns the commerce inventory warehouse with the primary key or throws a <code>NoSuchInventoryWarehouseException</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse
	 * @throws NoSuchInventoryWarehouseException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse findByPrimaryKey(
			long commerceInventoryWarehouseId)
		throws NoSuchInventoryWarehouseException {

		return findByPrimaryKey((Serializable)commerceInventoryWarehouseId);
	}

	/**
	 * Returns the commerce inventory warehouse with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse, or <code>null</code> if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouse fetchByPrimaryKey(
		long commerceInventoryWarehouseId) {

		return fetchByPrimaryKey((Serializable)commerceInventoryWarehouseId);
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
		return "CIWarehouseId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceInventoryWarehouseId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryWarehouseModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory warehouse persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, false, null),
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryWarehouse::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryWarehouse::getUuid),
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouse::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouse::getCompanyId));

		_collectionPersistenceFinderByC_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, false),
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouse::getCompanyId),
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceInventoryWarehouse::isActive));

		_collectionPersistenceFinderByC_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "countryTwoLettersISOCode"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "countryTwoLettersISOCode"}, 0,
					2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "countryTwoLettersISOCode"}, 0,
					2, false, null),
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouse::getCompanyId),
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "countryTwoLettersISOCode",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryWarehouse::getCountryTwoLettersISOCode));

		_collectionPersistenceFinderByC_A_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "active_", "countryTwoLettersISOCode"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"companyId", "active_", "countryTwoLettersISOCode"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"companyId", "active_", "countryTwoLettersISOCode"
					},
					0, 4, false, null),
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE,
				CommerceInventoryWarehouseModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouse::getCompanyId),
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceInventoryWarehouse::isActive),
				new FinderColumn<>(
					"commerceInventoryWarehouse.", "countryTwoLettersISOCode",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryWarehouse::getCountryTwoLettersISOCode));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceInventoryWarehouse::getExternalReferenceCode),
				CommerceInventoryWarehouse::getCompanyId),
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE, "",
			new FinderColumn<>(
				"commerceInventoryWarehouse.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryWarehouse::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceInventoryWarehouse.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouse::getCompanyId));

		CommerceInventoryWarehouseUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryWarehouseUtil.setPersistence(null);

		entityCache.removeCache(CommerceInventoryWarehouseImpl.class.getName());
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
		CommerceInventoryWarehouseModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE =
		"SELECT commerceInventoryWarehouse FROM CommerceInventoryWarehouse commerceInventoryWarehouse";

	private static final String _SQL_SELECT_COMMERCEINVENTORYWAREHOUSE_WHERE =
		"SELECT commerceInventoryWarehouse FROM CommerceInventoryWarehouse commerceInventoryWarehouse WHERE ";

	private static final String _SQL_COUNT_COMMERCEINVENTORYWAREHOUSE_WHERE =
		"SELECT COUNT(commerceInventoryWarehouse) FROM CommerceInventoryWarehouse commerceInventoryWarehouse WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryWarehouse exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryWarehousePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "commerceInventoryWarehouseId", "active", "type"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:928583308