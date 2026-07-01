/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.service.persistence.impl;

import com.liferay.commerce.product.type.grouped.exception.NoSuchCPDefinitionGroupedEntryException;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntryTable;
import com.liferay.commerce.product.type.grouped.model.impl.CPDefinitionGroupedEntryImpl;
import com.liferay.commerce.product.type.grouped.model.impl.CPDefinitionGroupedEntryModelImpl;
import com.liferay.commerce.product.type.grouped.service.persistence.CPDefinitionGroupedEntryPersistence;
import com.liferay.commerce.product.type.grouped.service.persistence.CPDefinitionGroupedEntryUtil;
import com.liferay.commerce.product.type.grouped.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the cp definition grouped entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Andrea Di Giorgi
 * @generated
 */
@Component(service = CPDefinitionGroupedEntryPersistence.class)
public class CPDefinitionGroupedEntryPersistenceImpl
	extends BasePersistenceImpl
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
	implements CPDefinitionGroupedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionGroupedEntryUtil</code> to access the cp definition grouped entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionGroupedEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp definition grouped entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionGroupedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition grouped entries
	 * @param end the upper bound of the range of cp definition grouped entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition grouped entries
	 */
	@Override
	public List<CPDefinitionGroupedEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws NoSuchCPDefinitionGroupedEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definition grouped entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp definition grouped entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition grouped entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp definition grouped entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionGroupedEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionGroupedEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp definition grouped entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp definition grouped entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition grouped entry that was removed
	 */
	@Override
	public CPDefinitionGroupedEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionGroupedEntryException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry = findByUUID_G(
			uuid, groupId);

		return remove(cpDefinitionGroupedEntry);
	}

	/**
	 * Returns the number of cp definition grouped entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition grouped entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp definition grouped entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionGroupedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition grouped entries
	 * @param end the upper bound of the range of cp definition grouped entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition grouped entries
	 */
	@Override
	public List<CPDefinitionGroupedEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws NoSuchCPDefinitionGroupedEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition grouped entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp definition grouped entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition grouped entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
			_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the cp definition grouped entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionGroupedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition grouped entries
	 * @param end the upper bound of the range of cp definition grouped entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition grouped entries
	 */
	@Override
	public List<CPDefinitionGroupedEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws NoSuchCPDefinitionGroupedEntryException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition grouped entries where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of cp definition grouped entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition grouped entries
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
			_collectionPersistenceFinderByEntryCProductId;

	/**
	 * Returns an ordered range of all the cp definition grouped entries where entryCProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionGroupedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param entryCProductId the entry c product ID
	 * @param start the lower bound of the range of cp definition grouped entries
	 * @param end the upper bound of the range of cp definition grouped entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition grouped entries
	 */
	@Override
	public List<CPDefinitionGroupedEntry> findByEntryCProductId(
		long entryCProductId, int start, int end,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByEntryCProductId.find(
			finderCache, new Object[] {entryCProductId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where entryCProductId = &#63;.
	 *
	 * @param entryCProductId the entry c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByEntryCProductId_First(
			long entryCProductId,
			OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws NoSuchCPDefinitionGroupedEntryException {

		return _collectionPersistenceFinderByEntryCProductId.findFirst(
			finderCache, new Object[] {entryCProductId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition grouped entry in the ordered set where entryCProductId = &#63;.
	 *
	 * @param entryCProductId the entry c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByEntryCProductId_First(
		long entryCProductId,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return _collectionPersistenceFinderByEntryCProductId.fetchFirst(
			finderCache, new Object[] {entryCProductId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition grouped entries where entryCProductId = &#63; from the database.
	 *
	 * @param entryCProductId the entry c product ID
	 */
	@Override
	public void removeByEntryCProductId(long entryCProductId) {
		_collectionPersistenceFinderByEntryCProductId.remove(
			finderCache, new Object[] {entryCProductId});
	}

	/**
	 * Returns the number of cp definition grouped entries where entryCProductId = &#63;.
	 *
	 * @param entryCProductId the entry c product ID
	 * @return the number of matching cp definition grouped entries
	 */
	@Override
	public int countByEntryCProductId(long entryCProductId) {
		return _collectionPersistenceFinderByEntryCProductId.count(
			finderCache, new Object[] {entryCProductId});
	}

	private UniquePersistenceFinder
		<CPDefinitionGroupedEntry, NoSuchCPDefinitionGroupedEntryException>
			_uniquePersistenceFinderByC_E;

	/**
	 * Returns the cp definition grouped entry where CPDefinitionId = &#63; and entryCProductId = &#63; or throws a <code>NoSuchCPDefinitionGroupedEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param entryCProductId the entry c product ID
	 * @return the matching cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByC_E(
			long CPDefinitionId, long entryCProductId)
		throws NoSuchCPDefinitionGroupedEntryException {

		return _uniquePersistenceFinderByC_E.find(
			finderCache, new Object[] {CPDefinitionId, entryCProductId});
	}

	/**
	 * Returns the cp definition grouped entry where CPDefinitionId = &#63; and entryCProductId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param entryCProductId the entry c product ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByC_E(
		long CPDefinitionId, long entryCProductId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_E.fetch(
			finderCache, new Object[] {CPDefinitionId, entryCProductId},
			useFinderCache);
	}

	/**
	 * Removes the cp definition grouped entry where CPDefinitionId = &#63; and entryCProductId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param entryCProductId the entry c product ID
	 * @return the cp definition grouped entry that was removed
	 */
	@Override
	public CPDefinitionGroupedEntry removeByC_E(
			long CPDefinitionId, long entryCProductId)
		throws NoSuchCPDefinitionGroupedEntryException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry = findByC_E(
			CPDefinitionId, entryCProductId);

		return remove(cpDefinitionGroupedEntry);
	}

	/**
	 * Returns the number of cp definition grouped entries where CPDefinitionId = &#63; and entryCProductId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param entryCProductId the entry c product ID
	 * @return the number of matching cp definition grouped entries
	 */
	@Override
	public int countByC_E(long CPDefinitionId, long entryCProductId) {
		return _uniquePersistenceFinderByC_E.count(
			finderCache, new Object[] {CPDefinitionId, entryCProductId});
	}

	public CPDefinitionGroupedEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDefinitionGroupedEntry.class);

		setModelImplClass(CPDefinitionGroupedEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionGroupedEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cp definition grouped entry with the primary key. Does not add the cp definition grouped entry to the database.
	 *
	 * @param CPDefinitionGroupedEntryId the primary key for the new cp definition grouped entry
	 * @return the new cp definition grouped entry
	 */
	@Override
	public CPDefinitionGroupedEntry create(long CPDefinitionGroupedEntryId) {
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			new CPDefinitionGroupedEntryImpl();

		cpDefinitionGroupedEntry.setNew(true);
		cpDefinitionGroupedEntry.setPrimaryKey(CPDefinitionGroupedEntryId);

		String uuid = PortalUUIDUtil.generate();

		cpDefinitionGroupedEntry.setUuid(uuid);

		cpDefinitionGroupedEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpDefinitionGroupedEntry;
	}

	/**
	 * Removes the cp definition grouped entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionGroupedEntryId the primary key of the cp definition grouped entry
	 * @return the cp definition grouped entry that was removed
	 * @throws NoSuchCPDefinitionGroupedEntryException if a cp definition grouped entry with the primary key could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry remove(long CPDefinitionGroupedEntryId)
		throws NoSuchCPDefinitionGroupedEntryException {

		return remove((Serializable)CPDefinitionGroupedEntryId);
	}

	@Override
	protected CPDefinitionGroupedEntry removeImpl(
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinitionGroupedEntry)) {
				cpDefinitionGroupedEntry =
					(CPDefinitionGroupedEntry)session.get(
						CPDefinitionGroupedEntryImpl.class,
						cpDefinitionGroupedEntry.getPrimaryKeyObj());
			}

			if (cpDefinitionGroupedEntry != null) {
				session.delete(cpDefinitionGroupedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinitionGroupedEntry != null) {
			clearCache(cpDefinitionGroupedEntry);
		}

		return cpDefinitionGroupedEntry;
	}

	@Override
	public CPDefinitionGroupedEntry updateImpl(
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry) {

		boolean isNew = cpDefinitionGroupedEntry.isNew();

		if (!(cpDefinitionGroupedEntry instanceof
				CPDefinitionGroupedEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinitionGroupedEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinitionGroupedEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinitionGroupedEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinitionGroupedEntry implementation " +
					cpDefinitionGroupedEntry.getClass());
		}

		CPDefinitionGroupedEntryModelImpl cpDefinitionGroupedEntryModelImpl =
			(CPDefinitionGroupedEntryModelImpl)cpDefinitionGroupedEntry;

		if (Validator.isNull(cpDefinitionGroupedEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDefinitionGroupedEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDefinitionGroupedEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDefinitionGroupedEntry.setCreateDate(date);
			}
			else {
				cpDefinitionGroupedEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpDefinitionGroupedEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDefinitionGroupedEntry.setModifiedDate(date);
			}
			else {
				cpDefinitionGroupedEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cpDefinitionGroupedEntry);
			}
			else {
				cpDefinitionGroupedEntry =
					(CPDefinitionGroupedEntry)session.merge(
						cpDefinitionGroupedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDefinitionGroupedEntry, false);

		if (isNew) {
			cpDefinitionGroupedEntry.setNew(false);
		}

		cpDefinitionGroupedEntry.resetOriginalValues();

		return cpDefinitionGroupedEntry;
	}

	/**
	 * Returns the cp definition grouped entry with the primary key or throws a <code>NoSuchCPDefinitionGroupedEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionGroupedEntryId the primary key of the cp definition grouped entry
	 * @return the cp definition grouped entry
	 * @throws NoSuchCPDefinitionGroupedEntryException if a cp definition grouped entry with the primary key could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry findByPrimaryKey(
			long CPDefinitionGroupedEntryId)
		throws NoSuchCPDefinitionGroupedEntryException {

		return findByPrimaryKey((Serializable)CPDefinitionGroupedEntryId);
	}

	/**
	 * Returns the cp definition grouped entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionGroupedEntryId the primary key of the cp definition grouped entry
	 * @return the cp definition grouped entry, or <code>null</code> if a cp definition grouped entry with the primary key could not be found
	 */
	@Override
	public CPDefinitionGroupedEntry fetchByPrimaryKey(
		long CPDefinitionGroupedEntryId) {

		return fetchByPrimaryKey((Serializable)CPDefinitionGroupedEntryId);
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
		return "CPDefinitionGroupedEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITIONGROUPEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CPDefinitionGroupedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cp definition grouped entry persistence.
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
			_SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE,
			_SQL_COUNT_CPDEFINITIONGROUPEDENTRY_WHERE,
			CPDefinitionGroupedEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpDefinitionGroupedEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionGroupedEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDefinitionGroupedEntry::getUuid),
				CPDefinitionGroupedEntry::getGroupId),
			_SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionGroupedEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionGroupedEntry::getUuid),
			new FinderColumn<>(
				"cpDefinitionGroupedEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionGroupedEntry::getGroupId));

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
				_SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE,
				_SQL_COUNT_CPDEFINITIONGROUPEDENTRY_WHERE,
				CPDefinitionGroupedEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpDefinitionGroupedEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionGroupedEntry::getUuid),
				new FinderColumn<>(
					"cpDefinitionGroupedEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionGroupedEntry::getCompanyId));

		_collectionPersistenceFinderByCPDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionId", new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, false),
				_SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE,
				_SQL_COUNT_CPDEFINITIONGROUPEDENTRY_WHERE,
				CPDefinitionGroupedEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpDefinitionGroupedEntry.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionGroupedEntry::getCPDefinitionId));

		_collectionPersistenceFinderByEntryCProductId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByEntryCProductId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"entryCProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByEntryCProductId",
					new String[] {Long.class.getName()},
					new String[] {"entryCProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByEntryCProductId",
					new String[] {Long.class.getName()},
					new String[] {"entryCProductId"}, false),
				_SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE,
				_SQL_COUNT_CPDEFINITIONGROUPEDENTRY_WHERE,
				CPDefinitionGroupedEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpDefinitionGroupedEntry.", "entryCProductId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionGroupedEntry::getEntryCProductId));

		_uniquePersistenceFinderByC_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_E",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"CPDefinitionId", "entryCProductId"}, 0, 0, false,
				CPDefinitionGroupedEntry::getCPDefinitionId,
				CPDefinitionGroupedEntry::getEntryCProductId),
			_SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionGroupedEntry.", "CPDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionGroupedEntry::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionGroupedEntry.", "entryCProductId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionGroupedEntry::getEntryCProductId));

		CPDefinitionGroupedEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionGroupedEntryUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionGroupedEntryImpl.class.getName());
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
		CPDefinitionGroupedEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDEFINITIONGROUPEDENTRY =
		"SELECT cpDefinitionGroupedEntry FROM CPDefinitionGroupedEntry cpDefinitionGroupedEntry";

	private static final String _SQL_SELECT_CPDEFINITIONGROUPEDENTRY_WHERE =
		"SELECT cpDefinitionGroupedEntry FROM CPDefinitionGroupedEntry cpDefinitionGroupedEntry WHERE ";

	private static final String _SQL_COUNT_CPDEFINITIONGROUPEDENTRY_WHERE =
		"SELECT COUNT(cpDefinitionGroupedEntry) FROM CPDefinitionGroupedEntry cpDefinitionGroupedEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDefinitionGroupedEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionGroupedEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-424323550