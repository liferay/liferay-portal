/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.exception.DuplicateCommercePriceEntryExternalReferenceCodeException;
import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceEntryTable;
import com.liferay.commerce.price.list.model.impl.CommercePriceEntryImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryUtil;
import com.liferay.commerce.price.list.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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

import java.math.BigDecimal;

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
 * The persistence implementation for the commerce price entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommercePriceEntryPersistence.class)
public class CommercePriceEntryPersistenceImpl
	extends BasePersistenceImpl<CommercePriceEntry, NoSuchPriceEntryException>
	implements CommercePriceEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePriceEntryUtil</code> to access the commerce price entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePriceEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce price entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByUuid_First(
			String uuid,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByUuid_First(
		String uuid, OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce price entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce price entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce price entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByCommercePriceListId;

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByCommercePriceListId(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceListId.find(
			finderCache, new Object[] {commercePriceListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByCommercePriceListId_First(
			long commercePriceListId,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByCommercePriceListId.findFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByCommercePriceListId_First(
		long commercePriceListId,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceListId.fetchFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where commercePriceListId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 */
	@Override
	public void removeByCommercePriceListId(long commercePriceListId) {
		_collectionPersistenceFinderByCommercePriceListId.remove(
			finderCache, new Object[] {commercePriceListId});
	}

	/**
	 * Returns the number of commerce price entries where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByCommercePriceListId(long commercePriceListId) {
		return _collectionPersistenceFinderByCommercePriceListId.count(
			finderCache, new Object[] {commercePriceListId});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByCPInstanceUuid;

	/**
	 * Returns an ordered range of all the commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceUuid.find(
			finderCache, new Object[] {CPInstanceUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByCPInstanceUuid.findFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceUuid.fetchFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByCPInstanceUuid(String CPInstanceUuid) {
		_collectionPersistenceFinderByCPInstanceUuid.remove(
			finderCache, new Object[] {CPInstanceUuid});
	}

	/**
	 * Returns the number of commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByCPInstanceUuid(String CPInstanceUuid) {
		return _collectionPersistenceFinderByCPInstanceUuid.count(
			finderCache, new Object[] {CPInstanceUuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByC_C(
		long commercePriceListId, String CPInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {commercePriceListId, CPInstanceUuid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByC_C_First(
			long commercePriceListId, String CPInstanceUuid,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {commercePriceListId, CPInstanceUuid},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByC_C_First(
		long commercePriceListId, String CPInstanceUuid,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {commercePriceListId, CPInstanceUuid},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByC_C(long commercePriceListId, String CPInstanceUuid) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {commercePriceListId, CPInstanceUuid});
	}

	/**
	 * Returns the number of commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByC_C(long commercePriceListId, String CPInstanceUuid) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {commercePriceListId, CPInstanceUuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where displayDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByLtE_S;

	/**
	 * Returns all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status) {

		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtE_S.find(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByLtE_S.findFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.fetchFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where expirationDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.count(
			finderCache, new Object[] {expirationDate, status});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByC_C_S;

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status, int start,
		int end, OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_S.find(
			finderCache,
			new Object[] {commercePriceListId, CPInstanceUuid, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByC_C_S_First(
			long commercePriceListId, String CPInstanceUuid, int status,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByC_C_S.findFirst(
			finderCache,
			new Object[] {commercePriceListId, CPInstanceUuid, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByC_C_S_First(
		long commercePriceListId, String CPInstanceUuid, int status,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_S.fetchFirst(
			finderCache,
			new Object[] {commercePriceListId, CPInstanceUuid, status},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 */
	@Override
	public void removeByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status) {

		_collectionPersistenceFinderByC_C_S.remove(
			finderCache,
			new Object[] {commercePriceListId, CPInstanceUuid, status});
	}

	/**
	 * Returns the number of commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status) {

		return _collectionPersistenceFinderByC_C_S.count(
			finderCache,
			new Object[] {commercePriceListId, CPInstanceUuid, status});
	}

	private CollectionPersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_collectionPersistenceFinderByC_Q_U;

	/**
	 * Returns an ordered range of all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	@Override
	public List<CommercePriceEntry> findByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
		int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_Q_U.find(
			finderCache,
			new Object[] {CPInstanceUuid, quantity, unitOfMeasureKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByC_Q_U_First(
			String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws NoSuchPriceEntryException {

		return _collectionPersistenceFinderByC_Q_U.findFirst(
			finderCache,
			new Object[] {CPInstanceUuid, quantity, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByC_Q_U_First(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_Q_U.fetchFirst(
			finderCache,
			new Object[] {CPInstanceUuid, quantity, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 */
	@Override
	public void removeByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey) {

		_collectionPersistenceFinderByC_Q_U.remove(
			finderCache,
			new Object[] {CPInstanceUuid, quantity, unitOfMeasureKey});
	}

	/**
	 * Returns the number of commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey) {

		return _collectionPersistenceFinderByC_Q_U.count(
			finderCache,
			new Object[] {CPInstanceUuid, quantity, unitOfMeasureKey});
	}

	private UniquePersistenceFinder
		<CommercePriceEntry, NoSuchPriceEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchPriceEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchPriceEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce price entry that was removed
	 */
	@Override
	public CommercePriceEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchPriceEntryException {

		CommercePriceEntry commercePriceEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commercePriceEntry);
	}

	/**
	 * Returns the number of commerce price entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce price entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommercePriceEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePriceEntry.class);

		setModelImplClass(CommercePriceEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePriceEntryTable.INSTANCE);
	}

	/**
	 * Creates a new commerce price entry with the primary key. Does not add the commerce price entry to the database.
	 *
	 * @param commercePriceEntryId the primary key for the new commerce price entry
	 * @return the new commerce price entry
	 */
	@Override
	public CommercePriceEntry create(long commercePriceEntryId) {
		CommercePriceEntry commercePriceEntry = new CommercePriceEntryImpl();

		commercePriceEntry.setNew(true);
		commercePriceEntry.setPrimaryKey(commercePriceEntryId);

		String uuid = PortalUUIDUtil.generate();

		commercePriceEntry.setUuid(uuid);

		commercePriceEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commercePriceEntry;
	}

	/**
	 * Removes the commerce price entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry that was removed
	 * @throws NoSuchPriceEntryException if a commerce price entry with the primary key could not be found
	 */
	@Override
	public CommercePriceEntry remove(long commercePriceEntryId)
		throws NoSuchPriceEntryException {

		return remove((Serializable)commercePriceEntryId);
	}

	@Override
	protected CommercePriceEntry removeImpl(
		CommercePriceEntry commercePriceEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePriceEntry)) {
				commercePriceEntry = (CommercePriceEntry)session.get(
					CommercePriceEntryImpl.class,
					commercePriceEntry.getPrimaryKeyObj());
			}

			if ((commercePriceEntry != null) &&
				ctPersistenceHelper.isRemove(commercePriceEntry)) {

				session.delete(commercePriceEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePriceEntry != null) {
			clearCache(commercePriceEntry);
		}

		return commercePriceEntry;
	}

	@Override
	public CommercePriceEntry updateImpl(
		CommercePriceEntry commercePriceEntry) {

		boolean isNew = commercePriceEntry.isNew();

		if (!(commercePriceEntry instanceof CommercePriceEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commercePriceEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePriceEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePriceEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePriceEntry implementation " +
					commercePriceEntry.getClass());
		}

		CommercePriceEntryModelImpl commercePriceEntryModelImpl =
			(CommercePriceEntryModelImpl)commercePriceEntry;

		if (Validator.isNull(commercePriceEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commercePriceEntry.setUuid(uuid);
		}

		if (Validator.isNull(commercePriceEntry.getExternalReferenceCode())) {
			commercePriceEntry.setExternalReferenceCode(
				commercePriceEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					commercePriceEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commercePriceEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commercePriceEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commercePriceEntry.getPrimaryKey();
					}

					try {
						commercePriceEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommercePriceEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commercePriceEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommercePriceEntry ercCommercePriceEntry = fetchByERC_C(
				commercePriceEntry.getExternalReferenceCode(),
				commercePriceEntry.getCompanyId());

			if (isNew) {
				if (ercCommercePriceEntry != null) {
					throw new DuplicateCommercePriceEntryExternalReferenceCodeException(
						"Duplicate commerce price entry with external reference code " +
							commercePriceEntry.getExternalReferenceCode() +
								" and company " +
									commercePriceEntry.getCompanyId());
				}
			}
			else {
				if ((ercCommercePriceEntry != null) &&
					(commercePriceEntry.getCommercePriceEntryId() !=
						ercCommercePriceEntry.getCommercePriceEntryId())) {

					throw new DuplicateCommercePriceEntryExternalReferenceCodeException(
						"Duplicate commerce price entry with external reference code " +
							commercePriceEntry.getExternalReferenceCode() +
								" and company " +
									commercePriceEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePriceEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePriceEntry.setCreateDate(date);
			}
			else {
				commercePriceEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePriceEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePriceEntry.setModifiedDate(date);
			}
			else {
				commercePriceEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commercePriceEntry)) {
				if (!isNew) {
					session.evict(
						CommercePriceEntryImpl.class,
						commercePriceEntry.getPrimaryKeyObj());
				}

				session.save(commercePriceEntry);
			}
			else {
				commercePriceEntry = (CommercePriceEntry)session.merge(
					commercePriceEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePriceEntry, false);

		if (isNew) {
			commercePriceEntry.setNew(false);
		}

		commercePriceEntry.resetOriginalValues();

		return commercePriceEntry;
	}

	/**
	 * Returns the commerce price entry with the primary key or throws a <code>NoSuchPriceEntryException</code> if it could not be found.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry
	 * @throws NoSuchPriceEntryException if a commerce price entry with the primary key could not be found
	 */
	@Override
	public CommercePriceEntry findByPrimaryKey(long commercePriceEntryId)
		throws NoSuchPriceEntryException {

		return findByPrimaryKey((Serializable)commercePriceEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce price entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry, or <code>null</code> if a commerce price entry with the primary key could not be found
	 */
	@Override
	public CommercePriceEntry fetchByPrimaryKey(long commercePriceEntryId) {
		return fetchByPrimaryKey((Serializable)commercePriceEntryId);
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
		return "commercePriceEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICEENTRY;
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
		return CommercePriceEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommercePriceEntry";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("commercePriceListId");
		ctMergeColumnNames.add("CPInstanceUuid");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("bulkPricing");
		ctMergeColumnNames.add("discountDiscovery");
		ctMergeColumnNames.add("discountLevel1");
		ctMergeColumnNames.add("discountLevel2");
		ctMergeColumnNames.add("discountLevel3");
		ctMergeColumnNames.add("discountLevel4");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("hasTierPrice");
		ctMergeColumnNames.add("price");
		ctMergeColumnNames.add("priceOnApplication");
		ctMergeColumnNames.add("pricingQuantity");
		ctMergeColumnNames.add("promoPrice");
		ctMergeColumnNames.add("quantity");
		ctMergeColumnNames.add("unitOfMeasureKey");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("commercePriceEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the commerce price entry persistence.
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
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
			CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commercePriceEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceEntry::getUuid));

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
				_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
				CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commercePriceEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommercePriceEntry::getUuid),
				new FinderColumn<>(
					"commercePriceEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommercePriceEntry::getCompanyId));

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
				_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
				CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commercePriceEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommercePriceEntry::getCompanyId));

		_collectionPersistenceFinderByCommercePriceListId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePriceListId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePriceListId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePriceListId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceListId"}, false),
				_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
				CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commercePriceEntry.", "commercePriceListId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceEntry::getCommercePriceListId));

		_collectionPersistenceFinderByCPInstanceUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceUuid",
					new String[] {String.class.getName()},
					new String[] {"CPInstanceUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceUuid",
					new String[] {String.class.getName()},
					new String[] {"CPInstanceUuid"}, 0, 1, false, null),
				_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
				CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commercePriceEntry.", "CPInstanceUuid",
					FinderColumn.Type.STRING, "=", true, true,
					CommercePriceEntry::getCPInstanceUuid));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commercePriceListId", "CPInstanceUuid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"commercePriceListId", "CPInstanceUuid"}, 0, 2,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"commercePriceListId", "CPInstanceUuid"}, 0, 2,
				false, null),
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
			CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commercePriceEntry.", "commercePriceListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceEntry::getCommercePriceListId),
			new FinderColumn<>(
				"commercePriceEntry.", "CPInstanceUuid",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceEntry::getCPInstanceUuid));

		_collectionPersistenceFinderByLtD_S = new CollectionPersistenceFinder<>(
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
				new String[] {Date.class.getName(), Integer.class.getName()},
				new String[] {"displayDate", "status"}, false),
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
			CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commercePriceEntry.", "displayDate", FinderColumn.Type.DATE,
				"<", true, true, CommercePriceEntry::getDisplayDate),
			new FinderColumn<>(
				"commercePriceEntry.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CommercePriceEntry::getStatus));

		_collectionPersistenceFinderByLtE_S = new CollectionPersistenceFinder<>(
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
				new String[] {Date.class.getName(), Integer.class.getName()},
				new String[] {"expirationDate", "status"}, false),
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
			CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commercePriceEntry.", "expirationDate", FinderColumn.Type.DATE,
				"<", true, true, CommercePriceEntry::getExpirationDate),
			new FinderColumn<>(
				"commercePriceEntry.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CommercePriceEntry::getStatus));

		_collectionPersistenceFinderByC_C_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"commercePriceListId", "CPInstanceUuid", "status"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"commercePriceListId", "CPInstanceUuid", "status"
				},
				0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"commercePriceListId", "CPInstanceUuid", "status"
				},
				0, 2, false, null),
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
			CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commercePriceEntry.", "commercePriceListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceEntry::getCommercePriceListId),
			new FinderColumn<>(
				"commercePriceEntry.", "CPInstanceUuid",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceEntry::getCPInstanceUuid),
			new FinderColumn<>(
				"commercePriceEntry.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CommercePriceEntry::getStatus));

		_collectionPersistenceFinderByC_Q_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_Q_U",
				new String[] {
					String.class.getName(), BigDecimal.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"CPInstanceUuid", "quantity", "unitOfMeasureKey"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_Q_U",
				new String[] {
					String.class.getName(), BigDecimal.class.getName(),
					String.class.getName()
				},
				new String[] {"CPInstanceUuid", "quantity", "unitOfMeasureKey"},
				0, 5, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_Q_U",
				new String[] {
					String.class.getName(), BigDecimal.class.getName(),
					String.class.getName()
				},
				new String[] {"CPInstanceUuid", "quantity", "unitOfMeasureKey"},
				0, 5, false, null),
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCEPRICEENTRY_WHERE,
			CommercePriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commercePriceEntry.", "CPInstanceUuid",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceEntry::getCPInstanceUuid),
			new FinderColumn<>(
				"commercePriceEntry.", "quantity",
				FinderColumn.Type.BIG_DECIMAL, "=", true, true,
				CommercePriceEntry::getQuantity),
			new FinderColumn<>(
				"commercePriceEntry.", "unitOfMeasureKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceEntry::getUnitOfMeasureKey));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommercePriceEntry::getExternalReferenceCode),
				CommercePriceEntry::getCompanyId),
			_SQL_SELECT_COMMERCEPRICEENTRY_WHERE, "",
			new FinderColumn<>(
				"commercePriceEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"commercePriceEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommercePriceEntry::getCompanyId));

		CommercePriceEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePriceEntryUtil.setPersistence(null);

		entityCache.removeCache(CommercePriceEntryImpl.class.getName());
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
		CommercePriceEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPRICEENTRY =
		"SELECT commercePriceEntry FROM CommercePriceEntry commercePriceEntry";

	private static final String _SQL_SELECT_COMMERCEPRICEENTRY_WHERE =
		"SELECT commercePriceEntry FROM CommercePriceEntry commercePriceEntry WHERE ";

	private static final String _SQL_COUNT_COMMERCEPRICEENTRY_WHERE =
		"SELECT COUNT(commercePriceEntry) FROM CommercePriceEntry commercePriceEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePriceEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:619651540