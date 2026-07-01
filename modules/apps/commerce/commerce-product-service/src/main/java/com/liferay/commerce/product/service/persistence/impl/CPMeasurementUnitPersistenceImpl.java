/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPMeasurementUnitExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPMeasurementUnitException;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.model.CPMeasurementUnitTable;
import com.liferay.commerce.product.model.impl.CPMeasurementUnitImpl;
import com.liferay.commerce.product.model.impl.CPMeasurementUnitModelImpl;
import com.liferay.commerce.product.service.persistence.CPMeasurementUnitPersistence;
import com.liferay.commerce.product.service.persistence.CPMeasurementUnitUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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
 * The persistence implementation for the cp measurement unit service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPMeasurementUnitPersistence.class)
public class CPMeasurementUnitPersistenceImpl
	extends BasePersistenceImpl
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
	implements CPMeasurementUnitPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPMeasurementUnitUtil</code> to access the cp measurement unit persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPMeasurementUnitImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp measurement units where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPMeasurementUnitModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp measurement units
	 */
	@Override
	public List<CPMeasurementUnit> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPMeasurementUnit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByUuid_First(
			String uuid, OrderByComparator<CPMeasurementUnit> orderByComparator)
		throws NoSuchCPMeasurementUnitException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByUuid_First(
		String uuid, OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp measurement units where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp measurement units where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp measurement unit where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPMeasurementUnitException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByUUID_G(String uuid, long groupId)
		throws NoSuchCPMeasurementUnitException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp measurement unit where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp measurement unit where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp measurement unit that was removed
	 */
	@Override
	public CPMeasurementUnit removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPMeasurementUnitException {

		CPMeasurementUnit cpMeasurementUnit = findByUUID_G(uuid, groupId);

		return remove(cpMeasurementUnit);
	}

	/**
	 * Returns the number of cp measurement units where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp measurement units where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPMeasurementUnitModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp measurement units
	 */
	@Override
	public List<CPMeasurementUnit> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPMeasurementUnit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPMeasurementUnit> orderByComparator)
		throws NoSuchCPMeasurementUnitException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp measurement units where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp measurement units where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp measurement units where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPMeasurementUnitModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp measurement units
	 */
	@Override
	public List<CPMeasurementUnit> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPMeasurementUnit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByCompanyId_First(
			long companyId,
			OrderByComparator<CPMeasurementUnit> orderByComparator)
		throws NoSuchCPMeasurementUnitException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp measurement units where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp measurement units where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_uniquePersistenceFinderByC_K;

	/**
	 * Returns the cp measurement unit where companyId = &#63; and key = &#63; or throws a <code>NoSuchCPMeasurementUnitException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByC_K(long companyId, String key)
		throws NoSuchCPMeasurementUnitException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {companyId, key});
	}

	/**
	 * Returns the cp measurement unit where companyId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByC_K(
		long companyId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {companyId, key}, useFinderCache);
	}

	/**
	 * Removes the cp measurement unit where companyId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the cp measurement unit that was removed
	 */
	@Override
	public CPMeasurementUnit removeByC_K(long companyId, String key)
		throws NoSuchCPMeasurementUnitException {

		CPMeasurementUnit cpMeasurementUnit = findByC_K(companyId, key);

		return remove(cpMeasurementUnit);
	}

	/**
	 * Returns the number of cp measurement units where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByC_K(long companyId, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {companyId, key});
	}

	private CollectionPersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the cp measurement units where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPMeasurementUnitModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp measurement units
	 */
	@Override
	public List<CPMeasurementUnit> findByC_T(
		long companyId, int type, int start, int end,
		OrderByComparator<CPMeasurementUnit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			finderCache, new Object[] {companyId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByC_T_First(
			long companyId, int type,
			OrderByComparator<CPMeasurementUnit> orderByComparator)
		throws NoSuchCPMeasurementUnitException {

		return _collectionPersistenceFinderByC_T.findFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByC_T_First(
		long companyId, int type,
		OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Removes all the cp measurement units where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long companyId, int type) {
		_collectionPersistenceFinderByC_T.remove(
			finderCache, new Object[] {companyId, type});
	}

	/**
	 * Returns the number of cp measurement units where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByC_T(long companyId, int type) {
		return _collectionPersistenceFinderByC_T.count(
			finderCache, new Object[] {companyId, type});
	}

	private CollectionPersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_collectionPersistenceFinderByC_P_T;

	/**
	 * Returns an ordered range of all the cp measurement units where companyId = &#63; and primary = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPMeasurementUnitModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param type the type
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp measurement units
	 */
	@Override
	public List<CPMeasurementUnit> findByC_P_T(
		long companyId, boolean primary, int type, int start, int end,
		OrderByComparator<CPMeasurementUnit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P_T.find(
			finderCache, new Object[] {companyId, primary, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where companyId = &#63; and primary = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByC_P_T_First(
			long companyId, boolean primary, int type,
			OrderByComparator<CPMeasurementUnit> orderByComparator)
		throws NoSuchCPMeasurementUnitException {

		return _collectionPersistenceFinderByC_P_T.findFirst(
			finderCache, new Object[] {companyId, primary, type},
			orderByComparator);
	}

	/**
	 * Returns the first cp measurement unit in the ordered set where companyId = &#63; and primary = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByC_P_T_First(
		long companyId, boolean primary, int type,
		OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return _collectionPersistenceFinderByC_P_T.fetchFirst(
			finderCache, new Object[] {companyId, primary, type},
			orderByComparator);
	}

	/**
	 * Removes all the cp measurement units where companyId = &#63; and primary = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param type the type
	 */
	@Override
	public void removeByC_P_T(long companyId, boolean primary, int type) {
		_collectionPersistenceFinderByC_P_T.remove(
			finderCache, new Object[] {companyId, primary, type});
	}

	/**
	 * Returns the number of cp measurement units where companyId = &#63; and primary = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param primary the primary
	 * @param type the type
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByC_P_T(long companyId, boolean primary, int type) {
		return _collectionPersistenceFinderByC_P_T.count(
			finderCache, new Object[] {companyId, primary, type});
	}

	private UniquePersistenceFinder
		<CPMeasurementUnit, NoSuchCPMeasurementUnitException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp measurement unit where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPMeasurementUnitException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPMeasurementUnitException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp measurement unit where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp measurement unit where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp measurement unit that was removed
	 */
	@Override
	public CPMeasurementUnit removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPMeasurementUnitException {

		CPMeasurementUnit cpMeasurementUnit = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpMeasurementUnit);
	}

	/**
	 * Returns the number of cp measurement units where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp measurement units
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPMeasurementUnitPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");
		dbColumnNames.put("primary", "primary_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPMeasurementUnit.class);

		setModelImplClass(CPMeasurementUnitImpl.class);
		setModelPKClass(long.class);

		setTable(CPMeasurementUnitTable.INSTANCE);
	}

	/**
	 * Creates a new cp measurement unit with the primary key. Does not add the cp measurement unit to the database.
	 *
	 * @param CPMeasurementUnitId the primary key for the new cp measurement unit
	 * @return the new cp measurement unit
	 */
	@Override
	public CPMeasurementUnit create(long CPMeasurementUnitId) {
		CPMeasurementUnit cpMeasurementUnit = new CPMeasurementUnitImpl();

		cpMeasurementUnit.setNew(true);
		cpMeasurementUnit.setPrimaryKey(CPMeasurementUnitId);

		String uuid = PortalUUIDUtil.generate();

		cpMeasurementUnit.setUuid(uuid);

		cpMeasurementUnit.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpMeasurementUnit;
	}

	/**
	 * Removes the cp measurement unit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPMeasurementUnitId the primary key of the cp measurement unit
	 * @return the cp measurement unit that was removed
	 * @throws NoSuchCPMeasurementUnitException if a cp measurement unit with the primary key could not be found
	 */
	@Override
	public CPMeasurementUnit remove(long CPMeasurementUnitId)
		throws NoSuchCPMeasurementUnitException {

		return remove((Serializable)CPMeasurementUnitId);
	}

	@Override
	protected CPMeasurementUnit removeImpl(
		CPMeasurementUnit cpMeasurementUnit) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpMeasurementUnit)) {
				cpMeasurementUnit = (CPMeasurementUnit)session.get(
					CPMeasurementUnitImpl.class,
					cpMeasurementUnit.getPrimaryKeyObj());
			}

			if ((cpMeasurementUnit != null) &&
				ctPersistenceHelper.isRemove(cpMeasurementUnit)) {

				session.delete(cpMeasurementUnit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpMeasurementUnit != null) {
			clearCache(cpMeasurementUnit);
		}

		return cpMeasurementUnit;
	}

	@Override
	public CPMeasurementUnit updateImpl(CPMeasurementUnit cpMeasurementUnit) {
		boolean isNew = cpMeasurementUnit.isNew();

		if (!(cpMeasurementUnit instanceof CPMeasurementUnitModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpMeasurementUnit.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpMeasurementUnit);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpMeasurementUnit proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPMeasurementUnit implementation " +
					cpMeasurementUnit.getClass());
		}

		CPMeasurementUnitModelImpl cpMeasurementUnitModelImpl =
			(CPMeasurementUnitModelImpl)cpMeasurementUnit;

		if (Validator.isNull(cpMeasurementUnit.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpMeasurementUnit.setUuid(uuid);
		}

		if (Validator.isNull(cpMeasurementUnit.getExternalReferenceCode())) {
			cpMeasurementUnit.setExternalReferenceCode(
				cpMeasurementUnit.getUuid());
		}
		else {
			if (!Objects.equals(
					cpMeasurementUnitModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpMeasurementUnit.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpMeasurementUnit.getCompanyId();

					long groupId = cpMeasurementUnit.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpMeasurementUnit.getPrimaryKey();
					}

					try {
						cpMeasurementUnit.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPMeasurementUnit.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpMeasurementUnit.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPMeasurementUnit ercCPMeasurementUnit = fetchByERC_C(
				cpMeasurementUnit.getExternalReferenceCode(),
				cpMeasurementUnit.getCompanyId());

			if (isNew) {
				if (ercCPMeasurementUnit != null) {
					throw new DuplicateCPMeasurementUnitExternalReferenceCodeException(
						"Duplicate cp measurement unit with external reference code " +
							cpMeasurementUnit.getExternalReferenceCode() +
								" and company " +
									cpMeasurementUnit.getCompanyId());
				}
			}
			else {
				if ((ercCPMeasurementUnit != null) &&
					(cpMeasurementUnit.getCPMeasurementUnitId() !=
						ercCPMeasurementUnit.getCPMeasurementUnitId())) {

					throw new DuplicateCPMeasurementUnitExternalReferenceCodeException(
						"Duplicate cp measurement unit with external reference code " +
							cpMeasurementUnit.getExternalReferenceCode() +
								" and company " +
									cpMeasurementUnit.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpMeasurementUnit.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpMeasurementUnit.setCreateDate(date);
			}
			else {
				cpMeasurementUnit.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpMeasurementUnitModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpMeasurementUnit.setModifiedDate(date);
			}
			else {
				cpMeasurementUnit.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpMeasurementUnit)) {
				if (!isNew) {
					session.evict(
						CPMeasurementUnitImpl.class,
						cpMeasurementUnit.getPrimaryKeyObj());
				}

				session.save(cpMeasurementUnit);
			}
			else {
				cpMeasurementUnit = (CPMeasurementUnit)session.merge(
					cpMeasurementUnit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpMeasurementUnit, false);

		if (isNew) {
			cpMeasurementUnit.setNew(false);
		}

		cpMeasurementUnit.resetOriginalValues();

		return cpMeasurementUnit;
	}

	/**
	 * Returns the cp measurement unit with the primary key or throws a <code>NoSuchCPMeasurementUnitException</code> if it could not be found.
	 *
	 * @param CPMeasurementUnitId the primary key of the cp measurement unit
	 * @return the cp measurement unit
	 * @throws NoSuchCPMeasurementUnitException if a cp measurement unit with the primary key could not be found
	 */
	@Override
	public CPMeasurementUnit findByPrimaryKey(long CPMeasurementUnitId)
		throws NoSuchCPMeasurementUnitException {

		return findByPrimaryKey((Serializable)CPMeasurementUnitId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp measurement unit with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPMeasurementUnitId the primary key of the cp measurement unit
	 * @return the cp measurement unit, or <code>null</code> if a cp measurement unit with the primary key could not be found
	 */
	@Override
	public CPMeasurementUnit fetchByPrimaryKey(long CPMeasurementUnitId) {
		return fetchByPrimaryKey((Serializable)CPMeasurementUnitId);
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
		return "CPMeasurementUnitId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPMEASUREMENTUNIT;
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
		return CPMeasurementUnitModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPMeasurementUnit";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("rate");
		ctMergeColumnNames.add("primary_");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPMeasurementUnitId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "key_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp measurement unit persistence.
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
			_SQL_SELECT_CPMEASUREMENTUNIT_WHERE,
			_SQL_COUNT_CPMEASUREMENTUNIT_WHERE,
			CPMeasurementUnitModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"cpMeasurementUnit.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CPMeasurementUnit::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPMeasurementUnit::getUuid),
				CPMeasurementUnit::getGroupId),
			_SQL_SELECT_CPMEASUREMENTUNIT_WHERE, "",
			new FinderColumn<>(
				"cpMeasurementUnit.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CPMeasurementUnit::getUuid),
			new FinderColumn<>(
				"cpMeasurementUnit.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPMeasurementUnit::getGroupId));

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
				_SQL_SELECT_CPMEASUREMENTUNIT_WHERE,
				_SQL_COUNT_CPMEASUREMENTUNIT_WHERE,
				CPMeasurementUnitModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"cpMeasurementUnit.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPMeasurementUnit::getUuid),
				new FinderColumn<>(
					"cpMeasurementUnit.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CPMeasurementUnit::getCompanyId));

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
				_SQL_SELECT_CPMEASUREMENTUNIT_WHERE,
				_SQL_COUNT_CPMEASUREMENTUNIT_WHERE,
				CPMeasurementUnitModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"cpMeasurementUnit.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CPMeasurementUnit::getCompanyId));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 2, 2, false,
				CPMeasurementUnit::getCompanyId,
				convertCaseFunction(CPMeasurementUnit::getKey)),
			_SQL_SELECT_CPMEASUREMENTUNIT_WHERE, "",
			new FinderColumn<>(
				"cpMeasurementUnit.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CPMeasurementUnit::getCompanyId),
			new FinderColumn<>(
				"cpMeasurementUnit.", "key", "key_", FinderColumn.Type.STRING,
				"=", false, true, CPMeasurementUnit::getKey));

		_collectionPersistenceFinderByC_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "type_"}, false),
			_SQL_SELECT_CPMEASUREMENTUNIT_WHERE,
			_SQL_COUNT_CPMEASUREMENTUNIT_WHERE,
			CPMeasurementUnitModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"cpMeasurementUnit.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CPMeasurementUnit::getCompanyId),
			new FinderColumn<>(
				"cpMeasurementUnit.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CPMeasurementUnit::getType));

		_collectionPersistenceFinderByC_P_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "primary_", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "primary_", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "primary_", "type_"}, false),
			_SQL_SELECT_CPMEASUREMENTUNIT_WHERE,
			_SQL_COUNT_CPMEASUREMENTUNIT_WHERE,
			CPMeasurementUnitModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"cpMeasurementUnit.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CPMeasurementUnit::getCompanyId),
			new FinderColumn<>(
				"cpMeasurementUnit.", "primary", "primary_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CPMeasurementUnit::isPrimary),
			new FinderColumn<>(
				"cpMeasurementUnit.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CPMeasurementUnit::getType));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CPMeasurementUnit::getExternalReferenceCode),
				CPMeasurementUnit::getCompanyId),
			_SQL_SELECT_CPMEASUREMENTUNIT_WHERE, "",
			new FinderColumn<>(
				"cpMeasurementUnit.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPMeasurementUnit::getExternalReferenceCode),
			new FinderColumn<>(
				"cpMeasurementUnit.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CPMeasurementUnit::getCompanyId));

		CPMeasurementUnitUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPMeasurementUnitUtil.setPersistence(null);

		entityCache.removeCache(CPMeasurementUnitImpl.class.getName());
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
		CPMeasurementUnitModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPMEASUREMENTUNIT =
		"SELECT cpMeasurementUnit FROM CPMeasurementUnit cpMeasurementUnit";

	private static final String _SQL_SELECT_CPMEASUREMENTUNIT_WHERE =
		"SELECT cpMeasurementUnit FROM CPMeasurementUnit cpMeasurementUnit WHERE ";

	private static final String _SQL_COUNT_CPMEASUREMENTUNIT_WHERE =
		"SELECT COUNT(cpMeasurementUnit) FROM CPMeasurementUnit cpMeasurementUnit WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key", "primary", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-375124758