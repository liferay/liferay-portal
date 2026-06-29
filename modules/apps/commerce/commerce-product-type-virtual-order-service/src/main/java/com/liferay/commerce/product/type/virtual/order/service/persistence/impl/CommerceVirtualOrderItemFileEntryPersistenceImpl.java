/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.persistence.impl;

import com.liferay.commerce.product.type.virtual.order.exception.NoSuchVirtualOrderItemFileEntryException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntryTable;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryImpl;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemFileEntryPersistence;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemFileEntryUtil;
import com.liferay.commerce.product.type.virtual.order.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce virtual order item file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceVirtualOrderItemFileEntryPersistence.class)
public class CommerceVirtualOrderItemFileEntryPersistenceImpl
	extends BasePersistenceImpl
		<CommerceVirtualOrderItemFileEntry,
		 NoSuchVirtualOrderItemFileEntryException>
	implements CommerceVirtualOrderItemFileEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceVirtualOrderItemFileEntryUtil</code> to access the commerce virtual order item file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceVirtualOrderItemFileEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceVirtualOrderItemFileEntry,
		 NoSuchVirtualOrderItemFileEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce virtual order item file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce virtual order item file entries
	 */
	@Override
	public List<CommerceVirtualOrderItemFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceVirtualOrderItemFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry
	 * @throws NoSuchVirtualOrderItemFileEntryException if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry findByUuid_First(
			String uuid,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator)
		throws NoSuchVirtualOrderItemFileEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceVirtualOrderItemFileEntry>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce virtual order item file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce virtual order item file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce virtual order item file entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceVirtualOrderItemFileEntry,
		 NoSuchVirtualOrderItemFileEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce virtual order item file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchVirtualOrderItemFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce virtual order item file entry
	 * @throws NoSuchVirtualOrderItemFileEntryException if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry findByUUID_G(
			String uuid, long groupId)
		throws NoSuchVirtualOrderItemFileEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce virtual order item file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce virtual order item file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce virtual order item file entry that was removed
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchVirtualOrderItemFileEntryException {

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			findByUUID_G(uuid, groupId);

		return remove(commerceVirtualOrderItemFileEntry);
	}

	/**
	 * Returns the number of commerce virtual order item file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce virtual order item file entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceVirtualOrderItemFileEntry,
		 NoSuchVirtualOrderItemFileEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce virtual order item file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce virtual order item file entries
	 */
	@Override
	public List<CommerceVirtualOrderItemFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceVirtualOrderItemFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry
	 * @throws NoSuchVirtualOrderItemFileEntryException if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator)
		throws NoSuchVirtualOrderItemFileEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceVirtualOrderItemFileEntry>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce virtual order item file entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce virtual order item file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce virtual order item file entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceVirtualOrderItemFileEntry,
		 NoSuchVirtualOrderItemFileEntryException>
			_collectionPersistenceFinderByCommerceVirtualOrderItemId;

	/**
	 * Returns an ordered range of all the commerce virtual order item file entries where commerceVirtualOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce virtual order item file entries
	 */
	@Override
	public List<CommerceVirtualOrderItemFileEntry>
		findByCommerceVirtualOrderItemId(
			long commerceVirtualOrderItemId, int start, int end,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceVirtualOrderItemId.find(
			finderCache, new Object[] {commerceVirtualOrderItemId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where commerceVirtualOrderItemId = &#63;.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry
	 * @throws NoSuchVirtualOrderItemFileEntryException if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry
			findByCommerceVirtualOrderItemId_First(
				long commerceVirtualOrderItemId,
				OrderByComparator<CommerceVirtualOrderItemFileEntry>
					orderByComparator)
		throws NoSuchVirtualOrderItemFileEntryException {

		return _collectionPersistenceFinderByCommerceVirtualOrderItemId.
			findFirst(
				finderCache, new Object[] {commerceVirtualOrderItemId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where commerceVirtualOrderItemId = &#63;.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry
		fetchByCommerceVirtualOrderItemId_First(
			long commerceVirtualOrderItemId,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceVirtualOrderItemId.
			fetchFirst(
				finderCache, new Object[] {commerceVirtualOrderItemId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce virtual order item file entries where commerceVirtualOrderItemId = &#63; from the database.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 */
	@Override
	public void removeByCommerceVirtualOrderItemId(
		long commerceVirtualOrderItemId) {

		_collectionPersistenceFinderByCommerceVirtualOrderItemId.remove(
			finderCache, new Object[] {commerceVirtualOrderItemId});
	}

	/**
	 * Returns the number of commerce virtual order item file entries where commerceVirtualOrderItemId = &#63;.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @return the number of matching commerce virtual order item file entries
	 */
	@Override
	public int countByCommerceVirtualOrderItemId(
		long commerceVirtualOrderItemId) {

		return _collectionPersistenceFinderByCommerceVirtualOrderItemId.count(
			finderCache, new Object[] {commerceVirtualOrderItemId});
	}

	private CollectionPersistenceFinder
		<CommerceVirtualOrderItemFileEntry,
		 NoSuchVirtualOrderItemFileEntryException>
			_collectionPersistenceFinderByC_F;

	/**
	 * Returns an ordered range of all the commerce virtual order item file entries where commerceVirtualOrderItemId = &#63; and fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce virtual order item file entries
	 */
	@Override
	public List<CommerceVirtualOrderItemFileEntry> findByC_F(
		long commerceVirtualOrderItemId, long fileEntryId, int start, int end,
		OrderByComparator<CommerceVirtualOrderItemFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_F.find(
			finderCache, new Object[] {commerceVirtualOrderItemId, fileEntryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where commerceVirtualOrderItemId = &#63; and fileEntryId = &#63;.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry
	 * @throws NoSuchVirtualOrderItemFileEntryException if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry findByC_F_First(
			long commerceVirtualOrderItemId, long fileEntryId,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator)
		throws NoSuchVirtualOrderItemFileEntryException {

		return _collectionPersistenceFinderByC_F.findFirst(
			finderCache, new Object[] {commerceVirtualOrderItemId, fileEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce virtual order item file entry in the ordered set where commerceVirtualOrderItemId = &#63; and fileEntryId = &#63;.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry fetchByC_F_First(
		long commerceVirtualOrderItemId, long fileEntryId,
		OrderByComparator<CommerceVirtualOrderItemFileEntry>
			orderByComparator) {

		return _collectionPersistenceFinderByC_F.fetchFirst(
			finderCache, new Object[] {commerceVirtualOrderItemId, fileEntryId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce virtual order item file entries where commerceVirtualOrderItemId = &#63; and fileEntryId = &#63; from the database.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByC_F(long commerceVirtualOrderItemId, long fileEntryId) {
		_collectionPersistenceFinderByC_F.remove(
			finderCache,
			new Object[] {commerceVirtualOrderItemId, fileEntryId});
	}

	/**
	 * Returns the number of commerce virtual order item file entries where commerceVirtualOrderItemId = &#63; and fileEntryId = &#63;.
	 *
	 * @param commerceVirtualOrderItemId the commerce virtual order item ID
	 * @param fileEntryId the file entry ID
	 * @return the number of matching commerce virtual order item file entries
	 */
	@Override
	public int countByC_F(long commerceVirtualOrderItemId, long fileEntryId) {
		return _collectionPersistenceFinderByC_F.count(
			finderCache,
			new Object[] {commerceVirtualOrderItemId, fileEntryId});
	}

	public CommerceVirtualOrderItemFileEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commerceVirtualOrderItemFileEntryId",
			"cVirtualOrderItemFileEntryId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceVirtualOrderItemFileEntry.class);

		setModelImplClass(CommerceVirtualOrderItemFileEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceVirtualOrderItemFileEntryTable.INSTANCE);
	}

	/**
	 * Creates a new commerce virtual order item file entry with the primary key. Does not add the commerce virtual order item file entry to the database.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key for the new commerce virtual order item file entry
	 * @return the new commerce virtual order item file entry
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry create(
		long commerceVirtualOrderItemFileEntryId) {

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			new CommerceVirtualOrderItemFileEntryImpl();

		commerceVirtualOrderItemFileEntry.setNew(true);
		commerceVirtualOrderItemFileEntry.setPrimaryKey(
			commerceVirtualOrderItemFileEntryId);

		String uuid = PortalUUIDUtil.generate();

		commerceVirtualOrderItemFileEntry.setUuid(uuid);

		commerceVirtualOrderItemFileEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceVirtualOrderItemFileEntry;
	}

	/**
	 * Removes the commerce virtual order item file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 * @throws NoSuchVirtualOrderItemFileEntryException if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry remove(
			long commerceVirtualOrderItemFileEntryId)
		throws NoSuchVirtualOrderItemFileEntryException {

		return remove((Serializable)commerceVirtualOrderItemFileEntryId);
	}

	@Override
	protected CommerceVirtualOrderItemFileEntry removeImpl(
		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceVirtualOrderItemFileEntry)) {
				commerceVirtualOrderItemFileEntry =
					(CommerceVirtualOrderItemFileEntry)session.get(
						CommerceVirtualOrderItemFileEntryImpl.class,
						commerceVirtualOrderItemFileEntry.getPrimaryKeyObj());
			}

			if (commerceVirtualOrderItemFileEntry != null) {
				session.delete(commerceVirtualOrderItemFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceVirtualOrderItemFileEntry != null) {
			clearCache(commerceVirtualOrderItemFileEntry);
		}

		return commerceVirtualOrderItemFileEntry;
	}

	@Override
	public CommerceVirtualOrderItemFileEntry updateImpl(
		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry) {

		boolean isNew = commerceVirtualOrderItemFileEntry.isNew();

		if (!(commerceVirtualOrderItemFileEntry instanceof
				CommerceVirtualOrderItemFileEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceVirtualOrderItemFileEntry.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceVirtualOrderItemFileEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceVirtualOrderItemFileEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceVirtualOrderItemFileEntry implementation " +
					commerceVirtualOrderItemFileEntry.getClass());
		}

		CommerceVirtualOrderItemFileEntryModelImpl
			commerceVirtualOrderItemFileEntryModelImpl =
				(CommerceVirtualOrderItemFileEntryModelImpl)
					commerceVirtualOrderItemFileEntry;

		if (Validator.isNull(commerceVirtualOrderItemFileEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceVirtualOrderItemFileEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceVirtualOrderItemFileEntry.getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceVirtualOrderItemFileEntry.setCreateDate(date);
			}
			else {
				commerceVirtualOrderItemFileEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceVirtualOrderItemFileEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceVirtualOrderItemFileEntry.setModifiedDate(date);
			}
			else {
				commerceVirtualOrderItemFileEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceVirtualOrderItemFileEntry);
			}
			else {
				commerceVirtualOrderItemFileEntry =
					(CommerceVirtualOrderItemFileEntry)session.merge(
						commerceVirtualOrderItemFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceVirtualOrderItemFileEntry, false);

		if (isNew) {
			commerceVirtualOrderItemFileEntry.setNew(false);
		}

		commerceVirtualOrderItemFileEntry.resetOriginalValues();

		return commerceVirtualOrderItemFileEntry;
	}

	/**
	 * Returns the commerce virtual order item file entry with the primary key or throws a <code>NoSuchVirtualOrderItemFileEntryException</code> if it could not be found.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry
	 * @throws NoSuchVirtualOrderItemFileEntryException if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry findByPrimaryKey(
			long commerceVirtualOrderItemFileEntryId)
		throws NoSuchVirtualOrderItemFileEntryException {

		return findByPrimaryKey(
			(Serializable)commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * Returns the commerce virtual order item file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry, or <code>null</code> if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Override
	public CommerceVirtualOrderItemFileEntry fetchByPrimaryKey(
		long commerceVirtualOrderItemFileEntryId) {

		return fetchByPrimaryKey(
			(Serializable)commerceVirtualOrderItemFileEntryId);
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
		return "cVirtualOrderItemFileEntryId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceVirtualOrderItemFileEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceVirtualOrderItemFileEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce virtual order item file entry persistence.
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
			_SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
			_SQL_COUNT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
			CommerceVirtualOrderItemFileEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceVirtualOrderItemFileEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceVirtualOrderItemFileEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceVirtualOrderItemFileEntry::getUuid),
				CommerceVirtualOrderItemFileEntry::getGroupId),
			_SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"commerceVirtualOrderItemFileEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceVirtualOrderItemFileEntry::getUuid),
			new FinderColumn<>(
				"commerceVirtualOrderItemFileEntry.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceVirtualOrderItemFileEntry::getGroupId));

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
				_SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
				_SQL_COUNT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
				CommerceVirtualOrderItemFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceVirtualOrderItemFileEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceVirtualOrderItemFileEntry::getUuid),
				new FinderColumn<>(
					"commerceVirtualOrderItemFileEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceVirtualOrderItemFileEntry::getCompanyId));

		_collectionPersistenceFinderByCommerceVirtualOrderItemId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceVirtualOrderItemId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceVirtualOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceVirtualOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"commerceVirtualOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceVirtualOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"commerceVirtualOrderItemId"}, false),
				_SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
				_SQL_COUNT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
				CommerceVirtualOrderItemFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceVirtualOrderItemFileEntry.",
					"commerceVirtualOrderItemId", FinderColumn.Type.LONG, "=",
					true, true,
					CommerceVirtualOrderItemFileEntry::
						getCommerceVirtualOrderItemId));

		_collectionPersistenceFinderByC_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceVirtualOrderItemId", "fileEntryId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceVirtualOrderItemId", "fileEntryId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceVirtualOrderItemId", "fileEntryId"},
				false),
			_SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
			_SQL_COUNT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE,
			CommerceVirtualOrderItemFileEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceVirtualOrderItemFileEntry.",
				"commerceVirtualOrderItemId", FinderColumn.Type.LONG, "=", true,
				true,
				CommerceVirtualOrderItemFileEntry::
					getCommerceVirtualOrderItemId),
			new FinderColumn<>(
				"commerceVirtualOrderItemFileEntry.", "fileEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceVirtualOrderItemFileEntry::getFileEntryId));

		CommerceVirtualOrderItemFileEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceVirtualOrderItemFileEntryUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceVirtualOrderItemFileEntryImpl.class.getName());
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
		CommerceVirtualOrderItemFileEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY =
		"SELECT commerceVirtualOrderItemFileEntry FROM CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry";

	private static final String
		_SQL_SELECT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE =
			"SELECT commerceVirtualOrderItemFileEntry FROM CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEVIRTUALORDERITEMFILEENTRY_WHERE =
			"SELECT COUNT(commerceVirtualOrderItemFileEntry) FROM CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceVirtualOrderItemFileEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceVirtualOrderItemFileEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "commerceVirtualOrderItemFileEntryId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:340440601