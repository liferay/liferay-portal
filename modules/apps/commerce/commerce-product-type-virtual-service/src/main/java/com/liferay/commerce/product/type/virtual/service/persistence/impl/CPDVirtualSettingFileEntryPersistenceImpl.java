/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.persistence.impl;

import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDVirtualSettingFileEntryException;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntryTable;
import com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryImpl;
import com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDVirtualSettingFileEntryPersistence;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDVirtualSettingFileEntryUtil;
import com.liferay.commerce.product.type.virtual.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the cpd virtual setting file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDVirtualSettingFileEntryPersistence.class)
public class CPDVirtualSettingFileEntryPersistenceImpl
	extends BasePersistenceImpl
		<CPDVirtualSettingFileEntry, NoSuchCPDVirtualSettingFileEntryException>
	implements CPDVirtualSettingFileEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDVirtualSettingFileEntryUtil</code> to access the cpd virtual setting file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDVirtualSettingFileEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDVirtualSettingFileEntry, NoSuchCPDVirtualSettingFileEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	@Override
	public List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cpd virtual setting file entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDVirtualSettingFileEntry, NoSuchCPDVirtualSettingFileEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDVirtualSettingFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cpd virtual setting file entry that was removed
	 */
	@Override
	public CPDVirtualSettingFileEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDVirtualSettingFileEntryException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry = findByUUID_G(
			uuid, groupId);

		return remove(cpdVirtualSettingFileEntry);
	}

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDVirtualSettingFileEntry, NoSuchCPDVirtualSettingFileEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	@Override
	public List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDVirtualSettingFileEntry, NoSuchCPDVirtualSettingFileEntryException>
			_collectionPersistenceFinderByCPDefinitionVirtualSettingId;

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	@Override
	public List<CPDVirtualSettingFileEntry> findByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionVirtualSettingId.find(
			finderCache, new Object[] {CPDefinitionVirtualSettingId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry findByCPDefinitionVirtualSettingId_First(
			long CPDefinitionVirtualSettingId,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return _collectionPersistenceFinderByCPDefinitionVirtualSettingId.
			findFirst(
				finderCache, new Object[] {CPDefinitionVirtualSettingId},
				orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry fetchByCPDefinitionVirtualSettingId_First(
		long CPDefinitionVirtualSettingId,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionVirtualSettingId.
			fetchFirst(
				finderCache, new Object[] {CPDefinitionVirtualSettingId},
				orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63; from the database.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 */
	@Override
	public void removeByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId) {

		_collectionPersistenceFinderByCPDefinitionVirtualSettingId.remove(
			finderCache, new Object[] {CPDefinitionVirtualSettingId});
	}

	/**
	 * Returns the number of cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	@Override
	public int countByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId) {

		return _collectionPersistenceFinderByCPDefinitionVirtualSettingId.count(
			finderCache, new Object[] {CPDefinitionVirtualSettingId});
	}

	private CollectionPersistenceFinder
		<CPDVirtualSettingFileEntry, NoSuchCPDVirtualSettingFileEntryException>
			_collectionPersistenceFinderByFileEntryId;

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	@Override
	public List<CPDVirtualSettingFileEntry> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileEntryId.find(
			finderCache, new Object[] {fileEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry findByFileEntryId_First(
			long fileEntryId,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return _collectionPersistenceFinderByFileEntryId.findFirst(
			finderCache, new Object[] {fileEntryId}, orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry fetchByFileEntryId_First(
		long fileEntryId,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByFileEntryId.fetchFirst(
			finderCache, new Object[] {fileEntryId}, orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByFileEntryId(long fileEntryId) {
		_collectionPersistenceFinderByFileEntryId.remove(
			finderCache, new Object[] {fileEntryId});
	}

	/**
	 * Returns the number of cpd virtual setting file entries where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	@Override
	public int countByFileEntryId(long fileEntryId) {
		return _collectionPersistenceFinderByFileEntryId.count(
			finderCache, new Object[] {fileEntryId});
	}

	public CPDVirtualSettingFileEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"CPDefinitionVirtualSettingFileEntryId",
			"CPDVirtualSettingFileEntryId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDVirtualSettingFileEntry.class);

		setModelImplClass(CPDVirtualSettingFileEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CPDVirtualSettingFileEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cpd virtual setting file entry with the primary key. Does not add the cpd virtual setting file entry to the database.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key for the new cpd virtual setting file entry
	 * @return the new cpd virtual setting file entry
	 */
	@Override
	public CPDVirtualSettingFileEntry create(
		long CPDefinitionVirtualSettingFileEntryId) {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			new CPDVirtualSettingFileEntryImpl();

		cpdVirtualSettingFileEntry.setNew(true);
		cpdVirtualSettingFileEntry.setPrimaryKey(
			CPDefinitionVirtualSettingFileEntryId);

		String uuid = PortalUUIDUtil.generate();

		cpdVirtualSettingFileEntry.setUuid(uuid);

		cpdVirtualSettingFileEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpdVirtualSettingFileEntry;
	}

	/**
	 * Removes the cpd virtual setting file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a cpd virtual setting file entry with the primary key could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry remove(
			long CPDefinitionVirtualSettingFileEntryId)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return remove((Serializable)CPDefinitionVirtualSettingFileEntryId);
	}

	@Override
	protected CPDVirtualSettingFileEntry removeImpl(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpdVirtualSettingFileEntry)) {
				cpdVirtualSettingFileEntry =
					(CPDVirtualSettingFileEntry)session.get(
						CPDVirtualSettingFileEntryImpl.class,
						cpdVirtualSettingFileEntry.getPrimaryKeyObj());
			}

			if (cpdVirtualSettingFileEntry != null) {
				session.delete(cpdVirtualSettingFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpdVirtualSettingFileEntry != null) {
			clearCache(cpdVirtualSettingFileEntry);
		}

		return cpdVirtualSettingFileEntry;
	}

	@Override
	public CPDVirtualSettingFileEntry updateImpl(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		boolean isNew = cpdVirtualSettingFileEntry.isNew();

		if (!(cpdVirtualSettingFileEntry instanceof
				CPDVirtualSettingFileEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpdVirtualSettingFileEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpdVirtualSettingFileEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpdVirtualSettingFileEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDVirtualSettingFileEntry implementation " +
					cpdVirtualSettingFileEntry.getClass());
		}

		CPDVirtualSettingFileEntryModelImpl
			cpdVirtualSettingFileEntryModelImpl =
				(CPDVirtualSettingFileEntryModelImpl)cpdVirtualSettingFileEntry;

		if (Validator.isNull(cpdVirtualSettingFileEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpdVirtualSettingFileEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpdVirtualSettingFileEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpdVirtualSettingFileEntry.setCreateDate(date);
			}
			else {
				cpdVirtualSettingFileEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpdVirtualSettingFileEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpdVirtualSettingFileEntry.setModifiedDate(date);
			}
			else {
				cpdVirtualSettingFileEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cpdVirtualSettingFileEntry);
			}
			else {
				cpdVirtualSettingFileEntry =
					(CPDVirtualSettingFileEntry)session.merge(
						cpdVirtualSettingFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpdVirtualSettingFileEntry, false);

		if (isNew) {
			cpdVirtualSettingFileEntry.setNew(false);
		}

		cpdVirtualSettingFileEntry.resetOriginalValues();

		return cpdVirtualSettingFileEntry;
	}

	/**
	 * Returns the cpd virtual setting file entry with the primary key or throws a <code>NoSuchCPDVirtualSettingFileEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a cpd virtual setting file entry with the primary key could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry findByPrimaryKey(
			long CPDefinitionVirtualSettingFileEntryId)
		throws NoSuchCPDVirtualSettingFileEntryException {

		return findByPrimaryKey(
			(Serializable)CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry, or <code>null</code> if a cpd virtual setting file entry with the primary key could not be found
	 */
	@Override
	public CPDVirtualSettingFileEntry fetchByPrimaryKey(
		long CPDefinitionVirtualSettingFileEntryId) {

		return fetchByPrimaryKey(
			(Serializable)CPDefinitionVirtualSettingFileEntryId);
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
		return "CPDVirtualSettingFileEntryId";
	}

	@Override
	protected String getPKFieldName() {
		return "CPDefinitionVirtualSettingFileEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CPDVirtualSettingFileEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cpd virtual setting file entry persistence.
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
			_SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
			_SQL_COUNT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
			CPDVirtualSettingFileEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpdVirtualSettingFileEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDVirtualSettingFileEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDVirtualSettingFileEntry::getUuid),
				CPDVirtualSettingFileEntry::getGroupId),
			_SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"cpdVirtualSettingFileEntry.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDVirtualSettingFileEntry::getUuid),
			new FinderColumn<>(
				"cpdVirtualSettingFileEntry.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDVirtualSettingFileEntry::getGroupId));

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
				_SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
				_SQL_COUNT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
				CPDVirtualSettingFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpdVirtualSettingFileEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDVirtualSettingFileEntry::getUuid),
				new FinderColumn<>(
					"cpdVirtualSettingFileEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDVirtualSettingFileEntry::getCompanyId));

		_collectionPersistenceFinderByCPDefinitionVirtualSettingId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionVirtualSettingId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionVirtualSettingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionVirtualSettingId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionVirtualSettingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionVirtualSettingId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionVirtualSettingId"}, false),
				_SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
				_SQL_COUNT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
				CPDVirtualSettingFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpdVirtualSettingFileEntry.",
					"CPDefinitionVirtualSettingId", FinderColumn.Type.LONG, "=",
					true, true,
					CPDVirtualSettingFileEntry::
						getCPDefinitionVirtualSettingId));

		_collectionPersistenceFinderByFileEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFileEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFileEntryId", new String[] {Long.class.getName()},
					new String[] {"fileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFileEntryId", new String[] {Long.class.getName()},
					new String[] {"fileEntryId"}, false),
				_SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
				_SQL_COUNT_CPDVIRTUALSETTINGFILEENTRY_WHERE,
				CPDVirtualSettingFileEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpdVirtualSettingFileEntry.", "fileEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDVirtualSettingFileEntry::getFileEntryId));

		CPDVirtualSettingFileEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDVirtualSettingFileEntryUtil.setPersistence(null);

		entityCache.removeCache(CPDVirtualSettingFileEntryImpl.class.getName());
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
		CPDVirtualSettingFileEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY =
		"SELECT cpdVirtualSettingFileEntry FROM CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry";

	private static final String _SQL_SELECT_CPDVIRTUALSETTINGFILEENTRY_WHERE =
		"SELECT cpdVirtualSettingFileEntry FROM CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry WHERE ";

	private static final String _SQL_COUNT_CPDVIRTUALSETTINGFILEENTRY_WHERE =
		"SELECT COUNT(cpdVirtualSettingFileEntry) FROM CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDVirtualSettingFileEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDVirtualSettingFileEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "CPDefinitionVirtualSettingFileEntryId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:133490062