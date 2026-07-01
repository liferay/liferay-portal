/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.exception.DuplicateCommerceTierPriceEntryExternalReferenceCodeException;
import com.liferay.commerce.price.list.exception.NoSuchTierPriceEntryException;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntryTable;
import com.liferay.commerce.price.list.model.impl.CommerceTierPriceEntryImpl;
import com.liferay.commerce.price.list.model.impl.CommerceTierPriceEntryModelImpl;
import com.liferay.commerce.price.list.service.persistence.CommerceTierPriceEntryPersistence;
import com.liferay.commerce.price.list.service.persistence.CommerceTierPriceEntryUtil;
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
 * The persistence implementation for the commerce tier price entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceTierPriceEntryPersistence.class)
public class CommerceTierPriceEntryPersistenceImpl
	extends BasePersistenceImpl
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
	implements CommerceTierPriceEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTierPriceEntryUtil</code> to access the commerce tier price entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTierPriceEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce tier price entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByUuid_First(
			String uuid,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce tier price entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce tier price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce tier price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the commerce tier price entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of commerce tier price entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByCommercePriceEntryId;

	/**
	 * Returns an ordered range of all the commerce tier price entries where commercePriceEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByCommercePriceEntryId(
		long commercePriceEntryId, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceEntryId.find(
			finderCache, new Object[] {commercePriceEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByCommercePriceEntryId_First(
			long commercePriceEntryId,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByCommercePriceEntryId.findFirst(
			finderCache, new Object[] {commercePriceEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByCommercePriceEntryId_First(
		long commercePriceEntryId,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceEntryId.fetchFirst(
			finderCache, new Object[] {commercePriceEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where commercePriceEntryId = &#63; from the database.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 */
	@Override
	public void removeByCommercePriceEntryId(long commercePriceEntryId) {
		_collectionPersistenceFinderByCommercePriceEntryId.remove(
			finderCache, new Object[] {commercePriceEntryId});
	}

	/**
	 * Returns the number of commerce tier price entries where commercePriceEntryId = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByCommercePriceEntryId(long commercePriceEntryId) {
		return _collectionPersistenceFinderByCommercePriceEntryId.count(
			finderCache, new Object[] {commercePriceEntryId});
	}

	private UniquePersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_uniquePersistenceFinderByC_M;

	/**
	 * Returns the commerce tier price entry where commercePriceEntryId = &#63; and minQuantity = &#63; or throws a <code>NoSuchTierPriceEntryException</code> if it could not be found.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @return the matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByC_M(
			long commercePriceEntryId, BigDecimal minQuantity)
		throws NoSuchTierPriceEntryException {

		return _uniquePersistenceFinderByC_M.find(
			finderCache, new Object[] {commercePriceEntryId, minQuantity});
	}

	/**
	 * Returns the commerce tier price entry where commercePriceEntryId = &#63; and minQuantity = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByC_M(
		long commercePriceEntryId, BigDecimal minQuantity,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_M.fetch(
			finderCache, new Object[] {commercePriceEntryId, minQuantity},
			useFinderCache);
	}

	/**
	 * Removes the commerce tier price entry where commercePriceEntryId = &#63; and minQuantity = &#63; from the database.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @return the commerce tier price entry that was removed
	 */
	@Override
	public CommerceTierPriceEntry removeByC_M(
			long commercePriceEntryId, BigDecimal minQuantity)
		throws NoSuchTierPriceEntryException {

		CommerceTierPriceEntry commerceTierPriceEntry = findByC_M(
			commercePriceEntryId, minQuantity);

		return remove(commerceTierPriceEntry);
	}

	/**
	 * Returns the number of commerce tier price entries where commercePriceEntryId = &#63; and minQuantity = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByC_M(long commercePriceEntryId, BigDecimal minQuantity) {
		return _uniquePersistenceFinderByC_M.count(
			finderCache, new Object[] {commercePriceEntryId, minQuantity});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByC_LteM;

	/**
	 * Returns all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @return the matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM(
		long commercePriceEntryId, BigDecimal minQuantity) {

		return findByC_LteM(
			commercePriceEntryId, minQuantity, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @return the range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM(
		long commercePriceEntryId, BigDecimal minQuantity, int start, int end) {

		return findByC_LteM(
			commercePriceEntryId, minQuantity, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM(
		long commercePriceEntryId, BigDecimal minQuantity, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return findByC_LteM(
			commercePriceEntryId, minQuantity, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM(
		long commercePriceEntryId, BigDecimal minQuantity, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LteM.find(
			finderCache, new Object[] {commercePriceEntryId, minQuantity},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByC_LteM_First(
			long commercePriceEntryId, BigDecimal minQuantity,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByC_LteM.findFirst(
			finderCache, new Object[] {commercePriceEntryId, minQuantity},
			orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByC_LteM_First(
		long commercePriceEntryId, BigDecimal minQuantity,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LteM.fetchFirst(
			finderCache, new Object[] {commercePriceEntryId, minQuantity},
			orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; from the database.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 */
	@Override
	public void removeByC_LteM(
		long commercePriceEntryId, BigDecimal minQuantity) {

		_collectionPersistenceFinderByC_LteM.remove(
			finderCache, new Object[] {commercePriceEntryId, minQuantity});
	}

	/**
	 * Returns the number of commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByC_LteM(
		long commercePriceEntryId, BigDecimal minQuantity) {

		return _collectionPersistenceFinderByC_LteM.count(
			finderCache, new Object[] {commercePriceEntryId, minQuantity});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the commerce tier price entries where commercePriceEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_S(
		long commercePriceEntryId, int status, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {commercePriceEntryId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByC_S_First(
			long commercePriceEntryId, int status,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {commercePriceEntryId, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByC_S_First(
		long commercePriceEntryId, int status,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {commercePriceEntryId, status},
			orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where commercePriceEntryId = &#63; and status = &#63; from the database.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long commercePriceEntryId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {commercePriceEntryId, status});
	}

	/**
	 * Returns the number of commerce tier price entries where commercePriceEntryId = &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param status the status
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByC_S(long commercePriceEntryId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {commercePriceEntryId, status});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the commerce tier price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtD_S(
		Date displayDate, int status) {

		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce tier price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @return the range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where displayDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of commerce tier price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByLtE_S;

	/**
	 * Returns all the commerce tier price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtE_S(
		Date expirationDate, int status) {

		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce tier price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @return the range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtE_S.find(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByLtE_S.findFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.fetchFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where expirationDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of commerce tier price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.count(
			finderCache, new Object[] {expirationDate, status});
	}

	private CollectionPersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_collectionPersistenceFinderByC_LteM_S;

	/**
	 * Returns all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @return the matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM_S(
		long commercePriceEntryId, BigDecimal minQuantity, int status) {

		return findByC_LteM_S(
			commercePriceEntryId, minQuantity, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @return the range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM_S(
		long commercePriceEntryId, BigDecimal minQuantity, int status,
		int start, int end) {

		return findByC_LteM_S(
			commercePriceEntryId, minQuantity, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM_S(
		long commercePriceEntryId, BigDecimal minQuantity, int status,
		int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return findByC_LteM_S(
			commercePriceEntryId, minQuantity, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tier price entries
	 */
	@Override
	public List<CommerceTierPriceEntry> findByC_LteM_S(
		long commercePriceEntryId, BigDecimal minQuantity, int status,
		int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LteM_S.find(
			finderCache,
			new Object[] {commercePriceEntryId, minQuantity, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByC_LteM_S_First(
			long commercePriceEntryId, BigDecimal minQuantity, int status,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws NoSuchTierPriceEntryException {

		return _collectionPersistenceFinderByC_LteM_S.findFirst(
			finderCache,
			new Object[] {commercePriceEntryId, minQuantity, status},
			orderByComparator);
	}

	/**
	 * Returns the first commerce tier price entry in the ordered set where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByC_LteM_S_First(
		long commercePriceEntryId, BigDecimal minQuantity, int status,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LteM_S.fetchFirst(
			finderCache,
			new Object[] {commercePriceEntryId, minQuantity, status},
			orderByComparator);
	}

	/**
	 * Removes all the commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63; from the database.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 */
	@Override
	public void removeByC_LteM_S(
		long commercePriceEntryId, BigDecimal minQuantity, int status) {

		_collectionPersistenceFinderByC_LteM_S.remove(
			finderCache,
			new Object[] {commercePriceEntryId, minQuantity, status});
	}

	/**
	 * Returns the number of commerce tier price entries where commercePriceEntryId = &#63; and minQuantity &le; &#63; and status = &#63;.
	 *
	 * @param commercePriceEntryId the commerce price entry ID
	 * @param minQuantity the min quantity
	 * @param status the status
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByC_LteM_S(
		long commercePriceEntryId, BigDecimal minQuantity, int status) {

		return _collectionPersistenceFinderByC_LteM_S.count(
			finderCache,
			new Object[] {commercePriceEntryId, minQuantity, status});
	}

	private UniquePersistenceFinder
		<CommerceTierPriceEntry, NoSuchTierPriceEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce tier price entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchTierPriceEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTierPriceEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce tier price entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce tier price entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce tier price entry that was removed
	 */
	@Override
	public CommerceTierPriceEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTierPriceEntryException {

		CommerceTierPriceEntry commerceTierPriceEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceTierPriceEntry);
	}

	/**
	 * Returns the number of commerce tier price entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce tier price entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceTierPriceEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceTierPriceEntry.class);

		setModelImplClass(CommerceTierPriceEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTierPriceEntryTable.INSTANCE);
	}

	/**
	 * Creates a new commerce tier price entry with the primary key. Does not add the commerce tier price entry to the database.
	 *
	 * @param commerceTierPriceEntryId the primary key for the new commerce tier price entry
	 * @return the new commerce tier price entry
	 */
	@Override
	public CommerceTierPriceEntry create(long commerceTierPriceEntryId) {
		CommerceTierPriceEntry commerceTierPriceEntry =
			new CommerceTierPriceEntryImpl();

		commerceTierPriceEntry.setNew(true);
		commerceTierPriceEntry.setPrimaryKey(commerceTierPriceEntryId);

		String uuid = PortalUUIDUtil.generate();

		commerceTierPriceEntry.setUuid(uuid);

		commerceTierPriceEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceTierPriceEntry;
	}

	/**
	 * Removes the commerce tier price entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTierPriceEntryId the primary key of the commerce tier price entry
	 * @return the commerce tier price entry that was removed
	 * @throws NoSuchTierPriceEntryException if a commerce tier price entry with the primary key could not be found
	 */
	@Override
	public CommerceTierPriceEntry remove(long commerceTierPriceEntryId)
		throws NoSuchTierPriceEntryException {

		return remove((Serializable)commerceTierPriceEntryId);
	}

	@Override
	protected CommerceTierPriceEntry removeImpl(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTierPriceEntry)) {
				commerceTierPriceEntry = (CommerceTierPriceEntry)session.get(
					CommerceTierPriceEntryImpl.class,
					commerceTierPriceEntry.getPrimaryKeyObj());
			}

			if ((commerceTierPriceEntry != null) &&
				ctPersistenceHelper.isRemove(commerceTierPriceEntry)) {

				session.delete(commerceTierPriceEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTierPriceEntry != null) {
			clearCache(commerceTierPriceEntry);
		}

		return commerceTierPriceEntry;
	}

	@Override
	public CommerceTierPriceEntry updateImpl(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		boolean isNew = commerceTierPriceEntry.isNew();

		if (!(commerceTierPriceEntry instanceof
				CommerceTierPriceEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceTierPriceEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTierPriceEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTierPriceEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTierPriceEntry implementation " +
					commerceTierPriceEntry.getClass());
		}

		CommerceTierPriceEntryModelImpl commerceTierPriceEntryModelImpl =
			(CommerceTierPriceEntryModelImpl)commerceTierPriceEntry;

		if (Validator.isNull(commerceTierPriceEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceTierPriceEntry.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceTierPriceEntry.getExternalReferenceCode())) {

			commerceTierPriceEntry.setExternalReferenceCode(
				commerceTierPriceEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceTierPriceEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceTierPriceEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceTierPriceEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceTierPriceEntry.getPrimaryKey();
					}

					try {
						commerceTierPriceEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceTierPriceEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceTierPriceEntry.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceTierPriceEntry ercCommerceTierPriceEntry = fetchByERC_C(
				commerceTierPriceEntry.getExternalReferenceCode(),
				commerceTierPriceEntry.getCompanyId());

			if (isNew) {
				if (ercCommerceTierPriceEntry != null) {
					throw new DuplicateCommerceTierPriceEntryExternalReferenceCodeException(
						"Duplicate commerce tier price entry with external reference code " +
							commerceTierPriceEntry.getExternalReferenceCode() +
								" and company " +
									commerceTierPriceEntry.getCompanyId());
				}
			}
			else {
				if ((ercCommerceTierPriceEntry != null) &&
					(commerceTierPriceEntry.getCommerceTierPriceEntryId() !=
						ercCommerceTierPriceEntry.
							getCommerceTierPriceEntryId())) {

					throw new DuplicateCommerceTierPriceEntryExternalReferenceCodeException(
						"Duplicate commerce tier price entry with external reference code " +
							commerceTierPriceEntry.getExternalReferenceCode() +
								" and company " +
									commerceTierPriceEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTierPriceEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTierPriceEntry.setCreateDate(date);
			}
			else {
				commerceTierPriceEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTierPriceEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTierPriceEntry.setModifiedDate(date);
			}
			else {
				commerceTierPriceEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commerceTierPriceEntry)) {
				if (!isNew) {
					session.evict(
						CommerceTierPriceEntryImpl.class,
						commerceTierPriceEntry.getPrimaryKeyObj());
				}

				session.save(commerceTierPriceEntry);
			}
			else {
				commerceTierPriceEntry = (CommerceTierPriceEntry)session.merge(
					commerceTierPriceEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceTierPriceEntry, false);

		if (isNew) {
			commerceTierPriceEntry.setNew(false);
		}

		commerceTierPriceEntry.resetOriginalValues();

		return commerceTierPriceEntry;
	}

	/**
	 * Returns the commerce tier price entry with the primary key or throws a <code>NoSuchTierPriceEntryException</code> if it could not be found.
	 *
	 * @param commerceTierPriceEntryId the primary key of the commerce tier price entry
	 * @return the commerce tier price entry
	 * @throws NoSuchTierPriceEntryException if a commerce tier price entry with the primary key could not be found
	 */
	@Override
	public CommerceTierPriceEntry findByPrimaryKey(
			long commerceTierPriceEntryId)
		throws NoSuchTierPriceEntryException {

		return findByPrimaryKey((Serializable)commerceTierPriceEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce tier price entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTierPriceEntryId the primary key of the commerce tier price entry
	 * @return the commerce tier price entry, or <code>null</code> if a commerce tier price entry with the primary key could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchByPrimaryKey(
		long commerceTierPriceEntryId) {

		return fetchByPrimaryKey((Serializable)commerceTierPriceEntryId);
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
		return "commerceTierPriceEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETIERPRICEENTRY;
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
		return CommerceTierPriceEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommerceTierPriceEntry";
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
		ctMergeColumnNames.add("commercePriceEntryId");
		ctMergeColumnNames.add("price");
		ctMergeColumnNames.add("promoPrice");
		ctMergeColumnNames.add("discountDiscovery");
		ctMergeColumnNames.add("discountLevel1");
		ctMergeColumnNames.add("discountLevel2");
		ctMergeColumnNames.add("discountLevel3");
		ctMergeColumnNames.add("discountLevel4");
		ctMergeColumnNames.add("minQuantity");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
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
			Collections.singleton("commerceTierPriceEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commercePriceEntryId", "minQuantity"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the commerce tier price entry persistence.
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
			_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
			CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceTierPriceEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceTierPriceEntry::getUuid));

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
				_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
				CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTierPriceEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceTierPriceEntry::getUuid),
				new FinderColumn<>(
					"commerceTierPriceEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTierPriceEntry::getCompanyId));

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
				_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
				CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTierPriceEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTierPriceEntry::getCompanyId));

		_collectionPersistenceFinderByCommercePriceEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePriceEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePriceEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePriceEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceEntryId"}, false),
				_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
				CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTierPriceEntry.", "commercePriceEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTierPriceEntry::getCommercePriceEntryId));

		_uniquePersistenceFinderByC_M = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_M",
				new String[] {Long.class.getName(), BigDecimal.class.getName()},
				new String[] {"commercePriceEntryId", "minQuantity"}, 0, 0,
				false, CommerceTierPriceEntry::getCommercePriceEntryId,
				CommerceTierPriceEntry::getMinQuantity),
			_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE, "",
			new FinderColumn<>(
				"commerceTierPriceEntry.", "commercePriceEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTierPriceEntry::getCommercePriceEntryId),
			new FinderColumn<>(
				"commerceTierPriceEntry.", "minQuantity",
				FinderColumn.Type.BIG_DECIMAL, "=", true, true,
				CommerceTierPriceEntry::getMinQuantity));

		_collectionPersistenceFinderByC_LteM =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LteM",
					new String[] {
						Long.class.getName(), BigDecimal.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceEntryId", "minQuantity"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LteM",
					new String[] {
						Long.class.getName(), BigDecimal.class.getName()
					},
					new String[] {"commercePriceEntryId", "minQuantity"},
					false),
				_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
				CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTierPriceEntry.", "commercePriceEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTierPriceEntry::getCommercePriceEntryId),
				new FinderColumn<>(
					"commerceTierPriceEntry.", "minQuantity",
					FinderColumn.Type.BIG_DECIMAL, "<=", true, true,
					CommerceTierPriceEntry::getMinQuantity));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commercePriceEntryId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"commercePriceEntryId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"commercePriceEntryId", "status"}, false),
			_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
			CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceTierPriceEntry.", "commercePriceEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTierPriceEntry::getCommercePriceEntryId),
			new FinderColumn<>(
				"commerceTierPriceEntry.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, CommerceTierPriceEntry::getStatus));

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
			_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
			CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceTierPriceEntry.", "displayDate",
				FinderColumn.Type.DATE, "<", true, true,
				CommerceTierPriceEntry::getDisplayDate),
			new FinderColumn<>(
				"commerceTierPriceEntry.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, CommerceTierPriceEntry::getStatus));

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
			_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
			_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
			CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceTierPriceEntry.", "expirationDate",
				FinderColumn.Type.DATE, "<", true, true,
				CommerceTierPriceEntry::getExpirationDate),
			new FinderColumn<>(
				"commerceTierPriceEntry.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, CommerceTierPriceEntry::getStatus));

		_collectionPersistenceFinderByC_LteM_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LteM_S",
					new String[] {
						Long.class.getName(), BigDecimal.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"commercePriceEntryId", "minQuantity", "status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LteM_S",
					new String[] {
						Long.class.getName(), BigDecimal.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"commercePriceEntryId", "minQuantity", "status"
					},
					false),
				_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE,
				_SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE,
				CommerceTierPriceEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTierPriceEntry.", "commercePriceEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTierPriceEntry::getCommercePriceEntryId),
				new FinderColumn<>(
					"commerceTierPriceEntry.", "minQuantity",
					FinderColumn.Type.BIG_DECIMAL, "<=", true, true,
					CommerceTierPriceEntry::getMinQuantity),
				new FinderColumn<>(
					"commerceTierPriceEntry.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					CommerceTierPriceEntry::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceTierPriceEntry::getExternalReferenceCode),
				CommerceTierPriceEntry::getCompanyId),
			_SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE, "",
			new FinderColumn<>(
				"commerceTierPriceEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceTierPriceEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceTierPriceEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CommerceTierPriceEntry::getCompanyId));

		CommerceTierPriceEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTierPriceEntryUtil.setPersistence(null);

		entityCache.removeCache(CommerceTierPriceEntryImpl.class.getName());
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
		CommerceTierPriceEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCETIERPRICEENTRY =
		"SELECT commerceTierPriceEntry FROM CommerceTierPriceEntry commerceTierPriceEntry";

	private static final String _SQL_SELECT_COMMERCETIERPRICEENTRY_WHERE =
		"SELECT commerceTierPriceEntry FROM CommerceTierPriceEntry commerceTierPriceEntry WHERE ";

	private static final String _SQL_COUNT_COMMERCETIERPRICEENTRY_WHERE =
		"SELECT COUNT(commerceTierPriceEntry) FROM CommerceTierPriceEntry commerceTierPriceEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceTierPriceEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTierPriceEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1483984101