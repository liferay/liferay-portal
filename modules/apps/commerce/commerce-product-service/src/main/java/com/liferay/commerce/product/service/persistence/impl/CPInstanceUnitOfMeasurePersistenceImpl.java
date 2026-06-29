/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasureTable;
import com.liferay.commerce.product.model.impl.CPInstanceUnitOfMeasureImpl;
import com.liferay.commerce.product.model.impl.CPInstanceUnitOfMeasureModelImpl;
import com.liferay.commerce.product.service.persistence.CPInstanceUnitOfMeasurePersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceUnitOfMeasureUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cp instance unit of measure service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPInstanceUnitOfMeasurePersistence.class)
public class CPInstanceUnitOfMeasurePersistenceImpl
	extends BasePersistenceImpl
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
	implements CPInstanceUnitOfMeasurePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPInstanceUnitOfMeasureUtil</code> to access the cp instance unit of measure persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPInstanceUnitOfMeasureImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByUuid_First(
			String uuid,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByUuid_First(
		String uuid,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp instance unit of measures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp instance unit of measures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByCPInstanceId;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceId.find(
			finderCache, new Object[] {CPInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByCPInstanceId.findFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceId.fetchFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByCPInstanceId(long CPInstanceId) {
		_collectionPersistenceFinderByCPInstanceId.remove(
			finderCache, new Object[] {CPInstanceId});
	}

	/**
	 * Returns the number of cp instance unit of measures where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByCPInstanceId(long CPInstanceId) {
		return _collectionPersistenceFinderByCPInstanceId.count(
			finderCache, new Object[] {CPInstanceId});
	}

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where companyId = &#63; and sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByC_S(
		long companyId, String sku, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, sku}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByC_S_First(
			long companyId, String sku,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, sku}, orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByC_S_First(
		long companyId, String sku,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, sku}, orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where companyId = &#63; and sku = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 */
	@Override
	public void removeByC_S(long companyId, String sku) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, sku});
	}

	/**
	 * Returns the number of cp instance unit of measures where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByC_S(long companyId, String sku) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, sku});
	}

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where CPInstanceId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param active the active
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByC_A(
		long CPInstanceId, boolean active, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {CPInstanceId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where CPInstanceId = &#63; and active = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByC_A_First(
			long CPInstanceId, boolean active,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {CPInstanceId, active},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where CPInstanceId = &#63; and active = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByC_A_First(
		long CPInstanceId, boolean active,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {CPInstanceId, active},
			orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where CPInstanceId = &#63; and active = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long CPInstanceId, boolean active) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {CPInstanceId, active});
	}

	/**
	 * Returns the number of cp instance unit of measures where CPInstanceId = &#63; and active = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param active the active
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByC_A(long CPInstanceId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {CPInstanceId, active});
	}

	private UniquePersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_uniquePersistenceFinderByC_K;

	/**
	 * Returns the cp instance unit of measure where CPInstanceId = &#63; and key = &#63; or throws a <code>NoSuchCPInstanceUnitOfMeasureException</code> if it could not be found.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param key the key
	 * @return the matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByC_K(long CPInstanceId, String key)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {CPInstanceId, key});
	}

	/**
	 * Returns the cp instance unit of measure where CPInstanceId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByC_K(
		long CPInstanceId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {CPInstanceId, key}, useFinderCache);
	}

	/**
	 * Removes the cp instance unit of measure where CPInstanceId = &#63; and key = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param key the key
	 * @return the cp instance unit of measure that was removed
	 */
	@Override
	public CPInstanceUnitOfMeasure removeByC_K(long CPInstanceId, String key)
		throws NoSuchCPInstanceUnitOfMeasureException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure = findByC_K(
			CPInstanceId, key);

		return remove(cpInstanceUnitOfMeasure);
	}

	/**
	 * Returns the number of cp instance unit of measures where CPInstanceId = &#63; and key = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param key the key
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByC_K(long CPInstanceId, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {CPInstanceId, key});
	}

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByC_P;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where CPInstanceId = &#63; and primary = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param primary the primary
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByC_P(
		long CPInstanceId, boolean primary, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P.find(
			finderCache, new Object[] {CPInstanceId, primary}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where CPInstanceId = &#63; and primary = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param primary the primary
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByC_P_First(
			long CPInstanceId, boolean primary,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByC_P.findFirst(
			finderCache, new Object[] {CPInstanceId, primary},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where CPInstanceId = &#63; and primary = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param primary the primary
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByC_P_First(
		long CPInstanceId, boolean primary,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByC_P.fetchFirst(
			finderCache, new Object[] {CPInstanceId, primary},
			orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where CPInstanceId = &#63; and primary = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param primary the primary
	 */
	@Override
	public void removeByC_P(long CPInstanceId, boolean primary) {
		_collectionPersistenceFinderByC_P.remove(
			finderCache, new Object[] {CPInstanceId, primary});
	}

	/**
	 * Returns the number of cp instance unit of measures where CPInstanceId = &#63; and primary = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param primary the primary
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByC_P(long CPInstanceId, boolean primary) {
		return _collectionPersistenceFinderByC_P.count(
			finderCache, new Object[] {CPInstanceId, primary});
	}

	private CollectionPersistenceFinder
		<CPInstanceUnitOfMeasure, NoSuchCPInstanceUnitOfMeasureException>
			_collectionPersistenceFinderByC_K_S;

	/**
	 * Returns an ordered range of all the cp instance unit of measures where companyId = &#63; and key = &#63; and sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceUnitOfMeasureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param sku the sku
	 * @param start the lower bound of the range of cp instance unit of measures
	 * @param end the upper bound of the range of cp instance unit of measures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance unit of measures
	 */
	@Override
	public List<CPInstanceUnitOfMeasure> findByC_K_S(
		long companyId, String key, String sku, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_K_S.find(
			finderCache, new Object[] {companyId, key, sku}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where companyId = &#63; and key = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByC_K_S_First(
			long companyId, String key, String sku,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return _collectionPersistenceFinderByC_K_S.findFirst(
			finderCache, new Object[] {companyId, key, sku}, orderByComparator);
	}

	/**
	 * Returns the first cp instance unit of measure in the ordered set where companyId = &#63; and key = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance unit of measure, or <code>null</code> if a matching cp instance unit of measure could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByC_K_S_First(
		long companyId, String key, String sku,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return _collectionPersistenceFinderByC_K_S.fetchFirst(
			finderCache, new Object[] {companyId, key, sku}, orderByComparator);
	}

	/**
	 * Removes all the cp instance unit of measures where companyId = &#63; and key = &#63; and sku = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param sku the sku
	 */
	@Override
	public void removeByC_K_S(long companyId, String key, String sku) {
		_collectionPersistenceFinderByC_K_S.remove(
			finderCache, new Object[] {companyId, key, sku});
	}

	/**
	 * Returns the number of cp instance unit of measures where companyId = &#63; and key = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param sku the sku
	 * @return the number of matching cp instance unit of measures
	 */
	@Override
	public int countByC_K_S(long companyId, String key, String sku) {
		return _collectionPersistenceFinderByC_K_S.count(
			finderCache, new Object[] {companyId, key, sku});
	}

	public CPInstanceUnitOfMeasurePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("CPInstanceUnitOfMeasureId", "CPInstanceUOMId");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("key", "key_");
		dbColumnNames.put("precision", "precision_");
		dbColumnNames.put("primary", "primary_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPInstanceUnitOfMeasure.class);

		setModelImplClass(CPInstanceUnitOfMeasureImpl.class);
		setModelPKClass(long.class);

		setTable(CPInstanceUnitOfMeasureTable.INSTANCE);
	}

	/**
	 * Creates a new cp instance unit of measure with the primary key. Does not add the cp instance unit of measure to the database.
	 *
	 * @param CPInstanceUnitOfMeasureId the primary key for the new cp instance unit of measure
	 * @return the new cp instance unit of measure
	 */
	@Override
	public CPInstanceUnitOfMeasure create(long CPInstanceUnitOfMeasureId) {
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			new CPInstanceUnitOfMeasureImpl();

		cpInstanceUnitOfMeasure.setNew(true);
		cpInstanceUnitOfMeasure.setPrimaryKey(CPInstanceUnitOfMeasureId);

		String uuid = PortalUUIDUtil.generate();

		cpInstanceUnitOfMeasure.setUuid(uuid);

		cpInstanceUnitOfMeasure.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpInstanceUnitOfMeasure;
	}

	/**
	 * Removes the cp instance unit of measure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPInstanceUnitOfMeasureId the primary key of the cp instance unit of measure
	 * @return the cp instance unit of measure that was removed
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a cp instance unit of measure with the primary key could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure remove(long CPInstanceUnitOfMeasureId)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return remove((Serializable)CPInstanceUnitOfMeasureId);
	}

	@Override
	protected CPInstanceUnitOfMeasure removeImpl(
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpInstanceUnitOfMeasure)) {
				cpInstanceUnitOfMeasure = (CPInstanceUnitOfMeasure)session.get(
					CPInstanceUnitOfMeasureImpl.class,
					cpInstanceUnitOfMeasure.getPrimaryKeyObj());
			}

			if ((cpInstanceUnitOfMeasure != null) &&
				ctPersistenceHelper.isRemove(cpInstanceUnitOfMeasure)) {

				session.delete(cpInstanceUnitOfMeasure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpInstanceUnitOfMeasure != null) {
			clearCache(cpInstanceUnitOfMeasure);
		}

		return cpInstanceUnitOfMeasure;
	}

	@Override
	public CPInstanceUnitOfMeasure updateImpl(
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure) {

		boolean isNew = cpInstanceUnitOfMeasure.isNew();

		if (!(cpInstanceUnitOfMeasure instanceof
				CPInstanceUnitOfMeasureModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpInstanceUnitOfMeasure.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpInstanceUnitOfMeasure);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpInstanceUnitOfMeasure proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPInstanceUnitOfMeasure implementation " +
					cpInstanceUnitOfMeasure.getClass());
		}

		CPInstanceUnitOfMeasureModelImpl cpInstanceUnitOfMeasureModelImpl =
			(CPInstanceUnitOfMeasureModelImpl)cpInstanceUnitOfMeasure;

		if (Validator.isNull(cpInstanceUnitOfMeasure.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpInstanceUnitOfMeasure.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpInstanceUnitOfMeasure.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpInstanceUnitOfMeasure.setCreateDate(date);
			}
			else {
				cpInstanceUnitOfMeasure.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpInstanceUnitOfMeasureModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpInstanceUnitOfMeasure.setModifiedDate(date);
			}
			else {
				cpInstanceUnitOfMeasure.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpInstanceUnitOfMeasure)) {
				if (!isNew) {
					session.evict(
						CPInstanceUnitOfMeasureImpl.class,
						cpInstanceUnitOfMeasure.getPrimaryKeyObj());
				}

				session.save(cpInstanceUnitOfMeasure);
			}
			else {
				cpInstanceUnitOfMeasure =
					(CPInstanceUnitOfMeasure)session.merge(
						cpInstanceUnitOfMeasure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpInstanceUnitOfMeasure, false);

		if (isNew) {
			cpInstanceUnitOfMeasure.setNew(false);
		}

		cpInstanceUnitOfMeasure.resetOriginalValues();

		return cpInstanceUnitOfMeasure;
	}

	/**
	 * Returns the cp instance unit of measure with the primary key or throws a <code>NoSuchCPInstanceUnitOfMeasureException</code> if it could not be found.
	 *
	 * @param CPInstanceUnitOfMeasureId the primary key of the cp instance unit of measure
	 * @return the cp instance unit of measure
	 * @throws NoSuchCPInstanceUnitOfMeasureException if a cp instance unit of measure with the primary key could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure findByPrimaryKey(
			long CPInstanceUnitOfMeasureId)
		throws NoSuchCPInstanceUnitOfMeasureException {

		return findByPrimaryKey((Serializable)CPInstanceUnitOfMeasureId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp instance unit of measure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPInstanceUnitOfMeasureId the primary key of the cp instance unit of measure
	 * @return the cp instance unit of measure, or <code>null</code> if a cp instance unit of measure with the primary key could not be found
	 */
	@Override
	public CPInstanceUnitOfMeasure fetchByPrimaryKey(
		long CPInstanceUnitOfMeasureId) {

		return fetchByPrimaryKey((Serializable)CPInstanceUnitOfMeasureId);
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
		return "CPInstanceUOMId";
	}

	@Override
	protected String getPKFieldName() {
		return "CPInstanceUnitOfMeasureId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPINSTANCEUNITOFMEASURE;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return CPInstanceUnitOfMeasureModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPInstanceUOM";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPInstanceId");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("incrementalOrderQuantity");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("precision_");
		ctMergeColumnNames.add("pricingQuantity");
		ctMergeColumnNames.add("primary_");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("rate");
		ctMergeColumnNames.add("sku");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPInstanceUOMId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"CPInstanceId", "key_"});
	}

	/**
	 * Initializes the cp instance unit of measure persistence.
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
			_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
			_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
			CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstanceUnitOfMeasure::getUuid));

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
				_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
				_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
				CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpInstanceUnitOfMeasure.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPInstanceUnitOfMeasure::getUuid),
				new FinderColumn<>(
					"cpInstanceUnitOfMeasure.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceUnitOfMeasure::getCompanyId));

		_collectionPersistenceFinderByCPInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, false),
				_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
				_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
				CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpInstanceUnitOfMeasure.", "CPInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceUnitOfMeasure::getCPInstanceId));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "sku"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "sku"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "sku"}, 0, 2, false, null),
			_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
			_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
			CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPInstanceUnitOfMeasure::getCompanyId),
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "sku", FinderColumn.Type.STRING,
				"=", true, true, CPInstanceUnitOfMeasure::getSku));

		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CPInstanceId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"CPInstanceId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"CPInstanceId", "active_"}, false),
			_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
			_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
			CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "CPInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				CPInstanceUnitOfMeasure::getCPInstanceId),
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CPInstanceUnitOfMeasure::isActive));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPInstanceId", "key_"}, 0, 2, false,
				CPInstanceUnitOfMeasure::getCPInstanceId,
				convertNullFunction(CPInstanceUnitOfMeasure::getKey)),
			_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE, "",
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "CPInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				CPInstanceUnitOfMeasure::getCPInstanceId),
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstanceUnitOfMeasure::getKey));

		_collectionPersistenceFinderByC_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CPInstanceId", "primary_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"CPInstanceId", "primary_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"CPInstanceId", "primary_"}, false),
			_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
			_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
			CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "CPInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				CPInstanceUnitOfMeasure::getCPInstanceId),
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "primary", "primary_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CPInstanceUnitOfMeasure::isPrimary));

		_collectionPersistenceFinderByC_K_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_K_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "key_", "sku"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_K_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "key_", "sku"}, 0, 6, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_K_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "key_", "sku"}, 0, 6, false, null),
			_SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE,
			_SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE,
			CPInstanceUnitOfMeasureModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPInstanceUnitOfMeasure::getCompanyId),
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstanceUnitOfMeasure::getKey),
			new FinderColumn<>(
				"cpInstanceUnitOfMeasure.", "sku", FinderColumn.Type.STRING,
				"=", true, true, CPInstanceUnitOfMeasure::getSku));

		CPInstanceUnitOfMeasureUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPInstanceUnitOfMeasureUtil.setPersistence(null);

		entityCache.removeCache(CPInstanceUnitOfMeasureImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CPInstanceUnitOfMeasureModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPINSTANCEUNITOFMEASURE =
		"SELECT cpInstanceUnitOfMeasure FROM CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure";

	private static final String _SQL_SELECT_CPINSTANCEUNITOFMEASURE_WHERE =
		"SELECT cpInstanceUnitOfMeasure FROM CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure WHERE ";

	private static final String _SQL_COUNT_CPINSTANCEUNITOFMEASURE_WHERE =
		"SELECT COUNT(cpInstanceUnitOfMeasure) FROM CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPInstanceUnitOfMeasure exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPInstanceUnitOfMeasurePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "CPInstanceUnitOfMeasureId", "active", "key", "precision",
			"primary"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1826119027