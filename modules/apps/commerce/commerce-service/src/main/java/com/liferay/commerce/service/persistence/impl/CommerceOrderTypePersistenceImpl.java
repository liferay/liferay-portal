/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderTypeExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.model.impl.CommerceOrderTypeImpl;
import com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderTypePersistence;
import com.liferay.commerce.service.persistence.CommerceOrderTypeUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
 * The persistence implementation for the commerce order type service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderTypePersistence.class)
public class CommerceOrderTypePersistenceImpl
	extends BasePersistenceImpl<CommerceOrderType, NoSuchOrderTypeException>
	implements CommerceOrderTypePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderTypeUtil</code> to access the commerce order type persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderTypeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommerceOrderType, NoSuchOrderTypeException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce order types where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order type in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByUuid_First(
			String uuid, OrderByComparator<CommerceOrderType> orderByComparator)
		throws NoSuchOrderTypeException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByUuid_First(
		String uuid, OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order types that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce order types where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce order types where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce order types that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order types that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<CommerceOrderType, NoSuchOrderTypeException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce order types where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderType> orderByComparator)
		throws NoSuchOrderTypeException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order types that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce order types where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce order types where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of commerce order types that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order types that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceOrderType, NoSuchOrderTypeException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce order types where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceOrderType> orderByComparator)
		throws NoSuchOrderTypeException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order types that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce order types where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce order types where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce order types that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce order types that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceOrderType, NoSuchOrderTypeException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the commerce order types where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<CommerceOrderType> orderByComparator)
		throws NoSuchOrderTypeException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order types that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the commerce order types where companyId = &#63; and active = &#63; from the database.
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
	 * Returns the number of commerce order types where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, active});
	}

	/**
	 * Returns the number of commerce order types that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce order types that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.filterCount(
			finderCache, new Object[] {companyId, active}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<CommerceOrderType, NoSuchOrderTypeException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the commerce order types where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce order types where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @return the range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce order types where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce order types where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommerceOrderType> orderByComparator)
		throws NoSuchOrderTypeException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first commerce order type in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns all the commerce order types that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByLtD_S(
		Date displayDate, int status) {

		return filterFindByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce order types that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @return the range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByLtD_S(
		Date displayDate, int status, int start, int end) {

		return filterFindByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce order types that the user has permissions to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.filterFind(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce order types where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of commerce order types where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of commerce order types that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce order types that the user has permission to view
	 */
	@Override
	public int filterCountByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.filterCount(
			finderCache, new Object[] {displayDate, status});
	}

	private FilterCollectionPersistenceFinder
		<CommerceOrderType, NoSuchOrderTypeException>
			_collectionPersistenceFinderByLtE_S;

	/**
	 * Returns all the commerce order types where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtE_S(
		Date expirationDate, int status) {

		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce order types where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @return the range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce order types where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce order types where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order types
	 */
	@Override
	public List<CommerceOrderType> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtE_S.find(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order type in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommerceOrderType> orderByComparator)
		throws NoSuchOrderTypeException {

		return _collectionPersistenceFinderByLtE_S.findFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order type in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.fetchFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns all the commerce order types that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByLtE_S(
		Date expirationDate, int status) {

		return filterFindByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce order types that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @return the range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return filterFindByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce order types that the user has permissions to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order types that the user has permission to view
	 */
	@Override
	public List<CommerceOrderType> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceOrderType> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.filterFind(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce order types where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByLtE_S(Date expirationDate, int status) {
		_collectionPersistenceFinderByLtE_S.remove(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of commerce order types where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.count(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of commerce order types that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce order types that the user has permission to view
	 */
	@Override
	public int filterCountByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.filterCount(
			finderCache, new Object[] {expirationDate, status});
	}

	private UniquePersistenceFinder<CommerceOrderType, NoSuchOrderTypeException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce order type where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderTypeException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order type
	 * @throws NoSuchOrderTypeException if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderTypeException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce order type where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public CommerceOrderType fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce order type where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order type that was removed
	 */
	@Override
	public CommerceOrderType removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderTypeException {

		CommerceOrderType commerceOrderType = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceOrderType);
	}

	/**
	 * Returns the number of commerce order types where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order types
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceOrderTypePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceOrderType.class);

		setModelImplClass(CommerceOrderTypeImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderTypeTable.INSTANCE);
	}

	/**
	 * Creates a new commerce order type with the primary key. Does not add the commerce order type to the database.
	 *
	 * @param commerceOrderTypeId the primary key for the new commerce order type
	 * @return the new commerce order type
	 */
	@Override
	public CommerceOrderType create(long commerceOrderTypeId) {
		CommerceOrderType commerceOrderType = new CommerceOrderTypeImpl();

		commerceOrderType.setNew(true);
		commerceOrderType.setPrimaryKey(commerceOrderTypeId);

		String uuid = PortalUUIDUtil.generate();

		commerceOrderType.setUuid(uuid);

		commerceOrderType.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrderType;
	}

	/**
	 * Removes the commerce order type with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type that was removed
	 * @throws NoSuchOrderTypeException if a commerce order type with the primary key could not be found
	 */
	@Override
	public CommerceOrderType remove(long commerceOrderTypeId)
		throws NoSuchOrderTypeException {

		return remove((Serializable)commerceOrderTypeId);
	}

	@Override
	protected CommerceOrderType removeImpl(
		CommerceOrderType commerceOrderType) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrderType)) {
				commerceOrderType = (CommerceOrderType)session.get(
					CommerceOrderTypeImpl.class,
					commerceOrderType.getPrimaryKeyObj());
			}

			if (commerceOrderType != null) {
				session.delete(commerceOrderType);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrderType != null) {
			clearCache(commerceOrderType);
		}

		return commerceOrderType;
	}

	@Override
	public CommerceOrderType updateImpl(CommerceOrderType commerceOrderType) {
		boolean isNew = commerceOrderType.isNew();

		if (!(commerceOrderType instanceof CommerceOrderTypeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrderType.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrderType);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrderType proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrderType implementation " +
					commerceOrderType.getClass());
		}

		CommerceOrderTypeModelImpl commerceOrderTypeModelImpl =
			(CommerceOrderTypeModelImpl)commerceOrderType;

		if (Validator.isNull(commerceOrderType.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceOrderType.setUuid(uuid);
		}

		if (Validator.isNull(commerceOrderType.getExternalReferenceCode())) {
			commerceOrderType.setExternalReferenceCode(
				commerceOrderType.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceOrderTypeModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceOrderType.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceOrderType.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceOrderType.getPrimaryKey();
					}

					try {
						commerceOrderType.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceOrderType.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceOrderType.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceOrderType ercCommerceOrderType = fetchByERC_C(
				commerceOrderType.getExternalReferenceCode(),
				commerceOrderType.getCompanyId());

			if (isNew) {
				if (ercCommerceOrderType != null) {
					throw new DuplicateCommerceOrderTypeExternalReferenceCodeException(
						"Duplicate commerce order type with external reference code " +
							commerceOrderType.getExternalReferenceCode() +
								" and company " +
									commerceOrderType.getCompanyId());
				}
			}
			else {
				if ((ercCommerceOrderType != null) &&
					(commerceOrderType.getCommerceOrderTypeId() !=
						ercCommerceOrderType.getCommerceOrderTypeId())) {

					throw new DuplicateCommerceOrderTypeExternalReferenceCodeException(
						"Duplicate commerce order type with external reference code " +
							commerceOrderType.getExternalReferenceCode() +
								" and company " +
									commerceOrderType.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrderType.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrderType.setCreateDate(date);
			}
			else {
				commerceOrderType.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderTypeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrderType.setModifiedDate(date);
			}
			else {
				commerceOrderType.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrderType);
			}
			else {
				commerceOrderType = (CommerceOrderType)session.merge(
					commerceOrderType);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceOrderType, false);

		if (isNew) {
			commerceOrderType.setNew(false);
		}

		commerceOrderType.resetOriginalValues();

		return commerceOrderType;
	}

	/**
	 * Returns the commerce order type with the primary key or throws a <code>NoSuchOrderTypeException</code> if it could not be found.
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type
	 * @throws NoSuchOrderTypeException if a commerce order type with the primary key could not be found
	 */
	@Override
	public CommerceOrderType findByPrimaryKey(long commerceOrderTypeId)
		throws NoSuchOrderTypeException {

		return findByPrimaryKey((Serializable)commerceOrderTypeId);
	}

	/**
	 * Returns the commerce order type with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type, or <code>null</code> if a commerce order type with the primary key could not be found
	 */
	@Override
	public CommerceOrderType fetchByPrimaryKey(long commerceOrderTypeId) {
		return fetchByPrimaryKey((Serializable)commerceOrderTypeId);
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
		return "commerceOrderTypeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDERTYPE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderTypeModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order type persistence.
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
				_SQL_SELECT_COMMERCEORDERTYPE_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPE_WHERE,
				CommerceOrderTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderType.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceOrderType::getUuid));

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
				_SQL_SELECT_COMMERCEORDERTYPE_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPE_WHERE,
				CommerceOrderTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderType.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceOrderType::getUuid),
				new FinderColumn<>(
					"commerceOrderType.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceOrderType::getCompanyId));

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
				_SQL_SELECT_COMMERCEORDERTYPE_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPE_WHERE,
				CommerceOrderTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderType.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceOrderType::getCompanyId));

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
				_SQL_SELECT_COMMERCEORDERTYPE_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPE_WHERE,
				CommerceOrderTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderType.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceOrderType::getCompanyId),
				new FinderColumn<>(
					"commerceOrderType.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceOrderType::isActive));

		_collectionPersistenceFinderByLtD_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"displayDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
					new String[] {
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"displayDate", "status"}, false),
				_SQL_SELECT_COMMERCEORDERTYPE_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPE_WHERE,
				CommerceOrderTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderType.", "displayDate", FinderColumn.Type.DATE,
					"<", true, true, CommerceOrderType::getDisplayDate),
				new FinderColumn<>(
					"commerceOrderType.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CommerceOrderType::getStatus));

		_collectionPersistenceFinderByLtE_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtE_S",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtE_S",
					new String[] {
						Date.class.getName(), Integer.class.getName()
					},
					new String[] {"expirationDate", "status"}, false),
				_SQL_SELECT_COMMERCEORDERTYPE_WHERE,
				_SQL_COUNT_COMMERCEORDERTYPE_WHERE,
				CommerceOrderTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderType.", "expirationDate",
					FinderColumn.Type.DATE, "<", true, true,
					CommerceOrderType::getExpirationDate),
				new FinderColumn<>(
					"commerceOrderType.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CommerceOrderType::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceOrderType::getExternalReferenceCode),
				CommerceOrderType::getCompanyId),
			_SQL_SELECT_COMMERCEORDERTYPE_WHERE, "",
			new FinderColumn<>(
				"commerceOrderType.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderType::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceOrderType.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceOrderType::getCompanyId));

		CommerceOrderTypeUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderTypeUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderTypeImpl.class.getName());
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
		CommerceOrderTypeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEORDERTYPE =
		"SELECT commerceOrderType FROM CommerceOrderType commerceOrderType";

	private static final String _SQL_SELECT_COMMERCEORDERTYPE_WHERE =
		"SELECT commerceOrderType FROM CommerceOrderType commerceOrderType WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDERTYPE_WHERE =
		"SELECT COUNT(commerceOrderType) FROM CommerceOrderType commerceOrderType WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceOrderType exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderTypePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1343076089