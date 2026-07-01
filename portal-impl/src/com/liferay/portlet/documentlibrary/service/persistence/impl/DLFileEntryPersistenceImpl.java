/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryUtil;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;

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

/**
 * The persistence implementation for the document library file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileEntryPersistenceImpl
	extends BasePersistenceImpl<DLFileEntry, NoSuchFileEntryException>
	implements DLFileEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileEntryUtil</code> to access the document library file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUuid_First(
			String uuid, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUuid_First(
		String uuid, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the document library file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByUUID_G(uuid, groupId);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<DLFileEntry, NoSuchFileEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByGroupId_First(
			long groupId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByGroupId_First(
		long groupId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCompanyId_First(
			long companyId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of document library file entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByRepositoryId;

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRepositoryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByRepositoryId_First(
			long repositoryId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByRepositoryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByRepositoryId_First(
		long repositoryId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByRepositoryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	@Override
	public void removeByRepositoryId(long repositoryId) {
		_collectionPersistenceFinderByRepositoryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId});
	}

	/**
	 * Returns the number of document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByRepositoryId(long repositoryId) {
		return _collectionPersistenceFinderByRepositoryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByMimeType;

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByMimeType.find(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByMimeType_First(
			String mimeType, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByMimeType.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByMimeType_First(
		String mimeType, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByMimeType.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where mimeType = &#63; from the database.
	 *
	 * @param mimeType the mime type
	 */
	@Override
	public void removeByMimeType(String mimeType) {
		_collectionPersistenceFinderByMimeType.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType});
	}

	/**
	 * Returns the number of document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByMimeType(String mimeType) {
		return _collectionPersistenceFinderByMimeType.count(
			FinderCacheUtil.getFinderCache(), new Object[] {mimeType});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByFileEntryTypeId;

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileEntryTypeId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryTypeId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByFileEntryTypeId_First(
			long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByFileEntryTypeId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryTypeId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByFileEntryTypeId_First(
		long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByFileEntryTypeId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryTypeId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where fileEntryTypeId = &#63; from the database.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 */
	@Override
	public void removeByFileEntryTypeId(long fileEntryTypeId) {
		_collectionPersistenceFinderByFileEntryTypeId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryTypeId});
	}

	/**
	 * Returns the number of document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByFileEntryTypeId(long fileEntryTypeId) {
		return _collectionPersistenceFinderByFileEntryTypeId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {fileEntryTypeId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderBySmallImageId;

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySmallImageId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {smallImageId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findBySmallImageId_First(
			long smallImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderBySmallImageId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {smallImageId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchBySmallImageId_First(
		long smallImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderBySmallImageId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {smallImageId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 */
	@Override
	public void removeBySmallImageId(long smallImageId) {
		_collectionPersistenceFinderBySmallImageId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {smallImageId});
	}

	/**
	 * Returns the number of document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countBySmallImageId(long smallImageId) {
		return _collectionPersistenceFinderBySmallImageId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {smallImageId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByLargeImageId;

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLargeImageId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {largeImageId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByLargeImageId_First(
			long largeImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByLargeImageId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {largeImageId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByLargeImageId_First(
		long largeImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByLargeImageId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {largeImageId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where largeImageId = &#63; from the database.
	 *
	 * @param largeImageId the large image ID
	 */
	@Override
	public void removeByLargeImageId(long largeImageId) {
		_collectionPersistenceFinderByLargeImageId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {largeImageId});
	}

	/**
	 * Returns the number of document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByLargeImageId(long largeImageId) {
		return _collectionPersistenceFinderByLargeImageId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {largeImageId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByCustom1ImageId;

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCustom1ImageId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {custom1ImageId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCustom1ImageId_First(
			long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByCustom1ImageId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {custom1ImageId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCustom1ImageId_First(
		long custom1ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByCustom1ImageId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {custom1ImageId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where custom1ImageId = &#63; from the database.
	 *
	 * @param custom1ImageId the custom1 image ID
	 */
	@Override
	public void removeByCustom1ImageId(long custom1ImageId) {
		_collectionPersistenceFinderByCustom1ImageId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {custom1ImageId});
	}

	/**
	 * Returns the number of document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByCustom1ImageId(long custom1ImageId) {
		return _collectionPersistenceFinderByCustom1ImageId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {custom1ImageId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByCustom2ImageId;

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCustom2ImageId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {custom2ImageId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCustom2ImageId_First(
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByCustom2ImageId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {custom2ImageId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCustom2ImageId_First(
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByCustom2ImageId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {custom2ImageId},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where custom2ImageId = &#63; from the database.
	 *
	 * @param custom2ImageId the custom2 image ID
	 */
	@Override
	public void removeByCustom2ImageId(long custom2ImageId) {
		_collectionPersistenceFinderByCustom2ImageId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {custom2ImageId});
	}

	/**
	 * Returns the number of document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByCustom2ImageId(long custom2ImageId) {
		return _collectionPersistenceFinderByCustom2ImageId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {custom2ImageId});
	}

	private FilterCollectionPersistenceFinder
		<DLFileEntry, NoSuchFileEntryException>
			_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_U_First(
			long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByG_U.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<DLFileEntry, NoSuchFileEntryException>
			_collectionPersistenceFinderByG_F;

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByG_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}}, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_F.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_F.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_F(long groupId, long folderId) {
		_collectionPersistenceFinderByG_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F(long groupId, long folderId) {
		return _collectionPersistenceFinderByG_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F(long groupId, long[] folderIds) {
		return _collectionPersistenceFinderByG_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)});
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long folderId) {
		return _collectionPersistenceFinderByG_F.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}}, groupId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long[] folderIds) {
		return _collectionPersistenceFinderByG_F.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, ArrayUtil.sortedUnique(folderIds)}, groupId);
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByR_F;

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, folderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByR_F_First(
			long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByR_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, folderId}, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByR_F_First(
		long repositoryId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByR_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, folderId}, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where repositoryId = &#63; and folderId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByR_F(long repositoryId, long folderId) {
		_collectionPersistenceFinderByR_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, folderId});
	}

	/**
	 * Returns the number of document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByR_F(long repositoryId, long folderId) {
		return _collectionPersistenceFinderByR_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, folderId});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByF_N;

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByF_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {folderId, name},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByF_N_First(
			long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByF_N.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {folderId, name},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByF_N_First(
		long folderId, String name,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByF_N.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {folderId, name},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where folderId = &#63; and name = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 */
	@Override
	public void removeByF_N(long folderId, String name) {
		_collectionPersistenceFinderByF_N.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {folderId, name});
	}

	/**
	 * Returns the number of document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByF_N(long folderId, String name) {
		return _collectionPersistenceFinderByF_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {folderId, name});
	}

	private FilterCollectionPersistenceFinder
		<DLFileEntry, NoSuchFileEntryException>
			_collectionPersistenceFinderByG_U_F;

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_U_F_First(
			long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByG_U_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_U_F_First(
		long groupId, long userId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_F.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_U_F.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(folderIds)},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(folderIds)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_U_F(long groupId, long userId, long folderId) {
		_collectionPersistenceFinderByG_U_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_U_F(long groupId, long userId, long folderId) {
		return _collectionPersistenceFinderByG_U_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_U_F(long groupId, long userId, long[] folderIds) {
		return _collectionPersistenceFinderByG_U_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(folderIds)});
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_F(long groupId, long userId, long folderId) {
		return _collectionPersistenceFinderByG_U_F.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, new long[] {folderId}}, groupId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_F(long groupId, long userId, long[] folderIds) {
		return _collectionPersistenceFinderByG_U_F.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, ArrayUtil.sortedUnique(folderIds)},
			groupId);
	}

	private UniquePersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_uniquePersistenceFinderByG_F_N;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_N(long groupId, long folderId, String name)
		throws NoSuchFileEntryException {

		return _uniquePersistenceFinderByG_F_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, name});
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_N(
		long groupId, long folderId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_F_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, name}, useFinderCache);
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByG_F_N(long groupId, long folderId, String name)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByG_F_N(groupId, folderId, name);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_N(long groupId, long folderId, String name) {
		return _uniquePersistenceFinderByG_F_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, name});
	}

	private UniquePersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_uniquePersistenceFinderByG_F_FN;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_FN(
			long groupId, long folderId, String fileName)
		throws NoSuchFileEntryException {

		return _uniquePersistenceFinderByG_F_FN.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, fileName});
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_F_FN.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, fileName}, useFinderCache);
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByG_F_FN(
			long groupId, long folderId, String fileName)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByG_F_FN(groupId, folderId, fileName);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_FN(long groupId, long folderId, String fileName) {
		return _uniquePersistenceFinderByG_F_FN.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, fileName});
	}

	private UniquePersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_uniquePersistenceFinderByG_F_T;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_T(long groupId, long folderId, String title)
		throws NoSuchFileEntryException {

		return _uniquePersistenceFinderByG_F_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title});
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_T(
		long groupId, long folderId, String title, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_F_T.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title}, useFinderCache);
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByG_F_T(long groupId, long folderId, String title)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByG_F_T(groupId, folderId, title);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_T(long groupId, long folderId, String title) {
		return _uniquePersistenceFinderByG_F_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, folderId, title});
	}

	private FilterCollectionPersistenceFinder
		<DLFileEntry, NoSuchFileEntryException>
			_collectionPersistenceFinderByG_F_F;

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_F_First(
			long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByG_F_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_F_First(
		long groupId, long folderId, long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_F_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_F_F.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_F_F.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, ArrayUtil.sortedUnique(folderIds), fileEntryTypeId
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, ArrayUtil.sortedUnique(folderIds), fileEntryTypeId
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 */
	@Override
	public void removeByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		_collectionPersistenceFinderByG_F_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_F(long groupId, long folderId, long fileEntryTypeId) {
		return _collectionPersistenceFinderByG_F_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId});
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return _collectionPersistenceFinderByG_F_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, ArrayUtil.sortedUnique(folderIds), fileEntryTypeId
			});
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return _collectionPersistenceFinderByG_F_F.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, new long[] {folderId}, fileEntryTypeId},
			groupId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return _collectionPersistenceFinderByG_F_F.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, ArrayUtil.sortedUnique(folderIds), fileEntryTypeId
			},
			groupId);
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByC_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_collectionPersistenceFinderByS_L_C1_C2;

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_L_C1_C2.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				smallImageId, largeImageId, custom1ImageId, custom2ImageId
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByS_L_C1_C2_First(
			long smallImageId, long largeImageId, long custom1ImageId,
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		return _collectionPersistenceFinderByS_L_C1_C2.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				smallImageId, largeImageId, custom1ImageId, custom2ImageId
			},
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByS_L_C1_C2_First(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return _collectionPersistenceFinderByS_L_C1_C2.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				smallImageId, largeImageId, custom1ImageId, custom2ImageId
			},
			orderByComparator);
	}

	/**
	 * Removes all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 */
	@Override
	public void removeByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		_collectionPersistenceFinderByS_L_C1_C2.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				smallImageId, largeImageId, custom1ImageId, custom2ImageId
			});
	}

	/**
	 * Returns the number of document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		return _collectionPersistenceFinderByS_L_C1_C2.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				smallImageId, largeImageId, custom1ImageId, custom2ImageId
			});
	}

	private UniquePersistenceFinder<DLFileEntry, NoSuchFileEntryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFileEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the document library file entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByERC_G(externalReferenceCode, groupId);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public DLFileEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileEntry.class);

		setModelImplClass(DLFileEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileEntryTable.INSTANCE);
	}

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	@Override
	public DLFileEntry create(long fileEntryId) {
		DLFileEntry dlFileEntry = new DLFileEntryImpl();

		dlFileEntry.setNew(true);
		dlFileEntry.setPrimaryKey(fileEntryId);

		String uuid = PortalUUIDUtil.generate();

		dlFileEntry.setUuid(uuid);

		dlFileEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileEntry;
	}

	/**
	 * Removes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry remove(long fileEntryId)
		throws NoSuchFileEntryException {

		return remove((Serializable)fileEntryId);
	}

	@Override
	protected DLFileEntry removeImpl(DLFileEntry dlFileEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileEntry)) {
				dlFileEntry = (DLFileEntry)session.get(
					DLFileEntryImpl.class, dlFileEntry.getPrimaryKeyObj());
			}

			if ((dlFileEntry != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileEntry)) {

				session.delete(dlFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileEntry != null) {
			clearCache(dlFileEntry);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry updateImpl(DLFileEntry dlFileEntry) {
		boolean isNew = dlFileEntry.isNew();

		if (!(dlFileEntry instanceof DLFileEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dlFileEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileEntry implementation " +
					dlFileEntry.getClass());
		}

		DLFileEntryModelImpl dlFileEntryModelImpl =
			(DLFileEntryModelImpl)dlFileEntry;

		if (Validator.isNull(dlFileEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileEntry.setUuid(uuid);
		}

		if (Validator.isNull(dlFileEntry.getExternalReferenceCode())) {
			dlFileEntry.setExternalReferenceCode(dlFileEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFileEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFileEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFileEntry.getCompanyId();

					long groupId = dlFileEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFileEntry.getPrimaryKey();
					}

					try {
						dlFileEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFileEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFileEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFileEntry ercDLFileEntry = fetchByERC_G(
				dlFileEntry.getExternalReferenceCode(),
				dlFileEntry.getGroupId());

			if (isNew) {
				if (ercDLFileEntry != null) {
					throw new DuplicateDLFileEntryExternalReferenceCodeException(
						"Duplicate document library file entry with external reference code " +
							dlFileEntry.getExternalReferenceCode() +
								" and group " + dlFileEntry.getGroupId());
				}
			}
			else {
				if ((ercDLFileEntry != null) &&
					(dlFileEntry.getFileEntryId() !=
						ercDLFileEntry.getFileEntryId())) {

					throw new DuplicateDLFileEntryExternalReferenceCodeException(
						"Duplicate document library file entry with external reference code " +
							dlFileEntry.getExternalReferenceCode() +
								" and group " + dlFileEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFileEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFileEntry.setCreateDate(date);
			}
			else {
				dlFileEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!dlFileEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFileEntry.setModifiedDate(date);
			}
			else {
				dlFileEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileEntry)) {
				if (!isNew) {
					session.evict(
						DLFileEntryImpl.class, dlFileEntry.getPrimaryKeyObj());
				}

				session.save(dlFileEntry);
			}
			else {
				dlFileEntry = (DLFileEntry)session.merge(dlFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFileEntry, false);

		if (isNew) {
			dlFileEntry.setNew(false);
		}

		dlFileEntry.resetOriginalValues();

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry with the primary key or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry findByPrimaryKey(long fileEntryId)
		throws NoSuchFileEntryException {

		return findByPrimaryKey((Serializable)fileEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the document library file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry, or <code>null</code> if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry fetchByPrimaryKey(long fileEntryId) {
		return fetchByPrimaryKey((Serializable)fileEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "fileEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILEENTRY;
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
		return DLFileEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileEntry";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("folderId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("fileName");
		ctMergeColumnNames.add("extension");
		ctMergeColumnNames.add("mimeType");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("extraSettings");
		ctMergeColumnNames.add("fileEntryTypeId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("size_");
		ctMergeColumnNames.add("smallImageId");
		ctMergeColumnNames.add("largeImageId");
		ctMergeColumnNames.add("custom1ImageId");
		ctMergeColumnNames.add("custom2ImageId");
		ctMergeColumnNames.add("manualCheckInRequired");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("reviewDate");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("fileEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "folderId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "folderId", "fileName"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "folderId", "title"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library file entry persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
			DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"dlFileEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DLFileEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileEntry::getUuid),
				DLFileEntry::getGroupId),
			_SQL_SELECT_DLFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"dlFileEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DLFileEntry::getUuid),
			new FinderColumn<>(
				"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getGroupId));

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
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, DLFileEntry::getUuid),
				new FinderColumn<>(
					"dlFileEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getGroupId));

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
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getCompanyId));

		_collectionPersistenceFinderByRepositoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByRepositoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"repositoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByRepositoryId", new String[] {Long.class.getName()},
					new String[] {"repositoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRepositoryId", new String[] {Long.class.getName()},
					new String[] {"repositoryId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "repositoryId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getRepositoryId));

		_collectionPersistenceFinderByMimeType =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByMimeType",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"mimeType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByMimeType",
					new String[] {String.class.getName()},
					new String[] {"mimeType"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByMimeType", new String[] {String.class.getName()},
					new String[] {"mimeType"}, 0, 1, false, null),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "mimeType", FinderColumn.Type.STRING, "=",
					true, true, DLFileEntry::getMimeType));

		_collectionPersistenceFinderByFileEntryTypeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFileEntryTypeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fileEntryTypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFileEntryTypeId",
					new String[] {Long.class.getName()},
					new String[] {"fileEntryTypeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFileEntryTypeId",
					new String[] {Long.class.getName()},
					new String[] {"fileEntryTypeId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "fileEntryTypeId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntry::getFileEntryTypeId));

		_collectionPersistenceFinderBySmallImageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySmallImageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"smallImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySmallImageId", new String[] {Long.class.getName()},
					new String[] {"smallImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySmallImageId", new String[] {Long.class.getName()},
					new String[] {"smallImageId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "smallImageId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getSmallImageId));

		_collectionPersistenceFinderByLargeImageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLargeImageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"largeImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLargeImageId", new String[] {Long.class.getName()},
					new String[] {"largeImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLargeImageId", new String[] {Long.class.getName()},
					new String[] {"largeImageId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "largeImageId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getLargeImageId));

		_collectionPersistenceFinderByCustom1ImageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCustom1ImageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"custom1ImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCustom1ImageId", new String[] {Long.class.getName()},
					new String[] {"custom1ImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCustom1ImageId",
					new String[] {Long.class.getName()},
					new String[] {"custom1ImageId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "custom1ImageId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntry::getCustom1ImageId));

		_collectionPersistenceFinderByCustom2ImageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCustom2ImageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"custom2ImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCustom2ImageId", new String[] {Long.class.getName()},
					new String[] {"custom2ImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCustom2ImageId",
					new String[] {Long.class.getName()},
					new String[] {"custom2ImageId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "custom2ImageId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntry::getCustom2ImageId));

		_collectionPersistenceFinderByG_U =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "userId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getGroupId),
				new FinderColumn<>(
					"dlFileEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, DLFileEntry::getUserId));

		_collectionPersistenceFinderByG_F =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_F",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "folderId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getGroupId),
				new ArrayableFinderColumn<>(
					"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=",
					false, true, true, DLFileEntry::getFolderId));

		_collectionPersistenceFinderByR_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"repositoryId", "folderId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"repositoryId", "folderId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"repositoryId", "folderId"}, false),
			_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
			DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"dlFileEntry.", "repositoryId", FinderColumn.Type.LONG, "=",
				true, true, DLFileEntry::getRepositoryId),
			new FinderColumn<>(
				"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getFolderId));

		_collectionPersistenceFinderByF_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByF_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"folderId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByF_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"folderId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByF_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"folderId", "name"}, 0, 2, false, null),
			_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
			DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getFolderId),
			new FinderColumn<>(
				"dlFileEntry.", "name", FinderColumn.Type.STRING, "=", true,
				true, DLFileEntry::getName));

		_collectionPersistenceFinderByG_U_F =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "userId", "folderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "userId", "folderId"}, false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getGroupId),
				new FinderColumn<>(
					"dlFileEntry.", "userId", FinderColumn.Type.LONG, "=", true,
					true, DLFileEntry::getUserId),
				new ArrayableFinderColumn<>(
					"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=",
					false, true, true, DLFileEntry::getFolderId));

		_uniquePersistenceFinderByG_F_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_F_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "folderId", "name"}, 0, 4, false,
				DLFileEntry::getGroupId, DLFileEntry::getFolderId,
				convertNullFunction(DLFileEntry::getName)),
			_SQL_SELECT_DLFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getGroupId),
			new FinderColumn<>(
				"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getFolderId),
			new FinderColumn<>(
				"dlFileEntry.", "name", FinderColumn.Type.STRING, "=", true,
				true, DLFileEntry::getName));

		_uniquePersistenceFinderByG_F_FN = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_F_FN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "folderId", "fileName"}, 0, 4, false,
				DLFileEntry::getGroupId, DLFileEntry::getFolderId,
				convertNullFunction(DLFileEntry::getFileName)),
			_SQL_SELECT_DLFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getGroupId),
			new FinderColumn<>(
				"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getFolderId),
			new FinderColumn<>(
				"dlFileEntry.", "fileName", FinderColumn.Type.STRING, "=", true,
				true, DLFileEntry::getFileName));

		_uniquePersistenceFinderByG_F_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_F_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "folderId", "title"}, 0, 4, false,
				DLFileEntry::getGroupId, DLFileEntry::getFolderId,
				convertNullFunction(DLFileEntry::getTitle)),
			_SQL_SELECT_DLFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getGroupId),
			new FinderColumn<>(
				"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getFolderId),
			new FinderColumn<>(
				"dlFileEntry.", "title", FinderColumn.Type.STRING, "=", true,
				true, DLFileEntry::getTitle));

		_collectionPersistenceFinderByG_F_F =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "folderId", "fileEntryTypeId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "folderId", "fileEntryTypeId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_F_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "folderId", "fileEntryTypeId"},
					false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getGroupId),
				new ArrayableFinderColumn<>(
					"dlFileEntry.", "folderId", FinderColumn.Type.LONG, "=",
					false, true, true, DLFileEntry::getFolderId),
				new FinderColumn<>(
					"dlFileEntry.", "fileEntryTypeId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntry::getFileEntryTypeId));

		_collectionPersistenceFinderByC_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, false),
			_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
			DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"dlFileEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getCompanyId),
			new FinderColumn<>(
				"dlFileEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DLFileEntry::getClassNameId),
			new FinderColumn<>(
				"dlFileEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getClassPK));

		_collectionPersistenceFinderByS_L_C1_C2 =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_L_C1_C2",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"smallImageId", "largeImageId", "custom1ImageId",
						"custom2ImageId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByS_L_C1_C2",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"smallImageId", "largeImageId", "custom1ImageId",
						"custom2ImageId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByS_L_C1_C2",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"smallImageId", "largeImageId", "custom1ImageId",
						"custom2ImageId"
					},
					false),
				_SQL_SELECT_DLFILEENTRY_WHERE, _SQL_COUNT_DLFILEENTRY_WHERE,
				DLFileEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dlFileEntry.", "smallImageId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getSmallImageId),
				new FinderColumn<>(
					"dlFileEntry.", "largeImageId", FinderColumn.Type.LONG, "=",
					true, true, DLFileEntry::getLargeImageId),
				new FinderColumn<>(
					"dlFileEntry.", "custom1ImageId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntry::getCustom1ImageId),
				new FinderColumn<>(
					"dlFileEntry.", "custom2ImageId", FinderColumn.Type.LONG,
					"=", true, true, DLFileEntry::getCustom2ImageId));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(DLFileEntry::getExternalReferenceCode),
				DLFileEntry::getGroupId),
			_SQL_SELECT_DLFILEENTRY_WHERE, "",
			new FinderColumn<>(
				"dlFileEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DLFileEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"dlFileEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DLFileEntry::getGroupId));

		DLFileEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileEntryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileEntryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		DLFileEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFILEENTRY =
		"SELECT dlFileEntry FROM DLFileEntry dlFileEntry";

	private static final String _SQL_SELECT_DLFILEENTRY_WHERE =
		"SELECT dlFileEntry FROM DLFileEntry dlFileEntry WHERE ";

	private static final String _SQL_COUNT_DLFILEENTRY_WHERE =
		"SELECT COUNT(dlFileEntry) FROM DLFileEntry dlFileEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-637523695